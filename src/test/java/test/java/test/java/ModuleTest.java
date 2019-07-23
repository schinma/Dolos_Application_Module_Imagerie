/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.java.test.java.test.java;

import fr.dolos.module.imagerie.FramePacket;
import fr.dolos.module.imagerie.ImageryInfosPacket;
import fr.dolos.module.imagerie.LabeledFramePacket;
import java.util.ArrayList;
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
        
        LabeledFramePacket packet = new LabeledFramePacket(frame, "label");
        Imgcodecs.imwrite("test.png", packet.getFrame());
        LabeledFramePacket newPacket = new LabeledFramePacket(packet.serialize());
        
        Imgcodecs.imwrite("test2.png", newPacket.getFrame());
        System.out.println("Label : " + newPacket.getLabel());
        
        ArrayList<String> labels = new ArrayList<String>();
        labels.add("dog");
        labels.add("cat");
        labels.add("cow");
        
       ImageryInfosPacket infoPacket = new ImageryInfosPacket(true, labels);
       ImageryInfosPacket newInfo = new ImageryInfosPacket(infoPacket.serialize());
       
       System.out.println("newInfo started :" + newInfo.getStarted());
       System.out.println("newInfo labels :" + newInfo.getLabels().toString());       
    }     
}
