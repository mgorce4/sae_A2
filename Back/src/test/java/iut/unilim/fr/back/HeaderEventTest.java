package iut.unilim.fr.back;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import iut.unilim.fr.back.Ressource.HeaderEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HeaderEventTest {

    @Mock
    private PdfPCell cell;
    @Mock
    private Rectangle position;

    @Test
    void testHeaderEvent_RecordProperties() {
        String title = "THIS IS A TITLE";
        Font font = new Font();
        BaseColor color = BaseColor.RED;

        HeaderEvent event = new HeaderEvent(title, font, color);


        assertEquals(title, event.titre());
        assertEquals(font, event.font());
        assertEquals(color, event.fondHeader());
    }

    @Test
    void testCellLayout_DrawsRectangleAndText() throws DocumentException {
        String title = "Test Title";
        Font font = new Font(Font.FontFamily.HELVETICA, 12);
        BaseColor color = BaseColor.BLUE;
        HeaderEvent event = new HeaderEvent(title, font, color);

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, out);
        document.open();

        PdfContentByte realCanvas = writer.getDirectContent();

        PdfContentByte spyCanvas = spy(realCanvas);

        PdfContentByte[] canvases = new PdfContentByte[4];
        canvases[PdfPTable.TEXTCANVAS] = spyCanvas;
        canvases[PdfPTable.BASECANVAS] = spyCanvas;
        canvases[PdfPTable.LINECANVAS] = spyCanvas;
        canvases[PdfPTable.BACKGROUNDCANVAS] = spyCanvas;

        when(position.getTop()).thenReturn(100f);
        when(position.getLeft()).thenReturn(10f);
        when(position.getWidth()).thenReturn(200f);

        event.cellLayout(cell, position, canvases);

        verify(spyCanvas, atLeastOnce()).saveState();
        verify(spyCanvas, atLeastOnce()).restoreState();

        verify(spyCanvas).setColorFill(color);

        verify(spyCanvas).rectangle(eq(10f), eq(65f), eq(200f), eq(35f));
        verify(spyCanvas).fill();

        document.close();
    }
}
