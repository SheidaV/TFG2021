import java.io.*;
import java.sql.*;
import java.util.*;

import org.jbibtex.*;

/*
references(idRef,  abstract, author, doi, year, citeKey, booktitle,
            title,  journal, keywords, number, numpages, pages, volume,dl)
{dl} references digitalLibraries
primary key(idRef)
*/

public class BibProgram {

    public static void main(String[] args) throws IOException, ParseException, SQLException {
        String framework = "embedded";
        System.out.println("SimpleApp starting in " + framework + " mode");

        Connection conn;
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
            Key abstractKey = new Key("abstract");
            Key keywordsKey = new Key("keywords");
            Key numpagesKey = new Key("numpages");
            try{
                Properties props = new Properties(); // connection properties
                props.put("user", "user1");
                props.put("password", "user1");
                //props.put("derby.language.sequence.preallocator", "1");
                //props.put("shutdown", true);
                /*Add ;shutdown=true to the JDBC URL. This will shut the database down when the application ends.
                Set the derby.language.sequence.preallocator property to 1 (its default value is 100). This will ensure that the column value is never cached.
                 */
                String dbName = "derbyDB";

                // View classpath
                String classpathStr = System.getProperty("java.class.path");
                System.out.println(classpathStr);

                String protocol = "jdbc:derby:";
                conn = DriverManager.getConnection(protocol + dbName + ";create=true", props);
                System.out.println("Connected to and created database " + dbName);
                conn.setAutoCommit(false);

                s = conn.createStatement();
                statements.add(s);

                // We create a table...
                //s.execute("create table referencesDL(idRef int(20)  NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) NOT NULL, " +

                s.execute("create table referencesDL(idRef INT NOT NULL GENERATED ALWAYS AS IDENTITY, type varchar(50), author varchar(200), " +
                        "doi varchar(50), citeKey varchar(50), booktitle varchar(100), title varchar(200), journal varchar(100), " +
                        "keywords varchar(500), number INT, numpages INT, pages INT, volume INT, año INT, " +
                        "abstract varchar(2000), PRIMARY KEY (idRef))");
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
                //psInsert = conn.prepareStatement
                //statements.add(psInsert);
                StringBuilder atributs = new StringBuilder();
                StringBuilder values = new StringBuilder();

                String query = "";
//,doi,citeKey,title,journal,number,numpages,pages,volume,año,abstract) VALUES ('author X','10.4018/IJDET.20210401.oa2','citeKey','title','journal',11,22,33,44,1999,'abstract')";
                for(BibTeXEntry entry : entries){
                    atributs = new StringBuilder("INSERT INTO referencesDL(");
                    values = new StringBuilder(") VALUES (");
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

                    Key type = entry.getType();
                    atributs.append("type");
                    values.append("'").append(type).append("'");
                    if (author != null) {
                        atributs.append(", author");
                        values.append(", '").append(author.toUserString().replaceAll("[{-}]", "")).append("'");
                    }
                    if (doi != null) {
                        atributs.append(", doi");
                        values.append(", '").append(doi.toUserString().replaceAll("[{-}]", "")).append("'");
                    }
                    if (booktitle != null) {
                        atributs.append(", booktitle");
                        values.append(", '").append(booktitle.toUserString().replaceAll("[{-}]", "")).append("'");
                    }
                    if (title != null) {
                        atributs.append(", title");
                        values.append(", '").append(title.toUserString().replaceAll("[{-}]", "")).append("'");
                    }
                    if (journal != null) {
                        atributs.append(", journal");
                        values.append(", '").append(journal.toUserString().replaceAll("[{-}]", "")).append("'");
                    }
                    if (keywords != null) {
                        atributs.append(", keywords");
                        values.append(", '").append(keywords.toUserString().replaceAll("[{-}]", "")).append("'");
                    }
                    if (number != null) {
                        atributs.append(", number");
                        values.append(", ").append(number.toUserString().replaceAll("[{-}]", ""));
                    }
                    if (numpages != null) {
                        atributs.append(", numpages");
                        values.append(", ").append(numpages.toUserString().replaceAll("[{-}]", ""));
                    }
                    if (pages != null) {
                        atributs.append(", pages");
                        values.append(", ").append(pages.toUserString().replaceAll("[{-}]", ""));
                    }
                    if (volume != null) {
                        atributs.append(", volume");
                        values.append(", ").append(volume.toUserString().replaceAll("[{-}]", ""));
                    }
                    if (year != null) {
                        atributs.append(", año");
                        values.append(", ").append(year.toUserString().replaceAll("[{-}]", ""));
                    }
                    if (abstractE != null) {
                        atributs.append(", abstract");
                        values.append(", '").append(abstractE.toUserString().replaceAll("[{-}]", "")).append("'");
                    }
                    values.append(")");
                    query = atributs.toString() + values;
                    System.out.println(query);

                    s.execute(query);
                    System.out.println("Inserted author and doi ....");

                }
                // Select data
                rs = s.executeQuery("SELECT * FROM referencesDL");

                while (rs.next()){
                    System.out.println(rs.getString(1));
                    System.out.println(rs.getString(2));
                    System.out.println(rs.getString(3));
                }

                // delete the table BORRAR !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                s.execute("drop table referencesDL");
                System.out.println("Dropped table referencesDL");

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

        reader.close();

    }
}