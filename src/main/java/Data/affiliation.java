package Data;

import java.sql.SQLException;
import java.sql.Statement;

public class affiliation {
    /* create table affiliations(name varchar(50),
    PRIMARY KEY (name));*/
    public static void createTable(Statement s) {
        try {
            s.execute("create table affiliations(name varchar(50), PRIMARY KEY (name))");
            System.out.println("Created table affiliations");
        } catch (SQLException t  ){
            if (t.getSQLState().equals("X0Y32"))
                System.out.println("Table affiliations exists");
            else System.out.println("Error en la creación de table affiliations");
        }
    }
    public static void dropTable(Statement s) throws SQLException {
        s.execute("drop table affiliations");
        System.out.println("Dropped table affiliations");
    }
}
