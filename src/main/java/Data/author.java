package Data;

import java.sql.SQLException;
import java.sql.Statement;

public class author {
    /* create table authors( name varchar(50),
    PRIMARY KEY (name));*/
    public static void createTable(Statement s) {
        try {
            s.execute("CREATE TABLE authors( idRes int , idRef int , PRIMARY KEY (idRes, idRef)," +
                    "CONSTRAINT RES_FK_AU FOREIGN KEY (idRes) REFERENCES researchers (idRes)," +
                    "CONSTRAINT REF_FK_AU FOREIGN KEY (idRef) REFERENCES referencias (idRef))");
            System.out.println("Created table authors");
        } catch (SQLException t  ){
            if (t.getSQLState().equals("X0Y32"))
                System.out.println("Table authors exists");
            else System.out.println("Error en la creación de table authors");
        }
    }
    public static void insertRows(Integer[] ids, int idRef, Statement s) throws SQLException {
        String queryRow = "INSERT INTO authors(idRes,idRef) VALUES (";
        String query;
        for(int x : ids) {
            query = queryRow + x + ", " + idRef + ")";
            s.execute(query);
            System.out.println("Inserted row with idRes and idRef in Authors");
        }
    }

    public static void dropTable(Statement s) throws SQLException {
        s.execute("drop table authors");
        System.out.println("Dropped table authors");
    }

}
