/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.dolos.app.module.user.imagerie;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;

/**
 *
 * @author schin
 */
public class Label {
    
    private final SimpleStringProperty label;
    private CheckBox select;
    
    Label(String label){
        this.label = new SimpleStringProperty(label);
        this.select = new CheckBox();
    }
    
    public String getLabel(){
        return this.label.get();
    }
    
    public void setLabel(String label) {
        this.label.set(label);
    }
    
    public SimpleStringProperty labelProperty() {
        return this.label;
    }
    
    public CheckBox getSelect() {
        return this.select;
    }
    
    public void setSelect(CheckBox select) {
        this.select = select;
    }
    
}
