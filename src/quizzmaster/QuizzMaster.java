package quizzmaster;

import Tools.callback.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import netscape.javascript.JSObject;
import sounds.MIDI_parser;
import sounds.SecuenceLong_main;

public class QuizzMaster extends Application {

    public static boolean init = false;

    Stage primaryStage;

    public static String //
    host_sql = "localhost:3306",
            bd_sql = "quizzmaster",
            usr_sql = "root",
            pass_sql = "",
            //
            url = "jdbc:mysql://" + host_sql + "/" + bd_sql + "?useSSL=false&serverTimezone=UTC";

    public static WebEngine webEngine;
    public static Connection conexion;

    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
        }
        try {
            conexion = DriverManager.getConnection(url, usr_sql, pass_sql);
            System.out.println("Conexión exitosa");
        } catch (SQLException e) {
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
        new Thread(() -> {
            while (true) {
                String contenido = new Date().getTime() + "";
                if (init) {
                    emit("test", contenido);
                    sleep(100);
                } else {
                    sleep(1000);
                }
            }
        }) {
            {
                start();
            }
        };
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        WebView webView = new WebView();
        webView.setStyle("-fx-background-color: black;");
        webEngine = webView.getEngine();

        JSObject window = (JSObject) webEngine.executeScript("window");
        window.setMember("javaApp", new Socket());

        String url = getClass().getResource("front/index.html").toExternalForm();
        webEngine.load(url);

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject window2 = (JSObject) webEngine.executeScript("window");
                window2.setMember("Socket", new Socket());
            }
        });

        BorderPane root = new BorderPane();
        root.setCenter(webView);
        Scene scene = new Scene(root, 800, 600);
        scene.setFill(Color.BLACK);
        primaryStage.setTitle("QuizzMaster");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("front/img/java.png")));
        primaryStage.show();
        primaryStage.opacityProperty().setValue(0.6);
        primaryStage.setOnCloseRequest(event -> {
            try {
                conexion.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.exit(0);
        });
    }

    public static void emit(String asunto, String contenido) {
        if (webEngine == null) {
            return;
        }
        Platform.runLater(() -> {
            webEngine.executeScript("on('" + asunto + "', '" + contenido + "')");
        });
    }

    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception ex) {
        }
    }

    public static void setTimeout(final Simple_Longs simple) {
        setTimeout(simple, 0);
    }

    public static void setTimeout(final Simple_Longs simple, int ms) {
        new Thread(() -> {
            sleep(ms);
            while (simple.boolrun(System.currentTimeMillis())) {
                sleep(50);
            }
        }).start();
    }

    public class Socket {

        public void emit(String asunto, String contenido) {
            switch (asunto) {
                case "init":
                    init = true;

                    MIDI_parser.midi_pararell(
                            "main-theme",
                            SecuenceLong_main.bank()//
                    )
                            .setLoop(true)
                            .setVolume(0.5f)
                            .fade_value(5)
                            .start_playing();

                    setTimeout((longs) -> {
                        MIDI_parser.midi_pararell("main-theme").fade_value(0);
                        return false;
                    }, 5000);

                    primaryStage.opacityProperty().setValue(1);
                    break;
                default:
                    throw new AssertionError();
            }
        }
    }
}
