package quizzmaster;

import Tools.callback.*;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import static quizzmaster.NimbusModificado.*;
import sounds.MIDI_parser;
import sounds.SecuenceLong_main;

public class JFrameQuizzMaster extends javax.swing.JFrame {

    public static String //
            host_sql = "localhost:3306",
            bd_sql = "quizzmaster",
            usr_sql = "root",
            pass_sql = "",
            //
            url = "jdbc:mysql://" + host_sql + "/" + bd_sql + "?useSSL=false&serverTimezone=UTC";

    public static Connection conexion;

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

    public JFrameQuizzMaster() {
        initComponents();
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

        setTimeout(() -> {
            MIDI_parser.midi_pararell("main-theme").fade_value(0);
        }, 5000);
    }

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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridLayout(1, 0));

        jPanel2.setLayout(new java.awt.GridLayout(0, 1, 0, 20));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("QuizzMaster");
        jPanel2.add(jLabel1);

        jButton1.setText("Iniciar sesión");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1);

        jButton2.setText("Crear cuenta");
        jPanel2.add(jButton2);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("_____________________________");
        jPanel2.add(jLabel2);

        jButton3.setText("Hacer un Quizz");
        jPanel2.add(jButton3);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(264, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(264, Short.MAX_VALUE))
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

        jButton4.setText("Inicio");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(183, 183, 183))
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
        jPanel4.add(jTextField2);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("_____________________________");
        jPanel4.add(jLabel4);

        jButton6.setText("Ingresar");
        jPanel4.add(jButton6);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(250, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(250, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(78, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(78, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Inicio de sesión", jPanel3);

        getContentPane().add(jTabbedPane1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cambiarVista(int index) {
        jTabbedPane1.setSelectedIndex(index);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
        this.getContentPane().setFocusable(true);
        this.getContentPane().requestFocusInWindow();
    }

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        cambiarVista(0);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        cambiarVista(1);
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    public static javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
