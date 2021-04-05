package Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class reference {
    public static int insertRow(Statement s, String doi, String idDL) throws SQLException {
        try {
            String query = "INSERT INTO referencias(doi,idDL) VALUES (\'" + doi + "\', " + idDL + ")";
            System.out.println(query);
            s.execute(query);
            System.out.println("Inserted row with doi, idDL in referencias");
            s.getConnection().commit();
        } catch (SQLException e) {
            while (e != null) {
                System.err.println("\n----- SQLException -----");
                System.err.println("  SQL State:  " + e.getSQLState());
                System.err.println("  Error Code: " + e.getErrorCode());
                System.err.println("  Message:    " + e.getMessage());
                e = e.getNextException();
            }
        }
        ResultSet rs = s.executeQuery("SELECT idRef FROM referencias where doi = '" + doi + "' and idDL =" + idDL);
        rs.next();
        return rs.getInt(1);
    }

    public static void createTable(Statement s) {
        try {
            s.execute("create table referencias(idRef INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, " +
                    "INCREMENT BY 1), doi varchar(50), idDL int, " +
                    "PRIMARY KEY (idRef), CONSTRAINT DL_FK_R FOREIGN KEY (idDL) REFERENCES digitalLibraries (idDL)," +
                    "CONSTRAINT AR_FK_R FOREIGN KEY (doi) REFERENCES articles (doi))");
            //UNIQUE EN DOI
            System.out.println("Created table referencias");
        } catch (SQLException t  ){
            if (t.getSQLState().equals("X0Y32"))
                System.out.println("Table referencias exists");
            else System.out.println("Error en la creación de table referencias");
        }
    }
    public static void dropTable(Statement s) throws SQLException {
        s.execute("drop table referencias");
        System.out.println("Dropped table referencias");
    }
    public static int getLastID(Statement s) throws SQLException {
        ResultSet rs;
        rs = s.executeQuery("SELECT idRef FROM referencias ORDER BY idref DESC");
        rs.next();
        return rs.getInt(1);
    }
}
