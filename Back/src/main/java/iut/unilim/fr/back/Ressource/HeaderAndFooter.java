package iut.unilim.fr.back.Ressource;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import static iut.unilim.fr.back.controllerBack.LogController.writeInPdfLog;

public class HeaderAndFooter extends PdfPageEventHelper {
    private static final BaseColor COL_ROUGE_BARRE = new BaseColor(176, 32, 40);
    private static final BaseColor COL_LIGNE_SEPARATRICE = BaseColor.GRAY;

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

    private int imageWidth = 100;
    private int imageHeight = 50;

    private float standardBorderWidth = 2f;
    private int initialPadding = 0;
    private int initialMargin = 0;

    private int footerNbColumns = 2;
    private float[] footerWidth = new float[]{4, 1};
    private int footerInitalOffset = 0;
    private int footerInitialPosition = 0;
    private int footerYPosDif = 10;
    private int footerXFinalPosition = -1;

    private int headerNbColumns = 3;
    private float[] headerWidth = new float[]{4, 1, 2};
    private float[] headerSubWidth = new float[]{1.5f, 0.1f, 3f};
    private float headerSubHeight = 30f;
    private int headerInitialPosition = 0;
    private int headerXFinalPosition = -1;
    private int headerYDiff = 15;
    private int headerSubDivYDiff = 70;
    private int headerPaddingTop = 10;

    private int cellPadding = 8;

    private int pageNumberPosition = 2;
    private int pageNumberRotation = 0;

    public HeaderAndFooter(String reference, String department, String ue, String resource) {
        try {
            blackFont = FontFactory.getFont(baseFont, 11, BaseColor.BLACK);
            whiteFont = FontFactory.getFont(baseFont, 11, BaseColor.WHITE);

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
        p.add(new Chunk(Image.getInstance(totalPagesTemplate), footerInitalOffset, footerInitalOffset));
        p.setAlignment(Element.ALIGN_RIGHT);

        cellPage.addElement(p);
        footerTable.addCell(cellPage);

        footerTable.writeSelectedRows(footerInitialPosition, footerXFinalPosition, footerInitialPosition, document.bottom() - footerYPosDif, writer.getDirectContent());
    }

    private void createHeader(PdfWriter writer, Document document) throws DocumentException {
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
        cBar.setBorderColorLeft(COL_LIGNE_SEPARATRICE);
        cBar.setFixedHeight(headerSubHeight);
        subTable.addCell(cBar);

        PdfPCell ressourceSheet = new PdfPCell(new Phrase(" Fiche Ressource", blackFont));
        ressourceSheet.setBorder(Rectangle.NO_BORDER);
        ressourceSheet.setVerticalAlignment(Element.ALIGN_MIDDLE);
        subTable.addCell(ressourceSheet);

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

        headerTable.writeSelectedRows(headerInitialPosition, headerXFinalPosition, document.left(), pageTop - headerYDiff , writer.getDirectContent());


        PdfPTable redHeaderTable = new PdfPTable(2);
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
        cell.setBackgroundColor(COL_ROUGE_BARRE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(cellPadding);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    }

    @Override
    public void onCloseDocument(PdfWriter writer, Document document) {
        ColumnText.showTextAligned(totalPagesTemplate, Element.ALIGN_LEFT,
                new Phrase(String.valueOf(writer.getPageNumber()), whiteFont),
                pageNumberPosition, pageNumberPosition, pageNumberRotation);
    }

    public static Font getWhiteFont() {return whiteFont;}
    public static void setWhiteFont(Font whiteFont) {HeaderAndFooter.whiteFont = whiteFont;}
    public static Font getBlackFont() {return blackFont;}
    public static void setBlackFont(Font blackFont) {HeaderAndFooter.blackFont = blackFont;}
    public PdfTemplate getTotalPagesTemplate() {return totalPagesTemplate;}
    public void setTotalPagesTemplate(PdfTemplate totalPagesTemplate) {this.totalPagesTemplate = totalPagesTemplate;}
    public Path getImagePath() {return imagePath;}
    public void setImagePath(Path imagePath) {this.imagePath = imagePath;}
    public Image getImage() {return image;}
    public void setImage(Image image) {this.image = image;}
    public String getReference() {return reference;}
    public void setReference(String reference) {this.reference = reference;}
    public String getDepartment() {return department;}
    public void setDepartment(String department) {this.department = department;}
    public String getUe() {return ue;}
    public void setUe(String ue) {this.ue = ue;}
    public String getResource() {return resource;}
    public void setResource(String resource) {this.resource = resource;}
    public int getImageWidth() {return imageWidth;}
    public void setImageWidth(int imageWidth) {this.imageWidth = imageWidth;}
    public int getImageHeight() {return imageHeight;}
    public void setImageHeight(int imageHeight) {this.imageHeight = imageHeight;}
    public float getStandardBorderWidth() {return standardBorderWidth;}
    public void setStandardBorderWidth(float standardBorderWidth) {this.standardBorderWidth = standardBorderWidth;}
    public int getInitialPadding() {return initialPadding;}
    public void setInitialPadding(int initialPadding) {this.initialPadding = initialPadding;}
    public int getInitialMargin() {return initialMargin;}
    public void setInitialMargin(int initialMargin) {this.initialMargin = initialMargin;}
    public int getFooterNbColumns() {return footerNbColumns;}
    public void setFooterNbColumns(int footerNbColumns) {this.footerNbColumns = footerNbColumns;}
    public float[] getFooterWidth() {return footerWidth;}
    public void setFooterWidth(float[] footerWidth) {this.footerWidth = footerWidth;}
    public int getFooterInitalOffset() {return footerInitalOffset;}
    public void setFooterInitalOffset(int footerInitalOffset) {this.footerInitalOffset = footerInitalOffset;}
    public int getFooterInitialPosition() {return footerInitialPosition;}
    public void setFooterInitialPosition(int footerInitialPosition) {this.footerInitialPosition = footerInitialPosition;}
    public int getFooterYPosDif() {return footerYPosDif;}
    public void setFooterYPosDif(int footerYPosDif) {this.footerYPosDif = footerYPosDif;}
    public int getFooterXFinalPosition() {return footerXFinalPosition;}
    public void setFooterXFinalPosition(int footerXFinalPosition) {this.footerXFinalPosition = footerXFinalPosition;}
    public int getHeaderNbColumns() {return headerNbColumns;}
    public void setHeaderNbColumns(int headerNbColumns) {this.headerNbColumns = headerNbColumns;}
    public float[] getHeaderWidth() {return headerWidth;}
    public void setHeaderWidth(float[] headerWidth) {this.headerWidth = headerWidth;}
    public float[] getHeaderSubWidth() {return headerSubWidth;}
    public void setHeaderSubWidth(float[] headerSubWidth) {this.headerSubWidth = headerSubWidth;}
    public float getHeaderSubHeight() {return headerSubHeight;}
    public void setHeaderSubHeight(float headerSubHeight) {this.headerSubHeight = headerSubHeight;}
    public int getHeaderInitialPosition() {return headerInitialPosition;}
    public void setHeaderInitialPosition(int headerInitialPosition) {this.headerInitialPosition = headerInitialPosition;}
    public int getHeaderXFinalPosition() {return headerXFinalPosition;}
    public void setHeaderXFinalPosition(int headerXFinalPosition) {this.headerXFinalPosition = headerXFinalPosition;}
    public int getHeaderYDiff() {return headerYDiff;}
    public void setHeaderYDiff(int headerYDiff) {this.headerYDiff = headerYDiff;}
    public int getHeaderSubDivYDiff() {return headerSubDivYDiff;}
    public void setHeaderSubDivYDiff(int headerSubDivYDiff) {this.headerSubDivYDiff = headerSubDivYDiff;}
    public int getHeaderPaddingTop() {return headerPaddingTop;}
    public void setHeaderPaddingTop(int headerPaddingTop) {this.headerPaddingTop = headerPaddingTop;}
    public int getCellPadding() {return cellPadding;}
    public void setCellPadding(int cellPadding) {this.cellPadding = cellPadding;}
    public int getPageNumberPosition() {return pageNumberPosition;}
    public void setPageNumberPosition(int pageNumberPosition) {this.pageNumberPosition = pageNumberPosition;}
    public int getPageNumberRotation() {return pageNumberRotation;}
    public void setPageNumberRotation(int pageNumberRotation) {this.pageNumberRotation = pageNumberRotation;}
}
