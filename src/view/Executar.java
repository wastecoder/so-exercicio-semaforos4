package view;

import controller.ThreadCarro;

import java.util.concurrent.Semaphore;

public class Executar {
    public static void main(String[] args) {
        Semaphore semaforo = new Semaphore(1);

        final int QUANTIDADE_ESCUDERIAS = 7;
        final int CARROS_POR_ESCUDERIAS = 2;
        final int TOTAL_DE_CARROS = QUANTIDADE_ESCUDERIAS * CARROS_POR_ESCUDERIAS;
        ThreadCarro[] carros = new ThreadCarro[TOTAL_DE_CARROS];

        int escuderia = 0; //Vai de 10 a 70
        for (int i = 0; i < TOTAL_DE_CARROS; i++) {
            if (i % 2 == 0) {
                escuderia += 10;
            }

            carros[i] = new ThreadCarro(escuderia, semaforo);
        }

        for (ThreadCarro atual : carros) {
            atual.start();
        }
    }
}
