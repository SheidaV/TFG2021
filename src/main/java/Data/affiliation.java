package Data;

import java.sql.SQLException;
import java.sql.Statement;

public class affiliation {
    /* create table affiliations(name varchar(50),
    PRIMARY KEY (name));*/
    public static void createTable(Statement s) {
        try {
            s.execute("create table affiliations(idCom int, idRef int, PRIMARY KEY (idCom,idRef), " +
                    "CONSTRAINT COM_FK_AF FOREIGN KEY (idCom) REFERENCES companies( idCom )," +
                    "CONSTRAINT REF_FK_AF FOREIGN KEY (idRef) REFERENCES referencias( idRef ))");
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
    public static void insertRow(Statement s, int idCom, int idRef) throws SQLException {
        String queryRow = "INSERT INTO affiliations(idCom,idRef) VALUES (";
        String query;
        query = queryRow + idCom + ", " + idRef + ")";
        System.out.println(query);
        s.execute(query);
        System.out.println("Inserted row with idCom and idRef in affiliations");
    }
}
