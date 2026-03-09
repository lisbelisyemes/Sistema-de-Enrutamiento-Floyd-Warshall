package com.model;

public class Computadora extends Nodo {
    private String direccionIP;

    public Computadora(int idNodo, String nombre, String direccionIP) {
        super(idNodo, nombre);
        this.direccionIP = direccionIP;
    }

    public String getDireccionIP() {
        return direccionIP;
    }

    public void setDireccionIP(String direccionIP) {
        this.direccionIP = direccionIP;
    }

    public String consultarDatos() {
        return "Computadora: " + getNombre() + " - IP: " + direccionIP;
    }

    @Override
    public String toString() {
        return "Computadora [ID: " + getIdNodo() +
                ", Nombre: " + getNombre() +
                ", IP: " + direccionIP + "]";
    }
}