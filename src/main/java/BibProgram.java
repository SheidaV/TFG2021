import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jbibtex.ParseException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BibProgram {
    public static void main(String[] args) throws IOException, ParseException {
        Reader reader = new FileReader("src/main/resources/ExScopus.bib");

        org.jbibtex.BibTeXParser bibtexParser = new org.jbibtex.BibTeXParser();

        org.jbibtex.BibTeXDatabase database = bibtexParser.parse(reader);
        
        System.out.println(database); 
        reader.close();
    }
}