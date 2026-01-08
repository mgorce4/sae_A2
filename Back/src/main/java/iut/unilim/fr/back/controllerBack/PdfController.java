package iut.unilim.fr.back.controllerBack;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import iut.unilim.fr.back.Ressource.HeaderAndFooter;
import iut.unilim.fr.back.Ressource.HeaderEvent;
import iut.unilim.fr.back.service.ResourceGetterService;

import java.io.FileOutputStream;
import java.util.ArrayList;

import static com.itextpdf.text.List.UNORDERED;
import static iut.unilim.fr.back.controllerBack.LogController.writeInPdfLog;

public class PdfController {
    private final BaseColor COL_BLEU_FOND = new BaseColor(59, 66, 117);
    private final BaseColor COL_NOIR_HEADER = new BaseColor(34, 34, 34);
    private final BaseColor COL_GRIS_CORPS = new BaseColor(128, 128, 128);
    private final BaseColor COL_CONTENT_TEXTE = BaseColor.WHITE;
    private final BaseColor COL_TEXTE = BaseColor.BLACK;
    private final String baseFont = "src/main/resources/font/trade-gothic-lt-std-58a78e64434a9.otf";

    private final int NONE = 0;
    private final int listIndent = 12;
    private final int widthPercentage = 100;
    private final int standardPadding = 15;
    private final int subPadding = 10;
    private final int specialTableNbRow = 1;



    public void generatePdf(ResourceGetterService res, String absolutePath) {
        int documentMargin = 36;
        int documentMarginTop = 110;
        int documentMarginBottom = 40;
        int documentStandardFontSize = 11;
        int documentTitleFontSize = 15;
        int keyWordStartIndex = 1;
        int repartitionProgramNbColumn = 5;
        int pedagoTableNbColumn = 2;
        float[] pedagoTableWidth = {1f, 6f};
        String pedagoContentSplitDelimitator = ",";


        String fileName = res.getFileName();
        if (!fileName.isEmpty()) {
            try {
                Document document = new Document(PageSize.A4, documentMargin, documentMargin, documentMarginTop, documentMarginBottom);
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(absolutePath + fileName + "_ressource_sheet.pdf"));

                HeaderAndFooter event = new HeaderAndFooter(res.getNbRessource(), res.getDepartment(), res.getRefUE(), res.getLabelResource());
                writer.setPageEvent(event);

                document.open();


                Font font = FontFactory.getFont(baseFont, documentStandardFontSize, COL_TEXTE);
                Font contentFont = FontFactory.getFont(baseFont, documentStandardFontSize, COL_CONTENT_TEXTE);
                Font fontTitle = FontFactory.getFont(baseFont, documentTitleFontSize, COL_TEXTE);

                PdfPTable table = createPdfPTable(res, font, fontTitle);

                // Premiere div
                Chunk descriptif = new Chunk("Descriptif", contentFont);
                Chunk objectif = new Chunk("Objectif", contentFont);
                Chunk objectifContent = new Chunk(res.getObjectiveContent(), contentFont);
                PdfPTable content = createContentDiv(objectif, objectifContent);

                ArrayList<PdfPTable> contents = new ArrayList<>();
                contents.add(content);

                // Div competence
                Chunk competenceTitle = new Chunk("Compétence", contentFont);

                ArrayList<String> competences = (ArrayList<String>) res.getSkills();
                com.itextpdf.text.List listePuces = new com.itextpdf.text.List(UNORDERED);


                for (String competence : competences) {
                    listePuces.setListSymbol("•");
                    listePuces.setSymbolIndent(listIndent);

                    listePuces.add(new ListItem(new Chunk(competence, contentFont)));
                }


                PdfPTable contentCompetence = createContentDiv(competenceTitle, listePuces);
                contents.add(contentCompetence);

                // Div SAE
                Chunk saeConcerne = new Chunk("SAÉ Concernée", contentFont);
                ArrayList<String> saes = (ArrayList<String>) res.getSaes();

                createITextList(contentFont, contents, saeConcerne, saes);

                // Div mot cles
                Chunk motsCleTitre = new Chunk("Mots Clé", contentFont);
                ArrayList<String> motsCles = (ArrayList<String>) res.getKeyWords();
                StringBuilder motCle = new StringBuilder(motsCles.getFirst());
                for  (int index=keyWordStartIndex; index<motsCles.size(); index++) {
                    motCle.append(", ").append(motsCles.get(index));
                }


                Chunk motsCleChunk = new Chunk(motCle.toString(), contentFont);

                PdfPTable motCleContent = createContentDiv(motsCleTitre, motsCleChunk);
                contents.add(motCleContent);


                Chunk modalite = new Chunk("Modalité", contentFont);
                ArrayList<String> modalites = (ArrayList<String>) res.getModalities();

                createITextList(contentFont, contents, modalite, modalites);

                document.add(table);

                // Repartition des heures
                Chunk repartitionHeure = new Chunk("Répartition des heures par élève:", contentFont);
                PdfPTable repartitionProgrammeTable = new PdfPTable(repartitionProgramNbColumn);
                repartitionProgrammeTable.setWidthPercentage(widthPercentage);

                ArrayList<Chunk> programmeContent = completeProgrammContent(res, contentFont);

                for (Chunk chunk : programmeContent) {
                    PdfPCell cell = new PdfPCell(new Phrase(chunk.getContent(), contentFont));

                    styleCelluleGrise(cell);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

                    repartitionProgrammeTable.addCell(cell);
                }

                PdfPTable repartitionContent = createContentDiv(repartitionHeure, repartitionProgrammeTable);
                repartitionContent.setPaddingTop(standardPadding);
                contents.add(repartitionContent);

                Chunk contenuePedago = new Chunk("Contenue pédagogique", contentFont);
                PdfPTable pedagoTable = new PdfPTable(pedagoTableNbColumn);
                pedagoTable.setWidthPercentage(widthPercentage);
                pedagoTable.setWidths(pedagoTableWidth);

                String pedagoContentDs = res.getPedagoContentDs();
                String pedagoContentCm = res.getPedagoContentCm();
                String pedagoContentTd = res.getPedagoContentTd();
                String pedagoContentTp = res.getPedagoContentTp();

                String[] itemsDs = pedagoContentDs.split(pedagoContentSplitDelimitator);
                ajouterLigneCategorie(pedagoTable, "DS", itemsDs, contentFont);

                String[] itemsCM = pedagoContentCm.split(pedagoContentSplitDelimitator);
                ajouterLigneCategorie(pedagoTable, "CM", itemsCM, contentFont);

                String[] itemsTD = pedagoContentTd.split(pedagoContentSplitDelimitator);
                ajouterLigneCategorie(pedagoTable, "TD", itemsTD, contentFont);

                String[] itemsTP = pedagoContentTp.split(pedagoContentSplitDelimitator);
                ajouterLigneCategorie(pedagoTable, "TP", itemsTP, contentFont);

                PdfPTable contentPedagoContent = createContentDiv(contenuePedago, pedagoTable);
                contents.add(contentPedagoContent);

                PdfPTable contentDiv = createContent(descriptif, contents);
                document.add(contentDiv);
                document.newPage();
                ArrayList<PdfPTable> suiviRessourceContents = new ArrayList<>();

                Chunk suiviRessource = new Chunk("Suivi de la Ressource/Module", contentFont);
                Chunk retourEquipePedago = new Chunk("Retour de l'équipe pédagogique", contentFont);
                Chunk retourEquipePedagoContent = new Chunk(res.getPedagoTeamFeedback(), contentFont);

                suiviRessourceContents.add(createContentDiv(retourEquipePedago, retourEquipePedagoContent));

                Chunk retourEtu = new Chunk("Retour étudiant", contentFont);
                Chunk retourEtuContent = new Chunk(res.getStudentFeedback(), contentFont);

                suiviRessourceContents.add(createContentDiv(retourEtu, retourEtuContent));

                Chunk amelioration = new Chunk("Améliorations à mettre en oeuvre", contentFont);
                Chunk ameliorationContent = new Chunk(res.getImprovements(), contentFont);

                suiviRessourceContents.add(createContentDiv(amelioration, ameliorationContent));

                PdfPTable suiviRessourcesTable = createContent(suiviRessource, suiviRessourceContents);
                suiviRessourcesTable.setPaddingTop(standardPadding);

                document.add(suiviRessourcesTable);
                document.close();

                writeInPdfLog("{user} Create a pdf for resource sheet: " + fileName + "_resource_sheet.pdf at " + absolutePath);
            } catch (Exception e) {
                writeInPdfLog(e.getMessage());
            }
        } else {
            writeInPdfLog("{user} attempt to generate a resource sheet pdf, but no matches found for the resource name");
        }
    }

    private ArrayList<Chunk> completeProgrammContent(ResourceGetterService res, Font contentFont) {
        Chunk ressource = new Chunk("Ressource", contentFont);
        Chunk cm = new Chunk("CM", contentFont);
        Chunk td = new Chunk("TD", contentFont);
        Chunk tp = new Chunk("TP", contentFont);
        Chunk total = new Chunk("Total", contentFont);

        ArrayList<Chunk> programmeContent = new ArrayList<>();
        programmeContent.add(ressource);
        programmeContent.add(cm);
        programmeContent.add(td);
        programmeContent.add(tp);
        programmeContent.add(total);

        // PN :
        Chunk pn = new Chunk("Programme Nationnal", contentFont);
        programmeContent.add(pn);

        ArrayList<Integer> pnContent = (ArrayList<Integer>) res.getHoursPN();
        for (Integer h : pnContent) {
            Chunk chunk = new Chunk(h.toString(), contentFont);
            programmeContent.add(chunk);
        }

        // Actuel :
        Chunk programme = new Chunk("Votre programme", contentFont);
        programmeContent.add(programme);
        ArrayList<Integer> hours = (ArrayList<Integer>) res.getHoursStudent();
        for (Integer h : hours) {
            Chunk chunk = new Chunk(h.toString(), contentFont);
            programmeContent.add(chunk);
        }
        return programmeContent;
    }

    private PdfPTable createPdfPTable(ResourceGetterService res, Font font, Font fontTitle) {
        int numColumns = 3;
        int standartLeading = 0;
        int refUeMultipliedLeading = 2;
        float refResourceStandardLeading = 10f;

        PdfPTable table = new PdfPTable(numColumns);
        table.setWidthPercentage(widthPercentage);

        // Titre Ressource
        PdfPCell cellLeft = new PdfPCell();
        cellLeft.setBorder(Rectangle.NO_BORDER);


        Paragraph refUE = new Paragraph(res.getRefUE(), font);
        Paragraph refRessource = new Paragraph(res.getRef(), font);

        Paragraph titreRessource = new Paragraph(res.getLabelResource(), fontTitle);
        Paragraph profReferent = new Paragraph(res.getProfRef(), font);

        refUE.setLeading(standartLeading, refUeMultipliedLeading);
        refRessource.setLeading(refResourceStandardLeading, standartLeading);

        cellLeft.addElement(refUE);
        cellLeft.addElement(refRessource);
        cellLeft.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellLeft.setPaddingBottom(standardPadding);
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
        return table;
    }

    private void createITextList(Font contentFont, ArrayList<PdfPTable> contents, Chunk title, ArrayList<String> items) {
        List modaliteList = new List(UNORDERED);
        modaliteList.setListSymbol("•");
        modaliteList.setSymbolIndent(listIndent);
        for (String u_modalite : items) {
            modaliteList.add(new ListItem(new Chunk(u_modalite, contentFont)));
        }

        PdfPTable contentModalite = createContentDiv(title, modaliteList);
        contents.add(contentModalite);
    }

    public PdfPTable createContentDiv(Chunk titreHeader, Element elementBody) {
        int headerFontSize = 12;
        int cellPaddingTop = 45;
        float spacingAfter = 10f;
        Font FONT_HEADER_BLOCK = FontFactory.getFont(baseFont, headerFontSize, Font.BOLD, COL_CONTENT_TEXTE);

        PdfPTable table = new PdfPTable(specialTableNbRow);
        table.setWidthPercentage(widthPercentage);
        table.setSplitLate(true);
        table.setSplitRows(true);

        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);

        cell.setBackgroundColor(COL_GRIS_CORPS);

        cell.setPaddingTop(cellPaddingTop);
        cell.setPaddingLeft(standardPadding);
        cell.setPaddingRight(standardPadding);
        cell.setPaddingBottom(standardPadding);

        cell.setCellEvent(new HeaderEvent(titreHeader.getContent(), FONT_HEADER_BLOCK, COL_NOIR_HEADER));

        cell.addElement(elementBody);

        table.addCell(cell);
        table.setSpacingAfter(spacingAfter);

        return table;
    }

    private PdfPTable createContent(Chunk containerTitle, ArrayList<PdfPTable> contents)  {
        int containerFontSize = 14;
        Font FONT_TITRE_CONTAINER = FontFactory.getFont(baseFont, containerFontSize, Font.NORMAL, COL_CONTENT_TEXTE);

        PdfPTable masterTable = new PdfPTable(specialTableNbRow);
        masterTable.setPaddingTop(standardPadding);
        masterTable.setWidthPercentage(widthPercentage);
        masterTable.setSplitLate(false);
        masterTable.setSplitRows(true);

        PdfPTable headerTable = new PdfPTable(specialTableNbRow);
        headerTable.setWidthPercentage(widthPercentage);
        headerTable.setTableEvent(new BlueBackgroundEvent(COL_BLEU_FOND));

        PdfPCell cellTitre = new PdfPCell(new Paragraph(new Phrase(containerTitle).getContent(), FONT_TITRE_CONTAINER));
        cellTitre.setBorder(Rectangle.NO_BORDER);
        cellTitre.setBackgroundColor(null);
        cellTitre.setPadding(standardPadding);
        cellTitre.setPaddingBottom(standardPadding);

        headerTable.addCell(cellTitre);

        PdfPCell cellWrapperHeader = new PdfPCell(headerTable);
        cellWrapperHeader.setBorder(Rectangle.NO_BORDER);
        masterTable.addCell(cellWrapperHeader);

        PdfPTable bodyTable = new PdfPTable(specialTableNbRow);
        bodyTable.setWidthPercentage(widthPercentage);
        bodyTable.setKeepTogether(true);
        bodyTable.setPaddingTop(standardPadding);

        for (PdfPTable content : contents) {
            content.setWidthPercentage(widthPercentage);

            PdfPCell wrapperCell = new PdfPCell();
            wrapperCell.setBorder(Rectangle.NO_BORDER);
            wrapperCell.setBackgroundColor(null);

            wrapperCell.setPaddingLeft(standardPadding);
            wrapperCell.setPaddingRight(standardPadding);
            wrapperCell.setPaddingTop(subPadding);

            wrapperCell.addElement(content);

            bodyTable.addCell(wrapperCell);
        }

        PdfPCell cellWrapperBody = new PdfPCell(bodyTable);
        cellWrapperBody.setBorder(Rectangle.NO_BORDER);
        cellWrapperBody.setPadding(NONE);
        masterTable.addCell(cellWrapperBody);

        return masterTable;
    }

    private void ajouterLigneCategorie(PdfPTable table, String label, String[] items, Font font) {
        int colspan = 2;

        PdfPCell cellLabel = new PdfPCell(new Phrase(label, font));
        cellLabel.setBorder(Rectangle.NO_BORDER);
        cellLabel.setBackgroundColor(null);
        cellLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellLabel.setVerticalAlignment(Element.ALIGN_TOP);
        cellLabel.setPaddingRight(subPadding);
        cellLabel.setPaddingTop(NONE);
        table.addCell(cellLabel);

        PdfPCell cellListe = generatePdfPCell(items, font);
        table.addCell(cellListe);

        PdfPCell cellSpace = new PdfPCell();
        cellSpace.setColspan(colspan);
        cellSpace.setBorder(Rectangle.NO_BORDER);
        cellSpace.setFixedHeight(standardPadding);
        table.addCell(cellSpace);
    }

    private PdfPCell generatePdfPCell(String[] items, Font font) {
        float spacing = 2f;

        PdfPCell cellListe = new PdfPCell();
        cellListe.setBorder(Rectangle.NO_BORDER);
        cellListe.setBackgroundColor(null);
        cellListe.setPadding(NONE);

        List list = new List(List.UNORDERED);
        list.setListSymbol("•");
        list.setSymbolIndent(listIndent);
        Chunk bulletSymbol = new Chunk("•", font);
        list.setListSymbol(bulletSymbol);

        for (String item : items) {
            String[] singleLine = item.split("\\r?\\n");

            for (String ligne : singleLine) {
                ListItem li = new ListItem(ligne.trim(), font);
                li.setAlignment(Element.ALIGN_LEFT);
                li.setSpacingAfter(spacing);
                list.add(li);
            }
        }

        cellListe.addElement(list);
        return cellListe;
    }

    private void styleCelluleGrise(PdfPCell cell) {
        float borderWidth = 1f;

        cell.setBackgroundColor(COL_GRIS_CORPS);
        cell.setBorderColor(BaseColor.WHITE);
        cell.setBorderWidth(borderWidth);
        cell.setPadding(subPadding);
    }

    private record BlueBackgroundEvent(BaseColor color) implements PdfPTableEvent {

        @Override
        public void tableLayout(PdfPTable table, float[][] widths, float[] heights, int headerRows, int rowStart, PdfContentByte[] canvases) {
            int start = 0;
            float[] width = widths[start];
            int end = width.length - 1;
            int roundValue = 10;

            float x1 = width[start];
            float x2 = width[end];
            float y1 = heights[start];
            float y2 = heights[end];

            PdfContentByte cb = canvases[PdfPTable.BACKGROUNDCANVAS];
            cb.saveState();
            cb.setColorFill(color);
            cb.roundRectangle(x1, y2, x2 - x1, y1 - y2, roundValue);
            cb.fill();
            cb.restoreState();
        }
    }
}
