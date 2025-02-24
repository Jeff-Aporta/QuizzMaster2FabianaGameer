package Tools;

import math.Matematica;

public class chronometer {

    public static final byte UNIDAD_MEDIDA_NANOSEGUNDOS = 0;
    public static final byte UNIDAD_MEDIDA_MILISEGUNDOS = 1;

    private byte UNIDAD_MEDIDA;

    long TiempoReferencia = -1;
    long Acumulado = 0;

    public static void main(String[] args) throws Exception {
        chronometer cronometro = new chronometer(UNIDAD_MEDIDA_NANOSEGUNDOS);
        cronometro.Iniciar();
        for (int i = 0; i < 1000000; i++) {
            System.out.println(i);
        }
        System.out.println(cronometro.SegundosTranscurridos());
    }

    public chronometer() {
        this(UNIDAD_MEDIDA_MILISEGUNDOS);
    }

    public chronometer(byte UNIDAD_MEDIDA) {
        switch (UNIDAD_MEDIDA) {
            case UNIDAD_MEDIDA_MILISEGUNDOS:
            case UNIDAD_MEDIDA_NANOSEGUNDOS:
                this.UNIDAD_MEDIDA = UNIDAD_MEDIDA;
                break;
            default:
                throw new RuntimeException("No se reconoce la unidad de medida");
        }
    }

    public byte UNIDAD_MEDIDA() {
        return UNIDAD_MEDIDA;
    }

    public void Modificar_UNIDAD_MEDIDA(byte UnidadMedida) {
        if (TiempoReferencia != -1 && Acumulado != 0) {
            return;
        }
        UNIDAD_MEDIDA = UnidadMedida;
    }

    public void CambiarTiempo(long nuevoTiempo) {
        Acumulado = nuevoTiempo;
        if (!isOnPause()) {
            CalcularTiempoReferencia_Actual();
        }
    }

    public void Reestablecer() {
        TiempoReferencia = -1;
        Acumulado = 0;
    }

    public void Reiniciar() {
        CalcularTiempoReferencia_Actual();
        Acumulado = 0;
    }

    public chronometer Iniciar() {
        if (!isOnPause()) {
            return this;
        }
        Reiniciar();
        return this;
    }

    public void Pausar() {
        if (isOnPause()) {
            return;
        }
        Acumulado = TiempoTranscurrido();
        TiempoReferencia = -1;
    }

    public void resume() {
        if (!isOnPause()) {
            return;
        }
        CalcularTiempoReferencia_Actual();
    }

    public long TiempoTranscurrido() {
        if (isOnPause()) {
            return Acumulado;
        }
        switch (UNIDAD_MEDIDA) {
            case UNIDAD_MEDIDA_MILISEGUNDOS:
                return System.currentTimeMillis() - TiempoReferencia + Acumulado;
            case UNIDAD_MEDIDA_NANOSEGUNDOS:
                return System.nanoTime() - TiempoReferencia + Acumulado;
            default:
                throw new RuntimeException("No se reconoce la unidad de medida");
        }
    }

    public boolean isOnPause() {
        return TiempoReferencia == -1;
    }

    public double SegundosTranscurridos() {
        return TiempoTranscurrido() / UnidadesPorSegundo();
    }

    private void CalcularTiempoReferencia_Actual() {
        switch (UNIDAD_MEDIDA) {
            case UNIDAD_MEDIDA_MILISEGUNDOS:
                TiempoReferencia = System.currentTimeMillis();
                break;
            case UNIDAD_MEDIDA_NANOSEGUNDOS:
                TiempoReferencia = System.nanoTime();
                break;
        }
    }

    private double UnidadesPorSegundo() {
        switch (UNIDAD_MEDIDA) {
            case UNIDAD_MEDIDA_MILISEGUNDOS:
                return 1000.0;
            case UNIDAD_MEDIDA_NANOSEGUNDOS:
                return 1000000000.0;
            default:
                throw new RuntimeException("No se reconoce la unidad de medida");
        }
    }

    @Override
    public String toString() {
        return convertir_HHMMSS_3_9_decimales(TiempoTranscurrido(), UNIDAD_MEDIDA);
    }

    public String Cadena_SegundosEnteros() {
        return convertir_HHMMSS_entero(TiempoTranscurrido());
    }

    public static String convertir_HHMMSS(int segundos) {
        return convertir_HHMMSS_entero(segundos * 1000);
    }

    public static String convertir_HHMMSS_entero(long tiempo) {
        return convertir_HHMMSS_decimales_adaptables(tiempo, UNIDAD_MEDIDA_MILISEGUNDOS, 0);
    }

    public static String convertir_HHMMSS_3_9_decimales(long tiempo, byte UnidadMedida) {
        return chronometer.convertir_HHMMSS_decimales_adaptables(
                tiempo,
                UnidadMedida,
                (UnidadMedida == UNIDAD_MEDIDA_MILISEGUNDOS ? 3 : 9)
        );
    }

    public static String convertir_HHMMSS_decimales_adaptables(long tiempo, byte UnidadMedida, int decimales) {
        double factorConversión;
        switch (UnidadMedida) {
            case UNIDAD_MEDIDA_MILISEGUNDOS:
                factorConversión = 1 / 1000.0;
                break;
            case UNIDAD_MEDIDA_NANOSEGUNDOS:
                factorConversión = 1 / 1000000000.0;
                break;
            default:
                throw new RuntimeException("No se reconoce la unidad de medida");
        }
        double segundos = (tiempo * factorConversión);
        if (decimales == 0) {
            segundos = (int) segundos;
        }
        int horas = (int) (segundos / 60 / 60);
        int minutos = (int) (segundos / 60) % 60;
        segundos %= 60;
        return Matematica.CerosIzquierda(horas, 2)
                + ":" + Matematica.CerosIzquierda(minutos, 2)
                + ":" + (segundos < 10 ? "0" : "")
                + String.format(
                        "%." + decimales + "f", segundos
                ).replace(",", ".");
    }

}
