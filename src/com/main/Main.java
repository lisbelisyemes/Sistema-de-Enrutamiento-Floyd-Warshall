package com.main;

import com.model.GrafoRed;
import com.view.VentanaPrincipal;
import com.controller.ControladorRed;

import javax.swing.UIManager;


public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        }
        catch (Exception ex) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        GrafoRed modelo = new GrafoRed();
        VentanaPrincipal vista = new VentanaPrincipal();
        new ControladorRed(vista, modelo);
        vista.setVisible(true);
        System.out.println("Sistema de Red iniciado correctamente.");
    }
}