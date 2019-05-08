/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.java.test.java.test.java;

import fr.dolos.module.imagerie.FramePacket;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;


/**
 *
 * @author schin
 */
public class ModuleTest {
    
    public static void main(String[] args) {
        
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        VideoCapture capture = new VideoCapture(0);
        Mat frame = new Mat(); 
        capture.read(frame);
        
        FramePacket packet = new FramePacket(frame, "label");
        Imgcodecs.imwrite("test.jpg", packet.getFrame()); 
        System.out.println(packet.serialize());
        FramePacket newPacket = new FramePacket(packet.serialize());
        
        Imgcodecs.imwrite("test2.jpg", newPacket.getFrame());
        System.out.println("Label : " + newPacket.getLabel());
    } 
    
}
