import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Fila fila = new Fila(5, 1, 2, 4, 3, 5);
        Fila fila1 = new Fila(5, 2, 2, 4, 3, 5);
        fila.run(100000L, 3D, 1, 2L, 3L, 4L, 5L);
        fila1.run(10000L, 3D,1,2,3,4,5);
        save(fila.getLOG(), "logFila01.txt");
        save(fila1.getLOG(), "logFila02.txt");
        //save(fila.getLOG2(), "log_de_simulacao_2.txt");
    }

    private static void save(StringBuilder text, String fileName) {
        File file = new File(fileName);

        try {
            if (!file.exists()) file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write(text.toString());
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}



/*

*/