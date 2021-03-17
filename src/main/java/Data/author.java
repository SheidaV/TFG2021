package Data;

import java.sql.SQLException;
import java.sql.Statement;

public class author {
    /* create table authors( name varchar(50),
    PRIMARY KEY (name));*/
    public static void createTable(Statement s) {
        try {
            s.execute("CREATE TABLE authors( name varchar(50) , idRef INT , PRIMARY KEY (name, idRef)," +
                    "CONSTRAINT REF_FK FOREIGN KEY (idRef) REFERENCES referencias (idRef)," +
                    "CONSTRAINT RES_FK FOREIGN KEY (name) REFERENCES researchers (name))");
            System.out.println("Created table authors");
        } catch (SQLException t  ){
            if (t.getSQLState().equals("X0Y32"))
                System.out.println("Table authors exists");
            else System.out.println("Error en la creación de table authors");
        }
    }
    public static void insertRows(String[] names, int idRef, Statement s) throws SQLException {
        String query;
        String queryRow;

        queryRow = "INSERT INTO authors(name,idRef) VALUES (";

        for(String x : names) {
            query = queryRow +"'" + x + "', " + idRef + ")";
            System.out.println(query);

            s.execute(query);
            System.out.println("Inserted row with name in Authors");
        }

    }

    public static void dropTable(Statement s) throws SQLException {
        s.execute("drop table authors");
        System.out.println("Dropped table authors");
    }

}
