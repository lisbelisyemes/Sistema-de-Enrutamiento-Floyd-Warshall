package com.model;

public abstract class Nodo {
    private int idNodo;
    private String nombre;

    public Nodo(int idNodo, String nombre) {
        this.idNodo = idNodo;
        this.nombre = nombre;
    }

    public int getIdNodo() {
        return idNodo;
    }

    public void setIdNodo(int idNodo) {
        this.idNodo = idNodo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}