package iut.unilim.fr.back.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import iut.unilim.fr.back.Ressource.HeaderAndFooter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class PdfController {
    private static final BaseColor COL_BLEU_FOND = new BaseColor(59, 66, 117);
    private static final BaseColor COL_NOIR_HEADER = new BaseColor(34, 34, 34);
    private static final BaseColor COL_GRIS_CORPS = new BaseColor(128, 128, 128);
    private static final BaseColor COL_TEXTE = BaseColor.WHITE;
    private static final String baseFont = "src/main/resources/font/trade-gothic-lt-std-58a78e64434a9.otf";

    public static void generatePdf() {
        try {
            Document document = new Document(PageSize.A4, 36, 36, 59, 40);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("iTextHelloWorld.pdf"));

            HeaderAndFooter event = new HeaderAndFooter();
            writer.setPageEvent(event);

            document.open();


            Font font = FontFactory.getFont(baseFont, 11, BaseColor.BLACK);
            Font contentFont = FontFactory.getFont(baseFont, 11, new BaseColor(255, 255, 255));
            Font fontTitle = FontFactory.getFont(baseFont, 16, BaseColor.BLACK);

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);

            // Header, doit etre supprimer a terme
            Chunk fileName = new Chunk("Fiche Ressource", font);
            Chunk department = new Chunk("Departement Info", contentFont);

            // Titre Ressource
            PdfPCell cellLeft = new PdfPCell();
            cellLeft.setBorder(Rectangle.NO_BORDER);
            cellLeft.setVerticalAlignment(Element.ALIGN_MIDDLE);


            Paragraph refUE = new Paragraph("RefUE", font);
            Paragraph refRessource = new Paragraph("RefRessource", font);

            Paragraph titreRessource = new Paragraph("Titre Ressource", fontTitle);
            Paragraph profReferent =  new Paragraph("ProfReferent Info", font);

            refUE.setLeading(0,2);
            refRessource.setLeading(10f,0);

            cellLeft.addElement(refUE);
            cellLeft.addElement(refRessource);
            cellLeft.setPaddingTop(15);
            cellLeft.setPaddingBottom(15);
            table.addCell(cellLeft);

            PdfPCell cellCenter = new PdfPCell(titreRessource);
            cellCenter.setBorder(Rectangle.NO_BORDER);
            cellCenter.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellCenter.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cellCenter);

            PdfPCell cellRight = new PdfPCell(profReferent);
            cellRight.setBorder(Rectangle.NO_BORDER);
            cellRight.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellRight.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cellRight);

            // Premiere div
            Chunk descriptif = new Chunk("Descriptif", contentFont);
            Chunk objectif = new Chunk("Objectif", contentFont);
            Chunk objectifContent =  new Chunk("blablablablablablablablablablablablablablablablablablablablablabablablablablablablabalblablablablablablablablablabalblablablablablablabnlablablablablablablablablablabalblablablablablablablablablablababablabla", contentFont);
            PdfPTable content = createContentDiv(objectif, objectifContent);

            ArrayList<PdfPTable> contents = new ArrayList<>();
            contents.add(content);

            // Div competence
            Chunk competence = new Chunk("Competence", contentFont);

            PdfPTable competenceTable = new PdfPTable(2);
            competenceTable.setWidthPercentage(100);
            competenceTable.setWidths(new float[]{1, 3});
            Integer nbLigne = 2;

            for (int i = 0; i < nbLigne; i++) {
                Chunk chunkTitre = new Chunk("Truc " + (i + 1), contentFont);
                PdfPCell competenceCellLeft = new PdfPCell(new Phrase(chunkTitre));

                styleCelluleGrise(competenceCellLeft);
                competenceCellLeft.setHorizontalAlignment(Element.ALIGN_CENTER);
                competenceCellLeft.setVerticalAlignment(Element.ALIGN_MIDDLE);

                competenceTable.addCell(competenceCellLeft);

                PdfPCell competenceCellRight = new PdfPCell();
                styleCelluleGrise(competenceCellRight);

                com.itextpdf.text.List listePuces = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
                listePuces.setListSymbol("•");
                listePuces.setSymbolIndent(12);

                listePuces.add(new ListItem(new Chunk("Competence " + (i + 1) + ".1", contentFont)));
                listePuces.add(new ListItem(new Chunk("Competence " + (i + 1) + ".2", contentFont)));

                competenceCellRight.addElement(listePuces);
                competenceTable.addCell(competenceCellRight);
            }

            PdfPTable contentCompetence = createContentDiv(competence,competenceTable);
            contents.add(contentCompetence);

            // Div SAE
            Chunk saeConcerne = new Chunk("SaeConcerne", contentFont);
            ArrayList<String> saes = new ArrayList<>();
            saes.add("truc1");
            saes.add("truc2");

            com.itextpdf.text.List saeList = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
            saeList.setListSymbol("•");
            saeList.setSymbolIndent(12);
            for (String sae : saes) {
                saeList.add(new ListItem(new Chunk(sae, contentFont)));
            }

            PdfPTable contentSae = createContentDiv(saeConcerne, saeList);
            contents.add(contentSae);

            // Div mot cles
            Chunk motsCleTitre =  new Chunk("Mots Cle", contentFont);
            Chunk motsCle = new Chunk("SQL, Management...", contentFont);

            PdfPTable motCleContent = createContentDiv(motsCleTitre, motsCle);
            contents.add(motCleContent);


            Chunk modalite = new Chunk("Modalite", contentFont);
            ArrayList<String> modalites = new ArrayList<>();
            modalites.add("UML jsp j'en ai mare un peu");

            com.itextpdf.text.List modaliteList = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
            modaliteList.setListSymbol("•");
            modaliteList.setSymbolIndent(12);
            for (String u_modalite : modalites) {
                modaliteList.add(new ListItem(new Chunk(u_modalite, contentFont)));
            }

            PdfPTable contentModalite = createContentDiv(modalite, modaliteList);
            contents.add(contentModalite);

            document.add(fileName);
            document.add(department);
            document.add(table);

            PdfPTable contentDiv = createContent(descriptif, contents);
            document.add(contentDiv);

            document.close();

            System.out.println("PDF généré !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PdfPTable createContentDiv(Chunk titreHeader, Element elementBody) {
        Font FONT_HEADER_BLOCK = FontFactory.getFont(baseFont, 12, Font.BOLD, COL_TEXTE);

        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);

        PdfPCell cellHeader = new PdfPCell(new Phrase(String.valueOf(titreHeader), FONT_HEADER_BLOCK));
        cellHeader.setBorder(Rectangle.NO_BORDER);
        cellHeader.setBackgroundColor(COL_NOIR_HEADER);
        cellHeader.setPadding(8);
        cellHeader.setPaddingLeft(15);
        table.addCell(cellHeader);

        PdfPCell cellBody = new PdfPCell();
        cellBody.setBorder(Rectangle.NO_BORDER);
        cellBody.setBackgroundColor(COL_GRIS_CORPS);
        cellBody.setPadding(15);

        cellBody.addElement(elementBody);

        table.addCell(cellBody);
        table.setSpacingAfter(10f);

        return table;
    }

    private static PdfPTable createContent(Chunk containerTitle, ArrayList<PdfPTable> contents)  {
        Font FONT_TITRE_CONTAINER = FontFactory.getFont(baseFont, 14, Font.NORMAL, COL_TEXTE);

        PdfPTable masterTable = new PdfPTable(1);
        masterTable.setPaddingTop(15);
        masterTable.setWidthPercentage(100);
        masterTable.setSplitLate(false);
        masterTable.setSplitRows(true);

        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(100);
        headerTable.setTableEvent(new BlueBackgroundEvent(COL_BLEU_FOND));

        PdfPCell cellTitre = new PdfPCell(new Paragraph(new Phrase(containerTitle).getContent(), FONT_TITRE_CONTAINER));
        cellTitre.setBorder(Rectangle.NO_BORDER);
        cellTitre.setBackgroundColor(null);
        cellTitre.setPadding(15);
        cellTitre.setPaddingBottom(15);

        headerTable.addCell(cellTitre);

        PdfPCell cellWrapperHeader = new PdfPCell(headerTable);
        cellWrapperHeader.setBorder(Rectangle.NO_BORDER);
        masterTable.addCell(cellWrapperHeader);

        PdfPTable bodyTable = new PdfPTable(1);
        bodyTable.setWidthPercentage(100);
        bodyTable.setSplitLate(false);
        bodyTable.setSplitRows(true);
        bodyTable.setPaddingTop(15);

        for (PdfPTable content : contents) {
            content.setWidthPercentage(100);

            PdfPCell wrapperCell = new PdfPCell();
            wrapperCell.setBorder(Rectangle.NO_BORDER);
            wrapperCell.setBackgroundColor(null);

            wrapperCell.setPaddingLeft(15);
            wrapperCell.setPaddingRight(15);
            wrapperCell.setPaddingTop(10);

            wrapperCell.addElement(content);

            bodyTable.addCell(wrapperCell);
        }

        PdfPCell cellWrapperBody = new PdfPCell(bodyTable);
        cellWrapperBody.setBorder(Rectangle.NO_BORDER);
        cellWrapperBody.setPadding(0);
        masterTable.addCell(cellWrapperBody);

        return masterTable;
    }

    private static void styleCelluleGrise(PdfPCell cell) {
        cell.setBackgroundColor(COL_GRIS_CORPS);
        cell.setBorderColor(BaseColor.WHITE);
        cell.setBorderWidth(1f);
        cell.setPadding(10);
    }

    static class BlueBackgroundEvent implements PdfPTableEvent {
        private BaseColor color;

        public BlueBackgroundEvent(BaseColor color) {
            this.color = color;
        }

        @Override
        public void tableLayout(PdfPTable table, float[][] widths, float[] heights, int headerRows, int rowStart, PdfContentByte[] canvases) {
            float[] width = widths[0];
            float x1 = width[0];
            float x2 = width[width.length - 1];
            float y1 = heights[0];
            float y2 = heights[heights.length - 1];

            PdfContentByte cb = canvases[PdfPTable.BACKGROUNDCANVAS];
            cb.saveState();
            cb.setColorFill(color);
            cb.roundRectangle(x1, y2, x2 - x1, y1 - y2, 10);
            cb.fill();
            cb.restoreState();
        }
    }
}
