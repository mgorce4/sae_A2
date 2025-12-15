package iut.unilim.fr.back.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.List;
import iut.unilim.fr.back.Ressource.HeaderAndFooter;
import iut.unilim.fr.back.Ressource.HeaderEvent;

import java.io.FileOutputStream;
import java.util.ArrayList;

import static com.itextpdf.text.List.UNORDERED;

public class PdfController {
    private static final BaseColor COL_BLEU_FOND = new BaseColor(59, 66, 117);
    private static final BaseColor COL_NOIR_HEADER = new BaseColor(34, 34, 34);
    private static final BaseColor COL_GRIS_CORPS = new BaseColor(128, 128, 128);
    private static final BaseColor COL_TEXTE = BaseColor.WHITE;
    private static final String baseFont = "src/main/resources/font/trade-gothic-lt-std-58a78e64434a9.otf";

    public static void generatePdf() {
        try {
            Document document = new Document(PageSize.A4, 36, 36, 110, 40);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("iTextHelloWorld.pdf"));

            HeaderAndFooter event = new HeaderAndFooter();
            writer.setPageEvent(event);

            document.open();


            Font font = FontFactory.getFont(baseFont, 11, BaseColor.BLACK);
            Font contentFont = FontFactory.getFont(baseFont, 11, new BaseColor(255, 255, 255));
            Font fontTitle = FontFactory.getFont(baseFont, 16, BaseColor.BLACK);

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);

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

                com.itextpdf.text.List listePuces = new com.itextpdf.text.List(UNORDERED);
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

            com.itextpdf.text.List saeList = new com.itextpdf.text.List(UNORDERED);
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

            com.itextpdf.text.List modaliteList = new com.itextpdf.text.List(UNORDERED);
            modaliteList.setListSymbol("•");
            modaliteList.setSymbolIndent(12);
            for (String u_modalite : modalites) {
                modaliteList.add(new ListItem(new Chunk(u_modalite, contentFont)));
            }

            PdfPTable contentModalite = createContentDiv(modalite, modaliteList);
            contents.add(contentModalite);

            document.add(table);

            // Repartition des heures
            Chunk repartitionHeure = new Chunk("Répartition des heures par élève:", contentFont);
            PdfPTable repartitionProgrammeTable = new PdfPTable(5);
            repartitionProgrammeTable.setWidthPercentage(100);
            Chunk ressource = new Chunk("Ressource",  contentFont);
            Chunk cm = new Chunk("CM",  contentFont);
            Chunk td = new Chunk("TD",  contentFont);
            Chunk tp = new Chunk("TP",  contentFont);
            Chunk total =  new Chunk("Total",  contentFont);

            // PN :
            Chunk pn = new Chunk("Programme Nationnal",  contentFont);
            Chunk pnCm = new Chunk("4",  contentFont);
            Chunk pnTd = new Chunk("6",  contentFont);
            Chunk pnTp = new Chunk("30", contentFont);
            Chunk pnTotal = new Chunk("40", contentFont);

            // Actuel :
            Chunk programme = new Chunk("Votre programme", contentFont);
            Chunk programmeCm = new Chunk("4", contentFont);
            Chunk programmeTd = new Chunk("8", contentFont);
            Chunk programmeTp = new Chunk("20", contentFont);
            Chunk programmeTotal = new Chunk("32", contentFont);

            ArrayList<Chunk> programmeContent = new ArrayList<>();

            programmeContent.add(ressource);
            programmeContent.add(cm);
            programmeContent.add(td);
            programmeContent.add(tp);
            programmeContent.add(total);
            programmeContent.add(pn);
            programmeContent.add(pnCm);
            programmeContent.add(pnTd);
            programmeContent.add(pnTp);
            programmeContent.add(pnTotal);
            programmeContent.add(programme);
            programmeContent.add(programmeCm);
            programmeContent.add(programmeTd);
            programmeContent.add(programmeTp);
            programmeContent.add(programmeTotal);

            for (Chunk chunk : programmeContent) {
                PdfPCell cell = new PdfPCell(new Phrase(chunk.getContent(), contentFont));

                styleCelluleGrise(cell);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

                repartitionProgrammeTable.addCell(cell);
            }


            PdfPTable repartitionContent = createContentDiv(repartitionHeure, repartitionProgrammeTable);
            repartitionContent.setPaddingTop(15);
            contents.add(repartitionContent);

            // Contenue pedago
            Chunk contenuePedago = new Chunk("Contenue pedagogique", contentFont);
            PdfPTable pedagoTable = new PdfPTable(2);
            pedagoTable.setWidthPercentage(100);
            pedagoTable.setWidths(new float[]{1f, 6f});

            Font fontBlanc = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, BaseColor.WHITE);

            String[] itemsCM = {
                    "1 Lorem ipsum dolor",
                    "2 Lorem ipsum dolor"
            };
            ajouterLigneCategorie(pedagoTable, "CM", itemsCM, fontBlanc);

            String[] itemsTD = {
                    "1 Lorem ipsum dolor",
                    "2 Lorem ipsum dolor",
                    "3 Lorem ipsum dolor",
                    "4 Lorem ipsum dolor"
            };
            ajouterLigneCategorie(pedagoTable, "TD", itemsTD, fontBlanc);

            String[] itemsTP = {
                    "1 Lorem ipsum dolor",
                    "2 Lorem ipsum dolor",
                    "3 Lorem ipsum dolor",
                    "4 Lorem ipsum dolor",
                    "5 Lorem ipsum dolor",
                    "6 Lorem ipsum dolor",
                    "7 Lorem ipsum dolor",
                    "8 Lorem ipsum dolor",
                    "9 Lorem ipsum dolor"
            };
            ajouterLigneCategorie(pedagoTable, "TP", itemsTP, fontBlanc);

            PdfPTable contentPedagoContent = createContentDiv(contenuePedago, pedagoTable);
            contents.add(contentPedagoContent);

            PdfPTable contentDiv = createContent(descriptif, contents);
            document.add(contentDiv);
            document.newPage();
            ArrayList<PdfPTable> suiviRessourceContents = new ArrayList<>();

            Chunk suiviRessource = new Chunk("Suivi de la Ressource/Module", contentFont);
            Chunk retourEquipePedago = new Chunk("Retour de l'equipe pedagogique", contentFont);
            Chunk retourEquipePedagoContent = new Chunk("lorem ipsum", contentFont);

            suiviRessourceContents.add(createContentDiv(retourEquipePedago, retourEquipePedagoContent));

            Chunk retourEtu = new Chunk("Retour Etudiant", contentFont);
            Chunk retourEtuContent = new Chunk("lorem ipsum", contentFont);

            suiviRessourceContents.add(createContentDiv(retourEtu, retourEtuContent));

            Chunk amelioration = new Chunk("Ameliorations a mettre en oeuvre", contentFont);
            Chunk ameliorationContent = new Chunk("lorem ipsum", contentFont);

            suiviRessourceContents.add(createContentDiv(amelioration, ameliorationContent));

            PdfPTable suiviRessourcesTable = createContent(suiviRessource, suiviRessourceContents);
            suiviRessourcesTable.setPaddingTop(15);

            document.add(suiviRessourcesTable);
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
        table.setSplitLate(true);
        table.setSplitRows(true);

        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);

        cell.setBackgroundColor(COL_GRIS_CORPS);

        cell.setPaddingTop(45f);
        cell.setPaddingLeft(15f);
        cell.setPaddingRight(15f);
        cell.setPaddingBottom(15f);

        cell.setCellEvent(new HeaderEvent(titreHeader.getContent(), FONT_HEADER_BLOCK, COL_NOIR_HEADER));

        cell.addElement(elementBody);

        table.addCell(cell);
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

    private static void ajouterLigneCategorie(PdfPTable table, String label, String[] items, Font font) {

        PdfPCell cellLabel = new PdfPCell(new Phrase(label, font));
        cellLabel.setBorder(Rectangle.NO_BORDER);
        cellLabel.setBackgroundColor(null);
        cellLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellLabel.setVerticalAlignment(Element.ALIGN_TOP);
        cellLabel.setPaddingRight(10);
        cellLabel.setPaddingTop(0);
        table.addCell(cellLabel);

        PdfPCell cellListe = new PdfPCell();
        cellListe.setBorder(Rectangle.NO_BORDER);
        cellListe.setBackgroundColor(null);
        cellListe.setPadding(0);

        com.itextpdf.text.List list = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
        list.setListSymbol("\u2022");
        list.setSymbolIndent(10);
        Chunk bulletSymbol = new Chunk("\u2022", font);
        list.setListSymbol(bulletSymbol);

        for (String item : items) {
            ListItem li = new ListItem(item, font);
            li.setAlignment(Element.ALIGN_LEFT);
            li.setSpacingAfter(2f);
            list.add(li);
        }

        cellListe.addElement(list);
        table.addCell(cellListe);

        PdfPCell cellSpace = new PdfPCell();
        cellSpace.setColspan(2);
        cellSpace.setBorder(Rectangle.NO_BORDER);
        cellSpace.setFixedHeight(15f);
        table.addCell(cellSpace);
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
