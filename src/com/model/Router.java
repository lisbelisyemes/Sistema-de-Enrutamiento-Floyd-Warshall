package com.model;

public class Router extends Nodo {
    private int cantidadPuertos;

    public Router(int idNodo, String nombre, int cantidadPuertos) {
        super(idNodo, nombre);
        this.cantidadPuertos = cantidadPuertos;
    }

    public int getCantidadPuertos() {
        return cantidadPuertos;
    }

    public void setCantidadPuertos(int cantidadPuertos) {
        this.cantidadPuertos = cantidadPuertos;
    }
    
    @Override
    public String toString() {
        return "Router [ID: " + getIdNodo() +
                ", Nombre: " + getNombre() +
                ", Puertos: " + cantidadPuertos + "]";
    }
}