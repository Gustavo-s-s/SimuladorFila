import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Fila fila = new Fila(5, 1, 2, 4, 3, 5);
        fila.run(100000L, 2D, 1L, 2L, 3L, 4L, 5L);
        save(fila.getLOG(), "log_de_simulacao.txt");
        save(fila.getLOG2(), "log_de_simulacao_2.txt");
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
Este código é uma simulação de fila com eventos de entrada e saída. A fila possui uma capacidade
máxima, um número de servidores disponíveis e taxas de entrada e saída para usuários. O objetivo é
calcular a probabilidade de cada estado da fila, ou seja, o número de usuários na fila em um
determinado momento e o tempo em que esse estado ocorreu.

O programa principal cria uma nova instância da fila com capacidade máxima de 5, 2 servidores
disponíveis, uma taxa de entrada entre 2 e 4 e uma taxa de saída entre 3 e 5.

Também é possivel simular a fila com x usuario e seu tempo inicial.
*/