package Data;

import java.sql.SQLException;
import java.sql.Statement;

public class venue {
    //Venue name unique - comprobar if exists y afegirla si no esta

    private static void createTable(Statement s) {
        try {
            sqlCommand(s, "create table venues(id varchar(50) UNIQUE, name varchar(50), " +
                    "PRIMARY KEY (name) ", "Created table venues");
        } catch (SQLException t  ){
            if (t.getSQLState().equals("X0Y32"))
                System.out.println("Table venues exists");
            else System.out.println("Error en la creación de table venues");
        }
    }

    private static void sqlCommand(Statement s, String s2, String s3) throws SQLException {
        s.execute(s2);
        System.out.println(s3);
    }
/*    create table authors(

            name varchar(50),

    PRIMARY KEY (name)

);

    create table affiliations(

            name varchar(50),

    PRIMARY KEY (name)

);

    create table venues(

            id varchar(50),
    name varchar(50),

    PRIMARY KEY (name)

);*/
}
