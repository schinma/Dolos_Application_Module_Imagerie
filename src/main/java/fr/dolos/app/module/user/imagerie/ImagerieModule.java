/*
 * Dolos Main Module class for Imagerie module 
 */
package fr.dolos.app.module.user.imagerie;

import fr.dolos.app.module.user.imagerie.packets.*;

import fr.dolos.sdk.Core;
import fr.dolos.sdk.commands.CommandHandler;
import fr.dolos.sdk.modules.UserModule;
import fr.dolos.sdk.network.NetworkClient;
import fr.dolos.sdk.network.PacketDeserializer;
import fr.dolos.sdk.network.PacketHandler;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import javafx.scene.control.Tab;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.MatOfByte;


/**
 *
 * @author schindler
 */
public class ImagerieModule extends UserModule implements CommandHandler {

    private static final String MODULE_NAME = "Dolos_Imagerie";
    
    private Core core = null;
    private int imgCount = 0;
    
    private NetworkClient client = null;
    private boolean viewLive = false;
    private boolean imageryStarted = false;
    
    private final ObservableList<Label> availableLabels = FXCollections.observableArrayList();
    
    @FXML
    private ImageView currentFrame;
    private Mat lastFrameReceived;
    
    @FXML
    private TableView<Label> labelTable;
    @FXML
    private TableColumn<Label, String> labelColumn;
    @FXML
    private TableColumn<Label, CheckBox> selectColumn;
    
    private ScheduledExecutorService timer;
    
    private final Map<String, PacketDeserializer> packetDeserializers;
    
    public ImagerieModule() {
        packetDeserializers = new HashMap<>();        
        packetDeserializers.put(FramePacket.PACKET_NAME, new FramePacket());
        packetDeserializers.put(LabeledFramePacket.PACKET_NAME, new LabeledFramePacket());
        packetDeserializers.put(ImageryInfosPacket.PACKET_NAME, new ImageryInfosPacket());
        packetDeserializers.put(ControlImageryPacket.PACKET_NAME, new ControlImageryPacket());        
        packetDeserializers.put(ViewLiveControlPacket.PACKET_NAME, new ViewLiveControlPacket());
    }
    
    @FXML
    public void initialize() {
        labelColumn.setCellValueFactory(cellData -> cellData.getValue().labelProperty());
        selectColumn.setCellValueFactory(new PropertyValueFactory<>("select"));
        
        this.availableLabels.add(new Label("dog"));
        this.availableLabels.add(new Label("cat"));
        this.availableLabels.add(new Label("cow"));
        this.availableLabels.add(new Label("dolphin"));
        
        this.labelTable.setItems(this.availableLabels);
    }
    
    @Override
    public boolean load(Core core) {
        this.core = core;
             
        // register the module's packet deserializers
        log("register deserializer " + LabeledFramePacket.PACKET_NAME);
        core.getNetworkManager().registerDeserializer(LabeledFramePacket.PACKET_NAME, packetDeserializers.get(LabeledFramePacket.PACKET_NAME));    
        log("register deserializer " + FramePacket.PACKET_NAME);
        core.getNetworkManager().registerDeserializer(FramePacket.PACKET_NAME, packetDeserializers.get(FramePacket.PACKET_NAME));       
        log("register deserializer " + ImageryInfosPacket.PACKET_NAME);
        core.getNetworkManager().registerDeserializer(ImageryInfosPacket.PACKET_NAME, packetDeserializers.get(ImageryInfosPacket.PACKET_NAME)); 
      
        // register the module's commands
        core.getCommandManager().registerHandler(ImageryCommands.START_IMAGERY.label, this);
        core.getCommandManager().registerHandler(ImageryCommands.STOP_IMAGERY.label, this);
        core.getCommandManager().registerHandler(ImageryCommands.START_LIVE.label, this);
        core.getCommandManager().registerHandler(ImageryCommands.STOP_LIVE.label, this);
        core.getCommandManager().registerHandler(ImageryCommands.LABELS.label, this);
        core.getCommandManager().registerHandler(ImageryCommands.SEND_LABELS.label, this);
        
        core.getNetworkManager().registerReceiver(this);
            
        //  create the tab 
        Tab tab = core.getUIManager().createTab(ImagerieModule.MODULE_NAME);
        try {
            tab.setContent(createTabContent("moduleImagerie"));
        } catch (IOException ex) {
            log("wrong fxml file");
        }       
        return true;
    }
    
    private SplitPane createTabContent(String fxml) throws IOException {          
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        SplitPane mainLayout = fxmlLoader.load();     
        return mainLayout;
    }

    @Override
    public void unload() {  
        if (client.isConnected()) {
            client.close();
        }
    }
    
    @Override
    public String getName()
    {
       return MODULE_NAME;
    }
    
    @Override
    public void update(){
        
        if (viewLive) {
            Image img = mat2Image(this.lastFrameReceived);
            currentFrame.setImage(img);
        }
    }
    
    @PacketHandler
    public void onPacketReceived(LabeledFramePacket framePacket, NetworkClient client) {        
        log("Packet received : " + framePacket.getName());
        log("Packet label : " + framePacket.getLabel());
        lastFrameReceived = framePacket.getFrame();
        imgCount++;
        Imgcodecs.imwrite("data_received/image_received_"+ imgCount+ ".jpg", framePacket.getFrame());        
    }   

    @PacketHandler
    public void onPacketReceived(FramePacket framePacket, NetworkClient client) {     
      this.lastFrameReceived = framePacket.getFrame();
     
    }
    
    @PacketHandler
    public void onPacketReceived(ImageryInfosPacket infoPacket, NetworkClient client) {
        this.imageryStarted = infoPacket.getStarted();
        infoPacket.getLabels().forEach((label) -> {
            this.availableLabels.add(new Label(label));
        });
        this.client = client;
        
        log("Available labels from drone : " + this.availableLabels.toString());
    }

    private void log(String msg) {
        System.out.println("Module Imagerie : " + msg);
    }
    
    public Image mat2Image(Mat frame) {
        // create a temporary buffer
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", frame, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }
    
    @Override
    public void onCommand(String label, String[] args) {
        
        if (label.equals(ImageryCommands.START_IMAGERY.label)) {
            this.startImagery();
        }
        else if (label.equals(ImageryCommands.STOP_IMAGERY.label)) {
            this.stopImagery();
        }
        else if (label.equals(ImageryCommands.START_LIVE.label)) {
           this.startVideo();
        }
        else if (label.equals(ImageryCommands.STOP_LIVE.label)) {
            this.stopVideo();
        }
        else if (label.equals(ImageryCommands.LABELS.label)) {
            if (this.availableLabels.isEmpty()) {
                log(label + " : no labels available");
                return;
            }            
            log("Available labels : " + availableLabels.toString());
        }
        else if (label.equals(ImageryCommands.SEND_LABELS.label)) {
            if (args.length < 2 ) {
                log(ImageryCommands.SEND_LABELS + " : need at least 2 arguments : send label_1 label_2 ..");
                return;
            }
            for (int i = 1; i < args.length; i++) {
                for (Label el : this.availableLabels) {
                    if (args[i].equals(el.getLabel())) {
                        el.getSelect().setSelected(true);
                    }
                }
            }
            this.sendLabels();
        }
    }
    
    @FXML
    private void stopImagery(){
        String label = ImageryCommands.STOP_IMAGERY.label;
        if (client == null) {
                log(label + " : Need to connect to a drone");
                return;
        }
        if (!imageryStarted) {
            log("Imagery already stopped");
            return;
        }
        try {
            client.send(new ControlImageryPacket(false));
            imageryStarted = false;
            log(label + " : Stopping imagery");
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            log("Unable to send '" + label + "' command");
        }
    }
    
    @FXML
    private void startImagery(){
        String label = ImageryCommands.START_IMAGERY.label;
        if (client == null) {
                log(label + " : Need to connect to a drone");
                return;
        }
        if (imageryStarted) {
            log(label + " : Imagery already started");
            return;
        }
        try {
            client.send(new ControlImageryPacket(true));
            imageryStarted = true;
            log(label + " : Starting imagery");
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            log("Unable to send '" + label + "' command");
        }
    }
    
    @FXML
    private void stopVideo(){
        String label = ImageryCommands.STOP_LIVE.label;
        if (client == null) {
                log(label + " : Need to connect to a drone");
                return;
        }    
        if (!viewLive) {
                log(label + " : video already stopped");
                return;
        }
        try {
            client.send(new ViewLiveControlPacket(false));
            viewLive = false;
            log("Sopping live");
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            log("Unable to send '" + label + "' command");
        }
    }
    
    @FXML
    private void startVideo(){
        String label = ImageryCommands.START_LIVE.label;
        if (client == null) {
                log(label + " : Need to connect to a drone");
                return;
        }       
        if (viewLive) {
                log(label + " : Live already started");
                return;
        }
        try {
            client.send(new ViewLiveControlPacket(true));
            viewLive = true;
            log(label + " : Starting live");
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            log("Unable to send '" + label + "' command");
        }
    }
    
    @FXML
    private void sendLabels() {        
        String cmd = ImageryCommands.SEND_LABELS.label;

        ArrayList<String> toSend = new ArrayList<>();
        this.availableLabels.stream().filter((label) -> (label.getSelect().isSelected())).forEachOrdered((label) -> {
            toSend.add(label.getLabel());
        });
        System.out.println(toSend.toString());
        if (client == null) {
            log(cmd + " : Need to connect to a drone to send labels");
            return;
        }
        LabelSelectionPacket packet = new LabelSelectionPacket(toSend);  
        try {
            client.send(packet);
        } catch (IOException ex) {
            log("Unable to send '" + cmd + "' command");
            ex.printStackTrace(System.err);            
        }       
    }
}
