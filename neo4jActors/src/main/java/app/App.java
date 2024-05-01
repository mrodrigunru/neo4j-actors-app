package app;

import java.util.Map;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import static org.neo4j.driver.Values.parameters;

public class App {

    private static final String URI = "neo4j+s://0ed877d4.databases.neo4j.io";
    private static final String USER = "neo4j";
    private static final String PASSWORD = "6bm1gYJ9KfwdAbq_g5C_r6XW0sb13SM94duC2XWIIwQ";

    public static void main(String[] args) {

        try (var driver = GraphDatabase.driver(URI, AuthTokens.basic(USER, PASSWORD))) {
            driver.verifyConnectivity();

            List<Map> people = List.of(
                    Map.of("name", "Alice", "age", 42, "friends", List.of("Bob", "Peter", "Anna")),
                    Map.of("name", "Bob", "age", 19),
                    Map.of("name", "Peter", "age", 50),
                    Map.of("name", "Anna", "age", 30)
            );

            //Crear Nodos
            try (Session session = driver.session()) {

                people.forEach(person -> {
                    var result = driver.executableQuery("CREATE (p:Person {name: $person.name, age: $person.age})")
                            .withConfig(QueryConfig.builder().withDatabase("neo4j").build())
                            .withParameters(Map.of("person", person))
                            .execute();
                });

                //Crear relaciones
                people.forEach(person -> {
                    if(person.containsKey("friends")) {
                        var result = driver.executableQuery("""
                            MATCH (p:Person {name: $person.name})
                            UNWIND $person.friends AS friend_name
                            MATCH (friend:Person {name: friend_name})
                            CREATE (p)-[:KNOWS]->(friend)
                             """)
                                .withConfig(QueryConfig.builder().withDatabase("neo4j").build())
                                .withParameters(Map.of("person", person))
                                .execute();
                    }
                });

                // migos de la alice que tienen menos de 40
                var result = driver.session()
                        .run("MATCH (p:Person {name: $name})-[:KNOWS]-(friend:Person) " +
                                        "WHERE friend.age < $age " +
                                        "RETURN friend ",
                                parameters("name", "Alice", "age", 40));

                // Iterar directamente sobre el resultado
                for (Record record : result.list()) {
                    String friendName = record.get("friend").get("name").asString();
                    int friendAge = record.get("friend").get("age").asInt();
                    System.out.println("Nombre: " + friendName + ", Edad: " + friendAge);
                }



                // Ejecutar la consulta Cypher para eliminar todos los nodos y relaciones
                session.run("MATCH (n) DETACH DELETE n");
                System.out.println("Se han eliminado todos los nodos y relaciones de la base de datos.");

            }


        } catch (Exception e){
            e.printStackTrace();
        }







    }
}
