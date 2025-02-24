package sounds;

import Tools.callback;
import javax.sound.midi.*;

public class Play_notes {

    public float Volúmen = 1;
    public float Eco = 0;
    public MidiChannel[] CanalSonido = new MidiChannel[32];

    public final Probar Probar = new Probar();

    Synthesizer synth;
    Synthesizer synth2;

    public Play_notes() {
        try {
            this.synth = MidiSystem.getSynthesizer();
            this.synth2 = MidiSystem.getSynthesizer();
            synth.open();
            synth2.open();
            for (int i = 0; i < 16; i++) {
                CanalSonido[i] = synth.getChannels()[i];
                CanalSonido[i + 16] = synth2.getChannels()[i];
            }
        } catch (MidiUnavailableException ex) {
        }
    }

    public static void main(String[] args) {
        Play_notes notas = new Play_notes();
        notas.CambiarInstrumento(HerramientasMIDI.MUSIC_BOX);
        notas.Eco = 10;
        notas.Probar.Tapión();
    }

    public void Deponer() {
        CanalSonido = null;
        synth.close();
        synth2.close();
        synth = null;
        synth2 = null;
    }

    public void Reproducir(String Nota) {
        CanalSonido[0].noteOn(HerramientasMIDI.CalcularByte_Nota(Nota), (int) (Volúmen * 128));
    }

    public void Reproducir(String Nota, int Sostener, int Pausa) {
        Reproducir(0, Nota, Sostener, Pausa);
    }

    public void Reproducir(int Canal, String Nota) {
        CanalSonido[Canal].noteOn(HerramientasMIDI.CalcularByte_Nota(Nota), (int) (Volúmen * 128));
    }

    public void Reproducir(int Canal, String Nota, int Sostener, int Pausa) {
        new Thread() {
            @Override
            public void run() {
                Reproducir(Canal, Nota);
                Descansar(Sostener);
                Suspender(Canal, Nota);
            }
        }.start();
        if (Pausa < 0) {
            Descansar(Sostener);
        } else {
            Descansar(Pausa);
        }
    }

    public void Reproducir(int Canal, int Nota, int Sostener, int Pausa) {
        new Thread() {
            @Override
            public void run() {
                Reproducir(Canal, Nota);
                Descansar(Sostener);
                suspendPlay(Canal, Nota);
            }
        }.start();
        if (Pausa < 0) {
            Descansar(Sostener);
        } else {
            Descansar(Pausa);
        }
    }

    public void Reproducir(int byteNota) {
        CanalSonido[0].noteOn(byteNota, (int) (Volúmen * 128));
    }

    public void Reproducir(int Canal, int byteNota) {
        CanalSonido[Canal].noteOn(byteNota, (int) (Volúmen * 128));
    }

    public void toSound(int Canal, int byteNota, float volumen) {
        CanalSonido[Canal].noteOn(byteNota, (int) (volumen * 128));
    }

    private void Descansar(int Descanso) {
        try {
            Thread.sleep(Math.abs(Descanso));
        } catch (InterruptedException e) {
        }
    }

    byte ReguladorEco[] = new byte[12 * 11];

    public void Suspender(String Nota) {
        suspendPlay(0, HerramientasMIDI.CalcularByte_Nota(Nota));
    }

    public void Suspender(int Canal, String Nota) {
        suspendPlay(Canal, HerramientasMIDI.CalcularByte_Nota(Nota));
    }

    public void Suspender(int byteNota) {
        suspendPlay(0, byteNota);
    }

    public void suspendPlay(int Canal, int byteNota) {
        new Thread() {
            @Override
            public void run() {
                ReguladorEco[byteNota]++;
                Descansar((int) (Eco * 1000));
                ReguladorEco[byteNota]--;
                if (ReguladorEco[byteNota] == 0) {
                    CanalSonido[Canal].noteOff(byteNota);
                }
            }
        }.start();
    }

    public void CambiarInstrumento(int Instrumento) {
        CambiarInstrumento(0, Instrumento);
    }

    public void CambiarInstrumento(int Canal, int Instrumento) {
        int Banco = (Instrumento >> 16) & 0xffff;
        int Programa = Instrumento & 0xffff;
        changeInstrument(Canal, Banco, Programa);
    }

    public void changeInstrument(int Canal, int Banco, int Programa) {
        CanalSonido[Canal].programChange(Banco, Programa);
    }

    public static Object[] TablaInstrumentos_Obtener1(int Indice) throws MidiUnavailableException {
        Instrument[] orchestra = MidiSystem.getSynthesizer().getAvailableInstruments();
        String renglón = orchestra[Indice].toString();
        String instrumento;
        if (renglón.contains("Instrument:")) {
            instrumento = renglón.substring(
                    "Instrument:".length(),
                    renglón.lastIndexOf("bank #")
            );
        } else {
            instrumento = renglón.substring(
                    "Drumkit:".length(),
                    renglón.lastIndexOf("bank #")
            );
        }
        String Banco = renglón.substring(
                renglón.lastIndexOf("bank #") + "bank #".length(),
                renglón.lastIndexOf("preset #")
        );
        String Programa = renglón.substring(
                renglón.lastIndexOf("preset #") + "preset #".length()
        );
        return new Object[]{
            instrumento.trim(),
            Integer.parseInt(Banco.trim()),
            Integer.parseInt(Programa.trim())
        };
    }

    public static Object[][] TablaInstrumentos() throws MidiUnavailableException {
        Instrument[] orchestra = MidiSystem.getSynthesizer().getAvailableInstruments();
        Object[][] retorno = new Object[orchestra.length][3];
        for (int i = 0; i < orchestra.length; i++) {
            String renglón = orchestra[i].toString();
            String instrumento;
            if (renglón.contains("Instrument:")) {
                instrumento = renglón.substring(
                        "Instrument:".length(),
                        renglón.lastIndexOf("bank #")
                );
            } else {
                instrumento = renglón.substring(
                        "Drumkit:".length(),
                        renglón.lastIndexOf("bank #")
                );
            }
            String Banco = renglón.substring(
                    renglón.lastIndexOf("bank #") + "bank #".length(),
                    renglón.lastIndexOf("preset #")
            );
            String Programa = renglón.substring(
                    renglón.lastIndexOf("preset #") + "preset #".length()
            );
            retorno[i] = new Object[]{
                instrumento.trim(),
                Integer.parseInt(Banco.trim()),
                Integer.parseInt(Programa.trim())};
        }
        return retorno;
    }

    public static String TablaInstrumentosEspañol() throws MidiUnavailableException {
        Instrument[] orchestra = MidiSystem.getSynthesizer().getAvailableInstruments();
        String retorno = "";
        for (int i = 0; i < orchestra.length; i++) {
            String renglón = orchestra[i].toString();
            String instrumento;
            if (renglón.contains("Instrument:")) {
                instrumento = renglón.substring(
                        "Instrument:".length(),
                        renglón.lastIndexOf("bank #")
                );
            } else {
                instrumento = renglón.substring(
                        "Drumkit:".length(),
                        renglón.lastIndexOf("bank #")
                );
            }
            String Banco = renglón.substring(
                    renglón.lastIndexOf("bank #") + "bank #".length(),
                    renglón.lastIndexOf("preset #")
            );
            String Programa = renglón.substring(
                    renglón.lastIndexOf("preset #") + "preset #".length()
            );
            Programa = Programa.trim();
            Banco = Banco.trim();
            instrumento = instrumento.trim();
            String NuevoRenglón = i + ") " + instrumento + " ->    Banco: " + Banco + "   Programa: " + Programa;
            retorno += NuevoRenglón;
            if (i < orchestra.length - 1) {
                retorno += "\n";
            }
        }
        return retorno;
    }

    public static String TablaInstrumentosOriginal() throws MidiUnavailableException {
        Instrument[] orchestra = MidiSystem.getSynthesizer().getAvailableInstruments();
        String retorno = "";
        for (int i = 0; i < orchestra.length; i++) {
            String NuevoRenglón = i + ") " + orchestra[i].toString();
            retorno += NuevoRenglón;
            if (i < orchestra.length - 1) {
                retorno += "\n";
            }
        }
        return retorno;
    }

    private void GenerarCódigoParaLaConversiónDeLLaveAInstrumento() {
        String Instrumentos
                = "INDICE_PIANO_1 = 0,\n"
                + "                INDICE_PIANO_2 = 1,\n"
                + "                INDICE_PIANO_3 = 2,\n"
                + "                INDICE_HONKY_TONK = 3,\n"
                + "                INDICE_E_PIANO_1 = 4,\n"
                + "                INDICE_E_PIANO_2 = 5,\n"
                + "                INDICE_HARPSICHORD = 6,\n"
                + "                INDICE_CLAV_ = 7,\n"
                + "                INDICE_CELESTA = 8,\n"
                + "                INDICE_GLOCKENSPIEL = 9,\n"
                + "                INDICE_MUSIC_BOX = 10,\n"
                + "                INDICE_VIBRAPHONE = 11,\n"
                + "                INDICE_MARIMBA = 12,\n"
                + "                INDICE_XYLOPHONE = 13,\n"
                + "                INDICE_TUBULAR_BELL = 14,\n"
                + "                INDICE_SANTUR = 15,\n"
                + "                INDICE_ORGAN_1 = 16,\n"
                + "                INDICE_ORGAN_2 = 17,\n"
                + "                INDICE_ORGAN_3 = 18,\n"
                + "                INDICE_CHURCH_ORG_1 = 19,\n"
                + "                INDICE_REED_ORGAN = 20,\n"
                + "                INDICE_ACCORDION_FR = 21,\n"
                + "                INDICE_HARMONICA = 22,\n"
                + "                INDICE_BANDONEON = 23,\n"
                + "                INDICE_NYLON_STR_GT = 24,\n"
                + "                INDICE_STEEL_STR_GT = 25,\n"
                + "                INDICE_JAZZ_GT_ = 26,\n"
                + "                INDICE_CLEAN_GT_ = 27,\n"
                + "                INDICE_MUTED_GT_ = 28,\n"
                + "                INDICE_OVERDRIVE_GT = 29,\n"
                + "                INDICE_DISTORTIONGT = 30,\n"
                + "                INDICE_GT_HARMONICS = 31,\n"
                + "                INDICE_ACOUSTIC_BS_ = 32,\n"
                + "                INDICE_FINGERED_BS_ = 33,\n"
                + "                INDICE_PICKED_BS_ = 34,\n"
                + "                INDICE_FRETLESS_BS_ = 35,\n"
                + "                INDICE_SLAP_BASS_1 = 36,\n"
                + "                INDICE_SLAP_BASS_2 = 37,\n"
                + "                INDICE_SYNTH_BASS_1 = 38,\n"
                + "                INDICE_SYNTH_BASS_2 = 39,\n"
                + "                INDICE_VIOLIN = 40,\n"
                + "                INDICE_VIOLA = 41,\n"
                + "                INDICE_CELLO = 42,\n"
                + "                INDICE_CONTRABASS = 43,\n"
                + "                INDICE_TREMOLO_STR = 44,\n"
                + "                INDICE_PIZZICATOSTR = 45,\n"
                + "                INDICE_HARP = 46,\n"
                + "                INDICE_TIMPANI = 47,\n"
                + "                INDICE_STRINGS = 48,\n"
                + "                INDICE_SLOW_STRINGS = 49,\n"
                + "                INDICE_SYN_STRINGS1 = 50,\n"
                + "                INDICE_SYN_STRINGS2 = 51,\n"
                + "                INDICE_CHOIR_AAHS = 52,\n"
                + "                INDICE_VOICE_OOHS = 53,\n"
                + "                INDICE_SYNVOX = 54,\n"
                + "                INDICE_ORCHESTRAHIT = 55,\n"
                + "                INDICE_TRUMPET = 56,\n"
                + "                INDICE_TROMBONE = 57,\n"
                + "                INDICE_TUBA = 58,\n"
                + "                INDICE_MUTEDTRUMPET = 59,\n"
                + "                INDICE_FRENCH_HORNS = 60,\n"
                + "                INDICE_BRASS_1 = 61,\n"
                + "                INDICE_SYNTH_BRASS1 = 62,\n"
                + "                INDICE_SYNTH_BRASS2 = 63,\n"
                + "                INDICE_SOPRANO_SAX = 64,\n"
                + "                INDICE_ALTO_SAX = 65,\n"
                + "                INDICE_TENOR_SAX = 66,\n"
                + "                INDICE_BARITONE_SAX = 67,\n"
                + "                INDICE_OBOE = 68,\n"
                + "                INDICE_ENGLISH_HORN = 69,\n"
                + "                INDICE_BASSOON = 70,\n"
                + "                INDICE_CLARINET = 71,\n"
                + "                INDICE_PICCOLO = 72,\n"
                + "                INDICE_FLUTE = 73,\n"
                + "                INDICE_RECORDER = 74,\n"
                + "                INDICE_PAN_FLUTE = 75,\n"
                + "                INDICE_BOTTLE_BLOW = 76,\n"
                + "                INDICE_SHAKUHACHI = 77,\n"
                + "                INDICE_WHISTLE = 78,\n"
                + "                INDICE_OCARINA = 79,\n"
                + "                INDICE_SQUARE_WAVE = 80,\n"
                + "                INDICE_SAW_WAVE = 81,\n"
                + "                INDICE_SYN_CALLIOPE = 82,\n"
                + "                INDICE_CHIFFER_LEAD = 83,\n"
                + "                INDICE_CHARANG = 84,\n"
                + "                INDICE_SOLO_VOX = 85,\n"
                + "                INDICE_5TH_SAW_WAVE = 86,\n"
                + "                INDICE_BASS_N_LEAD = 87,\n"
                + "                INDICE_FANTASIA = 88,\n"
                + "                INDICE_WARM_PAD = 89,\n"
                + "                INDICE_POLYSYNTH = 90,\n"
                + "                INDICE_SPACE_VOICE = 91,\n"
                + "                INDICE_BOWED_GLASS = 92,\n"
                + "                INDICE_METAL_PAD = 93,\n"
                + "                INDICE_HALO_PAD = 94,\n"
                + "                INDICE_SWEEP_PAD = 95,\n"
                + "                INDICE_ICE_RAIN = 96,\n"
                + "                INDICE_SOUNDTRACK = 97,\n"
                + "                INDICE_CRYSTAL = 98,\n"
                + "                INDICE_ATMOSPHERE = 99,\n"
                + "                INDICE_BRIGHTNESS = 100,\n"
                + "                INDICE_GOBLIN = 101,\n"
                + "                INDICE_ECHO_DROPS = 102,\n"
                + "                INDICE_STAR_THEME = 103,\n"
                + "                INDICE_SITAR = 104,\n"
                + "                INDICE_BANJO = 105,\n"
                + "                INDICE_SHAMISEN = 106,\n"
                + "                INDICE_KOTO = 107,\n"
                + "                INDICE_KALIMBA = 108,\n"
                + "                INDICE_BAGPIPE = 109,\n"
                + "                INDICE_FIDDLE = 110,\n"
                + "                INDICE_SHANAI = 111,\n"
                + "                INDICE_TINKLE_BELL = 112,\n"
                + "                INDICE_AGOGO = 113,\n"
                + "                INDICE_STEEL_DRUMS = 114,\n"
                + "                INDICE_WOODBLOCK = 115,\n"
                + "                INDICE_TAIKO = 116,\n"
                + "                INDICE_MELO_TOM_1 = 117,\n"
                + "                INDICE_SYNTH_DRUM = 118,\n"
                + "                INDICE_REVERSE_CYM_ = 119,\n"
                + "                INDICE_GT_FRETNOISE = 120,\n"
                + "                INDICE_BREATH_NOISE = 121,\n"
                + "                INDICE_SEASHORE = 122,\n"
                + "                INDICE_BIRD = 123,\n"
                + "                INDICE_TELEPHONE_1 = 124,\n"
                + "                INDICE_HELICOPTER = 125,\n"
                + "                INDICE_APPLAUSE = 126,\n"
                + "                INDICE_GUN_SHOT = 127,\n"
                + "                INDICE_SYNTHBASS101 = 128,\n"
                + "                INDICE_TROMBONE_2 = 129,\n"
                + "                INDICE_FR_HORN_2 = 130,\n"
                + "                INDICE_SQUARE = 131,\n"
                + "                INDICE_SAW = 132,\n"
                + "                INDICE_SYN_MALLET = 133,\n"
                + "                INDICE_ECHO_BELL = 134,\n"
                + "                INDICE_SITAR_2 = 135,\n"
                + "                INDICE_GT_CUT_NOISE = 136,\n"
                + "                INDICE_FL_KEY_CLICK = 137,\n"
                + "                INDICE_RAIN = 138,\n"
                + "                INDICE_DOG = 139,\n"
                + "                INDICE_TELEPHONE_2 = 140,\n"
                + "                INDICE_CAR_ENGINE = 141,\n"
                + "                INDICE_LAUGHING = 142,\n"
                + "                INDICE_MACHINE_GUN = 143,\n"
                + "                INDICE_ECHO_PAN = 144,\n"
                + "                INDICE_STRING_SLAP = 145,\n"
                + "                INDICE_THUNDER = 146,\n"
                + "                INDICE_HORSE_GALLOP = 147,\n"
                + "                INDICE_DOORCREAKING = 148,\n"
                + "                INDICE_CAR_STOP = 149,\n"
                + "                INDICE_SCREAMING = 150,\n"
                + "                INDICE_LASERGUN = 151,\n"
                + "                INDICE_WIND = 152,\n"
                + "                INDICE_BIRD_2 = 153,\n"
                + "                INDICE_DOOR = 154,\n"
                + "                INDICE_CAR_PASS = 155,\n"
                + "                INDICE_PUNCH = 156,\n"
                + "                INDICE_EXPLOSION = 157,\n"
                + "                INDICE_STREAM = 158,\n"
                + "                INDICE_SCRATCH = 159,\n"
                + "                INDICE_CAR_CRASH = 160,\n"
                + "                INDICE_HEART_BEAT = 161,\n"
                + "                INDICE_BUBBLE = 162,\n"
                + "                INDICE_WIND_CHIMES = 163,\n"
                + "                INDICE_SIREN = 164,\n"
                + "                INDICE_FOOTSTEPS = 165,\n"
                + "                INDICE_TRAIN = 166,\n"
                + "                INDICE_JETPLANE = 167,\n"
                + "                INDICE_PIANO_1$ = 168,\n"
                + "                INDICE_PIANO_2$ = 169,\n"
                + "                INDICE_PIANO_3$ = 170,\n"
                + "                INDICE_HONKY_TONK$ = 171,\n"
                + "                INDICE_DETUNED_EP_1 = 172,\n"
                + "                INDICE_DETUNED_EP_2 = 173,\n"
                + "                INDICE_COUPLED_HPS_ = 174,\n"
                + "                INDICE_VIBRAPHONE$ = 175,\n"
                + "                INDICE_MARIMBA$ = 176,\n"
                + "                INDICE_CHURCH_BELL = 177,\n"
                + "                INDICE_DETUNED_OR_1 = 178,\n"
                + "                INDICE_DETUNED_OR_2 = 179,\n"
                + "                INDICE_CHURCH_ORG_2 = 180,\n"
                + "                INDICE_ACCORDION_IT = 181,\n"
                + "                INDICE_UKULELE = 182,\n"
                + "                INDICE_12_STR_GT = 183,\n"
                + "                INDICE_HAWAIIAN_GT_ = 184,\n"
                + "                INDICE_CHORUS_GT_ = 185,\n"
                + "                INDICE_FUNK_GT_ = 186,\n"
                + "                INDICE_FEEDBACK_GT_ = 187,\n"
                + "                INDICE_GT_FEEDBACK = 188,\n"
                + "                INDICE_SYNTH_BASS_3 = 189,\n"
                + "                INDICE_SYNTH_BASS_4 = 190,\n"
                + "                INDICE_SLOW_VIOLIN = 191,\n"
                + "                INDICE_ORCHESTRA = 192,\n"
                + "                INDICE_SYN_STRINGS3 = 193,\n"
                + "                INDICE_BRASS_2 = 194,\n"
                + "                INDICE_SYNTH_BRASS3 = 195,\n"
                + "                INDICE_SYNTH_BRASS4 = 196,\n"
                + "                INDICE_SINE_WAVE = 197,\n"
                + "                INDICE_DOCTOR_SOLO = 198,\n"
                + "                INDICE_TAISHO_KOTO = 199,\n"
                + "                INDICE_CASTANETS = 200,\n"
                + "                INDICE_CONCERT_BD = 201,\n"
                + "                INDICE_MELO_TOM_2 = 202,\n"
                + "                INDICE_808_TOM = 203,\n"
                + "                INDICE_STARSHIP = 204,\n"
                + "                INDICE_CARILLON = 205,\n"
                + "                INDICE_ELEC_PERC_ = 206,\n"
                + "                INDICE_BURST_NOISE = 207,\n"
                + "                INDICE_PIANO_1D = 208,\n"
                + "                INDICE_E_PIANO_1V = 209,\n"
                + "                INDICE_E_PIANO_2V = 210,\n"
                + "                INDICE_HARPSICHORD$ = 211,\n"
                + "                INDICE_60_S_ORGAN_1 = 212,\n"
                + "                INDICE_CHURCH_ORG_3 = 213,\n"
                + "                INDICE_NYLON_GT_O = 214,\n"
                + "                INDICE_MANDOLIN = 215,\n"
                + "                INDICE_FUNK_GT_2 = 216,\n"
                + "                INDICE_RUBBER_BASS = 217,\n"
                + "                INDICE_ANALOGBRASS1 = 218,\n"
                + "                INDICE_ANALOGBRASS2 = 219,\n"
                + "                INDICE_60_S_E_PIANO = 220,\n"
                + "                INDICE_HARPSI_O = 221,\n"
                + "                INDICE_ORGAN_4 = 222,\n"
                + "                INDICE_ORGAN_5 = 223,\n"
                + "                INDICE_NYLON_GT_2 = 224,\n"
                + "                INDICE_CHOIR_AAHS_2 = 225,\n"
                + "                INDICE_STANDARD = 226,\n"
                + "                INDICE_ROOM = 227,\n"
                + "                INDICE_POWER = 228,\n"
                + "                INDICE_ELECTRONIC = 229,\n"
                + "                INDICE_TR_808 = 230,\n"
                + "                INDICE_JAZZ = 231,\n"
                + "                INDICE_BRUSH = 232,\n"
                + "                INDICE_ORCHESTRA$ = 233,\n"
                + "                INDICE_SFX = 234;";
        String[] instrumentos = Instrumentos.split("\n");
        System.out.println("switch(Instrumento){");
        for (int i = 0; i < instrumentos.length; i++) {
            instrumentos[i] = instrumentos[i].trim();
            instrumentos[i] = instrumentos[i].substring(0, instrumentos[i].indexOf(" "));
            System.out.print("case ");
            System.out.print(instrumentos[i].replace("INDICE", "INSTRUMENTO"));
            System.out.println(":");
            System.out.println("return " + instrumentos[i] + ";");
        }
        System.out.println("}");
    }

    private void GenerarCódigoParaLaConversiónDeInstrumentoALLave() {
        String Instrumentos
                = "INDICE_PIANO_1 = 0,\n"
                + "                INDICE_PIANO_2 = 1,\n"
                + "                INDICE_PIANO_3 = 2,\n"
                + "                INDICE_HONKY_TONK = 3,\n"
                + "                INDICE_E_PIANO_1 = 4,\n"
                + "                INDICE_E_PIANO_2 = 5,\n"
                + "                INDICE_HARPSICHORD = 6,\n"
                + "                INDICE_CLAV_ = 7,\n"
                + "                INDICE_CELESTA = 8,\n"
                + "                INDICE_GLOCKENSPIEL = 9,\n"
                + "                INDICE_MUSIC_BOX = 10,\n"
                + "                INDICE_VIBRAPHONE = 11,\n"
                + "                INDICE_MARIMBA = 12,\n"
                + "                INDICE_XYLOPHONE = 13,\n"
                + "                INDICE_TUBULAR_BELL = 14,\n"
                + "                INDICE_SANTUR = 15,\n"
                + "                INDICE_ORGAN_1 = 16,\n"
                + "                INDICE_ORGAN_2 = 17,\n"
                + "                INDICE_ORGAN_3 = 18,\n"
                + "                INDICE_CHURCH_ORG_1 = 19,\n"
                + "                INDICE_REED_ORGAN = 20,\n"
                + "                INDICE_ACCORDION_FR = 21,\n"
                + "                INDICE_HARMONICA = 22,\n"
                + "                INDICE_BANDONEON = 23,\n"
                + "                INDICE_NYLON_STR_GT = 24,\n"
                + "                INDICE_STEEL_STR_GT = 25,\n"
                + "                INDICE_JAZZ_GT_ = 26,\n"
                + "                INDICE_CLEAN_GT_ = 27,\n"
                + "                INDICE_MUTED_GT_ = 28,\n"
                + "                INDICE_OVERDRIVE_GT = 29,\n"
                + "                INDICE_DISTORTIONGT = 30,\n"
                + "                INDICE_GT_HARMONICS = 31,\n"
                + "                INDICE_ACOUSTIC_BS_ = 32,\n"
                + "                INDICE_FINGERED_BS_ = 33,\n"
                + "                INDICE_PICKED_BS_ = 34,\n"
                + "                INDICE_FRETLESS_BS_ = 35,\n"
                + "                INDICE_SLAP_BASS_1 = 36,\n"
                + "                INDICE_SLAP_BASS_2 = 37,\n"
                + "                INDICE_SYNTH_BASS_1 = 38,\n"
                + "                INDICE_SYNTH_BASS_2 = 39,\n"
                + "                INDICE_VIOLIN = 40,\n"
                + "                INDICE_VIOLA = 41,\n"
                + "                INDICE_CELLO = 42,\n"
                + "                INDICE_CONTRABASS = 43,\n"
                + "                INDICE_TREMOLO_STR = 44,\n"
                + "                INDICE_PIZZICATOSTR = 45,\n"
                + "                INDICE_HARP = 46,\n"
                + "                INDICE_TIMPANI = 47,\n"
                + "                INDICE_STRINGS = 48,\n"
                + "                INDICE_SLOW_STRINGS = 49,\n"
                + "                INDICE_SYN_STRINGS1 = 50,\n"
                + "                INDICE_SYN_STRINGS2 = 51,\n"
                + "                INDICE_CHOIR_AAHS = 52,\n"
                + "                INDICE_VOICE_OOHS = 53,\n"
                + "                INDICE_SYNVOX = 54,\n"
                + "                INDICE_ORCHESTRAHIT = 55,\n"
                + "                INDICE_TRUMPET = 56,\n"
                + "                INDICE_TROMBONE = 57,\n"
                + "                INDICE_TUBA = 58,\n"
                + "                INDICE_MUTEDTRUMPET = 59,\n"
                + "                INDICE_FRENCH_HORNS = 60,\n"
                + "                INDICE_BRASS_1 = 61,\n"
                + "                INDICE_SYNTH_BRASS1 = 62,\n"
                + "                INDICE_SYNTH_BRASS2 = 63,\n"
                + "                INDICE_SOPRANO_SAX = 64,\n"
                + "                INDICE_ALTO_SAX = 65,\n"
                + "                INDICE_TENOR_SAX = 66,\n"
                + "                INDICE_BARITONE_SAX = 67,\n"
                + "                INDICE_OBOE = 68,\n"
                + "                INDICE_ENGLISH_HORN = 69,\n"
                + "                INDICE_BASSOON = 70,\n"
                + "                INDICE_CLARINET = 71,\n"
                + "                INDICE_PICCOLO = 72,\n"
                + "                INDICE_FLUTE = 73,\n"
                + "                INDICE_RECORDER = 74,\n"
                + "                INDICE_PAN_FLUTE = 75,\n"
                + "                INDICE_BOTTLE_BLOW = 76,\n"
                + "                INDICE_SHAKUHACHI = 77,\n"
                + "                INDICE_WHISTLE = 78,\n"
                + "                INDICE_OCARINA = 79,\n"
                + "                INDICE_SQUARE_WAVE = 80,\n"
                + "                INDICE_SAW_WAVE = 81,\n"
                + "                INDICE_SYN_CALLIOPE = 82,\n"
                + "                INDICE_CHIFFER_LEAD = 83,\n"
                + "                INDICE_CHARANG = 84,\n"
                + "                INDICE_SOLO_VOX = 85,\n"
                + "                INDICE_5TH_SAW_WAVE = 86,\n"
                + "                INDICE_BASS_N_LEAD = 87,\n"
                + "                INDICE_FANTASIA = 88,\n"
                + "                INDICE_WARM_PAD = 89,\n"
                + "                INDICE_POLYSYNTH = 90,\n"
                + "                INDICE_SPACE_VOICE = 91,\n"
                + "                INDICE_BOWED_GLASS = 92,\n"
                + "                INDICE_METAL_PAD = 93,\n"
                + "                INDICE_HALO_PAD = 94,\n"
                + "                INDICE_SWEEP_PAD = 95,\n"
                + "                INDICE_ICE_RAIN = 96,\n"
                + "                INDICE_SOUNDTRACK = 97,\n"
                + "                INDICE_CRYSTAL = 98,\n"
                + "                INDICE_ATMOSPHERE = 99,\n"
                + "                INDICE_BRIGHTNESS = 100,\n"
                + "                INDICE_GOBLIN = 101,\n"
                + "                INDICE_ECHO_DROPS = 102,\n"
                + "                INDICE_STAR_THEME = 103,\n"
                + "                INDICE_SITAR = 104,\n"
                + "                INDICE_BANJO = 105,\n"
                + "                INDICE_SHAMISEN = 106,\n"
                + "                INDICE_KOTO = 107,\n"
                + "                INDICE_KALIMBA = 108,\n"
                + "                INDICE_BAGPIPE = 109,\n"
                + "                INDICE_FIDDLE = 110,\n"
                + "                INDICE_SHANAI = 111,\n"
                + "                INDICE_TINKLE_BELL = 112,\n"
                + "                INDICE_AGOGO = 113,\n"
                + "                INDICE_STEEL_DRUMS = 114,\n"
                + "                INDICE_WOODBLOCK = 115,\n"
                + "                INDICE_TAIKO = 116,\n"
                + "                INDICE_MELO_TOM_1 = 117,\n"
                + "                INDICE_SYNTH_DRUM = 118,\n"
                + "                INDICE_REVERSE_CYM_ = 119,\n"
                + "                INDICE_GT_FRETNOISE = 120,\n"
                + "                INDICE_BREATH_NOISE = 121,\n"
                + "                INDICE_SEASHORE = 122,\n"
                + "                INDICE_BIRD = 123,\n"
                + "                INDICE_TELEPHONE_1 = 124,\n"
                + "                INDICE_HELICOPTER = 125,\n"
                + "                INDICE_APPLAUSE = 126,\n"
                + "                INDICE_GUN_SHOT = 127,\n"
                + "                INDICE_SYNTHBASS101 = 128,\n"
                + "                INDICE_TROMBONE_2 = 129,\n"
                + "                INDICE_FR_HORN_2 = 130,\n"
                + "                INDICE_SQUARE = 131,\n"
                + "                INDICE_SAW = 132,\n"
                + "                INDICE_SYN_MALLET = 133,\n"
                + "                INDICE_ECHO_BELL = 134,\n"
                + "                INDICE_SITAR_2 = 135,\n"
                + "                INDICE_GT_CUT_NOISE = 136,\n"
                + "                INDICE_FL_KEY_CLICK = 137,\n"
                + "                INDICE_RAIN = 138,\n"
                + "                INDICE_DOG = 139,\n"
                + "                INDICE_TELEPHONE_2 = 140,\n"
                + "                INDICE_CAR_ENGINE = 141,\n"
                + "                INDICE_LAUGHING = 142,\n"
                + "                INDICE_MACHINE_GUN = 143,\n"
                + "                INDICE_ECHO_PAN = 144,\n"
                + "                INDICE_STRING_SLAP = 145,\n"
                + "                INDICE_THUNDER = 146,\n"
                + "                INDICE_HORSE_GALLOP = 147,\n"
                + "                INDICE_DOORCREAKING = 148,\n"
                + "                INDICE_CAR_STOP = 149,\n"
                + "                INDICE_SCREAMING = 150,\n"
                + "                INDICE_LASERGUN = 151,\n"
                + "                INDICE_WIND = 152,\n"
                + "                INDICE_BIRD_2 = 153,\n"
                + "                INDICE_DOOR = 154,\n"
                + "                INDICE_CAR_PASS = 155,\n"
                + "                INDICE_PUNCH = 156,\n"
                + "                INDICE_EXPLOSION = 157,\n"
                + "                INDICE_STREAM = 158,\n"
                + "                INDICE_SCRATCH = 159,\n"
                + "                INDICE_CAR_CRASH = 160,\n"
                + "                INDICE_HEART_BEAT = 161,\n"
                + "                INDICE_BUBBLE = 162,\n"
                + "                INDICE_WIND_CHIMES = 163,\n"
                + "                INDICE_SIREN = 164,\n"
                + "                INDICE_FOOTSTEPS = 165,\n"
                + "                INDICE_TRAIN = 166,\n"
                + "                INDICE_JETPLANE = 167,\n"
                + "                INDICE_PIANO_1$ = 168,\n"
                + "                INDICE_PIANO_2$ = 169,\n"
                + "                INDICE_PIANO_3$ = 170,\n"
                + "                INDICE_HONKY_TONK$ = 171,\n"
                + "                INDICE_DETUNED_EP_1 = 172,\n"
                + "                INDICE_DETUNED_EP_2 = 173,\n"
                + "                INDICE_COUPLED_HPS_ = 174,\n"
                + "                INDICE_VIBRAPHONE$ = 175,\n"
                + "                INDICE_MARIMBA$ = 176,\n"
                + "                INDICE_CHURCH_BELL = 177,\n"
                + "                INDICE_DETUNED_OR_1 = 178,\n"
                + "                INDICE_DETUNED_OR_2 = 179,\n"
                + "                INDICE_CHURCH_ORG_2 = 180,\n"
                + "                INDICE_ACCORDION_IT = 181,\n"
                + "                INDICE_UKULELE = 182,\n"
                + "                INDICE_12_STR_GT = 183,\n"
                + "                INDICE_HAWAIIAN_GT_ = 184,\n"
                + "                INDICE_CHORUS_GT_ = 185,\n"
                + "                INDICE_FUNK_GT_ = 186,\n"
                + "                INDICE_FEEDBACK_GT_ = 187,\n"
                + "                INDICE_GT_FEEDBACK = 188,\n"
                + "                INDICE_SYNTH_BASS_3 = 189,\n"
                + "                INDICE_SYNTH_BASS_4 = 190,\n"
                + "                INDICE_SLOW_VIOLIN = 191,\n"
                + "                INDICE_ORCHESTRA = 192,\n"
                + "                INDICE_SYN_STRINGS3 = 193,\n"
                + "                INDICE_BRASS_2 = 194,\n"
                + "                INDICE_SYNTH_BRASS3 = 195,\n"
                + "                INDICE_SYNTH_BRASS4 = 196,\n"
                + "                INDICE_SINE_WAVE = 197,\n"
                + "                INDICE_DOCTOR_SOLO = 198,\n"
                + "                INDICE_TAISHO_KOTO = 199,\n"
                + "                INDICE_CASTANETS = 200,\n"
                + "                INDICE_CONCERT_BD = 201,\n"
                + "                INDICE_MELO_TOM_2 = 202,\n"
                + "                INDICE_808_TOM = 203,\n"
                + "                INDICE_STARSHIP = 204,\n"
                + "                INDICE_CARILLON = 205,\n"
                + "                INDICE_ELEC_PERC_ = 206,\n"
                + "                INDICE_BURST_NOISE = 207,\n"
                + "                INDICE_PIANO_1D = 208,\n"
                + "                INDICE_E_PIANO_1V = 209,\n"
                + "                INDICE_E_PIANO_2V = 210,\n"
                + "                INDICE_HARPSICHORD$ = 211,\n"
                + "                INDICE_60_S_ORGAN_1 = 212,\n"
                + "                INDICE_CHURCH_ORG_3 = 213,\n"
                + "                INDICE_NYLON_GT_O = 214,\n"
                + "                INDICE_MANDOLIN = 215,\n"
                + "                INDICE_FUNK_GT_2 = 216,\n"
                + "                INDICE_RUBBER_BASS = 217,\n"
                + "                INDICE_ANALOGBRASS1 = 218,\n"
                + "                INDICE_ANALOGBRASS2 = 219,\n"
                + "                INDICE_60_S_E_PIANO = 220,\n"
                + "                INDICE_HARPSI_O = 221,\n"
                + "                INDICE_ORGAN_4 = 222,\n"
                + "                INDICE_ORGAN_5 = 223,\n"
                + "                INDICE_NYLON_GT_2 = 224,\n"
                + "                INDICE_CHOIR_AAHS_2 = 225,\n"
                + "                INDICE_STANDARD = 226,\n"
                + "                INDICE_ROOM = 227,\n"
                + "                INDICE_POWER = 228,\n"
                + "                INDICE_ELECTRONIC = 229,\n"
                + "                INDICE_TR_808 = 230,\n"
                + "                INDICE_JAZZ = 231,\n"
                + "                INDICE_BRUSH = 232,\n"
                + "                INDICE_ORCHESTRA$ = 233,\n"
                + "                INDICE_SFX = 234;";
        String[] instrumentos = Instrumentos.split("\n");
        System.out.println("switch(Instrumento){");
        for (int i = 0; i < instrumentos.length; i++) {
            instrumentos[i] = instrumentos[i].trim();
            instrumentos[i] = instrumentos[i].substring(0, instrumentos[i].indexOf(" "));
            System.out.print("case ");
            System.out.print(instrumentos[i]);
            System.out.println(":");
            System.out.println("return " + instrumentos[i].replace("INDICE", "INSTRUMENTO") + ";");
        }
        System.out.println("}");
    }

    private void GenerarCódigoParaConstantesDeInstrumentos() throws MidiUnavailableException {
        System.out.println("public final static int ");
        for (Object[] TablaInstrumento : TablaInstrumentos()) {
            int banco = ((int) TablaInstrumento[1]);
            int programa = ((int) TablaInstrumento[2]);
            int key = banco << 16 | programa;
            System.out.println(
                    " "
                    + TablaInstrumento[0].toString().toUpperCase()
                            .replace("'", "_")
                            .replace(" ", "_")
                            .replace("-", "_")
                            .replace(".", "_")
                            .replace("&", "N")
                            .replace("__", "_")
                    + " = " + key + ","
            );
        }
        System.out.println();
        System.out.println("public final static int ");
        int i = 0;
        for (Object[] TablaInstrumento : TablaInstrumentos()) {
            int banco = ((int) TablaInstrumento[1]);
            int programa = ((int) TablaInstrumento[2]);
            int key = banco << 16 | programa;
            System.out.println(
                    "INDICE_"
                    + TablaInstrumento[0].toString().toUpperCase()
                            .replace("'", "_")
                            .replace(" ", "_")
                            .replace("-", "_")
                            .replace(".", "_")
                            .replace("&", "N")
                            .replace("__", "_")
                    + " = " + (i++) + ","
            );
        }
    }

    class Probar {

        void Piratas() {
            int octava = 5;
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "FA", 500, -1);
            Reproducir((octava + 1) + "FA", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir(octava + "LA", 1100, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir(octava + "LA", 1100, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SI", 1100, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "LA", 1100, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "FA", 500, -1);
            Reproducir((octava + 1) + "FA", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir(octava + "LA", 1100, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir(octava + "SOL", 700, -1);

            Reproducir(octava + "SOL", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir((octava + 1) + "FA", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "FA", 1500, -1);

            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "FA", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "MI", 700, -1);

            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "FA", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "LA", 1100, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir((octava + 1) + "DO", 1100, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "RE", 1100, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir(octava + "SI", 1100, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "LA", 1100, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir((octava + 1) + "DO", 1100, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir(octava + "LA", 1100, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "FA", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "DO", 1100, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "LA", 1100, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "FA", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "RE", 1100, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "DO", 1100, -1);
            Reproducir((octava + 1) + "DO", 1100, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "LA", 1100, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "FA", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "MI", 700, -1);

            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "RE", 1100, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "SI", 1100, -1);
            Reproducir((octava + 1) + "RE", 1700, -1);
            Descansar(1000);
        }

        void CanCan_Garlop_Infernal() {
            int octava = 4;
            Reproducir(octava + "RE", 500, -1);
            Reproducir(octava + "RE", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "RE", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "RE", 700, -1);

            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "RE", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "RE", 700, -1);

            Reproducir(octava + "RE", 500, -1);
            Reproducir(octava + "RE", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "RE", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "RE", 500, -1);
            Reproducir(octava + "RE", 500, -1);
            Reproducir(octava + "RE", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "RE", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "RE", 700, -1);
            Descansar(1000);
        }

        void Lillium() {
            int octava = 5;
            Reproducir(octava + "FA#", 500, -1);
            Reproducir((octava + 1) + "DO#", 500, -1);
            Reproducir(octava + "SOL#", 1100, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "LA", 1100, -1);

            Reproducir(octava + "FA#", 500, -1);
            Reproducir((octava + 1) + "DO#", 500, -1);
            Reproducir(octava + "SOL#", 1100, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "LA", 1100, -1);

            Reproducir(octava + "FA#", 500, -1);
            Reproducir((octava + 1) + "DO#", 500, -1);
            Reproducir(octava + "SOL#", 1100, -1);

            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "FA#", 1100, -1);
            Reproducir(octava + "SOL#", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "RE", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "FA#", 500, -1);
            Reproducir(octava + "RE", 500, -1);
            Reproducir(octava + "DO", 500, -1);
            Reproducir(octava + "RE", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "FA#", 500, -1);
            Reproducir(octava + "SOL#", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SOL#", 500, -1);

            Reproducir(octava + "FA#", 500, -1);
            Reproducir((octava + 1) + "DO#", 500, -1);
            Reproducir(octava + "SOL#", 1100, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "LA", 1100, -1);

            Reproducir(octava + "FA#", 500, -1);
            Reproducir((octava + 1) + "DO#", 500, -1);
            Reproducir(octava + "SOL#", 1100, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "LA", 1100, -1);

            Reproducir(octava + "FA#", 500, -1);
            Reproducir((octava + 1) + "DO#", 500, -1);
            Reproducir(octava + "SOL#", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "FA#", 500, -1);
            Reproducir(octava + "SOL#", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "RE", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "FA#", 500, -1);
            Reproducir(octava + "RE", 500, -1);
            Reproducir(octava + "DO", 500, -1);
            Reproducir(octava + "RE", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SOL#", 500, -1);
            Reproducir(octava + "FA#", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "FA#", 500, -1);
            Reproducir(octava + "LA#", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir((octava + 1) + "DO#", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SOL#", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "DO#", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir((octava + 1) + "DO#", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SOL#", 500, -1);
            Reproducir(octava + "FA#", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "LA#", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir((octava + 1) + "DO#", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SOL#", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "DO#", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir(octava + "LA", 1100, -1);

            Reproducir(octava + "FA#", 500, -1);
            Reproducir((octava + 1) + "DO#", 500, -1);
            Reproducir(octava + "SOL#", 500, -1);
            Reproducir(octava + "FA", 1100, -1);
            Reproducir(octava + "FA#", 1100, -1);
            Descansar(1000);
        }

        void BrillaBrillaEstrellita() {
            int octava = 5;
            Reproducir(octava + "DO", 500, -1);
            Reproducir(octava + "DO", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SOL", 700, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "RE", 500, -1);
            Reproducir(octava + "RE", 500, -1);
            Reproducir(octava + "DO", 700, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "RE", 700, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "RE", 700, -1);
            Reproducir(octava + "DO", 500, -1);
            Reproducir(octava + "DO", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SOL", 700, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "RE", 500, -1);
            Reproducir(octava + "RE", 500, -1);
            Reproducir(octava + "DO", 700, -1);
            Descansar(1000);
        }

        void ChavoDel8() {
            int octava = 5;
            Reproducir(octava + "SOL", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir(octava + "SOL", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir((octava + 1) + "MI", 400, -1);
            Reproducir((octava + 1) + "RE", 400, -1);
            Reproducir(octava + "SI", 400, -1);
            Reproducir(octava + "LA", 400, -1);
            Reproducir(octava + "SOL", 400, -1);
            Reproducir(octava + "FA", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir(octava + "FA", 400, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "SOL", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir(octava + "SOL", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir((octava + 1) + "MI", 400, -1);
            Reproducir((octava + 1) + "RE", 400, -1);
            Reproducir(octava + "SI", 400, -1);
            Reproducir(octava + "LA", 400, -1);
            Reproducir(octava + "SOL", 400, -1);
            Reproducir(octava + "FA#", 400, -1);
            Reproducir(octava + "SOL#", 400, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir((octava + 1) + "DO", 400, -1);
            Reproducir(octava + "LA", 400, -1);
            Reproducir(octava + "LA", 400, -1);
            Reproducir(octava + "LA", 400, -1);
            Reproducir((octava + 1) + "RE", 400, -1);
            Reproducir(octava + "SI", 400, -1);
            Reproducir(octava + "SI", 400, -1);
            Reproducir(octava + "SI", 400, -1);
            Reproducir((octava + 1) + "RE", 400, -1);
            Reproducir(octava + "SI", 400, -1);
            Reproducir((octava + 1) + "RE", 400, -1);
            Reproducir(octava + "SI", 400, -1);
            Reproducir((octava + 1) + "MI", 400, -1);
            Reproducir((octava + 1) + "DO", 400, -1);
            Reproducir((octava + 1) + "MI", 400, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir(octava + "SOL", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir(octava + "SOL", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir((octava + 1) + "MI", 400, -1);
            Reproducir((octava + 1) + "RE", 400, -1);
            Reproducir(octava + "SI", 400, -1);
            Reproducir(octava + "LA", 400, -1);
            Reproducir(octava + "SOL", 400, -1);
            Reproducir(octava + "FA", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir(octava + "FA", 400, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "SOL", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir(octava + "SOL", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir((octava + 1) + "MI", 400, -1);
            Reproducir((octava + 1) + "RE", 400, -1);
            Reproducir(octava + "SI", 400, -1);
            Reproducir(octava + "LA", 400, -1);
            Reproducir(octava + "SOL", 400, -1);
            Reproducir(octava + "FA#", 400, -1);
            Reproducir(octava + "SOL#", 400, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir((octava + 1) + "DO", 400, -1);
            Reproducir(octava + "LA", 400, -1);
            Reproducir(octava + "LA", 400, -1);
            Reproducir(octava + "LA", 400, -1);
            Reproducir((octava + 1) + "RE", 400, -1);
            Reproducir(octava + "SI", 400, -1);
            Reproducir(octava + "SI", 400, -1);
            Reproducir(octava + "SI", 400, -1);
            Reproducir((octava + 1) + "RE", 400, -1);
            Reproducir(octava + "SI", 400, -1);
            Reproducir((octava + 1) + "RE", 400, -1);
            Reproducir(octava + "SI", 400, -1);
            Reproducir((octava + 1) + "MI", 400, -1);
            Reproducir((octava + 1) + "DO", 400, -1);
            Reproducir((octava + 1) + "MI", 400, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir(octava + "SOL", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir(octava + "SOL", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir((octava + 1) + "MI", 400, -1);
            Reproducir((octava + 1) + "RE", 400, -1);
            Reproducir(octava + "SI", 400, -1);
            Reproducir(octava + "LA", 400, -1);
            Reproducir(octava + "SOL", 400, -1);
            Reproducir(octava + "FA", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir(octava + "FA", 400, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "SOL", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir(octava + "SOL", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir((octava + 1) + "MI", 400, -1);
            Reproducir((octava + 1) + "RE", 400, -1);
            Reproducir(octava + "SI", 400, -1);
            Reproducir(octava + "LA", 400, -1);
            Reproducir(octava + "SOL", 400, -1);
            Reproducir(octava + "FA", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir(octava + "FA", 400, -1);
            Reproducir(octava + "SOL", 500, -1);
            Descansar(1000);
        }

        void MarioBrossFinal() {
            int octava = 4;
            Reproducir(octava + "SOL", 400, -1);
            Reproducir(octava + "FA#", 400, -1);
            Reproducir(octava + "SOL", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir(octava + "FA", 400, -1);
            Reproducir(octava + "FA#", 400, -1);
            Reproducir(octava + "SOL", 400, -1);
            Reproducir((octava + 1) + "DO", 400, -1);
            Reproducir((octava + 1) + "MI", 400, -1);
            Reproducir((octava + 1) + "RE", 400, -1);
            Reproducir((octava + 1) + "MI", 400, -1);
            Reproducir((octava + 1) + "FA", 400, -1);
            Reproducir(octava + "SI", 400, -1);
            Reproducir((octava + 1) + "RE", 400, -1);
            Reproducir((octava + 1) + "DO", 400, -1);
            Reproducir(octava + "SOL", 400, -1);
            Reproducir(octava + "FA#", 400, -1);
            Reproducir(octava + "SOL", 400, -1);
            Reproducir(octava + "MI", 400, -1);
            Reproducir(octava + "FA", 400, -1);
            Reproducir(octava + "FA#", 400, -1);
            Reproducir(octava + "SOL", 400, -1);
            Reproducir((octava + 1) + "DO", 400, -1);
            Reproducir((octava + 1) + "MI", 400, -1);
            Reproducir((octava + 1) + "MI", 400, -1);
            Reproducir((octava + 1) + "RE", 400, -1);
            Reproducir((octava + 1) + "MI", 400, -1);
            Reproducir((octava + 1) + "FA", 400, -1);
            Reproducir(octava + "SI", 400, -1);
            Reproducir((octava + 1) + "RE", 400, -1);
            Reproducir((octava + 1) + "DO", 400, -1);
            Descansar(1000);
        }

        void AdornemosEstaNavidad() {
            int octava = 5;
            Reproducir((octava + 1) + "DO", 1100, -1);
            Reproducir(octava + "LA#", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "LA#", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "LA", 1100, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "FA", 1100, -1);

            Reproducir((octava + 1) + "DO", 1100, -1);
            Reproducir(octava + "LA#", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "LA#", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "LA", 1100, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "FA", 1100, -1);

            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "LA#", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "LA", 1100, -1);
            Reproducir(octava + "LA#", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "LA#", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir((octava + 1) + "DO", 1100, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "FA", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "DO", 1100, -1);

            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir(octava + "LA#", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "DO", 1100, -1);
            Reproducir(octava + "LA#", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "FA", 1100, -1);
            Descansar(1000);
        }

        void LaMañana() {
            int octava = 5;
            callback.Simple p0 = () -> {
                Reproducir(octava + "LA", 600, -1);
                Reproducir(octava + "SOL", 600, -1);
            };
            callback.Simple p3 = () -> {
                Reproducir(octava + "MI", 500, -1);
                Reproducir(octava + "RE", 500, -1);
                Reproducir(octava + "DO", 500, -1);
            };
            callback.Simple p1 = () -> {
                Reproducir(octava + "SOL", 500, -1);
                p3.run();
                Reproducir(octava + "RE", 500, -1);
                Reproducir(octava + "MI", 500, -1);
            };
            callback.Simple p2 = () -> {
                Reproducir(octava + "RE", 300, -1);
                Reproducir(octava + "MI", 300, -1);
                Reproducir(octava + "SOL", 500, -1);
                Reproducir(octava + "MI", 500, -1);
                Reproducir(octava + "SOL", 600, -1);
                Reproducir(octava + "LA", 600, -1);
                Reproducir(octava + "MI", 600, -1);
                p0.run();
            };
            callback.Simple p4 = () -> {
                p1.run();
                p1.run();
                p2.run();
            };
            callback.Simple p5 = () -> {
                p3.run();
                p1.run();
                p1.run();
                p2.run();
            };
            p4.run();
            p3.run();
            Reproducir(octava + "MI", 500, -1);
            p0.run();
            p4.run();
            p5.run();
            p1.run();
            p0.run();
            p5.run();
            p3.run();
            Descansar(1000);
        }

        void ElPuenteDeLondres() {
            int octava = 5;
            Reproducir((octava + 1) + "RE", 1100, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "RE", 1100, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir((octava + 1) + "RE", 1100, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir((octava + 1) + "DO", 1100, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "RE", 1100, -1);
            Reproducir((octava + 1) + "RE", 1100, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "RE", 1100, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir((octava + 1) + "RE", 1100, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir((octava + 1) + "DO", 1100, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "RE", 1100, -1);
            Reproducir(octava + "LA", 1100, -1);
            Reproducir((octava + 1) + "RE", 1100, -1);
            Reproducir(octava + "SI", 1100, -1);
            Reproducir(octava + "SOL", 500, -1);
            Descansar(1000);
        }

        void CanciónDeCuna_Lullaby() {
            int octava = 5;
            Reproducir(octava + "FA#", 500, -1);
            Reproducir(octava + "FA#", 500, -1);
            Reproducir(octava + "LA", 1100, -1);
            Reproducir(octava + "FA#", 500, -1);
            Reproducir(octava + "FA#", 500, -1);
            Reproducir(octava + "LA", 1100, -1);
            Reproducir(octava + "FA#", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "DO#", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "LA", 1100, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "FA#", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "FA#", 500, -1);
            Reproducir(octava + "SOL", 1100, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir((octava + 1) + "DO#", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir((octava + 1) + "DO#", 500, -1);
            Reproducir((octava + 1) + "RE", 1100, -1);
            Reproducir(octava + "RE", 500, -1);
            Reproducir(octava + "RE", 500, -1);
            Reproducir((octava + 1) + "RE", 1100, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "FA#", 500, -1);
            Reproducir(octava + "RE", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "LA", 1100, -1);
            Reproducir(octava + "RE", 500, -1);
            Reproducir(octava + "RE", 500, -1);
            Reproducir((octava + 1) + "RE", 1100, -1);
            Reproducir(octava + "SI", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "LA", 1100, -1);
            Reproducir(octava + "FA#", 500, -1);
            Reproducir(octava + "RE", 500, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "FA#", 500, -1);
            Reproducir(octava + "MI", 500, -1);
            Reproducir(octava + "DO#", 1100, -1);
            Descansar(1000);
        }

        void Tapión() {
            int octava = 5;
            callback.Simple p1 = () -> {
                Reproducir(octava + "MI", 500, 600);
                Reproducir(octava + "MI", 500, -1);
                Reproducir(octava + "LA", 1100, -1);
            };
            callback.Simple p2 = () -> {
                Reproducir((octava + 1) + "MI", 500, -1);
                Reproducir((octava + 1) + "RE", 500, -1);
                Reproducir((octava + 1) + "DO", 500, -1);
                Reproducir((octava + 1) + "RE", 1100, -1);
            };
            callback.Simple p3 = () -> {
                p1.run();
                p2.run();
            };
            callback.Simple p4 = () -> {
                Reproducir(octava + "LA", 500, -1);
                Reproducir(octava + "SI", 500, -1);
                Reproducir((octava + 1) + "DO", 1100, -1);
            };
            callback.Simple p5 = () -> {
                Reproducir((octava + 1) + "DO", 500, -1);
                Reproducir((octava + 1) + "RE", 500, -1);
                Reproducir((octava + 1) + "DO", 500, -1);
                Reproducir(octava + "SI", 1100, -1);
            };
            callback.Simple p6 = () -> {
                p4.run();
                p5.run();
            };
            callback.Simple p7 = () -> {
                Reproducir((octava + 1) + "DO", 500, -1);
                Reproducir(octava + "SI", 500, -1);
                Reproducir(octava + "LA", 500, -1);
                Reproducir(octava + "SOL", 500, -1);
                Reproducir(octava + "LA", 1100, -1);
            };
            callback.Simple p8 = () -> {
                p4.run();
                p7.run();
            };
            callback.Simple p9 = () -> {
                p1.run();
                Reproducir(octava + "MI", 500, -1);
                Reproducir(octava + "RE", 500, -1);
                Reproducir(octava + "MI", 500, -1);
                Reproducir(octava + "LA", 500, -1);
                p2.run();
                Reproducir(octava + "LA", 500, -1);
                Reproducir(octava + "SOL", 500, -1);
                Reproducir(octava + "LA", 500, -1);
                Reproducir((octava + 1) + "RE", 500, -1);
            };
            callback.Simple p10 = () -> {
                p3.run();
                p6.run();
            };
            callback.Simple p11 = () -> {
                p3.run();
                p8.run();
            };
            for (int i = 1; i <= 3; i++) {
                p10.run();
            }
            p11.run();
            p9.run();
            p4.run();
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            p5.run();
            p9.run();
            p8.run();
            p9.run();
            Reproducir(octava + "SOL", 500, -1);
            Reproducir(octava + "LA", 500, -1);
            Reproducir((octava + 1) + "DO", 1100, -1);
            p7.run();
            p10.run();
            p11.run();
            Descansar(1000);
        }

        void ParaElisa() {
            int octava = 3;
            callback.Simple p1 = () -> {
                Reproducir((octava + 1) + "MI", 500, -1);
                Reproducir((octava + 1) + "RE#", 500, -1);
                Reproducir((octava + 1) + "MI", 500, -1);
                Reproducir((octava + 1) + "RE#", 500, -1);
                Reproducir((octava + 1) + "MI", 500, -1);
                Reproducir(octava + "SI", 500, -1);
                Reproducir((octava + 1) + "RE", 500, -1);
                Reproducir((octava + 1) + "DO", 500, -1);
                Reproducir(octava + "LA", 1100, -1);
                Reproducir(octava + "DO", 500, -1);
                Reproducir(octava + "FA", 500, -1);
                Reproducir(octava + "LA", 500, -1);
                Reproducir(octava + "SI", 1100, -1);
                Reproducir(octava + "MI", 500, -1);
                Reproducir(octava + "FA#", 500, -1);
                Reproducir(octava + "SI", 500, -1);
            };
            for (int i = 1; i <= 4; i++) {
                p1.run();
                Reproducir((i == 4) ? octava + "LA" : (octava + 1) + "DO", 1100, -1);
            }
            Reproducir(octava + "SI", 500, -1);
            Reproducir((octava + 1) + "DO", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "MI", 1100, -1);
            Reproducir(octava + "SOL", 500, -1);
            Reproducir((octava + 1) + "FA", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "RE", 1100, -1);
            Reproducir(octava + "FA", 500, -1);
            Reproducir((octava + 1) + "MI", 500, -1);
            Reproducir((octava + 1) + "RE", 500, -1);
            Reproducir((octava + 1) + "DO", 1100, -1);
            for (int i = 0; i < 2; i++) {
                p1.run();
                Reproducir((octava + 1) + "DO", 1100, -1);
            }
            Descansar(1000);
        }

    }

    public interface Constantes {

        public final static int INSTRUMENTOS_SOPORTADOS = 225;

        public final static int PIANO_1 = 0,
                PIANO_2 = 1,
                PIANO_3 = 2,
                HONKY_TONK = 3,
                E_PIANO_1 = 4,
                E_PIANO_2 = 5,
                HARPSICHORD = 6,
                CLAV = 7,
                CELESTA = 8,
                GLOCKENSPIEL = 9,
                MUSIC_BOX = 10,
                VIBRAPHONE = 11,
                MARIMBA = 12,
                XYLOPHONE = 13,
                TUBULAR_BELL = 14,
                SANTUR = 15,
                ORGAN_1 = 16,
                ORGAN_2 = 17,
                ORGAN_3 = 18,
                CHURCH_ORG_1 = 19,
                REED_ORGAN = 20,
                ACCORDION_FR = 21,
                HARMONICA = 22,
                BANDONEON = 23,
                NYLON_STR_GT = 24,
                STEEL_STR_GT = 25,
                JAZZ_GT_ = 26,
                CLEAN_GT_ = 27,
                MUTED_GT_ = 28,
                OVERDRIVE_GT = 29,
                DISTORTIONGT = 30,
                GT_HARMONICS = 31,
                ACOUSTIC_BS = 32,
                FINGERED_BS = 33,
                PICKED_BS = 34,
                FRETLESS_BS = 35,
                SLAP_BASS_1 = 36,
                SLAP_BASS_2 = 37,
                SYNTH_BASS_1 = 38,
                SYNTH_BASS_2 = 39,
                VIOLIN = 40,
                VIOLA = 41,
                CELLO = 42,
                CONTRABASS = 43,
                TREMOLO_STR = 44,
                PIZZICATOSTR = 45,
                HARP = 46,
                TIMPANI = 47,
                STRINGS = 48,
                SLOW_STRINGS = 49,
                SYN_STRINGS1 = 50,
                SYN_STRINGS2 = 51,
                CHOIR_AAHS = 52,
                VOICE_OOHS = 53,
                SYNVOX = 54,
                ORCHESTRAHIT = 55,
                TRUMPET = 56,
                TROMBONE = 57,
                TUBA = 58,
                MUTEDTRUMPET = 59,
                FRENCH_HORNS = 60,
                BRASS_1 = 61,
                SYNTH_BRASS1 = 62,
                SYNTH_BRASS2 = 63,
                SOPRANO_SAX = 64,
                ALTO_SAX = 65,
                TENOR_SAX = 66,
                BARITONE_SAX = 67,
                OBOE = 68,
                ENGLISH_HORN = 69,
                BASSOON = 70,
                CLARINET = 71,
                PICCOLO = 72,
                FLUTE = 73,
                RECORDER = 74,
                PAN_FLUTE = 75,
                BOTTLE_BLOW = 76,
                SHAKUHACHI = 77,
                WHISTLE = 78,
                OCARINA = 79,
                SQUARE_WAVE = 80,
                SAW_WAVE = 81,
                SYN_CALLIOPE = 82,
                CHIFFER_LEAD = 83,
                CHARANG = 84,
                SOLO_VOX = 85,
                _5TH_SAW_WAVE = 86,
                BASS_N_LEAD = 87,
                FANTASIA = 88,
                WARM_PAD = 89,
                POLYSYNTH = 90,
                SPACE_VOICE = 91,
                BOWED_GLASS = 92,
                METAL_PAD = 93,
                HALO_PAD = 94,
                SWEEP_PAD = 95,
                ICE_RAIN = 96,
                SOUNDTRACK = 97,
                CRYSTAL = 98,
                ATMOSPHERE = 99,
                BRIGHTNESS = 100,
                GOBLIN = 101,
                ECHO_DROPS = 102,
                STAR_THEME = 103,
                SITAR = 104,
                BANJO = 105,
                SHAMISEN = 106,
                KOTO = 107,
                KALIMBA = 108,
                BAGPIPE = 109,
                FIDDLE = 110,
                SHANAI = 111,
                TINKLE_BELL = 112,
                AGOGO = 113,
                STEEL_DRUMS = 114,
                WOODBLOCK = 115,
                TAIKO = 116,
                MELO_TOM_1 = 117,
                SYNTH_DRUM = 118,
                REVERSE_CYM_ = 119,
                GT_FRETNOISE = 120,
                BREATH_NOISE = 121,
                SEASHORE = 122,
                BIRD = 123,
                TELEPHONE_1 = 124,
                HELICOPTER = 125,
                APPLAUSE = 126,
                GUN_SHOT = 127,
                SYNTHBASS101 = 8388646,
                TROMBONE_2 = 8388665,
                FR_HORN_2 = 8388668,
                SQUARE = 8388688,
                SAW = 8388689,
                SYN_MALLET = 8388706,
                ECHO_BELL = 8388710,
                SITAR_2 = 8388712,
                GT_CUT_NOISE = 8388728,
                FL_KEY_CLICK = 8388729,
                RAIN = 8388730,
                DOG = 8388731,
                TELEPHONE_2 = 8388732,
                CAR_ENGINE = 8388733,
                LAUGHING = 8388734,
                MACHINE_GUN = 8388735,
                ECHO_PAN = 16777318,
                STRING_SLAP = 16777336,
                THUNDER = 16777338,
                HORSE_GALLOP = 16777339,
                DOORCREAKING = 16777340,
                CAR_STOP = 16777341,
                SCREAMING = 16777342,
                LASERGUN = 16777343,
                WIND = 25165946,
                BIRD_2 = 25165947,
                DOOR = 25165948,
                CAR_PASS = 25165949,
                PUNCH = 25165950,
                EXPLOSION = 25165951,
                STREAM = 33554554,
                SCRATCH = 33554556,
                CAR_CRASH = 33554557,
                HEART_BEAT = 33554558,
                BUBBLE = 41943162,
                WIND_CHIMES = 41943164,
                SIREN = 41943165,
                FOOTSTEPS = 41943166,
                TRAIN = 50331773,
                JETPLANE = 58720381,
                PIANO_1$ = 67108864,
                PIANO_2$ = 67108865,
                PIANO_3$ = 67108866,
                HONKY_TONK$ = 67108867,
                DETUNED_EP_1 = 67108868,
                DETUNED_EP_2 = 67108869,
                COUPLED_HPS_ = 67108870,
                VIBRAPHONE$ = 67108875,
                MARIMBA$ = 67108876,
                CHURCH_BELL = 67108878,
                DETUNED_OR_1 = 67108880,
                DETUNED_OR_2 = 67108881,
                CHURCH_ORG_2 = 67108883,
                ACCORDION_IT = 67108885,
                UKULELE = 67108888,
                _12_STR_GT = 67108889,
                HAWAIIAN_GT_ = 67108890,
                CHORUS_GT_ = 67108891,
                FUNK_GT_ = 67108892,
                FEEDBACK_GT = 67108894,
                GT_FEEDBACK = 67108895,
                SYNTH_BASS_3 = 67108902,
                SYNTH_BASS_4 = 67108903,
                SLOW_VIOLIN = 67108904,
                ORCHESTRA = 67108912,
                SYN_STRINGS3 = 67108914,
                BRASS_2 = 67108925,
                SYNTH_BRASS3 = 67108926,
                SYNTH_BRASS4 = 67108927,
                SINE_WAVE = 67108944,
                DOCTOR_SOLO = 67108945,
                TAISHO_KOTO = 67108971,
                CASTANETS = 67108979,
                CONCERT_BD = 67108980,
                MELO_TOM_2 = 67108981,
                _808_TOM = 67108982,
                STARSHIP = 67108989,
                CARILLON = 75497486,
                ELEC_PERC_ = 75497590,
                BURST_NOISE = 75497597,
                PIANO_1D = 134217728,
                E_PIANO_1V = 134217732,
                E_PIANO_2V = 134217733,
                HARPSICHORD$ = 134217734,
                _60_S_ORGAN_1 = 134217744,
                CHURCH_ORG_3 = 134217747,
                NYLON_GT_O = 134217752,
                MANDOLIN = 134217753,
                FUNK_GT_2 = 134217756,
                RUBBER_BASS = 134217767,
                ANALOGBRASS1 = 134217790,
                ANALOGBRASS2 = 134217791,
                _60_S_E_PIANO = 201326596,
                HARPSI_O = 201326598,
                ORGAN_4 = 268435472,
                ORGAN_5 = 268435473,
                NYLON_GT_2 = 268435480,
                CHOIR_AAHS_2 = 268435508;

        public final static int INDICE_PIANO_1 = 0,
                INDICE_PIANO_2 = 1,
                INDICE_PIANO_3 = 2,
                INDICE_HONKY_TONK = 3,
                INDICE_E_PIANO_1 = 4,
                INDICE_E_PIANO_2 = 5,
                INDICE_HARPSICHORD = 6,
                INDICE_CLAV_ = 7,
                INDICE_CELESTA = 8,
                INDICE_GLOCKENSPIEL = 9,
                INDICE_MUSIC_BOX = 10,
                INDICE_VIBRAPHONE = 11,
                INDICE_MARIMBA = 12,
                INDICE_XYLOPHONE = 13,
                INDICE_TUBULAR_BELL = 14,
                INDICE_SANTUR = 15,
                INDICE_ORGAN_1 = 16,
                INDICE_ORGAN_2 = 17,
                INDICE_ORGAN_3 = 18,
                INDICE_CHURCH_ORG_1 = 19,
                INDICE_REED_ORGAN = 20,
                INDICE_ACCORDION_FR = 21,
                INDICE_HARMONICA = 22,
                INDICE_BANDONEON = 23,
                INDICE_NYLON_STR_GT = 24,
                INDICE_STEEL_STR_GT = 25,
                INDICE_JAZZ_GT_ = 26,
                INDICE_CLEAN_GT_ = 27,
                INDICE_MUTED_GT_ = 28,
                INDICE_OVERDRIVE_GT = 29,
                INDICE_DISTORTIONGT = 30,
                INDICE_GT_HARMONICS = 31,
                INDICE_ACOUSTIC_BS_ = 32,
                INDICE_FINGERED_BS_ = 33,
                INDICE_PICKED_BS_ = 34,
                INDICE_FRETLESS_BS_ = 35,
                INDICE_SLAP_BASS_1 = 36,
                INDICE_SLAP_BASS_2 = 37,
                INDICE_SYNTH_BASS_1 = 38,
                INDICE_SYNTH_BASS_2 = 39,
                INDICE_VIOLIN = 40,
                INDICE_VIOLA = 41,
                INDICE_CELLO = 42,
                INDICE_CONTRABASS = 43,
                INDICE_TREMOLO_STR = 44,
                INDICE_PIZZICATOSTR = 45,
                INDICE_HARP = 46,
                INDICE_TIMPANI = 47,
                INDICE_STRINGS = 48,
                INDICE_SLOW_STRINGS = 49,
                INDICE_SYN_STRINGS1 = 50,
                INDICE_SYN_STRINGS2 = 51,
                INDICE_CHOIR_AAHS = 52,
                INDICE_VOICE_OOHS = 53,
                INDICE_SYNVOX = 54,
                INDICE_ORCHESTRAHIT = 55,
                INDICE_TRUMPET = 56,
                INDICE_TROMBONE = 57,
                INDICE_TUBA = 58,
                INDICE_MUTEDTRUMPET = 59,
                INDICE_FRENCH_HORNS = 60,
                INDICE_BRASS_1 = 61,
                INDICE_SYNTH_BRASS1 = 62,
                INDICE_SYNTH_BRASS2 = 63,
                INDICE_SOPRANO_SAX = 64,
                INDICE_ALTO_SAX = 65,
                INDICE_TENOR_SAX = 66,
                INDICE_BARITONE_SAX = 67,
                INDICE_OBOE = 68,
                INDICE_ENGLISH_HORN = 69,
                INDICE_BASSOON = 70,
                INDICE_CLARINET = 71,
                INDICE_PICCOLO = 72,
                INDICE_FLUTE = 73,
                INDICE_RECORDER = 74,
                INDICE_PAN_FLUTE = 75,
                INDICE_BOTTLE_BLOW = 76,
                INDICE_SHAKUHACHI = 77,
                INDICE_WHISTLE = 78,
                INDICE_OCARINA = 79,
                INDICE_SQUARE_WAVE = 80,
                INDICE_SAW_WAVE = 81,
                INDICE_SYN_CALLIOPE = 82,
                INDICE_CHIFFER_LEAD = 83,
                INDICE_CHARANG = 84,
                INDICE_SOLO_VOX = 85,
                INDICE_5TH_SAW_WAVE = 86,
                INDICE_BASS_N_LEAD = 87,
                INDICE_FANTASIA = 88,
                INDICE_WARM_PAD = 89,
                INDICE_POLYSYNTH = 90,
                INDICE_SPACE_VOICE = 91,
                INDICE_BOWED_GLASS = 92,
                INDICE_METAL_PAD = 93,
                INDICE_HALO_PAD = 94,
                INDICE_SWEEP_PAD = 95,
                INDICE_ICE_RAIN = 96,
                INDICE_SOUNDTRACK = 97,
                INDICE_CRYSTAL = 98,
                INDICE_ATMOSPHERE = 99,
                INDICE_BRIGHTNESS = 100,
                INDICE_GOBLIN = 101,
                INDICE_ECHO_DROPS = 102,
                INDICE_STAR_THEME = 103,
                INDICE_SITAR = 104,
                INDICE_BANJO = 105,
                INDICE_SHAMISEN = 106,
                INDICE_KOTO = 107,
                INDICE_KALIMBA = 108,
                INDICE_BAGPIPE = 109,
                INDICE_FIDDLE = 110,
                INDICE_SHANAI = 111,
                INDICE_TINKLE_BELL = 112,
                INDICE_AGOGO = 113,
                INDICE_STEEL_DRUMS = 114,
                INDICE_WOODBLOCK = 115,
                INDICE_TAIKO = 116,
                INDICE_MELO_TOM_1 = 117,
                INDICE_SYNTH_DRUM = 118,
                INDICE_REVERSE_CYM_ = 119,
                INDICE_GT_FRETNOISE = 120,
                INDICE_BREATH_NOISE = 121,
                INDICE_SEASHORE = 122,
                INDICE_BIRD = 123,
                INDICE_TELEPHONE_1 = 124,
                INDICE_HELICOPTER = 125,
                INDICE_APPLAUSE = 126,
                INDICE_GUN_SHOT = 127,
                INDICE_SYNTHBASS101 = 128,
                INDICE_TROMBONE_2 = 129,
                INDICE_FR_HORN_2 = 130,
                INDICE_SQUARE = 131,
                INDICE_SAW = 132,
                INDICE_SYN_MALLET = 133,
                INDICE_ECHO_BELL = 134,
                INDICE_SITAR_2 = 135,
                INDICE_GT_CUT_NOISE = 136,
                INDICE_FL_KEY_CLICK = 137,
                INDICE_RAIN = 138,
                INDICE_DOG = 139,
                INDICE_TELEPHONE_2 = 140,
                INDICE_CAR_ENGINE = 141,
                INDICE_LAUGHING = 142,
                INDICE_MACHINE_GUN = 143,
                INDICE_ECHO_PAN = 144,
                INDICE_STRING_SLAP = 145,
                INDICE_THUNDER = 146,
                INDICE_HORSE_GALLOP = 147,
                INDICE_DOORCREAKING = 148,
                INDICE_CAR_STOP = 149,
                INDICE_SCREAMING = 150,
                INDICE_LASERGUN = 151,
                INDICE_WIND = 152,
                INDICE_BIRD_2 = 153,
                INDICE_DOOR = 154,
                INDICE_CAR_PASS = 155,
                INDICE_PUNCH = 156,
                INDICE_EXPLOSION = 157,
                INDICE_STREAM = 158,
                INDICE_SCRATCH = 159,
                INDICE_CAR_CRASH = 160,
                INDICE_HEART_BEAT = 161,
                INDICE_BUBBLE = 162,
                INDICE_WIND_CHIMES = 163,
                INDICE_SIREN = 164,
                INDICE_FOOTSTEPS = 165,
                INDICE_TRAIN = 166,
                INDICE_JETPLANE = 167,
                INDICE_PIANO_1$ = 168,
                INDICE_PIANO_2$ = 169,
                INDICE_PIANO_3$ = 170,
                INDICE_HONKY_TONK$ = 171,
                INDICE_DETUNED_EP_1 = 172,
                INDICE_DETUNED_EP_2 = 173,
                INDICE_COUPLED_HPS_ = 174,
                INDICE_VIBRAPHONE$ = 175,
                INDICE_MARIMBA$ = 176,
                INDICE_CHURCH_BELL = 177,
                INDICE_DETUNED_OR_1 = 178,
                INDICE_DETUNED_OR_2 = 179,
                INDICE_CHURCH_ORG_2 = 180,
                INDICE_ACCORDION_IT = 181,
                INDICE_UKULELE = 182,
                INDICE_12_STR_GT = 183,
                INDICE_HAWAIIAN_GT_ = 184,
                INDICE_CHORUS_GT_ = 185,
                INDICE_FUNK_GT_ = 186,
                INDICE_FEEDBACK_GT_ = 187,
                INDICE_GT_FEEDBACK = 188,
                INDICE_SYNTH_BASS_3 = 189,
                INDICE_SYNTH_BASS_4 = 190,
                INDICE_SLOW_VIOLIN = 191,
                INDICE_ORCHESTRA = 192,
                INDICE_SYN_STRINGS3 = 193,
                INDICE_BRASS_2 = 194,
                INDICE_SYNTH_BRASS3 = 195,
                INDICE_SYNTH_BRASS4 = 196,
                INDICE_SINE_WAVE = 197,
                INDICE_DOCTOR_SOLO = 198,
                INDICE_TAISHO_KOTO = 199,
                INDICE_CASTANETS = 200,
                INDICE_CONCERT_BD = 201,
                INDICE_MELO_TOM_2 = 202,
                INDICE_808_TOM = 203,
                INDICE_STARSHIP = 204,
                INDICE_CARILLON = 205,
                INDICE_ELEC_PERC_ = 206,
                INDICE_BURST_NOISE = 207,
                INDICE_PIANO_1D = 208,
                INDICE_E_PIANO_1V = 209,
                INDICE_E_PIANO_2V = 210,
                INDICE_HARPSICHORD$ = 211,
                INDICE_60_S_ORGAN_1 = 212,
                INDICE_CHURCH_ORG_3 = 213,
                INDICE_NYLON_GT_O = 214,
                INDICE_MANDOLIN = 215,
                INDICE_FUNK_GT_2 = 216,
                INDICE_RUBBER_BASS = 217,
                INDICE_ANALOGBRASS1 = 218,
                INDICE_ANALOGBRASS2 = 219,
                INDICE_60_S_E_PIANO = 220,
                INDICE_HARPSI_O = 221,
                INDICE_ORGAN_4 = 222,
                INDICE_ORGAN_5 = 223,
                INDICE_NYLON_GT_2 = 224,
                INDICE_CHOIR_AAHS_2 = 225;

    }

    public interface HerramientasMIDI extends Constantes {

        public static int CalcularByte_Nota(String Nota) {
            int Octava = 0;
            int i;
            try {
                i = 2;
                Octava = Integer.parseInt(Nota.substring(0, i));
            } catch (Exception e) {
                try {
                    i = 1;
                    Octava = Integer.parseInt(Nota.substring(0, i));
                } catch (Exception e2) {
                    i = 0;
                }
            }
            int nota = ValorColumnaNota(Nota.substring(i)) + 12 * Octava;
            return nota;
        }

        public static int ValorColumnaNota(String nota) {
            switch (nota.toUpperCase()) {
                case "C":
                case "DO":
                    return 0;
                case "C#":
                case "DO#":
                    return 1;
                case "D":
                case "RE":
                    return 2;
                case "D#":
                case "RE#":
                    return 3;
                case "E":
                case "MI":
                    return 4;
                case "F":
                case "FA":
                    return 5;
                case "F#":
                case "FA#":
                    return 6;
                case "G":
                case "SOL":
                    return 7;
                case "G#":
                case "SOL#":
                    return 8;
                case "A":
                case "LA":
                    return 9;
                case "A#":
                case "LA#":
                    return 10;
                case "B":
                case "SI":
                    return 11;
                default:
                    throw new RuntimeException("La nota no se reconoce: " + nota);
            }
        }

        public static String ConvertirNotaString(int nota) {
            int octava = nota / 12;
            int n = nota % 12;
            switch (n) {
                case 0:
                    return octava + "DO";
                case 1:
                    return octava + "DO#";
                case 2:
                    return octava + "RE";
                case 3:
                    return octava + "RE#";
                case 4:
                    return octava + "MI";
                case 5:
                    return octava + "FA";
                case 6:
                    return octava + "FA#";
                case 7:
                    return octava + "SOL";
                case 8:
                    return octava + "SOL#";
                case 9:
                    return octava + "LA";
                case 10:
                    return octava + "LA#";
                case 11:
                    return octava + "SI";
                default:
                    return null;
            }
        }

        static String ConvertirInstrumentoAString(int llave) {
            try {
                return TablaInstrumentos_Obtener1(HerramientasMIDI.ObtenerIndice(llave))[0].toString();
            } catch (MidiUnavailableException ex) {
                return null;
            }
        }

        static int ObtenerLlave(int Indice) {
            switch (Indice) {
                case INDICE_PIANO_1:
                    return PIANO_1;
                case INDICE_PIANO_2:
                    return PIANO_2;
                case INDICE_PIANO_3:
                    return PIANO_3;
                case INDICE_HONKY_TONK:
                    return HONKY_TONK;
                case INDICE_E_PIANO_1:
                    return E_PIANO_1;
                case INDICE_E_PIANO_2:
                    return E_PIANO_2;
                case INDICE_HARPSICHORD:
                    return HARPSICHORD;
                case INDICE_CLAV_:
                    return CLAV;
                case INDICE_CELESTA:
                    return CELESTA;
                case INDICE_GLOCKENSPIEL:
                    return GLOCKENSPIEL;
                case INDICE_MUSIC_BOX:
                    return MUSIC_BOX;
                case INDICE_VIBRAPHONE:
                    return VIBRAPHONE;
                case INDICE_MARIMBA:
                    return MARIMBA;
                case INDICE_XYLOPHONE:
                    return XYLOPHONE;
                case INDICE_TUBULAR_BELL:
                    return TUBULAR_BELL;
                case INDICE_SANTUR:
                    return SANTUR;
                case INDICE_ORGAN_1:
                    return ORGAN_1;
                case INDICE_ORGAN_2:
                    return ORGAN_2;
                case INDICE_ORGAN_3:
                    return ORGAN_3;
                case INDICE_CHURCH_ORG_1:
                    return CHURCH_ORG_1;
                case INDICE_REED_ORGAN:
                    return REED_ORGAN;
                case INDICE_ACCORDION_FR:
                    return ACCORDION_FR;
                case INDICE_HARMONICA:
                    return HARMONICA;
                case INDICE_BANDONEON:
                    return BANDONEON;
                case INDICE_NYLON_STR_GT:
                    return NYLON_STR_GT;
                case INDICE_STEEL_STR_GT:
                    return STEEL_STR_GT;
                case INDICE_JAZZ_GT_:
                    return JAZZ_GT_;
                case INDICE_CLEAN_GT_:
                    return CLEAN_GT_;
                case INDICE_MUTED_GT_:
                    return MUTED_GT_;
                case INDICE_OVERDRIVE_GT:
                    return OVERDRIVE_GT;
                case INDICE_DISTORTIONGT:
                    return DISTORTIONGT;
                case INDICE_GT_HARMONICS:
                    return GT_HARMONICS;
                case INDICE_ACOUSTIC_BS_:
                    return ACOUSTIC_BS;
                case INDICE_FINGERED_BS_:
                    return FINGERED_BS;
                case INDICE_PICKED_BS_:
                    return PICKED_BS;
                case INDICE_FRETLESS_BS_:
                    return FRETLESS_BS;
                case INDICE_SLAP_BASS_1:
                    return SLAP_BASS_1;
                case INDICE_SLAP_BASS_2:
                    return SLAP_BASS_2;
                case INDICE_SYNTH_BASS_1:
                    return SYNTH_BASS_1;
                case INDICE_SYNTH_BASS_2:
                    return SYNTH_BASS_2;
                case INDICE_VIOLIN:
                    return VIOLIN;
                case INDICE_VIOLA:
                    return VIOLA;
                case INDICE_CELLO:
                    return CELLO;
                case INDICE_CONTRABASS:
                    return CONTRABASS;
                case INDICE_TREMOLO_STR:
                    return TREMOLO_STR;
                case INDICE_PIZZICATOSTR:
                    return PIZZICATOSTR;
                case INDICE_HARP:
                    return HARP;
                case INDICE_TIMPANI:
                    return TIMPANI;
                case INDICE_STRINGS:
                    return STRINGS;
                case INDICE_SLOW_STRINGS:
                    return SLOW_STRINGS;
                case INDICE_SYN_STRINGS1:
                    return SYN_STRINGS1;
                case INDICE_SYN_STRINGS2:
                    return SYN_STRINGS2;
                case INDICE_CHOIR_AAHS:
                    return CHOIR_AAHS;
                case INDICE_VOICE_OOHS:
                    return VOICE_OOHS;
                case INDICE_SYNVOX:
                    return SYNVOX;
                case INDICE_ORCHESTRAHIT:
                    return ORCHESTRAHIT;
                case INDICE_TRUMPET:
                    return TRUMPET;
                case INDICE_TROMBONE:
                    return TROMBONE;
                case INDICE_TUBA:
                    return TUBA;
                case INDICE_MUTEDTRUMPET:
                    return MUTEDTRUMPET;
                case INDICE_FRENCH_HORNS:
                    return FRENCH_HORNS;
                case INDICE_BRASS_1:
                    return BRASS_1;
                case INDICE_SYNTH_BRASS1:
                    return SYNTH_BRASS1;
                case INDICE_SYNTH_BRASS2:
                    return SYNTH_BRASS2;
                case INDICE_SOPRANO_SAX:
                    return SOPRANO_SAX;
                case INDICE_ALTO_SAX:
                    return ALTO_SAX;
                case INDICE_TENOR_SAX:
                    return TENOR_SAX;
                case INDICE_BARITONE_SAX:
                    return BARITONE_SAX;
                case INDICE_OBOE:
                    return OBOE;
                case INDICE_ENGLISH_HORN:
                    return ENGLISH_HORN;
                case INDICE_BASSOON:
                    return BASSOON;
                case INDICE_CLARINET:
                    return CLARINET;
                case INDICE_PICCOLO:
                    return PICCOLO;
                case INDICE_FLUTE:
                    return FLUTE;
                case INDICE_RECORDER:
                    return RECORDER;
                case INDICE_PAN_FLUTE:
                    return PAN_FLUTE;
                case INDICE_BOTTLE_BLOW:
                    return BOTTLE_BLOW;
                case INDICE_SHAKUHACHI:
                    return SHAKUHACHI;
                case INDICE_WHISTLE:
                    return WHISTLE;
                case INDICE_OCARINA:
                    return OCARINA;
                case INDICE_SQUARE_WAVE:
                    return SQUARE_WAVE;
                case INDICE_SAW_WAVE:
                    return SAW_WAVE;
                case INDICE_SYN_CALLIOPE:
                    return SYN_CALLIOPE;
                case INDICE_CHIFFER_LEAD:
                    return CHIFFER_LEAD;
                case INDICE_CHARANG:
                    return CHARANG;
                case INDICE_SOLO_VOX:
                    return SOLO_VOX;
                case INDICE_5TH_SAW_WAVE:
                    return _5TH_SAW_WAVE;
                case INDICE_BASS_N_LEAD:
                    return BASS_N_LEAD;
                case INDICE_FANTASIA:
                    return FANTASIA;
                case INDICE_WARM_PAD:
                    return WARM_PAD;
                case INDICE_POLYSYNTH:
                    return POLYSYNTH;
                case INDICE_SPACE_VOICE:
                    return SPACE_VOICE;
                case INDICE_BOWED_GLASS:
                    return BOWED_GLASS;
                case INDICE_METAL_PAD:
                    return METAL_PAD;
                case INDICE_HALO_PAD:
                    return HALO_PAD;
                case INDICE_SWEEP_PAD:
                    return SWEEP_PAD;
                case INDICE_ICE_RAIN:
                    return ICE_RAIN;
                case INDICE_SOUNDTRACK:
                    return SOUNDTRACK;
                case INDICE_CRYSTAL:
                    return CRYSTAL;
                case INDICE_ATMOSPHERE:
                    return ATMOSPHERE;
                case INDICE_BRIGHTNESS:
                    return BRIGHTNESS;
                case INDICE_GOBLIN:
                    return GOBLIN;
                case INDICE_ECHO_DROPS:
                    return ECHO_DROPS;
                case INDICE_STAR_THEME:
                    return STAR_THEME;
                case INDICE_SITAR:
                    return SITAR;
                case INDICE_BANJO:
                    return BANJO;
                case INDICE_SHAMISEN:
                    return SHAMISEN;
                case INDICE_KOTO:
                    return KOTO;
                case INDICE_KALIMBA:
                    return KALIMBA;
                case INDICE_BAGPIPE:
                    return BAGPIPE;
                case INDICE_FIDDLE:
                    return FIDDLE;
                case INDICE_SHANAI:
                    return SHANAI;
                case INDICE_TINKLE_BELL:
                    return TINKLE_BELL;
                case INDICE_AGOGO:
                    return AGOGO;
                case INDICE_STEEL_DRUMS:
                    return STEEL_DRUMS;
                case INDICE_WOODBLOCK:
                    return WOODBLOCK;
                case INDICE_TAIKO:
                    return TAIKO;
                case INDICE_MELO_TOM_1:
                    return MELO_TOM_1;
                case INDICE_SYNTH_DRUM:
                    return SYNTH_DRUM;
                case INDICE_REVERSE_CYM_:
                    return REVERSE_CYM_;
                case INDICE_GT_FRETNOISE:
                    return GT_FRETNOISE;
                case INDICE_BREATH_NOISE:
                    return BREATH_NOISE;
                case INDICE_SEASHORE:
                    return SEASHORE;
                case INDICE_BIRD:
                    return BIRD;
                case INDICE_TELEPHONE_1:
                    return TELEPHONE_1;
                case INDICE_HELICOPTER:
                    return HELICOPTER;
                case INDICE_APPLAUSE:
                    return APPLAUSE;
                case INDICE_GUN_SHOT:
                    return GUN_SHOT;
                case INDICE_SYNTHBASS101:
                    return SYNTHBASS101;
                case INDICE_TROMBONE_2:
                    return TROMBONE_2;
                case INDICE_FR_HORN_2:
                    return FR_HORN_2;
                case INDICE_SQUARE:
                    return SQUARE;
                case INDICE_SAW:
                    return SAW;
                case INDICE_SYN_MALLET:
                    return SYN_MALLET;
                case INDICE_ECHO_BELL:
                    return ECHO_BELL;
                case INDICE_SITAR_2:
                    return SITAR_2;
                case INDICE_GT_CUT_NOISE:
                    return GT_CUT_NOISE;
                case INDICE_FL_KEY_CLICK:
                    return FL_KEY_CLICK;
                case INDICE_RAIN:
                    return RAIN;
                case INDICE_DOG:
                    return DOG;
                case INDICE_TELEPHONE_2:
                    return TELEPHONE_2;
                case INDICE_CAR_ENGINE:
                    return CAR_ENGINE;
                case INDICE_LAUGHING:
                    return LAUGHING;
                case INDICE_MACHINE_GUN:
                    return MACHINE_GUN;
                case INDICE_ECHO_PAN:
                    return ECHO_PAN;
                case INDICE_STRING_SLAP:
                    return STRING_SLAP;
                case INDICE_THUNDER:
                    return THUNDER;
                case INDICE_HORSE_GALLOP:
                    return HORSE_GALLOP;
                case INDICE_DOORCREAKING:
                    return DOORCREAKING;
                case INDICE_CAR_STOP:
                    return CAR_STOP;
                case INDICE_SCREAMING:
                    return SCREAMING;
                case INDICE_LASERGUN:
                    return LASERGUN;
                case INDICE_WIND:
                    return WIND;
                case INDICE_BIRD_2:
                    return BIRD_2;
                case INDICE_DOOR:
                    return DOOR;
                case INDICE_CAR_PASS:
                    return CAR_PASS;
                case INDICE_PUNCH:
                    return PUNCH;
                case INDICE_EXPLOSION:
                    return EXPLOSION;
                case INDICE_STREAM:
                    return STREAM;
                case INDICE_SCRATCH:
                    return SCRATCH;
                case INDICE_CAR_CRASH:
                    return CAR_CRASH;
                case INDICE_HEART_BEAT:
                    return HEART_BEAT;
                case INDICE_BUBBLE:
                    return BUBBLE;
                case INDICE_WIND_CHIMES:
                    return WIND_CHIMES;
                case INDICE_SIREN:
                    return SIREN;
                case INDICE_FOOTSTEPS:
                    return FOOTSTEPS;
                case INDICE_TRAIN:
                    return TRAIN;
                case INDICE_JETPLANE:
                    return JETPLANE;
                case INDICE_PIANO_1$:
                    return PIANO_1$;
                case INDICE_PIANO_2$:
                    return PIANO_2$;
                case INDICE_PIANO_3$:
                    return PIANO_3$;
                case INDICE_HONKY_TONK$:
                    return HONKY_TONK$;
                case INDICE_DETUNED_EP_1:
                    return DETUNED_EP_1;
                case INDICE_DETUNED_EP_2:
                    return DETUNED_EP_2;
                case INDICE_COUPLED_HPS_:
                    return COUPLED_HPS_;
                case INDICE_VIBRAPHONE$:
                    return VIBRAPHONE$;
                case INDICE_MARIMBA$:
                    return MARIMBA$;
                case INDICE_CHURCH_BELL:
                    return CHURCH_BELL;
                case INDICE_DETUNED_OR_1:
                    return DETUNED_OR_1;
                case INDICE_DETUNED_OR_2:
                    return DETUNED_OR_2;
                case INDICE_CHURCH_ORG_2:
                    return CHURCH_ORG_2;
                case INDICE_ACCORDION_IT:
                    return ACCORDION_IT;
                case INDICE_UKULELE:
                    return UKULELE;
                case INDICE_12_STR_GT:
                    return _12_STR_GT;
                case INDICE_HAWAIIAN_GT_:
                    return HAWAIIAN_GT_;
                case INDICE_CHORUS_GT_:
                    return CHORUS_GT_;
                case INDICE_FUNK_GT_:
                    return FUNK_GT_;
                case INDICE_FEEDBACK_GT_:
                    return FEEDBACK_GT;
                case INDICE_GT_FEEDBACK:
                    return GT_FEEDBACK;
                case INDICE_SYNTH_BASS_3:
                    return SYNTH_BASS_3;
                case INDICE_SYNTH_BASS_4:
                    return SYNTH_BASS_4;
                case INDICE_SLOW_VIOLIN:
                    return SLOW_VIOLIN;
                case INDICE_ORCHESTRA:
                    return ORCHESTRA;
                case INDICE_SYN_STRINGS3:
                    return SYN_STRINGS3;
                case INDICE_BRASS_2:
                    return BRASS_2;
                case INDICE_SYNTH_BRASS3:
                    return SYNTH_BRASS3;
                case INDICE_SYNTH_BRASS4:
                    return SYNTH_BRASS4;
                case INDICE_SINE_WAVE:
                    return SINE_WAVE;
                case INDICE_DOCTOR_SOLO:
                    return DOCTOR_SOLO;
                case INDICE_TAISHO_KOTO:
                    return TAISHO_KOTO;
                case INDICE_CASTANETS:
                    return CASTANETS;
                case INDICE_CONCERT_BD:
                    return CONCERT_BD;
                case INDICE_MELO_TOM_2:
                    return MELO_TOM_2;
                case INDICE_808_TOM:
                    return _808_TOM;
                case INDICE_STARSHIP:
                    return STARSHIP;
                case INDICE_CARILLON:
                    return CARILLON;
                case INDICE_ELEC_PERC_:
                    return ELEC_PERC_;
                case INDICE_BURST_NOISE:
                    return BURST_NOISE;
                case INDICE_PIANO_1D:
                    return PIANO_1D;
                case INDICE_E_PIANO_1V:
                    return E_PIANO_1V;
                case INDICE_E_PIANO_2V:
                    return E_PIANO_2V;
                case INDICE_HARPSICHORD$:
                    return HARPSICHORD$;
                case INDICE_60_S_ORGAN_1:
                    return _60_S_ORGAN_1;
                case INDICE_CHURCH_ORG_3:
                    return CHURCH_ORG_3;
                case INDICE_NYLON_GT_O:
                    return NYLON_GT_O;
                case INDICE_MANDOLIN:
                    return MANDOLIN;
                case INDICE_FUNK_GT_2:
                    return FUNK_GT_2;
                case INDICE_RUBBER_BASS:
                    return RUBBER_BASS;
                case INDICE_ANALOGBRASS1:
                    return ANALOGBRASS1;
                case INDICE_ANALOGBRASS2:
                    return ANALOGBRASS2;
                case INDICE_60_S_E_PIANO:
                    return _60_S_E_PIANO;
                case INDICE_HARPSI_O:
                    return HARPSI_O;
                case INDICE_ORGAN_4:
                    return ORGAN_4;
                case INDICE_ORGAN_5:
                    return ORGAN_5;
                case INDICE_NYLON_GT_2:
                    return NYLON_GT_2;
                case INDICE_CHOIR_AAHS_2:
                    return CHOIR_AAHS_2;
                default:
                    return 0;
            }
        }

        static int GenerarLlaveInstrumento(int banco, int programa) {
            return ((banco & 0xffff) << 16) | (programa & 0xffff);
        }

        static int ObtenerIndice(int banco, int programa) {
            return ObtenerIndice(GenerarLlaveInstrumento(banco, programa));
        }

        static int ObtenerIndice(int llave) {
            switch (llave) {
                case PIANO_1:
                    return INDICE_PIANO_1;
                case PIANO_2:
                    return INDICE_PIANO_2;
                case PIANO_3:
                    return INDICE_PIANO_3;
                case HONKY_TONK:
                    return INDICE_HONKY_TONK;
                case E_PIANO_1:
                    return INDICE_E_PIANO_1;
                case E_PIANO_2:
                    return INDICE_E_PIANO_2;
                case HARPSICHORD:
                    return INDICE_HARPSICHORD;
                case CLAV:
                    return INDICE_CLAV_;
                case CELESTA:
                    return INDICE_CELESTA;
                case GLOCKENSPIEL:
                    return INDICE_GLOCKENSPIEL;
                case MUSIC_BOX:
                    return INDICE_MUSIC_BOX;
                case VIBRAPHONE:
                    return INDICE_VIBRAPHONE;
                case MARIMBA:
                    return INDICE_MARIMBA;
                case XYLOPHONE:
                    return INDICE_XYLOPHONE;
                case TUBULAR_BELL:
                    return INDICE_TUBULAR_BELL;
                case SANTUR:
                    return INDICE_SANTUR;
                case ORGAN_1:
                    return INDICE_ORGAN_1;
                case ORGAN_2:
                    return INDICE_ORGAN_2;
                case ORGAN_3:
                    return INDICE_ORGAN_3;
                case CHURCH_ORG_1:
                    return INDICE_CHURCH_ORG_1;
                case REED_ORGAN:
                    return INDICE_REED_ORGAN;
                case ACCORDION_FR:
                    return INDICE_ACCORDION_FR;
                case HARMONICA:
                    return INDICE_HARMONICA;
                case BANDONEON:
                    return INDICE_BANDONEON;
                case NYLON_STR_GT:
                    return INDICE_NYLON_STR_GT;
                case STEEL_STR_GT:
                    return INDICE_STEEL_STR_GT;
                case JAZZ_GT_:
                    return INDICE_JAZZ_GT_;
                case CLEAN_GT_:
                    return INDICE_CLEAN_GT_;
                case MUTED_GT_:
                    return INDICE_MUTED_GT_;
                case OVERDRIVE_GT:
                    return INDICE_OVERDRIVE_GT;
                case DISTORTIONGT:
                    return INDICE_DISTORTIONGT;
                case GT_HARMONICS:
                    return INDICE_GT_HARMONICS;
                case ACOUSTIC_BS:
                    return INDICE_ACOUSTIC_BS_;
                case FINGERED_BS:
                    return INDICE_FINGERED_BS_;
                case PICKED_BS:
                    return INDICE_PICKED_BS_;
                case FRETLESS_BS:
                    return INDICE_FRETLESS_BS_;
                case SLAP_BASS_1:
                    return INDICE_SLAP_BASS_1;
                case SLAP_BASS_2:
                    return INDICE_SLAP_BASS_2;
                case SYNTH_BASS_1:
                    return INDICE_SYNTH_BASS_1;
                case SYNTH_BASS_2:
                    return INDICE_SYNTH_BASS_2;
                case VIOLIN:
                    return INDICE_VIOLIN;
                case VIOLA:
                    return INDICE_VIOLA;
                case CELLO:
                    return INDICE_CELLO;
                case CONTRABASS:
                    return INDICE_CONTRABASS;
                case TREMOLO_STR:
                    return INDICE_TREMOLO_STR;
                case PIZZICATOSTR:
                    return INDICE_PIZZICATOSTR;
                case HARP:
                    return INDICE_HARP;
                case TIMPANI:
                    return INDICE_TIMPANI;
                case STRINGS:
                    return INDICE_STRINGS;
                case SLOW_STRINGS:
                    return INDICE_SLOW_STRINGS;
                case SYN_STRINGS1:
                    return INDICE_SYN_STRINGS1;
                case SYN_STRINGS2:
                    return INDICE_SYN_STRINGS2;
                case CHOIR_AAHS:
                    return INDICE_CHOIR_AAHS;
                case VOICE_OOHS:
                    return INDICE_VOICE_OOHS;
                case SYNVOX:
                    return INDICE_SYNVOX;
                case ORCHESTRAHIT:
                    return INDICE_ORCHESTRAHIT;
                case TRUMPET:
                    return INDICE_TRUMPET;
                case TROMBONE:
                    return INDICE_TROMBONE;
                case TUBA:
                    return INDICE_TUBA;
                case MUTEDTRUMPET:
                    return INDICE_MUTEDTRUMPET;
                case FRENCH_HORNS:
                    return INDICE_FRENCH_HORNS;
                case BRASS_1:
                    return INDICE_BRASS_1;
                case SYNTH_BRASS1:
                    return INDICE_SYNTH_BRASS1;
                case SYNTH_BRASS2:
                    return INDICE_SYNTH_BRASS2;
                case SOPRANO_SAX:
                    return INDICE_SOPRANO_SAX;
                case ALTO_SAX:
                    return INDICE_ALTO_SAX;
                case TENOR_SAX:
                    return INDICE_TENOR_SAX;
                case BARITONE_SAX:
                    return INDICE_BARITONE_SAX;
                case OBOE:
                    return INDICE_OBOE;
                case ENGLISH_HORN:
                    return INDICE_ENGLISH_HORN;
                case BASSOON:
                    return INDICE_BASSOON;
                case CLARINET:
                    return INDICE_CLARINET;
                case PICCOLO:
                    return INDICE_PICCOLO;
                case FLUTE:
                    return INDICE_FLUTE;
                case RECORDER:
                    return INDICE_RECORDER;
                case PAN_FLUTE:
                    return INDICE_PAN_FLUTE;
                case BOTTLE_BLOW:
                    return INDICE_BOTTLE_BLOW;
                case SHAKUHACHI:
                    return INDICE_SHAKUHACHI;
                case WHISTLE:
                    return INDICE_WHISTLE;
                case OCARINA:
                    return INDICE_OCARINA;
                case SQUARE_WAVE:
                    return INDICE_SQUARE_WAVE;
                case SAW_WAVE:
                    return INDICE_SAW_WAVE;
                case SYN_CALLIOPE:
                    return INDICE_SYN_CALLIOPE;
                case CHIFFER_LEAD:
                    return INDICE_CHIFFER_LEAD;
                case CHARANG:
                    return INDICE_CHARANG;
                case SOLO_VOX:
                    return INDICE_SOLO_VOX;
                case _5TH_SAW_WAVE:
                    return INDICE_5TH_SAW_WAVE;
                case BASS_N_LEAD:
                    return INDICE_BASS_N_LEAD;
                case FANTASIA:
                    return INDICE_FANTASIA;
                case WARM_PAD:
                    return INDICE_WARM_PAD;
                case POLYSYNTH:
                    return INDICE_POLYSYNTH;
                case SPACE_VOICE:
                    return INDICE_SPACE_VOICE;
                case BOWED_GLASS:
                    return INDICE_BOWED_GLASS;
                case METAL_PAD:
                    return INDICE_METAL_PAD;
                case HALO_PAD:
                    return INDICE_HALO_PAD;
                case SWEEP_PAD:
                    return INDICE_SWEEP_PAD;
                case ICE_RAIN:
                    return INDICE_ICE_RAIN;
                case SOUNDTRACK:
                    return INDICE_SOUNDTRACK;
                case CRYSTAL:
                    return INDICE_CRYSTAL;
                case ATMOSPHERE:
                    return INDICE_ATMOSPHERE;
                case BRIGHTNESS:
                    return INDICE_BRIGHTNESS;
                case GOBLIN:
                    return INDICE_GOBLIN;
                case ECHO_DROPS:
                    return INDICE_ECHO_DROPS;
                case STAR_THEME:
                    return INDICE_STAR_THEME;
                case SITAR:
                    return INDICE_SITAR;
                case BANJO:
                    return INDICE_BANJO;
                case SHAMISEN:
                    return INDICE_SHAMISEN;
                case KOTO:
                    return INDICE_KOTO;
                case KALIMBA:
                    return INDICE_KALIMBA;
                case BAGPIPE:
                    return INDICE_BAGPIPE;
                case FIDDLE:
                    return INDICE_FIDDLE;
                case SHANAI:
                    return INDICE_SHANAI;
                case TINKLE_BELL:
                    return INDICE_TINKLE_BELL;
                case AGOGO:
                    return INDICE_AGOGO;
                case STEEL_DRUMS:
                    return INDICE_STEEL_DRUMS;
                case WOODBLOCK:
                    return INDICE_WOODBLOCK;
                case TAIKO:
                    return INDICE_TAIKO;
                case MELO_TOM_1:
                    return INDICE_MELO_TOM_1;
                case SYNTH_DRUM:
                    return INDICE_SYNTH_DRUM;
                case REVERSE_CYM_:
                    return INDICE_REVERSE_CYM_;
                case GT_FRETNOISE:
                    return INDICE_GT_FRETNOISE;
                case BREATH_NOISE:
                    return INDICE_BREATH_NOISE;
                case SEASHORE:
                    return INDICE_SEASHORE;
                case BIRD:
                    return INDICE_BIRD;
                case TELEPHONE_1:
                    return INDICE_TELEPHONE_1;
                case HELICOPTER:
                    return INDICE_HELICOPTER;
                case APPLAUSE:
                    return INDICE_APPLAUSE;
                case GUN_SHOT:
                    return INDICE_GUN_SHOT;
                case SYNTHBASS101:
                    return INDICE_SYNTHBASS101;
                case TROMBONE_2:
                    return INDICE_TROMBONE_2;
                case FR_HORN_2:
                    return INDICE_FR_HORN_2;
                case SQUARE:
                    return INDICE_SQUARE;
                case SAW:
                    return INDICE_SAW;
                case SYN_MALLET:
                    return INDICE_SYN_MALLET;
                case ECHO_BELL:
                    return INDICE_ECHO_BELL;
                case SITAR_2:
                    return INDICE_SITAR_2;
                case GT_CUT_NOISE:
                    return INDICE_GT_CUT_NOISE;
                case FL_KEY_CLICK:
                    return INDICE_FL_KEY_CLICK;
                case RAIN:
                    return INDICE_RAIN;
                case DOG:
                    return INDICE_DOG;
                case TELEPHONE_2:
                    return INDICE_TELEPHONE_2;
                case CAR_ENGINE:
                    return INDICE_CAR_ENGINE;
                case LAUGHING:
                    return INDICE_LAUGHING;
                case MACHINE_GUN:
                    return INDICE_MACHINE_GUN;
                case ECHO_PAN:
                    return INDICE_ECHO_PAN;
                case STRING_SLAP:
                    return INDICE_STRING_SLAP;
                case THUNDER:
                    return INDICE_THUNDER;
                case HORSE_GALLOP:
                    return INDICE_HORSE_GALLOP;
                case DOORCREAKING:
                    return INDICE_DOORCREAKING;
                case CAR_STOP:
                    return INDICE_CAR_STOP;
                case SCREAMING:
                    return INDICE_SCREAMING;
                case LASERGUN:
                    return INDICE_LASERGUN;
                case WIND:
                    return INDICE_WIND;
                case BIRD_2:
                    return INDICE_BIRD_2;
                case DOOR:
                    return INDICE_DOOR;
                case CAR_PASS:
                    return INDICE_CAR_PASS;
                case PUNCH:
                    return INDICE_PUNCH;
                case EXPLOSION:
                    return INDICE_EXPLOSION;
                case STREAM:
                    return INDICE_STREAM;
                case SCRATCH:
                    return INDICE_SCRATCH;
                case CAR_CRASH:
                    return INDICE_CAR_CRASH;
                case HEART_BEAT:
                    return INDICE_HEART_BEAT;
                case BUBBLE:
                    return INDICE_BUBBLE;
                case WIND_CHIMES:
                    return INDICE_WIND_CHIMES;
                case SIREN:
                    return INDICE_SIREN;
                case FOOTSTEPS:
                    return INDICE_FOOTSTEPS;
                case TRAIN:
                    return INDICE_TRAIN;
                case JETPLANE:
                    return INDICE_JETPLANE;
                case PIANO_1$:
                    return INDICE_PIANO_1$;
                case PIANO_2$:
                    return INDICE_PIANO_2$;
                case PIANO_3$:
                    return INDICE_PIANO_3$;
                case HONKY_TONK$:
                    return INDICE_HONKY_TONK$;
                case DETUNED_EP_1:
                    return INDICE_DETUNED_EP_1;
                case DETUNED_EP_2:
                    return INDICE_DETUNED_EP_2;
                case COUPLED_HPS_:
                    return INDICE_COUPLED_HPS_;
                case VIBRAPHONE$:
                    return INDICE_VIBRAPHONE$;
                case MARIMBA$:
                    return INDICE_MARIMBA$;
                case CHURCH_BELL:
                    return INDICE_CHURCH_BELL;
                case DETUNED_OR_1:
                    return INDICE_DETUNED_OR_1;
                case DETUNED_OR_2:
                    return INDICE_DETUNED_OR_2;
                case CHURCH_ORG_2:
                    return INDICE_CHURCH_ORG_2;
                case ACCORDION_IT:
                    return INDICE_ACCORDION_IT;
                case UKULELE:
                    return INDICE_UKULELE;
                case _12_STR_GT:
                    return INDICE_12_STR_GT;
                case HAWAIIAN_GT_:
                    return INDICE_HAWAIIAN_GT_;
                case CHORUS_GT_:
                    return INDICE_CHORUS_GT_;
                case FUNK_GT_:
                    return INDICE_FUNK_GT_;
                case FEEDBACK_GT:
                    return INDICE_FEEDBACK_GT_;
                case GT_FEEDBACK:
                    return INDICE_GT_FEEDBACK;
                case SYNTH_BASS_3:
                    return INDICE_SYNTH_BASS_3;
                case SYNTH_BASS_4:
                    return INDICE_SYNTH_BASS_4;
                case SLOW_VIOLIN:
                    return INDICE_SLOW_VIOLIN;
                case ORCHESTRA:
                    return INDICE_ORCHESTRA;
                case SYN_STRINGS3:
                    return INDICE_SYN_STRINGS3;
                case BRASS_2:
                    return INDICE_BRASS_2;
                case SYNTH_BRASS3:
                    return INDICE_SYNTH_BRASS3;
                case SYNTH_BRASS4:
                    return INDICE_SYNTH_BRASS4;
                case SINE_WAVE:
                    return INDICE_SINE_WAVE;
                case DOCTOR_SOLO:
                    return INDICE_DOCTOR_SOLO;
                case TAISHO_KOTO:
                    return INDICE_TAISHO_KOTO;
                case CASTANETS:
                    return INDICE_CASTANETS;
                case CONCERT_BD:
                    return INDICE_CONCERT_BD;
                case MELO_TOM_2:
                    return INDICE_MELO_TOM_2;
                case _808_TOM:
                    return INDICE_808_TOM;
                case STARSHIP:
                    return INDICE_STARSHIP;
                case CARILLON:
                    return INDICE_CARILLON;
                case ELEC_PERC_:
                    return INDICE_ELEC_PERC_;
                case BURST_NOISE:
                    return INDICE_BURST_NOISE;
                case PIANO_1D:
                    return INDICE_PIANO_1D;
                case E_PIANO_1V:
                    return INDICE_E_PIANO_1V;
                case E_PIANO_2V:
                    return INDICE_E_PIANO_2V;
                case HARPSICHORD$:
                    return INDICE_HARPSICHORD$;
                case _60_S_ORGAN_1:
                    return INDICE_60_S_ORGAN_1;
                case CHURCH_ORG_3:
                    return INDICE_CHURCH_ORG_3;
                case NYLON_GT_O:
                    return INDICE_NYLON_GT_O;
                case MANDOLIN:
                    return INDICE_MANDOLIN;
                case FUNK_GT_2:
                    return INDICE_FUNK_GT_2;
                case RUBBER_BASS:
                    return INDICE_RUBBER_BASS;
                case ANALOGBRASS1:
                    return INDICE_ANALOGBRASS1;
                case ANALOGBRASS2:
                    return INDICE_ANALOGBRASS2;
                case _60_S_E_PIANO:
                    return INDICE_60_S_E_PIANO;
                case HARPSI_O:
                    return INDICE_HARPSI_O;
                case ORGAN_4:
                    return INDICE_ORGAN_4;
                case ORGAN_5:
                    return INDICE_ORGAN_5;
                case NYLON_GT_2:
                    return INDICE_NYLON_GT_2;
                case CHOIR_AAHS_2:
                    return INDICE_CHOIR_AAHS_2;
                default:
                    throw new RuntimeException(
                            "La llave para el instrumento no se reconoce " + llave + "\n"
                            + "32-16 " + ((llave >>> 16) & 0xff) + " : 16-0 " + (llave & 0xff)
                    );
            }
        }

    }

}
