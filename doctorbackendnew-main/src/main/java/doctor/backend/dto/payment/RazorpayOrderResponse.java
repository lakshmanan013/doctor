package doctor.backend.dto.payment;

public class RazorpayOrderResponse {

    private String razorpayOrderId;
    private String razorpayKeyId;
    private Long amountInPaise;
    private String currency;
    private Long invoiceId;

    public RazorpayOrderResponse() {
    }

    public RazorpayOrderResponse(String razorpayOrderId, String razorpayKeyId,
                                  Long amountInPaise, String currency, Long invoiceId) {
        this.razorpayOrderId = razorpayOrderId;
        this.razorpayKeyId = razorpayKeyId;
        this.amountInPaise = amountInPaise;
        this.currency = currency;
        this.invoiceId = invoiceId;
    }

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

    public String getRazorpayKeyId() {
        return razorpayKeyId;
    }

    public void setRazorpayKeyId(String razorpayKeyId) {
        this.razorpayKeyId = razorpayKeyId;
    }

    public Long getAmountInPaise() {
        return amountInPaise;
    }

    public void setAmountInPaise(Long amountInPaise) {
        this.amountInPaise = amountInPaise;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }
}
