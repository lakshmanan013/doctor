// Lazily injects the Razorpay Checkout script (only once) and resolves
// once `window.Razorpay` is ready to use.
let loadPromise = null;

export function loadRazorpayScript() {
  if (window.Razorpay) return Promise.resolve(true);
  if (loadPromise) return loadPromise;

  loadPromise = new Promise((resolve) => {
    const script = document.createElement("script");
    script.src = "https://checkout.razorpay.com/v1/checkout.js";
    script.async = true;
    script.onload = () => resolve(true);
    script.onerror = () => {
      loadPromise = null;
      resolve(false);
    };
    document.body.appendChild(script);
  });

  return loadPromise;
}

// Opens the Razorpay Checkout modal for a previously-created order.
// `order` is the response from createRazorpayOrder():
//   { razorpayOrderId, razorpayKeyId, amountInPaise, currency, invoiceId }
// Resolves with the checkout response on success, rejects on dismiss/failure.
export function openRazorpayCheckout(order, { name, description, prefill = {}, theme } = {}) {
  return new Promise(async (resolve, reject) => {
    const ready = await loadRazorpayScript();
    if (!ready || !window.Razorpay) {
      reject(new Error("Could not load Razorpay checkout. Check your internet connection."));
      return;
    }

    const rzp = new window.Razorpay({
      key: order.razorpayKeyId,
      amount: order.amountInPaise,
      currency: order.currency || "INR",
      order_id: order.razorpayOrderId,
      name: name || "Zenve Veterinary Clinic",
      description: description || "Invoice payment",
      prefill,
      theme: theme || { color: "#1d4ed8" },
      handler: (response) => resolve(response),
      modal: {
        ondismiss: () => reject(new Error("Payment cancelled")),
      },
    });

    rzp.on("payment.failed", (response) => {
      reject(new Error(response?.error?.description || "Payment failed"));
    });

    rzp.open();
  });
}
