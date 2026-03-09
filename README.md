# SISTEMA DE RED DE COMPUTADORAS - SIMULADOR DE ENRUTAMIENTO

Este proyecto ha sido desarrollado como parte de la evaluación para la asignatura **Técnicas de Programación 3** en la Universidad Nacional Experimental de Guayana (UNEG). El propósito fundamental es la implementación práctica del Análisis y Diseño Orientado a Objetos (ADOO) y los pilares de la Programación Orientada a Objetos (POO): herencia, polimorfismo, encapsulamiento y abstracción. El software sigue el patrón de arquitectura Modelo-Vista-Controlador (MVC) para garantizar un código modular, bajo acoplamiento y alta cohesión.

## Descripción del Sistema

El software ha sido diseñado para la simulación y optimización de topologías de red complejas, automatizando el cálculo de la latencia mínima entre todos los puntos de la infraestructura tecnológica mediante la Teoría de Grafos.

### Funcionalidades Principales
* **Gestión Dinámica de Dispositivos:** Registro e instanciación de equipos diferenciados por su naturaleza (Computadoras con IP y Routers con cantidad de puertos).
* **Configuración de Enlaces:** Creación de vínculos dirigidos entre dispositivos con asignación de latencia o costo en milisegundos.
* **Cálculo de Rutas Óptimas:** Procesamiento matemático del grafo utilizando el algoritmo de Floyd-Warshall para generar la matriz de distancias mínimas con una complejidad de O(V³).
* **Renderizado Gráfico Interactivo:** Representación visual en tiempo real de la topología (nodos, aristas y pesos) utilizando la API `Graphics2D` de Java.
* **Edición en Tiempo Real:** Capacidad de eliminar nodos de forma selectiva (limpiando sus enlaces asociados) o aplicar un reseteo total del panel de dibujo.
* **Persistencia de Sesiones:** Integración con base de datos SQLite bajo el patrón Singleton, permitiendo guardar historiales cronológicos y cargar configuraciones previas.

## Especificaciones Técnicas

* Lenguaje de Programación: Java (JDK 8).
* Entorno de Desarrollo (IDE): NetBeans 8.2.
* Interfaz Gráfica: Java Swing con soporte de librerías para diseño moderno.
* Motor de Base de Datos: SQLite.

### Dependencias Incluidas (Directorio /lib)
Para el correcto funcionamiento del sistema, se han integrado las siguientes librerías:
* flatlaf-demo-3.6.2.jar: Soporte para la interfaz gráfica moderna y estética de los componentes visuales.
* sqlite-jdbc-3.46.0.0.jar: Conector JDBC necesario para la persistencia y ejecución de sentencias SQL en la base de datos.
* slf4j-api-1.7.36.jar y slf4j-simple-1.7.36.jar: Dependencias para el registro de eventos del sistema.

## Instrucciones de Instalación y Configuración

### 1. Obtención del Código
Descargue el repositorio en formato comprimido (ZIP) o realice la clonación mediante Git. Extraiga el contenido en su directorio de trabajo.

### 2. Importación en el IDE
1. Inicie NetBeans 8.2.
2. Seleccione la opción File > Open Project.
3. Localice el directorio del proyecto y confirme la apertura.

### 3. Configuración de Librerías (Crítico)
Es fundamental verificar que las referencias a los archivos JAR sean correctas:
1. Acceda a las propiedades del proyecto (Properties) y diríjase a la sección Libraries.
2. Si las librerías presentan errores de vinculación (aparecen en rojo), utilice la opción Add JAR/Folder.
3. Vincule los cuatro archivos ubicados en la carpeta lib del proyecto.

### 4. Gestión de Base de Datos
El archivo `simulador_red.db` debe permanecer en el directorio correspondiente configurado en la clase `GestorBaseDatos`. El sistema, mediante el patrón Singleton, utiliza rutas relativas para establecer la conexión automática y gestionar las tablas de nodos y enlaces.

## Información Académica

* Desarrollador: Lisbelis Yemes - C.I: 30437441.
* Institución: Universidad Nacional Experimental de Guayana (UNEG).
* Asignatura: Técnicas de Programación III.
* Sección: 3.
* Facilitador: Ing. Dubraska Roca
* Período Académico: 2025-2.
