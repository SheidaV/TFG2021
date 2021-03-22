package Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class researcher {
    public static void createTable(Statement s) {
        try {
            s.execute("create table researchers( idRes INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
                    "name varchar(50), " +
                    "PRIMARY KEY (idRes)) ");
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

    public static ArrayList<String> getNames(Statement s) throws SQLException {
        ArrayList<String> ret = new ArrayList<>();
        ResultSet rs = s.executeQuery("SELECT name FROM researchers");
        while(rs.next()) {
            ret.add(rs.getString("name"));
        }
        return ret;
    }

    public static int insertRow(Statement s, String name) throws SQLException {
        try{
            String query = "INSERT INTO researchers(name) VALUES (\'" + name + "\')";
            System.out.println(query);
            s.execute(query);
            System.out.println("Inserted row with idRes name in researchers");
            s.getConnection().commit();
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505"))
                System.out.println("Researcher exists");
            else {
                while (e != null) {
                    System.err.println("\n----- SQLException -----");
                    System.err.println("  SQL State:  " + e.getSQLState());
                    System.err.println("  Error Code: " + e.getErrorCode());
                    System.err.println("  Message:    " + e.getMessage());
                    // for stack traces, refer to derby.log or uncomment this:
                    //e.printStackTrace(System.err);
                    e = e.getNextException();
                }
            }
        }
        ResultSet rs = s.executeQuery("SELECT idRes FROM researchers where name = '" + name + "'");
        rs.next();
        return rs.getInt(1);
    }

    public static Integer[] insertRows(String names, Statement s) throws SQLException {
        String[] splitArray = names.split("and ");
        Integer[] ret = new Integer[splitArray.length];
        int i = 0;
        for(String x : splitArray) {
            ret[i++] = insertRow(s,x);
        }
        return ret;
    }

}
