package view;

import controller.ThreadCarro;

import java.util.concurrent.Semaphore;

public class Executar {
    public static void main(String[] args) {
        final int CARROS_POR_PISTA = 5;
        final int TOTAL_DE_ESCUDERIAS = 7;
        final int TOTAL_DE_CARROS = 14;

        Semaphore semaforoPista = new Semaphore(CARROS_POR_PISTA);
        Semaphore[] semaforoPorEscuderia = new Semaphore[TOTAL_DE_ESCUDERIAS];
        iniciarSemaforosEscuderia(semaforoPorEscuderia);

        ThreadCarro[] carros = new ThreadCarro[TOTAL_DE_CARROS];

        int escuderia = -1; //Vai de 0 a 6, cada valor se repete 2x
        for (int i = 0; i < TOTAL_DE_CARROS; i++) {
            if (i % 2 == 0) {
                escuderia += 1;
            }

            carros[i] = new ThreadCarro(i, escuderia, semaforoPorEscuderia, semaforoPista);
        }

        for (ThreadCarro atual : carros) {
            atual.start();
        }
    }

    public static void iniciarSemaforosEscuderia(Semaphore[] semaforosEscuderia) {
        final int ESCUDERIAS_POR_PISTA = 1;
        for (int i = 0; i < semaforosEscuderia.length; i++) {
            semaforosEscuderia[i] = new Semaphore(ESCUDERIAS_POR_PISTA);
        }
    }
}
