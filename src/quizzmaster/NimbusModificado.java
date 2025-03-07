package quizzmaster;

import quizzmaster.Dupla;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.Painter;
import javax.swing.UIManager;

public class NimbusModificado {

    public static String TxtColorHTML(String texto, Color c) {
        String hex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
        return "<font color=" + hex + "> " + texto + " </font>";
    }

    public static String nlHTML() {
        return "<br>";
    }

    public static void CargarNimbus()  {
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                try {
                    UIManager.setLookAndFeel(info.getClassName());
                } catch (Exception ex) {
                    System.out.println("aaaa");
                }
                break;
            }
        }
    }
    /**
     * Color de la última carga de color
     */
    public static Color control, textForeground, nimbusBase, nimbusLightBackground, nimbusSelectionBackground;

    public static void CargarColorControl(Color colorControl) {
        control = colorControl;
        UIManager.put("control", control);
    }

    public static void CargarColorNimbusBase(Color colorNimbusBase) {
        nimbusBase = colorNimbusBase;
        UIManager.put("control", nimbusBase);
    }

    public static void CargarColorNimbusLightBackground(Color colorNimbusLightBackground) {
        nimbusLightBackground = colorNimbusLightBackground;
        UIManager.put("nimbusLightBackground", nimbusLightBackground);
    }

    public static void CargarNimbusSelectionBackground(Color colorNimbusSelectionBackground) {
        nimbusSelectionBackground = colorNimbusSelectionBackground;
        UIManager.put("nimbusSelectionBackground", nimbusSelectionBackground);
    }

    public static void CargarTemaClaroNimbus() {
        CargarColorControl(new Color(224, 227, 233));
        CargarColorNimbusBase(new Color(61, 108, 150));
        UIManager.put("nimbusBlueGrey", new Color(214, 217, 223));
        UIManager.put("nimbusFocus", new Color(0, 0, 0, 0));
        UIManager.put("nimbusSelectionBackground", null);
        UIManager.put("textForeground", null);
        CargarColorNimbusLightBackground(null);
        UIManager.put("ScrollBar:ScrollBarThumb[Enabled].backgroundPainter", null);
        UIManager.put("ScrollBar:ScrollBarThumb[MouseOver].backgroundPainter", null);
        UIManager.put("RadioButton[Enabled+Selected].iconPainter", null);
        UIManager.put("RadioButton[Selected+Focused].iconPainter", null);
        UIManager.put("RadioButton[Focused+MouseOver].iconPainter", null);
        UIManager.put("RadioButton[Focused+Selected+MouseOver].iconPainter", null);
        UIManager.put("RadioButton[Selected+MouseOver].iconPainter", null);
        UIManager.put("RadioButton[MouseOver].iconPainter", null);
    }

    public static void CargarTemaColorPersonalizadoNimbus(Color ColorPrincipal, Color ColorDeAtención, float p) {
        float[] HSB = Color.RGBtoHSB(ColorPrincipal.getRed(), ColorPrincipal.getGreen(), ColorPrincipal.getBlue(), null);
        HSB[1] = p * HSB[1];
        if (HSB[2] < .5) {
            Color foreground = new Color(255, 255, 255);
            UIManager.put("textForeground", foreground);
            UIManager.put("nimbusLightBackground", Color.getHSBColor(HSB[0], HSB[1] * .6f, HSB[2] * .5f));
            UIManager.put("MenuItem[Enabled].textForeground", foreground);
            UIManager.put("Menu[Enabled].textForeground", foreground);
        } else {
            UIManager.put("textForeground", new Color(0, 0, 0));
            CargarColorNimbusLightBackground(Color.getHSBColor(HSB[0], HSB[1] * .6f, HSB[2] * 2 > 1 ? 1 : HSB[1] * 2));
        }
        
        UIManager.put("control", ColorPrincipal);
        UIManager.put("ToolTip[Enabled].backgroundPainter", ColorPrincipal);
        
        UIManager.put("nimbusBase", Color.getHSBColor(
                HSB[0],
                HSB[1] * 2 > 1 ? 1 : HSB[1] * 2,
                HSB[2] * .5f
        ));
        UIManager.put("nimbusBlueGrey", Color.getHSBColor(
                HSB[0],
                HSB[1] * 1.5f > 1 ? 1 : HSB[1] * 1.5f,
                HSB[2] * .6f
        ));
        if (ColorDeAtención != null) {
            UIManager.put("nimbusFocus", ColorDeAtención);
        } else {
            UIManager.put("nimbusFocus", Color.getHSBColor(
                    HSB[0],
                    HSB[1] * 1.5f > 1 ? 1 : HSB[1] * 1.5f,
                    1
            ));
        }
        UIManager.put("nimbusSelectionBackground", Color.getHSBColor(
                HSB[0],
                HSB[1] * 2 > 1 ? 1 : HSB[1] * 2,
                HSB[2] * .5f
        ));
    }

    public static void CargarTemaGrisNimbus() {
        CargarTemaGrisNimbus(new Color(180, 180, 180));
    }

    public static void CargarTemaOscuroNimbus() {
        CargarTemaGrisNimbus(new Color(80, 80, 80));
    }

    public static void CargarTemaNegroNimbus() {
        CargarTemaGrisNimbus(new Color(0x121212));
    }
    public static void CargarTemaBlancoNimbus() {
        CargarTemaGrisNimbus(new Color(0xeeeeee));
    }

    private static void CargarTemaGrisNimbus(Color c) {
        CargarTemaColorPersonalizadoNimbus(
                c,
                new Color(0, 150, 255),
                0
        );
        CargarNimbusSelectionBackground(new Color(0, 150, 100));
        
        Painter<Component> p2 = new Painter<Component>() {
            @Override
            public void paint(Graphics2D g, Component object, int width, int height) {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (object instanceof JRadioButton) {
                    int d = 4;
                    Point2D center = new Point2D.Float(width / 2 - d, height / 2 - d);
                    float radius = width / 3.0f;
                    float[] dist = {0.0f, 1.0f};
                    Color[] colors = {Color.CYAN, Color.GREEN};
                    RadialGradientPaint p = new RadialGradientPaint(center, radius, dist, colors);
                    g.setPaint(p);
                    g.fillOval(1, 1, width - d, height - d);
                    g.setColor(Color.BLACK);
                    g.drawOval(1, 1, width - d, height - d);
                    if (((JRadioButton) object).isSelected()) {
                        g.setColor(Color.BLACK);
                        Dupla pos = Dupla.Alinear(
                                new Dupla(width, height),
                                new Dupla(width * .4, height * .4),
                                Dupla.MEDIO, Dupla.MEDIO
                        );
                        g.fillOval(pos.intX(), pos.intY(), (int) (width * .4), (int) (height * .4));
                    }
                } else if (object instanceof JComponent) {
                    GradientPaint p = new GradientPaint(0, 0, Color.YELLOW, 0, height / 2, Color.ORANGE, true);
                    g.setPaint(p);
                    g.fillRoundRect(0, -height, width, 2 * height, 20, 20);
                }
            }
        };
        Painter<Component> p = new Painter<Component>() {
            @Override
            public void paint(Graphics2D g, Component object, int width, int height) {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (object instanceof JRadioButton) {
                    int d = 4;
                    Point2D center = new Point2D.Float(width / 2 - 1, height / 2 - 1);
                    float radius = width / 3.0f;
                    float[] dist = {0.0f, 1.0f};
                    Color[] colors = {Color.CYAN, Color.GREEN};
                    RadialGradientPaint p = new RadialGradientPaint(center, radius, dist, colors);
                    g.setPaint(p);
                    g.fillOval(1, 1, width - d, height - d);
                    g.setColor(Color.BLACK);
                    g.drawOval(1, 1, width - d, height - d);
                    if (((JRadioButton) object).isSelected()) {
                        g.setColor(Color.BLACK);
                        Dupla pos = Dupla.Alinear(
                                new Dupla(width, height),
                                new Dupla(width * .4, height * .4),
                                Dupla.MEDIO, Dupla.MEDIO
                        );
                        g.fillOval(pos.intX(), pos.intY(), (int) (width * .4), (int) (height * .4));
                    }
                } else if (object instanceof JComponent) {
                    GradientPaint p = new GradientPaint(0, 0, Color.ORANGE, 0, height / 2, Color.YELLOW, true);
                    g.setPaint(p);
                    g.fillRoundRect(0, -height, width, 2 * height, 20, 20);
                }
            }
        };
//        UIManager.put("ScrollBar:ScrollBarThumb[Enabled].backgroundPainter", p);
//        UIManager.put("ScrollBar:ScrollBarThumb[MouseOver].backgroundPainter", p2);
        UIManager.put("RadioButton[Enabled+Selected].iconPainter", p);
        UIManager.put("RadioButton[Selected+Focused].iconPainter", p2);
        UIManager.put("RadioButton[Focused+MouseOver].iconPainter", p2);
        UIManager.put("RadioButton[Focused+Selected+MouseOver].iconPainter", p);
        UIManager.put("RadioButton[Selected+MouseOver].iconPainter", p);
        UIManager.put("RadioButton[MouseOver].iconPainter", p);
    }
}
