/*
 * Imagery Control class for Imagerie Module 
 */
package fr.dolos.app.module.user.imagerie.packets;

import fr.dolos.sdk.network.Packet;
import fr.dolos.sdk.network.PacketDeserializer;

/**
 *
 * @author schin
 */
public class ControlImageryPacket extends ControlPacket implements PacketDeserializer{

    public static final String PACKET_NAME = "Control_Imagery_Packet";
    
    public ControlImageryPacket() { 
        super();
    }
    
    public ControlImageryPacket(boolean enable) {
        super(enable);
    }
    
    public ControlImageryPacket(String data) {
        super(data);
    }
    
    @Override
    public String getName() {
        return PACKET_NAME;
    }
    
    @Override
    public Packet deserialize(String packet, String data) {
         if (packet.equals(PACKET_NAME)) {
           Packet newPacket = new ControlImageryPacket(data);
           return newPacket;
       } else {
           System.out.println("Wrong type of packet : expected " + PACKET_NAME + " and got " + packet);
           return null;
       }
    }
}
