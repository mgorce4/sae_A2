package iut.unilim.fr.back.Ressource;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import static iut.unilim.fr.back.controllerBack.LogController.writeInLog;

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

    public HeaderAndFooter(String reference, String department, String ue, String resource) {
        try {
            blackFont = FontFactory.getFont(baseFont, 11, BaseColor.BLACK);
            whiteFont = FontFactory.getFont(baseFont, 11, BaseColor.WHITE);

            this.image = Image.getInstance(imagePath.toAbsolutePath().toString());
            this.image.scaleToFit(100, 50);
            this.reference = reference;
            this.resource = resource;
            this.department = department;
            this.ue = ue;
        } catch (Exception e) {
            writeInLog(e.getMessage());
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
        PdfPTable footerTable = new PdfPTable(2);
        footerTable.setTotalWidth(document.getPageSize().getWidth());
        footerTable.setLockedWidth(true);
        footerTable.setWidths(new float[]{4, 1});

        PdfPCell cellNomUE = new PdfPCell(new Phrase("  " + ue + " - " + resource, whiteFont));
        styleCelluleRouge(cellNomUE);
        cellNomUE.setHorizontalAlignment(Element.ALIGN_LEFT);
        footerTable.addCell(cellNomUE);

        String textPage = "page " + writer.getPageNumber() + "/";

        PdfPCell cellPage = new PdfPCell();
        styleCelluleRouge(cellPage);
        cellPage.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Paragraph p = new Paragraph(textPage, whiteFont);
        p.add(new Chunk(Image.getInstance(totalPagesTemplate), 0, 0));
        p.setAlignment(Element.ALIGN_RIGHT);

        cellPage.addElement(p);
        footerTable.addCell(cellPage);

        footerTable.writeSelectedRows(0, -1, 0, document.bottom() - 10, writer.getDirectContent());
    }

    private void createHeader(PdfWriter writer, Document document) throws DocumentException {
        PdfPTable headerTable = new PdfPTable(3);
        headerTable.setTotalWidth(document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin());
        headerTable.setLockedWidth(true);
        headerTable.setWidths(new float[]{4, 1, 2});

        PdfPCell cellLogoTitre = new PdfPCell();
        cellLogoTitre.setBorder(Rectangle.NO_BORDER);
        cellLogoTitre.setVerticalAlignment(Element.ALIGN_MIDDLE);

        PdfPTable subTable = new PdfPTable(3);
        subTable.setWidths(new float[]{1.5f, 0.1f, 3f});

        PdfPCell unilimLogo = new PdfPCell(image);
        unilimLogo.setBorder(Rectangle.NO_BORDER);
        unilimLogo.setVerticalAlignment(Element.ALIGN_MIDDLE);

        unilimLogo.setHorizontalAlignment(Element.ALIGN_LEFT);
        unilimLogo.setPaddingLeft(0);
        subTable.addCell(unilimLogo);

        PdfPCell cBar = new PdfPCell();
        cBar.setBorder(Rectangle.NO_BORDER);
        cBar.setBorderWidthLeft(2f);
        cBar.setBorderColorLeft(COL_LIGNE_SEPARATRICE);
        cBar.setFixedHeight(30f);
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
        departmentCell.setPaddingTop(10);
        headerTable.addCell(departmentCell);

        float pageTop = document.getPageSize().getHeight();

        headerTable.writeSelectedRows(0, -1, document.left(), pageTop - 15 , writer.getDirectContent());


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

        redHeaderTable.writeSelectedRows(0, -1, 0, pageTop - 70, writer.getDirectContent());
    }

    private void styleCelluleRouge(PdfPCell cell) {
        cell.setBackgroundColor(COL_ROUGE_BARRE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(8);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    }

    @Override
    public void onCloseDocument(PdfWriter writer, Document document) {
        ColumnText.showTextAligned(totalPagesTemplate, Element.ALIGN_LEFT,
                new Phrase(String.valueOf(writer.getPageNumber()), whiteFont),
                2, 2, 0);
    }
}
