package Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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

    public static ArrayList<String> getNames(Statement s) throws SQLException {
        ArrayList<String> ret = new ArrayList<>();
        ResultSet rs = s.executeQuery("SELECT name FROM researchers");
        while(rs.next()) {
            ret.add(rs.getString("name"));
        }
        return ret;
    }

    public static boolean exists(Statement s, String name) throws SQLException {
        ArrayList<String> ret = new ArrayList<>();
        ResultSet rs = s.executeQuery("SELECT name FROM researchers");
        while(rs.next()) {
            ret.add(rs.getString("name"));
        }
        return ret.contains(name);
    }

    public static boolean insertRow(Statement s, String name) throws SQLException {
        if (!exists(s,name)) {
            String query = "INSERT INTO researchers VALUES (\'" + name + "\')";
            System.out.println(query);
            s.execute(query);
            System.out.println("Inserted row with name in researchers");
            return true;
        }
        return false;
    }

    public static String[] insertRows(String names, Statement s) throws SQLException {
        String[] splitArray = names.split("and ");
        for(String x : splitArray) {
            insertRow(s,x);
        }
        return splitArray;
    }

}
