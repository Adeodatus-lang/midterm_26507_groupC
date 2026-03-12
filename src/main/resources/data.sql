-- Initial Data Seeding for Barber Management System
-- Using ON CONFLICT DO NOTHING to make the script idempotent for PostgreSQL

-- 1. Insert Locations (Provinces/Districts/Sectors/Cells/Villages)
INSERT INTO location (id, name, type, code, parent_id) VALUES (1, 'Kigali', 'PROVINCE', 'RW-K', NULL) ON CONFLICT (id) DO NOTHING;
INSERT INTO location (id, name, type, code, parent_id) VALUES (2, 'Nyarugenge', 'DISTRICT', 'RW-K-NY', 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO location (id, name, type, code, parent_id) VALUES (3, 'Nyarugenge Sector', 'SECTOR', 'RW-K-NY-S', 2) ON CONFLICT (id) DO NOTHING;
INSERT INTO location (id, name, type, code, parent_id) VALUES (4, 'Biryogo', 'CELL', 'RW-K-NY-S-B', 3) ON CONFLICT (id) DO NOTHING;
INSERT INTO location (id, name, type, code, parent_id) VALUES (5, 'Village 1', 'VILLAGE', 'RW-K-NY-S-B-V1', 4) ON CONFLICT (id) DO NOTHING;

-- 2. Insert Users (Admin, Barber, Customer)
INSERT INTO users (id, email, password, full_name, phone_number, role, village_id, created_at, updated_at) VALUES 
(1, 'admin@barber.com', 'admin123', 'System Admin', '0780000001', 'ADMIN', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) ON CONFLICT (id) DO NOTHING;
INSERT INTO users (id, email, password, full_name, phone_number, role, village_id, created_at, updated_at) VALUES 
(2, 'barber1@barber.com', 'barber123', 'John Barber', '0780000002', 'BARBER', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) ON CONFLICT (id) DO NOTHING;
INSERT INTO users (id, email, password, full_name, phone_number, role, village_id, created_at, updated_at) VALUES 
(3, 'customer1@gmail.com', 'customer123', 'Alice Customer', '0780000003', 'CUSTOMER', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) ON CONFLICT (id) DO NOTHING;

-- 3. Insert Barbers
INSERT INTO barbers (id, specialization, rating, years_of_experience, is_available, license_number, user_id, created_at, updated_at) VALUES 
(1, 'Haircut & Styling', 4.8, 5, true, 'LIC-2024-001', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) ON CONFLICT (id) DO NOTHING;

-- 4. Insert Customers
INSERT INTO customers (id, user_id, loyalty_points) VALUES (1, 3, 0) ON CONFLICT (id) DO NOTHING;

-- 5. Insert Services
INSERT INTO services (id, name, description, duration_minutes, price, is_active) VALUES 
(1, 'Classic Haircut', 'Standard haircut and styling', 30, 5000.0, true) ON CONFLICT (id) DO NOTHING;
INSERT INTO services (id, name, description, duration_minutes, price, is_active) VALUES 
(2, 'Beard Trim', 'Professional beard grooming', 15, 2000.0, true) ON CONFLICT (id) DO NOTHING;

-- 6. Link Barber to Services
INSERT INTO barber_services (barber_id, service_id)
SELECT 1, 1 WHERE NOT EXISTS (SELECT 1 FROM barber_services WHERE barber_id = 1 AND service_id = 1);
INSERT INTO barber_services (barber_id, service_id)
SELECT 1, 2 WHERE NOT EXISTS (SELECT 1 FROM barber_services WHERE barber_id = 1 AND service_id = 2);

-- Adjust sequences (Directly without DO block to avoid Spring parser issues)
-- These will only run after Hibernate setup due to defer-datasource-initialization=true
SELECT setval('location_id_seq', (SELECT COALESCE(max(id), 1) FROM location));
SELECT setval('users_id_seq', (SELECT COALESCE(max(id), 1) FROM users));
SELECT setval('barbers_id_seq', (SELECT COALESCE(max(id), 1) FROM barbers));
SELECT setval('customers_id_seq', (SELECT COALESCE(max(id), 1) FROM customers));
SELECT setval('services_id_seq', (SELECT COALESCE(max(id), 1) FROM services));
