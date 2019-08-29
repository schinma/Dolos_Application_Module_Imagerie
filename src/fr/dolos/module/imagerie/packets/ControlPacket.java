/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.dolos.module.imagerie.packets;

/**
 *
 * @author schin
 */
public abstract class ControlPacket extends JsonPacket {
    
    public boolean enable;
    
    ControlPacket() {
        this.enable = false;
    }
    
    ControlPacket(boolean enable) {
        this.enable = enable;
    }

    @Override
    public void serializeData() {
     
    }
}
