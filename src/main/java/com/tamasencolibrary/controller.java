package com.tamasencolibrary;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RequestBody;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;

public class controller extends AbstractVerticle {

    private HttpServer server; // Server declaration outside the start so that it is accessible from everywhere
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new controller()); // main Verticle deployment(controller)
        vertx.deployVerticle(new model());//model verticle deployed

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

        router.get("/home").handler(this :: homePage); //runs automatically in port 8080 or another default port if chosen
        //router paths for PeopleAPI
        router.put("/people/add").handler(this::addPeople);
        router.delete("/people/delete").handler(this::deletePeople);
        router.post("/people/alter").handler(this::alterPeople);
        //router paths for Book API
        router.put("/book/add").handler(this::addBook);
        router.delete("/book/delete").handler(this::deleteBook);
        router.post("/book/alter").handler(this::alterBook);
        //router paths for Lending API
        router.get("/lendrequest").handler(this::lendRequest);
        router.post("/returnbook").handler(this::returnBook);//it is not PUT beacuse if the book gets returned even if the count is currenlty 0, after the return its still an alteration not an addition


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
        "\n\nBook API:/book/add?ISBN=&title=&author&count=\nBook API:/book/delete?ISBN=\nBook API:/book/alter?ISBN=&title=&Author"+
        "\n\nLending API:/lendrequest?id=&nameofbook=\nLending API:/returnbook?id=&nameofbook="); 
    }

    /*needs some orgnazitaion. Either three separate file to obey the mvc principles
     * or one file here but again organize it so that the mvc principles are followed
    */

    //people API
    private void addPeople(io.vertx.ext.web.RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();
        HttpServerResponse response = routingContext.response();
        request.bodyHandler(body -> {
            String requestBody = body.toString(); 
            JsonObject json = new JsonObject(requestBody); // *** the data must be in JSON fashion to be parsed
            vertx.eventBus().send("model.addPeople", json);//event bus senting for the model
            response.setStatusCode(200).end("people fields sent for addition to model successfully");//this should be in view probably
        });
    }
    private void deletePeople(io.vertx.ext.web.RoutingContext routingContext){
        HttpServerRequest request = routingContext.request();
        HttpServerResponse response = routingContext.response();
        request.bodyHandler(body -> {
            String requestBody = body.toString(); 
            JsonObject json = new JsonObject(requestBody);
            vertx.eventBus().send("model.deletePeople", json);
            response.setStatusCode(200).end("person id sent for deletion to model successfully");
        });
    }
    private void alterPeople(io.vertx.ext.web.RoutingContext routingContext){
        HttpServerRequest request = routingContext.request();
        HttpServerResponse response = routingContext.response();
        request.bodyHandler(body -> {
            String requestBody = body.toString(); 
            JsonObject json = new JsonObject(requestBody); 
            vertx.eventBus().send("model.alterPeople", json);
            response.setStatusCode(200).end("people fields sent for alteration to model successfully");
        });
    }
    //book API
    private void addBook(io.vertx.ext.web.RoutingContext routingContext){
        HttpServerRequest request = routingContext.request();
        HttpServerResponse response = routingContext.response();
        request.bodyHandler(body -> {
            String requestBody = body.toString(); 
            JsonObject json = new JsonObject(requestBody); 
            vertx.eventBus().send("model.addBook", json);
            response.setStatusCode(200).end("book fields sent for addition to model successfully");
        });
    }
    private void deleteBook(io.vertx.ext.web.RoutingContext routingContext){
        HttpServerRequest request = routingContext.request();
        HttpServerResponse response = routingContext.response();
        request.bodyHandler(body -> {
            String requestBody = body.toString(); 
            JsonObject json = new JsonObject(requestBody);
            vertx.eventBus().send("model.deleteBook", json);
            response.setStatusCode(200).end("book deletion sent to model successfully");
        });
    }
    private void alterBook(io.vertx.ext.web.RoutingContext routingContext){
        HttpServerRequest request = routingContext.request();
        HttpServerResponse response = routingContext.response();
        request.bodyHandler(body -> {
            String requestBody = body.toString(); 
            JsonObject json = new JsonObject(requestBody); 
            vertx.eventBus().send("model.alterBook", json);
            response.setStatusCode(200).end("book fields for alteration sent to model successfully");//this should be in view probably
        });
    }
    //lending API
    private void lendRequest(io.vertx.ext.web.RoutingContext routingContext){
        HttpServerRequest request = routingContext.request();
        HttpServerResponse response = routingContext.response();
        request.bodyHandler(body -> {
            String requestBody = body.toString(); 
            JsonObject json = new JsonObject(requestBody); 
            vertx.eventBus().send("model.lendRequest", json);
            response.setStatusCode(200).end("lend request sent to model successfully");//this should be in view probably
        });
    }
    private void returnBook(io.vertx.ext.web.RoutingContext routingContext){
        HttpServerRequest request = routingContext.request();
        HttpServerResponse response = routingContext.response();
        request.bodyHandler(body -> {
            String requestBody = body.toString(); 
            JsonObject json = new JsonObject(requestBody); 
            vertx.eventBus().send("model.returnBook", json);
            response.setStatusCode(200).end("return of book sent to model successfully");//this should be in view probably
        });
    }

}
