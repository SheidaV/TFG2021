package Data;

import java.sql.SQLException;
import java.sql.Statement;

public class venue {
    /*Venue name unique - comprobar if exists y afegirla si no esta
    create table venues(id varchar(50), name varchar(50),
    PRIMARY KEY (id));*/

    public static void createTable(Statement s) {
        try {
            s.execute("create table venues(id varchar(50), name varchar(50) UNIQUE, PRIMARY KEY (id) ) ");
            System.out.println("Created table venues");
        } catch (SQLException t  ){
            if (t.getSQLState().equals("X0Y32"))
                System.out.println("Table venues exists");
            else System.out.println("Error en la creación de table venues");
        }
    }
    public static void dropTable(Statement s) throws SQLException {
        s.execute("drop table venues");
        System.out.println("Dropped table venues");
    }
}
