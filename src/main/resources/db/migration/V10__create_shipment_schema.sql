-- =====================================================
-- Migration: V10 - Create Shipment Tracking Schema
-- Description: Tables for shipment tracking functionality
-- Author: AI System
-- Date: 2026-07-17
-- =====================================================

-- Create shipment schema
CREATE SCHEMA IF NOT EXISTS shipment;

-- =====================================================
-- Table: shipments
-- Description: Main shipment tracking table
-- =====================================================
CREATE TABLE shipment.shipments (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    shipment_number VARCHAR(100) NOT NULL,
    lc_id UUID,

    -- Parties
    shipper_name VARCHAR(255) NOT NULL,
    shipper_address TEXT,
    consignee_name VARCHAR(255) NOT NULL,
    consignee_address TEXT,
    notify_party VARCHAR(255),

    -- Shipment Details
    transport_mode VARCHAR(50) NOT NULL,
    carrier_name VARCHAR(255),
    vessel_name VARCHAR(255),
    voyage_number VARCHAR(100),
    bill_of_lading_number VARCHAR(100),
    container_numbers TEXT[],

    -- Route
    port_of_loading VARCHAR(255),
    port_of_discharge VARCHAR(255),
    final_destination VARCHAR(255),

    -- Dates
    expected_departure_date TIMESTAMP,
    actual_departure_date TIMESTAMP,
    expected_arrival_date TIMESTAMP,
    actual_arrival_date TIMESTAMP,
    estimated_delivery_date TIMESTAMP,
    actual_delivery_date TIMESTAMP,

    -- Status
    status VARCHAR(50) NOT NULL,
    current_location VARCHAR(255),

    -- Cargo Details
    cargo_description TEXT,
    total_packages INTEGER,
    total_weight_kg DECIMAL(15,2),
    total_volume_cbm DECIMAL(15,2),

    -- Documents
    documents_received BOOLEAN DEFAULT FALSE,
    documents_verified BOOLEAN DEFAULT FALSE,
    customs_cleared BOOLEAN DEFAULT FALSE,

    -- Tracking
    last_update_time TIMESTAMP,
    last_update_location VARCHAR(255),

    -- Delays
    is_delayed BOOLEAN DEFAULT FALSE,
    delay_reason TEXT,
    days_delayed INTEGER DEFAULT 0,

    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 0,

    -- Constraints
    CONSTRAINT uq_shipment_number_tenant UNIQUE (tenant_id, shipment_number),
    CONSTRAINT ck_shipment_status CHECK (status IN ('CREATED', 'READY', 'IN_TRANSIT', 'ARRIVED', 'CUSTOMS_CLEARED', 'DELIVERED', 'CANCELLED')),
    CONSTRAINT ck_transport_mode CHECK (transport_mode IN ('SEA', 'AIR', 'ROAD', 'RAIL', 'MULTIMODAL'))
);

-- =====================================================
-- Table: shipment_events
-- Description: Tracking events and updates
-- =====================================================
CREATE TABLE shipment.shipment_events (
    id UUID PRIMARY KEY,
    shipment_id UUID NOT NULL,
    tenant_id VARCHAR(50) NOT NULL,

    -- Event Details
    event_type VARCHAR(50) NOT NULL,
    event_time TIMESTAMP NOT NULL,
    location VARCHAR(255),
    description TEXT,

    -- Status
    old_status VARCHAR(50),
    new_status VARCHAR(50),

    -- Additional Data
    metadata JSONB,

    -- Audit
    recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    recorded_by VARCHAR(100),

    -- Foreign Keys
    CONSTRAINT fk_shipment_events_shipment FOREIGN KEY (shipment_id)
        REFERENCES shipment.shipments(id) ON DELETE CASCADE,
    CONSTRAINT ck_event_type CHECK (event_type IN (
        'CREATED', 'READY_FOR_SHIPMENT', 'DEPARTED', 'IN_TRANSIT_UPDATE',
        'ARRIVED', 'CUSTOMS_CLEARANCE', 'DELIVERED', 'DELAYED',
        'EXCEPTION', 'CANCELLED'
    ))
);

-- =====================================================
-- Table: shipment_documents
-- Description: Documents related to shipments
-- =====================================================
CREATE TABLE shipment.shipment_documents (
    id UUID PRIMARY KEY,
    shipment_id UUID NOT NULL,
    tenant_id VARCHAR(50) NOT NULL,

    -- Document Info
    document_type VARCHAR(100) NOT NULL,
    document_number VARCHAR(100),
    file_path VARCHAR(500),
    file_name VARCHAR(255),

    -- Status
    received BOOLEAN DEFAULT FALSE,
    verified BOOLEAN DEFAULT FALSE,

    -- Dates
    received_date TIMESTAMP,
    verified_date TIMESTAMP,
    verified_by VARCHAR(100),

    -- Audit
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Keys
    CONSTRAINT fk_shipment_documents_shipment FOREIGN KEY (shipment_id)
        REFERENCES shipment.shipments(id) ON DELETE CASCADE
);

-- =====================================================
-- Indexes for Performance
-- =====================================================

-- Primary lookups
CREATE INDEX idx_shipments_tenant ON shipment.shipments(tenant_id);
CREATE INDEX idx_shipments_number ON shipment.shipments(shipment_number);
CREATE INDEX idx_shipments_lc ON shipment.shipments(lc_id);
CREATE INDEX idx_shipments_status ON shipment.shipments(status);

-- Date-based queries
CREATE INDEX idx_shipments_departure ON shipment.shipments(expected_departure_date);
CREATE INDEX idx_shipments_arrival ON shipment.shipments(expected_arrival_date);
CREATE INDEX idx_shipments_delivery ON shipment.shipments(estimated_delivery_date);

-- Delayed shipments
CREATE INDEX idx_shipments_delayed ON shipment.shipments(is_delayed, tenant_id) WHERE is_delayed = TRUE;

-- Events
CREATE INDEX idx_shipment_events_shipment ON shipment.shipment_events(shipment_id, event_time DESC);
CREATE INDEX idx_shipment_events_tenant ON shipment.shipment_events(tenant_id);
CREATE INDEX idx_shipment_events_time ON shipment.shipment_events(event_time DESC);

-- Documents
CREATE INDEX idx_shipment_documents_shipment ON shipment.shipment_documents(shipment_id);
CREATE INDEX idx_shipment_documents_tenant ON shipment.shipment_documents(tenant_id);

-- =====================================================
-- Comments
-- =====================================================

COMMENT ON TABLE shipment.shipments IS 'Main shipment tracking table for trade finance transactions';
COMMENT ON TABLE shipment.shipment_events IS 'Tracking events and status updates for shipments';
COMMENT ON TABLE shipment.shipment_documents IS 'Documents associated with shipments (BOL, packing list, etc.)';

COMMENT ON COLUMN shipment.shipments.transport_mode IS 'Mode of transportation: SEA, AIR, ROAD, RAIL, MULTIMODAL';
COMMENT ON COLUMN shipment.shipments.status IS 'Current status of shipment';
COMMENT ON COLUMN shipment.shipments.is_delayed IS 'Flag indicating if shipment is delayed beyond expected delivery';

