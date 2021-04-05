package Data;

import org.jbibtex.*;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.*;
import java.util.*;

import static Data.digitalLibrary.getIDs;
import static Data.digitalLibrary.getNames;

public class article {

    private static String authorsToInsert = null;
    private static int affiliationToInsert = -1;

    static Key abstractKey = new Key("abstract");
    static Key keywordsKey = new Key("keywords");
    static Key numpagesKey = new Key("numpages");
    static Key articleKey = new Key("article");
    static Key affiliationKey = new Key("affiliation");

    public static String[] pedirInfo(Statement s) throws SQLException {
        System.out.println("Escribir el path absoluto donde se encuentra el fichero a exportar: ");
        Scanner entrada=new Scanner(System.in);
        String path = entrada.nextLine();
        System.out.println("Path escogido: " + path);
        System.out.println("Escoger el número de la biblioteca de donde se exporta el archivo:");

        ArrayList<String> DLs = getNames(s);
        String idDL = null;
        for (int i = 0; i < DLs.size(); i++)
            System.out.println(i+1 + ". " + DLs.get(i));
        try {
            int num=entrada.nextInt() - 1;
            if (num >= 0 & num < DLs.size()) {
                idDL = getIDs(s).get(num);
                System.out.println("Se ha escogido " + idDL + ". " + DLs.get(num));
            } else {
                System.out.println("El numero no esta entre el 1 y el 6");
            }
        } catch (Exception e) {
            System.out.println("No se ha escrito un número.");
        }
        return new String[] {path, idDL};
    }

    public static void importar(String path, String nameDL, Statement s) throws IOException,ParseException,SQLException {
        Reader reader = new FileReader(path);
        BibTeXParser bibtexParser = new BibTeXParser(); //add Exception
        BibTeXDatabase database = bibtexParser.parse(reader);
        Map<Key, BibTeXEntry> entryMap = database.getEntries();
        Collection<BibTeXEntry> entries = entryMap.values();
        // add rows of file
        for(BibTeXEntry entry : entries){
            String doi = addArticle(nameDL, s, entry);
            if (doi != null) {
                if (authorsToInsert != null) {
                    Integer[] idsResearchers = researcher.insertRows(authorsToInsert, s);
                    s.getConnection().commit();
                    author.insertRows(idsResearchers, doi, s);
                }
                if (affiliationToInsert != -1) affiliation.insertRow(s, affiliationToInsert, doi);
            }
        }
        reader.close();
    }

    public static ResultSet getAllData(Statement s) throws SQLException {
        ResultSet rs;
        rs = s.executeQuery("SELECT * FROM articles");
        return rs;
    }
    public static ResultSet getArticle(Statement s, String doi) throws SQLException {
        return s.executeQuery("SELECT * FROM articles where doi = '" + doi + "' ");
    }

//Devuelve un string de todos los autores de la referencia
    static String addArticle(String idDL, Statement s, BibTeXEntry entry) throws SQLException {

        String doi = entry.getField(BibTeXEntry.KEY_DOI).toUserString();

        if (doi != null) {
            ResultSet rs = getArticle(s, doi);
            if (rs.next()) {
                addInformationOfDL(rs,entry,s,doi); //añadir informacion en los valores null
            }
            else {
                System.out.println("El article no existe");
                insertRow(s, entry, doi);//create article nuevo
            }
            reference.insertRow(s,doi,idDL); //despues del try catch crear una nueva referencia
            return doi;
        }
        else {
            System.out.println("Este article no contiene doi");
            return null;
        }
    }

    private static void insertRow(Statement s, BibTeXEntry entry, String doi) {
        try {
            Key type = entry.getType();
            Value title = entry.getField(BibTeXEntry.KEY_TITLE);
            Value authors = entry.getField(BibTeXEntry.KEY_AUTHOR);
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

            String query;
            StringBuilder atributsOfRow = new StringBuilder("INSERT INTO articles(");
            StringBuilder valuesOfRow = new StringBuilder(") VALUES (");

            atributsOfRow.append("doi");
            valuesOfRow.append("'").append(doi.replaceAll("[{-}]", "")).append("'");

            atributsOfRow.append(", type, citeKey");
            valuesOfRow.append(", '").append(type).append("', '").append(entry.getKey()).append("'");
            //Citekey puede contener el DOI

            if (booktitle != null || article != null) {
                String ven;
                if (booktitle != null) ven = booktitle.toUserString().replaceAll("[{-}]", "");
                else ven = article.toUserString().replaceAll("[{-}]", "");
                int idVen = venue.insertRow(s, ven);
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
            if (number != null ) {
                if (!number.toUserString().replaceAll("[{-}]", "").equals("")) {
                    atributsOfRow.append(", number");
                    valuesOfRow.append(", ").append(number.toUserString().replaceAll("[{-}]", ""));
                }
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
                if (!volume.toUserString().replaceAll("[{-}]", "").equals("")) {
                    atributsOfRow.append(", volume");
                    valuesOfRow.append(", ").append(volume.toUserString().replaceAll("[{-}]", ""));
                }
            }
            if (year != null) {
                atributsOfRow.append(", año");
                valuesOfRow.append(", ").append(year.toUserString().replaceAll("[{-}]", ""));
            }
            if (abstractE != null) {
                String aux1 = abstractE.toUserString().replaceAll("[']", "");
                //El simbolo ' dentro del abstract provoca errores
                atributsOfRow.append(", abstract");
                valuesOfRow.append(", \'").append(aux1.replaceAll("[{-}]", "")).append("\'");
            }
            authorsToInsert = null;
            if (authors != null)
                authorsToInsert = authors.toUserString().replaceAll("[\n]", " ").replaceAll("[{-}]", "");

            affiliationToInsert = -1;
            if (affil != null) {
                String aux1 = affil.toUserString().replaceAll("[{-}]", "").replaceAll("[']", "");
                affiliationToInsert = company.insertRow(s, aux1);
            }
            query = atributsOfRow.toString() + valuesOfRow.append(") ");
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
                e = e.getNextException();
            }
        }
    }

    private static void addInformationOfDL(ResultSet rs, BibTeXEntry entry, Statement s, String doi)  {
        try {
            /*UPDATE Articles
            SET  keywords= 'computer science, software', volume= 15
            WHERE doi = '10.18178/ijiet.2020.10.10.1455' ;
            */
            Value title = entry.getField(BibTeXEntry.KEY_TITLE);
            Value authors = entry.getField(BibTeXEntry.KEY_AUTHOR);
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

            String query;
            StringBuilder queryIni = new StringBuilder("UPDATE Articles SET ");
            StringBuilder queryEnd = new StringBuilder(" WHERE doi = '" + doi + "'");
            boolean first = true;

           // "idVen int, title varchar(200), journal varchar(100), keywords varchar(1000) number INT, numpages INT, pages varchar(20), volume INT, año INT, abstract "

            if ((rs.getString(4) == null ) & (booktitle != null || article != null)) {
                String ven;
                if (booktitle != null) ven = booktitle.toUserString().replaceAll("[{-}]", "");
                else ven = article.toUserString().replaceAll("[{-}]", "");
                int idVen = venue.insertRow(s, ven);
                first = false;
                queryIni.append(" idVen = ").append(idVen);
            }
            if ((rs.getString(5) == null ) & (title != null)) {
                if (first) first = false;
                else queryIni.append(", ");
                queryIni.append(" title = '").append(title.toUserString().replaceAll("[{-}]", "")).append("'");
            }
            if ((rs.getString(6) == null ) & (journal != null)) {
                if (first) first = false;
                else queryIni.append(", ");
                queryIni.append(" journal = '").append(journal.toUserString().replaceAll("[{-}]", "")).append("'");
            }
            if ((rs.getString(7) == null ) & (keywords != null)) {
                if (first) first = false;
                else queryIni.append(", ");
                queryIni.append(" keywords = '").append(keywords.toUserString().replaceAll("[{-}]", "")).append("'");
            }
            if ((rs.getString(8) == null ) & (number != null )) {
                if (!number.toUserString().replaceAll("[{-}]", "").equals("")){
                    if (first) first = false;
                    else queryIni.append(", ");
                    queryIni.append(" number = ").append(number.toUserString().replaceAll("[{-}]", ""));
                }
            }
            if ((rs.getString(9) == null ) & (numpages != null)) {
                if (first) first = false;
                else queryIni.append(", ");
                queryIni.append(" numpages = ").append(numpages.toUserString().replaceAll("[{-}]", ""));
            }
            if ((rs.getString(10) == null ) & (pages != null)) {
                if (first) first = false;
                else queryIni.append(", ");
                queryIni.append(" pages = '").append(pages.toUserString().replaceAll("[{-}]", "")).append("'");
            }
            if ((rs.getString(11) == null) & (volume != null )) {
                if (!volume.toUserString().replaceAll("[{-}]", "").equals("")) {
                    if (first) first = false;
                    else queryIni.append(", ");
                    queryIni.append(" volume = ").append(volume.toUserString().replaceAll("[{-}]", ""));
                }
            }
            if ((rs.getString(12) == null ) &(year != null)) {
                if (first) first = false;
                else queryIni.append(", ");
                queryIni.append(" año = ").append(year.toUserString().replaceAll("[{-}]", ""));
            }
            if ((rs.getString(13) == null ) & (abstractE != null)) {
                if (first) first = false;
                else queryIni.append(", ");
                String aux1 = abstractE.toUserString().replaceAll("[']", "");
                //El simbolo ' dentro del abstract provoca errores
                queryIni.append(" abstract = \'").append(aux1.replaceAll("[{-}]", "")).append("\'");
            }
            authorsToInsert = null;
            if (authors != null)
                authorsToInsert = authors.toUserString().replaceAll("[\n]", " ").replaceAll("[{-}]", "");

            affiliationToInsert = -1;
            if (affil != null) {
                String aux1 = affil.toUserString().replaceAll("[{-}]", "").replaceAll("[']", "");
                affiliationToInsert = company.insertRow(s, aux1);
            }
            query = queryIni.toString() + queryEnd;

            if (!first) {
                System.out.println(query );
                s.execute(query);
            }
            System.out.println("Inserted row with author, doi, ....");
        } catch (SQLException e) {
            System.out.println("Error");
            while (e != null) {
                System.err.println("\n----- SQLException -----");
                System.err.println("  SQL State:  " + e.getSQLState());
                System.err.println("  Error Code: " + e.getErrorCode());
                System.err.println("  Message:    " + e.getMessage());
                e = e.getNextException();
            }
        }
    }

    public static void createTable(Statement s) {
        try {
            s.execute("create table articles( doi varchar(50), type varchar(50), citeKey varchar(50), " +
                    "idVen int, title varchar(200), journal varchar(100), keywords varchar(1000), " +
                    "number INT, numpages INT, pages varchar(20), volume INT, año INT, abstract varchar(3000), " +
                    "PRIMARY KEY (doi), CONSTRAINT VEN_FK_R FOREIGN KEY (idVen) REFERENCES venues (idVen))");
            //type y citekey not null
            System.out.println("Created table articles");
        } catch (SQLException t  ) {
            if (t.getSQLState().equals("X0Y32"))
                System.out.println("Table articles exists");
            else System.out.println("Error en la creación de table articles");
        }
    }

    public static void dropTable(Statement s) throws SQLException {
        s.execute("drop table articles");
        System.out.println("Dropped table articles");
    }
}
