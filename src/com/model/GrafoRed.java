package com.model;

import java.util.ArrayList;
import java.util.List;

/**
 * En esta clase manejamos la topología.
 * (Es decir, quién está conectado con quién) y corremos el algoritmo matemático.
 */
public class GrafoRed {
    private List<Nodo> listaDeNodos;
    private List<Conexion> listaDeConexiones;
    private int[][] matrizDeDistancias;
    private final int INF = 999999;

    public GrafoRed() {
        this.listaDeNodos = new ArrayList<>();
        this.listaDeConexiones = new ArrayList<>();
    }

    public void agregarNodo(Nodo nodo) {
        this.listaDeNodos.add(nodo);
    }

    public void agregarConexion(Conexion conexion) {
        this.listaDeConexiones.add(conexion);
    }

    /**
     * Este método calcula las rutas más cortas entre todos los equipos.
     * Devuelve la matriz final con los resultados optimizados.
     */
    public int[][] ejecutarFloydWarshall() {
        int n = listaDeNodos.size();
        matrizDeDistancias = generarMatrizAdyacencia();
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (matrizDeDistancias[i][k] != INF && matrizDeDistancias[k][j] != INF) {
                        if (matrizDeDistancias[i][k] + matrizDeDistancias[k][j] < matrizDeDistancias[i][j]) {
                            matrizDeDistancias[i][j] = matrizDeDistancias[i][k] + matrizDeDistancias[k][j];
                        }
                    }
                }
            }
        }
        return matrizDeDistancias;
    }

    private int[][] generarMatrizAdyacencia() {
        int n = listaDeNodos.size();
        int[][] matriz = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) matriz[i][j] = (i == j) ? 0 : INF;

        for (Conexion con : listaDeConexiones) {
            int i = listaDeNodos.indexOf(con.getOrigen());
            int j = listaDeNodos.indexOf(con.getDestino());
            if (i != -1 && j != -1) {
                matriz[i][j] = con.getPeso(); 
            }
        }
        return matriz;
    }

    public boolean eliminarNodo(int id) {
        Nodo nodoAEliminar = null;
        for (Nodo n : listaDeNodos) {
            if (n.getIdNodo() == id) {
                nodoAEliminar = n;
                break;
            }
        }
        if (nodoAEliminar == null) return false;
        java.util.Iterator<Conexion> it = listaDeConexiones.iterator();
        while (it.hasNext()) {
            Conexion c = it.next();
            if (c.getOrigen().getIdNodo() == id || c.getDestino().getIdNodo() == id) it.remove();
        }
        listaDeNodos.remove(nodoAEliminar);
        return true;
    }

    public List<Nodo> getListaDeNodos() {
        return listaDeNodos;
    }

    public List<Conexion> getListaDeConexiones() {
        return listaDeConexiones;
    }
}