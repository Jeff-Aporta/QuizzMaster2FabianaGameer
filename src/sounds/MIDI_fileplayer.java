package sounds;

import Tools.system;
import javax.sound.midi.*;
import java.io.*;
import java.net.URL;

public final class MIDI_fileplayer {

    public Sequencer InterpretadorMidi;

    public static void main(String args[]) throws Exception {

        MIDI_fileplayer ReproductorMidi = new MIDI_fileplayer(
                "https://www.vgmusic.com/music/console/nintendo/nes/Troika.mid"
        );
        ReproductorMidi.Reproducir();
        ReproductorMidi.RepetirEnBucle_Indefinidamente();
        ReproductorMidi.InterpretadorMidi.setTrackMute(1, true);
        while (ReproductorMidi.EstaReproduciendo()) {
            Thread.sleep(1000);
        }
        ReproductorMidi.Cerrar();
    }

    public MIDI_fileplayer() {
        this(null);
    }

    public MIDI_fileplayer(Object o) {
        try {
            Asignar_Midi(o);
        } catch (Exception ex) {
        }
    }

    public void CambiarVelocidadReproducción(float f) {
        InterpretadorMidi.setTempoFactor(f);
    }

    public void Asignar_Midi(Object o) throws Exception {
        if (InterpretadorMidi == null) {
            InterpretadorMidi = MidiSystem.getSequencer();
        }
        if (o == null) {
            return;
        }
        boolean reproduciendo = EstaReproduciendo();
        if (TieneCargadoUnArchivo()) {
            Pausar();
            Reiniciar();
        }
        InterpretadorMidi.setSequence(Generar_Sequence(o));
        InterpretadorMidi.open();
        if (reproduciendo) {
            Reproducir();
        }
    }

    public static Sequence Generar_Sequence(Object o) throws Exception {
        if (o instanceof File) {
            File f = (File) o;
            return MidiSystem.getSequence(f);
        }
        if (o instanceof URL) {
            URL url = (URL) o;
            return MidiSystem.getSequence(url);
        }
        if (o instanceof InputStream) {
            InputStream inputStream = (InputStream) o;
            return MidiSystem.getSequence(inputStream);
        }
        if (o instanceof String) {
            String string = (String) o;
            File f = new File(string);
            if (f.exists()) {
                return MidiSystem.getSequence(f);
            } else if (system.Verificar_Existencia_URL(string)) {
                URL url = new URL(string);
                return MidiSystem.getSequence(url);
            }
        }
        return null;
    }

    public boolean EstaReproduciendo() {
        return InterpretadorMidi.isRunning();
    }

    public boolean TieneCargadoUnArchivo() {
        return InterpretadorMidi != null && InterpretadorMidi.isOpen();
    }

    public long ObtenerDuración_MicroSegundos() {
        return InterpretadorMidi.getMicrosecondLength();
    }

    public float ObtenerDuración_Segundos() {
        return ObtenerDuración_MicroSegundos() / 1000000f;
    }

    public long ObtenerUbicación_MicroSegundos() {
        return InterpretadorMidi.getMicrosecondPosition();
    }

    public float ObtenerUbicación_Segundos() {
        return ObtenerUbicación_MicroSegundos() / 1000000f;
    }

    public float ObtenerUbicación_Porcentaje() {
        return ObtenerUbicación_Segundos() / ObtenerDuración_Segundos();
    }

    public MIDI_fileplayer RepetirEnBucle_Indefinidamente() {
        return RepetirEnBucle(-1);
    }

    public MIDI_fileplayer RepetirEnBucle(int númeroDeRepeticiones) {
        InterpretadorMidi.setLoopCount(númeroDeRepeticiones);
        return this;
    }

    public MIDI_fileplayer ModificarUbicación_Porcentaje(float p) {
        if (p > 1) {
            throw new RuntimeException("Se ha excedido la duración del audio");
        }
        InterpretadorMidi.setMicrosecondPosition((long) (InterpretadorMidi.getMicrosecondLength() * p));
        return this;
    }

    public MIDI_fileplayer ModificarUbicación_Segundos(float s) {
        ModificarUbicación_Porcentaje(s / ObtenerDuración_Segundos());
        return this;
    }

    public MIDI_fileplayer Reiniciar() {
        InterpretadorMidi.setMicrosecondPosition(0);
        return this;
    }

    public MIDI_fileplayer Pausar() {
        if (InterpretadorMidi.isRunning()) {
            InterpretadorMidi.stop();
        }
        return this;
    }

    public MIDI_fileplayer Reproducir() {
        if (!InterpretadorMidi.isRunning()) {
            InterpretadorMidi.start();
        }
        return this;
    }

    public MIDI_fileplayer Cerrar() {
        Pausar();
        InterpretadorMidi.close();
        return this;
    }

    public String CadenaTiempoDuración() {
        return ConvertirSegundosACadena(ObtenerDuración_Segundos());
    }

    public String tiempoUbicación() {
        return ConvertirSegundosACadena(ObtenerUbicación_Segundos());
    }

    public static String ConvertirSegundosACadena(double segundos) {
        double S = segundos % 60;
        int M = (int) ((segundos / 60) % 60);
        int H = (int) (segundos / 60 / 60);
        String SH = (H < 10 ? "0" : "") + H;
        String SM = (M < 10 ? "0" : "") + M;
        String SS = (S < 10 ? "0" : "") + String.format("%.1f", S).replace(",", ".");
        return SH + ":" + SM + ":" + SS;
    }
}
