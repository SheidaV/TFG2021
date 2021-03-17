package Data;

import org.jbibtex.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.*;
import java.util.*;

import static Data.digitalLibrary.getIDs;
import static Data.digitalLibrary.getNames;

public class reference {

    static String path;
    static String nameDL;

    static Key abstractKey = new Key("abstract");
    static Key keywordsKey = new Key("keywords");
    static Key numpagesKey = new Key("numpages");
    static Key citeKey = new Key("\\cite{key}");

    public static String[] pedirInfo(Statement s) throws SQLException {
        System.out.println("Escribir el path absoluto donde se encuentra el fichero a exportar: ");
        Scanner entrada=new Scanner(System.in);
        path = entrada.nextLine();
        System.out.println("Path escogido: " + path);
        System.out.println("Escoger el número de la biblioteca de donde se exporta el archivo:");

        ArrayList<String> DLs = getNames(s);
        for (int i = 0; i < DLs.size(); i++)
            System.out.println(i+1 + ". " + DLs.get(i));
        try {
            int num=entrada.nextInt();
            if (num > 0 & num <= DLs.size()) {
                nameDL = getIDs(s).get(num-1);
                System.out.println("Se ha escogido " + nameDL);
            } else {
                System.out.println("El numero no esta entre el 1 y el 6");
            }
        } catch (Exception e) {
            System.out.println("No se ha escrito un número.");
        }
        return new String[]{path, nameDL};
    }

    public static void importar(String path, String nameDL, Statement s, Connection conn) throws IOException, ParseException, SQLException {
        Reader reader = new FileReader(path);
        BibTeXParser bibtexParser = new BibTeXParser(); //addd Exception
        BibTeXDatabase database = bibtexParser.parse(reader);
        Map<Key, BibTeXEntry> entryMap = database.getEntries();
        Collection<BibTeXEntry> entries = entryMap.values();
        // add rows of file
        for(BibTeXEntry entry : entries){
            String authorsRet = insertRow(nameDL, s, entry);
            String[] splitNames = researcher.insertRows(authorsRet,s);
            conn.commit();
            Statement s2 = conn.createStatement();
            int q = getLastID(s2).getInt(1);
            System.out.println(q);
            author.insertRows(splitNames, q, s);
        }
        reader.close();
    }

    public static ResultSet getAllData(Statement s) throws SQLException {
        ResultSet rs;
        rs = s.executeQuery("SELECT * FROM referencias");
        return rs;
    }

    public static ResultSet getLastID(Statement s) throws SQLException {
        ResultSet rs;
        rs = s.executeQuery("SELECT idRef FROM referencias ORDER BY idref DESC");
        return rs;
    }

    static String insertRow(String nameDL, Statement s, BibTeXEntry entry) throws SQLException {
        String ret = "";
        String query;
        StringBuilder atributsOfRow;
        StringBuilder valuesOfRow;
        Key type = entry.getType();
        Value title = entry.getField(BibTeXEntry.KEY_TITLE);
        Value authors = entry.getField(BibTeXEntry.KEY_AUTHOR);
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
        Value cite = entry.getField(citeKey);

        atributsOfRow = new StringBuilder("INSERT INTO referencias(type");
        valuesOfRow = new StringBuilder(") VALUES (").append("'").append(type).append("'");
        if (authors != null) {
            String aux = authors.toUserString().replaceAll("[\n]", " ");
            aux = aux.replaceAll("[{-}]", "");
            atributsOfRow.append(", author");
            valuesOfRow.append(", '").append(aux).append("'");
            ret = aux;
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
            valuesOfRow.append(", '").append(pages.toUserString().replaceAll("[{-}]", "")).append("'");;
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
        if (cite != null) System.out.println("Cite es: " + cite);
        atributsOfRow.append(", dl");
        valuesOfRow.append(", '").append(nameDL).append("') ");

        query = atributsOfRow.toString() + valuesOfRow;
        System.out.println(query);

        s.execute(query);
        System.out.println("Inserted row with author, doi, ....");
        System.out.println(ret);
        return ret;
    }

    /*
    referencias(idRef,  abstract, author, doi, year, citeKey, booktitle,
                title,  journal, keywords, number, numpages, pages, volume,dl)
    {dl} references digitalLibraries
    primary key(idRef)
    */
    public static void createTable(Statement s) {
        try {

            s.execute("create table referencias(idRef INT NOT NULL GENERATED ALWAYS AS IDENTITY, type varchar(50), " +
                    "author varchar(200), doi varchar(50), citeKey varchar(50), booktitle varchar(100), title varchar(200), " +
                    "journal varchar(100), keywords varchar(500), number INT, numpages INT, pages varchar(10), volume INT, " +
                    "año INT, abstract varchar(2000), dl char(50), PRIMARY KEY (idRef), " +
                    "CONSTRAINT DL_FK FOREIGN KEY (dl) REFERENCES digitalLibraries (dl))");
            System.out.println("Created table referencias");
        } catch (SQLException t  ){
            if (t.getSQLState().equals("X0Y32"))
                System.out.println("Table referencias exists");
            else System.out.println("Error en la creación de table referencias");
        }
    }
    public static void dropTable(Statement s) throws SQLException {
        s.execute("drop table referencias");
        System.out.println("Dropped table referencias");
    }
}
