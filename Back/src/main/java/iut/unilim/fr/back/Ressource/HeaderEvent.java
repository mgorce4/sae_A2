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

        int leading = 15;
        int topDiffY = 25;
        int subTopDiffY = 8;

        cb.setColorFill(fondHeader);
        cb.rectangle(left, top - headerHeight, width, headerHeight);
        cb.fill();

        ColumnText ct = new ColumnText(cb);
        ct.setSimpleColumn(new Phrase(titre, font),
                left + leading,
                top - topDiffY,
                left + width - leading,
                top - subTopDiffY,
                leading,
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