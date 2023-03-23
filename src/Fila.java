import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Fila {

    private int capacidade, ocupados;
    private int servidores;
    private int taxaDeEntrada0, taxaDeEntrada1;
    private int taxaDeSaida0, taxaDeSaida1;

    private double tempoGlobal;
    private double[] deltaTempo;
    private Double saida, chegada;

    private int losses;
    private final StringBuilder LOG;
    private final StringBuilder LOG2;

    public Fila(int capacidade, int servidores, int taxaDeEntrada0, int taxaDeEntrada1, int taxaDeSaida0, int taxaDeSaida1) {
        this.capacidade = capacidade;
        this.losses = 0;
        this.servidores = servidores;
        this.deltaTempo = new double[this.capacidade + 1];
        this.taxaDeSaida0 = taxaDeSaida0;
        this.taxaDeSaida1 = taxaDeSaida1;
        this.taxaDeEntrada0 = taxaDeEntrada0;
        this.taxaDeEntrada1 = taxaDeEntrada1;
        this.LOG = new StringBuilder();
        this.LOG2 = new StringBuilder();
        this.saida = null;
        this.chegada = null;
    }

    public void run(long executions, double initialTime, long... seeds) {

        double[] avarage = new double[this.capacidade + 2];
        for (var seed : seeds) {
            RNG rand = new RNG(seed);
            for (int i = 0; i < executions; i++) {
                if (this.chegada == null) {
                    this.chegada = initialTime;
                    continue;
                }
                operation(rand);
            }
            this.makeLog(seed);
            this.clear(seeds.length, avarage);
            this.LOG2.append("\n\n");
        }
        this.makeLog(avarage);
    }

    private void operation(RNG rand) {
        double aux = this.tempoGlobal;
        if ((this.saida == null) || (this.chegada < this.saida)) {
            this.tempoGlobal = chegada;
            this.deltaTempo[this.ocupados] += (this.tempoGlobal - aux);
            this.LOG2.append("CHEGADA PARA: " + this.chegada).append("\n");
            if (ocupados < capacidade) {
                this.ocupados++;
                if (this.ocupados <= this.servidores) this.agendaSaida(rand);
            } else this.losses++;

            this.agendaChegada(rand);
        } else {
            this.tempoGlobal = this.saida;
            this.deltaTempo[this.ocupados] += (this.tempoGlobal - aux);
            this.ocupados--;
            this.LOG2.append("SAIDA PARA: " + this.saida).append("\n");
            if (this.ocupados >= this.servidores) this.agendaSaida(rand);
            else this.saida = null;
        }
        this.LOG2.append("TEMPO ATUAL: " + this.tempoGlobal).append("\n");
    }

    private void agendaSaida(RNG r) {
        this.saida = this.tempoGlobal + (taxaDeSaida0 + r.next() * (taxaDeSaida1 - taxaDeSaida0));
    }

    private void agendaChegada(RNG r) {
        this.chegada = this.tempoGlobal + (taxaDeEntrada0 + r.next() * (taxaDeEntrada1 - taxaDeEntrada0));
    }

    private void makeLog(long seed) {
        this.LOG.append(String.format("seed: %s %nSTATE | TIME | PROBABILITY %n", seed));

        double aux = 0;

        for (var delta : this.deltaTempo) aux += delta;

        for (int  i = 0; i < this.deltaTempo.length; i++) {
            final double delta = this.deltaTempo[i];
            if (delta == 0) continue;
            this.LOG.append(String.format("%d | %.4f | %.2f%% %n", i,delta,((delta/aux)*100)));
        }
        this.LOG.append(String.format("TOTAL | %.4f | 100%% %n", aux));
        this.LOG.append("Number of losses: " + this.losses + "\n\n");
    }

    private void makeLog(double[] avarage) {
        this.LOG.append("===== Avarage ==== \n");
        this.LOG.append(String.format("STATE | TIME | PROBABILITY %n"));

        double aux = 0;
        for (int i = 0; i < avarage.length - 1; i++) aux += avarage[i];

        for (int  i = 0; i < avarage.length - 1; i++) {
            final double av = avarage[i];
            if (av == 0) continue;
            this.LOG.append(String.format("%d | %f | %.2f%% %n", i, av,((av/aux)*100)));
        }
        this.LOG.append(String.format("TOTAL | %.4f | 100%% %n", aux));
        this.LOG.append("Number of losses: ").append(avarage[avarage.length - 1]).append("\n\n");
    }

    private void clear(long numberOfSeeds, double save[]){
        this.tempoGlobal = 0;
        this.ocupados = 0;
        this.saida = null;
        this.chegada = null;

        for(int i = 0; i < this.deltaTempo.length; i++) save[i] += (this.deltaTempo[i] / numberOfSeeds);
        save[save.length - 1] += (this.losses / (double) numberOfSeeds);
        this.deltaTempo = new double[this.capacidade + 1];
        this.losses = 0;
    }

    public StringBuilder getLOG() {return this.LOG;}

    public StringBuilder getLOG2() {
        return LOG2;
    }
}
