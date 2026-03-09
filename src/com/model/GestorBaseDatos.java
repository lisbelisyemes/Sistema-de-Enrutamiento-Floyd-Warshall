package com.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GestorBaseDatos {
    private static GestorBaseDatos instancia;
    private Connection conexion;
    private final String URL = "jdbc:sqlite:simulador_red.db";
    private long idSesionActual;

    private GestorBaseDatos() {
        this.idSesionActual = System.currentTimeMillis();
        conectar();
        crearTablas();
    }

    public static GestorBaseDatos getInstancia() {
        if (instancia == null) instancia = new GestorBaseDatos();
        return instancia;
    }

    public void iniciarNuevaSesion() {
        this.idSesionActual = System.currentTimeMillis();
    }

    private void conectar() {
        try {
            conexion = DriverManager.getConnection(URL);
        }
        catch (SQLException e) {
            System.err.println("Error al abrir la base de datos: " + e.getMessage());
        }
    }

    private void crearTablas() {
        String sqlNodos = "CREATE TABLE IF NOT EXISTS nodos (" +
            "id_sesion INTEGER, " +
            "fecha_registro DATETIME DEFAULT (datetime('now', 'localtime')), " + 
            "id INTEGER, " +
            "nombre TEXT NOT NULL, " +
            "tipo TEXT NOT NULL, " +
            "extra TEXT, " +
            "PRIMARY KEY (id, id_sesion))";

    String sqlConexiones = "CREATE TABLE IF NOT EXISTS conexiones (" +
            "id_sesion INTEGER, " +
            "fecha_registro DATETIME DEFAULT (datetime('now', 'localtime')), " +
            "id_registro INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "origen_id INTEGER, " +
            "destino_id INTEGER, " +
            "peso INTEGER, " +
            "FOREIGN KEY(origen_id) REFERENCES nodos(id), " +
            "FOREIGN KEY(destino_id) REFERENCES nodos(id))";

    try (Statement statement = conexion.createStatement()) {
        statement.execute(sqlNodos);
        statement.execute(sqlConexiones);
    } catch (SQLException e) {
        e.printStackTrace();
    }
    }

    public void borrarSesionActual() {
        String sqlConnections = "DELETE FROM conexiones WHERE id_sesion = ?";
        String sqlNodes = "DELETE FROM nodos WHERE id_sesion = ?";
        try (PreparedStatement preparedStatementConnections = conexion.prepareStatement(sqlConnections);
             PreparedStatement prepareStatementNodes = conexion.prepareStatement(sqlNodes)) {
                preparedStatementConnections.setLong(1, idSesionActual);
                preparedStatementConnections.executeUpdate();
                prepareStatementNodes.setLong(1, idSesionActual);
                prepareStatementNodes.executeUpdate();
                System.out.println("Historial de la sesión " + idSesionActual + " eliminado.");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void guardarGrafo(List<Nodo> nodos, List<Conexion> conexiones) {
        this.idSesionActual = System.currentTimeMillis();
        try {
            // Guardado masivo de nodos (Routers o Computadoras)
            String insertNodo = "INSERT OR REPLACE INTO nodos (id_sesion, id, nombre, tipo, extra) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = conexion.prepareStatement(insertNodo)) {
                for (Nodo n : nodos) {
                    statement.setLong(1, idSesionActual);
                    statement.setInt(2, n.getIdNodo());
                    statement.setString(3, n.getNombre());
                    if (n instanceof Router) {
                        statement.setString(4, "Router");
                        statement.setString(5, String.valueOf(((Router) n).getCantidadPuertos()));
                    }
                    else if (n instanceof Computadora) {
                        statement.setString(4, "Computadora");
                        statement.setString(5, ((Computadora) n).getDireccionIP());
                    }
                    statement.addBatch();
                }
                statement.executeBatch();
            }

            // Limpieza previa de enlaces para esta sesión antes de reinsertar
            String deleteConnexions = "DELETE FROM conexiones WHERE id_sesion = ?";
            try (PreparedStatement statementDelete = conexion.prepareStatement(deleteConnexions)) {
                statementDelete.setLong(1, idSesionActual);
                statementDelete.executeUpdate();
            }

            // Inserción de los enlaces con sus pesos (latencias)
            String insertConnexions = "INSERT INTO conexiones (id_sesion, origen_id, destino_id, peso) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = conexion.prepareStatement(insertConnexions)) {
                for (Conexion c : conexiones) {
                    statement.setLong(1, idSesionActual);
                    statement.setInt(2, c.getOrigen().getIdNodo());
                    statement.setInt(3, c.getDestino().getIdNodo());
                    statement.setInt(4, c.getPeso());
                    statement.addBatch();
                }
                statement.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Nodo> cargarNodos() {
        List<Nodo> nodos = new ArrayList<>();
        String sql = "SELECT * FROM nodos WHERE id_sesion = (SELECT MAX(id_sesion) FROM nodos)";
        try (Statement statement = conexion.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String nombre = resultSet.getString("nombre");
                    String tipo = resultSet.getString("tipo");
                    String extra = resultSet.getString("extra");
                    if (tipo.equals("Router")) nodos.add(new Router(id, nombre, Integer.parseInt(extra)));
                    else if (tipo.equals("Computadora")) nodos.add(new Computadora(id, nombre, extra));
                }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return nodos;
    }

    // Reconstruye los enlaces uniendo los objetos Nodo cargados previamente
    public List<Conexion> cargarConexiones(List<Nodo> nodosCargados) {
        List<Conexion> conexiones = new ArrayList<>();
        String sql = "SELECT * FROM conexiones WHERE id_sesion = (SELECT MAX(id_sesion) FROM conexiones)";
        try (Statement statement = conexion.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int idOr = resultSet.getInt("origen_id");
                int idDes = resultSet.getInt("destino_id");
                int peso = resultSet.getInt("peso");
                Nodo origen = null, destino = null;
                for (Nodo n : nodosCargados) {
                    if (n.getIdNodo() == idOr) origen = n;
                    if (n.getIdNodo() == idDes) destino = n;
                }
                if (origen != null && destino != null) conexiones.add(new Conexion(origen, destino, peso));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return conexiones;
    }

    public void eliminarNodoBD(int idNodo) {
        String sqlCables = "DELETE FROM conexiones WHERE (origen_id = ? OR destino_id = ?) AND id_sesion = ?";
        String sqlEquipo = "DELETE FROM nodos WHERE id = ? AND id_sesion = ?";
        try (PreparedStatement statementCables = conexion.prepareStatement(sqlCables);
             PreparedStatement statementEquipo = conexion.prepareStatement(sqlEquipo)) {
                statementCables.setInt(1, idNodo);
                statementCables.setInt(2, idNodo);
                statementCables.setLong(3, idSesionActual);
                statementCables.executeUpdate();

                statementEquipo.setInt(1, idNodo);
                statementEquipo.setLong(2, idSesionActual);
                statementEquipo.executeUpdate();
        }
        catch (SQLException e) {
            System.err.println("Falla al remover de la BD: " + e.getMessage());
        }
    }

    public void cerrarConexion() {
        try {
            if (conexion != null) conexion.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}