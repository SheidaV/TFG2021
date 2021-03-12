import Data.*;
import org.jbibtex.ParseException;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

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

        ini();
        pruebaReference();
    }

    private static void ini() {
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

            crearTablas(s,conn,statements);
            //deleteTables(s,conn,statements);

            /*ResultSet rs = digitalLibrary.getAllData(s);
            System.out.println("Información de la tabla:");
            while (rs.next()) {
                for (int i = 1; i<= 3; i++)
                    System.out.println(rs.getString(i));
            }*/
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
        String[] aux = reference.pedirInfo();
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

            reference.importar(path, nameDL, s);
            // Select data
            rs = reference.getAllData(s);

            while (rs.next()){
                for(int i= 1; i<17; i++) {
                    System.out.println(rs.getString(i));
                }
            }
            // delete the table
            //reference.dropTable(s);

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

    private static void crearTablas(Statement s, Connection conn, ArrayList<Statement> statements) throws SQLException {
        // Create table digitalLibraries if not exist
        if (digitalLibrary.createTable(s))
            //insert rows in table
            digitalLibrary.insertRows(conn, statements);
        researcher.createTable(s);
        reference.createTable(s);
        author.createTable(s);
        venue.createTable(s);
        affiliation.createTable(s);
    }

    private static void deleteTables(Statement s, Connection conn, ArrayList<Statement> statements) throws SQLException {
        venue.dropTable(s);
        affiliation.dropTable(s);
        author.dropTable(s);
        researcher.dropTable(s);
        reference.dropTable(s);
        digitalLibrary.dropTable(s);
    }
}
