package iut.unilim.fr.back.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PdfController {
    public static void generatePdf() {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("iTextHelloWorld.pdf"));
            Path imagePath = Paths.get("src/main/resources/img/unilim.jpg");
            BaseFont baseFont = BaseFont.createFont("src/main/resources/font/trade-gothic-lt-std-58a78e64434a9.otf",  BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            document.open();


            Font font = FontFactory.getFont(String.valueOf(baseFont), 11, BaseColor.BLACK);
            Font whiteFont = FontFactory.getFont(String.valueOf(baseFont), 11, new BaseColor(255, 255, 255));

            Image unilimImage = Image.getInstance(imagePath.toAbsolutePath().toString());
            Chunk fileName = new Chunk("Fiche Ressource", font);
            Chunk department = new Chunk("Departement Info", whiteFont);

            Chunk reference = new Chunk("Reference", whiteFont);
            Chunk date = new Chunk("Date", whiteFont);

            Chunk refUE = new Chunk("RefUE", font);
            Chunk refRessource = new Chunk("RefRessource", font);

            Chunk titreRessource = new Chunk("Titre Ressource", font);
            Chunk profReferent =  new Chunk("ProfReferent Info", font);

            Chunk descriptif = new Chunk("Descriptif", whiteFont);
            Chunk objectif = new Chunk("Objectif", whiteFont);
            Chunk objectifContent =  new Chunk("blablablablablablablablablablablablablablablablablablablablablabablablablablablablabalblablablablablablablablablabalblablablablablablabnlablablablablablablablablablabalblablablablablablablablablablababablabla", whiteFont);

            Chunk competance = new Chunk("Competance", whiteFont);


            Chunk saeConcerne = new Chunk("SaeConcerne", whiteFont);

            Chunk motsCle =  new Chunk("MotsCle", whiteFont);

            Chunk modalite = new Chunk("Modalite", whiteFont);


            BaseColor couleurSurlignage = new BaseColor(181, 22, 33);
            reference.setBackground(couleurSurlignage);



            document.add(unilimImage);
            document.add(fileName);
            document.add(reference);
            document.add(department);
            document.add(date);
            document.add(refUE);
            document.add(refRessource);
            document.add(titreRessource);
            document.add(profReferent);
            document.add(descriptif);
            document.add(objectif);
            document.add(objectifContent);
            document.add(competance);
            document.add(saeConcerne);
            document.add(motsCle);
            document.add(modalite);

            document.newPage();
            

            document.close();

            System.out.println("PDF généré !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
