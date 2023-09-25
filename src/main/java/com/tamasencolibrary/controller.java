package com.tamasencolibrary;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;

public class controller extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new controller()); // Verticle deployment
    }

    @Override
    public void start() {
        HttpServer server = vertx.createHttpServer(); // Server creation
        Router router = Router.router(vertx); // Create a router

        // Define a route that handles the "/helloworld" path
        router.route().handler(this :: homePage);
        router.route("/helloworld").handler(this::helloWorldRequest);

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
        response.end("This is the homepage of tamasenco lending library\nPlease type one of the bellow http requests"+
        "\n\nPeople API:/add?id=&name=&role=\nPeople API:/delete?id=\nPeople API:/alter?id=&name=&role"+
        "\n\nBook API:/add?ISBN=&title=&Author\nBook API:/delete?ISBN=\nBook API:/alter?ISBN=&title=&Author"+
        "\n\nLending API:/lendrequest?id=&nameofbook=\nLending API:/returnbook?id=&nameofbook="); 
    }

    private void helloWorldRequest(io.vertx.ext.web.RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        response.end("Hello, World!"); // Send "Hello, World!" as the response
    }
}
