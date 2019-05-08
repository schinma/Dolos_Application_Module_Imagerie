/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.dolos.module.imagerie;

import fr.dolos.sdk.network.Packet;
import fr.dolos.sdk.network.PacketDeserializer;
import java.util.Base64;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.core.CvType;
import org.opencv.core.MatOfByte;


/**
 *
 * @author schindler
 */
public class FramePacket implements Packet, PacketDeserializer {

    public static final String PACKET_NAME = "Frame_Packet";
    
    private Mat frame;
    private String label;
    
    public FramePacket(){
        
    }
    
    public FramePacket(Mat frame, String label) {
       this.frame = frame;
       this.label = label;
    }
    
    public FramePacket(String data){
              
       String tokens[] = data.split(",");
       this.label = tokens[0];
       byte[] decodedBytes = Base64.getDecoder().decode(tokens[1]);
       Mat encoded = new Mat(1, decodedBytes.length, CvType.CV_8U);
       encoded.put(0, 0, decodedBytes);
       frame = Imgcodecs.imdecode(encoded,CvType.CV_8U);
    }
    
    @Override
    public String getName() {
        return PACKET_NAME;
    }

    @Override
    public String serialize() {
        
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", frame, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        String encodedString = Base64.getEncoder().encodeToString(byteArray);  
        return label + "," + encodedString;
    }
    
    public Mat getFrame() {
        return frame;
    }
    
    public String getLabel() {
        return label;
    }
    
    @Override
    public Packet deserialize(String packet, String data) {
        
       if (packet.equals(PACKET_NAME)) {
           Packet newPacket = new FramePacket(data);
           return newPacket;
       } else {
           System.out.println("Wrong type of packet : expected " + PACKET_NAME + " and got " + packet);
           return null;
       }
    }
}
