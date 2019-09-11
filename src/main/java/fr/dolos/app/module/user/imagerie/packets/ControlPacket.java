/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.dolos.app.module.user.imagerie.packets;

import org.json.simple.JSONObject;

/**
 *
 * @author schin
 */
public abstract class ControlPacket extends JsonPacket {
    
    public boolean enable;
    private String key = "status";
    
    ControlPacket() {
        this.enable = false;
    }
    
    ControlPacket(boolean enable) {
        this.enable = enable;
    }
    
    ControlPacket(String data) {
        JSONObject obj = this.deserializeData(data);
        this.enable = (Boolean) obj.get(key);
    }

    @Override
    public void serializeData(JSONObject object) {     
        object.put(key, this.enable);
    }
}
