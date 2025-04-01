package quizzmaster;

import Tools.callback.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.net.URL;

import static quizzmaster.NimbusModificado.*;
import sounds.MIDI_parser;
import sounds.Play_notes;
import sounds.SecuenceLong_main;

public class JFrameQuizzMaster extends javax.swing.JFrame {

    // Variables de configuración de la base de datos
    public static String host_sql = "localhost:3306";
    public static String bd_sql = "quizzmaster";
    public static String usr_sql = "root";
    public static String pass_sql = "";
    public static String url = "jdbc:mysql://" + host_sql + "/" + bd_sql + "?useSSL=false&serverTimezone=UTC";

    // Variables de autenticación
    public static String user = "admin";
    public static String pass = "admin123";
    public static boolean auth = false;

    // Variables para el estado del quiz
    public static int index_quest = 1;
    static String identificacion_usuario = "";
    static String password_usuario = ""; // Nueva variable para almacenar la contraseña
    static String opcionCorrecta = "";
    static int contadorCorrectas = 0;
    static int contadorRealizadas = 0;
    
    // Variable para almacenar la asignatura seleccionada
    static String asignaturaSeleccionada = "";

    // Variables de la interfaz
    public static final int VISTA_INICIO = 0;
    public static final int VISTA_INICIO_SESION = 1;
    public static final int VISTA_QUIZZ = 2;
    public static final int VISTA_PANEL_ADMN = 3;
    public static final int VISTA_PARTICIPANTES = 4;

    public static int contador_tiempo = -1;

    // Variables de la conexión a la base de datos
    public static Connection conexion;

    Play_notes notas = new Play_notes();

    // Métodos de inicialización
    public static void main(String args[]) {
        try {
            conexion = DriverManager.getConnection(url, usr_sql, pass_sql);
            System.out.println("Conexión exitosa");
        } catch (Exception e) {
            System.out.println("Conexión rechazada");
            JOptionPane.showMessageDialog(
                    null,
                    "Conexión rechazada",
                    "Error SQL",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(-1);
            return;
        }

        CargarNimbus();
        CargarTemaOscuroNimbus();

        new JFrameQuizzMaster();
    }

    public void setOpaqueRecursively(JComponent component, boolean value) {
        // Si es un panel pero no un JTabbedPane, hacerlo transparente
        if (component instanceof JPanel && !(component instanceof JTabbedPane)) {
            ((JPanel) component).setOpaque(value);
        }
        
        // Recursivamente procesar todos los componentes hijos
        for (Component c : component.getComponents()) {
            if (c instanceof JComponent) {
                setOpaqueRecursively((JComponent) c, value);
            }
        }
    }

    public JFrameQuizzMaster() {
        crear_tablas();
        initComponents();
        setOpaqueRecursively((JComponent)getContentPane(), false);
        setExtendedState(MAXIMIZED_BOTH);

        try {
            Image icono = ImageIO.read(getClass().getClassLoader().getResource("srcmedia/java.png"));
            setIconImage(icono);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("No se pudo cargar el icono.");
        }

        jTabbedPane1.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected int calculateTabAreaHeight(int tabPlacement, int runCount, int maxTabHeight) {
                return 0;
            }
        });

        setVisible(true);

        MIDI_parser.midi_pararell(
                "main-theme",
                SecuenceLong_main.bank()//
        )
                .setLoop(true)
                .setVolume(0.5f)
                .fade_value(5)
                .start_playing();

        notas.CambiarInstrumento(Play_notes.HerramientasMIDI.WHISTLE);
        notas.CambiarInstrumento(1, Play_notes.HerramientasMIDI._5TH_SAW_WAVE);
        notas.CambiarInstrumento(2, Play_notes.HerramientasMIDI.SQUARE);
        notas.CambiarInstrumento(3, Play_notes.HerramientasMIDI.CRYSTAL);

        setTimeout(() -> {
            MIDI_parser.midi_pararell("main-theme").fade_value(0);
        }, 5000);
        
        // Cargar los participantes existentes
        cargarParticipantes();
    }

    // Métodos de la interfaz
    private void cambiarVista(int index) {
        if (index == VISTA_QUIZZ) {
            empezar_quizz();
        }
        if (auth && index == VISTA_INICIO_SESION) {
            index = VISTA_PANEL_ADMN;
        }
        if (index == VISTA_PANEL_ADMN) {
            if (auth) {
                actualizarPreguntasYOpciones();
            } else {
                cambiarVista(VISTA_INICIO);
            }
        }
        jTabbedPane1.setSelectedIndex(index);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
        this.getContentPane().setFocusable(true);
        this.getContentPane().requestFocusInWindow();
    }

    private void crear_tablas() {
        String sqlPreguntas = "CREATE TABLE IF NOT EXISTS preguntas (\n"
                + "id INT PRIMARY KEY,\n"
                + "pregunta VARCHAR(255),\n"
                + "opcion_correcta VARCHAR(255),\n"
                + "distractor1 VARCHAR(255),\n"
                + "distractor2 VARCHAR(255),\n"
                + "distractor3 VARCHAR(255),\n"
                + "asignatura VARCHAR(50) DEFAULT 'Asignatura 1'\n"
                + ");";
        String sqlResultados = "CREATE TABLE IF NOT EXISTS resultados (\n"
                + "id INT AUTO_INCREMENT PRIMARY KEY,\n"
                + "identificacion VARCHAR(255),\n"
                + "puntaje INT,\n"
                + "total INT\n"
                + ");";
        String sqlParticipantes = "CREATE TABLE IF NOT EXISTS participantes (\n"
                + "id INT PRIMARY KEY,\n"
                + "identificacion VARCHAR(255) NOT NULL,\n"
                + "password VARCHAR(255) NOT NULL\n"
                + ");";
        String sqlAsignaturasCompletadas = "CREATE TABLE IF NOT EXISTS asignaturas_completadas (\n"
                + "id INT AUTO_INCREMENT PRIMARY KEY,\n"
                + "identificacion VARCHAR(255) NOT NULL,\n"
                + "asignatura VARCHAR(50) NOT NULL,\n"
                + "UNIQUE KEY unique_participante_asignatura (identificacion, asignatura)\n"
                + ");";
        try {
            Statement stmt = conexion.createStatement();
            stmt.executeUpdate(sqlPreguntas);
            stmt.executeUpdate(sqlResultados);
            stmt.executeUpdate(sqlParticipantes);
            stmt.executeUpdate(sqlAsignaturasCompletadas);
        } catch (SQLException e) {
            e.printStackTrace();
            notas.Reproducir(2, "2SI", 200, -1, 0.6f);
            JOptionPane.showMessageDialog(null, "Error al crear las tablas: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void empezar_quizz() {
        contadorCorrectas = 0;
        contadorRealizadas = 0;
        cargarPreguntaPorId(index_quest);
        index_quest++;
    }

    private void cargarPreguntaPorId(int contador) {
        // Verificar si se han terminado las preguntas
        int d = 0;
        switch (asignaturaSeleccionada) {
            case "Asignatura 1":
                d = 1;
                break;
            case "Asignatura 2":
                d = 2;
                break;
            case "Asignatura 3":
                d = 3;
                break;
            case "Asignatura 4":
                d = 4;
                break;
            case "Asignatura 5":
                d = 5;
                break;
        }
        int index = contador + d*10;
        int cindex = index-d*10;
        if (cindex > 4) { // Asumiendo que hay 4 preguntas
            terminar_quiz();
            return;
        }
        
        // Si no hay asignatura seleccionada, usar la predeterminada
        if (asignaturaSeleccionada == null || asignaturaSeleccionada.isEmpty()) {
            asignaturaSeleccionada = "Asignatura 1";
        }


        
        // Cargar la pregunta correspondiente desde la base de datos
        String sql = "SELECT pregunta, opcion_correcta, distractor1, distractor2, distractor3 FROM preguntas WHERE id = ? AND asignatura = ?";
        try {
            PreparedStatement pstmt = conexion.prepareStatement(sql);
            pstmt.setInt(1, index);
            pstmt.setString(2, asignaturaSeleccionada);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String pregunta = rs.getString("pregunta");
                String opcionCorrecta_ = rs.getString("opcion_correcta");
                String distractor1 = rs.getString("distractor1");
                String distractor2 = rs.getString("distractor2");
                String distractor3 = rs.getString("distractor3");

                opcionCorrecta = opcionCorrecta_;

                // Crear un arreglo con la opción correcta y los distractores
                String[] opciones = { opcionCorrecta_, distractor1, distractor2, distractor3 };

                // Mezclar el arreglo usando el algoritmo de Fisher-Yates
                for (int i = opciones.length - 1; i > 0; i--) {
                    int j = (int) (Math.random() * (i + 1));
                    // Intercambiar opciones[i] con el elemento en posición aleatoria
                    String temp = opciones[i];
                    opciones[i] = opciones[j];
                    opciones[j] = temp;
                }

                // Actualizar la interfaz
                jLabel9.setText(pregunta);
                jButton7.setText(opciones[0]);
                jButton8.setText(opciones[1]);
                jButton9.setText(opciones[2]);
                jButton10.setText(opciones[3]);
            } else {
                notas.Reproducir(2, "2SI", 200, -1, 0.6f);
                JOptionPane.showMessageDialog(null, "No se encontró la pregunta.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            notas.Reproducir(2, "2SI", 200, -1, 0.6f);
            JOptionPane.showMessageDialog(null, "Error al cargar la pregunta: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verificarRespuesta(JButton botonSeleccionado) {
        String respuestaSeleccionada = botonSeleccionado.getText();
        if (respuestaSeleccionada.equals(opcionCorrecta)) {
            contadorCorrectas++;
            new Thread(() -> {
                for (int i = 0; i < 20; i++) {
                    notas.Reproducir(2, 63 + i, 20 + i * 2, 40, 0.6f);
                }
            }).start();
            JOptionPane.showMessageDialog(null, "¡Respuesta correcta!", "Correcto", JOptionPane.INFORMATION_MESSAGE);
        } else {
            notas.Reproducir(2, "2SI", 200, -1, 0.6f);
            JOptionPane.showMessageDialog(null, "Respuesta incorrecta. Intenta de nuevo.", "Incorrecto",
                    JOptionPane.ERROR_MESSAGE);
        }
        contadorRealizadas++;
        jLabel30.setText("Correctas: " + contadorCorrectas);
        jLabel31.setText("Realizadas: " + contadorRealizadas);
        cargarPreguntaPorId(index_quest++);
    }

    private void terminar_quiz() {
        // Guardar el puntaje en la base de datos
        String sql = "INSERT INTO resultados (identificacion, puntaje, total) VALUES (?, ?, ?);";
        try {
            PreparedStatement pstmt = conexion.prepareStatement(sql);
            pstmt.setString(1, identificacion_usuario);
            pstmt.setInt(2, contadorCorrectas);
            pstmt.setInt(3, contadorRealizadas);
            pstmt.executeUpdate();
            
            // Registrar que el usuario ha completado esta asignatura
            String sqlCompletado = "INSERT INTO asignaturas_completadas (identificacion, asignatura) VALUES (?, ?)";
            PreparedStatement pstmtCompletado = conexion.prepareStatement(sqlCompletado);
            pstmtCompletado.setString(1, identificacion_usuario);
            pstmtCompletado.setString(2, asignaturaSeleccionada);
            pstmtCompletado.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al guardar el puntaje: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        StringBuilder resultados = new StringBuilder();

        resultados.append("<html><body><h2>Resultados:</h2><ol>");
        resultados.append("<li>Correctas: " + contadorCorrectas + "</li>");
        resultados.append("<li>Incorrectas: " + (contadorRealizadas - contadorCorrectas) + "</li>");
        resultados.append("<li>Total: " + contadorRealizadas + "</li>");
        resultados.append("</ol></body></html>");

        JOptionPane.showMessageDialog(null,
                resultados.toString(),
                "Fin del Quiz", JOptionPane.INFORMATION_MESSAGE);
        cambiarVista(VISTA_INICIO);
    }

    private void actualizarPreguntasYOpciones() {
        // Si no hay asignatura seleccionada, establecer una por defecto
        if (asignaturaSeleccionada == null || asignaturaSeleccionada.isEmpty()) {
            asignaturaSeleccionada = "Asignatura 1";
        }
        int d = 0;
        switch (asignaturaSeleccionada) {
            case "Asignatura 1":
                d = 1;
                break;
            case "Asignatura 2":
                d = 2;
                break;
            case "Asignatura 3":
                d = 3;
                break;
            case "Asignatura 4":
                d = 4;
                break;
            case "Asignatura 5":
                d = 5;
                break;
        }
        
        String sql = "SELECT id, pregunta, opcion_correcta, distractor1, distractor2, distractor3 FROM preguntas WHERE asignatura = ? ORDER BY id;";
        try {
            PreparedStatement pstmt = conexion.prepareStatement(sql);
            pstmt.setString(1, asignaturaSeleccionada);
            ResultSet rs = pstmt.executeQuery();
            int i = 0;
            jTextField3.setText("");
            jTextField4.setText("");
            jTextField5.setText("");
            jTextField6.setText("");
            jTextField7.setText("");
            jTextField8.setText("");
            jTextField9.setText("");
            jTextField10.setText("");
            jTextField11.setText("");
            jTextField12.setText("");
            jTextField13.setText("");
            jTextField14.setText("");
            jTextField15.setText("");
            jTextField16.setText("");
            jTextField17.setText("");
            jTextField18.setText("");
            jTextField19.setText("");
            jTextField20.setText("");
            jTextField21.setText("");
            jTextField22.setText("");
            while (rs.next()) {
                int id = rs.getInt("id");
                String pregunta = rs.getString("pregunta");
                String opcionCorrecta = rs.getString("opcion_correcta");
                String distractor1 = rs.getString("distractor1");
                String distractor2 = rs.getString("distractor2");
                String distractor3 = rs.getString("distractor3");

                // Asignar preguntas y opciones a índices fijos
                final int index = id - d * 10;
                switch (index) {
                    case 1:
                        jLabel8.setText("Pregunta 1");
                        jTextField3.setText(pregunta);
                        jTextField4.setText(opcionCorrecta);
                        jTextField5.setText(distractor1);
                        jTextField6.setText(distractor2);
                        jTextField7.setText(distractor3);
                        break;
                    case 2:
                        jLabel14.setText("Pregunta 2");
                        jTextField8.setText(pregunta);
                        jTextField9.setText(opcionCorrecta);
                        jTextField10.setText(distractor1);
                        jTextField11.setText(distractor2);
                        jTextField12.setText(distractor3);
                        break;
                    case 3:
                        jLabel19.setText("Pregunta 3");
                        jTextField13.setText(pregunta);
                        jTextField14.setText(opcionCorrecta);
                        jTextField15.setText(distractor1);
                        jTextField16.setText(distractor2);
                        jTextField17.setText(distractor3);
                        break;
                    case 4:
                        jLabel24.setText("Pregunta 4");
                        jTextField18.setText(pregunta);
                        jTextField19.setText(opcionCorrecta);
                        jTextField20.setText(distractor1);
                        jTextField21.setText(distractor2);
                        jTextField22.setText(distractor3);
                        break;
                }
                i++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al cargar las preguntas: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarParticipantes() {
        // Limpiar todos los campos de texto
        jTextField2.setText("");
        jTextField23.setText("");
        jTextField24.setText("");
        jTextField25.setText("");
        jTextField26.setText("");
        jTextField27.setText("");
        jTextField28.setText("");
        jTextField29.setText("");
        jTextField30.setText("");
        jTextField31.setText("");
        jTextField32.setText("");
        jTextField33.setText("");
        jTextField34.setText("");
        jTextField35.setText("");
        jTextField36.setText("");
        jTextField37.setText("");
        
        // Cargar participantes desde la base de datos
        String sql = "SELECT id, identificacion, password FROM participantes ORDER BY id";
        try {
            PreparedStatement pstmt = conexion.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String identificacion = rs.getString("identificacion");
                String password = rs.getString("password");
                
                // Asignar a los campos correspondientes según el ID
                switch (id) {
                    case 1:
                        jTextField2.setText(identificacion);
                        jTextField23.setText(password);
                        break;
                    case 2:
                        jTextField24.setText(identificacion);
                        jTextField25.setText(password);
                        break;
                    case 3:
                        jTextField26.setText(identificacion);
                        jTextField27.setText(password);
                        break;
                    case 4:
                        jTextField28.setText(identificacion);
                        jTextField29.setText(password);
                        break;
                    case 5:
                        jTextField30.setText(identificacion);
                        jTextField31.setText(password);
                        break;
                    case 6:
                        jTextField32.setText(identificacion);
                        jTextField33.setText(password);
                        break;
                    case 7:
                        jTextField34.setText(identificacion);
                        jTextField35.setText(password);
                        break;
                    case 8:
                        jTextField36.setText(identificacion);
                        jTextField37.setText(password);
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al cargar los participantes: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Métodos de eventos
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        cambiarVista(VISTA_INICIO_SESION);
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        StringBuilder resultados = new StringBuilder();
        String sql = "SELECT identificacion, puntaje, total FROM resultados;";
        try {
            PreparedStatement pstmt = conexion.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            resultados.append("<html><body><h2>Resultados:</h2><ol>");
            while (rs.next()) {
                String id = rs.getString("identificacion");
                int puntaje = rs.getInt("puntaje");
                int total = rs.getInt("total");
                resultados.append("<li>" + id + ": Correctas " + puntaje + ", Incorrectas " + (total - puntaje)
                        + ", Total " + total + "</li>");
            }
            resultados.append("</ol></body></html>");
            JOptionPane.showMessageDialog(null, resultados.toString(), "Lista de Resultados",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al obtener resultados: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {
        identificacion_usuario = JOptionPane.showInputDialog(null, "Ingrese su identificación:");
        if (identificacion_usuario != null && identificacion_usuario.length() >= 3) {
            // Solicitar contraseña
            password_usuario = JOptionPane.showInputDialog(null, "Ingrese su contraseña:");
            if (password_usuario != null && !password_usuario.isEmpty()) {
                // Verificar las credenciales en la base de datos
                String sql = "SELECT * FROM participantes WHERE identificacion = ? AND password = ?";
                try {
                    PreparedStatement pstmt = conexion.prepareStatement(sql);
                    pstmt.setString(1, identificacion_usuario);
                    pstmt.setString(2, password_usuario);
                    ResultSet rs = pstmt.executeQuery();
                    
                    if (rs.next()) {
                        // Credenciales correctas
                        JOptionPane.showMessageDialog(null, "¡Bienvenido " + identificacion_usuario + "!", "Inicio de sesión exitoso", JOptionPane.INFORMATION_MESSAGE);
                        
                        // Solicitar el número de asignatura
                        solicitarAsignatura();
                    } else {
                        // Credenciales incorrectas
                        JOptionPane.showMessageDialog(null, "Identificación o contraseña incorrectas", "Error de autenticación", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al verificar credenciales: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        "La contraseña no puede estar vacía.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null,
                    "La identificación no puede estar vacía y debe tener al menos 3 caracteres.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void solicitarAsignatura() {
        // Obtener las asignaturas disponibles (que no hayan sido completadas por el usuario)
        try {
            // Primero, obtener todas las asignaturas disponibles
            Set<String> todasLasAsignaturas = new HashSet<>();
            String sqlAsignaturas = "SELECT DISTINCT asignatura FROM preguntas";
            PreparedStatement pstmtAsignaturas = conexion.prepareStatement(sqlAsignaturas);
            ResultSet rsAsignaturas = pstmtAsignaturas.executeQuery();
            
            while (rsAsignaturas.next()) {
                todasLasAsignaturas.add(rsAsignaturas.getString("asignatura"));
            }
            
            // Luego, obtener las asignaturas ya completadas por el usuario
            Set<String> asignaturasCompletadas = new HashSet<>();
            String sqlCompletadas = "SELECT asignatura FROM asignaturas_completadas WHERE identificacion = ?";
            PreparedStatement pstmtCompletadas = conexion.prepareStatement(sqlCompletadas);
            pstmtCompletadas.setString(1, identificacion_usuario);
            ResultSet rsCompletadas = pstmtCompletadas.executeQuery();
            
            while (rsCompletadas.next()) {
                asignaturasCompletadas.add(rsCompletadas.getString("asignatura"));
            }
            
            // Filtrar para obtener solo las asignaturas no completadas
            todasLasAsignaturas.removeAll(asignaturasCompletadas);
            
            // Verificar si hay asignaturas disponibles para el usuario
            if (todasLasAsignaturas.isEmpty()) {
                // El usuario ya completó todas las asignaturas
                JOptionPane.showMessageDialog(null, 
                        "Ya has completado todas las asignaturas disponibles.", 
                        "No hay asignaturas disponibles", 
                        JOptionPane.INFORMATION_MESSAGE);
                cambiarVista(VISTA_INICIO);
                return;
            }
            
            // Crear un arreglo ordenado de asignaturas disponibles
            List<String> asignaturasDisponibles = new ArrayList<>(todasLasAsignaturas);
            Collections.sort(asignaturasDisponibles);
            
            // Construir el mensaje para mostrar asignaturas disponibles
            StringBuilder mensaje = new StringBuilder("Seleccione el número de la asignatura que desea realizar:\n\n");
            for (int i = 0; i < asignaturasDisponibles.size(); i++) {
                mensaje.append((i + 1)).append(". ").append(asignaturasDisponibles.get(i)).append("\n");
            }
            
            // Solicitar al usuario que seleccione una asignatura
            String seleccion = JOptionPane.showInputDialog(null, mensaje.toString());
            
            if (seleccion != null && !seleccion.isEmpty()) {
                try {
                    int opcion = Integer.parseInt(seleccion);
                    if (opcion >= 1 && opcion <= asignaturasDisponibles.size()) {
                        // Asignatura válida seleccionada
                        asignaturaSeleccionada = asignaturasDisponibles.get(opcion - 1);
                        
                        // Iniciar el quiz con la asignatura seleccionada
                        cambiarVista(VISTA_QUIZZ);
                    } else {
                        JOptionPane.showMessageDialog(null, 
                                "Por favor, seleccione un número de asignatura válido.", 
                                "Selección inválida", 
                                JOptionPane.ERROR_MESSAGE);
                        solicitarAsignatura(); // Volver a solicitar asignatura
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, 
                            "Por favor, ingrese un número válido.", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    solicitarAsignatura(); // Volver a solicitar asignatura
                }
            } else {
                // Usuario canceló la selección, volver a la pantalla inicial
                cambiarVista(VISTA_INICIO);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                    "Error al obtener asignaturas: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            cambiarVista(VISTA_INICIO);
        }
    }

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {
        jButton4.setMargin(new Insets(0, 0, 0, 0));
        cambiarVista(VISTA_INICIO);
    }

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {
        // Reiniciar contador en cada nuevo quizz
        contadorCorrectas = 0;
        contadorRealizadas = 0;
        index_quest = 1;
        jLabel30.setText("Correctas: 0");
        jLabel31.setText("Realizadas: 0");
        
        // Cargar primera pregunta según la asignatura seleccionada
        cargarPreguntaPorId(index_quest);
    }

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {
        char[] passwordChars = jPasswordField1.getPassword();
        String password = new String(passwordChars);
        if (jTextField1.getText().equals(user) && password.equals(pass)) {
            auth = true;
            cambiarVista(VISTA_PANEL_ADMN);
        } else {
            JOptionPane.showMessageDialog(null, "Autenticación fallida. Por favor, verifica tus credenciales.",
                    "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
        }
    }// GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {
        verificarRespuesta(jButton7);
    }

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {
        verificarRespuesta(jButton8);
    }

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {
        verificarRespuesta(jButton9);
    }

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {
        verificarRespuesta(jButton10);
    }

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton16ActionPerformed
        // ID estático correspondiente al índice de pregunta
        int id1 = 1;
        int id2 = 2;
        int id3 = 3;
        int id4 = 4;

        // Si no hay asignatura seleccionada, usar la predeterminada
        if (asignaturaSeleccionada == null || asignaturaSeleccionada.isEmpty()) {
            asignaturaSeleccionada = "Asignatura 1";
        }

        int d = 0;
        switch (asignaturaSeleccionada) {
            case "Asignatura 1":
                d = 1;
                break;
            case "Asignatura 2":
                d = 2;
                break;
            case "Asignatura 3":
                d = 3;
                break;
            case "Asignatura 4":
                d = 4;
                break;
            case "Asignatura 5":
                d = 5;
                break;
        }

        String pregunta1 = jTextField3.getText();
        String opcionCorrecta1 = jTextField4.getText();
        String distractor1_1 = jTextField5.getText();
        String distractor1_2 = jTextField6.getText();
        String distractor1_3 = jTextField7.getText();

        String pregunta2 = jTextField8.getText();
        String opcionCorrecta2 = jTextField9.getText();
        String distractor2_1 = jTextField10.getText();
        String distractor2_2 = jTextField11.getText();
        String distractor2_3 = jTextField12.getText();

        String pregunta3 = jTextField13.getText();
        String opcionCorrecta3 = jTextField14.getText();
        String distractor3_1 = jTextField15.getText();
        String distractor3_2 = jTextField16.getText();
        String distractor3_3 = jTextField17.getText();

        String pregunta4 = jTextField18.getText();
        String opcionCorrecta4 = jTextField19.getText();
        String distractor4_1 = jTextField20.getText();
        String distractor4_2 = jTextField21.getText();
        String distractor4_3 = jTextField22.getText();

        String sql = "INSERT INTO preguntas (id, pregunta, opcion_correcta, distractor1, distractor2, distractor3, asignatura) VALUES (?, ?, ?, ?, ?, ?, ?)";
        sql += " ON DUPLICATE KEY UPDATE pregunta = ?, opcion_correcta = ?, distractor1 = ?, distractor2 = ?, distractor3 = ?, asignatura = ?";
        try {
            PreparedStatement pstmt = conexion.prepareStatement(sql);
            // Inserción para la primera pregunta
            pstmt.setInt(1, id1+d*10);
            pstmt.setString(2, pregunta1);
            pstmt.setString(3, opcionCorrecta1);
            pstmt.setString(4, distractor1_1);
            pstmt.setString(5, distractor1_2);
            pstmt.setString(6, distractor1_3);
            pstmt.setString(7, asignaturaSeleccionada);
            pstmt.setString(8, pregunta1);
            pstmt.setString(9, opcionCorrecta1);
            pstmt.setString(10, distractor1_1);
            pstmt.setString(11, distractor1_2);
            pstmt.setString(12, distractor1_3);
            pstmt.setString(13, asignaturaSeleccionada);
            pstmt.executeUpdate();
            // Inserción para la segunda pregunta
            pstmt.setInt(1, id2+d*10);
            pstmt.setString(2, pregunta2);
            pstmt.setString(3, opcionCorrecta2);
            pstmt.setString(4, distractor2_1);
            pstmt.setString(5, distractor2_2);
            pstmt.setString(6, distractor2_3);
            pstmt.setString(7, asignaturaSeleccionada);
            pstmt.setString(8, pregunta2);
            pstmt.setString(9, opcionCorrecta2);
            pstmt.setString(10, distractor2_1);
            pstmt.setString(11, distractor2_2);
            pstmt.setString(12, distractor2_3);
            pstmt.setString(13, asignaturaSeleccionada);
            pstmt.executeUpdate();
            // Inserción para la tercera pregunta
            pstmt.setInt(1, id3+d*10);
            pstmt.setString(2, pregunta3);
            pstmt.setString(3, opcionCorrecta3);
            pstmt.setString(4, distractor3_1);
            pstmt.setString(5, distractor3_2);
            pstmt.setString(6, distractor3_3);
            pstmt.setString(7, asignaturaSeleccionada);
            pstmt.setString(8, pregunta3);
            pstmt.setString(9, opcionCorrecta3);
            pstmt.setString(10, distractor3_1);
            pstmt.setString(11, distractor3_2);
            pstmt.setString(12, distractor3_3);
            pstmt.setString(13, asignaturaSeleccionada);
            pstmt.executeUpdate();
            // Inserción para la cuarta pregunta
            pstmt.setInt(1, id4 +d*10);
            pstmt.setString(2, pregunta4);
            pstmt.setString(3, opcionCorrecta4);
            pstmt.setString(4, distractor4_1);
            pstmt.setString(5, distractor4_2);
            pstmt.setString(6, distractor4_3);
            pstmt.setString(7, asignaturaSeleccionada);
            pstmt.setString(8, pregunta4);
            pstmt.setString(9, opcionCorrecta4);
            pstmt.setString(10, distractor4_1);
            pstmt.setString(11, distractor4_2);
            pstmt.setString(12, distractor4_3);
            pstmt.setString(13, asignaturaSeleccionada);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Preguntas guardadas exitosamente!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al guardar las preguntas: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }// GEN-LAST:event_jButton16ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        asignaturaSeleccionada = "Asignatura 1";
        actualizarPreguntasYOpciones();
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        asignaturaSeleccionada = "Asignatura 2";
        actualizarPreguntasYOpciones();
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        asignaturaSeleccionada = "Asignatura 3";
        actualizarPreguntasYOpciones();
    }//GEN-LAST:event_jButton17ActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        asignaturaSeleccionada = "Asignatura 4";
        actualizarPreguntasYOpciones();
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        asignaturaSeleccionada = "Asignatura 5";
        actualizarPreguntasYOpciones();
    }//GEN-LAST:event_jButton19ActionPerformed

    // Métodos de eventos
    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {                                          
        // Guardar datos de participantes
        try {
            // Preparar la sentencia SQL
            String sql = "INSERT INTO participantes (id, identificacion, password) VALUES (?, ?, ?) " +
                         "ON DUPLICATE KEY UPDATE identificacion = ?, password = ?";
            PreparedStatement pstmt = conexion.prepareStatement(sql);
            
            // Guardar participante 1
            if (!jTextField2.getText().isEmpty() && !jTextField23.getText().isEmpty()) {
                pstmt.setInt(1, 1);
                pstmt.setString(2, jTextField2.getText());
                pstmt.setString(3, jTextField23.getText());
                pstmt.setString(4, jTextField2.getText());
                pstmt.setString(5, jTextField23.getText());
                pstmt.executeUpdate();
            }
            
            // Guardar participante 2
            if (!jTextField24.getText().isEmpty() && !jTextField25.getText().isEmpty()) {
                pstmt.setInt(1, 2);
                pstmt.setString(2, jTextField24.getText());
                pstmt.setString(3, jTextField25.getText());
                pstmt.setString(4, jTextField24.getText());
                pstmt.setString(5, jTextField25.getText());
                pstmt.executeUpdate();
            }
            
            // Guardar participante 3
            if (!jTextField26.getText().isEmpty() && !jTextField27.getText().isEmpty()) {
                pstmt.setInt(1, 3);
                pstmt.setString(2, jTextField26.getText());
                pstmt.setString(3, jTextField27.getText());
                pstmt.setString(4, jTextField26.getText());
                pstmt.setString(5, jTextField27.getText());
                pstmt.executeUpdate();
            }
            
            // Guardar participante 4
            if (!jTextField28.getText().isEmpty() && !jTextField29.getText().isEmpty()) {
                pstmt.setInt(1, 4);
                pstmt.setString(2, jTextField28.getText());
                pstmt.setString(3, jTextField29.getText());
                pstmt.setString(4, jTextField28.getText());
                pstmt.setString(5, jTextField29.getText());
                pstmt.executeUpdate();
            }
            
            // Guardar participante 5
            if (!jTextField30.getText().isEmpty() && !jTextField31.getText().isEmpty()) {
                pstmt.setInt(1, 5);
                pstmt.setString(2, jTextField30.getText());
                pstmt.setString(3, jTextField31.getText());
                pstmt.setString(4, jTextField30.getText());
                pstmt.setString(5, jTextField31.getText());
                pstmt.executeUpdate();
            }
            
            // Guardar participante 6
            if (!jTextField32.getText().isEmpty() && !jTextField33.getText().isEmpty()) {
                pstmt.setInt(1, 6);
                pstmt.setString(2, jTextField32.getText());
                pstmt.setString(3, jTextField33.getText());
                pstmt.setString(4, jTextField32.getText());
                pstmt.setString(5, jTextField33.getText());
                pstmt.executeUpdate();
            }
            
            // Guardar participante 7
            if (!jTextField34.getText().isEmpty() && !jTextField35.getText().isEmpty()) {
                pstmt.setInt(1, 7);
                pstmt.setString(2, jTextField34.getText());
                pstmt.setString(3, jTextField35.getText());
                pstmt.setString(4, jTextField34.getText());
                pstmt.setString(5, jTextField35.getText());
                pstmt.executeUpdate();
            }
            
            // Guardar participante 8
            if (!jTextField36.getText().isEmpty() && !jTextField37.getText().isEmpty()) {
                pstmt.setInt(1, 8);
                pstmt.setString(2, jTextField36.getText());
                pstmt.setString(3, jTextField37.getText());
                pstmt.setString(4, jTextField36.getText());
                pstmt.setString(5, jTextField37.getText());
                pstmt.executeUpdate();
            }
            
            JOptionPane.showMessageDialog(null, "Participantes guardados exitosamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarParticipantes(); // Método para cargar los participantes existentes en los campos
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al guardar los participantes: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        cambiarVista(VISTA_PANEL_ADMN);
    }

    // Métodos de utilidad
    public static void setTimeout(Simple c, int ms) {
        new Thread(() -> {
            try {
                Thread.sleep(ms);
                c.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private JButton crearBoton(Color color) {
        return crearBoton(color, 0);
    }

    public static BufferedImage imagen = null;

    private JPanel crearPanel() {
        if (imagen == null) {
            try {
                Image img = ImageIO.read(new URL("https://i.ibb.co/XxtK2wk8/bg-1.jpg"));
                imagen = new BufferedImage(
                        img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB
                );
                Graphics2D g2 = (Graphics2D) imagen.getGraphics();
                g2.drawImage(img, 0, 0, null);
                g2.setColor(new Color(0,0,0,100));
                g2.fillRect(0, 0, imagen.getWidth(null), imagen.getHeight(null));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int w = this.getWidth();
                int h = this.getHeight();
                float escala = Math.max((float) w / imagen.getWidth(null), (float) h / imagen.getHeight(null));
                int new_w = (int) (imagen.getWidth(null) * escala);
                int new_h = (int) (imagen.getHeight(null) * escala);
                int x = (w - new_w) / 2;
                int y = (h - new_h) / 2;
                g.drawImage(imagen, x, y, new_w, new_h, null);
            }
        };
        return panel;
    }

    private JButton crearBoton(Color color, int dx) {
        JButton boton = new JButton() {
            boolean hover = false;

            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        hover = true;
                        notas.Reproducir(0, "5RE", 200, -1, 0.5f);
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        notas.Reproducir(1, (int) ((10) * Math.random() + 65), 200, -1);
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        hover = false;
                    }
                });
                setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                setBackground(new Color(0, 0, 0, 0));
            }

            @Override
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(255, 128, 0, color.getAlpha()));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                if (hover) {
                    g2.setColor(new Color(255, 255, 255, 100));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                super.paint(g);
            }

        };
        return boton;
    }

    // Componentes de la interfaz
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = crearPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = crearBoton(new Color(30, 144, 255));
        jLabel2 = new javax.swing.JLabel();
        jButton3 = crearBoton(new Color(30, 144, 255));
        jPanel3 = crearPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jButton4 = crearBoton(new Color(30, 144, 255, 0));
        jLabel6 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jPasswordField1 = new javax.swing.JPasswordField();
        jButton6 = crearBoton(new Color(30, 144, 255));
        jPanel6 = crearPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jButton7 = crearBoton(new Color(30, 144, 255));
        jButton8 = crearBoton(new Color(30, 144, 255));
        jButton9 = crearBoton(new Color(30, 144, 255));
        jButton10 = crearBoton(new Color(30, 144, 255));
        jPanel8 = crearPanel();
        jButton5 = crearBoton(new Color(30, 144, 255));
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel10 = crearPanel();
        jPanel11 = new javax.swing.JPanel();
        jButton11 = crearBoton(new Color(30, 144, 255));
        jButton16 = crearBoton(new Color(30, 144, 255));
        jButton2 = crearBoton(new Color(30, 144, 255));
        jButton12 = crearBoton(new Color(30, 144, 255));
        jPanel35 = new javax.swing.JPanel();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jButton13 = crearBoton(new Color(30, 144, 255));
        jPanel9 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jPanel13 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jPanel15 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jPanel16 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jPanel17 = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jPanel19 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jPanel20 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jTextField10 = new javax.swing.JTextField();
        jPanel21 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jPanel22 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jTextField12 = new javax.swing.JTextField();
        jPanel23 = new javax.swing.JPanel();
        jPanel24 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jTextField13 = new javax.swing.JTextField();
        jPanel25 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jTextField14 = new javax.swing.JTextField();
        jPanel26 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jTextField15 = new javax.swing.JTextField();
        jPanel27 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jTextField16 = new javax.swing.JTextField();
        jPanel28 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jTextField17 = new javax.swing.JTextField();
        jPanel29 = new javax.swing.JPanel();
        jPanel30 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jTextField18 = new javax.swing.JTextField();
        jPanel31 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jTextField19 = new javax.swing.JTextField();
        jPanel32 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        jTextField20 = new javax.swing.JTextField();
        jPanel33 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jTextField21 = new javax.swing.JTextField();
        jPanel34 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jTextField22 = new javax.swing.JTextField();
        jPanel36 = crearPanel();
        jLabel4 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jTextField23 = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jTextField24 = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        jTextField25 = new javax.swing.JTextField();
        jTextField26 = new javax.swing.JTextField();
        jTextField27 = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jTextField28 = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        jTextField29 = new javax.swing.JTextField();
        jTextField30 = new javax.swing.JTextField();
        jTextField31 = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jTextField32 = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        jTextField33 = new javax.swing.JTextField();
        jTextField34 = new javax.swing.JTextField();
        jTextField35 = new javax.swing.JTextField();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jTextField36 = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        jTextField37 = new javax.swing.JTextField();
        jButton20 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridLayout(1, 0));

        jPanel2.setLayout(new java.awt.GridLayout(0, 1, 0, 20));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("QuizzMaster");
        jPanel2.add(jLabel1);

        jButton1.setFont(new java.awt.Font("sansserif", 0, 18)); // NOI18N
        jButton1.setText("Iniciar sesión");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("_____________________________");
        jPanel2.add(jLabel2);

        jButton3.setFont(new java.awt.Font("sansserif", 0, 18)); // NOI18N
        jButton3.setText("Hacer el Quizz");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton3);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(311, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(312, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(137, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(138, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("inicio", jPanel1);

        jPanel4.setLayout(new java.awt.GridLayout(0, 1, 0, 10));

        jButton4.setText("Volver");
        jButton4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel4.add(jPanel5);

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("_____________________________");
        jPanel4.add(jLabel6);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Inicia sesión");
        jPanel4.add(jLabel3);

        jLabel5.setText("ID");
        jPanel4.add(jLabel5);
        jPanel4.add(jTextField1);

        jLabel7.setText("Contraseña");
        jPanel4.add(jLabel7);
        jPanel4.add(jPasswordField1);

        jButton6.setFont(new java.awt.Font("sansserif", 0, 18)); // NOI18N
        jButton6.setText("Ingresar");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton6);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(297, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(298, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(78, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(78, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Inicio de sesión", jPanel3);

        jPanel7.setLayout(new java.awt.GridLayout(0, 1, 0, 10));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Pregunta");
        jPanel7.add(jLabel9);

        jButton7.setText("Opción 1");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jPanel7.add(jButton7);

        jButton8.setText("Opción 2");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        jPanel7.add(jButton8);

        jButton9.setText("Opción 3");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        jPanel7.add(jButton9);

        jButton10.setText("Opción 4");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });
        jPanel7.add(jButton10);

        jButton5.setText("Finalizar");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel30.setText("Correctas: 0");

        jLabel31.setText("Realizadas: 0");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel30)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel31)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 54, Short.MAX_VALUE)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 647, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(43, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap(78, Short.MAX_VALUE)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(78, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Resolver Quizz", jPanel6);

        jPanel10.setLayout(new java.awt.GridLayout(0, 1, 0, 10));

        jPanel11.setLayout(new java.awt.GridLayout(1, 0, 15, 0));

        jButton11.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jButton11.setText("Inicio");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });
        jPanel11.add(jButton11);

        jButton16.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jButton16.setText("Guardar");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });
        jPanel11.add(jButton16);

        jButton2.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jButton2.setText("Ver resultados");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel11.add(jButton2);

        jButton12.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jButton12.setText("PDF");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });
        jPanel11.add(jButton12);

        jPanel10.add(jPanel11);

        jButton14.setText("Asignatura 1");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton15.setText("Asignatura 2");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jButton17.setText("Asignatura 3");
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jButton18.setText("Asignatura 4");
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        jButton19.setText("Asignatura 5");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        jButton13.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jButton13.setText("Participantes");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel35Layout = new javax.swing.GroupLayout(jPanel35);
        jPanel35.setLayout(jPanel35Layout);
        jPanel35Layout.setHorizontalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel35Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 215, Short.MAX_VALUE)
                .addComponent(jButton13)
                .addContainerGap())
        );
        jPanel35Layout.setVerticalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel35Layout.createSequentialGroup()
                .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton14)
                        .addComponent(jButton15)
                        .addComponent(jButton17)
                        .addComponent(jButton18)
                        .addComponent(jButton19))
                    .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(46, Short.MAX_VALUE))
        );

        jPanel10.add(jPanel35);

        jPanel9.setLayout(new java.awt.GridLayout(1, 0));

        jPanel12.setLayout(new java.awt.GridLayout(0, 1));

        jLabel8.setText("Pregunta 1");
        jPanel12.add(jLabel8);

        jTextField3.setText("¿Cuánto es 1+1?");
        jPanel12.add(jTextField3);

        jPanel9.add(jPanel12);

        jPanel13.setLayout(new java.awt.GridLayout(0, 1));

        jLabel10.setText("Correcta");
        jPanel13.add(jLabel10);

        jTextField4.setText("2");
        jPanel13.add(jTextField4);

        jPanel9.add(jPanel13);

        jPanel14.setLayout(new java.awt.GridLayout(0, 1));

        jLabel11.setText("Incorrecta 1");
        jPanel14.add(jLabel11);

        jTextField5.setText("1");
        jPanel14.add(jTextField5);

        jPanel9.add(jPanel14);

        jPanel15.setLayout(new java.awt.GridLayout(0, 1));

        jLabel12.setText("Incorrecta 2");
        jPanel15.add(jLabel12);

        jTextField6.setText("3");
        jPanel15.add(jTextField6);

        jPanel9.add(jPanel15);

        jPanel16.setLayout(new java.awt.GridLayout(0, 1));

        jLabel13.setText("Incorrecta 3");
        jPanel16.add(jLabel13);

        jTextField7.setText("4");
        jPanel16.add(jTextField7);

        jPanel9.add(jPanel16);

        jPanel10.add(jPanel9);

        jPanel17.setLayout(new java.awt.GridLayout(1, 0));

        jPanel18.setLayout(new java.awt.GridLayout(0, 1));

        jLabel14.setText("Pregunta 2");
        jPanel18.add(jLabel14);

        jTextField8.setText("¿Cuánto es 2+1?");
        jPanel18.add(jTextField8);

        jPanel17.add(jPanel18);

        jPanel19.setLayout(new java.awt.GridLayout(0, 1));

        jLabel15.setText("Correcta");
        jPanel19.add(jLabel15);

        jTextField9.setText("3");
        jPanel19.add(jTextField9);

        jPanel17.add(jPanel19);

        jPanel20.setLayout(new java.awt.GridLayout(0, 1));

        jLabel16.setText("Incorrecta 1");
        jPanel20.add(jLabel16);

        jTextField10.setText("1");
        jPanel20.add(jTextField10);

        jPanel17.add(jPanel20);

        jPanel21.setLayout(new java.awt.GridLayout(0, 1));

        jLabel17.setText("Incorrecta 2");
        jPanel21.add(jLabel17);

        jTextField11.setText("2");
        jPanel21.add(jTextField11);

        jPanel17.add(jPanel21);

        jPanel22.setLayout(new java.awt.GridLayout(0, 1));

        jLabel18.setText("Incorrecta 3");
        jPanel22.add(jLabel18);

        jTextField12.setText("4");
        jPanel22.add(jTextField12);

        jPanel17.add(jPanel22);

        jPanel10.add(jPanel17);

        jPanel23.setLayout(new java.awt.GridLayout(1, 0));

        jPanel24.setLayout(new java.awt.GridLayout(0, 1));

        jLabel19.setText("Pregunta 3");
        jPanel24.add(jLabel19);

        jTextField13.setText("¿Cuánto es 3+1?");
        jPanel24.add(jTextField13);

        jPanel23.add(jPanel24);

        jPanel25.setLayout(new java.awt.GridLayout(0, 1));

        jLabel20.setText("Correcta");
        jPanel25.add(jLabel20);

        jTextField14.setText("4");
        jPanel25.add(jTextField14);

        jPanel23.add(jPanel25);

        jPanel26.setLayout(new java.awt.GridLayout(0, 1));

        jLabel21.setText("Incorrecta 1");
        jPanel26.add(jLabel21);

        jTextField15.setText("1");
        jPanel26.add(jTextField15);

        jPanel23.add(jPanel26);

        jPanel27.setLayout(new java.awt.GridLayout(0, 1));

        jLabel22.setText("Incorrecta 2");
        jPanel27.add(jLabel22);

        jTextField16.setText("2");
        jPanel27.add(jTextField16);

        jPanel23.add(jPanel27);

        jPanel28.setLayout(new java.awt.GridLayout(0, 1));

        jLabel23.setText("Incorrecta 3");
        jPanel28.add(jLabel23);

        jTextField17.setText("5");
        jPanel28.add(jTextField17);

        jPanel23.add(jPanel28);

        jPanel10.add(jPanel23);

        jPanel29.setLayout(new java.awt.GridLayout(1, 0));

        jPanel30.setLayout(new java.awt.GridLayout(0, 1));

        jLabel24.setText("Pregunta 4");
        jPanel30.add(jLabel24);

        jTextField18.setText("¿Cuánto es 4+1?");
        jPanel30.add(jTextField18);

        jPanel29.add(jPanel30);

        jPanel31.setLayout(new java.awt.GridLayout(0, 1));

        jLabel25.setText("Correcta");
        jPanel31.add(jLabel25);

        jTextField19.setText("5");
        jPanel31.add(jTextField19);

        jPanel29.add(jPanel31);

        jPanel32.setLayout(new java.awt.GridLayout(0, 1));

        jLabel26.setText("Incorrecta 1");
        jPanel32.add(jLabel26);

        jTextField20.setText("2");
        jPanel32.add(jTextField20);

        jPanel29.add(jPanel32);

        jPanel33.setLayout(new java.awt.GridLayout(0, 1));

        jLabel27.setText("Incorrecta 2");
        jPanel33.add(jLabel27);

        jTextField21.setText("3");
        jPanel33.add(jTextField21);

        jPanel29.add(jPanel33);

        jPanel34.setLayout(new java.awt.GridLayout(0, 1));

        jLabel28.setText("Incorrecta 3");
        jPanel34.add(jLabel28);

        jTextField22.setText("4");
        jPanel34.add(jTextField22);

        jPanel29.add(jPanel34);

        jPanel10.add(jPanel29);

        jScrollPane2.setViewportView(jPanel10);

        jTabbedPane1.addTab("Panel admn", jScrollPane2);

        jLabel4.setText("ID");

        jLabel29.setText("contraseña");

        jLabel32.setText("ID");

        jLabel33.setText("contraseña");

        jLabel34.setText("ID");

        jLabel35.setText("contraseña");

        jLabel36.setText("ID");

        jLabel37.setText("contraseña");

        jLabel38.setText("ID");

        jLabel39.setText("contraseña");

        jLabel40.setText("ID");

        jLabel41.setText("contraseña");

        jLabel42.setText("ID");

        jLabel43.setText("contraseña");

        jLabel44.setText("ID");

        jLabel45.setText("contraseña");

        jButton20.setText("Guardar");
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel36Layout = new javax.swing.GroupLayout(jPanel36);
        jPanel36.setLayout(jPanel36Layout);
        jPanel36Layout.setHorizontalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel36Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton20)
                    .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel36Layout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel29)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jTextField23, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel36Layout.createSequentialGroup()
                            .addComponent(jLabel32)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jTextField24, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel33)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jTextField25, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel36Layout.createSequentialGroup()
                            .addComponent(jLabel36)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jTextField28, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel37)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jTextField29, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel36Layout.createSequentialGroup()
                            .addComponent(jLabel34)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jTextField26, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel35)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jTextField27, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel36Layout.createSequentialGroup()
                            .addComponent(jLabel40)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jTextField32, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel41)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jTextField33, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel36Layout.createSequentialGroup()
                            .addComponent(jLabel38)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jTextField30, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel39)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jTextField31, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel36Layout.createSequentialGroup()
                            .addComponent(jLabel44)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jTextField36, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel45)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jTextField37, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel36Layout.createSequentialGroup()
                            .addComponent(jLabel42)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jTextField34, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel43)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jTextField35, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(484, Short.MAX_VALUE))
        );
        jPanel36Layout.setVerticalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel36Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29)
                    .addComponent(jTextField23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(jTextField24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33)
                    .addComponent(jTextField25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36)
                    .addComponent(jTextField28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37)
                    .addComponent(jTextField29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(jTextField26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35)
                    .addComponent(jTextField27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40)
                    .addComponent(jTextField32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41)
                    .addComponent(jTextField33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel38)
                    .addComponent(jTextField30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39)
                    .addComponent(jTextField31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel44)
                    .addComponent(jTextField36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel45)
                    .addComponent(jTextField37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel42)
                    .addComponent(jTextField34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel43)
                    .addComponent(jTextField35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton20)
                .addContainerGap(200, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Participantes", jPanel36);

        getContentPane().add(jTabbedPane1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        cambiarVista(VISTA_PARTICIPANTES);
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton12ActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jButton12ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        cambiarVista(VISTA_INICIO);
        auth = false; // Cierra sesión
    }//GEN-LAST:event_jButton11ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel32;
    private javax.swing.JPanel jPanel33;
    private javax.swing.JPanel jPanel34;
    private javax.swing.JPanel jPanel35;
    private javax.swing.JPanel jPanel36;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JScrollPane jScrollPane2;
    public static javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField15;
    private javax.swing.JTextField jTextField16;
    private javax.swing.JTextField jTextField17;
    private javax.swing.JTextField jTextField18;
    private javax.swing.JTextField jTextField19;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField20;
    private javax.swing.JTextField jTextField21;
    private javax.swing.JTextField jTextField22;
    private javax.swing.JTextField jTextField23;
    private javax.swing.JTextField jTextField24;
    private javax.swing.JTextField jTextField25;
    private javax.swing.JTextField jTextField26;
    private javax.swing.JTextField jTextField27;
    private javax.swing.JTextField jTextField28;
    private javax.swing.JTextField jTextField29;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField30;
    private javax.swing.JTextField jTextField31;
    private javax.swing.JTextField jTextField32;
    private javax.swing.JTextField jTextField33;
    private javax.swing.JTextField jTextField34;
    private javax.swing.JTextField jTextField35;
    private javax.swing.JTextField jTextField36;
    private javax.swing.JTextField jTextField37;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    // End of variables declaration//GEN-END:variables
}
