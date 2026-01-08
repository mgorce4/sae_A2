package iut.unilim.fr.back.Ressource;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import static iut.unilim.fr.back.controllerBack.LogController.writeInPdfLog;

public record HeaderEvent(String titre, Font font, BaseColor fondHeader) implements PdfPCellEvent {

    @Override
    public void cellLayout(PdfPCell cell, Rectangle position, PdfContentByte[] canvases) {
        PdfContentByte cb = canvases[PdfPTable.TEXTCANVAS];
        cb.saveState();

        float headerHeight = 35f;
        float top = position.getTop();
        float left = position.getLeft();
        float width = position.getWidth();

        cb.setColorFill(fondHeader);
        cb.rectangle(left, top - headerHeight, width, headerHeight);
        cb.fill();

        ColumnText ct = new ColumnText(cb);
        ct.setSimpleColumn(new Phrase(titre, font),
                left + 15,
                top - 25,
                left + width - 15,
                top - 8,
                15,
                Element.ALIGN_LEFT
        );
        try {
            ct.go();
        } catch (DocumentException e) {
            writeInPdfLog(e.getMessage());
        }

        cb.restoreState();
    }
}