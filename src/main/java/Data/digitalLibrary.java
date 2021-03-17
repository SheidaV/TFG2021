package Data;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class digitalLibrary {

    public static ResultSet getAllData(Statement s) throws SQLException {
        ResultSet rs;
        rs = s.executeQuery("SELECT * FROM digitalLibraries");
        return rs;
    }

    public static ArrayList<String> getNames(Statement s) throws SQLException {
        ArrayList<String> ret = new ArrayList<>();
        ResultSet rs = s.executeQuery("SELECT name FROM digitalLibraries ORDER BY dl asc ");
        while(rs.next()) {
            ret.add(rs.getString("name"));
        }
        return ret;
    }

    public static ArrayList<String> getIDs(Statement s) throws SQLException {
        ArrayList<String> ret = new ArrayList<>();
        ResultSet rs = s.executeQuery("SELECT dl FROM digitalLibraries ORDER BY dl asc ");
        while(rs.next()) {
            ret.add(rs.getString("dl"));
        }
        return ret;
    }

    public static boolean createTable(Statement s) {
        try {
            s.execute("create table digitalLibraries(dl char(50), name varchar(50), url varchar(150) , PRIMARY KEY (dl))");
            System.out.println("Created table digitalLibraries");
            return true;

        } catch (SQLException t ) {
            if (t.getSQLState().equals("X0Y32")) System.out.println("Table digitalLibraries exists");
            else System.out.println("Error en create table digitalLibraries");
            return false;
        }
    }

    public static void insertRows(Connection conn, ArrayList<Statement> statements) throws SQLException {
        PreparedStatement psInsert;
        psInsert = conn.prepareStatement("insert into digitalLibraries values (?, ?, ?)");
        statements.add(psInsert);

        psInsert.setString(1, "ACM");
        psInsert.setString(2, "ACM DL");
        psInsert.setString(3, "https://dl.acm.org/");
        psInsert.executeUpdate();
        System.out.println("Inserted ('ACM', 'ACM DL', 'https://dl.acm.org/') ");

        psInsert.setString(1, "IEEExplore");
        psInsert.setString(2, "IEE Explore");
        psInsert.setString(3, "https://ieeexplore.ieee.org/Xplore/home.jsp");
        psInsert.executeUpdate();
        System.out.println("Inserted ('IEEE', 'IEEE Xplore', 'https://ieeexplore.ieee.org/Xplore/home.jsp') ");

        psInsert.setString(1, "ScienceDirect");
        psInsert.setString(2, "ScienceDirect");
        psInsert.setString(3, "https://www.sciencedirect.com/");
        psInsert.executeUpdate();
        System.out.println("Inserted ('ScienceDirect', 'ScienceDirect', 'https://www.sciencedirect.com/')' ");

        psInsert.setString(1, "SpringerLink");
        psInsert.setString(2, "SpringerLink");
        psInsert.setString(3, "https://link.springer.com/");
        psInsert.executeUpdate();
        System.out.println("Inserted ('SpringerLink', 'SpringerLink', 'https://link.springer.com/') ");

        psInsert.setString(1, "Scopus");
        psInsert.setString(2, "Scopus");
        psInsert.setString(3, "https://www.scopus.com/");
        psInsert.executeUpdate();
        System.out.println("Inserted ('Scopus', 'Scopus', 'https://www.scopus.com/') ");

        psInsert.setString(1, "WebOfScience");
        psInsert.setString(2, "Web of Science");
        psInsert.setString(3, "https://mjl.clarivate.com/home");
        psInsert.executeUpdate();
        System.out.println("Inserted ('WebOfScience', 'Web of Science', 'https://mjl.clarivate.com/home') ");
    }

    public static void dropTable(Statement s) throws SQLException {
        s.execute("drop table digitalLibraries");
        System.out.println("Dropped table digitalLibraries");
    }

}
