package quizzmaster;

import java.util.ArrayList;
import static java.lang.Math.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class Matemática {

    public static final double pi = 3.14159265358979323846, π = pi;

    public static final double e = 2.718281828459045235360;

    public static final double Phi = (1 + raiz2(5)) / 2, φ = Phi, Φ = Phi;

    public static final byte ÁNGULO_GRADOS = 0;

    public static final byte ÁNGULO_RADIANES = 1;

    public static final byte ÁNGULO_PORCENTUAL = 2;

    public static final byte PROMEDIO_ARITMÉTICO = 0;
    public static final byte PROMEDIO_ARMÓNICO = 1;
    public static final byte PROMEDIO_GEOMÉTRICO = 2;

    public static void main(String[] args) {
        System.out.println(esEntero(1));
        System.out.println(esEntero(1.0001));
        System.out.println(esEntero(Math.PI));
        System.out.println(esEntero(5));
        System.out.println(esEntero(Math.E));
    }

    public static ArrayList<Integer> ObtenerPrimosEntre(int partida, int llegada) {
        Rango rango = new Rango(partida, llegada);
        ArrayList<Integer> ListaDinamicaDePrimos = new ArrayList<>();
        for (int i = rango.IntExtremoInicial(); i <= rango.ExtremoFinal; i += rango.Sentido) {
            if (esPrimo(i)) {
                ListaDinamicaDePrimos.add(i);
            }
        }
        return ListaDinamicaDePrimos;
    }

    public static boolean esEntero(double Número) {
        return Número == (int) Número;
    }

    public static boolean tieneParteDecimal(double Número) {
        return !esEntero(Número);
    }

    public static boolean esNatural(double Número) {
        return esEntero(Número) && esPositivo(Número);
    }

    public static boolean esNegativo(double Número) {
        return Número <= 0;
    }

    public static boolean esPositivo(double Número) {
        return Número >= 0;
    }

    public static boolean esImpar(double Número) {
        return Número % 2 != 0;
    }

    public static boolean esPar(double Número) {
        return EsMultiploDe(2, Número);
    }

    public static boolean EsDivisorDe(double a, double b) {
        return b % a == 0;
    }

    public static boolean EsMultiploDe(double a, double b) {
        return a % b == 0;
    }

    public static boolean esPrimo(double Número) {
        if (Número <= 1 || !esEntero(Número)) {
            return false;
        }
        int contador = 2;
        while (contador < Número / 2) {
            if (Número % contador == 0) {
                return false;
            }
            contador += contador >= 3 ? 2 : 1;
        }
        return true;
    }

    public static double fracción(double num, double den) {
        return num / den;
    }

    public static double frac(double num, double den) {
        return num / den;
    }

    public static int[] FACT(int n) {
        return Factorizar(n);
    }

    public static int[] Factorizar(int n) {
        if (!esEntero(n) || n < 2) {
            return new int[]{};
        }
        ArrayList<Integer> FACT = new ArrayList<>();
        {
            int k = n;
            for (int i = 2; i <= k; i += i <= 2 ? 1 : 2) {
                if (EsDivisorDe(i, k)) {
                    FACT.add(i);
                    k /= i;
                    i = 1;
                }
            }
        }
        int[] retorno = new int[FACT.size()];
        {
            int c = 0;
            for (int i : FACT) {
                retorno[c++] = i;
            }
        }
        return retorno;
    }

    public static double AleatorioEntre(double vi, double vf) {
        if (vi == vf) {
            return vi;
        }
        return (vf - vi) * random() + vi;
    }

    public static int AleatorioEnteroEntre(double vi, double vf) {
        int min = (int) min(vi, vf);
        int max = (int) max(vi, vf);
        if (max == 1 && min == 0) {
            return (int) round(Math.random());
        }
        return (int) round(AleatorioEntre(min - .5, max + .5));
    }

    public static double Γ(double z) {
        if (z < 0.5) {
            return π / (sen(π * z) * Γ(1 - z));
        }
        z--;
        double C[] = {
            676.5203681218851, -1259.1392167224028, 771.32342877765313, -176.61502916214059,
            12.507343278686905, -0.13857109526572012, 9.9843695780195716e-6, 1.5056327351493116e-7
        };
        int C_ejemplares = C.length;
        double Ag = 0.99999999999980993, t = z + (C_ejemplares - 1) + 0.5;
        for (int k = 0; k < C_ejemplares; k++) {
            Ag += C[k] / (z + k + 1);
        }
        return raiz2(2 * π) * pow(t, z + 0.5) * pow(e, -t) * Ag;
    }

    public static double Gamma(double n) {
        return Γ(n);
    }

    public static double Gamma_DistribuciónDeProbabilidad(double α, double ß, double X) {
        Función GammaDist = (double x) -> fracción(pow(x, α - 1) * pow(e, -x / ß), pow(ß, α) * Γ(α));
        return X <= 0 ? 0 : GammaDist.Apróx_Integral(0, X);
    }

    public static double Gamma_DistribuciónDeProbabilidad(double α, double ß, double Xi, double Xf) {
        return Gamma_DistribuciónDeProbabilidad(α, ß, Xf) - Gamma_DistribuciónDeProbabilidad(α, ß, Xi);
    }

    public static long Factorial(long n) {
        if (n == 0) {
            return 1;
        } else if (n < 0) {
            long k = abs(n);
            return -(k * Factorial(k - 1));
        } else {
            return n * Factorial(n - 1);
        }
    }

    public static double Factorial(double n) {
        if (n <= 12 && n >= -12 && esEntero(n)) {
            return Factorial((long) n);
        }
        if (esPositivo(n)) {
            return Gamma(n + 1);
        }
        return -Gamma(-n + 1);
    }

    public static long Permutación(long n, long k) {
        return (long) (Factorial(n) / Factorial(n - k));
    }

    public static long P(long n, long k) {
        return Permutación(n, k);
    }

    public static double Permutación(double n, double k) {
        return Factorial(n) / Factorial(n - k);
    }

    public static double P(double n, double k) {
        return Permutación(n, k);
    }

    public static double CombinatoriaSinRepetición(double n, double k) {
        return Factorial(n) / (Factorial(k) * Factorial(n - k));
    }

    public static double C(double n, double k) {
        return CombinatoriaSinRepetición(n, k);
    }

    public static long CombinatoriaSinRepetición(long n, long k) {
        return (long) (Factorial(n) / (Factorial(k) * Factorial(n - k)));
    }

    public static long C(long n, long k) {
        return CombinatoriaSinRepetición(n, k);
    }

    public static double CombinatoriaConRepetición(double n, double k) {
        return C(n + k - 1, k);
    }

    public static double CR(double n, double k) {
        return CombinatoriaConRepetición(n, k);
    }

    public static long CombinatoriaConRepetición(long n, long k) {
        return C(n + k - 1, k);
    }

    public static long CR(int n, long k) {
        return CombinatoriaConRepetición(n, k);
    }

    public static double Log(double Número, double Base) {
        return log(Número) / log(Base);
    }

    public static double Ln(double Número) {
        return log(Número);
    }

    public static double raiz4(double Número) {
        return Math.pow(Número, 1 / 4f);
    }

    public static double raiz3(double Número) {
        return Math.pow(Número, 1 / 3f);
    }

    public static double raiz2(double Número) {
        return sqrt(Número);
    }

    public static double raiz(double Número, double Índice) {
        return Math.pow(Número, 1.0 / Índice);
    }

    public static double poten4(double base) {
        return pow(base, 4);
    }

    public static double poten3(double base) {
        return base * base * base;
    }

    public static double poten2(double base) {
        return base * base;
    }

    public static double senh(double x) {
        return sinh(x);
    }

    public static double sech(double x) {
        return 1 / cosh(x);
    }

    public static double csch(double x) {
        return 1 / senh(x);
    }

    public static double coth(double x) {
        return 1 / tanh(x);
    }

    public static double arctanh(double x) {
        return .5 * (Ln(fracción(1 + x, 1 - x)));
    }

    public static double arcsenh(double x) {
        return Ln(x + raiz2(x * x + 1));
    }

    public static double arccosh(double x) {
        return Ln(x + raiz2(x * x - 1));
    }

    public static double cos2(double θ) {
        return poten2(cos(θ));
    }

    public static double cos3(double θ) {
        return poten3(cos(θ));
    }

    public static double cos4(double θ) {
        return poten4(cos(θ));
    }

    public static double sen(double θ) {
        return sin(θ);
    }

    public static double sen2(double θ) {
        return poten2(sen(θ));
    }

    public static double sen3(double θ) {
        return poten3(sen(θ));
    }

    public static double sen4(double θ) {
        return poten4(sen(θ));
    }

    public static double cot(double θ) {
        return 1 / tan(θ);
    }

    public static double csc(double θ) {
        return 1 / sen(θ);
    }

    public static double sec(double θ) {
        return 1 / cos(θ);
    }

    public static double arcsen(double x) {
        return asin(x);
    }

    public static double arccos(double x) {
        return acos(x);
    }

    public static double arctan(double x) {
        return atan(x);
    }

    public static double arccsc(double x) {
        return asin(1 / x);
    }

    public static double arcsec(double x) {
        return π / 2 - asin(1 / x);
    }

    public static double arccot(double x) {
        return π / 2 - atan(x);
    }

    public static double Radianes(double θ, byte TIPO_ÁNGULO) {
        switch (TIPO_ÁNGULO) {
            case ÁNGULO_GRADOS:
                return toRadians(θ);
            case ÁNGULO_PORCENTUAL:
                return θ * 2 * π;
            case ÁNGULO_RADIANES:
            default:
                return θ;
        }
    }

    public static double Grados(double θ, byte TIPO_ÁNGULO) {
        switch (TIPO_ÁNGULO) {
            case ÁNGULO_RADIANES:
                return toDegrees(θ);
            case ÁNGULO_PORCENTUAL:
                return θ * 360;
            case ÁNGULO_GRADOS:
            default:
                return θ;
        }
    }

    public static double ReducciónAngular(double θ) {
        double Rev = 2 * π;
        double p = ParteDecimal(θ / Rev);
        return θ < 0 ? Rev * (p + 1) : p * Rev;
    }

    public static int Sgn(double x) {
        return (int) signum(x);
    }

    public static int ParteEntera(double Número) {
        return (int) Número;
    }

    public static double ParteDecimal(double Número) {
        return abs(Número % 1);
    }

    public static double Truncar(double x, final int n) {
        return Truncar(x, n, false);
    }

    public static double Truncar(double x, final int n, boolean redondear) {
        double a = (int) Math.pow(10, n);
        if (redondear) {
            return Math.round(a * x) / a;
        }
        return ParteEntera(a * x) / a;
    }

    public static double[] ProductoCrúz(double[] U, double[] V) {
        return new double[]{
            U[1] * V[2] - U[2] * V[1],
            U[2] * V[0] - U[0] * V[2],
            U[0] * V[1] - U[1] * V[0]
        };
    }

    public static double ÁreaTriángulo(double[] U, double[] V) {
        return ÁreaParalelogramo(U, V) / 2;
    }

    public static double ÁreaParalelogramo(double[] U, double[] V) {
        return Norma(ProductoCrúz(U, V));
    }

    public static double[] CorteEntre2Rectas(
            double ax, double ay,
            double bx, double by,
            double cx, double cy,
            double dx, double dy
    ) {
        double k1 = (bx - bx);
        double k2 = (by - by);
        double w = -fracción(
                k1 * (cy - ay) - k2 * (cx - ax),
                k1 * (dy - cy) - k2 * (dx - cx)
        );
        double x = (dx - cx) * w + cx;
        double y = (dy - cy) * w + cy;
        return new double[]{x, y};
    }

    public static double[] MultiplicarVectorMatriz(double[] Vector, double[][] Matriz) {
        if (Vector.length != Matriz.length) {
            throw new Error("Longitudes diferentes, V: " + Vector.length + ", M: " + Matriz.length);
        }
        double[] retorno = new double[Matriz[0].length];
        for (int c = 0; c < Matriz[0].length; c++) {
            for (int f = 0; f < Vector.length; f++) {
                retorno[c] += Vector[f] * Matriz[f][c];
            }
        }
        return retorno;
    }

    public static float[] MultiplicarVectorMatriz(float[] Vector, float[][] Matriz) {
        if (Vector.length != Matriz.length) {
            throw new Error("Longitudes diferentes, V: " + Vector.length + ", M: " + Matriz.length);
        }
        float[] retorno = new float[Matriz[0].length];
        for (int c = 0; c < Matriz[0].length; c++) {
            for (int f = 0; f < Vector.length; f++) {
                retorno[c] += Vector[f] * Matriz[f][c];
            }
        }
        return retorno;
    }

    public static double Norma(double... componentes) {
        double Sumatoria = 0;
        for (double d : componentes) {
            Sumatoria += d * d;
        }
        return Math.sqrt(Sumatoria);
    }

    public static double Pitagoras(double a, double b) {
        return raiz2(poten2(a) + poten2(b));
    }

    public static double Máx(double... TérminosDeComparación) {
        double NúmeroMayor = TérminosDeComparación[0];
        for (int i = 1; i < TérminosDeComparación.length; i++) {
            double Número = TérminosDeComparación[i];
            if (Número > NúmeroMayor) {
                NúmeroMayor = Número;
            }
        }
        return NúmeroMayor;
    }

    public static double Mín(double... TérminosDeComparación) {
        double NúmeroMenor = TérminosDeComparación[0];
        for (int i = 1; i < TérminosDeComparación.length; i++) {
            double Número = TérminosDeComparación[i];
            if (Número < NúmeroMenor) {
                NúmeroMenor = Número;
            }
        }
        return NúmeroMenor;
    }

    public static float Máx(float... TérminosDeComparación) {
        return (float) Máx(ConvArreglo_double(TérminosDeComparación));
    }

    public static int Máx(int... TérminosDeComparación) {
        return (int) Máx(ConvArreglo_double(TérminosDeComparación));
    }

    public static long Máx(long... TérminosDeComparación) {
        return (long) Máx(ConvArreglo_double(TérminosDeComparación));
    }

    public static double Máx(Object TérminosDeComparación) {
        return Máx(ConvArreglo_double(TérminosDeComparación));
    }

    public static float Mín(float... TérminosDeComparación) {
        return (float) Mín(ConvArreglo_double(TérminosDeComparación));
    }

    public static int Mín(int... TérminosDeComparación) {
        return (int) Mín(ConvArreglo_double(TérminosDeComparación));
    }

    public static long Mín(long... TérminosDeComparación) {
        return (long) Mín(ConvArreglo_double(TérminosDeComparación));
    }

    public static double Mín(Object TérminosDeComparación) {
        return Mín(ConvArreglo_double(TérminosDeComparación));
    }

    public static int Sumatoria(int... Números) {
        return (int) Sumatoria(ConvArreglo_double(Números));
    }

    public static long Sumatoria(long... Números) {
        return (long) Sumatoria(ConvArreglo_double(Números));
    }

    public static float Sumatoria(float... Números) {
        return (float) Sumatoria(ConvArreglo_double(Números));
    }

    public static double Sumatoria(Object Números) {
        return Sumatoria(ConvArreglo_double(Números));
    }

    public static double Sumatoria(double... Números) {
        double suma = Números[0];
        for (int i = 1; i < Números.length; i++) {
            suma += Números[i];
        }
        return suma;
    }

    public static double Promedio(Object Datos) {
        return Promedio(ConvArreglo_double(Datos));
    }

    public static double Promedio(double... Datos) {
        return PromedioAritmético(Datos);
    }

    public static double PromedioAritmético(Object Datos) {
        return PromedioAritmético(ConvArreglo_double(Datos));
    }

    public static double PromedioAritmético(double... Datos) {
        return Promedio(PROMEDIO_ARITMÉTICO, Datos);
    }

    public static double PromedioArmónico(Object Datos) {
        return PromedioArmónico(ConvArreglo_double(Datos));
    }

    public static double PromedioArmónico(double... Datos) {
        return Promedio(PROMEDIO_ARMÓNICO, Datos);
    }

    public static double PromedioGeométrico(Object Datos) {
        return PromedioGeométrico(ConvArreglo_double(Datos));
    }

    public static double PromedioGeométrico(double... Datos) {
        return Promedio(PROMEDIO_GEOMÉTRICO, Datos);
    }

    public static double Promedio(byte TIPO_PROMEDIO, Object Datos) {
        return Promedio(TIPO_PROMEDIO, ConvArreglo_double(Datos));
    }

    public static double Promedio(byte TIPO_PROMEDIO, double... Datos) {
        int CantidadDatos = Datos.length;
        switch (TIPO_PROMEDIO) {
            case PROMEDIO_ARITMÉTICO:
                double suma = Datos[0];
                for (int i = 1; i < CantidadDatos; i++) {
                    suma += Datos[i];
                }
                return suma / CantidadDatos;
            case PROMEDIO_ARMÓNICO:
                double sumaReciproca = 1 / Datos[0];
                for (int i = 1; i < CantidadDatos; i++) {
                    sumaReciproca += 1 / Datos[i];
                }
                return CantidadDatos / sumaReciproca;
            case PROMEDIO_GEOMÉTRICO:
                double producto = Datos[0];
                for (int i = 1; i < CantidadDatos; i++) {
                    producto *= Datos[i];
                }
                return Math.pow(producto, 1.0 / CantidadDatos);
            default:
                throw new RuntimeException("No se reconoce el tipo de promedio que se quiere calcular");
        }
    }

    public static double[] ConvArreglo_double(Object vect) {
        if (vect instanceof double[]) {
            double[] nvect = ((double[]) vect);
            return nvect;
        }
        double[] retorno;
        if (vect instanceof float[]) {
            float[] nvect = ((float[]) vect);
            retorno = new double[nvect.length];
            for (int i = 0; i < retorno.length; i++) {
                retorno[i] = nvect[i];
            }
        } else if (vect instanceof int[]) {
            int[] nvect = ((int[]) vect);
            retorno = new double[nvect.length];
            for (int i = 0; i < retorno.length; i++) {
                retorno[i] = nvect[i];
            }
        } else if (vect instanceof long[]) {
            long[] nvect = ((long[]) vect);
            retorno = new double[nvect.length];
            for (int i = 0; i < retorno.length; i++) {
                retorno[i] = nvect[i];
            }
        } else if (vect instanceof byte[]) {
            byte[] nvect = ((byte[]) vect);
            retorno = new double[nvect.length];
            for (int i = 0; i < retorno.length; i++) {
                retorno[i] = nvect[i];
            }
        } else if (vect instanceof short[]) {
            short[] nvect = ((short[]) vect);
            retorno = new double[nvect.length];
            for (int i = 0; i < retorno.length; i++) {
                retorno[i] = nvect[i];
            }
        } else if (vect instanceof Number[]) {
            Number[] nvect = ((Number[]) vect);
            retorno = new double[nvect.length];
            for (int i = 0; i < retorno.length; i++) {
                retorno[i] = nvect[i].doubleValue();
            }
        } else {
            throw new RuntimeException("No se reconoce el dato de entrada");
        }
        return retorno;
    }

    public static String ConvertirArregloACadena(int[] a) {
        return ConvertirArregloACadena(ConvArreglo_double(a));
    }

    public static String CerosIzquierda(long número, long longitud_minima) {
        return String.format("%0" + longitud_minima + "d", número);
    }

    public static String CerosIzquierda(String c, int ceros) {
        if (c.length() >= ceros) {
            return c;
        }
        int d = ceros - c.length();
        String r = c;
        for (int i = 0; i < d; i++) {
            r = "0" + r;
        }
        return r;
    }

    public static String ConvertirArregloACadena(double[] a) {
        System.out.println("s: " + a.length);
        String retorno = "";
        for (int i = 0; i < a.length; i++) {
            retorno += ConvertirDoubleEnString(a[i]);
            if (i != a.length - 1) {
                retorno += ",";
            }
        }
        return retorno;
    }

    static String ConvertirDoubleEnString(double d) {
        if (d == Double.POSITIVE_INFINITY) {
            return "∞";
        } else if (Double.isNaN(d)) {
            return "×";
        } else if (d == (int) d) {
            return (int) d + "";
        }
        return d + "";
    }

    public static String EliminarNotaciónCientifica(final double Número) {
        String d = "####################################";
        return new DecimalFormat("#." + d + d + d).format(Número);
    }

    public static String recortarDecimales(final double Número) {
        return recortarDecimales(Número, 4);
    }

    public static String recortarDecimales(final double Número, final int DecimalesMáximos) {
        if (Número == (int) Número) {
            return (int) Número + "";
        }
        String retorno = Truncar(Número, DecimalesMáximos, true) + "";
        return retorno.endsWith(".0") ? retorno.replace(".0", "") : retorno;
    }

    public static String ConvertirSegundosATiempo(double segundos, int númeroDecimales) {
        int horas = (int) (segundos / 60 / 60);
        int minutos = (int) (segundos / 60) % 60;
        segundos %= 60;
        return (horas < 10 ? "0" : "") + horas
                + ":" + (minutos < 10 ? "0" : "") + minutos
                + ":" + (segundos < 10 ? "0" : "")
                + String.format(
                        "%." + númeroDecimales + "f", segundos
                ).replace(",", ".");
    }

    public interface Función {

        public double Y(double x);

        public default double Apróx_Integral(final double a, final double b) {
            int iteraciones = 120;
            double ΔX = (b - a) / iteraciones;
            double Sumatoria = ((Sucesión) (int n) -> (esPar(n) ? 2 : 4) * Y(a + ΔX * n)).Sumatoria(iteraciones - 1);
            return (ΔX / 3) * (Y(a) + Sumatoria + Y(b));
        }

        public default double Apróx_Derivada(double a1) {
            try {
                final BigDecimal Δx;
                final double absa = abs(a1);
                if (absa > 1000000000) {
                    Δx = new BigDecimal("0.00001");
                } else if (absa > 500000000) {
                    Δx = new BigDecimal("0.000001");
                } else if (absa > 20000000) {
                    Δx = new BigDecimal("0.0000001");
                } else if (absa > 7000000) {
                    Δx = new BigDecimal("0.00000001");
                } else if (absa > 1000000) {
                    Δx = new BigDecimal("0.000000001");
                } else if (absa > 10000) {
                    Δx = new BigDecimal("0.0000000001");
                } else {
                    Δx = new BigDecimal("0.00000000001");
                }
                final BigDecimal a = new BigDecimal(a1);
                final BigDecimal x_Δx = new BigDecimal(a1).add(Δx);
                final BigDecimal x1 = new BigDecimal(Y(x_Δx.doubleValue()) + "");
                final BigDecimal x2 = new BigDecimal(Y(a.doubleValue()));
                return new BigDecimal(x1 + "").subtract(x2).divide(Δx).doubleValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
            throw new RuntimeException("No se pudo calcular la derivada");
        }
    }

    public static interface FunciónPolar {

        public double r(double θ);

        public static FunciónPolar Polar_Cardioide() {
            return (double θ) -> 1 - cos(θ);
        }

        public static FunciónPolar Polar_Corazón() {
            return (double θ) -> sen(θ) * fracción(raiz2(abs(cos(θ))), sen(θ) + 7f / 5) - 2 * sen(θ) + 2;
        }

        public static FunciónPolar Polar_Corazón_Rizado() {
            return (double θ) -> 1.2 - fracción(sen(θ), 2 * raiz2(.2 + abs(cos(θ)))) + (sen(θ) + cos(2 * θ)) / 5 + sen(70 * θ) * 3 / 50.0;
        }

        public static FunciónPolar Polar_Mitad_Flor(float NúmeroDePetalos) {
            return (double θ) -> cos(θ * NúmeroDePetalos / 2);
        }

        public static FunciónPolar Polar_Flor(float NúmeroDePetalos) {
            return (double θ) -> cos(θ * NúmeroDePetalos);
        }

        public static FunciónPolar Polar_BrazosRadiales(double brazos, double anchuraDeBrazos, double estiramiento) {
            double c = 1 + (1 / estiramiento);
            return (double θ) -> anchuraDeBrazos / (c - sen(brazos * θ));
        }

    }

    public static interface Sucesión {

        public double n_ésimo_Termino(final int n);

        default public double Sumatoria(int llegada) {
            return Sumatoria(1, llegada);
        }

        default public double Sumatoria(int partida, int llegada) {
            Rango r = new Rango(partida, llegada);
            double Suma = 0;
            for (int n = r.IntExtremoInicial(); n <= r.ExtremoFinal(); n += r.Sentido) {
                Suma += n_ésimo_Termino(n);
            }
            return Suma;
        }

        public static Sucesión Sucesión_Fibonacci() {
            return (int n) -> fracción(pow(φ, n) - pow(-φ, -n), n);
        }

        public static Sucesión Sucesión_Seno(final double θ) {
            return (int n) -> (pow(-1, n) * pow(θ, 2 * n + 1)) / Factorial(2 * n + 1);
        }

        public static Sucesión Sucesión_Coseno(final double θ) {
            return (int n) -> (pow(-1, n) * pow(θ, 2 * n)) / Factorial(2 * n);
        }

        public static Sucesión Sucesión_SenoHiperbolico(final double X) {
            return (int n) -> (pow(X, 2 * n + 1)) / Factorial(2 * n + 1);
        }

        public static Sucesión Sucesión_CosenoHiperbolico(final double X) {
            return (int n) -> (pow(X, 2 * n)) / Factorial(2 * n);
        }
    }

    public static final class Rango {

        public final static int POR_DETRAS = -1;
        public final static int POR_DENTRO = 0;
        public final static int POR_DELANTE = 1;

        public static final byte AJUSTE_A_EXTREMOS = 0;
        public static final byte AJUSTE_A_EXTREMOS_ABS = 1;
        public static final byte AJUSTE_CIRCULAR = 2;
        public static final byte AJUSTE_CIRCULAR_ABS = 3;
        public static final byte AJUSTE_REFLEJO = 4;
        public static final byte AJUSTE_REFLEJO_INV = 5;

        private double ExtremoInicial;
        private double ExtremoFinal;
        private double Magnitud;
        private int Sentido;

        public Rango(double ExtremoInicial, double ExtremoFinal) {
            ModificarExtremos(ExtremoInicial, ExtremoFinal);
        }

        public Rango(double ExtremoFinal) {
            ModificarExtremos(0, ExtremoFinal);
        }

        public double ExtremoInicial() {
            return ExtremoInicial;
        }

        public int IntExtremoInicial() {
            return (int) ExtremoInicial;
        }

        public float floatExtremoInicial() {
            return (float) ExtremoInicial;
        }

        public double ExtremoFinal() {
            return ExtremoFinal;
        }

        public int IntExtremoFinal() {
            return (int) ExtremoFinal;
        }

        public float floatExtremoFinal() {
            return (float) ExtremoFinal;
        }

        public double ObtenerExtremoMayor() {
            return Máx(ExtremoInicial, ExtremoFinal);
        }

        public double ObtenerExtremoMenor() {
            return Mín(ExtremoInicial, ExtremoFinal);
        }

        public double LongitudDelRango() {
            return Magnitud;
        }

        public int ObtenerSentido() {
            return Sentido;
        }

        public void ModificarExtremos(double NuevoExtremoInicial, double NuevoExtremoFinal) {
            this.ExtremoInicial = NuevoExtremoInicial;
            this.ExtremoFinal = NuevoExtremoFinal;
            Magnitud = NuevoExtremoFinal - NuevoExtremoInicial;
            Sentido = Sgn(Magnitud);
        }

        public void ModificarExtremoInicial(double NuevoExtremoInicial) {
            this.ExtremoInicial = NuevoExtremoInicial;
            Magnitud = ExtremoFinal - NuevoExtremoInicial;
            Sentido = Sgn(Magnitud);
        }

        public void ModificarExtremoFinal(double NuevoExtremoFinal) {
            this.ExtremoFinal = NuevoExtremoFinal;
            Magnitud = NuevoExtremoFinal - ExtremoInicial;
            Sentido = Sgn(Magnitud);
        }

        public void ModificarSentido() {
            ModificarExtremos(ExtremoFinal, ExtremoInicial);
        }

        public void ModificarLongitud(double Lr) {
            this.Magnitud = Lr;
            ExtremoFinal = ExtremoInicial + Sentido * Lr;
            Sentido = Lr <= 0 ? Sgn(Lr) : Sentido;
        }

        public int Valoración(double n) {
            return Valoración(n, false);
        }

        public int Valoración(double n, boolean Incluyente) {
            if (n == ExtremoFinal && Incluyente) {
                return POR_DENTRO;
            }
            if (ExtremoInicial < ExtremoFinal) {
                return n < ExtremoInicial ? POR_DETRAS : n >= ExtremoFinal ? POR_DELANTE : POR_DENTRO;
            } else {
                return n > ExtremoInicial ? POR_DETRAS : n <= ExtremoFinal ? POR_DELANTE : POR_DENTRO;
            }
        }

        public double Ubicar_ValorPorcentual(double t) {
            if (ExtremoInicial == 0) {
                return ExtremoFinal * t;
            }
            return (ExtremoFinal - ExtremoInicial) * t + ExtremoInicial;
        }

        public double porcentuality(double n) {
            return (n - ExtremoInicial) / (ExtremoFinal - ExtremoInicial);
        }

        public double porcentuality_reduced(double n) {
            return ValorPorcentualReducido(n, false);
        }

        public double ValorPorcentualReducido(double n, boolean incluirExtremoFinal) {
            if (n == ExtremoInicial) {
                return 0;
            }
            if (n == ExtremoFinal) {
                return incluirExtremoFinal ? 1 : 0;
            }
            double Vp = porcentuality(n);
            double Vpr = Vp % 1;
            double Retorno = n > 0 ? Vpr : 1 + Vpr;
            if (incluirExtremoFinal && Retorno == 0 && Vp > 0) {
                Retorno = 1;
            }
            return Retorno;
        }

        public double PuntoOpuesto(double n) {
            return ExtremoFinal + ExtremoInicial - n;
        }

        public double Discretizar(double V, int N) {
            if (N > 1) {
                if (N == 2) {
                    if (V < ExtremoInicial + Sentido * Magnitud / 2) {
                        return ExtremoInicial;
                    }
                    return ExtremoFinal;
                }
                final double ΔN = N - 1;
                double Vpr = ValorPorcentualReducido(V, true);
                double t = round(ΔN * Vpr) / ΔN;
                return Ubicar_ValorPorcentual(t);
            }
            return ExtremoFinal;
        }

        public double AjusteAlRango(double n, byte TIPO_AJUSTE) {
            switch (TIPO_AJUSTE) {
                case AJUSTE_A_EXTREMOS_ABS:
                    return AjusteAExtremosAbs(n);
                case AJUSTE_CIRCULAR:
                    return AjusteCircular(n);
                case AJUSTE_CIRCULAR_ABS:
                    return AjusteCircularAbs(n);
                case AJUSTE_REFLEJO:
                    return AjusteReflejo(n);
                case AJUSTE_REFLEJO_INV:
                    return AjusteReflejoInv(n);
                case AJUSTE_A_EXTREMOS:
                default:
                    return AjusteAExtremos(n);
            }
        }

        public float AjusteReflejo(float n) {
            return (float) AjusteReflejo((double) n);
        }

        public int AjusteReflejo(int n) {
            return (int) AjusteReflejo((double) n);
        }

        public double AjusteReflejo(double n) {
            double Vp = porcentuality(n);
            if (floor(Vp) % 2 == 0 || porcentuality_reduced(n) == 0) {
                return AjusteCircular(n);
            } else {
                return PuntoOpuesto(AjusteCircular(n));
            }
        }

        public float AjusteReflejoInv(float n) {
            return (float) AjusteReflejoInv((double) n);
        }

        public int AjusteReflejoInv(int n) {
            return (int) AjusteReflejoInv((double) n);
        }

        public double AjusteReflejoInv(double n) {
            double Vp = porcentuality(n);
            if (floor(Vp) % 2 == 0 || porcentuality_reduced(n) == 0) {
                return PuntoOpuesto(AjusteCircular(n));
            } else {
                return AjusteCircular(n);
            }
        }

        public float AjusteCircular(float n) {
            return (float) AjusteCircular((double) n);
        }

        public int AjusteCircular(int n) {
            return (int) AjusteCircular((double) n);
        }

        public double AjusteCircular(double n) {
            if (Valoración(n) == POR_DENTRO) {
                return n;
            }
            return porcentuality_reduced(n) * Magnitud + ExtremoInicial;
        }

        public float AjusteCircularAbs(float n) {
            return AjusteCircular(abs(n));
        }

        public int AjusteCircularAbs(int n) {
            return AjusteCircular(abs(n));
        }

        public double AjusteCircularAbs(double n) {
            return AjusteCircular(abs(n));
        }

        public float AjusteAExtremos(float n) {
            return (float) AjusteAExtremos((double) n);
        }

        public int AjusteAExtremos(int n) {
            return (int) AjusteAExtremos((double) n);
        }

        public double AjusteAExtremos(double n) {
            return n > ExtremoFinal ? ExtremoFinal : n < ExtremoInicial ? ExtremoInicial : n;
        }

        public float AjusteAExtremosAbs(float n) {
            return Rango.this.AjusteAExtremos(abs(n));
        }

        public int AjusteAExtremosAbs(int n) {
            return Rango.this.AjusteAExtremos(abs(n));
        }

        public double AjusteAExtremosAbs(double n) {
            return AjusteAExtremos(abs(n));
        }

        public boolean Por_Debajo(double núm, boolean IncluirExtremoFinal) {
            return Valoración(núm, IncluirExtremoFinal) == POR_DETRAS;
        }

        public boolean Por_Debajo(double núm) {
            return Por_Debajo(núm, false);
        }

        public boolean Por_Dentro(double núm, boolean IncluirExtremoFinal) {
            return Valoración(núm, IncluirExtremoFinal) == POR_DENTRO;
        }

        public boolean Por_Dentro(double núm) {
            return Por_Dentro(núm, false);
        }

        public boolean Por_Fuera(double núm, boolean IncluirExtremoFinal) {
            return !Por_Dentro(núm, IncluirExtremoFinal);
        }

        public boolean Por_Fuera(double núm) {
            return Por_Fuera(núm, false);
        }

        public boolean Por_Encima(double núm, boolean IncluirExtremoFinal) {
            return Valoración(núm, IncluirExtremoFinal) == POR_DELANTE;
        }

        public boolean Por_Encima(double núm) {
            return Por_Encima(núm, false);
        }

        @Override
        public String toString() {
            return "(" + "Ei: " + Truncar(ExtremoInicial, 4, true)
                    + ", Ef: " + Truncar(ExtremoFinal, 4, true)
                    + ", L: " + Truncar(Magnitud, 4, true)
                    + ", S: " + Sentido + ')';
        }
    }

    public static class Rango2D {

        public Rango rangoX;
        public Rango rangoY;

        public double[] Posición() {
            return new double[]{rangoX.ExtremoInicial, rangoY.ExtremoInicial};
        }

        public double[] Dimensión() {
            return new double[]{rangoX.ExtremoFinal - rangoX.ExtremoInicial, rangoY.ExtremoFinal - rangoY.ExtremoInicial};
        }

        public Rango2D(Rango X, Rango Y) {
            rangoX = X;
            rangoY = Y;
        }

        public Rango2D(double Xf, double Yf) {
            rangoX = new Rango(0, Xf);
            rangoY = new Rango(0, Yf);
        }

        public Rango2D(double Xi, double Xf, double Yi, double Yf) {
            rangoX = new Rango(Xi, Xf);
            rangoY = new Rango(Yi, Yf);
        }

        public boolean estáDentro(double... punto2D) {
            return estáDentro(punto2D[0], punto2D[1]);
        }

        public boolean estáDentro(double x, double y) {
            return rangoX.Por_Dentro(x, true) && rangoY.Por_Dentro(y, true);
        }

        public double[] Ajustación_Dona(double x, double y) {
            return new double[]{rangoX.AjusteCircular(x), rangoY.AjusteCircular(y)};
        }
    }
}
