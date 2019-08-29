/*
 * Imagery Control class for Imagerie Module 
 */
package fr.dolos.module.imagerie.packets;

import fr.dolos.sdk.network.Packet;
import fr.dolos.sdk.network.PacketDeserializer;

/**
 *
 * @author schin
 */
public class ControlImageryPacket extends ControlPacket {

    public static final String PACKET_NAME = "Control_Imagery_Packet";
    
    public ControlImageryPacket() { 
        super();
    }
    
    public ControlImageryPacket(boolean enable) {
        super(enable);
    }
    
    @Override
    public String getName() {
        return PACKET_NAME;
    }
}
