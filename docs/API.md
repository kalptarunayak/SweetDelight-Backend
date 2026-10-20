# SweetDelight API Reference

### Authentication & Swagger Authorize (How to test)
1. Sign in via Supabase Auth or call `supabase.auth.signInWithPassword()` to obtain your `access_token` JWT.
2. For local testing, pass `test-admin-token` for full ADMIN role or `test-customer-token` for CUSTOMER role.
3. Navigate to `http://localhost:8080/swagger-ui.html` in your browser.
4. Click the green **Authorize** button located in the top-right corner.
5. Enter your Supabase JWT or test token into the **Value** box and click **Authorize**.
6. All protected Customer and Admin endpoints are now authorized for interactive testing.

---

## Catalog (Public)

### 1. `GET /api/ping`
Wake-up ping for frontend client.
```bash
curl http://localhost:8080/api/ping
```
Response:
```json
{ "status": "ok" }
```

### 2. `GET /api/categories`
List all product categories.
```bash
curl http://localhost:8080/api/categories
```
Response:
```json
[
  { "id": 1, "name": "Cakes", "slug": "cakes", "description": "Freshly baked artisanal cakes" },
  { "id": 2, "name": "Chocolates", "slug": "chocolates", "description": "Handcrafted bonbons and truffles" }
]
```

### 3. `GET /api/products`
List all products with variants. Supports optional `?categoryId=1&inStockOnly=true`.
```bash
curl http://localhost:8080/api/products?categoryId=1
```
Response:
```json
[
  {
    "id": 1,
    "name": "Belgian Chocolate Truffle Cake",
    "description": "Rich 54% dark chocolate ganache layered with moist sponge.",
    "imageUrl": "https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=800",
    "categoryId": 1,
    "categoryName": "Cakes",
    "inStock": true,
    "variants": [
      { "id": 1, "title": "500g", "priceInr": 699.00, "inStock": true },
      { "id": 2, "title": "1kg", "priceInr": 1299.00, "inStock": true }
    ]
  }
]
```

### 4. `GET /api/products/{id}`
Retrieve details of a single product.
```bash
curl http://localhost:8080/api/products/1
```

---

## Delivery Radius (Public)

### 5. `POST /api/delivery/check`
Calculates Haversine distance from the bakery shop and checks if address is within delivery radius.
```bash
curl -X POST http://localhost:8080/api/delivery/check \
  -H "Content-Type: application/json" \
  -d '{"lat": 19.0620, "lng": 72.8350}'
```
Response:
```json
{
  "deliverable": true,
  "distanceKm": 0.64,
  "radiusKm": 5.0,
  "shopLat": 19.0596,
  "shopLng": 72.8295,
  "message": "Delivery available! Your location is 0.64 km away (within our 5.0 km radius)."
}
```

---

## Banners & Coupons (Public)

### 6. `GET /api/banners`
Retrieves currently active and published promotional banners.
```bash
curl http://localhost:8080/api/banners
```
Response:
```json
[
  {
    "id": 1,
    "title": "Grand Celebration Treat 🍰",
    "subtitle": "Enjoy flat 15% discount on all cakes and chocolates!",
    "imageUrl": "https://images.unsplash.com/photo-1535141192574-5d4897c13136?w=1200",
    "discountPercent": 15,
    "couponCode": "SWEET15",
    "accentColor": "#E11D48",
    "startAt": "2026-10-19T00:00:00Z",
    "endAt": "2027-01-19T00:00:00Z",
    "published": true
  }
]
```

---

## Orders (Customer - Protected)

### 7. `POST /api/orders`
Place a new order. Server recomputes all prices from database and rejects with HTTP 422 if location is outside delivery radius.
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Aarav Sharma",
    "whatsappNumber": "+919876543210",
    "deliveryAddress": "Flat 402, Sea Green Apts, Hill Road, Bandra West, Mumbai",
    "deliveryLat": 19.0620,
    "deliveryLng": 72.8350,
    "deliverySlot": "Today 5:00 PM - 7:00 PM",
    "note": "Please write Happy Birthday Anita",
    "paymentMethod": "ONLINE",
    "couponCode": "SWEET15",
    "items": [
      { "variantId": 1, "quantity": 1 }
    ]
  }'
```
Response (HTTP 201 Created):
```json
{
  "id": 1,
  "orderNumber": "ORD-20261020-584912",
  "customerName": "Aarav Sharma",
  "whatsappNumber": "+919876543210",
  "subtotalInr": 699.00,
  "discountInr": 104.85,
  "couponCode": "SWEET15",
  "totalInr": 594.15,
  "status": "PENDING_PAYMENT",
  "items": [
    { "variantId": 1, "productName": "Belgian Chocolate Truffle Cake", "variantTitle": "500g", "unitPriceInr": 699.00, "quantity": 1, "totalPriceInr": 699.00 }
  ]
}
```

### 8. `GET /api/orders`
List past and active orders for the authenticated customer.
```bash
curl http://localhost:8080/api/orders -H "Authorization: Bearer <TOKEN>"
```

### 9. `GET /api/orders/{id}`
Retrieve detailed order summary.
```bash
curl http://localhost:8080/api/orders/1 -H "Authorization: Bearer <TOKEN>"
```

---

## Payments (Razorpay)

### 10. `POST /api/orders/{id}/payment`
Initializes or reuses the Razorpay order for the server-computed total in paise.
```bash
curl -X POST http://localhost:8080/api/orders/1/payment \
  -H "Authorization: Bearer <TOKEN>"
```
Response:
```json
{
  "razorpayOrderId": "order_O8xTest998877",
  "amountPaise": 59415,
  "currency": "INR",
  "keyId": "rzp_test_placeholder"
}
```

### 11. `POST /api/payments/verify`
Verifies Razorpay HMAC-SHA256 signature in constant time. Transitions order to `PLACED` and triggers customer + admin notifications.
```bash
curl -X POST http://localhost:8080/api/payments/verify \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "razorpay_order_id": "order_O8xTest998877",
    "razorpay_payment_id": "pay_O8yTest112233",
    "razorpay_signature": "4a58b88ef11612e61623910901e91280b27137a8581e7d0577fc761823ebaf82"
  }'
```

### 12. `POST /api/webhooks/razorpay` (Public)
Webhook source of truth. Handles `payment.captured`, `order.paid`, and `payment.failed` idempotently.
```bash
curl -X POST http://localhost:8080/api/webhooks/razorpay \
  -H "X-Razorpay-Signature: <HMAC_SHA256_HEX>" \
  -H "Content-Type: application/json" \
  -d '{"event":"payment.captured","payload":{"payment":{"entity":{"id":"pay_123","order_id":"order_O8xTest998877","amount":59415,"status":"captured"}}}}'
```

---

## Order Tracking & Rider Location (Public / Customer)

### 13. `GET /api/orders/{id}/tracking`
Track order status timeline, ETA, and live rider coordinates.
```bash
curl http://localhost:8080/api/orders/1/tracking
```
Response:
```json
{
  "orderId": 1,
  "orderNumber": "ORD-20261020-584912",
  "status": "OUT_FOR_DELIVERY",
  "estimatedDeliveryTime": "2026-10-20T18:30:00Z",
  "riderName": "Vikram Singh",
  "riderPhone": "+919876543299",
  "riderLat": 19.0605,
  "riderLng": 72.8310,
  "timeline": [
    { "status": "PLACED", "notes": "Order placed successfully", "createdAt": "2026-10-20T17:00:00Z" },
    { "status": "BAKING", "notes": "Cake is baking in the oven", "createdAt": "2026-10-20T17:20:00Z" },
    { "status": "OUT_FOR_DELIVERY", "notes": "Dispatched with rider Vikram", "createdAt": "2026-10-20T18:00:00Z" }
  ]
}
```

---

## Party Room Booking (Cake Cutting)

### 14. `GET /api/party-rooms`
List active celebration rooms.
```bash
curl http://localhost:8080/api/party-rooms
```

### 15. `POST /api/party-rooms/bookings`
Reserve a room for cake cutting. Rejects overlapping slots with HTTP 409 Conflict.
```bash
curl -X POST http://localhost:8080/api/party-rooms/bookings \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": 1,
    "bookingDate": "2026-11-20",
    "startTime": "16:00:00",
    "endTime": "18:00:00",
    "guestCount": 12,
    "contactName": "Priya Kapoor",
    "contactPhone": "+919876543212",
    "notes": "1st birthday cake cutting celebration"
  }'
```

### 16. `GET /api/party-rooms/bookings/my`
List bookings made by the logged-in customer.
```bash
curl http://localhost:8080/api/party-rooms/bookings/my -H "Authorization: Bearer <TOKEN>"
```

### 17. `GET /api/admin/party-rooms/bookings` (Admin)
List all room bookings across store.
```bash
curl http://localhost:8080/api/admin/party-rooms/bookings -H "Authorization: Bearer <ADMIN_TOKEN>"
```

### 18. `PUT /api/admin/party-rooms/bookings/{id}/decision` (Admin)
Approve or reject a booking and notify the customer.
```bash
curl -X PUT http://localhost:8080/api/admin/party-rooms/bookings/1/decision \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"status": "APPROVED", "notes": "Looking forward to hosting you!"}'
```

---

## Admin Endpoints (Requires ADMIN Role)

### 19. `GET /api/admin/settings` & `PUT /api/admin/settings/radius`
Retrieve or update delivery radius and shop coordinates.
```bash
curl -X PUT http://localhost:8080/api/admin/settings/radius \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"deliveryRadiusKm": 8.0}'
```

### 20. `GET /api/admin/orders`
Filter store orders by status (`?status=PLACED`).
```bash
curl http://localhost:8080/api/admin/orders?status=PLACED -H "Authorization: Bearer <ADMIN_TOKEN>"
```

### 21. `PUT /api/admin/orders/{id}/status`
Update status, ETA, and rider info.
```bash
curl -X PUT http://localhost:8080/api/admin/orders/1/status \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "OUT_FOR_DELIVERY",
    "estimatedDeliveryTime": "2026-10-20T18:45:00Z",
    "riderName": "Vikram Singh",
    "riderPhone": "+919876543299",
    "notes": "Dispatched fresh from oven"
  }'
```

### 22. `PUT /api/admin/orders/{id}/location`
Update rider's live coordinates.
```bash
curl -X PUT http://localhost:8080/api/admin/orders/1/location \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"lat": 19.0610, "lng": 72.8320}'
```

### 23. `POST /api/admin/orders/{id}/refund`
Processes full Razorpay refund, marks payment REFUNDED, and cancels order.
```bash
curl -X POST http://localhost:8080/api/admin/orders/1/refund \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```

### 24. Admin Product CRUD
- `POST /api/admin/products`: Create product with variants.
- `PUT /api/admin/products/{id}`: Update product and variants.
- `DELETE /api/admin/products/{id}`: Delete product.
- `PUT /api/admin/products/{id}/stock`: Toggle stock.

### 25. Admin Banner CRUD
- `GET /api/admin/banners`: List all banners.
- `POST /api/admin/banners`: Create promotional banner.
- `PUT /api/admin/banners/{id}`: Update banner.
- `DELETE /api/admin/banners/{id}`: Delete banner.
