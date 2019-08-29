/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.dolos.module.imagerie.packets;

import fr.dolos.sdk.network.Packet;
import fr.dolos.sdk.network.PacketDeserializer;
import java.util.ArrayList;

/**
 *
 * @author schin
 */
public class ImageryInfosPacket extends JsonPacket implements PacketDeserializer {

    public static final String PACKET_NAME = "Imagery_Infos_Packet";
    
    private boolean imageryStarted;
    private ArrayList<String> imageryLabels;
    
    public ImageryInfosPacket() {}            
    
    public ImageryInfosPacket(String data) {
        String datas[] = data.split(";");
        imageryLabels = new ArrayList<>();
        
        this.imageryStarted = Boolean.parseBoolean(datas[0]);
        String labels[] = datas[1].split(",");
        for (String label : labels) {
            this.imageryLabels.add(label);
        }
    }
    
    public boolean getStarted() {
        return this.imageryStarted;
    }
    
    public ArrayList<String> getLabels() {
        return this.imageryLabels;
    }
    
    @Override
    public String getName() {
        return PACKET_NAME;
    }
    
    @Override //JsonPacket
    public void serializeData() {
        
    }  

    @Override
    public Packet deserialize(String packet, String data) {
         if (packet.equals(PACKET_NAME)) {
           Packet newPacket = new ImageryInfosPacket(data);
           return newPacket;
       } else {
           System.out.println("Wrong type of packet : expected " + PACKET_NAME + " and got " + packet);
           return null;
       }
    }
}
