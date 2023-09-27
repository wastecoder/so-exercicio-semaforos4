package controller;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class ThreadCarro extends Thread {
    private int escuderia;
    Semaphore semaforo;
    boolean escudeiraJaSelecionada;
    private static AtomicIntegerArray carrosNaPista = new AtomicIntegerArray(5);
    private static AtomicInteger quantidadeFinalistas = new AtomicInteger(0);
    private static int[][] tabelaFinalistas = new int[5][2];

    public ThreadCarro(int escuderia, Semaphore semaforo) {
        this.escuderia = escuderia;
        this.semaforo = semaforo;
    }

    @Override
    public void run() {
        try {
            semaforo.acquire();

            selecionarCarrosParaPista();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            semaforo.release();

            if (escudeiraJaSelecionada) {
                darVoltasNaPista();
                exibirTabelaResultado();
            }
        }
    }

    private void selecionarCarrosParaPista() {
        escudeiraJaSelecionada = escudeiraEmPista();

        if (!escudeiraJaSelecionada) {
            colocarCarroEmPista();
        }
    }

    private boolean escudeiraEmPista() {
        for (int i = 0; i < carrosNaPista.length(); i++) {
            if (carrosNaPista.get(i) == escuderia) {
                return true;
            }
        }
        return false;
    }

    private void colocarCarroEmPista() {
        for (int i = 0; i < carrosNaPista.length(); i++) {
            if (carrosNaPista.get(i) == 0) {
                carrosNaPista.set(i, escuderia);
                break;
            }
        }
    }

    public void darVoltasNaPista() {
        final int QUANTIDADE_VOLTAS = 3;
        int voltaMaisRapida = 0;

        int tempoVoltaAtual;
        for (int i = 0; i < QUANTIDADE_VOLTAS; i++) {
            tempoVoltaAtual = gerarTempoDeVoltaAleatorio();
            System.out.println("#Escuderia [" + escuderia + "] >>> " + (i + 1) + "a. volta = " + tempoVoltaAtual + "ms.");

            //Atualiza a volta mais rápida
            if (tempoVoltaAtual < voltaMaisRapida || voltaMaisRapida == 0) {
                voltaMaisRapida = tempoVoltaAtual;
            }
        }

        armazenarVoltaMaisRapida(voltaMaisRapida);
    }

    public int gerarTempoDeVoltaAleatorio() {
        int SEGUNDOS_MINIMO = 60;
        int SEGUNDOS_MAXIMO = 121;
        int segundosAleatorio = ThreadLocalRandom.current().nextInt(SEGUNDOS_MINIMO,SEGUNDOS_MAXIMO);

        return segundosAleatorio * 1000;
    }

    private void armazenarVoltaMaisRapida(int voltaMaisRapida) {
        int finalistas = quantidadeFinalistas.getAndIncrement();
        tabelaFinalistas[finalistas][0] = escuderia;
        tabelaFinalistas[finalistas][1] = voltaMaisRapida;
    }

    public void ordenarSegundaColunaCrescentemente() {
        Arrays.sort(tabelaFinalistas, Comparator.comparingInt(a -> a[1]));
    }

    public void exibirTabelaResultado() {
        if (quantidadeFinalistas.get() == 5) {
            ordenarSegundaColunaCrescentemente();

            System.out.println("\n+=========================+");
            System.out.println("|   VOLTAS MAIS RAPIDAS   |");
            System.out.println("+============+============+");
            System.out.println("| Escuderia  | Tempo      |");
            System.out.println("+============+============+");
            for (int[] linha : tabelaFinalistas) {
                System.out.printf("| %-10d | %-10d |%n", linha[0], linha[1]);
            }
            System.out.println("+============+============+");
        }
    }
}
