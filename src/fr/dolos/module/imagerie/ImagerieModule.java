/*
 * Dolos Main Module class for Imagerie module 
 */
package fr.dolos.module.imagerie;

import fr.dolos.module.imagerie.packets.ViewLiveControlPacket;
import fr.dolos.module.imagerie.packets.LabeledFramePacket;
import fr.dolos.module.imagerie.packets.ControlImageryPacket;
import fr.dolos.module.imagerie.packets.ImageryInfosPacket;
import fr.dolos.module.imagerie.packets.FramePacket;
import fr.dolos.sdk.Core;
import fr.dolos.sdk.commands.CommandHandler;
import fr.dolos.sdk.modules.UserModule;
import fr.dolos.sdk.network.NetworkClient;
import fr.dolos.sdk.network.PacketDeserializer;
import fr.dolos.sdk.network.PacketHandler;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;


/**
 *
 * @author schindler
 */
public class ImagerieModule extends UserModule implements CommandHandler {

    private static final String MODULE_NAME = "Dolos_Imagerie";
    
    private Core core = null;
    private Mat lastFrameReceived = null;
    private int imgCount = 0;
    
    private NetworkClient client = null;
    private boolean viewLive = false;
    private boolean imageryStarted = false;
    private List<String> availableLlabels = null;
    private List<String> selectedLabels = null;
    
    private Map<String, PacketDeserializer> packetDeserializers;
    
    public ImagerieModule()
    {
        selectedLabels = new ArrayList<>();
        packetDeserializers = new HashMap<>();        
        packetDeserializers.put(FramePacket.PACKET_NAME, new FramePacket());
        packetDeserializers.put(LabeledFramePacket.PACKET_NAME, new LabeledFramePacket());
        packetDeserializers.put(ImageryInfosPacket.PACKET_NAME, new ImageryInfosPacket());
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
      
        core.getNetworkManager().registerReceiver(this);
            
        return true;
    }

    @Override
    public void unload() {
        
    }
    
    @Override
    public void update()
    {
    }

    @Override
    public String getName()
    {
       return MODULE_NAME;
    }
    
    @PacketHandler
    public void onPacketReceived(LabeledFramePacket framePacket, NetworkClient client)
    {        
        log("Packet received : " + framePacket.getName());
        log("Packet label : " + framePacket.getLabel());
        lastFrameReceived = framePacket.getFrame();
        imgCount++;
        Imgcodecs.imwrite("data_received/image_received_"+ imgCount+ ".jpg", framePacket.getFrame());        
    }   

    @PacketHandler
    public void onPacketReceived(FramePacket framePacket, NetworkClient client) {
        
        BufferedImage image = MatToBufferedImage(framePacket.getFrame());
    
        // Afficher l'image 
    }
    
    @PacketHandler
    public void onPacketReceived(ImageryInfosPacket infoPacket, NetworkClient client)
    {
        this.imageryStarted = infoPacket.getStarted();
        this.availableLlabels = infoPacket.getLabels();
        this.client = client;
        
        log("Available labels : " + availableLlabels.toString());
    }

    private void log(String msg) {
        System.out.println("Module Imagerie : " + msg);
    }
    
    private BufferedImage MatToBufferedImage(Mat frame) {
        int type = 0;
        if (frame.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        } else if (frame.channels() == 3) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        BufferedImage image = new BufferedImage(frame.width(), frame.height(), type);
        WritableRaster raster = image.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        frame.get(0, 0, data);
      
        return image;
    }

    @Override
    public void onCommand(String label, String[] args) {
        if (client == null) {
                log(label + " : Need to connect to a client");
                return;
        }
        if (label.equals(ImageryCommands.START_IMAGERY.label)) {
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
                log("Unable to send start_imagery command");
            }           
        }
        else if (label.equals(ImageryCommands.STOP_IMAGERY.label)) {
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
                log("Unable to send stop_imagery command");
            }
        }
        else if (label.equals(ImageryCommands.START_LIVE.label)) {
           if (viewLive) {
                log(label + " : Live already started");
                return;
            }
            try {
                client.send(new ViewLiveControlPacket(true));
                viewLive = true;
                log(label + " : Starting imagery");
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
                log("Unable to send start_imagery command");
            }
        }
        else if (label.equals(ImageryCommands.STOP_LIVE)) {
            if (!viewLive) {
                log(label + " : live already stopped");
                return;
            }
            try {
                client.send(new ViewLiveControlPacket(false));
                viewLive = false;
                log("Starting imagery");
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
                log("Unable to send start_imagery command");
            }
        }
        else if (label.equals(ImageryCommands.LABELS)) {
            if (availableLlabels == null || availableLlabels.size() == 0) {
                log(label + " : no labels available");
                return;
            }            
            log("Available labels : " + availableLlabels.toString());
        }
        else if (label.equals(ImageryCommands.SEND_LABELS)) {
            if (args.length < 2 ) {
                log(ImageryCommands.SEND_LABELS + " : need at least 2 arguments : send label_1 label_2 ..");
                return;
            }
            // envoyer les availableLlabels
            selectedLabels.clear();
            for (int i = 1; i < args.length; i++) {
                selectedLabels.add(args[i]);
            }
        }
    }
}
