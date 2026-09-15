package doctor.backend.dto.payment;

import java.math.BigDecimal;

public class RazorpayOrderRequest {

    private Long invoiceId;

    // Optional — if not sent, the invoice's outstanding due amount is used.
    private BigDecimal amount;

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
