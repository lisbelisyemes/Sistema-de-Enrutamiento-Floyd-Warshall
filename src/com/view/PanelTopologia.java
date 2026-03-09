package com.view;

import com.model.Conexion;
import com.model.GrafoRed;
import com.model.Nodo;
import java.awt.*;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class PanelTopologia extends JPanel {
    private GrafoRed modelo;
    private Image imagenRouter;
    private Image imagenPC;

    public PanelTopologia(GrafoRed modelo) {
        this.modelo = modelo;
        this.setBackground(Color.WHITE);
        try {
            java.net.URL urlRouter = getClass().getResource("/com/images/router.png");
            if (urlRouter != null) imagenRouter = new ImageIcon(urlRouter).getImage();
            java.net.URL urlPc = getClass().getResource("/com/images/pc.png");
            if (urlPc != null) imagenPC = new ImageIcon(urlPc).getImage();
        }
        catch (Exception e) {
            System.err.println("No se pudieron cargar las imágenes.");
        }
    }

@Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    
    // Suavizado para que las flechas y líneas se vean nítidas
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    List<Nodo> nodos = modelo.getListaDeNodos(); //
    List<Conexion> conexiones = modelo.getListaDeConexiones(); //
    int numNodos = nodos.size();
    
    if (numNodos == 0) return;

    // Coordenadas del área de dibujo
    int width = getWidth();
    int height = getHeight();
    int padding = 60; 
    int radioX = (width / 2) - padding;
    int radioY = (height / 2) - padding;
    int centroX = width / 2;
    int centroY = height / 2;

    // --- 1. DIBUJAR LAS CONEXIONES (LÍNEAS Y FLECHAS) ---
    g2.setStroke(new BasicStroke(2)); 
    for (Conexion c : conexiones) { //
        int i = nodos.indexOf(c.getOrigen()); //
        int j = nodos.indexOf(c.getDestino()); //
        
        if (i == -1 || j == -1) continue;

        // Posiciones de los nodos
        double anguloI = 2 * Math.PI * i / numNodos;
        int x1 = centroX + (int) (radioX * Math.cos(anguloI));
        int y1 = centroY + (int) (radioY * Math.sin(anguloI));

        double anguloJ = 2 * Math.PI * j / numNodos;
        int x2 = centroX + (int) (radioX * Math.cos(anguloJ));
        int y2 = centroY + (int) (radioY * Math.sin(anguloJ));

        // Dibujo de la línea
        g2.setColor(new Color(80, 80, 80)); 
        g2.drawLine(x1, y1, x2, y2);

        // --- DIBUJO DE LA FLECHA ---
        double anguloLinea = Math.atan2(y2 - y1, x2 - x1);
        int largoCabeza = 15; 
        int flechaX = x2 - (int) (25 * Math.cos(anguloLinea)); // Ajuste para no tapar el icono
        int flechaY = y2 - (int) (25 * Math.sin(anguloLinea));

        Polygon puntaFlecha = new Polygon();
        puntaFlecha.addPoint(flechaX, flechaY);
        puntaFlecha.addPoint(
            (int) (flechaX - largoCabeza * Math.cos(anguloLinea - Math.toRadians(20))),
            (int) (flechaY - largoCabeza * Math.sin(anguloLinea - Math.toRadians(20)))
        );
        puntaFlecha.addPoint(
            (int) (flechaX - largoCabeza * Math.cos(anguloLinea + Math.toRadians(20))),
            (int) (flechaY - largoCabeza * Math.sin(anguloLinea + Math.toRadians(20)))
        );
        g2.fill(puntaFlecha); 

        // --- ETIQUETA DE LATENCIA (CON DESPLAZAMIENTO 0.7) ---
        int posX = (int) (x1 + (x2 - x1) * 0.7); 
        int posY = (int) (y1 + (y2 - y1) * 0.7);

        String textoPeso = c.getPeso() + " ms"; //
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        int anchoTexto = g2.getFontMetrics().stringWidth(textoPeso);
        
        // Fondo blanco para que el número sea legible
        g2.setColor(new Color(255, 255, 255, 220)); 
        g2.fillRect(posX - (anchoTexto / 2) - 2, posY - 10, anchoTexto + 4, 14);
        
        g2.setColor(Color.RED);
        g2.drawString(textoPeso, posX - (anchoTexto / 2), posY + 2);
    }

    // --- 2. DIBUJAR LOS ICONOS DE LOS NODOS ---
    int diametro = 50; 
    for (int i = 0; i < numNodos; i++) {
        Nodo n = nodos.get(i); //
        double angulo = 2 * Math.PI * i / numNodos;
        int x = centroX + (int) (radioX * Math.cos(angulo)) - (diametro / 2);
        int y = centroY + (int) (radioY * Math.sin(angulo)) - (diametro / 2);

        boolean esRouter = n.getClass().getSimpleName().equals("Router");
        Image icono = esRouter ? imagenRouter : imagenPC;

        if (icono != null) {
            g2.drawImage(icono, x, y, diametro, diametro, this);
        } else {
            g2.setColor(esRouter ? new Color(0, 112, 184) : new Color(40, 167, 69));
            g2.fillOval(x, y, diametro, diametro);
        }

        // Etiquetas del nombre e ID
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2.drawString("ID: " + n.getIdNodo(), x + 10, y - 5); //
        
        String nombre = n.getNombre(); //
        int anchoNombre = g2.getFontMetrics().stringWidth(nombre);
        g2.drawString(nombre, x + (diametro / 2) - (anchoNombre / 2), y + diametro + 15);
    }
}
}