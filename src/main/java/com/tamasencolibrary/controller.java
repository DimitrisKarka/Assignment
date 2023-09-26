package com.tamasencolibrary;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;

public class controller extends AbstractVerticle {

    private HttpServer server; // Server declaration outside the start so that it is accessible from everywhere
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new controller()); // Verticle deployment
    }

    @Override
    public void start() {
        server = vertx.createHttpServer();//server initializaton
        Router router = Router.router(vertx); // Router creation
        MySQLConnectOptions connectOptions = new MySQLConnectOptions()//database conection
        .setPort(3306)
        .setHost("localhost")
        .setDatabase("Tamanesco_lending_library")
        .setUser("admin")
        .setPassword("12345678910");
        PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
        SqlClient client = MySQLPool.client(vertx, connectOptions, poolOptions);


        router.get().handler(this :: homePage); //runs automatically in port 8080 or another default port if chosen
        router.put("/people/add").handler(this::addPeople);
        router.put("/book/add").handler(this::addBook);
        server.requestHandler(router).listen(8080, result -> {
            if (result.succeeded()) {
                System.out.println("HTTP server started on port 8080");
            } else {
                System.err.println("HTTP server failed to start: " + result.cause());
            }
        });
    }

    private void homePage(io.vertx.ext.web.RoutingContext routingContext){
        HttpServerResponse response = routingContext.response();
        response.end("This is the homepage of Tamasenco lending library\nPlease type one of the bellow http requests"+
        "\n\nPeople API:/people/add?id=&name=&role=\nPeople API:/people/delete?id=\nPeople API:/people/alter?id=&name=&role"+
        "\n\nBook API:/book/add?ISBN=&title=&Author\nBook API:/book/delete?ISBN=\nBook API:/book/alter?ISBN=&title=&Author"+
        "\n\nLending API:/lendrequest?id=&nameofbook=\nLending API:/returnbook?id=&nameofbook="); 
    }
    private void addPeople(io.vertx.ext.web.RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        response.end("we are at the people add api post choice"); 
    }
    private void addBook(io.vertx.ext.web.RoutingContext routingContext){
        HttpServerResponse response = routingContext.response();
        response.end("we are at the book add api post choice"); 
    }


}
