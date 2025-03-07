package quizzmaster;

import java.awt.Component;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import javax.swing.SwingUtilities;

import static quizzmaster.Matemática.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import static java.lang.Math.*;

public final class Dupla {

    public double X, Y;

    public boolean Protegido = false;

    public static final Dupla ORIGEN = new Dupla().Proteger();

    public static final Dupla IDENTIDAD = new Dupla(1).Proteger();

    public static final Dupla DIMENSIÓN_PANTALLA = new Dupla(
            Toolkit.getDefaultToolkit().getScreenSize()
    ).Proteger();

    public static final Dupla THD_1280x720 = new Dupla(1280, 720).Proteger();

    public static final Dupla TM_905x509 = THD_1280x720.Multiplicar(raiz2(1 / 2f)).Proteger();

    public static final Dupla TM_640x360 = THD_1280x720.Mitad().Proteger();

    public static final Dupla TSD_320x180 = THD_1280x720.Cuarto().Proteger();

    public static final Dupla TSD2_160x90 = THD_1280x720.Dividir(8).Proteger();

    public final static byte ARRIBA = 1;

    public final static byte ABAJO = 2;

    public final static byte MEDIO = 3;

    public final static byte IZQUIERDA = 4;

    public final static byte DERECHA = 5;

    public final static byte POR_DEFECTO = -1;

    public Dupla() {
        X = 0;
        Y = 0;
    }

    public Dupla(double X, double Y) {
        this.X = X;
        this.Y = Y;
    }

    public Dupla(double n) {
        this.X = n;
        this.Y = n;
    }

    public Dupla(double... d) {
        X = d[0];
        Y = d[1];
    }

    public Dupla(Object obj) {
        if (obj == null) {
            ReemplazarXY(0, 0);
        }
        if (obj instanceof Dupla) {
            ReemplazarXY(((Dupla) obj).X, ((Dupla) obj).Y);
        } else if (obj instanceof Number) {
            ReemplazarXY(((Number) obj).doubleValue(), ((Number) obj).doubleValue());
        } else if (obj instanceof Dimension) {
            ReemplazarXY(((Dimension) obj).width, ((Dimension) obj).height);
        } else if (obj instanceof Dimension2D) {
            ReemplazarXY(((Dimension2D) obj).getWidth(), ((Dimension2D) obj).getHeight());
        } else if (obj instanceof BufferedImage) {
            ReemplazarXY(((BufferedImage) obj).getWidth(null), ((BufferedImage) obj).getHeight(null));
        } else if (obj instanceof Image) {
            ReemplazarXY(((Image) obj).getWidth(null), ((Image) obj).getHeight(null));
        } else if (obj instanceof Point) {
            ReemplazarXY(((Point) obj).x, ((Point) obj).y);
        } else if (obj instanceof Component) {
            ReemplazarXY(((Component) obj).getWidth(), ((Component) obj).getHeight());
        } else if (obj instanceof RoundRectangle2D) {
            ReemplazarXY(((RoundRectangle2D) obj).getWidth(), ((RoundRectangle2D) obj).getHeight());
        } else if (obj instanceof Rectangle2D) {
            ReemplazarXY(((Rectangle2D) obj).getWidth(), ((Rectangle2D) obj).getHeight());
        } else {
            throw new RuntimeException("El objeto usado para inicializar la dupla no es válido");
        }
    }

    public Dupla(double Radio, double θ, byte TIPO_ÁNGULO) {
        X = Radio * cos(Radianes(θ, TIPO_ÁNGULO));
        Y = Radio * sen(Radianes(θ, TIPO_ÁNGULO));
    }

    public Dupla Proteger() {
        Protegido = true;
        return this;
    }

    public Dupla Desproteger() {
        Protegido = false;
        return this;
    }

    public Dupla Adicionar(Object obj) {
        Dupla dupla = new Dupla(obj);
        if (Protegido) {
            return Clon().Adicionar(dupla.X, dupla.Y);
        }
        return Adicionar(dupla.X, dupla.Y);
    }

    public Dupla Adicionar(double n) {
        if (Protegido) {
            return Clon().Adicionar(n, n);
        }
        return Adicionar(n, n);
    }

    public Dupla Adicionar(double x, double y) {
        if (Protegido) {
            return Clon().Adicionar(x, y);
        }
        X += x;
        Y += y;
        return this;
    }

    public Dupla AdicionarX(Object obj) {
        if (Protegido) {
            return Clon().AdicionarX(obj);
        }
        X += new Dupla(obj).X;
        return this;
    }

    public Dupla AdicionarX(double x) {
        if (Protegido) {
            return Clon().AdicionarX(x);
        }
        X += x;
        return this;
    }

    public Dupla AdicionarY(Object obj) {
        if (Protegido) {
            return Clon().AdicionarY(obj);
        }
        Y += new Dupla(obj).Y;
        return this;
    }

    public Dupla AdicionarY(double y) {
        if (Protegido) {
            return Clon().Adicionar(0, y);
        }
        return Adicionar(0, y);
    }

    public Dupla Trasladar(Object obj) {
        Dupla dupla = new Dupla(obj);
        if (Protegido) {
            return Clon().Adicionar(dupla);
        }
        return Adicionar(dupla);
    }

    public Dupla Trasladar(double n) {
        if (Protegido) {
            return Clon().Adicionar(n);
        }
        return Adicionar(n);
    }

    public Dupla Trasladar(double x, double y) {
        if (Protegido) {
            return Clon().Trasladar(x, y);
        }
        return Adicionar(x, y);
    }

    public Dupla Desplazar1_Derecha() {
        if (Protegido) {
            return Clon().Desplazar1_Derecha();
        }
        X++;
        return this;
    }

    public Dupla Desplazar1_Izquierda() {
        if (Protegido) {
            return Clon().Desplazar1_Izquierda();
        }
        X--;
        return this;
    }

    public Dupla Desplazar1_Abajo() {
        if (Protegido) {
            return Clon().Desplazar1_Abajo();
        }
        Y++;
        return this;
    }

    public Dupla Desplazar1_Arriba() {
        if (Protegido) {
            return Clon().Desplazar1_Arriba();
        }
        Y--;
        return this;
    }

    public Dupla Desplazar1_DerechaArriba() {
        if (Protegido) {
            return Clon().Desplazar1_Derecha();
        }
        Y--;
        X++;
        return this;
    }

    public Dupla Desplazar1_IzquierdaAbajo() {
        if (Protegido) {
            return Clon().Desplazar1_IzquierdaAbajo();
        }
        X--;
        Y++;
        return this;
    }

    public Dupla Desplazar1_DerechaAbajo() {
        if (Protegido) {
            return Clon().Desplazar1_DerechaAbajo();
        }
        Y++;
        X++;
        return this;
    }

    public Dupla Desplazar1_IzquierdaArriba() {
        if (Protegido) {
            return Clon().Desplazar1_IzquierdaArriba();
        }
        Y--;
        X--;
        return this;
    }

    public Dupla Sustraer(Object obj) {
        Dupla dupla = new Dupla(obj);
        if (Protegido) {
            return Clon().Adicionar(-dupla.X, -dupla.Y);
        }
        return Adicionar(-dupla.X, -dupla.Y);
    }

    public Dupla Sustraer(double n) {
        if (Protegido) {
            return Clon().Adicionar(-n, -n);
        }
        return Adicionar(-n, -n);
    }

    public Dupla Sustraer(double x, double y) {
        if (Protegido) {
            return Clon().Sustraer(x, y);
        }
        X -= x;
        Y -= y;
        return this;
    }

    public Dupla SustraerX(Object obj) {
        Dupla dupla = new Dupla(obj);
        if (Protegido) {
            return Clon().Adicionar(-dupla.X, 0);
        }
        return Adicionar(-dupla.X, 0);
    }

    public Dupla SustraerX(double x) {
        if (Protegido) {
            return Clon().Adicionar(-x, 0);
        }
        return Adicionar(-x, 0);
    }

    public Dupla SustraerY(Object obj) {
        Dupla dupla = new Dupla(obj);
        if (Protegido) {
            return Clon().Adicionar(0, -dupla.Y);
        }
        return Adicionar(0, -dupla.Y);
    }

    public Dupla SustraerY(double y) {
        if (Protegido) {
            return Clon().Adicionar(0, -y);
        }
        return Adicionar(0, -y);
    }

    public Dupla Multiplicar(double n) {
        if (Protegido) {
            return Clon().Multiplicar(n);
        }
        X *= n;
        Y *= n;
        return this;
    }

    public Dupla Multiplicar(double x, double y) {
        if (Protegido) {
            return Clon().Multiplicar(x, y);
        }
        X *= x;
        Y *= y;
        return this;
    }

    public Dupla Multiplicar(Object obj) {
        Dupla dupla = new Dupla(obj);
        if (Protegido) {
            return Clon().Multiplicar(obj);
        }
        X *= dupla.X;
        Y *= dupla.Y;
        return this;
    }

    public Dupla Multiplicar(double a, double b, double c, double d) {
        if (Protegido) {
            return Clon().ReemplazarXY(a * X + b * Y, c * X + d * Y);
        }
        return ReemplazarXY(a * X + b * Y, c * X + d * Y);
    }

    public Dupla MultiplicarX(double Numero) {
        if (Protegido) {
            return Clon().MultiplicarX(Numero);
        }
        X *= Numero;
        return this;
    }

    public Dupla MultiplicarX(Object obj) {
        if (Protegido) {
            return Clon().MultiplicarX(obj);
        }
        X *= new Dupla(obj).X;
        return this;
    }

    public Dupla MultiplicarY(double Numero) {
        if (Protegido) {
            return Clon().MultiplicarY(Numero);
        }
        Y *= Numero;
        return this;
    }

    public Dupla MultiplicarY(Object obj) {
        if (Protegido) {
            return Clon().MultiplicarY(obj);
        }
        Y *= new Dupla(obj).Y;
        return this;
    }

    public Dupla Escalar(Object obj) {
        Dupla dupla = new Dupla(obj);
        if (Protegido) {
            return Clon().Escalar(dupla);
        }
        return Multiplicar(dupla);
    }

    public Dupla Escalar(double x, double y) {
        if (Protegido) {
            return Clon().Multiplicar(x, y);
        }
        return Multiplicar(x, y);
    }

    public Dupla Escalar(double escala) {
        if (Protegido) {
            return Clon().Multiplicar(escala);
        }
        return Multiplicar(escala);
    }

    public Dupla CambiarSentido() {
        if (Protegido) {
            return Clon().Multiplicar(-1);
        }
        return Multiplicar(-1);
    }

    public Dupla CambiarSentidoX() {
        if (Protegido) {
            return Clon().MultiplicarX(-1);
        }
        return MultiplicarX(-1);
    }

    public Dupla CambiarSentidoY() {
        if (Protegido) {
            return Clon().MultiplicarY(-1);
        }
        return MultiplicarY(-1);
    }

    public Dupla Dividir(double x, double y) {
        if (Protegido) {
            return Clon().Dividir(x, y);
        }
        X /= x;
        Y /= y;
        return this;
    }

    public Dupla Dividir(double n) {
        if (Protegido) {
            return Clon().Dividir(n);
        }
        X /= n;
        Y /= n;
        return this;
    }

    public Dupla Dividir(Object obj) {
        Dupla dupla = new Dupla(obj);
        if (Protegido) {
            return Clon().Dividir(dupla);
        }
        X /= dupla.X;
        Y /= dupla.Y;
        return this;
    }

    public Dupla DividirX(double Numero) {
        if (Protegido) {
            return Clon().DividirX(Numero);
        }
        X /= Numero;
        return this;
    }

    public Dupla DividirX(Object obj) {
        Dupla dupla = new Dupla(obj);
        if (Protegido) {
            return Clon().DividirX(dupla);
        }
        X /= dupla.X;
        return this;
    }

    public Dupla DividirY(double Numero) {
        if (Protegido) {
            return Clon().DividirY(Numero);
        }
        Y /= Numero;
        return this;
    }

    public Dupla DividirY(Object obj) {
        Dupla dupla = new Dupla(obj);
        if (Protegido) {
            return Clon().DividirY(dupla);
        }
        Y /= dupla.Y;
        return this;
    }

    public Dupla Normalizar() {
        if (Protegido) {
            return Clon().Dividir(Magnitud());
        }
        return this.Dividir(Magnitud());
    }

    public Dupla ReemplazarXY(double nuevoX, double nuevoY) {
        if (Protegido) {
            return Clon().ReemplazarXY(nuevoX, nuevoY);
        }
        X = nuevoX;
        Y = nuevoY;
        return this;
    }

    public Dupla ReemplazarXY(Object obj) {
        Dupla dupla = new Dupla(obj);
        if (Protegido) {
            return Clon().ReemplazarXY(obj);
        }
        X = dupla.X;
        Y = dupla.Y;
        return this;
    }

    public Dupla ReemplazarX(double nuevoX) {
        if (Protegido) {
            return Clon().ReemplazarX(nuevoX);
        }
        X = nuevoX;
        return this;
    }

    public Dupla ReemplazarX(Object obj) {
        Dupla dupla = new Dupla(obj);
        if (Protegido) {
            return Clon().ReemplazarX(obj);
        }
        X = dupla.X;
        return this;
    }

    public Dupla ReemplazarY(double nuevoY) {
        if (Protegido) {
            return Clon().ReemplazarY(nuevoY);
        }
        Y = nuevoY;
        return this;
    }

    public Dupla ReemplazarY(Object obj) {
        Dupla dupla = new Dupla(obj);
        if (Protegido) {
            return Clon().ReemplazarY(obj);
        }
        Y = dupla.Y;
        return this;
    }

    public Dupla ReemplazarRθ(double nuevoRadio, double Nuevoθ) {
        if (Protegido) {
            return Clon().ReemplazarXY(nuevoRadio * cos(Nuevoθ), nuevoRadio * sen(Nuevoθ));
        }
        return ReemplazarXY(nuevoRadio * cos(Nuevoθ), nuevoRadio * sen(Nuevoθ));
    }

    public Dupla ReemplazarRadio(double NuevoRadio) {
        double θ = θ();
        if (Protegido) {
            return Clon().ReemplazarXY(NuevoRadio * cos(θ), NuevoRadio * sen(θ));
        }
        return ReemplazarXY(NuevoRadio * cos(θ), NuevoRadio * sen(θ));
    }

    public Dupla Reemplazarθ(double θ) {
        double r = Radio();
        if (Protegido) {
            return Clon().ReemplazarXY(r * cos(θ), r * sen(θ));
        }
        return ReemplazarXY(r * cos(θ), r * sen(θ));
    }

    public Dupla girar(double θ) {
        double a = cos(θ), b = sen(θ);
        if (Protegido) {
            return Clon().ReemplazarXY(a * X - b * Y, b * X + a * Y);
        }
        return ReemplazarXY(a * X - b * Y, b * X + a * Y);
    }

    public Dupla girar(double Δθ, byte TIPO_ÁNGULO) {
        if (Protegido) {
            return Clon().girar(Radianes(Δθ, TIPO_ÁNGULO));
        }
        return girar(Radianes(Δθ, TIPO_ÁNGULO));
    }

    public Dupla g90Derecha() {
        if (Protegido) {
            return Clon().ReemplazarXY(Y, -X);
        }
        return ReemplazarXY(Y, -X);
    }

    public Dupla g90Izquierda() {
        if (Protegido) {
            return Clon().ReemplazarXY(-Y, X);
        }
        return ReemplazarXY(-Y, X);
    }

    public Dupla Invertir_XYaYX() {
        if (Protegido) {
            return Clon().ReemplazarXY(Y, X);
        }
        return ReemplazarXY(Y, X);
    }

    public Dupla Atracción(Dupla puntoPrueba, double r) {
        if (Protegido) {
            return Clon().PuntoAtractor(puntoPrueba, r);
        }
        if (puntoPrueba.distanciaAlPunto(this) == 0) {
            return this;
        }
        Dupla Atracción = new Dupla(r / (puntoPrueba.distanciaAlPunto(this)), puntoPrueba.Ángulo(this), ÁNGULO_RADIANES);
        return Atracción;
    }

    public Dupla PuntoAtractor(Dupla puntoPrueba, double r) {
        Dupla Atracción = Atracción(puntoPrueba, r);
        return Atracción.Radio() > Dupla.this.distanciaAlPunto(puntoPrueba) ? ReemplazarXY(puntoPrueba) : Trasladar(Atracción);
    }

    public boolean EsOrtogonal(double x, double y) {
        return ProductoPunto(x, y) == 0;
    }

    public boolean EsOrtogonal(Object obj) {
        Dupla d = new Dupla(obj);
        return ProductoPunto(d) == 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        }
        final Dupla other = new Dupla(obj);
        if (Double.doubleToLongBits(this.X) != Double.doubleToLongBits(other.X)) {
            return false;
        }
        if (Double.doubleToLongBits(this.Y) != Double.doubleToLongBits(other.Y)) {
            return false;
        }
        return true;
    }

    public boolean esIgual(Object obj) {
        return equals(obj);
    }

    public boolean esIgual(double x, double y) {
        return X == x && Y == y;
    }

    public boolean esMayor(Object obj) {
        Dupla d = new Dupla(obj);
        return X > d.X || Y > d.Y;
    }

    public boolean esMayorOIgual(Object obj) {
        Dupla d = new Dupla(obj);
        return X >= d.X || Y >= d.Y;
    }

    public boolean esMenor(Object obj) {
        Dupla d = new Dupla(obj);
        return X < d.X || Y < d.Y;
    }

    public boolean esMenorOIgual(Object obj) {
        Dupla d = new Dupla(obj);
        return X <= d.X && Y <= d.Y;
    }

    public boolean esDiferente(Object obj) {
        return !esIgual(obj);
    }

    public boolean estáDentro(int xi, int xf, int yi, int yf) {
        return XestáEntre(xi, xf) && YestáEntre(yi, yf);
    }

    public boolean XestáEntre(int xi, int xf) {
        return X >= min(xi, xf) && X <= max(xi, xf);
    }

    public boolean XesMayor(Object obj) {
        return X > new Dupla(obj).X;
    }

    public boolean XesMenor(Object obj) {
        return X < new Dupla(obj).X;
    }

    public boolean XesMayor_Igual(Object obj) {
        return X >= new Dupla(obj).X;
    }

    public boolean XesMenor_Igual(Object obj) {
        return X <= new Dupla(obj).X;
    }

    public boolean YestáEntre(int yi, int yf) {
        return Y >= min(yi, yf) && Y <= max(yi, yf);
    }

    public boolean YesMenor(Object obj) {
        return Y < new Dupla(obj).Y;
    }

    public boolean YesMayor(Object obj) {
        return Y > new Dupla(obj).Y;
    }

    public boolean YesMenor_Igual(Object obj) {
        return Y <= new Dupla(obj).Y;
    }

    public boolean YesMayor_Igual(Object obj) {
        return Y >= new Dupla(obj).Y;
    }

    public double ProductoPunto(double x, double y) {
        return X * x + Y * y;
    }

    public double ProductoPunto(Dupla p) {
        return X * p.X + Y * p.Y;
    }

    public double Ángulo(Object Cola) {
        Dupla dupla = new Dupla(Cola);
        return Ángulo(dupla.X, dupla.Y);
    }

    public double Ángulo(Dupla Cola) {
        return Ángulo(Cola.X, Cola.Y);
    }

    public double Ángulo(double Cola_X, double Cola_Y) {
        double θ = atan2(Y - Cola_Y, X - Cola_X);
        return θ < 0 ? θ + 2 * π : θ;
    }

    public double Ángulo() {
        double θ = atan2(Y, X);
        return θ < 0 ? θ + 2 * π : θ;
    }

    public double θ() {
        return Ángulo();
    }

    public double Área() {
        return abs(X * Y);
    }

    public int intÁrea() {
        return (int) Área();
    }

    public int intX() {
        return (int) X;
    }

    public int Columna() {
        return intX();
    }

    public int Ancho() {
        return rndX();
    }

    public int rndX() {
        return (int) round(X);
    }

    public float floatX() {
        return (float) X;
    }

    public double absX() {
        return abs(X);
    }

    public int intY() {
        return (int) Y;
    }

    public int Fila() {
        return intY();
    }

    public int rndY() {
        return (int) round(Y);
    }

    public int Alto() {
        return rndY();
    }

    public float floatY() {
        return (float) Y;
    }

    public double absY() {
        return abs(Y);
    }

    public double distanciaAlPunto(Dupla punto) {
        return Pitagoras(X - punto.X, Y - punto.Y);
    }

    public double distanciaAlPunto(double x, double y) {
        return Pitagoras(X - x, Y - y);
    }

    public double Radio(Dupla Cola) {
        return distanciaAlPunto(Cola);
    }

    public double Radio() {
        return Pitagoras(X, Y);
    }

    public double Magnitud() {
        return Radio();
    }

    public double Magnitud2() {
        double r = Radio();
        return r * r;
    }

    public Dupla Clon() {
        return new Dupla(X, Y);
    }

    public Dupla ElementoOpuesto() {
        return Clon().CambiarSentido();
    }

    public Dupla distanciaDirigida(Dupla punto) {
        return Clon().Sustraer(punto);
    }

    public Dupla distanciaDirigida(double x, double y) {
        return Clon().Sustraer(x, y);
    }

    public Dupla Centro() {
        return Clon().Dividir(2);
    }

    public Dupla Mitad() {
        return Clon().Dividir(2);
    }

    public Dupla Tercio() {
        return Clon().Dividir(3);
    }

    public Dupla Cuarto() {
        return Clon().Dividir(4);
    }

    public Dupla PuntoMedio(Dupla punto) {
        return new Dupla((X + punto.X) / 2, (Y + punto.Y) / 2);
    }

    public Dupla Convertir_Polar() {
        return new Dupla(Radio(), θ());
    }

    public Dupla ProyectarOrtogonalmenteEn(Object u) {
        Dupla dupla = new Dupla(u);
        return dupla.Escalar(ProductoPunto(dupla) / dupla.Magnitud2());
    }

    public Dupla ProyectarOrtogonalmenteEn(Object a, Object b) {
        Dupla u = new Dupla(b).Sustraer(a);
        Dupla v = Clon().Sustraer(a);
        return v.ProyectarOrtogonalmenteEn(u).Adicionar(a);
    }

    public Dupla ImprimirConsola(String PreTexto) {
        System.out.println(PreTexto + " " + this);
        return this;
    }

    public void ImprimirConsola() {
        System.out.println(this);
    }

    public void ImprimirÁnguloConsola(Dupla Cola) {
        System.out.println(toDegrees(Ángulo(Cola)) + "°");
    }

    public void ImprimirÁnguloConsola() {
        System.out.println(toDegrees(θ()) + "°");
    }

    @Override
    public String toString() {
        return "(" + Matemática.recortarDecimales(X) + " , " + Matemática.recortarDecimales(Y) + ")";
    }

    public double[] convVector() {
        return new double[]{X, Y};
    }

    public Dimension convDimension() {
        return new Dimension(rndX(), rndY());
    }

    public Point2D convPoint2D() {
        return new Point2D.Double(X, Y);
    }

    public Point convPoint() {
        return new Point(rndX(), rndY());
    }

    public BufferedImage convBufferedImage() {
        return new BufferedImage(
                X < 1 ? 1 : rndX(),
                Y < 1 ? 1 : rndY(),
                BufferedImage.TYPE_INT_ARGB
        );
    }

    public static BufferedImage convBufferedImage(double x, double y) {
        return new Dupla(x, y).convBufferedImage();
    }

    public static BufferedImage convBufferedImage(Object obj) {
        return new Dupla(obj).convBufferedImage();
    }

    public static Dupla PosiciónCursorEnPantalla() {
        return new Dupla(MouseInfo.getPointerInfo().getLocation());
    }

    public static Dupla PosiciónCursorEnComponente(Component c) {
        Point posicionActual = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(posicionActual, c);
        return new Dupla(posicionActual);
    }

    public static Dupla Alinear(Object DimEnvolvente, Object DimMenor, byte AlineaciónH, byte AlineaciónV) {
        Dupla DuplaEnvolvente = new Dupla(DimEnvolvente).Sustraer(new Dupla(DimMenor));
        return new Dupla(
                AlineaciónH == DERECHA ? DuplaEnvolvente.X : AlineaciónH == MEDIO ? DuplaEnvolvente.X / 2 : 0,
                AlineaciónV == ABAJO ? DuplaEnvolvente.Y : AlineaciónV == MEDIO ? DuplaEnvolvente.Y / 2 : 0
        );
    }

    public static Dupla CorteEntre2Rectas(Dupla a, Dupla b, Dupla c, Dupla d) {
        return new Dupla(Matemática.CorteEntre2Rectas(a.X, a.Y, b.X, b.Y, c.X, c.Y, d.X, d.Y));
    }

    public static double CalcularÁngulo(Object vector) {
        return new Dupla(vector).θ();
    }

    public static double CalcularÁngulo(Object Cola, Object Cabeza) {
        return new Dupla(Cabeza).Ángulo(Cola);
    }

    public static interface Curva {

        Dupla XY(double t);

        static Curva RectaVectorial2Puntos(Dupla a, Dupla b) {
            return (t) -> new Dupla((b.X - a.X) * t + a.X, (b.Y - a.Y) * t + a.Y);
        }

        static Curva Circulo() {
            return Circulo(ORIGEN, 1);
        }

        static Curva Circulo(Dupla centro, double radio) {
            return Circulo(centro, radio, radio, 0);
        }

        static Curva Circulo(Dupla centro, double radiox, double radioy) {
            return Circulo(centro, radiox, radioy, 0);
        }

        static Curva Circulo(Dupla centro, double radiox, double radioy, double Δθ) {
            return (t) -> new Dupla(radiox * Math.cos(t + Δθ) + centro.X, radioy * Math.sin(t + Δθ) + centro.Y);
        }

        static Curva Funcion(Función f) {
            return (t) -> new Dupla(t, f.Y(t));
        }

        static Curva Poligono(double v) {
            final double k = 2 * π / v;
            return (t) -> {
                Curva curva = Circulo();
                double Piv = Sgn(t) * floor(abs(t) / k) * k,
                        Pfv = Piv + Sgn(t) * k;
                if (Pfv == Piv) {
                    Pfv = Piv + k;
                }
                return CorteEntre2Rectas(
                        Dupla.ORIGEN, curva.XY(t),
                        curva.XY(Piv), curva.XY(Pfv)
                );
            };
        }
    }

}
