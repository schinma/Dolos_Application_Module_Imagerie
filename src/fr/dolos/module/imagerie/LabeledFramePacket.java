/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.dolos.module.imagerie;

import org.opencv.core.Mat;

/**
 *
 * @author schin
 */
public class LabeledFramePacket extends FramePacket {
    
    public static final String PACKET_NAME = "Labeled_Frame_Packet";
        
    public String label;
    
    public LabeledFramePacket() {        
    }
    
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
}
