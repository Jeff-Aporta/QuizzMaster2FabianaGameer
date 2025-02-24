package Tools;


public final class callback {

    public interface Simple {

        void run();
    }

    public interface Simple_Strings {

        void Ejecutar(String... s);
    }

    public interface Simple_Enteros {

        void run(int... n);
    }

    public interface Simple_Longs {
        boolean boolrun(long... n);
    }
}
