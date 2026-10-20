-- Enable btree_gist extension for exclusion constraints on overlapping ranges
CREATE EXTENSION IF NOT EXISTS btree_gist;

-- 1. App Settings Table
CREATE TABLE IF NOT EXISTS app_settings (
    id BIGSERIAL PRIMARY KEY,
    shop_lat DOUBLE PRECISION NOT NULL,
    shop_lng DOUBLE PRECISION NOT NULL,
    delivery_radius_km DOUBLE PRECISION NOT NULL DEFAULT 5.0,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Profiles Table (Synced with Supabase Auth or created on sign-in)
CREATE TABLE IF NOT EXISTS profiles (
    id UUID PRIMARY KEY,
    email VARCHAR(255),
    full_name VARCHAR(255),
    phone VARCHAR(50),
    role VARCHAR(50) NOT NULL DEFAULT 'CUSTOMER',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. Categories Table
CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 4. Products Table
CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    image_url VARCHAR(1000),
    is_in_stock BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 5. Product Variants Table
CREATE TABLE IF NOT EXISTS product_variants (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    title VARCHAR(100) NOT NULL,
    price_inr NUMERIC(10, 2) NOT NULL,
    is_in_stock BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 6. Banners Table
CREATE TABLE IF NOT EXISTS banners (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    subtitle VARCHAR(255),
    image_url VARCHAR(1000),
    discount_percent INT NOT NULL CHECK (discount_percent >= 0 AND discount_percent <= 100),
    coupon_code VARCHAR(50) UNIQUE,
    accent_color VARCHAR(30),
    start_at TIMESTAMP WITH TIME ZONE NOT NULL,
    end_at TIMESTAMP WITH TIME ZONE NOT NULL,
    is_published BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 7. Orders Table
CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(64) NOT NULL UNIQUE,
    customer_id UUID NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    whatsapp_number VARCHAR(30) NOT NULL,
    delivery_address TEXT NOT NULL,
    delivery_lat DOUBLE PRECISION NOT NULL,
    delivery_lng DOUBLE PRECISION NOT NULL,
    delivery_distance_km DOUBLE PRECISION NOT NULL,
    delivery_slot VARCHAR(100) NOT NULL,
    note TEXT,
    payment_method VARCHAR(20) NOT NULL, -- ONLINE, COD
    subtotal_inr NUMERIC(10, 2) NOT NULL,
    discount_inr NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    coupon_code VARCHAR(50),
    total_inr NUMERIC(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL, -- PENDING_PAYMENT, PLACED, CONFIRMED, BAKING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED, PAYMENT_FAILED
    estimated_delivery_time TIMESTAMP WITH TIME ZONE,
    rider_name VARCHAR(100),
    rider_phone VARCHAR(50),
    rider_lat DOUBLE PRECISION,
    rider_lng DOUBLE PRECISION,
    rider_location_updated_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 8. Order Items Table
CREATE TABLE IF NOT EXISTS order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id),
    variant_id BIGINT NOT NULL REFERENCES product_variants(id),
    product_name VARCHAR(255) NOT NULL,
    variant_title VARCHAR(100) NOT NULL,
    unit_price_inr NUMERIC(10, 2) NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    total_price_inr NUMERIC(10, 2) NOT NULL
);

-- 9. Order Status Events Table
CREATE TABLE IF NOT EXISTS order_status_events (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    status VARCHAR(50) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 10. Party Rooms Table
CREATE TABLE IF NOT EXISTS party_rooms (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    capacity INT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 11. Room Bookings Table
CREATE TABLE IF NOT EXISTS room_bookings (
    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL REFERENCES party_rooms(id) ON DELETE RESTRICT,
    customer_id UUID NOT NULL,
    booking_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    guest_count INT NOT NULL CHECK (guest_count > 0),
    contact_name VARCHAR(255) NOT NULL,
    contact_phone VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED, CANCELLED
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_booking_times CHECK (start_time < end_time)
);

-- Overlap exclusion constraint using btree_gist
ALTER TABLE room_bookings
    ADD CONSTRAINT no_overlapping_bookings
    EXCLUDE USING gist (
        room_id WITH =,
        booking_date WITH =,
        tsrange((booking_date + start_time)::timestamp, (booking_date + end_time)::timestamp) WITH &&
    )
    WHERE (status IN ('PENDING', 'APPROVED'));

-- 12. Payments Table
CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    razorpay_order_id VARCHAR(100) NOT NULL UNIQUE,
    razorpay_payment_id VARCHAR(100),
    razorpay_signature VARCHAR(255),
    amount_paise BIGINT NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'INR',
    status VARCHAR(30) NOT NULL, -- CREATED, PAID, FAILED, REFUNDED
    method VARCHAR(50),
    raw_event_json TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category_id);
CREATE INDEX IF NOT EXISTS idx_orders_customer ON orders(customer_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_payments_order ON payments(order_id);
CREATE INDEX IF NOT EXISTS idx_payments_razorpay_order ON payments(razorpay_order_id);
CREATE INDEX IF NOT EXISTS idx_room_bookings_room_date ON room_bookings(room_id, booking_date);
