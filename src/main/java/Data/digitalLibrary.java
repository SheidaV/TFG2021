package Data;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class digitalLibrary {

    public static Map<Integer, String> DLs = null;
    static String dbName = "derbyDB";
    static String protocol = "jdbc:derby:";
    static Properties props = iniProperties(); // connection properties

    private static Properties iniProperties() {
        Properties props = new Properties();
        props.put("user", "user1");
        props.put("password", "user1");
        return props;
    }

    static { DLs = iniDLs(); }

    static Map<Integer, String> iniDLs() {
        //agregamos los nombres de las librerias
        Map<Integer, String> ret = new HashMap<>();
        Connection conn;
        Statement s;
        try {
            conn = DriverManager.getConnection(protocol + dbName + ";create=true", props);
            ArrayList<Statement> statements = new ArrayList<>();
            System.out.println("Connected to and created database " + dbName);
            conn.setAutoCommit(false);

            // Statement object for running various SQL statements commands against the database.
            s = conn.createStatement();
            statements.add(s);
            ResultSet rs;

            rs = getNames(s);
            int i = 1;
            while (rs.next()){
                ret.put(i++, rs.getString(1));
            }
        } catch (SQLException throwables) {
            System.out.println("Error en inicializar las DLs");
            throwables.printStackTrace();
        }
        return ret;
    }

    public static ResultSet getAllData(Statement s) throws SQLException {
        ResultSet rs;
        rs = s.executeQuery("SELECT * FROM digitalLibraries");
        return rs;
    }

    public static ResultSet getNames(Statement s) throws SQLException {
        ResultSet rs;
        rs = s.executeQuery("SELECT name FROM digitalLibraries");
        return rs;
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

        psInsert.setString(1, "IEEExplore");
        psInsert.setString(2, "IEE Explore");
        psInsert.setString(3, "https://ieeexplore.ieee.org/Xplore/home.jsp");
        psInsert.executeUpdate();
        System.out.println("Inserted ('IEEE', 'IEEE Xplore', 'https://ieeexplore.ieee.org/Xplore/home.jsp') ");

        psInsert.setString(1, "ACM");
        psInsert.setString(2, "ACM DL");
        psInsert.setString(3, "https://dl.acm.org/");
        psInsert.executeUpdate();
        System.out.println("Inserted ('ACM', 'ACM DL', 'https://dl.acm.org/') ");

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
