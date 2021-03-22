import Data.author;
import Data.digitalLibrary;
import org.jbibtex.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class Pruebas {
    public static void main(String[] args) throws FileNotFoundException, ParseException {
        Reader reader = new FileReader("C:\\Apache\\db-derby-10.15.2.0-bin\\demo\\programs\\TFG\\src\\main\\resources\\10.1007_978-3-030-65847-2_8SpringerLinkConfernce.bib");
        BibTeXParser bibtexParser = new BibTeXParser();
        BibTeXDatabase database = bibtexParser.parse(reader);
        Map<Key, BibTeXEntry> entryMap = database.getEntries();
        Collection<BibTeXEntry> entries = entryMap.values();
        for(BibTeXEntry entry : entries){
            Value authors = entry.getField(BibTeXEntry.KEY_AUTHOR);
            if(authors!=null){
                //EL espacio EOF provoca errores!!!
                String aux = authors.toUserString().replaceAll("[\n]", "");
                aux = aux.replaceAll("[{-}]", "");

                ArrayList<Integer> ret = new ArrayList<>();
                System.out.println(ret.size());
                //author.insertRows(aux,s);
            }
        }
    }
}
