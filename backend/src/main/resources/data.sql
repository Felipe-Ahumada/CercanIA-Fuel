-- Development environment seeds (MySQL/MariaDB syntax).
-- INSERT IGNORE prevents errors if rows already exist (the DB persists across restarts).

INSERT IGNORE INTO role (id, name, description, active, created_at, updated_at)
VALUES (1, 'ADMIN', 'System administrator', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT IGNORE INTO role (id, name, description, active, created_at, updated_at)
VALUES (2, 'USER', 'Regular user', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Fuel types (seed for dev before CNE sync runs)
INSERT IGNORE INTO fuel_type (name, short_name, charge_unit, active) VALUES
('Gasolina 93',           '93',  'LT', TRUE),
('Gasolina 95',           '95',  'LT', TRUE),
('Gasolina 97',           '97',  'LT', TRUE),
('Diésel',                'DI',  'LT', TRUE),
('Gas Natural Vehicular', 'GNV', 'M3', TRUE);

-- Vehicle brands
INSERT IGNORE INTO vehicle_brand (name) VALUES
('Chevrolet'), ('Ford'), ('Honda'), ('Hyundai'), ('Jeep'),
('Kia'), ('Mazda'), ('Mitsubishi'), ('Nissan'), ('Peugeot'),
('Renault'), ('Subaru'), ('Suzuki'), ('Toyota'), ('Volkswagen');

-- Vehicle models
INSERT IGNORE INTO vehicle_model (vehicle_brand_id, name, vehicle_type) VALUES
((SELECT id FROM vehicle_brand WHERE name='Chevrolet'), 'Colorado',     'PICKUP'),
((SELECT id FROM vehicle_brand WHERE name='Chevrolet'), 'Cruze',        'CAR'),
((SELECT id FROM vehicle_brand WHERE name='Chevrolet'), 'Sail',         'CAR'),
((SELECT id FROM vehicle_brand WHERE name='Chevrolet'), 'Spark',        'CAR'),
((SELECT id FROM vehicle_brand WHERE name='Chevrolet'), 'Tracker',      'SUV'),
((SELECT id FROM vehicle_brand WHERE name='Ford'),      'Ecosport',     'SUV'),
((SELECT id FROM vehicle_brand WHERE name='Ford'),      'Escape',       'SUV'),
((SELECT id FROM vehicle_brand WHERE name='Ford'),      'F-150',        'PICKUP'),
((SELECT id FROM vehicle_brand WHERE name='Ford'),      'Ranger',       'PICKUP'),
((SELECT id FROM vehicle_brand WHERE name='Honda'),     'City',         'CAR'),
((SELECT id FROM vehicle_brand WHERE name='Honda'),     'Civic',        'CAR'),
((SELECT id FROM vehicle_brand WHERE name='Honda'),     'CR-V',         'SUV'),
((SELECT id FROM vehicle_brand WHERE name='Honda'),     'HR-V',         'SUV'),
((SELECT id FROM vehicle_brand WHERE name='Hyundai'),   'Accent',       'CAR'),
((SELECT id FROM vehicle_brand WHERE name='Hyundai'),   'Creta',        'SUV'),
((SELECT id FROM vehicle_brand WHERE name='Hyundai'),   'Elantra',      'CAR'),
((SELECT id FROM vehicle_brand WHERE name='Hyundai'),   'Santa Fe',     'SUV'),
((SELECT id FROM vehicle_brand WHERE name='Hyundai'),   'Tucson',       'SUV'),
((SELECT id FROM vehicle_brand WHERE name='Jeep'),      'Cherokee',     'SUV'),
((SELECT id FROM vehicle_brand WHERE name='Jeep'),      'Compass',      'SUV'),
((SELECT id FROM vehicle_brand WHERE name='Jeep'),      'Renegade',     'SUV'),
((SELECT id FROM vehicle_brand WHERE name='Jeep'),      'Wrangler',     'SUV'),
((SELECT id FROM vehicle_brand WHERE name='Kia'),       'Cerato',       'CAR'),
((SELECT id FROM vehicle_brand WHERE name='Kia'),       'Morning',      'CAR'),
((SELECT id FROM vehicle_brand WHERE name='Kia'),       'Rio',          'CAR'),
((SELECT id FROM vehicle_brand WHERE name='Kia'),       'Sorento',      'SUV'),
((SELECT id FROM vehicle_brand WHERE name='Kia'),       'Sportage',     'SUV'),
((SELECT id FROM vehicle_brand WHERE name='Mazda'),     'BT-50',        'PICKUP'),
((SELECT id FROM vehicle_brand WHERE name='Mazda'),     'CX-3',         'SUV'),
((SELECT id FROM vehicle_brand WHERE name='Mazda'),     'CX-5',         'SUV'),
((SELECT id FROM vehicle_brand WHERE name='Mazda'),     'Mazda 3',      'CAR'),
((SELECT id FROM vehicle_brand WHERE name='Mitsubishi'),'Eclipse Cross','SUV'),
((SELECT id FROM vehicle_brand WHERE name='Mitsubishi'),'L200',         'PICKUP'),
((SELECT id FROM vehicle_brand WHERE name='Mitsubishi'),'Outlander',    'SUV'),
((SELECT id FROM vehicle_brand WHERE name='Nissan'),    'Frontier',     'PICKUP'),
((SELECT id FROM vehicle_brand WHERE name='Nissan'),    'Kicks',        'SUV'),
((SELECT id FROM vehicle_brand WHERE name='Nissan'),    'Navara',       'PICKUP'),
((SELECT id FROM vehicle_brand WHERE name='Nissan'),    'Sentra',       'CAR'),
((SELECT id FROM vehicle_brand WHERE name='Nissan'),    'Versa',        'CAR'),
((SELECT id FROM vehicle_brand WHERE name='Peugeot'),   '2008',         'SUV'),
((SELECT id FROM vehicle_brand WHERE name='Peugeot'),   '208',          'CAR'),
((SELECT id FROM vehicle_brand WHERE name='Peugeot'),   '3008',         'SUV'),
((SELECT id FROM vehicle_brand WHERE name='Renault'),   'Duster',       'SUV'),
((SELECT id FROM vehicle_brand WHERE name='Renault'),   'Kwid',         'CAR'),
((SELECT id FROM vehicle_brand WHERE name='Renault'),   'Oroch',        'PICKUP'),
((SELECT id FROM vehicle_brand WHERE name='Renault'),   'Sandero',      'CAR'),
((SELECT id FROM vehicle_brand WHERE name='Subaru'),    'Forester',     'SUV'),
((SELECT id FROM vehicle_brand WHERE name='Subaru'),    'Impreza',      'CAR'),
((SELECT id FROM vehicle_brand WHERE name='Subaru'),    'Outback',      'SUV'),
((SELECT id FROM vehicle_brand WHERE name='Subaru'),    'XV',           'SUV'),
((SELECT id FROM vehicle_brand WHERE name='Suzuki'),    'Baleno',       'CAR'),
((SELECT id FROM vehicle_brand WHERE name='Suzuki'),    'Jimny',        'SUV'),
((SELECT id FROM vehicle_brand WHERE name='Suzuki'),    'Swift',        'CAR'),
((SELECT id FROM vehicle_brand WHERE name='Suzuki'),    'Vitara',       'SUV'),
((SELECT id FROM vehicle_brand WHERE name='Toyota'),    'Corolla',      'CAR'),
((SELECT id FROM vehicle_brand WHERE name='Toyota'),    'Fortuner',     'SUV'),
((SELECT id FROM vehicle_brand WHERE name='Toyota'),    'Hilux',        'PICKUP'),
((SELECT id FROM vehicle_brand WHERE name='Toyota'),    'Land Cruiser', 'SUV'),
((SELECT id FROM vehicle_brand WHERE name='Toyota'),    'RAV4',         'SUV'),
((SELECT id FROM vehicle_brand WHERE name='Toyota'),    'Yaris',        'CAR'),
((SELECT id FROM vehicle_brand WHERE name='Volkswagen'),'Amarok',       'PICKUP'),
((SELECT id FROM vehicle_brand WHERE name='Volkswagen'),'Golf',         'CAR'),
((SELECT id FROM vehicle_brand WHERE name='Volkswagen'),'Polo',         'CAR'),
((SELECT id FROM vehicle_brand WHERE name='Volkswagen'),'T-Cross',      'SUV'),
((SELECT id FROM vehicle_brand WHERE name='Volkswagen'),'Tiguan',       'SUV');

-- Dev-only admin user. Authenticate via header: X-Dev-User: admin@fuelonline.cl
-- firebase_uid is NULL intentionally: this user is only used in dev mode.
INSERT IGNORE INTO user (
    id, role_id, email, firebase_uid,
    rut, first_name, middle_name, last_name, second_last_name,
    birth_date, active, created_at, updated_at
) VALUES (
    '00000000-0000-0000-0000-000000000001', 1, 'admin@fuelonline.cl', NULL,
    '11111111-1', 'Admin', NULL, 'Dev', 'Local',
    '1990-01-01', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);
