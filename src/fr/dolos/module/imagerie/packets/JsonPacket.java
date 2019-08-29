/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.dolos.module.imagerie.packets;

import fr.dolos.sdk.network.Packet;
import fr.dolos.sdk.network.PacketDeserializer;

/**
 *
 * @author schin
 */
public abstract class JsonPacket implements Packet {

    @Override
    public String serialize() {
        return "serialize with json :p";
    }
    
    public abstract void serializeData();
}
