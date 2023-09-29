package com.tamasencolibrary;


import java.util.Iterator;
import java.util.LinkedList;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
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
        
        //people model communicating with the DB
        vertx.eventBus().consumer("model.addPeople", message -> {
            JsonObject data = (JsonObject) message.body();
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
            String sqlIdCheck = "SELECT EXISTS(SELECT 1 FROM people WHERE id = ?) AS id_exists";//checking if the id exists
            client.preparedQuery(sqlIdCheck).execute(Tuple.of(id), queryResultIdCheck -> {
                if (queryResultIdCheck.succeeded()) {
                    Boolean idExists = queryResultIdCheck.result().iterator().next().getBoolean("id_exists");
                    if (idExists) {
                        String sqlDelete = "DELETE FROM people WHERE id = (?)";
                        client.preparedQuery(sqlDelete).execute(Tuple.of(id), queryResultDelete -> {
                            if (queryResultDelete.succeeded()) {
                                System.out.println("query success");
                            } else {
                                Throwable exception = queryResultDelete.cause();//query failure if it enters here
                                exception.printStackTrace(); 
                            }
                        
                        });
                    }
                } else {
                    System.out.println("ID does not exist");
                    Throwable exception = queryResultIdCheck.cause();
                    exception.printStackTrace(); 
                }
            });
        }); 
        vertx.eventBus().consumer("model.alterPeople", message -> {
            JsonObject data = (JsonObject) message.body();
            Integer id = data.getInteger("id");
            String name = data.getString("name");
            String role = data.getString("role");
            String sqlIdCheck = "SELECT EXISTS(SELECT 1 FROM people WHERE id = ?) AS id_exists";
            client.preparedQuery(sqlIdCheck).execute(Tuple.of(id), queryResultIdCheck -> {
                if (queryResultIdCheck.succeeded()) {
                    Boolean idExists = queryResultIdCheck.result().iterator().next().getBoolean("id_exists");
                    if (idExists) {
                        String sqlAlter = "UPDATE people SET name = (?), role = (?) WHERE id = (?)";
                        client.preparedQuery(sqlAlter).execute(Tuple.of(name, role, id), queryResultAlter-> {
                            if (queryResultAlter.succeeded()) {
                                System.out.println("query success");
                            }else {
                                Throwable exception = queryResultAlter.cause();
                                exception.printStackTrace(); 
                            }
                        });
                    }
                    else{
                        System.out.println("ID does not exist");
                    }
                } else {
                    Throwable exception = queryResultIdCheck.cause();
                    exception.printStackTrace(); 
                }
            });                                
        });
        //book model communicating with the DB
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
            String sqlTitleCheck = "SELECT EXISTS(SELECT 1 FROM books WHERE title = ?) AS title_exists";//checking if the title exists
            client.preparedQuery(sqlTitleCheck).execute(Tuple.of(title), queryResultTitleCheck -> {
                if (queryResultTitleCheck.succeeded()) {
                    Boolean titleExists = queryResultTitleCheck.result().iterator().next().getBoolean("title_exists");
                    if (titleExists) {
                        String sqlDelete = "DELETE FROM books WHERE title = (?)";//no need to delete using the ISBN it just feels harder
                        client.preparedQuery(sqlDelete).execute(Tuple.of(title), queryResultDelete -> {
                            if (queryResultDelete.succeeded()) {
                                System.out.println("query success");
                            } else {
                                Throwable exception = queryResultDelete.cause();
                                exception.printStackTrace(); 
                            }
                        });
                    }
                } else {
                    System.out.println("title does not exist");
                    Throwable exception = queryResultTitleCheck.cause();
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
            String sqlIsbnCheck = "SELECT EXISTS(SELECT 1 FROM books WHERE ISBN = ?) AS ISBN_exists";//checking if the ISBN exists
            client.preparedQuery(sqlIsbnCheck).execute(Tuple.of(ISBN), queryResultIsbnCheck -> {
                if (queryResultIsbnCheck.succeeded()) {
                    Boolean ISBNExists = queryResultIsbnCheck.result().iterator().next().getBoolean("ISBN_exists");
                    if (ISBNExists) {
                        String sqlUpdate = "UPDATE books SET title = (?), author = (?), count = (?) WHERE ISBN = (?)";
                        client.preparedQuery(sqlUpdate).execute(Tuple.of(title, author, count, ISBN), queryResultUpdate -> {
                            if (queryResultUpdate.succeeded()) {
                                System.out.println("query success");
                            } else {
                                Throwable exception = queryResultUpdate.cause();
                                exception.printStackTrace(); 
                            }
                        });
                    }
                } else {
                    System.out.println("ISBN does not exist");
                    Throwable exception = queryResultIsbnCheck.cause();
                    exception.printStackTrace(); 
                }
            });                    
        });
        //lending model communicating with the DB
        vertx.eventBus().consumer("model.showAllBooks", message -> {
            String sqlAllBooks = "SELECT * FROM books";
            client.preparedQuery(sqlAllBooks).execute( queryResultAllBooks -> {
                if (queryResultAllBooks.succeeded()){
                    RowSet<Row> result = queryResultAllBooks.result();
                    LinkedList <String> rawData = new LinkedList<>();
                    for (Row row : result) {
                        String isbn = row.getString("isbn");
                        rawData.add(isbn);
                        String title = row.getString("title");
                        rawData.add(title);
                        String author = row.getString("author");
                        rawData.add(author);
                        Integer count = row.getInteger("count");  
                        rawData.add(Integer.toString(count));            
                    }
                    vertx.eventBus().send("controller.showAllBooks", rawData);////event bus returns data back to controller
                }
                else {
                    Throwable exception = queryResultAllBooks.cause();
                    exception.printStackTrace();
                }
            });
        });
        //the below code for model.lendRequest is WAYYY too much nested definitely not good code. Still works i think
        vertx.eventBus().consumer("model.lendRequest", message -> {
            JsonObject data = (JsonObject) message.body();
            Integer id = data.getInteger("id");
            String sqlIdCheck = "SELECT EXISTS(SELECT 1 FROM people WHERE id = ?) AS id_exists";
            client.preparedQuery(sqlIdCheck).execute(Tuple.of(id), queryResultIdCheck -> {
                if (queryResultIdCheck.succeeded()) {
                    Boolean idExists = queryResultIdCheck.result().iterator().next().getBoolean("id_exists");
                    if (idExists) {
                        String title = data.getString("title");
                        String lendingDate = data.getString("lending_date");
                        String sqlZeroCheck = "SELECT title, CASE WHEN count <> 0 THEN 'false' ELSE 'true' END AS count_zero FROM books WHERE title = (?)";//checking if the count of that book is zero;
                        client.preparedQuery(sqlZeroCheck).execute(Tuple.of(title), queryResultCount-> {
                            if (queryResultCount.succeeded()) {
                                String countValue = queryResultCount.result().iterator().next().getString("count_zero");//result of the count of the specific book
                                if ("false".equals(countValue)) {  //maybe i should do the string countZeroValue into an actuall boolean?
                                    String sqlCountUpdate = "UPDATE books SET count = count - 1 WHERE title = ?;";//updating book's count (minus 1)
                                    client.preparedQuery(sqlCountUpdate).execute(Tuple.of(title), queryResultCountUpdate -> {
                                        if (queryResultCountUpdate.succeeded()) {
                                            System.out.println("query count update(minus 1) success");
                                        } else {
                                            Throwable exception = queryResultCountUpdate.cause();
                                            exception.printStackTrace(); 
                                        }
                                    });
                                    String sqlisbn = "SELECT isbn FROM books WHERE title = ?";//exctracting query for isbn from table books using the title value
                                    client.preparedQuery(sqlisbn).execute(Tuple.of(title), queryResultIsbn ->{
                                        if (queryResultIsbn.succeeded()) {
                                            String isbn = "0";// just needs initializtaion, 0 is a grabage value
                                            RowSet<Row> resultSet = queryResultIsbn.result();
                                            Iterator<Row> iterator = resultSet.iterator();
                                            if (iterator.hasNext()) {
                                                Row row = iterator.next();
                                                isbn = row.getString("isbn");
                                                System.out.println("ISBN: " + isbn);
                                                String sqlStatusInsertion = "INSERT INTO status (id, isbn, title, lending_date) VALUES (? ,? ,? ,?)";//updating the status table
                                                client.preparedQuery(sqlStatusInsertion).execute(Tuple.of(id , isbn,  title, lendingDate), queryResultStatusInsertion -> {
                                                    if (queryResultStatusInsertion.succeeded()) {
                                                        System.out.println("query statusInsertion success");
                                                        vertx.eventBus().send("controller.lendRequest", title);//event bus returns data back to controller
                                                    } else {
                                                        Throwable exception = queryResultStatusInsertion.cause();
                                                        exception.printStackTrace(); 
                                                    }
                                                });
                                            }  
                                            else {
                                                System.out.println("ISBN doesnt exist");
                                            }
                                        } else{
                                            Throwable exception = queryResultIsbn.cause();
                                            exception.printStackTrace(); 
                                        }
                                    });
                                }  else {
                                    System.out.println("Count is zero, request must be denied.");
                                }
                            } else {
                                Throwable exception = queryResultCount.cause();
                                exception.printStackTrace(); 
                            }
                        });
                    }   else {
                        System.out.println("ID does not exist");
                    }
                } else {
                    Throwable exception = queryResultIdCheck.cause();
                    exception.printStackTrace(); 
                }
            }); 
        });                   
        vertx.eventBus().consumer("model.returnBook", message -> {
            JsonObject data = (JsonObject) message.body();
            Integer id = data.getInteger("id");
            String title = data.getString("title");
            String sqlIdCheck = "SELECT EXISTS(SELECT 1 FROM people WHERE id = ?) AS id_exists";
            client.preparedQuery(sqlIdCheck).execute(Tuple.of(id), queryResultIdCheck -> {
                if (queryResultIdCheck.succeeded()) {
                    Boolean idExists = queryResultIdCheck.result().iterator().next().getBoolean("id_exists");
                    if (idExists) {
                        String sqlTitleCheck = "SELECT EXISTS(SELECT 1 FROM books WHERE title = ?) AS title_exists";
                        client.preparedQuery(sqlTitleCheck).execute(Tuple.of(title), queryResultTitleCheck -> {
                            if (queryResultTitleCheck.succeeded()) {
                                Boolean titleExists = queryResultTitleCheck.result().iterator().next().getBoolean("title_exists");
                                if (titleExists) {
                                    String sqlReturn = "UPDATE books SET count = count + 1 WHERE title = ?";
                                    client.preparedQuery(sqlReturn).execute(Tuple.of(title), queryResultReturn -> {
                                        if (queryResultTitleCheck.succeeded()){
                                            System.out.println("succesfull return of book " + title);
                                        }
                                        else{
                                            Throwable exception = queryResultReturn.cause();
                                            exception.printStackTrace(); 
                                        }
                                    });
                                } else {
                                    System.out.println("title does not exist");
                                }
                            } else {
                                Throwable exception = queryResultTitleCheck.cause();
                                exception.printStackTrace(); 
                            }
                        });                 
                    } else {
                        System.out.println("ID does not exist");
                    }
                } else {
                    Throwable exception = queryResultIdCheck.cause();
                    exception.printStackTrace(); 
                }
            });
        });            
    }
}
        