/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.dolos.module.imagerie.packets;

import fr.dolos.sdk.network.Packet;
import fr.dolos.sdk.network.PacketDeserializer;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author schin
 */
public class ImageryInfosPacket extends JsonPacket implements PacketDeserializer {

    public static final String PACKET_NAME = "Imagery_Infos_Packet";
    
    private boolean imageryStarted;
    private ArrayList<String> imageryLabels = new ArrayList<>();
    
    public ImageryInfosPacket() {}
    
    public ImageryInfosPacket(ArrayList<String> labels, boolean status) {
        this.imageryLabels = labels;
        this.imageryStarted = status;
    }
    
    public ImageryInfosPacket(String data) {
        JSONObject obj = this.deserializeData(data);
        
        this.imageryStarted = (Boolean) obj.get("status");
        this.imageryLabels = (JSONArray) obj.get("labels");
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
    public void serializeData(JSONObject object) {
        
        object.put("labels", imageryLabels);
        object.put("status", this.imageryStarted);
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
