import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Properties;

public class SimpleProgram {

    public static void main(String[] args){

        try{
            String framework = "embedded";
            String protocol = "jdbc:derby:";
            String dbName = "derbyDB";
            ArrayList<Statement> statements = new ArrayList<Statement>();
            PreparedStatement psInsert;
            PreparedStatement psUpdate;
            Statement s;
            ResultSet rs = null;

            // View classpath
            String classpathStr = System.getProperty("java.class.path");
            System.out.println(classpathStr);

            Properties props = new Properties(); // connection properties
            props.put("user", "user1");
            props.put("password", "user1");

            Connection conn = DriverManager.getConnection(protocol + dbName + ";create=true", props);
            System.out.println("Connected to and created database " + dbName);
            conn.setAutoCommit(false);

            s = conn.createStatement();
            statements.add(s);

            // We create a table...
            s.execute("create table digitalLibraries(dl varchar(50), name varchar(50), url varchar(150))");
            /*digitalLibraries(dl,name,url); primaryKey(dl) */
            System.out.println("Created table digitalLibraries");

            // and add a few rows...

            /* It is recommended to use PreparedStatements when you are
             * repeating execution of an SQL statement. PreparedStatements also
             * allows you to parameterize variables. By using PreparedStatements
             * you may increase performance (because the Derby engine does not
             * have to recompile the SQL statement each time it is executed) and
             * improve security (because of Java type checking).
             */
            // parameter 1 is num (int), parameter 2 is addr (varchar)
            psInsert = conn.prepareStatement(
                    "insert into digitalLibraries values (?, ?, ?)");
            /* IEEEXplore,        IEEE Xplore	    https://ieeexplore.ieee.org/Xplore/home.jsp
            ACM,            ACM DL 	        https://dl.acm.org/
            ScienceDirect,  ScienceDirect   https://www.sciencedirect.com/
            SpringerLink,   SpringerLink	https://link.springer.com/
            Scopus ,        Scopus          https://www.scopus.com/
            WebOfScience,   Web of Science  https://mjl.clarivate.com/home     */
            statements.add(psInsert);
            //ex int : psInsert.setInt(1, 1900);

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

            // Select data
            rs = s.executeQuery("SELECT * FROM digitalLibraries");
            String NoNumber; // street number retrieved from the database
            boolean failure = false;

/*    if (!rs.next()){
      failure = true;
      System.out.println("No rows in ResultSet");
    }

    if (!rs.next()) {
      failure = true;
      System.out.println("Too few rows");
    }

    if (rs.next()) {
      failure = true;
      System.out.println("Too many rows");
    }
    if (!failure) {
      System.out.println("Verified the rows");
    }
*/
            NoNumber = rs.getString(1);
            System.out.println( "The number is " + NoNumber);

            while (rs.next()){
                System.out.println(rs.getString(1));
            }

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
