/*
 * Imagery Control class for Imagerie Module 
 */
package fr.dolos.module.imagerie;

import fr.dolos.sdk.network.Packet;
import fr.dolos.sdk.network.PacketDeserializer;

/**
 *
 * @author schin
 */
public class ControlImageryPacket implements Packet, PacketDeserializer{

    public static final String PACKET_NAME = "Control_Imagery_Packet";
    
    public boolean enable;
    
    public ControlImageryPacket(){}
    
    public ControlImageryPacket(boolean enable) {
        this.enable = enable;
    }
    
    public ControlImageryPacket(String data) {
        this.enable = Boolean.parseBoolean(data);
    }
    
    @Override
    public String getName() {
        return PACKET_NAME;
    }

    @Override
    public String serialize() {  
        return Boolean.toString(this.enable);
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
