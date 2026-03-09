package com.controller;

import com.model.*;
import com.view.PanelTopologia;
import com.view.VentanaPrincipal;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Controlador final para el Simulador de Red.
 */
public class ControladorRed {
    private VentanaPrincipal vista;
    private GrafoRed modelo;
    private PanelTopologia panelEstructural;
    private boolean yaSeGuardoEstaSesion = false;

    public ControladorRed(VentanaPrincipal vista, GrafoRed modelo) {
        this.vista = vista;
        this.modelo = modelo;
        this.inicializarEventos();
        this.panelEstructural = new PanelTopologia(modelo);
        this.vista.getPnlMapaRed().setLayout(new BorderLayout());
        this.vista.getPnlMapaRed().add(panelEstructural, BorderLayout.CENTER);

        int respuesta = JOptionPane.showConfirmDialog(vista, 
            "¿Deseas recuperar la última sesión de red guardada?", 
            "Recuperación de Datos", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (respuesta == JOptionPane.YES_OPTION) cargarDatosPrevios();
        else {
            GestorBaseDatos.getInstancia().iniciarNuevaSesion();
            vista.getTxtConsola().append("Iniciando sesión de diseño limpia.\n");
        }
        this.vista.getPnlMapaRed().revalidate();
        this.vista.getPnlMapaRed().repaint();
    }

    private void inicializarEventos() {
        this.vista.getBtnAgregarNodo().addActionListener(e -> agregarNodo());
        this.vista.getBtnCalcular().addActionListener(e -> ejecutarCalculoRutas());
        this.vista.getBtnAgregarConexion().addActionListener(e -> agregarConexion());
        this.vista.getBtnEliminar().addActionListener(e -> eliminarDispositivo());
        this.vista.getBtnResetear().addActionListener(e -> limpiarRedCompleta());
        this.vista.getBtnLimpiarPanel().addActionListener(e -> limpiarPanelManual());
        this.vista.getBtnGuardarSesion().addActionListener(e -> guardarVersionManual());
        this.vista.getBtnLimpiarTodo().addActionListener(e -> limpiarSoloVistaMatriz());
    }

    private void cargarDatosPrevios() {
        try {
            List<Nodo> nodosGuardados = GestorBaseDatos.getInstancia().cargarNodos();
            for (Nodo n : nodosGuardados) {
                modelo.agregarNodo(n);
                vista.getTxtConsola().append("RECUPERADO: " + n.getNombre() + "\n");
            }
            if (!nodosGuardados.isEmpty()) {
                List<Conexion> conexionesGuardadas = GestorBaseDatos.getInstancia().cargarConexiones(nodosGuardados);
                for (Conexion c : conexionesGuardadas) modelo.agregarConexion(c);
                JOptionPane.showMessageDialog(vista, "Datos recuperados de SQLite.");
            }
        }
        catch (Exception e) {
            System.err.println("Aviso: Historial vacío.");
        }
    }

    /**
     * RESET: Borra el diseño actual y su registro en la BD.
     */
    private void limpiarRedCompleta() {
        if (vista.getTablaResultados().getRowCount() == 0) {
            JOptionPane.showMessageDialog(vista, 
                "Operación cancelada: La tabla de resultados está vacía.", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(vista, 
            "¿Deseas eliminar el diseño actual?\n(Se borrará de la RAM y de la sesión actual en la BD)", 
            "Limpiar Sesión", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            modelo.getListaDeNodos().clear();
            modelo.getListaDeConexiones().clear();
            GestorBaseDatos.getInstancia().borrarSesionActual();
            GestorBaseDatos.getInstancia().iniciarNuevaSesion();
            yaSeGuardoEstaSesion = false;
            limpiarSoloVistaMatrizSinPregunta();
            panelEstructural.repaint();
            vista.getTxtConsola().setText("Sistema listo para nuevo diseño.\n");
            JOptionPane.showMessageDialog(vista, "Sesión eliminada. El historial previo está seguro.");
        }
    }

    private void guardarVersionManual() {
        if (vista.getTablaResultados().getRowCount() == 0) {
            JOptionPane.showMessageDialog(vista, 
                "Error: No hay resultados en la tabla para guardar en la base de datos.", 
                "Tabla Vacía", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (yaSeGuardoEstaSesion) {
            JOptionPane.showMessageDialog(vista, "Esta versión ya fue respaldada.");
            return;
        }
        GestorBaseDatos.getInstancia().guardarGrafo(modelo.getListaDeNodos(), modelo.getListaDeConexiones());
        yaSeGuardoEstaSesion = true;
        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formato = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a");
        vista.getTxtConsola().append("¡Respaldo exitoso en SQLite! Fecha: " + ahora.format(formato) + "\n");
        JOptionPane.showMessageDialog(vista, "Registro histórico completado a las " + ahora.format(formato));
    }

    private void ejecutarCalculoRutas() {
        if (modelo.getListaDeNodos().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "No hay nodos para procesar.");
            return;
        }
        int[][] resultado = modelo.ejecutarFloydWarshall();
        vista.mostrarMatrizResultados(resultado, new java.util.ArrayList<>(modelo.getListaDeNodos()));
        vista.getTxtConsola().append("Algoritmo ejecutado. Resultados en tabla.\n");
    }

    private void agregarNodo() {
        try {
            int id = Integer.parseInt(vista.getTxtIdNodo().getText().trim());
            String nom = vista.getTxtNombreNodo().getText().trim();
            String tipo = vista.getCbTipoNodo().getSelectedItem().toString();
            String extra = vista.getTxtTipoDato().getText().trim();
            if (buscarNodo(id) != null) {
                JOptionPane.showMessageDialog(vista, "ID duplicado.");
                return;
            }
            Nodo n = (tipo.equals("Router")) ? new Router(id, nom, Integer.parseInt(extra)) : new Computadora(id, nom, extra);
            modelo.agregarNodo(n);
            yaSeGuardoEstaSesion = false; // Habilita el guardado manual tras el cambio
            panelEstructural.repaint();
            limpiarCamposNodo();
            vista.getTxtConsola().append("Nodo añadido: " + nom + "\n");
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error en formato de datos.");
        }
    }

    private void agregarConexion() {
        try {
            int or = Integer.parseInt(vista.getTxtOrigenId().getText().trim());
            int des = Integer.parseInt(vista.getTxtDestinoId().getText().trim());
            int p = Integer.parseInt(vista.getTxtPeso().getText().trim());
            Nodo nOr = buscarNodo(or);
            Nodo nDes = buscarNodo(des);
            if (nOr != null && nDes != null) {
                modelo.agregarConexion(new Conexion(nOr, nDes, p));
                yaSeGuardoEstaSesion = false;
                panelEstructural.repaint();
                limpiarCamposConexion();
                vista.getTxtConsola().append("Conexión: " + nOr.getNombre() + " -> " + nDes.getNombre() + " [" + p + " ms]\n");
            }
            else JOptionPane.showMessageDialog(vista, "IDs de nodos no encontrados.");
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error en datos de conexión.");
        }
    }

    private void limpiarSoloVistaMatriz() {
        if (vista.getTablaResultados().getRowCount() == 0) {
            JOptionPane.showMessageDialog(vista, 
                "La tabla ya se encuentra vacía.", 
                "Información", JOptionPane.INFORMATION_MESSAGE);
            return; 
        }

        int conf = JOptionPane.showConfirmDialog(vista, "¿Limpiar resultados de la tabla?", "Limpiar Tabla", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) limpiarSoloVistaMatrizSinPregunta();
    }

    private void limpiarSoloVistaMatrizSinPregunta() {
        vista.getTablaResultados().setModel(new javax.swing.table.DefaultTableModel(
            null, new String[]{"Esperando nuevo cálculo..."}
        ));
        javax.swing.table.TableColumn columna = vista.getTablaResultados().getColumnModel().getColumn(0);
        columna.setPreferredWidth(380); 
        columna.setMinWidth(350);
        vista.getTxtConsola().append("Tabla despejada.\n");
    }

    private void eliminarDispositivo() {
        String input = JOptionPane.showInputDialog(vista, "Ingrese ID a eliminar:");
        if (input != null && !input.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(input.trim());
                if (modelo.eliminarNodo(id)) {
                    GestorBaseDatos.getInstancia().eliminarNodoBD(id);
                    yaSeGuardoEstaSesion = false; 
                    panelEstructural.repaint();
                    vista.getTxtConsola().append("Dispositivo ID " + id + " removido.\n");
                }
            }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(vista, "ID inválido.");
            }
        }
    }

    private Nodo buscarNodo(int id) {
        for (Nodo n : modelo.getListaDeNodos()) if (n.getIdNodo() == id) return n;
        return null;
    }

    private void limpiarPanelManual() {
        modelo.getListaDeNodos().clear();
        modelo.getListaDeConexiones().clear();
        yaSeGuardoEstaSesion = false;
        panelEstructural.repaint();
        vista.getTxtConsola().append("Lienzo de dibujo limpio.\n");
    }

    private void limpiarCamposNodo() {
        vista.getTxtIdNodo().setText("");
        vista.getTxtNombreNodo().setText("");
        vista.getTxtTipoDato().setText("");
    }

    private void limpiarCamposConexion() {
        vista.getTxtOrigenId().setText("");
        vista.getTxtDestinoId().setText("");
        vista.getTxtPeso().setText("");
    }
}