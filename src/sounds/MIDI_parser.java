package sounds;

import Tools.chronometer;
import Tools.callback.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import javax.sound.midi.*;

public class MIDI_parser {

    public static final int MIDIEVENT_NOTE_ON = 0x90;
    public static final int MIDIEVENT_NOTE_OFF = 0x80;
    public static final int MIDIEVENT_CHANGE_INSTRUMENT = 0xc0;

    public int Duración_ms = -1;

    private float velocidadReproducción = 1;
    public float volume = 1;

    public Canales channels;
    public boolean loop = false;

    public chronometer controlTiempo = new chronometer();

    public Play_notes playerNotes = new Play_notes();

    int[] positionNotalChannel;

    float fade = 0;

    Simple eventPlayEnd;
    Simple_Strings EventoCargaArchivo;
    Simple_Longs EventoCargaSecuenciaLong;
    EventoNota eventNote;

    public static HashMap<String, MIDI_parser> midis_pararells = new HashMap<>();

    public static void main(String[] args) throws Exception {
        MIDI_parser lectorMIDI = new MIDI_parser();
        lectorMIDI.load_secuenceLong(Libsounds.effects.SecuenciasLong.Megaman.SuperArm());
        lectorMIDI.CambiarInstrumento_TodosLosCanales(0, 10);
        lectorMIDI.start_playing();
        System.out.println(SecuenciaLong.Valor(0, 0, 11));
        SecuenciaLong.Volumen = 2;
    }

    public MIDI_parser() {
    }

    public MIDI_parser fade_value(float d) {
        fade = d;
        return this;
    }

    public MIDI_parser setVolume(float v) {
        volume = v;
        return this;
    }

    public static MIDI_parser midi_pararell(String id) {
        return midi_pararell(id, null);
    }

    public static MIDI_parser midi_pararell(String id, long[] secuence) {
        return MIDI_parser.midi_pararell(id, secuence, -1, -1, -1);
    }

    public static MIDI_parser midi_pararell(String id, long[] secuence, float volume, float velocity, int instrument) {
        if (midis_pararells.containsKey(id) || secuence == null) {
            return midis_pararells.get(id);
        }
        MIDI_parser playback_midi = new MIDI_parser();
        playback_midi.load_secuenceLong(secuence);
        midis_pararells.put(id, playback_midi);
        return playback_midi;
    }

    public MIDI_parser(Object o) throws Exception {
        CargarArchivo(o);
    }

    public boolean getLoop() {
        return loop;
    }

    public MIDI_parser setLoop(boolean l) {
        loop = l;
        return this;
    }

    public void playback_speed(float FactorVelocidad) {
        velocidadReproducción = FactorVelocidad;
        SuspenderTodasLasNotas();
        ReposicionarNotas((int) getPlayTime());
    }

    public void CambiarVolumenCanal(int canal, float volúmen) {
        channels.get(canal).volúmen = volúmen;
    }

    public void CambiarInstrumento(int canal, int banco, int programa) {
        channels.get(canal).banco = banco;
        channels.get(canal).programa = programa;
    }

    public void changeInstrument_allChannels(int llave) {
        CambiarInstrumento_TodosLosCanales((llave >>> 16) & 0xffff, llave & 0xffff);
    }

    public void CambiarInstrumento_TodosLosCanales(int banco, int programa) {
        for (int canal = 0; canal < channels.size(); canal++) {
            CambiarInstrumento(canal, banco, programa);
        }
    }

    public void load_secuenceLong(long[] l) {
        SuspenderTodasLasNotas();
        for (long n : l) {
            if (SecuenciaLong.esDuración(n)) {
                Duración_ms = SecuenciaLong.ObtenerDuración(n);
                break;
            }
        }
        channels = new Canales(l);
        positionNotalChannel = new int[channels.size()];
        Detener();
        if (EventoCargaSecuenciaLong != null) {
            EventoCargaSecuenciaLong.boolrun(l);
        }
    }

    public void CargarArchivo(Object o) throws Exception {
        try {
            SuspenderTodasLasNotas();

            RenglonesEventos r = GenerarRenglones(o);

            for (RenglónEvento renglón : r) {
                if (renglón.esDuración) {
                    Duración_ms = renglón.Duración;
                    break;
                }
            }
            channels = new Canales(r);
            positionNotalChannel = new int[channels.size()];
            Detener();
            if (EventoCargaArchivo != null) {
                EventoCargaArchivo.Ejecutar(o.toString());
            }
        } catch (Exception e) {
            Duración_ms = -1;
            if (EventoCargaArchivo != null) {
                EventoCargaArchivo.Ejecutar(o.toString());
            }
            throw new RuntimeException("Error al cargar el archivo");
        }
    }

    boolean tieneCargadoUnArchivo() {
        if (Duración_ms < 0) {
            return false;
        }
        return true;
    }

    void deponer() {
        channels.clear();
        channels = null;
        controlTiempo.Reestablecer();
        controlTiempo = null;
        playerNotes.Deponer();
        playerNotes = null;
        positionNotalChannel = null;
    }

    public double porcentajeRecorrido() {
        if (!tieneCargadoUnArchivo()) {
            return 0;
        }
        return getPlayTime() / Duración_ms;
    }

    public double getPlayTime() {
        if (!tieneCargadoUnArchivo()) {
            return 0;
        }
        return controlTiempo.TiempoTranscurrido() * velocidadReproducción;
    }

    void change_momentPlayback(int ms) {
        if (!tieneCargadoUnArchivo()) {
            return;
        }
        controlTiempo.CambiarTiempo(ms);
        SuspenderTodasLasNotas();
        ReposicionarNotas(ms);
    }

    void ReposicionarNotas(int ms) {
        if (!tieneCargadoUnArchivo()) {
            return;
        }
        for (int i = 0; i < channels.size(); i++) {
            positionNotalChannel[i] = 0;
            for (int j = 0; j < channels.get(i).size() && channels.get(i).get(j).ms_activación < ms; j++) {
                positionNotalChannel[i]++;
            }
        }
    }

    void Detener() {
        if (!tieneCargadoUnArchivo()) {
            return;
        }
        if (controlTiempo != null) {
            controlTiempo.Pausar();
            controlTiempo.Reestablecer();
            change_momentPlayback(0);
        }
        try {
            Thread.sleep(10);
        } catch (Exception ex) {
        }
        SuspenderTodasLasNotas();
    }

    void SuspenderTodasLasNotas() {
        if (!tieneCargadoUnArchivo()) {
            return;
        }
        if (playerNotes != null) {
            for (MidiChannel CanalSonido : playerNotes.CanalSonido) {
                for (int j = 0; j < Byte.MAX_VALUE; j++) {
                    CanalSonido.noteOff(j);
                }
            }
        }
    }

    void Pausar() {
        if (!tieneCargadoUnArchivo()) {
            return;
        }
        controlTiempo.Pausar();
        try {
            Thread.sleep(10);
        } catch (InterruptedException ex) {
        }
        SuspenderTodasLasNotas();
    }

    public MIDI_parser start_playing(int repeticiones) {
        for (int i = 0; i < repeticiones || repeticiones < 0; i++) {
            if (i == 0) {
                start_playing(true, false);
            } else if (i == repeticiones - 1) {
                start_playing(false, true);
            } else {
                start_playing(false, false);
            }
        }
        return this;
    }

    public void start_playing(float retardo) {
        play_async(true, true, retardo);
    }

    public MIDI_parser start_playing() {
        start_playing(true, true);
        return this;
    }

    boolean enPausa() {
        if (!tieneCargadoUnArchivo()) {
            return true;
        }
        return controlTiempo.isOnPause();
    }

    boolean playback_started() {
        return hiloReproducción != null;
    }

    Thread hiloReproducción;

    void start_playing(boolean DesvInicio, boolean DesvFin) {
        play_async(DesvInicio, DesvFin, 0);
    }

    void play_async(boolean DesvInicio, boolean DesvFin, float retardo) {
        if (!tieneCargadoUnArchivo()) {
            return;
        }
        controlTiempo.resume();
        if (!playback_started()) {
            hiloReproducción = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep((long) (1000 * retardo));
                    } catch (InterruptedException ex) {
                    }
                    play_sync(DesvInicio, DesvFin);
                }
            };
            hiloReproducción.start();
        }
    }

    void play_sync(boolean DesvInicio, boolean DesvFin) {
        if (!tieneCargadoUnArchivo()) {
            return;
        }
        controlTiempo.resume();
        double tiempo_ms = 0;
        while (tiempo_ms <= Duración_ms || loop) {
            if (loop && tiempo_ms > Duración_ms) {
                change_momentPlayback(0);
            }
            try {
                if (controlTiempo.isOnPause()) {
                    Thread.sleep(200);
                    continue;
                }
                tiempo_ms = getPlayTime();
                for (int index_chanel = 0; index_chanel < channels.size(); index_chanel++) {
                    Canal canal = channels.get(index_chanel);
                    playerNotes.changeInstrument(index_chanel, canal.banco, canal.programa);
                    if (positionNotalChannel[index_chanel] < canal.size()) {

                        boolean newEventKey = tiempo_ms > canal.get(positionNotalChannel[index_chanel]).ms_activación;

                        if (newEventKey) {
                            float volumeFactor = volume;
                            {
                                volumeFactor *= canal.volúmen;
                                if (fade > 0) {
                                    float factorDesvanecer = 1;
                                    float tiempo_s = (float) (tiempo_ms / 1000f);
                                    float desvExt = fade;
                                    if (DesvInicio && tiempo_s <= desvExt) {
                                        factorDesvanecer = tiempo_s / desvExt;
                                    } else if (DesvFin && tiempo_s >= (Duración_ms / 1000f) - desvExt) {
                                        factorDesvanecer = 1 + (((Duración_ms) / 1000f - tiempo_s - desvExt) / desvExt);
                                    }
                                    volumeFactor *= factorDesvanecer;
                                }
                            }
                            if (canal.get(positionNotalChannel[index_chanel]).ActivarNota) {
                                playerNotes.toSound(index_chanel,
                                        canal.get(positionNotalChannel[index_chanel]).nota,
                                        volumeFactor * canal.get(positionNotalChannel[index_chanel]).volumen / 128);
                                if (eventNote != null) {
                                    eventNote.exec(index_chanel,
                                            canal.get(positionNotalChannel[index_chanel]).ms_activación,
                                            true,
                                            canal.get(positionNotalChannel[index_chanel]).nota,
                                            (int) (volumeFactor
                                                    * canal.get(positionNotalChannel[index_chanel]).volumen));
                                }
                            } else {
                                playerNotes.suspendPlay(index_chanel,
                                        canal.get(positionNotalChannel[index_chanel]).nota);
                                if (eventNote != null) {
                                    eventNote.exec(index_chanel,
                                            canal.get(positionNotalChannel[index_chanel]).ms_activación,
                                            false,
                                            canal.get(positionNotalChannel[index_chanel]).nota,
                                            (int) (volumeFactor
                                                    * canal.get(positionNotalChannel[index_chanel]).volumen));
                                }
                            }
                            positionNotalChannel[index_chanel]++;
                        }
                    }
                }
            } catch (Exception e) {
                break;
            }
        }
        Detener();
        if (eventPlayEnd != null) {
            eventPlayEnd.run();
        }
        hiloReproducción = null;
    }

    static RenglonesEventos GenerarRenglones(Object o) throws Exception {
        Sequence sequence = MIDI_fileplayer.Generar_Sequence(o);
        RenglonesEventos renglones = new RenglonesEventos();

        renglones.add(new RenglónEvento((int) (1000 * sequence.getMicrosecondLength() / 1000000f)));

        for (Track track : sequence.getTracks()) {
            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                int ms = (int) (1000 * sequence.getMicrosecondLength() * event.getTick()
                        / (sequence.getTickLength() * 1000000f));
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    boolean activar = false;
                    switch (sm.getCommand()) {
                        case MIDIEVENT_NOTE_ON:
                            activar = true;
                        case MIDIEVENT_NOTE_OFF:
                            int key = sm.getData1();
                            int volumen = sm.getData2();
                            renglones.add(new RenglónEvento(sm.getChannel(), ms, key, volumen, activar));
                        case MIDIEVENT_CHANGE_INSTRUMENT:
                            renglones.add(new RenglónEvento(sm.getChannel(), sm.getData2(), sm.getData1()));
                            break;
                    }
                }
            }
        }
        renglones.Organizar();
        return renglones;
    }

    class Canales extends ArrayList<Canal> {

        public Canales(RenglonesEventos renglones) {
            for (RenglónEvento renglón : renglones) {
                while (size() <= renglón.canal) {
                    add(new Canal());
                }
                if (!renglón.esDuración) {
                    if (renglón.esInstrumento) {
                        get(renglón.canal).banco = renglón.banco;
                        get(renglón.canal).programa = renglón.programa;
                    } else {
                        get(renglón.canal).add(
                                new DatoEvento(
                                        renglón.milisegundoEvento,
                                        renglón.activar,
                                        renglón.nota,
                                        renglón.volumen));
                    }
                }
            }
        }

        public Canales(long[] l) {
            for (long n : l) {
                if (SecuenciaLong.esDuración(n)) {
                    continue;
                }
                int[] a;
                if (SecuenciaLong.esInstrumento(n)) {
                    a = SecuenciaLong.ObtenerInstrumento(n);
                    while (size() <= a[0]) {
                        add(new Canal());
                    }
                    get(a[0]).banco = a[1];
                    get(a[0]).programa = a[2];
                    continue;
                } else if (SecuenciaLong.esSonido(n)) {
                    a = SecuenciaLong.ObtenerSonido(n);
                    while (size() <= a[1]) {
                        add(new Canal());
                    }
                    get(a[1]).add(
                            new DatoEvento(
                                    a[0],
                                    true,
                                    a[3],
                                    a[2]));
                    get(a[1]).add(
                            new DatoEvento(
                                    a[0] + a[4],
                                    false,
                                    a[3],
                                    a[2]));
                }
            }
            for (Canal c : this) {
                Collections.sort(c);
            }
        }
    }

    class Canal extends ArrayList<DatoEvento> {

        int banco;
        int programa;

        void CambiarInstrumento(int indice) {
            int llave = Play_notes.HerramientasMIDI.ObtenerLlave(indice);
            banco = (llave >>> 16) & 0xffff;
            programa = (llave) & 0xffff;
        }

        float volúmen = 1;
    }

    class DatoEvento implements Comparable<DatoEvento> {

        long ms_activación;
        boolean ActivarNota;
        int nota;
        int volumen;

        public DatoEvento(long ms, boolean ActivarNota, int Tecla, int volumen) {
            this.ms_activación = ms;
            this.ActivarNota = ActivarNota;
            this.nota = Tecla;
            this.volumen = volumen;
        }

        @Override
        public int compareTo(DatoEvento t) {
            return new Long(ms_activación).compareTo(t.ms_activación);
        }
    }

    static class SecuenciaLong {

        public final static int ID_DURACIÓN = 0;
        public final static int ID_INSTRUMENTO = 1;
        public final static int ID_SONIDO = 2;

        public final static int bits_identificador = 2;
        public final static int bits_canal = 5;
        public final static int bits_ms_activación = 26;
        public final static int bits_nota = 7;
        public final static int bits_volumen = 8;
        public final static int bits_sostener = 16;

        public final static int mascara_identificador = 0x3;
        public final static int mascara_canal = 0x1f;
        public final static int mascara_ms_activación = 0x3ffffff;
        public final static int mascara_nota = 0x7f;
        public final static int mascara_volumen = 0xff;
        public final static int mascara_sostener = 0xffff;

        public static float Volumen = 1;

        static String GenerarSecuencia(String s) {
            try {
                ArrayList<RenglónTransformado> rt = GenerarRenglonesTransformados(GenerarRenglones(s));
                return GenerarSecuencia(rt);
            } catch (Exception ex) {
                return null;
            }
        }

        static String GenerarSecuencia(ArrayList<RenglónTransformado> T) {
            String retorno = "return new long[]{\n";
            for (int i = 0; i < T.size(); i++) {
                System.out.println(T.get(i) + " - " + Valor(T.get(i)));
                retorno += Valor(T.get(i)) + "L";
                if (i != T.size() - 1) {
                    retorno += ",";
                    if (i % 10 == 0) {
                        retorno += "\n";
                    }
                }
            }
            retorno += "\n};";
            return retorno;
        }

        static long Valor(int duración) {
            duración <<= bits_identificador;
            return duración | ID_DURACIÓN;
        }

        static long Valor(RenglónTransformado r) {
            if (r.esDuración) {
                return Valor(r.Duración);
            }
            if (r.esInstrumento) {
                return Valor(r.canal, r.banco, r.programa);
            }
            return Valor(
                    r.milisegundoEvento,
                    r.canal, (long) (r.volumen * Volumen),
                    r.nota,
                    r.sostener);
        }

        static long Valor(long canal, int banco, int programa) {
            long llave = Play_notes.HerramientasMIDI.GenerarLlaveInstrumento(banco, programa);
            llave <<= bits_identificador;
            canal <<= 32 + bits_identificador;
            return canal | llave | ID_INSTRUMENTO;
        }

        static long Valor(long ms_activación, long canal, long volumen, long nota, long sostener) {
            ms_activación &= mascara_ms_activación;
            canal &= mascara_canal;
            volumen &= mascara_volumen;
            nota &= mascara_nota;
            sostener &= mascara_sostener;

            int a = bits_ms_activación;
            ms_activación <<= 64 - a;
            a += bits_canal;
            canal <<= 64 - a;
            a += bits_volumen;
            volumen <<= 64 - a;
            a += bits_nota;
            nota <<= 64 - a;
            a += bits_sostener;
            sostener <<= 64 - a;

            return ms_activación | canal | volumen | nota | sostener | ID_SONIDO;
        }

        static boolean esDuración(long valor) {
            return ObtenerIdentificador(valor) == ID_DURACIÓN;
        }

        static boolean esInstrumento(long valor) {
            return ObtenerIdentificador(valor) == ID_INSTRUMENTO;
        }

        static boolean esSonido(long valor) {
            return ObtenerIdentificador(valor) == ID_SONIDO;
        }

        static int ObtenerIdentificador(long valor) {
            return (int) (valor & mascara_identificador);
        }

        static int ObtenerDuración(long valor) {
            return (int) (valor >>> bits_identificador);
        }

        static int[] ObtenerInstrumento(long valor) {
            valor >>>= bits_identificador;
            int canal = (int) (valor >>> 32) & mascara_canal;
            int banco = (int) (valor >>> 16) & 0xffff;
            int programa = (int) (valor) & 0xffff;
            return new int[] { canal, banco, programa };
        }

        static int[] ObtenerSonido(long valor) {
            long ms_activación = valor;
            long canal = valor;
            long volumen = valor;
            long nota = valor;
            long sostener = valor;

            int a = bits_ms_activación;
            ms_activación >>>= 64 - a;
            a += bits_canal;
            canal >>>= 64 - a;
            a += bits_volumen;
            volumen >>>= 64 - a;
            a += bits_nota;
            nota >>>= 64 - a;
            a += bits_sostener;
            sostener >>>= 64 - a;

            ms_activación &= mascara_ms_activación;
            canal &= mascara_canal;
            volumen &= mascara_volumen;
            nota &= mascara_nota;
            sostener &= mascara_sostener;
            int[] retorno = new int[] { (int) ms_activación, (int) canal, (int) volumen, (int) nota, (int) sostener };
            return retorno;
        }

    }

    static ArrayList<RenglónTransformado> GenerarRenglonesTransformados(long[] l) {
        ArrayList<RenglónTransformado> rt = new ArrayList<>();
        for (long m : l) {
            if (SecuenciaLong.esDuración(m)) {
                rt.add(new RenglónTransformado(SecuenciaLong.ObtenerDuración(m)));
                continue;
            }
            if (SecuenciaLong.esInstrumento(m)) {
                int[] a = SecuenciaLong.ObtenerInstrumento(m);
                rt.add(new RenglónTransformado(a[0], a[1], a[2]));
                continue;
            }
            if (SecuenciaLong.esSonido(m)) {
                int[] a = SecuenciaLong.ObtenerSonido(m);
                rt.add(new RenglónTransformado(
                        new RenglónEvento(a[1], a[0], a[3], a[2], true),
                        a[4]));
            }
        }
        return rt;
    }

    static ArrayList<RenglónTransformado> GenerarRenglonesTransformados(RenglonesEventos entrada) {
        RenglonesEventos r = new RenglonesEventos();
        int s = entrada.size();
        for (RenglónEvento renglónEvento : entrada) {
            r.add(renglónEvento);
        }
        ArrayList<RenglónTransformado> resultado = new ArrayList<>();

        while (!r.isEmpty()) {
            RenglónEvento renglónInicio = r.get(0);
            r.remove(0);
            if (renglónInicio.esDuración) {
                resultado.add(new RenglónTransformado(renglónInicio.Duración));
                continue;
            }
            if (!renglónInicio.activar) {

            }
            RenglónTransformado renglónResultado = null;
            for (RenglónEvento renglónEvento : r) {
                if (renglónEvento.nota == renglónInicio.nota) {
                    if (renglónEvento.activar) {

                    }
                    if (renglónInicio.esInstrumento) {
                        renglónResultado = new RenglónTransformado(
                                renglónInicio.canal, renglónInicio.banco, renglónInicio.programa);
                    } else {
                        renglónResultado = new RenglónTransformado(
                                renglónInicio,
                                renglónEvento.milisegundoEvento - renglónInicio.milisegundoEvento);
                    }
                    r.remove(renglónEvento);
                    break;
                }
            }
            if (renglónResultado == null) {

            } else {
                resultado.add(renglónResultado);
            }
        }
        return resultado;
    }

    static class RenglónTransformado extends RenglónEvento {

        int sostener;

        public RenglónTransformado(int Duración) {
            super(Duración);
        }

        public RenglónTransformado(int canal, int banco, int programa) {
            super(canal, banco, programa);
        }

        public RenglónTransformado(RenglónEvento renglón, int sostener) {
            super(
                    renglón.canal,
                    renglón.milisegundoEvento,
                    renglón.nota,
                    renglón.volumen,
                    renglón.activar);
            this.sostener = sostener;
        }

        @Override
        public String toString() {
            if (esDuración) {
                return Duración + " ms";
            }
            if (esInstrumento) {
                return "C:" + canal
                        + ":B:" + banco
                        + ":P:" + programa;
            }
            return "C:" + canal
                    + ":ME:" + String.format("%08d", milisegundoEvento)
                    + ":N:" + nota
                    + ":V:" + volumen
                    + ":S:" + sostener;
        }
    }

    public static class RenglonesEventos extends ArrayList<RenglónEvento> {

        void Organizar() {
            Collections.sort(this);
            int canal = -1;
            ArrayList<RenglónEvento> eliminar = new ArrayList<>();
            for (RenglónEvento r : this) {
                if (!r.esInstrumento) {
                    continue;
                }
                if (r.canal != canal) {
                    canal = r.canal;
                } else {
                    eliminar.add(r);
                }
            }
            removeAll(eliminar);
        }
    }

    public static class RenglónEvento implements Comparable<RenglónEvento> {

        int canal;

        boolean esDuración = false;
        int Duración;

        boolean esInstrumento = false;
        int banco;
        int programa;

        int milisegundoEvento;
        int nota;
        int volumen;
        boolean activar = false;

        public RenglónEvento(int canal, int milisegundoEvento, int nota, int volumen, boolean activar) {
            this.canal = canal;
            this.milisegundoEvento = milisegundoEvento;
            this.nota = nota;
            this.volumen = volumen;
            this.activar = activar;
        }

        public RenglónEvento(int canal, int banco, int programa) {
            this.canal = canal;
            this.banco = banco;
            this.programa = programa;
            esInstrumento = true;
        }

        public RenglónEvento(int Duración) {
            this.Duración = Duración;
            esDuración = true;
        }

        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }

        @Override
        public String toString() {
            if (esDuración) {
                return Duración + " ms";
            }
            if (esInstrumento) {
                return "C:" + canal
                        + ":B:" + banco
                        + ":Programa:" + programa;
            }
            return "C:" + canal
                    + ":ME:" + String.format("%08d", milisegundoEvento)
                    + ":N:" + nota
                    + ":V:" + volumen
                    + ":A:" + (activar);
        }

        @Override
        public int compareTo(RenglónEvento t) {
            return toString().compareTo(t.toString());
        }
    }

    public static interface EventoNota {

        void exec(int Canal, long ms_activación, boolean ActivarNota, int nota, int volumen);
    }
}
