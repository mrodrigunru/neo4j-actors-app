package app;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.util.InputMismatchException;
import java.util.Scanner;

import static org.neo4j.driver.Values.parameters;

public class MovieServices {

    private static final String URI = "neo4j+s://0ed877d4.databases.neo4j.io";
    private static final String USER = "neo4j";
    private static final String PASSWORD = "6bm1gYJ9KfwdAbq_g5C_r6XW0sb13SM94duC2XWIIwQ";

    private static MovieServices instance;
    private final Driver driver;
    private final Scanner scanner;

    public MovieServices() {
        scanner = new Scanner(System.in);
        driver = connect();
    }

    public static MovieServices getInstance() {
        if (instance == null)
            instance = new MovieServices();
        return instance;
    }

    public Driver connect() {
        System.out.println("Conectando con la base de datos...\n");
        Driver driverDB = null;

        try {
            driverDB = GraphDatabase.driver(URI, AuthTokens.basic(USER, PASSWORD));
            driverDB.verifyConnectivity();
        } catch (Exception e) {
            System.out.println("Ha ocurrido un error al conectar con la base de datos. Finalizando...");
            if (driver != null)
                driver.close();
            System.exit(1);
        }

        return driverDB;
    }

    public void disconnect() {
        scanner.close();
        driver.close();
    }

    public void actorRecommendation() {
        try {
            System.out.println("Ingrese el nombre de un actor/actriz (o escriba \"Salir\" para abortar): ");

            String nomActor = scanner.nextLine();

            if (!nomActor.equalsIgnoreCase("Salir")) {

                System.out.println("\nIngrese una película en la que haya actuado ese actor/actriz (o escriba \"Salir\" para abortar): ");

                String nomPelicula = scanner.nextLine();

                if (!nomPelicula.equalsIgnoreCase("Salir")) {
                    // Obtener actores que han colaborado con el actor indicado en la película dada.
                    var result = driver.session()
                            .run("MATCH (p:Actor {name: $name})-[:ACTED_WITH {movie:$movie}]-(a2:Actor) RETURN a2.name AS nombre, a2.data AS datos ",
                                    parameters("name", nomActor, "movie", nomPelicula));

                    if (result.hasNext()) {
                        System.out.println("Actor/es o actriz/actrices recomendado(s)");
                        // Iterar directamente sobre el resultado
                        int i = 1;
                        for (Record record : result.list()) {
                            String nombre = record.get("nombre").asString();
                            String datos = record.get("datos").asString();
                            System.out.println("\t" + i + ". - " + nombre + "\n\t\tDatos: " + datos + "\n");
                            i++;
                        }
                    } else
                        System.out.println("No se han encontrado actores que hayan trabajado con " + nomActor + " en la película " + nomPelicula + ".\n");
                }
            }

        } catch (Exception e) {
            System.out.println("Ha ocurrido un error al lanzar la consulta. Volviendo al menú principal...\n");
        }
    }

    public void commonMovies() {
        try {
            System.out.println("Ingrese el nombre de un actor/actriz (o escriba \"Salir\" para abortar): ");

            String nomActor = scanner.nextLine();

            if (!nomActor.equalsIgnoreCase("Salir")) {

                System.out.println("\nIngrese otro actor que haya trabajado con el anterior (o escriba \"Salir\" para abortar): ");

                String nomActor2 = scanner.nextLine();

                if (!nomActor2.equalsIgnoreCase("Salir")) {

                    var result = driver.session()
                            .run("MATCH (a1:Actor {name: $name1})-[r:ACTED_WITH]-(a2:Actor {name: $name2})\n" +
                                            "RETURN r.movie AS pelicula",
                                    parameters("name1", nomActor, "name2", nomActor2));

                    if (result.hasNext()) {
                        System.out.println("Peliculas en común:");
                        int i = 1;
                        for (Record record : result.list()) {
                            String pelicula = record.get("pelicula").asString();
                            // String datos = record.get("datos").asString();
                            System.out.println("\t" + i + ". - " + pelicula);
                            i++;
                        }
                    } else
                        System.out.println("No se han encontrado películas en común entre " + nomActor + " y " + nomActor2 + ".\n");
                }
            }
        } catch (Exception e) {
            System.out.println("Ha ocurrido un error al lanzar la consulta. Volviendo al menú principal...\n");
        }
    }

    public void commonMoviesRecomendation() {
        try {
            System.out.println("Ingrese el nombre de un actor/actriz (o escriba \"Salir\" para abortar): ");

            String nomActor = scanner.nextLine();

            if (!nomActor.equalsIgnoreCase("Salir")) {

                System.out.println("\nIngrese otro actor/actriz que haya trabajado con el anterior (o escriba \"Salir\" para abortar): ");

                String nomActor2 = scanner.nextLine();

                if (!nomActor2.equalsIgnoreCase("Salir")) {

                    var result = driver.session()
                            .run("MATCH (a1:Actor {name: $name1})-[r:ACTED_WITH]-(a2:Actor {name: $name2})\n" +
                                            "RETURN r.movie AS pelicula",
                                    parameters("name1", nomActor, "name2", nomActor2));

                    boolean b_movie = false;
                    int sel_movie = -1;
                    var results = result.list();
                    int i = 1;

                    while (!b_movie) {
                        System.out.println("\nSeleccione una de las películas en la que basar las recomendaciones:\n");
                        // Iterar directamente sobre el resultado
                        i = 1;
                        for (Record record : results) {
                            String pelicula = record.get("pelicula").asString();
                            // String datos = record.get("datos").asString();
                            System.out.println("\t[" + i + "] " + pelicula);
                            i++;
                        }

                        System.out.println();

                        try {
                            sel_movie = scanner.nextInt();

                            if (sel_movie > 0 && sel_movie <= results.size()) {
                                var movie = results.get(sel_movie - 1);

                                var result2 = driver.session()
                                        .run("MATCH (p:Actor {name: $name})-[:ACTED_WITH {movie:$movie}]-(a2:Actor) WHERE NOT a2.name = $name2 RETURN a2.name AS nombre, a2.data AS datos ",
                                                parameters("name", nomActor, "movie", movie.get("pelicula").asString(), "name2", nomActor2));

                                System.out.println((results.size() > 1) ? "\nActor/actriz recomendado: " : "\nActores/actrices recomendados: ");
                                // Iterar directamente sobre el resultado
                                i = 1;
                                for (Record record : result2.list()) {
                                    String nombre = record.get("nombre").asString();
                                    String datos = record.get("datos").asString();
                                    System.out.println("\t" + i + ". - " + nombre + "\n\t\tDatos: " + datos + "\n");
                                    i++;
                                }
                                b_movie = true;
                            }

                        } catch (InputMismatchException e) {
                            System.out.println("Inserte un valor numérico válido de entre los mostrados.\n");
                            sel_movie = -1;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Ha ocurrido un error al lanzar la consulta. Volviendo al menú principal...\n");
        }
    }
}
