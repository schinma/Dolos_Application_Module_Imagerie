/*
 * Dolos Main Module class for Imagerie module 
 */
package fr.dolos.module.imagerie;

import fr.dolos.sdk.Core;
import fr.dolos.sdk.commands.CommandHandler;
import fr.dolos.sdk.modules.UserModule;
import fr.dolos.sdk.network.NetworkClient;
import fr.dolos.sdk.network.Packet;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;


/**
 *
 * @author schindler
 */
public class ImagerieModule extends UserModule implements CommandHandler {

    private static final String MODULE_NAME = "Dolos_Imagerie";
    
    private Core core = null;
    private boolean register = false;
    private Mat lastFrameReceived = null;
    private int imgCount = 0;
    
    private NetworkClient client = null;
    private boolean viewLive = false;
    private boolean openWindow = false;
    private boolean imageryStarted = false;
    private List<String> labels = null;
    
    /*private PacketDeserializer deserializer = new FramePacket();
    private PacketDeserializer labelDeserializer = new LabeledFramePacket();
    private PacketDeserializer infoDeserializer = new ImageryInfosPacket();*/
    private Map<String, PacketDeserializer> packetDeserializers;
    
    public ImagerieModule()
    {
        packetDeserializers = new HashMap<>();
        packetDeserializers.put(FramePacket.PACKET_NAME, new FramePacket());
        packetDeserializers.put(LabeledFramePacket.PACKET_NAME, new LabeledFramePacket());
        packetDeserializers.put(ImageryInfosPacket.PACKET_NAME, new ImageryInfosPacket());
    }
    
    @Override
    public boolean load(Core core) {
        this.core = core;     
        return true;
    }

    @Override
    public void unload() {
        
    }
    
    @Override
    public void update()
    {

        if (!register){
            this.register();
            register = true;
        }
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
        this.labels = infoPacket.getLabels();
        this.client = client;
        
        log("Available labels : " + labels.toString());
        
        if (!imageryStarted) {
            Packet controlPacket = new ControlImageryPacket(true);
            Packet livePacket = new ViewLiveControlPacket(true);
            try {
                client.send(controlPacket);
                client.send(livePacket);
            } catch (IOException ex) {
                Logger.getLogger(ImagerieModule.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void register()
    {
        // register the module's packets
      //System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
      log("register deserializer " + LabeledFramePacket.PACKET_NAME);
      //core.getNetworkManager().registerDeserializer(LabeledFramePacket.PACKET_NAME, labelDeserializer); 
      core.getNetworkManager().registerDeserializer(LabeledFramePacket.PACKET_NAME, packetDeserializers.get(LabeledFramePacket.PACKET_NAME)); 
       
      log("register deserializer " + FramePacket.PACKET_NAME);
      //core.getNetworkManager().registerDeserializer(FramePacket.PACKET_NAME, deserializer);
      core.getNetworkManager().registerDeserializer(FramePacket.PACKET_NAME, packetDeserializers.get(FramePacket.PACKET_NAME)); 
      
      log("register deserializer " + ImageryInfosPacket.PACKET_NAME);
      //core.getNetworkManager().registerDeserializer(ImageryInfosPacket.PACKET_NAME, infoDeserializer);
      core.getNetworkManager().registerDeserializer(ImageryInfosPacket.PACKET_NAME, packetDeserializers.get(ImageryInfosPacket.PACKET_NAME)); 
      
      // register the module's commands
      core.getCommandManager().registerHandler(ImageryCommands.START_IMAGERY.label, this);
      core.getCommandManager().registerHandler(ImageryCommands.STOP_IMAGERY.label, this);
      core.getCommandManager().registerHandler(ImageryCommands.START_LIVE.label, this);
      core.getCommandManager().registerHandler(ImageryCommands.STOP_LIVE.label, this);
      
      core.getNetworkManager().registerReceiver(this);
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
                log("Need to connect to a client");
                return;
        }
        if (label.equals(ImageryCommands.START_IMAGERY.label)) {
            if (imageryStarted) {
                log("Imagery already started");
                return;
            }
            try {
                client.send(new ControlImageryPacket(true));
                imageryStarted = true;
                log("Starting imagery");
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
                log("Unable to send start_imagery comman");
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
                log("Stopping imagery");
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
                log("Unable to send stop_imagery command");
            }
        }
        else if (label.equals(ImageryCommands.START_LIVE.label)) {
           if (viewLive) {
                log("Live already started");
                return;
            }
            try {
                client.send(new ViewLiveControlPacket(true));
                viewLive = true;
                log("Starting imagery");
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
                log("Unable to send start_imagery comman");
            }
        }
        else if (label.equals(ImageryCommands.STOP_LIVE.label)) {
            if (!viewLive) {
                log("Live already stopped");
                return;
            }
            try {
                client.send(new ViewLiveControlPacket(false));
                viewLive = false;
                log("Starting imagery");
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
                log("Unable to send start_imagery comman");
            }
        }        
    }
}
