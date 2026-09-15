package doctor.backend.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Chunk;
import com.lowagie.text.pdf.PdfWriter;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfService {

    // =====================================================
    // GENERATE SIMPLE PDF
    // =====================================================

    public byte[] generatePdf(
            String title,
            String content) {

        if (title == null ||
                title.trim().isEmpty()) {

            throw new RuntimeException(
                    "PDF title is required"
            );
        }

        if (content == null) {
            content = "";
        }

        try {

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            Document document =
                    new Document();

            PdfWriter.getInstance(
                    document,
                    outputStream
            );

            document.open();

            // =========================
            // Title
            // =========================

            Font titleFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            20
                    );

            Paragraph titleParagraph =
                    new Paragraph(
                            title,
                            titleFont
                    );

            titleParagraph.setSpacingAfter(20);

            document.add(titleParagraph);

            // =========================
            // Content
            // =========================

            Font contentFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            12
                    );

            Paragraph contentParagraph =
                    new Paragraph(
                            content,
                            contentFont
                    );

            document.add(contentParagraph);

            // =========================
            // Footer
            // =========================

            document.add(
                    Chunk.NEWLINE
            );

            document.add(
                    new Paragraph(
                            "Veterinary Hospital"
                    )
            );

            document.close();

            return outputStream.toByteArray();

        } catch (DocumentException e) {

            throw new RuntimeException(
                    "Failed to generate PDF",
                    e
            );
        }
    }
}