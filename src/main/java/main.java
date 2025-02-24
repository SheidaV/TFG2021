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
        props.put("derby.language.sequence.preallocator", "1");
        return props;
    }

    public static void main(String[] args) throws IOException, ParseException, SQLException {
        ini();
        pruebaReference();
    }

    private static void ini() {
        System.out.println("Program starting in " + framework + " mode");
        Connection conn;
        ArrayList<Statement> statements = new ArrayList<>(); // list of Statements, PreparedStatements
        Statement s;
        try{
            String url = "jdbc:derby:derbyDB;create=true";
            conn = DriverManager.getConnection(url, props );
            System.out.println("Connected to and created database " + dbName);
            conn.setAutoCommit(false);

            // Statement object for running various SQL statements commands against the database.
            s = conn.createStatement();
            statements.add(s);

            crearTablas(s,conn,statements);
            //deleteTables(s,conn,statements);

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

    private static void pruebaReference() throws IOException, ParseException, SQLException {

            Connection conn = DriverManager.getConnection(protocol + dbName + ";create=true", props);
            Statement s = conn.createStatement();
            conn.setAutoCommit(false);
            ResultSet rs;
            String[] aux = article.pedirInfo(s);
            String path = aux[0];
            String nameDL = aux[1];
            System.out.println("SimpleApp starting in " + framework + " mode");

            article.importar(path, nameDL, s);
            // Select data
            rs = article.getAllData(s);

            while (rs.next()){
                for(int i = 1; i<=13; i++) {
                    System.out.println(rs.getString(i));
                }
            }
            conn.commit();
            System.out.println("Committed the transaction");


    }

    private static void crearTablas(Statement s, Connection conn, ArrayList<Statement> statements) throws SQLException {
        // Create table digitalLibraries if not exist
        if (digitalLibrary.createTable(s))
            //insert rows in table
            digitalLibrary.insertRows(conn, statements);
        researcher.createTable(s);
        venue.createTable(s);
        company.createTable(s);
        article.createTable(s);
        reference.createTable(s);
        affiliation.createTable(s);
        author.createTable(s);
    }

    private static void deleteTables(Statement s, Connection conn, ArrayList<Statement> statements) throws SQLException {
        affiliation.dropTable(s);
        company.dropTable(s);
        author.dropTable(s);
        reference.dropTable(s);
        researcher.dropTable(s);
        article.dropTable(s);
        venue.dropTable(s);
        digitalLibrary.dropTable(s);
    }
}
