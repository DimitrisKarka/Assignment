package com.tamasencolibrary;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

public class model extends AbstractVerticle{
    
    @Override
    public void start(){

        MySQLConnectOptions connectOptions = new MySQLConnectOptions()//database conection
        .setPort(3306)
        .setHost("localhost")
        .setDatabase("Tamanesco_lending_library")
        .setUser("admin")
        .setPassword("12345678910");
        PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
        SqlClient client = MySQLPool.client(vertx, connectOptions, poolOptions);

        vertx.eventBus().consumer("model.addPeople", message -> {
            JsonObject data = (JsonObject) message.body();
            // Process the data received from the event bus
            Integer id = data.getInteger("id");
            String name = data.getString("name");
            String role = data.getString("role");
            String sql = "INSERT INTO people (id, name, role) VALUES (?, ?, ?)";
            client.preparedQuery(sql).execute(Tuple.of(id, name, role), queryResult -> {
                if (queryResult.succeeded()) {
                    System.out.println("query success");
                } else {
                    Throwable exception = queryResult.cause();
                    exception.printStackTrace(); 
                }
            });
        }); 
        vertx.eventBus().consumer("model.deletePeople", message -> {
            JsonObject data = (JsonObject) message.body();
            Integer id = data.getInteger("id");
            String sql = "SELECT EXISTS(SELECT 1 FROM people WHERE id = ?) AS id_exists";//checking if th id exists
            client.preparedQuery(sql).execute(Tuple.of(id), queryResult -> {
                if (queryResult.succeeded()) {
                    Boolean idExists = queryResult.result().iterator().next().getBoolean("id_exists");
                    if (idExists) {
                        String sql2 = "DELETE FROM people WHERE id = (?)";
                        client.preparedQuery(sql2).execute(Tuple.of(id), queryResult2 -> {
                        if (queryResult.succeeded()) {
                            System.out.println("query success");
                        } else {
                            Throwable exception = queryResult.cause();//query failure if it enters here
                            exception.printStackTrace(); 
                        }
                        });
                    } else {
                    System.out.println("ID does not exist");
                }
                } else {
                    Throwable exception = queryResult.cause();//query failure if it enters here
                    exception.printStackTrace(); 
                }
            });
        }); 
        vertx.eventBus().consumer("model.alterPeople", message -> {
            JsonObject data = (JsonObject) message.body();
            Integer id = data.getInteger("id");
            String name = data.getString("name");
            String role = data.getString("role");
            String sql = "SELECT EXISTS(SELECT 1 FROM people WHERE id = ?) AS id_exists";//checking if th id exists
            client.preparedQuery(sql).execute(Tuple.of(id), queryResult -> {
                if (queryResult.succeeded()) {
                    Boolean idExists = queryResult.result().iterator().next().getBoolean("id_exists");
                    if (idExists) {
                        String sql2 = "UPDATE people SET name = (?), role = (?) WHERE id = (?)";
                        client.preparedQuery(sql2).execute(Tuple.of(name, role, id), queryResult2 -> {
                            if (queryResult.succeeded()) {
                                System.out.println("query success");
                            }else {
                                Throwable exception = queryResult.cause();//query failure if it enters here
                                exception.printStackTrace(); 
                            }
                        });
                    } else {
                        System.out.println("ID does not exist");
                    }
                } else {
                    Throwable exception = queryResult.cause();//query failure if it enters here
                    exception.printStackTrace(); 
                }
            });                                
        });
        vertx.eventBus().consumer("model.addBook", message -> {
            JsonObject data = (JsonObject) message.body();
            String ISBN = data.getString("ISBN");
            String title = data.getString("title");
            String author = data.getString("author");
            Integer count = data.getInteger("count");
            String sql = "INSERT INTO books (ISBN, title, author, count) VALUES (?, ?, ?, ?)";
            client.preparedQuery(sql).execute(Tuple.of(ISBN, title, author, count), queryResult -> {
                if (queryResult.succeeded()) {
                    System.out.println("query success");
                } else {
                    Throwable exception = queryResult.cause();
                    exception.printStackTrace(); 
                }
            });
        }); 
        vertx.eventBus().consumer("model.deleteBook", message -> {
            JsonObject data = (JsonObject) message.body();
            String title = data.getString("title");
            String sql = "SELECT EXISTS(SELECT 1 FROM books WHERE title = ?) AS title_exists";//checking if the title exists
            client.preparedQuery(sql).execute(Tuple.of(title), queryResult -> {
                if (queryResult.succeeded()) {
                    Boolean titleExists = queryResult.result().iterator().next().getBoolean("title_exists");
                    if (titleExists) {
                        String sql2 = "DELETE FROM books WHERE title = (?)";//no need to delete using the ISBN it just feels harder
                        client.preparedQuery(sql2).execute(Tuple.of(title), queryResult2 -> {
                            if (queryResult.succeeded()) {
                                System.out.println("query success");
                            } else {
                                Throwable exception = queryResult.cause();//query failure if it enters here
                                exception.printStackTrace(); 
                            }
                        });
                    } else {
                        System.out.println("title does not exist");
                    }
                } else {
                    Throwable exception = queryResult.cause();//query failure if it enters here
                    exception.printStackTrace(); 
                }
            });                                
        }); 
        vertx.eventBus().consumer("model.alterBook", message -> {
            JsonObject data = (JsonObject) message.body();
            String ISBN = data.getString("ISBN");//here if one wants to alter a book one needs to know the exact ISBN. Thats why ISBN is primary key too beacuse it is unique and there cant be typos unlike in "title"
            String title = data.getString("title");
            String author = data.getString("author");
            Integer count = data.getInteger("count");
            String sql = "SELECT EXISTS(SELECT 1 FROM books WHERE ISBN = ?) AS ISBN_exists";//checking if the ISBN exists
            client.preparedQuery(sql).execute(Tuple.of(ISBN), queryResult -> {
                if (queryResult.succeeded()) {
                    Boolean ISBNExists = queryResult.result().iterator().next().getBoolean("ISBN_exists");
                    if (ISBNExists) {
                        String sql2 = "UPDATE books SET title = (?), author = (?), count = (?) WHERE ISBN = (?)";
                        client.preparedQuery(sql2).execute(Tuple.of(title, author, count, ISBN), queryResult2 -> {
                            if (queryResult.succeeded()) {
                                System.out.println("query success");
                            } else {
                                Throwable exception = queryResult.cause();//query failure if it enters here
                                exception.printStackTrace(); 
                            }
                        });
                    } else {
                        System.out.println("ISBN does not exist");
                    }
                } else {
                    Throwable exception = queryResult.cause();//query failure if it enters here
                    exception.printStackTrace(); 
                }
            });                    
        }); 
        vertx.eventBus().consumer("model.lendRequest", message -> {
            JsonObject data = (JsonObject) message.body();
            Integer id = data.getInteger("id");
            String title = data.getString("title");
            System.out.println(id + " wants to lend book with title " + title);
            if(id != null){

            }
        });
        vertx.eventBus().consumer("model.returnBook", message -> {
            JsonObject data = (JsonObject) message.body();
            Integer id = data.getInteger("id");
            String title = data.getString("title");
            System.out.println(id + " returned book with title " + title + " make sure to put it back to the databaser");
        }); 
        
        
    }
}
        