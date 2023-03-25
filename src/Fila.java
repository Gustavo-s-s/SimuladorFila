import java.util.*;

public class Fila {

    private int capacidade, ocupados;
    private int servidores;
    private int taxaDeEntrada0, taxaDeEntrada1;
    private int taxaDeSaida0, taxaDeSaida1;

    private double tempoGlobal;
    private double[] deltaTempo;
    private Double chegada;
    private PriorityQueue<Double> saidas;

    private int losses;
    private final StringBuilder LOG;
    private final StringBuilder LOG2;

    public Fila(int capacidade, int servidores, int taxaDeEntrada0, int taxaDeEntrada1, int taxaDeSaida0, int taxaDeSaida1) {
        this.capacidade = capacidade;
        this.losses = 0;
        this.servidores = servidores;
        this.deltaTempo = new double[this.capacidade + 1];
        this.saidas = new PriorityQueue<>(Double::compare);
        this.chegada = null;
        this.taxaDeSaida0 = taxaDeSaida0;
        this.taxaDeSaida1 = taxaDeSaida1;
        this.taxaDeEntrada0 = taxaDeEntrada0;
        this.taxaDeEntrada1 = taxaDeEntrada1;
        this.LOG = new StringBuilder();
        this.LOG2 = new StringBuilder();
    }

    public void run(long executions, double initialTime, long... seeds) {

        double[] avarage = new double[this.capacidade + 2];
        for (var seed : seeds) {
            RNG rand = new RNG(seed);
            for (int i = 0; i <= executions; i++) {
                if (this.chegada == null) {
                    this.chegada = initialTime;
                    this.LOG2.append("[AGENDANDO CHEGADA] - " + this.chegada).append("\n");
                    // this.saida = initialTime + taxaDeSaida0 + Math.random() * (taxaDeSaida1 - taxaDeSaida0);
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
        if ((this.saidas.isEmpty()) || (this.chegada < this.saidas.peek())) {
            this.tempoGlobal = chegada; // atualizando tempo global
            this.deltaTempo[this.ocupados] += (this.tempoGlobal - aux); // modificando delta tempo
            this.LOG2.append("[CHEGANDO] - " + this.chegada).append("\n");
            if (ocupados < capacidade) { // posso ocupar a fila?
                this.ocupados++;
                if (this.ocupados <= this.servidores) { // estou de frente para o servidor?
                    this.agendaSaida(rand);
                    this.LOG2.append("[AGENDANDO SAIDA] - " + this.saidas.peek()).append("\n");
                }
            } else this.losses++;

            this.agendaChegada(rand);
            this.LOG2.append("[AGENDANDO CHEGADA] - " + this.chegada).append("\n");
        } else {
            this.tempoGlobal = this.saidas.poll();
            this.deltaTempo[this.ocupados] += (this.tempoGlobal - aux);
            this.ocupados--;
            this.LOG2.append("[SAINDO] " + this.tempoGlobal).append("\n");
            if (this.ocupados >= this.servidores) { // tem servidores disponivel
                double saidaAgendada = this.agendaSaida(rand);
                this.LOG2.append("[AGENDANDO SAIDA] - " + saidaAgendada).append("\n");
            }
        }
        //this.LOG2.append("TEMPO ATUAL: " + this.tempoGlobal).append("\n");
    }


    private double agendaSaida(RNG r) {
        double n = r.next();
        double result = this.tempoGlobal + (taxaDeSaida0 + n * (taxaDeSaida1 - taxaDeSaida0));
        this.saidas.add(result);
        this.LOG2.append("[RAND] - " + n).append("\n");
        return result;
    }

    private void agendaChegada(RNG r) {
        double n = r.next();
        this.chegada = this.tempoGlobal + (taxaDeEntrada0 + n * (taxaDeEntrada1 - taxaDeEntrada0));
        this.LOG2.append("[RAND] - " + n).append("\n");
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
        this.LOG.append("===== Average ==== \n");
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
        this.saidas = new PriorityQueue<>();
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
