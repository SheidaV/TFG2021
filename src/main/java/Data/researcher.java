package Data;

import java.sql.SQLException;
import java.sql.Statement;

public class researcher {
    public static void createTable(Statement s) {
        try {
            s.execute("create table researchers( name varchar(50), " +
                    "PRIMARY KEY (name)) ");
            System.out.println("Created table researchers");
        } catch (SQLException t  ){
            if (t.getSQLState().equals("X0Y32"))
                System.out.println("Table researchers exists");
            else System.out.println("Error en la creación de table researchers");
        }
    }

    public static void dropTable(Statement s) throws SQLException {
        s.execute("drop table researchers");
        System.out.println("Dropped table researchers");
    }
}
