/*
 * Labeled Frame Packet class for Imagerie Module 
 */
package fr.dolos.module.imagerie.packets;

import fr.dolos.module.imagerie.packets.FramePacket;
import fr.dolos.sdk.network.Packet;
import org.opencv.core.Mat;

/**
 *
 * @author schin
 */
public class LabeledFramePacket extends FramePacket {
    
    public static final String PACKET_NAME = "Labeled_Frame_Packet";
        
    public String label;
    
    public LabeledFramePacket() {}
    
    public LabeledFramePacket(Mat frame, String label) {
        super(frame);
        this.label = label;
    }
    
    public LabeledFramePacket(String data) {
        super(data.split(",")[1]);        
        this.label = data.split(",")[0];
    }
                
    @Override
    public String getName() {
        return PACKET_NAME;
    }

    public String getLabel() {
        return label;
    }
    
    @Override
    public String serialize() {
        return label + "," + super.serialize();
    }
    
    @Override
    public Packet deserialize(String packet, String data){
        
        if (packet.equals(PACKET_NAME)) {
           Packet newPacket = new LabeledFramePacket(data);
           return newPacket;
       } else {
           System.out.println("Wrong type of packet : expected " + PACKET_NAME + " and got " + packet);
           return null;
       }
    }
}
