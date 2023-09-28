package controller;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadCarro extends Thread {
    private int id;
    private int escuderia;
    private String nomeEscuderia;
    Semaphore[] semaforoPorEscuderia;
    Semaphore semaforoPista;

    private static AtomicInteger quantidadeFinalistas = new AtomicInteger(0);
    private static int[][] tabelaVoltasMaisRapidas = new int[14][3];


    public ThreadCarro(int id, int escuderia, Semaphore[] semaforosEscuderia, Semaphore semaforoPista) {
        this.id = id;
        this.escuderia = escuderia;
        nomeEscuderia = retornarNomeEscuderia(escuderia);
        this.semaforoPorEscuderia = semaforosEscuderia;
        this.semaforoPista = semaforoPista;
    }

    @Override
    public void run() {
        try {
            // Aguarda a sua vez de entrar na pista
            semaforoPorEscuderia[escuderia].acquire();

            // Entra na pista
            entrarNaPista();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // Libera o semáforo da escuderia após terminar o treino
            semaforoPorEscuderia[escuderia].release();
        }
    }

    public void entrarNaPista() {
        try {
            semaforoPista.acquire();

            darVoltasNaPista();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            semaforoPista.release();

            exibirTabelaResultado();
        }
    }


    public void darVoltasNaPista() {
        final int QUANTIDADE_VOLTAS = 3;
        int voltaMaisRapida = 0;

        int tempoVoltaAtual;
        for (int i = 0; i < QUANTIDADE_VOLTAS; i++) {
            tempoVoltaAtual = gerarTempoDeVoltaAleatorio();
            exibirTempoVoltaAtual(i, tempoVoltaAtual);

            //Atualiza a volta mais rápida
            if (tempoVoltaAtual < voltaMaisRapida || voltaMaisRapida == 0) {
                voltaMaisRapida = tempoVoltaAtual;
            }
        }

        armazenarVoltaMaisRapida(voltaMaisRapida);
    }

    public int gerarTempoDeVoltaAleatorio() {
        int MILISSEGUNDOS_MINIMO = 60000;
        int MILISSEGUNDOS_MAXIMO = 120001;
        return ThreadLocalRandom.current().nextInt(MILISSEGUNDOS_MINIMO,MILISSEGUNDOS_MAXIMO);
    }

    public void exibirTempoVoltaAtual(int index, int tempoVoltaAtual) {
        String formato = "ID [%2d] - Escuderia [%-8s] >>> %da volta = %,d ms";
        System.out.printf((formato) + "%n", id, nomeEscuderia, index + 1, tempoVoltaAtual);
    }

    private void armazenarVoltaMaisRapida(int voltaMaisRapida) {
        int finalistas = quantidadeFinalistas.getAndIncrement();

        tabelaVoltasMaisRapidas[finalistas][0] = id;
        tabelaVoltasMaisRapidas[finalistas][1] = escuderia;
        tabelaVoltasMaisRapidas[finalistas][2] = voltaMaisRapida;
    }

    public void ordenarSegundaColunaCrescentemente() {
        Arrays.sort(tabelaVoltasMaisRapidas, Comparator.comparingInt(a -> a[2]));
    }

    public void exibirTabelaResultado() {
        if (quantidadeFinalistas.get() == 14) {
            ordenarSegundaColunaCrescentemente();

            System.out.println("\n+===============================+");
            System.out.println("|  >>> VOLTAS MAIS RAPIDAS <<<  |");
            System.out.println("+=====+============+============+");
            System.out.println("| ID  | Escuderia  | Tempo (ms) |");
            System.out.println("+=====+============+============+");

            for (int[] linha : tabelaVoltasMaisRapidas) {
                System.out.printf("| %-3d | %-10s | %-10d |%n", linha[0], retornarNomeEscuderia(linha[1]), linha[2]);
            }

            System.out.println("+=====+============+============+");
        }
    }

    public String retornarNomeEscuderia(int escuderiaAtual) {
        //Precisa usar esse metodo porque a tabelaVoltaMaisRapida aceita apenas int, então nao da para colocar um nome dela
        //Daria pra remover esse metodo transformando a tabela em uma classe
        return switch (escuderiaAtual) {
            case 0 -> "MacLaren";
            case 1 -> "Red Bull";
            case 2 -> "Mercedes";
            case 3 -> "Ferrari";
            case 4 -> "Williams";
            case 5 -> "Alpine";
            case 6 -> "Romeo";
            default -> "ERROR";
        };
    }
}
