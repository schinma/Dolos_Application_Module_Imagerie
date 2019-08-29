/*
 * View Live Control Packet class for Imagerie Module 
 */
package fr.dolos.module.imagerie.packets;

import fr.dolos.sdk.network.Packet;
import fr.dolos.sdk.network.PacketDeserializer;

/**
 *
 * @author schin
 */
public class ViewLiveControlPacket extends ControlPacket {
    
    public static final String PACKET_NAME = "View_Live_Control_Packet";
    
    public ViewLiveControlPacket(){
        super();
    }
    
    public ViewLiveControlPacket(boolean enable) {
        super(enable);
    }
  
    @Override
    public String getName() {
        return PACKET_NAME;
    }
}
