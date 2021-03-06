import java.io.*;
import java.sql.*;
import java.util.*;

import org.jbibtex.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
/*
references(idRef,  abstract, author, doi, year, citeKey, booktitle,
            title,  journal, keywords, number, numpages, pages, volume,dl)
{dl} references digitalLibraries
primary key(idRef)
*/

public class BibProgram {
    private static String framework = "embedded";
    private static String protocol = "jdbc:derby:";

    public static void main(String[] args) throws IOException, ParseException {
        System.out.println("SimpleApp starting in " + framework + " mode");

        Connection conn = null;
        ArrayList<Statement> statements = new ArrayList<Statement>(); // list of Statements, PreparedStatements
        PreparedStatement psInsert;
        PreparedStatement psUpdate;
        Statement s;
        ResultSet rs = null;

        Reader reader = new FileReader("src/main/resources/savedrecs.bib");
        BibTeXParser bibtexParser = new BibTeXParser();
        BibTeXDatabase database = bibtexParser.parse(reader);
        Map<Key, BibTeXEntry> entryMap = database.getEntries();
        Collection<BibTeXEntry> entries = entryMap.values();

        if (entries!=null) {
            try{
                Properties props = new Properties(); // connection properties
                props.put("user", "user1");
                props.put("password", "user1");
                //props.put("derby.language.sequence.preallocator", "1");
                //props.put("shutdown", true);
                String dbName = "derbyDB";

                // View classpath
                String classpathStr = System.getProperty("java.class.path");
                System.out.println(classpathStr);

                conn = DriverManager.getConnection(protocol + dbName + ";create=true", props);
                System.out.println("Connected to and created database " + dbName);
                conn.setAutoCommit(false);

                s = conn.createStatement();
                statements.add(s);


                // We create a table...
                //s.execute("create table referencesDL(idRef int(20)  NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) NOT NULL, " +
                s.execute("create table referencesDL(idRef INT NOT NULL GENERATED ALWAYS AS IDENTITY, author varchar(50), " +
                        "doi varchar(50), citeKey varchar(50), booktitle varchar(50), title varchar(50), journal varchar(50), " +
                        "keywords varchar(100), number INTEGER, numpages INTEGER, pages INTEGER, volume INTEGER, año INTEGER, " +
                        "abstract varchar(1000), PRIMARY KEY (idRef))");
                //dl varchar(150), ....., FOREIGN KEY (dl) REFERENCES digitalLibraries(dl))
                /*
                references(idRef,  abstract, author, doi, year, citeKey, booktitle,
                            title,  journal, keywords, number, numpages, pages, volume,dl)
                {dl} references digitalLibraries
                primary key(idRef)
                */
                System.out.println("Created table referencesDL");

                // and add a few rows...

                // parameter 1 is num (int), parameter 2 is addr (varchar)
                psInsert = conn.prepareStatement(
                        "insert into referencesDL values (idRef, author, doi, citeKey, title , journal, number, numpages , pages , volume , año , abstract)");

                statements.add(psInsert);

                ("authorX","10.4018/IJDET.20210401.oa2",1999,"citeKey","title","journal",11,22,33,44,1999,"abstract");
                psInsert.executeUpdate();
                System.out.println("Inserted author and doi ");

                // Select data
                rs = s.executeQuery("SELECT * FROM referencesDL");

                while (rs.next()){
                    System.out.println(rs.getString(1));
                    System.out.println(rs.getString(2));
                }

                // delete the table BORRAR !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                s.execute("drop table referencesDL");
                System.out.println("Dropped table digitalLibraries");

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
        Key abstractKey = new Key("abstract");
        Key keywordsKey = new Key("keywords");
        Key numpagesKey = new Key("numpages");
        /*for(BibTeXEntry entry : entries){
            // attributes:
            // idRef,  , author, doi, year, citeKey, booktitle, title,
            // journal, keywords, number, numpages, pages, volume, abstract, dl
            Value title = entry.getField(BibTeXEntry.KEY_TITLE);
            Value author = entry.getField(BibTeXEntry.KEY_AUTHOR);
            Value doi = entry.getField(BibTeXEntry.KEY_DOI);
            Value year = entry.getField(BibTeXEntry.KEY_YEAR);
            Value booktitle = entry.getField(BibTeXEntry.KEY_BOOKTITLE);
            Value journal = entry.getField(BibTeXEntry.KEY_JOURNAL);
            Value number = entry.getField(BibTeXEntry.KEY_NUMBER);
            Value pages = entry.getField(BibTeXEntry.KEY_PAGES);
            Value volume = entry.getField(BibTeXEntry.KEY_VOLUME);
            Value abstractE = entry.getField(abstractKey);
            Value keywords = entry.getField(keywordsKey);
            Value numpages = entry.getField(numpagesKey);

            Key citeKey = entry.getType();
            System.out.println(citeKey);

            if (title != null) System.out.println(title.toUserString().replaceAll("[{-}]", ""));
            if (author != null) System.out.println( author.toUserString());
            if (doi != null) System.out.println(doi.toUserString().replaceAll("[{-}]", ""));
            if (abstractE != null) System.out.println(abstractE.toUserString().replaceAll("[{-}]", ""));
            if (booktitle != null) System.out.println(booktitle.toUserString().replaceAll("[{-}]", ""));
            if (journal != null) System.out.println(journal.toUserString().replaceAll("[{-}]", ""));
            if (year != null) System.out.println(year.toUserString().replaceAll("[{-}]", ""));
            if (number != null) System.out.println(number.toUserString().replaceAll("[{-}]", ""));
            if (pages != null) System.out.println(pages.toUserString().replaceAll("[{-}]", ""));
            if (volume != null) System.out.println(volume.toUserString().replaceAll("[{-}]", ""));
            if (keywords != null) System.out.println(keywords.toUserString().replaceAll("[{-}]", ""));
            if (numpages != null) System.out.println(numpages.toUserString().replaceAll("[{-}]", ""));

        }
        */
        reader.close();

    }
}