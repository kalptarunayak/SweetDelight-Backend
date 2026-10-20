# Razorpay Payments & Webhook Setup

1. **Get Test Keys**: Sign up at [dashboard.razorpay.com](https://dashboard.razorpay.com), switch to **Test Mode**, go to **Account & Settings > API Keys**, and click **Generate Test Key**. Copy `Key Id` and `Key Secret`.
2. **Register Webhook**: In Razorpay Dashboard, navigate to **Account & Settings > Webhooks > Add New Webhook**:
   - Webhook URL: `https://<your-render-app>.onrender.com/api/webhooks/razorpay`
   - Secret: Enter a strong random secret and save it as `RAZORPAY_WEBHOOK_SECRET`.
   - Active Events: Check `payment.captured`, `order.paid`, and `payment.failed`. Click **Create Webhook**.
3. **Configure Backend**: Set `RAZORPAY_KEY_ID`, `RAZORPAY_KEY_SECRET`, and `RAZORPAY_WEBHOOK_SECRET` in `.env` / Render environment variables.
4. **Test Payment Flow**:
   - Create an ONLINE order via `POST /api/orders` (status starts as `PENDING_PAYMENT`).
   - Call `POST /api/orders/{id}/payment` to fetch `razorpayOrderId` and `amountPaise`.
   - Complete checkout with Razorpay Standard Checkout in test mode (or test card `4111 1111 1111 1111`, any OTP).
   - Razorpay triggers `/api/webhooks/razorpay` or call `POST /api/payments/verify`. Order transitions to `PLACED`, status timeline updates, and notification is logged.
