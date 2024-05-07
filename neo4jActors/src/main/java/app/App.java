package app;

import java.util.Map;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import static org.neo4j.driver.Values.parameters;

public class App {

    private static final String URI = "neo4j+s://0ed877d4.databases.neo4j.io";
    private static final String USER = "neo4j";
    private static final String PASSWORD = "6bm1gYJ9KfwdAbq_g5C_r6XW0sb13SM94duC2XWIIwQ";

    public static void main(String[] args) {

        boolean exit = false;
        try (var driver = GraphDatabase.driver(URI, AuthTokens.basic(USER, PASSWORD))) {
            driver.verifyConnectivity();
            while (!exit) {


                Scanner scanner = new Scanner(System.in);

                System.out.println("Eliga el modo de búsqueda de recomendaciones:  \n 1.Recomendacion de actores \n 2.Recomendacion de peliculas ");

                String modo = scanner.nextLine();

                if (modo.equals("salir")) break;

                switch (modo) {
                    case "1":
                        try (Session session = driver.session()) {
                            System.out.println("Para salir, escriba salir.\n");
                            System.out.println("Ingrese el nombre de un actor o actriz: ");

                            String nomActor = scanner.nextLine();

                            if (nomActor.equals("salir")) break;

                            System.out.println("\nIngrese una pelicula en la que haya actuado ese actor o actriz: ");

                            String nomPelicula = scanner.nextLine();

                            if (nomPelicula.equals("salir")) break;

                            var result = driver.session()
                                    .run("MATCH (p:Actor {name: $name})-[:ACTED_WITH {movie:$movie}]-(a2:Actor) RETURN a2.name AS nombre, a2.data AS datos ",
                                            parameters("name", nomActor, "movie", nomPelicula));


                            System.out.println("Actor/es o actriz/actrices recomendado(s)");
                            // Iterar directamente sobre el resultado
                            for (Record record : result.list()) {
                                String nombre = record.get("nombre").asString();
                                String datos = record.get("datos").asString();
                                System.out.println("Actor: " + nombre + "\nDatos: " + datos + "\n");
                            }


                        }catch (Exception e){
                            System.out.println("Ha ocurrido un error.");
                        }
                        break;

                    case "2":

                        try (Session session = driver.session()) {

                            System.out.println("Para salir, escriba salir.\n");
                            System.out.println("Ingrese el nombre de un actor o actriz: ");

                            String nomActor = scanner.nextLine();

                            if (nomActor.equals("salir")) break;

                            System.out.println("\nIngrese otro actor que ha trabajado con el anterior: ");

                            String nomActor2 = scanner.nextLine();

                            if (nomActor2.equals("salir")) break;

                            var result = driver.session()
                                    .run("MATCH (a1:Actor {name: $name1})-[r:ACTED_WITH]-(a2:Actor {name: $name2})\n" +
                                                    "RETURN r.movie AS pelicula",
                                            parameters("name1", nomActor, "name2", nomActor2));


                            // Iterar directamente sobre el resultado
                            for (Record record : result.list()) {
                                String pelicula = record.get("pelicula").asString();
                                // String datos = record.get("datos").asString();
                                System.out.println("Pelicula recomendada: " + pelicula);
                            }
                        }catch (Exception e){
                            System.out.println("Ha ocurrido un error.");
                        }
                        break;
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
//consulta de nivel 2: dado dos actores devuelve su pelicula y con esa pelicula dame el resto de actores de la misma

    }
}
