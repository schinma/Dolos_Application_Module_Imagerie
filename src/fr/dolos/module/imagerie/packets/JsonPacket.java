/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.dolos.module.imagerie.packets;

import fr.dolos.sdk.network.Packet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author schin
 */
public abstract class JsonPacket implements Packet {

    @Override
    public String serialize() {
        
        JSONObject object = new JSONObject();        
        this.serializeData(object);        
        
        return object.toJSONString();
    }
    
    public abstract void serializeData(JSONObject object);
    
    protected JSONObject deserializeData(String data) {       
        
        JSONParser parser = new JSONParser();        
        JSONObject object;
        try {
            object = (JSONObject) parser.parse(data);
            return object;
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
