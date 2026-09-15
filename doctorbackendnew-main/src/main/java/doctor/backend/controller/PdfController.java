package doctor.backend.controller;

import doctor.backend.service.PdfService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pdfs")
@CrossOrigin(origins = "*")
public class PdfController {

    private final PdfService pdfService;

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    // =====================================================
    // GENERATE PDF
    // POST /api/pdfs/generate
    // =====================================================

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generatePdf(
            @RequestParam String title,
            @RequestParam String content) {

        byte[] pdf =
                pdfService.generatePdf(
                        title,
                        content
                );

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=zenve-report.pdf"
                )
                .contentType(
                        MediaType.APPLICATION_PDF
                )
                .body(pdf);
    }
}