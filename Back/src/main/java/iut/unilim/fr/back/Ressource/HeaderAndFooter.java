package iut.unilim.fr.back.Ressource;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import static iut.unilim.fr.back.controllerBack.LogController.writeInPdfLog;

public class HeaderAndFooter extends PdfPageEventHelper {
    private static final BaseColor COL_RED_BAR = new BaseColor(176, 32, 40);
    private static final BaseColor COL_SEPARATOR_LINE = BaseColor.GRAY;

    private static final String baseFont = "src/main/resources/font/trade-gothic-lt-std-58a78e64434a9.otf";
    private static Font whiteFont;
    private static Font blackFont;

    private PdfTemplate totalPagesTemplate;
    Path imagePath = Paths.get("src/main/resources/img/unilim.jpg");
    private Image image;
    private String reference;
    private String department;
    private String ue;
    private String resource;

    private final int initialPadding = 0;
    private final int initialMargin = 0;

    private final float[] footerWidth = new float[]{4, 1};

    private final float[] headerWidth = new float[]{4, 1, 2};
    private final float[] headerSubWidth = new float[]{1.5f, 0.1f, 3f};

    public HeaderAndFooter(String reference, String department, String ue, String resource) {
        try {
            int fontSize = 11;
            int imageWidth = 100;
            int imageHeight = 50;
            
            blackFont = FontFactory.getFont(baseFont, fontSize, BaseColor.BLACK);
            whiteFont = FontFactory.getFont(baseFont, fontSize, BaseColor.WHITE);

            this.image = Image.getInstance(imagePath.toAbsolutePath().toString());
            this.image.scaleToFit(imageWidth, imageHeight);
            this.reference = reference;
            this.resource = resource;
            this.department = department;
            this.ue = ue;
        } catch (Exception e) {
            writeInPdfLog(e.getMessage());
        }
    }

    @Override
    public void onOpenDocument(PdfWriter writer, Document document) {
        totalPagesTemplate = writer.getDirectContent().createTemplate(30, 16);
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        try {
            createHeader(writer, document);
            createFooter(writer, document);

        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }

    }

    private void createFooter(PdfWriter writer, Document document) throws DocumentException {
        int footerNbColumns = 2;
        int footerInitialOffset = 0;
        int footerInitialPosition = 0;
        int footerYPosDif = 10;
        int footerXFinalPosition = -1;

        PdfPTable footerTable = new PdfPTable(footerNbColumns);
        footerTable.setTotalWidth(document.getPageSize().getWidth());
        footerTable.setLockedWidth(true);
        footerTable.setWidths(footerWidth);

        PdfPCell cellNomUE = new PdfPCell(new Phrase("  " + ue + " - " + resource, whiteFont));
        styleCelluleRouge(cellNomUE);
        cellNomUE.setHorizontalAlignment(Element.ALIGN_LEFT);
        footerTable.addCell(cellNomUE);

        String textPage = "page " + writer.getPageNumber() + "/";

        PdfPCell cellPage = new PdfPCell();
        styleCelluleRouge(cellPage);
        cellPage.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Paragraph p = new Paragraph(textPage, whiteFont);
        p.add(new Chunk(Image.getInstance(totalPagesTemplate), footerInitialOffset, footerInitialOffset));
        p.setAlignment(Element.ALIGN_RIGHT);

        cellPage.addElement(p);
        footerTable.addCell(cellPage);

        footerTable.writeSelectedRows(footerInitialPosition, footerXFinalPosition, footerInitialPosition, document.bottom() - footerYPosDif, writer.getDirectContent());
    }

    private void createHeader(PdfWriter writer, Document document) throws DocumentException {
        int headerSubDivYDiff = 70;
        int headerInitialPosition = 0;
        int headerXFinalPosition = -1;
        int headerYDiff = 15;
        int redHeaderNumColumns = 2;
        int headerPaddingTop = 10;
        int headerNbColumns = 3;
        float standardBorderWidth = 2f;
        float headerSubHeight = 30f;

        PdfPTable headerTable = new PdfPTable(headerNbColumns);
        headerTable.setTotalWidth(document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin());
        headerTable.setLockedWidth(true);
        headerTable.setWidths(headerWidth);

        PdfPCell cellLogoTitre = new PdfPCell();
        cellLogoTitre.setBorder(Rectangle.NO_BORDER);
        cellLogoTitre.setVerticalAlignment(Element.ALIGN_MIDDLE);

        PdfPTable subTable = new PdfPTable(headerNbColumns);
        subTable.setWidths(headerSubWidth);

        PdfPCell unilimLogo = new PdfPCell(image);
        unilimLogo.setBorder(Rectangle.NO_BORDER);
        unilimLogo.setVerticalAlignment(Element.ALIGN_MIDDLE);

        unilimLogo.setHorizontalAlignment(Element.ALIGN_LEFT);
        unilimLogo.setPaddingLeft(initialPadding);
        subTable.addCell(unilimLogo);

        PdfPCell cBar = new PdfPCell();
        cBar.setBorder(Rectangle.NO_BORDER);
        cBar.setBorderWidthLeft(standardBorderWidth);
        cBar.setBorderColorLeft(COL_SEPARATOR_LINE);
        cBar.setFixedHeight(headerSubHeight);
        subTable.addCell(cBar);

        PdfPCell resourceSheet = new PdfPCell(new Phrase(" Fiche Ressource", blackFont));
        resourceSheet.setBorder(Rectangle.NO_BORDER);
        resourceSheet.setVerticalAlignment(Element.ALIGN_MIDDLE);
        subTable.addCell(resourceSheet);

        cellLogoTitre.addElement(subTable);
        headerTable.addCell(cellLogoTitre);

        PdfPCell cellVide = new PdfPCell();
        cellVide.setBorder(Rectangle.NO_BORDER);
        headerTable.addCell(cellVide);

        PdfPCell departmentCell = new PdfPCell(new Phrase("Département " + this.department, blackFont));
        departmentCell.setBorder(Rectangle.NO_BORDER);
        departmentCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        departmentCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        departmentCell.setPaddingTop(headerPaddingTop);
        headerTable.addCell(departmentCell);

        float pageTop = document.getPageSize().getHeight();

        headerTable.writeSelectedRows(headerInitialPosition, headerXFinalPosition, document.left(), pageTop - headerYDiff, writer.getDirectContent());


        PdfPTable redHeaderTable = new PdfPTable(redHeaderNumColumns);
        redHeaderTable.setTotalWidth(document.getPageSize().getWidth());
        redHeaderTable.setLockedWidth(true);

        PdfPCell cellRef = new PdfPCell(new Phrase("Référence : " + reference, whiteFont));
        styleCelluleRouge(cellRef);
        cellRef.setHorizontalAlignment(Element.ALIGN_LEFT);
        redHeaderTable.addCell(cellRef);


        String format = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(format);

        String date = sdf.format(new Date());
        PdfPCell cellDate = new PdfPCell(new Phrase("Date de création: " + date, whiteFont));
        styleCelluleRouge(cellDate);
        cellDate.setHorizontalAlignment(Element.ALIGN_RIGHT);
        redHeaderTable.addCell(cellDate);

        redHeaderTable.writeSelectedRows(headerInitialPosition, headerXFinalPosition, headerInitialPosition, pageTop - headerSubDivYDiff, writer.getDirectContent());
    }

    private void styleCelluleRouge(PdfPCell cell) {
        int cellPadding = 8;
        
        cell.setBackgroundColor(COL_RED_BAR);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(cellPadding);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    }

    @Override
    public void onCloseDocument(PdfWriter writer, Document document) {
        int pageNumberPosition = 2;
        int pageNumberRotation = 0;
        
        ColumnText.showTextAligned(totalPagesTemplate, Element.ALIGN_LEFT,
                new Phrase(String.valueOf(writer.getPageNumber()), whiteFont),
                pageNumberPosition, pageNumberPosition, pageNumberRotation);
    }
}
