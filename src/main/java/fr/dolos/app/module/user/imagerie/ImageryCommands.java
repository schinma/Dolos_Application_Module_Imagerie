/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.dolos.app.module.user.imagerie;

/**
 *
 * @author schin
 */
public enum ImageryCommands {
    
    START_IMAGERY("start_img"),
    STOP_IMAGERY("stop_img"),
    START_LIVE("start_live"),
    STOP_LIVE("stop_live"),
    LABELS("labels"),
    SEND_LABELS("send");
    
    public final String label;
    
    private ImageryCommands(String label) {
        this.label = label;
    }
}