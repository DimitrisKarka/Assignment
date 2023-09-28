package com.tamasencolibrary;

//import java.util.LinkedList;
import io.vertx.core.AbstractVerticle;
//import io.vertx.core.json.JsonArray;
//import io.vertx.core.json.JsonObject;

public class view extends AbstractVerticle{
    
    @Override
    public void start(){
        /*

        //the below code is the one i cant combine together with the controller via the event bus so it
        //is duplicate in the controller

        vertx.eventBus().consumer("view.showAllBooks", rawBooks -> {
            LinkedList<String> rawData = (LinkedList<String>) rawBooks.body();
            JsonArray allBooksFinal = new JsonArray();
            while(!allBooksRaw.isEmpty()){
                JsonObject book = new JsonObject();
                // Populate the book object
                book.put("isbn", allBooksRaw.poll());
                book.put("title", allBooksRaw.poll());
                book.put("author", allBooksRaw.poll());
                book.put("count", allBooksRaw.poll());
                allBooksFinal.add(book); // Add the book object to the jsonArray
            }
            try{
                vertx.eventBus().send("controller.recieveBooks", allBooksFinal);
            }
            catch (Exception e) {
                e.printStackTrace();// this prints nothing so i ges the vertx.eventBus().send i running?
            }    
        });
        */
        vertx.eventBus().consumer("view.lendRequest", message -> {
            String completeMessageForEnd = "Book with title : \"" + message.body().toString() + "\" has been succesfully lent!";
            vertx.eventBus().send("controller.completeMessageForEnd", completeMessageForEnd);
        });

    }
}