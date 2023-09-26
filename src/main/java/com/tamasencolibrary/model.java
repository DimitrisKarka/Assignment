package com.tamasencolibrary;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

public class model extends AbstractVerticle{
    
    @Override
    public void start(){

        vertx.eventBus().consumer("model.addPeople", message -> {
            JsonObject data = (JsonObject) message.body();
            Integer id = data.getInteger("id");
            String name = data.getString("name");
            String role = data.getString("role");
            System.out.println(id + " " + name + " " + role);
        }); 
        vertx.eventBus().consumer("model.deletePeople", message -> {
            JsonObject data = (JsonObject) message.body();
            Integer id = data.getInteger("id");
            System.out.println(id + " id for deletion");
        }); 
        vertx.eventBus().consumer("model.alterPeople", message -> {//**if a field is null there will be no alteration in that specific field
            JsonObject data = (JsonObject) message.body();
            Integer id = data.getInteger("id");
            String name = data.getString("name");
            String role = data.getString("role");
            System.out.println(id + " " + name + " " + role);
        }); 

        vertx.eventBus().consumer("model.addBook", message -> {
            JsonObject data = (JsonObject) message.body();
            // Process the data received from the event bus
            String ISBN = data.getString("ISBN");
            String title = data.getString("title");
            String author = data.getString("author");
            Integer count = data.getInteger("count");
            System.out.println(ISBN + " " + title + " " + author + " " + count);
        }); 
        vertx.eventBus().consumer("model.deleteBook", message -> {
            JsonObject data = (JsonObject) message.body();
            String ISBN = data.getString("ISBN");
            System.out.println(ISBN + " ISBN is for deletion");
        }); 
        vertx.eventBus().consumer("model.alterBook", message -> {//**if a field is null there will be no alteration in that specific field
            JsonObject data = (JsonObject) message.body();
            // Process the data received from the event bus
            String ISBN = data.getString("ISBN");
            String title = data.getString("title");
            String author = data.getString("author");
            Integer count = data.getInteger("count");
            System.out.println(ISBN + " " + title + " " + author + " " + count);
        }); 

        vertx.eventBus().consumer("model.lendRequest", message -> {
            JsonObject data = (JsonObject) message.body();
            Integer id = data.getInteger("id");
            String title = data.getString("title");
            System.out.println(id + " wants to lend book with title " + title);
        });
        vertx.eventBus().consumer("model.returnBook", message -> {
            JsonObject data = (JsonObject) message.body();
            Integer id = data.getInteger("id");
            String title = data.getString("title");
            System.out.println(id + " returned book with title " + title + " make sure to put it back to the databaser");
        }); 
        
        
    }
}
        