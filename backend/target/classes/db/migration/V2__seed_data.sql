-- Default App Settings (Shop location: Mumbai, India; 5.0 km delivery radius)
INSERT INTO app_settings (id, shop_lat, shop_lng, delivery_radius_km, updated_at)
VALUES (1, 19.0596, 72.8295, 5.0, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Seed Admin and Customer Profiles for Swagger testing
-- Admin User: sub=00000000-0000-0000-0000-000000000001
INSERT INTO profiles (id, email, full_name, phone, role, created_at, updated_at)
VALUES 
('00000000-0000-0000-0000-000000000001', 'admin@sweetdelight.com', 'Shop Administrator', '+919876543210', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('00000000-0000-0000-0000-000000000002', 'customer@example.com', 'Aarav Sharma', '+919876543211', 'CUSTOMER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Categories
INSERT INTO categories (id, name, slug, description)
VALUES 
(1, 'Cakes', 'cakes', 'Freshly baked artisanal cakes, celebration cakes, and eggless gourmet creations.'),
(2, 'Chocolates', 'chocolates', 'Handcrafted truffles, luxury bonbons, single-origin bars, and gift boxes.')
ON CONFLICT (id) DO NOTHING;

-- 6 Cakes
INSERT INTO products (id, category_id, name, description, image_url, is_in_stock)
VALUES
(1, 1, 'Belgian Chocolate Truffle Cake', 'Rich 54% dark chocolate ganache layered with moist chocolate sponge and cocoa glaze.', 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=800', true),
(2, 1, 'Classic Red Velvet Cake', 'Velvety crimson layers filled with silky Madagascar cream cheese frosting.', 'https://images.unsplash.com/photo-1616541823729-00fe0aacd32c?w=800', true),
(3, 1, 'Fresh Alphonso Mango Cake', 'Summer delicacy layered with fresh Ratnagiri mango compote and light vanilla cream.', 'https://images.unsplash.com/photo-1535141192574-5d4897c13136?w=800', true),
(4, 1, 'Black Forest Gateau', 'Classic German black forest cake with sour cherries, kirsch essence, and chocolate shavings.', 'https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=800', true),
(5, 1, 'Butterscotch Crunch Cake', 'Soft caramel sponge dressed in praline butterscotch crunch and golden butter glaze.', 'https://images.unsplash.com/photo-1588195538326-c5b1e9f80a1b?w=800', true),
(6, 1, 'Royal Rasmalai Fusion Cake', 'Cardamom-infused sponge soaked in rich saffron milk, topped with tender rasmalai and pistachios.', 'https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=800', true)
ON CONFLICT (id) DO NOTHING;

-- Cake Variants
INSERT INTO product_variants (product_id, title, price_inr, is_in_stock)
VALUES
(1, '500g', 699.00, true),
(1, '1kg', 1299.00, true),
(2, '500g', 749.00, true),
(2, '1kg', 1399.00, true),
(3, '500g', 799.00, true),
(3, '1kg', 1499.00, true),
(4, '500g', 649.00, true),
(4, '1kg', 1199.00, true),
(5, '500g', 599.00, true),
(5, '1kg', 1099.00, true),
(6, '500g', 849.00, true),
(6, '1kg', 1599.00, true);

-- 6 Chocolates
INSERT INTO products (id, category_id, name, description, image_url, is_in_stock)
VALUES
(7, 2, 'Artisanal Dark Chocolate Hazelnut Rocks', 'Crunchy roasted Piedmont hazelnuts enrobed in velvety 70% dark Belgian chocolate.', 'https://images.unsplash.com/photo-1549007994-cb92caebd54b?w=800', true),
(8, 2, 'Roasted Almond Dark Chocolate Bar', 'Single-origin dark chocolate blended with whole roasted Californian almonds.', 'https://images.unsplash.com/photo-1606312619070-d48b4c652a52?w=800', true),
(9, 2, 'Sea Salt Caramel Bonbons', 'Glossy hand-painted chocolate domes filled with molten Brittany salted butter caramel.', 'https://images.unsplash.com/photo-1541781774459-bb2af2f05b55?w=800', true),
(10, 2, 'Belgian Milk Chocolate Truffles', 'Ultra-smooth whipped milk chocolate centers dusted with French cocoa powder.', 'https://images.unsplash.com/photo-1548741487-18d16a14b580?w=800', true),
(11, 2, 'Berry Bliss White Chocolate Bark', 'Creamy cocoa butter white chocolate studded with freeze-dried raspberries and cranberries.', 'https://images.unsplash.com/photo-1511381939415-e44015466834?w=800', true),
(12, 2, 'Gourmet Pistachio Ganache Pralines', 'Crushed Iranian pistachios infused in rich white chocolate ganache and encased in dark shells.', 'https://images.unsplash.com/photo-1575372587186-500b396e95c7?w=800', true)
ON CONFLICT (id) DO NOTHING;

-- Chocolate Variants
INSERT INTO product_variants (product_id, title, price_inr, is_in_stock)
VALUES
(7, 'Box of 6 (150g)', 450.00, true),
(7, 'Box of 12 (300g)', 850.00, true),
(8, '100g Bar', 299.00, true),
(8, 'Pack of 3 Bars', 799.00, true),
(9, 'Box of 9 pcs', 599.00, true),
(9, 'Box of 16 pcs', 999.00, true),
(10, 'Box of 8 pcs', 499.00, true),
(10, 'Box of 16 pcs', 899.00, true),
(11, '150g Slab', 399.00, true),
(11, '300g Gift Box', 749.00, true),
(12, 'Box of 6 pcs', 549.00, true),
(12, 'Box of 12 pcs', 999.00, true);

-- 1 Party Room (for Cake Cutting)
INSERT INTO party_rooms (id, name, description, capacity, is_active)
VALUES
(1, 'The Sweet Celebration Lounge', 'A beautifully decorated private lounge designed for cake cutting celebrations, birthday memories, and anniversary parties.', 16, true)
ON CONFLICT (id) DO NOTHING;

-- 1 Banner with Coupon
INSERT INTO banners (id, title, subtitle, image_url, discount_percent, coupon_code, accent_color, start_at, end_at, is_published)
VALUES
(1, 'Grand Celebration Treat 🍰', 'Enjoy flat 15% discount on all cakes and handcrafted chocolates this season!', 'https://images.unsplash.com/photo-1535141192574-5d4897c13136?w=1200', 15, 'SWEET15', '#E11D48', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP + INTERVAL '90 days', true)
ON CONFLICT (id) DO NOTHING;

-- Update sequences to avoid ID collisions on future inserts
SELECT setval(pg_get_serial_sequence('categories', 'id'), COALESCE(MAX(id), 1)) FROM categories;
SELECT setval(pg_get_serial_sequence('products', 'id'), COALESCE(MAX(id), 1)) FROM products;
SELECT setval(pg_get_serial_sequence('product_variants', 'id'), COALESCE(MAX(id), 1)) FROM product_variants;
SELECT setval(pg_get_serial_sequence('party_rooms', 'id'), COALESCE(MAX(id), 1)) FROM party_rooms;
SELECT setval(pg_get_serial_sequence('banners', 'id'), COALESCE(MAX(id), 1)) FROM banners;
SELECT setval(pg_get_serial_sequence('app_settings', 'id'), COALESCE(MAX(id), 1)) FROM app_settings;
