-- =============================================================================
-- BrightMart Retail Management System - Database Schema & Initial Seed Data
-- Database Engine: MySQL 8.0+
-- Database Name: brightmart_db
-- =============================================================================

CREATE DATABASE IF NOT EXISTS `brightmart_db` 
    DEFAULT CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE `brightmart_db`;

-- -----------------------------------------------------------------------------
-- Table: users
-- Description: System authentication and role-based access control.
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
    `user_id` INT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `role` VARCHAR(20) NOT NULL DEFAULT 'Cashier',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- Table: supplier
-- Description: Registered suppliers for inventory purchasing and stock inward (GRN).
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS `supplier`;
CREATE TABLE `supplier` (
    `supplier_code` VARCHAR(50) PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `address` VARCHAR(255) DEFAULT '',
    `contact_details` VARCHAR(50) DEFAULT '',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- Table: product
-- Description: Retail product catalogue with barcode tracking and reorder thresholds.
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
    `item_code` VARCHAR(50) PRIMARY KEY,
    `barcode` VARCHAR(50) UNIQUE,
    `description` VARCHAR(255) NOT NULL,
    `category` VARCHAR(50) DEFAULT 'General',
    `unit_price` DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    `current_stock` INT NOT NULL DEFAULT 0,
    `reorder_level` INT NOT NULL DEFAULT 10,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- Table: invoice
-- Description: Customer sales invoices / POS receipts header records.
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS `invoice`;
CREATE TABLE `invoice` (
    `invoice_no` VARCHAR(50) PRIMARY KEY,
    `invoice_date_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `total_amount` DECIMAL(10, 2) NOT NULL DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- Table: invoice_items
-- Description: Individual line items associated with each customer invoice.
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS `invoice_items`;
CREATE TABLE `invoice_items` (
    `invoice_no` VARCHAR(50) NOT NULL,
    `item_code` VARCHAR(50) NOT NULL,
    `sold_qty` INT NOT NULL,
    `selling_price` DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (`invoice_no`, `item_code`),
    CONSTRAINT `fk_invoice_items_invoice` FOREIGN KEY (`invoice_no`) 
        REFERENCES `invoice` (`invoice_no`) ON DELETE CASCADE,
    CONSTRAINT `fk_invoice_items_product` FOREIGN KEY (`item_code`) 
        REFERENCES `product` (`item_code`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- Table: grn (Goods Received Note)
-- Description: Inventory purchasing and inward stock batch records.
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS `grn`;
CREATE TABLE `grn` (
    `grn_no` VARCHAR(50) PRIMARY KEY,
    `supplier_code` VARCHAR(50) NOT NULL,
    `received_date_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `total_amount` DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    CONSTRAINT `fk_grn_supplier` FOREIGN KEY (`supplier_code`) 
        REFERENCES `supplier` (`supplier_code`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- Table: grn_items
-- Description: Individual line items associated with inward stock batches.
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS `grn_items`;
CREATE TABLE `grn_items` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `grn_no` VARCHAR(50) NOT NULL,
    `item_code` VARCHAR(50) NOT NULL,
    `received_qty` INT NOT NULL,
    `buying_price` DECIMAL(10, 2) NOT NULL,
    CONSTRAINT `fk_grn_items_grn` FOREIGN KEY (`grn_no`) 
        REFERENCES `grn` (`grn_no`) ON DELETE CASCADE,
    CONSTRAINT `fk_grn_items_product` FOREIGN KEY (`item_code`) 
        REFERENCES `product` (`item_code`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- Initial Seed Data
-- =============================================================================

-- Default Users (admin / cashier)
INSERT INTO `users` (`username`, `password`, `role`) VALUES
('admin', 'admin123', 'Admin'),
('cashier', 'cashier123', 'Cashier')
ON DUPLICATE KEY UPDATE `role` = VALUES(`role`);

-- Default Suppliers
INSERT INTO `supplier` (`supplier_code`, `name`, `address`, `contact_details`) VALUES
('SUP-001', 'Lanka Distributing Co.', 'No. 45 Galle Road, Colombo 03', '0112345678'),
('SUP-002', 'Ceylon Wholesale Traders', '12 Kandy Road, Kiribathgoda', '0114987654'),
('SUP-003', 'Apex Food Supplies', '78 Industrial Zone, Ja-Ela', '0117123456')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- Default Retail Products
INSERT INTO `product` (`item_code`, `barcode`, `description`, `category`, `unit_price`, `current_stock`, `reorder_level`) VALUES
('PRD-001', '8934567890123', 'Anchor Full Cream Milk Powder 400g', 'Dairy', 1150.00, 45, 10),
('PRD-002', '8934567890124', 'Munchee Super Cream Cracker 490g', 'Bakery & Biscuits', 420.00, 60, 15),
('PRD-003', '8934567890125', 'Sunlight Lemon Clean Soap 115g', 'Household', 160.00, 80, 20),
('PRD-004', '8934567890126', 'Watawala Pure Ceylon Tea 200g', 'Beverages', 390.00, 35, 10),
('PRD-005', '8934567890127', 'Keells Krest Chicken Sausages 500g', 'Meat & Frozen', 980.00, 8, 12),
('PRD-006', '8934567890128', 'Diva Fresh Detergent Powder 1kg', 'Household', 450.00, 5, 10)
ON DUPLICATE KEY UPDATE `description` = VALUES(`description`);
