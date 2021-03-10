import Data.digitalLibrary;
import org.jbibtex.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Map;

public class Pruebas {
    public static void main(String[] args) throws FileNotFoundException, ParseException {
        System.out.println(digitalLibrary.DLs.size());
        Reader reader = new FileReader("src/main/resources/ExScopus.bib");
        BibTeXParser bibtexParser = new BibTeXParser();
        BibTeXDatabase database = bibtexParser.parse(reader);
        Map<Key, BibTeXEntry> entryMap = database.getEntries();
        Collection<BibTeXEntry> entries = entryMap.values();
        String s = "Ramos, David Brito and Martins Ramos, Ilmara Monteverde and Gasparini,    Isabela and Teixeira de Oliveira, Elaine Harada";
        System.out.println(s);

    }
}
