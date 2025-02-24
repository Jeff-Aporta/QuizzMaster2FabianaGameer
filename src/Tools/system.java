package Tools;

import java.awt.Desktop;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.swing.filechooser.FileSystemView;

public class system {

    public final static String username_windows = System.getenv("username");

    public final static String ruta_appdata = System.getenv("appdata") + "\\";
    public final static String ruta_tmp = System.getProperty("java.io.tmpdir");
    public final static String ruta_jar = System.getProperty("user.dir");
    public final static String ruta_documents = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();

    public final static Boolean is_windows = System.getProperty("os.name").toLowerCase().contains("windows");

    public static void cerrar_sesión_aplicaciones() {//<editor-fold defaultstate="collapsed" desc="Implementación de código »">
        Runtime r = Runtime.getRuntime();
        try {
            r.exec("shutdown -f");
        } catch (Exception e1) {
        }
    }//</editor-fold>

    public static void apagar_computador() {//<editor-fold defaultstate="collapsed" desc="Implementación de código »">
        Runtime r = Runtime.getRuntime();
        try {
            r.exec("shutdown -p");
        } catch (Exception e1) {
        }
    }//</editor-fold>

    public static void reiniciar_computador() {//<editor-fold defaultstate="collapsed" desc="Implementación de código »">
        Runtime r = Runtime.getRuntime();
        try {
            r.exec("shutdown -r  -f");
        } catch (Exception e1) {
        }
    }//</editor-fold>

    public static boolean AbrirRecursoDelComputador(String Dirección) {//<editor-fold defaultstate="collapsed" desc="Implementación de código »">
        return AbrirRecursoDelComputador(new File(Dirección));
    }//</editor-fold>

    public static boolean AbrirRecursoDelComputador(File archivo) {//<editor-fold defaultstate="collapsed" desc="Implementación de código »">
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(archivo);
            return true;
        } catch (Exception e) {
            return false;
        }
    }//</editor-fold>

    public static boolean Abrir_URL_EnNavegador(String Dirección) {//<editor-fold defaultstate="collapsed" desc="Implementación de código »">
        try {
            Desktop dk = Desktop.getDesktop();
            dk.browse(new URI(Dirección));
            return true;
        } catch (Exception e) {
            System.out.println("Error al abrir URL: " + Dirección + "\n" + e.getMessage());
        }
        return false;
    }//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" JavaDoc [≡] ">
    /**
     * <p style="text-align: center;">
     * <img src="https://docs.google.com/drawings/d/e/2PACX-1vQXJ1mWmWVufJ0FEBFsA_bLcy8PyxNjgpdRB8tfY8suqT-wNAEbEqZqM9oaPYueM4zp1unC2aOjTid0/pub?w=400&h=708" alt="">
     * <br> Ver el video que hice explicando como realizar este código
     * <br>https://youtu.be/acrdzJmSNqg<br><br>
     * <img src="https://i.ytimg.com/vi/acrdzJmSNqg/hqdefault.jpg" width="320" height="220" alt=""><br><br>
     * <img src="https://docs.google.com/drawings/d/e/2PACX-1vQ4bebmHUan3es_6PxZWKJooQmno9L2aQeu-6ldc9_SnV8TOEBcqcjFIRsEmiXM9WQlVDkzglIg2Yaz/pub?w=683&h=683" width="150" height="150" alt="">
     */
    //</editor-fold>
    public static boolean VerificarConexiónWeb() {//<editor-fold defaultstate="collapsed" desc="Implementación de código »">
        String dirWeb = "www.youtube.com";
        try {
            Socket socket = new Socket(dirWeb, 80);
            return socket.isConnected();
        } catch (Exception e) {
            return false;
        }
    }//</editor-fold>

    public static boolean Verificar_Existencia_URL(String Dirección) {//<editor-fold defaultstate="collapsed" desc="Implementación de código »">
        try {
            new URL(Dirección).openStream();
            return true;
        } catch (Exception exception) {
            return false;
        }
    }//</editor-fold>

    public static long Obtener_Tamaño_Archivo_URL(String url) {//<editor-fold defaultstate="collapsed" desc="Implementación de código »">
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("HEAD");
            return conn.getContentLengthLong();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }//</editor-fold>

    private static String GenerarDirección_JeffAportaAppData() {//<editor-fold defaultstate="collapsed" desc="Implementación de código »">
        String APPDATA = ruta_appdata;
        String retorno = APPDATA + "Jeff Aporta";
        File carpeta = new File(retorno);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
        return retorno;
    }//</editor-fold>

    private static String GenerarDirección_JeffAportaTemp() {//<editor-fold defaultstate="collapsed" desc="Implementación de código »">
        String TMP_DIR = ruta_tmp;
        String retorno = TMP_DIR + "Jeff Aporta";
        File carpeta = new File(retorno);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
        return retorno;
    }//</editor-fold>

    public static boolean DuplicarArchivo(String Origen, String Destino) {//<editor-fold defaultstate="collapsed" desc="Implementación de código »">
        return DuplicarArchivo(new File(Origen), new File(Destino));
    }//</editor-fold>

    public static boolean DuplicarArchivo(File Origen, File Destino) {//<editor-fold defaultstate="collapsed" desc="Implementación de código »">
        try {
            if (Origen.exists()) {
                Files.copy(
                        Paths.get(Origen.getPath()),
                        Paths.get(Destino.getAbsolutePath()),
                        StandardCopyOption.REPLACE_EXISTING
                );
            } else {
                System.out.println("El fichero " + Origen.getPath() + " no existe en el directorio ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Destino.exists();
    }//</editor-fold>

}
