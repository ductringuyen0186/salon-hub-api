-- V4: Seed initial service types for nail salon demo
-- Uses MERGE INTO for H2/PostgreSQL compatibility

-- Manicure Services
MERGE INTO service_types (name, estimated_duration_minutes, price, description, category, popular, active)
KEY (name)
VALUES ('Signature Manicure', 60, 45.00, 'Complete nail care with cuticle treatment, shaping, and luxury hand massage', 'Manicure Services', true, true);

MERGE INTO service_types (name, estimated_duration_minutes, price, description, category, popular, active)
KEY (name)
VALUES ('Express Manicure', 30, 25.00, 'Quick nail shaping, cuticle care, and polish application', 'Manicure Services', false, true);

MERGE INTO service_types (name, estimated_duration_minutes, price, description, category, popular, active)
KEY (name)
VALUES ('Gel Manicure', 45, 35.00, 'Long-lasting gel polish with chip-resistant finish', 'Manicure Services', false, true);

MERGE INTO service_types (name, estimated_duration_minutes, price, description, category, popular, active)
KEY (name)
VALUES ('French Manicure', 50, 40.00, 'Classic French tips with precision and elegance', 'Manicure Services', false, true);

-- Pedicure Services
MERGE INTO service_types (name, estimated_duration_minutes, price, description, category, popular, active)
KEY (name)
VALUES ('Deluxe Pedicure', 75, 65.00, 'Ultimate foot treatment with exfoliation, hot stone massage, and paraffin', 'Pedicure Services', true, true);

MERGE INTO service_types (name, estimated_duration_minutes, price, description, category, popular, active)
KEY (name)
VALUES ('Classic Pedicure', 45, 35.00, 'Essential foot care with nail trimming, shaping, and polish', 'Pedicure Services', false, true);

MERGE INTO service_types (name, estimated_duration_minutes, price, description, category, popular, active)
KEY (name)
VALUES ('Spa Pedicure', 60, 50.00, 'Relaxing treatment with callus removal and foot massage', 'Pedicure Services', false, true);

MERGE INTO service_types (name, estimated_duration_minutes, price, description, category, popular, active)
KEY (name)
VALUES ('Medical Pedicure', 60, 70.00, 'Therapeutic treatment for foot health and nail care', 'Pedicure Services', false, true);

-- Nail Enhancements
MERGE INTO service_types (name, estimated_duration_minutes, price, description, category, popular, active)
KEY (name)
VALUES ('Acrylic Full Set', 90, 55.00, 'Complete acrylic nail application with custom shaping and length', 'Nail Enhancements', false, true);

MERGE INTO service_types (name, estimated_duration_minutes, price, description, category, popular, active)
KEY (name)
VALUES ('Gel Extensions', 90, 60.00, 'Natural-looking gel extensions with superior durability', 'Nail Enhancements', false, true);

MERGE INTO service_types (name, estimated_duration_minutes, price, description, category, popular, active)
KEY (name)
VALUES ('Dip Powder', 60, 45.00, 'Healthy nail strengthening with beautiful color finish', 'Nail Enhancements', false, true);

MERGE INTO service_types (name, estimated_duration_minutes, price, description, category, popular, active)
KEY (name)
VALUES ('Fill & Maintenance', 45, 35.00, 'Professional maintenance for existing nail enhancements', 'Nail Enhancements', false, true);

-- Nail Art & Design
MERGE INTO service_types (name, estimated_duration_minutes, price, description, category, popular, active)
KEY (name)
VALUES ('Custom Nail Art', 30, 15.00, 'Per nail artistic design created by our talented artists', 'Nail Art & Design', true, true);

MERGE INTO service_types (name, estimated_duration_minutes, price, description, category, popular, active)
KEY (name)
VALUES ('3D Nail Art', 45, 25.00, 'Dimensional artwork with gems, charms, and textures', 'Nail Art & Design', false, true);

MERGE INTO service_types (name, estimated_duration_minutes, price, description, category, popular, active)
KEY (name)
VALUES ('Seasonal Designs', 35, 20.00, 'Trendy seasonal themes and holiday-inspired art', 'Nail Art & Design', false, true);

MERGE INTO service_types (name, estimated_duration_minutes, price, description, category, popular, active)
KEY (name)
VALUES ('Bridal Package', 120, 150.00, 'Complete bridal nail service with trial and wedding day application', 'Nail Art & Design', false, true);

-- Add-On Services
MERGE INTO service_types (name, estimated_duration_minutes, price, description, category, popular, active)
KEY (name)
VALUES ('Cuticle Oil Treatment', 10, 5.00, 'Nourishing treatment for healthy cuticles', 'Add-On Services', false, true);

MERGE INTO service_types (name, estimated_duration_minutes, price, description, category, popular, active)
KEY (name)
VALUES ('Hand Mask', 15, 10.00, 'Moisturizing mask for soft, smooth hands', 'Add-On Services', false, true);

MERGE INTO service_types (name, estimated_duration_minutes, price, description, category, popular, active)
KEY (name)
VALUES ('Paraffin Treatment', 20, 15.00, 'Therapeutic wax treatment for deep moisturizing', 'Add-On Services', false, true);

MERGE INTO service_types (name, estimated_duration_minutes, price, description, category, popular, active)
KEY (name)
VALUES ('Hot Stone Massage', 25, 20.00, 'Relaxing hot stone therapy for hands and arms', 'Add-On Services', false, true);

-- Combo Packages
MERGE INTO service_types (name, estimated_duration_minutes, price, description, category, popular, active)
KEY (name)
VALUES ('Mani-Pedi Combo', 90, 75.00, 'Complete manicure and pedicure package at a special price', 'Combo Packages', true, true);

MERGE INTO service_types (name, estimated_duration_minutes, price, description, category, popular, active)
KEY (name)
VALUES ('Spa Day Package', 150, 120.00, 'Deluxe manicure, deluxe pedicure, and relaxation treatments', 'Combo Packages', false, true);
