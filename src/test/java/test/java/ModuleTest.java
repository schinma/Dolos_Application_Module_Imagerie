/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.java;

import fr.dolos.app.module.user.imagerie.packets.*;
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
        
        nu.pattern.OpenCV.loadLocally();
        VideoCapture capture = new VideoCapture(0);
        Mat frame = new Mat(); 
        capture.read(frame);
        
        LabeledFramePacket packet = new LabeledFramePacket(frame, "label");
        Imgcodecs.imwrite("test.png", packet.getFrame());
        LabeledFramePacket newPacket = new LabeledFramePacket(packet.serialize());
        
        Imgcodecs.imwrite("test2.png", newPacket.getFrame());
        System.out.println("Label : " + newPacket.getLabel());
        
        ControlImageryPacket contPacket = new ControlImageryPacket(true);
        System.out.println(contPacket.serialize());
        
        ControlImageryPacket contPacket2 = new ControlImageryPacket(contPacket.serialize());
        System.out.println(contPacket2.serialize());
        
        ArrayList<String> labels = new ArrayList<String>();
        labels.add("dog");
        labels.add("cat");
        labels.add("cow");
        
        ImageryInfosPacket infoPacket = new ImageryInfosPacket(labels, true);        
        System.out.println(infoPacket.serialize());
        
        ImageryInfosPacket infoPacket2 = new ImageryInfosPacket(infoPacket.serialize());        
        System.out.println(infoPacket2.getLabels().get(2));
        System.out.println(infoPacket2.serialize());
         
        LabelSelectionPacket selectPacket = new LabelSelectionPacket(labels);
        System.out.println(selectPacket.serialize());
     }     
}
