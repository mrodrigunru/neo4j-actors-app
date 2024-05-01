package app;

import java.util.Map;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.QueryConfig;
import org.neo4j.driver.RoutingControl;

public class App {

    private static final String URI = "neo4j+s://0ed877d4.databases.neo4j.io";
    private static final String USER = "neo4j";
    private static final String PASSWORD = "6bm1gYJ9KfwdAbq_g5C_r6XW0sb13SM94duC2XWIIwQ";

    public static void main(String[] args) {

        try (var driver = GraphDatabase.driver(URI, AuthTokens.basic(USER, PASSWORD))) {
            driver.verifyConnectivity();

        }
    }
}
