package app;

import java.util.InputMismatchException;
import java.util.Scanner;

public class App {

    private static void printMainMenu() {
        System.out.println("Elija el modo de búsqueda de recomendaciones:");
        System.out.println("\t0. Salir");
        System.out.println("\t1.Recomendación de actores");
        System.out.println("\t2.Recomendación de peliculas");
        System.out.println("\t3.Recomendación de actores según película en común");
        System.out.println();
    }

    public static void main(String[] args) {

        boolean exit = false;
        MovieServices services = MovieServices.getInstance();
        Scanner scanner = new Scanner(System.in);
        int modo = -1;
        while (!exit) {
            printMainMenu();
            try {
                modo = scanner.nextInt();


                switch (modo) {
                    case 0:
                        exit = true;
                        System.out.println("Saliendo...\n");
                        break;
                    case 1: // Dado un actor y una película suya, devolver el resto de actores de dicha película
                        services.actorRecommendation();
                        break;

                    case 2: // Dados dos actores, devolver sus películas en común
                        services.commonMovies();
                        break;
                    case 3: // Dados 2 actores, devuelve sus películas y, tras seleccionar una, devolver el resto de actores de esta
                        services.commonMoviesRecomendation();
                        break;
                    default:
                        System.out.println("Entrada inválida. Introduzca un número según las opciones mostradas.\n");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Introduzca un número según las opciones mostradas.\n");
                scanner.nextLine();
            }
            System.out.println("\nPulse ENTER para continuar.");
            scanner.nextLine();
        }

        scanner.close();
        services.disconnect();
    }
}
