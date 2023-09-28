package com.tamasencolibrary;

import java.util.LinkedList;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
//import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class controller extends AbstractVerticle {

    private HttpServer server; // Server declaration outside the start so that it is accessible from everywhere
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new controller()); // main Verticle deployment(controller)
        vertx.deployVerticle(new model());//model verticle deployed
        vertx.deployVerticle(new view());//view verticle deployed

    }

    @Override
    public void start() {
        server = vertx.createHttpServer();//server initializaton
        Router router = Router.router(vertx); // Router creatiom
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
        router.get("/showallbooks").handler(this::showAllBooks);
        router.post("/lendrequest").handler(this::lendRequest);
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
        response.end("This is the homepage of Tamasenco lending library (GET) (http://localhost:8080/home)\nPlease type one of the bellow http requests"+
        "\n\nPeople API (PUT):/people/add?id=&name=&role=\nPeople API (POST) :/people/delete?id=\nPeople API (POST) :/people/alter?id=&name=&role="+
        "\n\nBook API (PUT) :/book/add?ISBN=&title=&author&count=\nBook API (POST) :/book/delete?title=\nBook API (POST) :/book/alter?ISBN=&title=&Author=&count="+
        "\n\nLending API (GET) :/showallbooks\nLending API (POST) :/lendrequest?id=&nameofbook=&lendingdate=\nLending API (POST) :/returnbook?id=&nameofbook="); 
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
            vertx.eventBus().send("model.addPeople", json);
            response.setStatusCode(200).end("people fields sent for addition to model successfully");//should this be in view probably? or not beacuse it is just a message and no organised data?
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
    private void showAllBooks(io.vertx.ext.web.RoutingContext routingContext){
        HttpServerRequest request = routingContext.request();
        HttpServerResponse response = routingContext.response();
        request.bodyHandler(body -> {
            String requestBody = body.toString(); 
            JsonObject json = new JsonObject(requestBody); 
            vertx.eventBus().send("model.showAllBooks", json);//event bus sents the request to model
            vertx.eventBus().consumer("controller.showAllBooks", allBooks ->{//event bus returns the data (all books) in a linked list
                LinkedList<String> allBooksRaw = (LinkedList<String>) allBooks.body();

                /****** From here on and below i just could not understand why it was not working *******

                vertx.eventBus().send("view.showAllBooks",  allBooksRaw);//event bus sends the books to view as raw data
                try {
                    vertx.eventBus().consumer(" controller.recieveBooks", allBooksJSON -> {//event bus recieves all books in JSON
                        if (allBooksJSON != null) {
                            JsonArray allBooksProccesed = new JsonArray();
                            allBooksProccesed = (JsonArray) allBooksJSON;
                            response.setStatusCode(200);
                            response.putHeader("Content-Type", "application/json");
                            response.end(allBooksProccesed.encode());//response sent to endpoint in JSON format
                        } else {
                            response.end("Message body is empty or invalid.");
                        }   
                    });
                } catch (Exception e) {
                    e.printStackTrace(); // never entered here so it looks like the data from the event bus were arriving fine in the vertx.eventBus().consumer(" controller.recieveBooks", allBooksJSON line????
                }    

                //so i never sent the data in view as it should be done and just prossecced it below
                //and send them direclty to the endpoint...

                */
                JsonArray allBooksFinal = new JsonArray();
                while(!allBooksRaw.isEmpty()){
                    JsonObject book = new JsonObject();
                    book.put("isbn", allBooksRaw.poll());
                    book.put("title", allBooksRaw.poll());
                    book.put("author", allBooksRaw.poll());
                    book.put("count", allBooksRaw.poll());
                    allBooksFinal.add(book); 
                }
                response.setStatusCode(200);
                response.putHeader("Content-Type", "application/json");
                response.end(allBooksFinal.encode());
            });
        });
    }
    private void lendRequest(io.vertx.ext.web.RoutingContext routingContext){//here i used the view to retuern back the title in a proccessed title message just to understand the proccess of communicating via event busses and MVC better
        HttpServerRequest request = routingContext.request();
        HttpServerResponse response = routingContext.response();
        request.bodyHandler(body -> {
            String requestBody = body.toString(); 
            JsonObject json = new JsonObject(requestBody); 
            vertx.eventBus().send("model.lendRequest", json);//event bus sents the request to model
            vertx.eventBus().consumer("controller.lendRequest", message ->{//event bus returns data(title) from model
                    System.out.println("");//there are issues with buffering i guess that i dont understand...this println is needed here for the event buses to work
                    vertx.eventBus().send("view.lendRequest", message.body());//event bus sends data(title) to view
                    vertx.eventBus().consumer("controller.completeMessageForEnd", messageForEnd ->{//events bus returns altered data "endpoint message" from view
                        response.setStatusCode(200);
                        response.putHeader("Content-Type", "text/plain");
                        Object messageBody = messageForEnd.body();
                        if (messageBody != null) {
                            response.end(messageBody.toString()); // controller sends data (endpoint message ) to endpoint
                        } else {
                            response.end("Message body is empty or invalid.");
                        }
                    });
            });
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
