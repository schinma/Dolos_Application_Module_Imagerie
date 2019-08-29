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
public class ViewLiveControlPacket extends ControlPacket implements PacketDeserializer {
    
    public static final String PACKET_NAME = "View_Live_Control_Packet";
    
    public ViewLiveControlPacket(){
        super();
    }
    
    public ViewLiveControlPacket(boolean enable) {
        super(enable);
    }
    
    public ViewLiveControlPacket(String data)
    {
        super(data);
    }
  
    @Override
    public String getName() {
        return PACKET_NAME;
    }
   
    @Override
    public Packet deserialize(String packet, String data) {
         if (packet.equals(PACKET_NAME)) {
           Packet newPacket = new ViewLiveControlPacket(data);
           return newPacket;
       } else {
           System.out.println("Wrong type of packet : expected " + PACKET_NAME + " and got " + packet);
           return null;
       }
    }
}
