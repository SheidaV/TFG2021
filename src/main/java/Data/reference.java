package Data;

import org.jbibtex.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.*;
import java.util.*;

import static Data.digitalLibrary.DLs;

public class reference {

    static String path;
    static String nameDL;

    static String framework = "embedded";
    static String dbName = "derbyDB";
    static String protocol = "jdbc:derby:";
    static Properties props = iniProperties(); // connection properties


    static Key abstractKey = new Key("abstract");
    static Key keywordsKey = new Key("keywords");
    static Key numpagesKey = new Key("numpages");
    // Key citeKey = new Key("\\cite");

    private static Properties iniProperties() {
        Properties props = new Properties();
        props.put("user", "user1");
        props.put("password", "user1");
        return props;
    }

    public static void main(String[] args) throws IOException, ParseException {
        pedirInfo();
        importar(path,nameDL);
    }

    public static void pedirInfo() {
        System.out.println("Escribir el path absoluto donde se encuentra el fichero a exportar: ");
        Scanner entrada=new Scanner(System.in);
        path = entrada.nextLine();
        System.out.println("Path escogido: " + path);
        System.out.println("Escoger el número de la biblioteca de donde se exporta el archivo:");
        for (int i = 1; i <= DLs.size(); i++)
            System.out.println(i + ". " + DLs.get(i));

        try {
            int num=entrada.nextInt();
            if (num > 0 & num <= DLs.size()) {
                nameDL = DLs.get(num);
                System.out.println("Se ha escogido " + nameDL);
            } else {
                System.out.println("El numero no esta entre el 1 y el 6");
            }
        } catch (Exception e) {
            System.out.println("No se ha escrito un número.");
        }
    }

    private static void importar(String path, String nameDL) throws IOException, ParseException {
        System.out.println("SimpleApp starting in " + framework + " mode");

        Connection conn;
        ArrayList<Statement> statements = new ArrayList<>(); // list of Statements, PreparedStatements
        Statement s;
        ResultSet rs;

        Reader reader = new FileReader(path);
        BibTeXParser bibtexParser = new BibTeXParser(); //addd Exception
        BibTeXDatabase database = bibtexParser.parse(reader);
        Map<Key, BibTeXEntry> entryMap = database.getEntries();
        Collection<BibTeXEntry> entries = entryMap.values();

        try{
            String classpathStr = System.getProperty("java.class.path");
            System.out.println(classpathStr);

            conn = DriverManager.getConnection(protocol + dbName + ";create=true", props);
            System.out.println("Connected to and created database " + dbName);
            conn.setAutoCommit(false);

            s = conn.createStatement();
            statements.add(s);

            // Create a table if not exists...
            createTable(s);

            // add rows of file
            for(BibTeXEntry entry : entries){
                insertRow(nameDL, s, entry);
            }
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
        reader.close();
    }

    private static ResultSet getAllData(Statement s) throws SQLException {
        ResultSet rs;
        rs = s.executeQuery("SELECT * FROM referencias");
        return rs;
    }

    private static void insertRow(String nameDL, Statement s, BibTeXEntry entry) throws SQLException {
        String query;
        StringBuilder atributsOfRow;
        StringBuilder valuesOfRow;
        Key type = entry.getType();
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
        //Value cite = entry.getField(citeKey);

        atributsOfRow = new StringBuilder("INSERT INTO referencias(type");
        valuesOfRow = new StringBuilder(") VALUES (").append("'").append(type).append("'");
        if (author != null) {
            String aux = author.toUserString().replaceAll("[<EOF>]", "");
            //El simbolo ' dentro del abstract provoca errores
            atributsOfRow.append(", author");
            valuesOfRow.append(", '").append(aux.replaceAll("[{-}]", "")).append("'");
        }
        if (doi != null) {
            atributsOfRow.append(", doi");
            valuesOfRow.append(", '").append(doi.toUserString().replaceAll("[{-}]", "")).append("'");
        }
        if (booktitle != null) {
            atributsOfRow.append(", booktitle");
            valuesOfRow.append(", '").append(booktitle.toUserString().replaceAll("[{-}]", "")).append("'");
        }
        if (title != null) {
            atributsOfRow.append(", title");
            valuesOfRow.append(", '").append(title.toUserString().replaceAll("[{-}]", "")).append("'");
        }
        if (journal != null) {
            atributsOfRow.append(", journal");
            valuesOfRow.append(", '").append(journal.toUserString().replaceAll("[{-}]", "")).append("'");
        }
        if (keywords != null) {
            atributsOfRow.append(", keywords");
            valuesOfRow.append(", '").append(keywords.toUserString().replaceAll("[{-}]", "")).append("'");
        }
        if (number != null) {
            atributsOfRow.append(", number");
            valuesOfRow.append(", ").append(number.toUserString().replaceAll("[{-}]", ""));
        }
        if (numpages != null) {
            atributsOfRow.append(", numpages");
            valuesOfRow.append(", ").append(numpages.toUserString().replaceAll("[{-}]", ""));
        }
        if (pages != null) {
            atributsOfRow.append(", pages");
            valuesOfRow.append(", ").append(pages.toUserString().replaceAll("[{-}]", ""));
        }
        if (volume != null) {
            atributsOfRow.append(", volume");
            valuesOfRow.append(", ").append(volume.toUserString().replaceAll("[{-}]", ""));
        }
        if (year != null) {
            atributsOfRow.append(", año");
            valuesOfRow.append(", ").append(year.toUserString().replaceAll("[{-}]", ""));
        }
        if (abstractE != null) {
            String aux = abstractE.toUserString().replaceAll("[']", "");
            //El simbolo ' dentro del abstract provoca errores
            atributsOfRow.append(", abstract");
            valuesOfRow.append(", '").append(aux.replaceAll("[{-}]", "")).append("'");
        }
        //if (cite != null) System.out.println("Cite es: " + cite);
        atributsOfRow.append(", dl");
        valuesOfRow.append(", '").append(nameDL).append("') ");

        query = atributsOfRow.toString() + valuesOfRow;
        System.out.println(query);

        sqlCommand(s, query, "Inserted row with author, doi, ....");
    }

    private static void sqlCommand(Statement s, String s2, String s3) throws SQLException {
        s.execute(s2);
        System.out.println(s3);
    }

    /*
    referencias(idRef,  abstract, author, doi, year, citeKey, booktitle,
                title,  journal, keywords, number, numpages, pages, volume,dl)
    {dl} references digitalLibraries
    primary key(idRef)
    */
    private static void createTable(Statement s) {
        try {
            sqlCommand(s, "create table referencias(idRef INT NOT NULL GENERATED ALWAYS AS IDENTITY, type varchar(50), " +
                    "author varchar(200), doi varchar(50), citeKey varchar(50), booktitle varchar(100), title varchar(200), " +
                    "journal varchar(100), keywords varchar(500), number INT, numpages INT, pages varchar(10), volume INT, " +
                    "año INT, abstract varchar(2000), dl char(50), PRIMARY KEY (idRef), " +
                    "CONSTRAINT DL_FK FOREIGN KEY (dl) REFERENCES digitalLibraries (dl))", "Created table referencias");
        } catch (SQLException t  ){
            if (t.getSQLState().equals("X0Y32"))
                System.out.println("Table referencias exists");
            else System.out.println("Error en la creación de table referencias");
        }
    }
    private static void dropTable(Statement s) throws SQLException {
        sqlCommand(s, "drop table referencias", "Dropped table referencias");
    }


}
