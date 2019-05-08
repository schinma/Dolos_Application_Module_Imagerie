/*
 * Dolos Main Module class for Imagerie module 
 */
package fr.dolos.module.imagerie;

import fr.dolos.sdk.Core;
import fr.dolos.sdk.modules.UserModule;
import fr.dolos.sdk.network.NetworkClient;
import fr.dolos.sdk.network.Packet;
import fr.dolos.sdk.network.PacketDeserializer;
import fr.dolos.sdk.network.PacketHandler;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;


/**
 *
 * @author schindler
 */
public class ImagerieModule extends UserModule {

    private static final String MODULE_NAME = "Dolos_Imagerie";
    
    private Core core = null;
    private boolean register = false;
    private Mat lastFrameReceived = null;
    private PacketDeserializer deserializer = new FramePacket();
    
    @Override
    public boolean load(Core core) {
        this.core = core;
        return true;
    }

    @Override
    public void unload() {
        
    }
    
    @Override
    public void update() {

        if (!register){
            this.register();
            register = true;
        }
    }

    @Override
    public String getName() {
       return MODULE_NAME;
    }
    
    @PacketHandler
    public void onPacketReceived(FramePacket framePacket, NetworkClient client) {
        
        log("Packet received : " + framePacket.getName());
        log("Packet label : " + framePacket.getLabel());
        lastFrameReceived = framePacket.getFrame();
        Imgcodecs.imwrite("image_received.jpg", framePacket.getFrame());        
    }   
    
    private void register() {
      log("register deserializer " + FramePacket.PACKET_NAME);
      core.getNetworkManager().registerDeserializer(FramePacket.PACKET_NAME, deserializer);
      core.getNetworkManager().registerReceiver(this);
    }

    private void log(String msg) {
        System.out.println("Module Imagerie : " + msg);
    }
}
