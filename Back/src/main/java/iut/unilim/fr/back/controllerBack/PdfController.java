package iut.unilim.fr.back.controllerBack;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import iut.unilim.fr.back.Ressource.HeaderAndFooter;
import iut.unilim.fr.back.service.ResourceGetterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static com.itextpdf.text.List.UNORDERED;
import static iut.unilim.fr.back.controllerBack.LogController.writeInPdfLog;

@RestController
@RequestMapping("/api/pdf")
@CrossOrigin(origins = "*")
public class PdfController {
    @Autowired
    private ResourceGetterService res;

    private final BaseColor COL_BLEU_FOND = new BaseColor(59, 66, 117);
    private final BaseColor COL_NOIR_HEADER = new BaseColor(34, 34, 34);
    private final BaseColor COL_GRIS_CORPS = new BaseColor(128, 128, 128);
    private final BaseColor COL_CONTENT_TEXT = BaseColor.WHITE;
    private final BaseColor COL_TEXT = BaseColor.BLACK;
    private final String baseFont = "src/main/resources/font/trade-gothic-lt-std-58a78e64434a9.otf";

    private static final int NONE = 0;
    private final int listIndent = 12;
    private final int widthPercentage = 100;
    private final int standardPadding = 15;
    private final int subPadding = 10;
    private final int specialTableNbRow = 1;

    @GetMapping("/generate")
    public ResponseEntity<Resource> generatePdf(@RequestParam String resourceName, @RequestParam String userName) {
        res.setValuesFromResource(resourceName);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

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

        String resResourceName = res.getResourceName();
        if (!resResourceName.isEmpty()) {
            String pdfFileName = resResourceName + "_ressource_sheet.pdf";
            try {
                Document document = new Document(PageSize.A4, documentMargin, documentMargin, documentMarginTop, documentMarginBottom);
                PdfWriter writer = PdfWriter.getInstance(document, out);

                HeaderAndFooter event = new HeaderAndFooter(res.qualityReference(), res.getDepartment(), res.getRefUE(), res.getLabelResource());
                writer.setPageEvent(event);

                document.open();

                Font font = FontFactory.getFont(baseFont, documentStandardFontSize, COL_TEXT);
                Font contentFont = FontFactory.getFont(baseFont, documentStandardFontSize, COL_CONTENT_TEXT);
                Font fontTitle = FontFactory.getFont(baseFont, documentTitleFontSize, COL_TEXT);

                PdfPTable table = createPdfPTable(res, font, fontTitle);

                // Premiere div
                Chunk descriptive = new Chunk("Descriptif", contentFont);
                Chunk objective = new Chunk("Objectif", contentFont);
                Chunk objectiveContent = new Chunk(res.getObjectiveContent(), contentFont);
                PdfPTable content = createContentDiv(objective, objectiveContent);

                ArrayList<PdfPTable> contents = new ArrayList<>();
                contents.add(content);

                // Div competence
                Chunk competenceTitle = new Chunk("Compétence", contentFont);

                ArrayList<String> competences = (ArrayList<String>) res.getSkills();
                com.itextpdf.text.List bulletedList = new com.itextpdf.text.List(UNORDERED);


                for (String competence : competences) {
                    bulletedList.setListSymbol("•");
                    bulletedList.setSymbolIndent(listIndent);

                    bulletedList.add(new ListItem(new Chunk(competence, contentFont)));
                }


                PdfPTable contentCompetence = createContentDiv(competenceTitle, bulletedList);
                contents.add(contentCompetence);

                // Div SAE
                Chunk saeConcerned = new Chunk("SAÉ Concernée", contentFont);
                ArrayList<String> saes = (ArrayList<String>) res.getSaes();

                createITextList(contentFont, contents, saeConcerned, saes);

                // Div keywords
                Chunk motsCleTitre = new Chunk("Mots Clé", contentFont);
                ArrayList<String> keyWords = (ArrayList<String>) res.getKeyWords();
                StringBuilder keyWord = new StringBuilder(keyWords.getFirst());
                for  (int index=keyWordStartIndex; index<keyWords.size(); index++) {
                    keyWord.append(", ").append(keyWords.get(index));
                }


                Chunk motsCleChunk = new Chunk(keyWord.toString(), contentFont);

                PdfPTable motCleContent = createContentDiv(motsCleTitre, motsCleChunk);
                contents.add(motCleContent);


                Chunk modality = new Chunk("Modalité", contentFont);
                ArrayList<String> modalities = (ArrayList<String>) res.getModalities();

                createITextList(contentFont, contents, modality, modalities);

                document.add(table);

                // hours repartition
                // ------------------------------------------------------------------------------
                boolean isAlternance = res.isAlternance();
                Chunk hoursRepartition = new Chunk("Répartition des heures par élève:", contentFont);
                ArrayList<PdfPTable> hours = new ArrayList<>();

                System.out.println(isAlternance);

                if (isAlternance) {
                    Chunk internshipRepartition = new Chunk("Répartition des heures par élève en alternance :");
                    PdfPTable internshipProgramTable = new PdfPTable(repartitionProgramNbColumn);
                    internshipProgramTable.setWidthPercentage(widthPercentage);

                    ArrayList<Chunk> programmeContent = completeInternshipProgramContent(res, contentFont);

                    applyGreyStyleOnCell(programmeContent, contentFont, internshipProgramTable, internshipRepartition, hours);

                }
                
                PdfPTable repartitionProgrammeTable = new PdfPTable(repartitionProgramNbColumn);
                repartitionProgrammeTable.setWidthPercentage(widthPercentage);

                ArrayList<Chunk> programmeContent = completeProgramContent(res, contentFont);

                PdfPTable repartitionContent = applyGreyStyleOnCell(programmeContent, contentFont, repartitionProgrammeTable, hoursRepartition, hours);
                repartitionContent.setPaddingTop(standardPadding);

                contents.addAll(hours);

                // ------------------------------------------------------------------------------
                
                Chunk pedagogicalContent = new Chunk("Contenue pédagogique", contentFont);
                PdfPTable pedagoTable = new PdfPTable(pedagoTableNbColumn);
                pedagoTable.setWidthPercentage(widthPercentage);
                pedagoTable.setWidths(pedagoTableWidth);

                String pedagoContentDs = res.getPedagoContentDs();
                String pedagoContentCm = res.getPedagoContentCm();
                String pedagoContentTd = res.getPedagoContentTd();
                String pedagoContentTp = res.getPedagoContentTp();

                String[] itemsDs = pedagoContentDs.split(pedagoContentSplitDelimitator);
                addCategoryLine(pedagoTable, "DS", itemsDs, contentFont);

                String[] itemsCM = pedagoContentCm.split(pedagoContentSplitDelimitator);
                addCategoryLine(pedagoTable, "CM", itemsCM, contentFont);

                String[] itemsTD = pedagoContentTd.split(pedagoContentSplitDelimitator);
                addCategoryLine(pedagoTable, "TD", itemsTD, contentFont);

                String[] itemsTP = pedagoContentTp.split(pedagoContentSplitDelimitator);
                addCategoryLine(pedagoTable, "TP", itemsTP, contentFont);

                PdfPTable contentPedagoContent = createContentDiv(pedagogicalContent, pedagoTable);
                contents.add(contentPedagoContent);

                PdfPTable contentDiv = createContent(descriptive, contents);
                document.add(contentDiv);
                document.newPage();
                ArrayList<PdfPTable> resourceContentsTracking = new ArrayList<>();

                Chunk resourceTracking = new Chunk("Suivi de la Ressource/Module", contentFont);
                Chunk pedagogicalTeamFeedback = new Chunk("Retour de l'équipe pédagogique", contentFont);
                Chunk pedagogicalTeamFeedbackContent = new Chunk(res.getPedagoTeamFeedback(), contentFont);

                resourceContentsTracking.add(createContentDiv(pedagogicalTeamFeedback, pedagogicalTeamFeedbackContent));

                Chunk studentFeedback = new Chunk("Retour étudiant", contentFont);
                Chunk studentFeedbackContent = new Chunk(res.getStudentFeedback(), contentFont);

                resourceContentsTracking.add(createContentDiv(studentFeedback, studentFeedbackContent));

                Chunk amelioration = new Chunk("Améliorations à mettre en oeuvre", contentFont);
                Chunk ameliorationContent = new Chunk(res.getImprovements(), contentFont);

                resourceContentsTracking.add(createContentDiv(amelioration, ameliorationContent));

                PdfPTable resourceTrackingTable = createContent(resourceTracking, resourceContentsTracking);
                resourceTrackingTable.setPaddingTop(standardPadding);

                document.add(resourceTrackingTable);
                document.close();

                writeInPdfLog(userName + " create a pdf for resource sheet: " + pdfFileName);
            } catch (Exception e) {
                writeInPdfLog(e.getMessage());
                return ResponseEntity.internalServerError().build();
            }
            byte[] pdfBytes = out.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + pdfFileName + ".pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);
        } else {
            writeInPdfLog( userName + " attempt to generate a resource sheet pdf, but no matches found for the resource name");
            return ResponseEntity.internalServerError().build();
        }
    }

    private PdfPTable applyGreyStyleOnCell(ArrayList<Chunk> programmeContent, Font contentFont, PdfPTable repartitionProgrammeTable, Chunk hoursRepartition, ArrayList<PdfPTable> hours) {
        for (Chunk chunk : programmeContent) {
            PdfPCell cell = new PdfPCell(new Phrase(chunk.getContent(), contentFont));

            greyCellStyle(cell);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

            repartitionProgrammeTable.addCell(cell);
        }

        PdfPTable repartitionContent = createContentDiv(hoursRepartition, repartitionProgrammeTable);
        hours.add(repartitionContent);
        return repartitionContent;
    }

    private ArrayList<Chunk> completeInternshipProgramContent(ResourceGetterService res, Font contentFont) {
        ArrayList<Chunk> programmeContent = completeInternshipNationalProgram(res, contentFont);

        // Actual :
        Chunk programme = new Chunk("Votre programme - Alternance", contentFont);
        programmeContent.add(programme);
        ArrayList<Double> hours = (ArrayList<Double>) res.getHoursStudentInternship();
        for (Double h : hours) {
            Chunk chunk = new Chunk(h.toString(), contentFont);
            programmeContent.add(chunk);
        }
        return programmeContent;
    }

    private ArrayList<Chunk> completeInternshipNationalProgram(ResourceGetterService res, Font contentFont) {
        ArrayList<Chunk> programmeContent = initializeProgramContent(contentFont);

        Chunk pn = new Chunk("Programme Nationnal - Alternance", contentFont);
        programmeContent.add(pn);

        ArrayList<Double> pnContent = (ArrayList<Double>) res.getHoursPNInternship();
        for (Double h : pnContent) {
            Chunk chunk = new Chunk(h.toString(), contentFont);
            programmeContent.add(chunk);
        }
        return programmeContent;
    }

    private static ArrayList<Chunk> initializeProgramContent(Font contentFont) {
        Chunk resource = new Chunk("Ressource", contentFont);
        Chunk cm = new Chunk("CM", contentFont);
        Chunk td = new Chunk("TD", contentFont);
        Chunk tp = new Chunk("TP", contentFont);
        Chunk total = new Chunk("Total", contentFont);

        ArrayList<Chunk> programContent = new ArrayList<>();
        programContent.add(resource);
        programContent.add(cm);
        programContent.add(td);
        programContent.add(tp);
        programContent.add(total);
        return programContent;
    }

    private static ArrayList<Chunk> completeNationalProgram(ResourceGetterService res, Font contentFont) {
        ArrayList<Chunk> programmeContent = initializeProgramContent(contentFont);

        Chunk pn = new Chunk("Programme Nationnal", contentFont);
        programmeContent.add(pn);

        ArrayList<Double> pnContent = (ArrayList<Double>) res.getHoursPN();
        for (Double h : pnContent) {
            Chunk chunk = new Chunk(h.toString(), contentFont);
            programmeContent.add(chunk);
        }
        return programmeContent;
    }

    private ArrayList<Chunk> completeProgramContent(ResourceGetterService res, Font contentFont) {
        ArrayList<Chunk> programmeContent = completeNationalProgram(res, contentFont);

        // Actual :
        Chunk programme = new Chunk("Votre programme", contentFont);
        programmeContent.add(programme);
        ArrayList<Double> hours = (ArrayList<Double>) res.getHoursStudent();
        for (Double h : hours) {
            Chunk chunk = new Chunk(h.toString(), contentFont);
            programmeContent.add(chunk);
        }
        return programmeContent;
    }

    private PdfPTable createPdfPTable(ResourceGetterService res, Font font, Font fontTitle) {
        int numColumns = 3;
        int standardLeading = 0;
        int refUeMultipliedLeading = 2;
        float refResourceStandardLeading = 10f;

        PdfPTable table = new PdfPTable(numColumns);
        table.setWidthPercentage(widthPercentage);

        // Resource title
        PdfPCell cellLeft = new PdfPCell();
        cellLeft.setBorder(Rectangle.NO_BORDER);


        Paragraph refUE = new Paragraph(res.getRefUE(), font);
        Paragraph resourceRef = new Paragraph(res.getRef(), font);

        Paragraph resourceTitle = new Paragraph(res.getLabelResource(), fontTitle);
        Paragraph profReferent = new Paragraph(res.getProfRef(), font);

        refUE.setLeading(standardLeading, refUeMultipliedLeading);
        resourceRef.setLeading(refResourceStandardLeading, standardLeading);

        cellLeft.addElement(refUE);
        cellLeft.addElement(resourceRef);
        cellLeft.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellLeft.setPaddingBottom(standardPadding);
        table.addCell(cellLeft);

        PdfPCell cellCenter = new PdfPCell(resourceTitle);
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
        for (String u_modality : items) {
            modaliteList.add(new ListItem(new Chunk(u_modality, contentFont)));
        }

        PdfPTable modalityContent = createContentDiv(title, modaliteList);
        contents.add(modalityContent);
    }

    public PdfPTable createContentDiv(Chunk titreHeader, Element elementBody) {
        int headerFontSize = 12;
        int cellPaddingTop = 45;
        float spacingAfter = 10f;
        Font FONT_HEADER_BLOCK = FontFactory.getFont(baseFont, headerFontSize, Font.BOLD, COL_CONTENT_TEXT);

        PdfPTable table = new PdfPTable(specialTableNbRow);
        table.setWidthPercentage(widthPercentage);
        table.setSplitLate(true);
        table.setSplitRows(true);

        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);


        cell.setPaddingTop(cellPaddingTop);
        cell.setPaddingLeft(standardPadding);
        cell.setPaddingRight(standardPadding);
        cell.setPaddingBottom(standardPadding);

        cell.setCellEvent(new GreyHeaderEvent(titreHeader.getContent(), FONT_HEADER_BLOCK, COL_NOIR_HEADER, COL_CONTENT_TEXT, COL_GRIS_CORPS));

        cell.addElement(elementBody);

        table.addCell(cell);
        table.setSpacingAfter(spacingAfter);

        return table;
    }

    private PdfPTable createContent(Chunk containerTitle, ArrayList<PdfPTable> contents)  {
        int containerFontSize = 14;
        Font FONT_TITRE_CONTAINER = FontFactory.getFont(baseFont, containerFontSize, Font.NORMAL, COL_CONTENT_TEXT);

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

    private void addCategoryLine(PdfPTable table, String label, String[] items, Font font) {
        int colspan = 2;

        PdfPCell cellLabel = new PdfPCell(new Phrase(label, font));
        cellLabel.setBorder(Rectangle.NO_BORDER);
        cellLabel.setBackgroundColor(null);
        cellLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellLabel.setVerticalAlignment(Element.ALIGN_TOP);
        cellLabel.setPaddingRight(subPadding);
        cellLabel.setPaddingTop(NONE);
        table.addCell(cellLabel);

        PdfPCell cellList = generatePdfPCell(items, font);
        table.addCell(cellList);

        PdfPCell cellSpace = new PdfPCell();
        cellSpace.setColspan(colspan);
        cellSpace.setBorder(Rectangle.NO_BORDER);
        cellSpace.setFixedHeight(standardPadding);
        table.addCell(cellSpace);
    }

    private PdfPCell generatePdfPCell(String[] items, Font font) {
        float spacing = 2f;

        PdfPCell cellList = new PdfPCell();
        cellList.setBorder(Rectangle.NO_BORDER);
        cellList.setBackgroundColor(null);
        cellList.setPadding(NONE);

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

        cellList.addElement(list);
        return cellList;
    }

    private void greyCellStyle(PdfPCell cell) {
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


    private record GreyHeaderEvent(String title, Font font, BaseColor headerColor, BaseColor textColor, BaseColor bodyColor) implements PdfPCellEvent {
        @Override
        public void cellLayout(PdfPCell cell, Rectangle position, PdfContentByte[] canvases) {
            PdfContentByte canvas = canvases[PdfPTable.LINECANVAS];

            float headerHeight = 40f;
            float radius = 10f;
            float paddingLeft = 10f;
            int policeValue = 3;
            int textPositionDivisionValue = 2;

            float x = position.getLeft();
            float width = position.getWidth();
            float yMiddle = position.getTop() - headerHeight;
            float yBottom = position.getBottom();
            float textY = yMiddle + (headerHeight / textPositionDivisionValue) - policeValue;

            canvas.saveState();
            canvas.setColorFill(bodyColor);

            float bodyHeight = yMiddle - yBottom;
            if (bodyHeight > NONE) {
                canvas.roundRectangle(x, yBottom, width, bodyHeight, radius);

                canvas.rectangle(x, yMiddle - radius, width, radius);

                canvas.fill();
            }

            canvas.restoreState();
            canvas.saveState();
            canvas.setColorFill(headerColor);
            canvas.roundRectangle(x, yMiddle, width, headerHeight, radius);
            canvas.rectangle(x, yMiddle, width, radius);

            canvas.fill();
            canvas.restoreState();
            ColumnText.showTextAligned(canvas,
                    Element.ALIGN_LEFT,
                    new Phrase(title, font),
                    x + paddingLeft,
                    textY,
                    NONE);
            }
        }
    }
