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
    static String idDL;
    private static String authorsToInsert;
    private static int affiliationToInsert;

    static Key abstractKey = new Key("abstract");
    static Key keywordsKey = new Key("keywords");
    static Key numpagesKey = new Key("numpages");
    static Key articleKey = new Key("article");
    static Key affiliationKey = new Key("affiliation");

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
            int num=entrada.nextInt() - 1;
            if (num > 0 & num < DLs.size()) {
                idDL = getIDs(s).get(num);
                System.out.println("Se ha escogido " + idDL + ". " + DLs.get(num));
            } else {
                System.out.println("El numero no esta entre el 1 y el 6");
            }
        } catch (Exception e) {
            System.out.println("No se ha escrito un número.");
        }
        return new String[]{path, idDL};
    }

    public static void importar(String path, String nameDL, Statement s) throws IOException, ParseException, SQLException {
        Reader reader = new FileReader(path);
        BibTeXParser bibtexParser = new BibTeXParser(); //addd Exception
        BibTeXDatabase database = bibtexParser.parse(reader);
        Map<Key, BibTeXEntry> entryMap = database.getEntries();
        Collection<BibTeXEntry> entries = entryMap.values();
        // add rows of file
        for(BibTeXEntry entry : entries){
            insertRow(nameDL, s, entry); //ret void
            Integer[] idsResearchers = researcher.insertRows(authorsToInsert,s); //han de ser idRes
            s.getConnection().commit();
            int id = getLastID(s);
            author.insertRows(idsResearchers, id, s);
            if (affiliationToInsert != -1) affiliation.insertRow(s, affiliationToInsert,id);
        }
        reader.close();
    }

    public static ResultSet getAllData(Statement s) throws SQLException {
        ResultSet rs;
        rs = s.executeQuery("SELECT * FROM referencias");
        return rs;
    }

    public static int getLastID(Statement s) throws SQLException {
        ResultSet rs;
        rs = s.executeQuery("SELECT idRef FROM referencias ORDER BY idref DESC");
        rs.next();
        return rs.getInt(1);
    }
//Devuelve un string de todos los autores de la referencia
    static void insertRow(String idDL, Statement s, BibTeXEntry entry) throws SQLException {
        try{
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
            Value article = entry.getField(articleKey);
            Value affil = entry.getField(affiliationKey);
            atributsOfRow = new StringBuilder("INSERT INTO referencias(type");
            valuesOfRow = new StringBuilder(") VALUES (").append("'").append(type).append("'");
            authorsToInsert = null;
            if (authors != null) {
                authorsToInsert = authors.toUserString().replaceAll("[\n]", " ").replaceAll("[{-}]", "");
            }
            if (doi != null) {
                atributsOfRow.append(", doi");
                valuesOfRow.append(", '").append(doi.toUserString().replaceAll("[{-}]", "")).append("'");
            }
            atributsOfRow.append(", citeKey");
            valuesOfRow.append(", '").append(entry.getKey()).append("'");
            //Citekey puede contener el DOI

            if (booktitle != null || article!= null) {
                String ven;
                if (booktitle!=null) ven = booktitle.toUserString().replaceAll("[{-}]", "");
                else ven = article.toUserString().replaceAll("[{-}]", "");
                int idVen = venue.insertRow(s,ven);
                atributsOfRow.append(", idVen");
                valuesOfRow.append(", ").append(idVen);
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
                valuesOfRow.append(", '").append(pages.toUserString().replaceAll("[{-}]", "")).append("'");
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
                valuesOfRow.append(", \'").append(aux.replaceAll("[{-}]", "")).append("\'");
            }
            affiliationToInsert = -1;
            if (affil != null) {
                String aux = affil.toUserString().replaceAll("[{-}]", "").replaceAll("[']", "");
                affiliationToInsert = company.insertRow(s,aux);
            }
            atributsOfRow.append(", idDL");
            valuesOfRow.append(", ").append(idDL).append(") ");

            query = atributsOfRow.toString() + valuesOfRow;
            System.out.println(query);

            s.execute(query);
            System.out.println("Inserted row with author, doi, ....");
        } catch (SQLException e) {
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

    /*
    referencias(idRef,  abstract, author, doi, year, citeKey, venue,
                title,  journal, keywords, number, numpages, pages, volume,dl)
    {dl} references digitalLibraries, {venue} references venues,   primary key(idRef)
    */
    public static void createTable(Statement s) {
        try {
            s.execute("create table referencias(idRef INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, " +
                    "INCREMENT BY 1), type varchar(50), doi varchar(50), citeKey varchar(50), " +
                    "idVen int, title varchar(200), journal varchar(100), keywords varchar(500), number INT, " +
                    "numpages INT, pages varchar(10), volume INT, año INT, abstract varchar(2000), idDL int, " +
                    "PRIMARY KEY (idRef), CONSTRAINT DL_FK_R FOREIGN KEY (idDL) REFERENCES digitalLibraries (idDL)," +
                    "CONSTRAINT VEN_FK_R FOREIGN KEY (idVen) REFERENCES venues (idVen))");
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
