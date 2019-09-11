/*
 * Frame Packet class for Imagerie Module 
 */

package fr.dolos.app.module.user.imagerie.packets;

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
    
    public FramePacket(){
        
    }
    
    public FramePacket(Mat frame) {
       this.frame = frame;
    }
    
    public FramePacket(String data){
       byte[] decodedBytes = Base64.getDecoder().decode(data);
       Mat encoded = new Mat(1, decodedBytes.length, CvType.CV_8U);
       encoded.put(0, 0, decodedBytes);
       this.frame = Imgcodecs.imdecode(encoded, Imgcodecs.IMREAD_COLOR);
    }
    
    @Override
    public String getName() {
        return PACKET_NAME;
    }

    @Override
    public String serialize() {
        
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".png", frame, matOfByte);
        byte[] byteArray = matOfByte.toArray();      
        String encodedString = Base64.getEncoder().encodeToString(byteArray);  
        return encodedString;
    }
    
    public Mat getFrame() {
        return frame;
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
