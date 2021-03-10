package Data;
import org.jbibtex.*;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import static Data.digitalLibrary.*;
import static Data.reference.*;

public class main {

    static String framework = "embedded";
    static String dbName = "derbyDB";
    static String protocol = "jdbc:derby:";
    static Properties props = iniProperties(); // connection properties

    private static Properties iniProperties() {
        Properties props = new Properties();
        props.put("user", "user1");
        props.put("password", "user1");
        return props;
    }

    public static void main(String[] args) throws IOException, ParseException {
        pruebaDigitalLibrary();
        pruebaReference();
    }

    private static void pruebaDigitalLibrary() {
        System.out.println("Program starting in " + framework + " mode");
        Connection conn;
        ArrayList<Statement> statements = new ArrayList<>(); // list of Statements, PreparedStatements
        Statement s;
        try{
            conn = DriverManager.getConnection(protocol + dbName + ";create=true", props);
            System.out.println("Connected to and created database " + dbName);
            conn.setAutoCommit(false);

            // Statement object for running various SQL statements commands against the database.
            s = conn.createStatement();
            statements.add(s);
            // Create table digitalLibraries if not exist
            if (createTable(s))
                //insert rows in table
                insertRows(conn, statements);

            conn.commit();
            System.out.println("Committed the transaction");

        } catch (SQLException e){
            System.out.println("Error");
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

    private static void pruebaReference() throws IOException, ParseException {
        String[] aux = pedirInfo();
        String path = aux[0];
        String nameDL = aux[1];
        System.out.println("SimpleApp starting in " + framework + " mode");

        Connection conn;
        ArrayList<Statement> statements = new ArrayList<>(); // list of Statements, PreparedStatements
        Statement s;
        ResultSet rs;
        String classpathStr = System.getProperty("java.class.path");
        System.out.println(classpathStr);

        try{
            conn = DriverManager.getConnection(protocol + dbName + ";create=true", props);
            System.out.println("Connected to and created database " + dbName);
            conn.setAutoCommit(false);

            s = conn.createStatement();
            statements.add(s);

            // Create a table if not exists...
            createTable(s);

            importar(path, nameDL, s);
            // Select data
            rs = getAllData(s);

            while (rs.next()){
                System.out.println(rs.getString(1));
                System.out.println(rs.getString(2));
                System.out.println(rs.getString(3));
            }

            // delete the table
            //dropTable(s);

            conn.commit();
            System.out.println("Committed the transaction");

        } catch (SQLException e){
            System.out.println("Error");
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
}
