package app;

import java.util.Map;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.QueryConfig;
import org.neo4j.driver.Record;
import org.neo4j.driver.RoutingControl;

import static org.neo4j.driver.Values.parameters;

public class App {

    private static final String URI = "neo4j+s://0ed877d4.databases.neo4j.io";
    private static final String USER = "neo4j";
    private static final String PASSWORD = "6bm1gYJ9KfwdAbq_g5C_r6XW0sb13SM94duC2XWIIwQ";

    public static void main(String[] args) {

        try (var driver = GraphDatabase.driver(URI, AuthTokens.basic(USER, PASSWORD))) {
            driver.verifyConnectivity();

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

        } catch (Exception e){
            e.printStackTrace();
        }







    }
}
