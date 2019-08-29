/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.dolos.module.imagerie.packets;

import java.util.ArrayList;

/**
 *
 * @author schin
 */
public class LabelSelectionPacket extends JsonPacket{

    public static final String PACKET_NAME = "Label_Selection_Packet"; 
    
    private ArrayList<String> labels;
    
    LabelSelectionPacket(ArrayList<String> labels) {
        this.labels = labels;
    }

    @Override
    public String getName() {
        return PACKET_NAME;
    }

    @Override
    public void serializeData() {
        
    }
}