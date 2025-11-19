CREATE SCHEMA IF NOT EXISTS itrum_demo;

CREATE TABLE IF NOT EXISTS itrum_demo.transaction
(
    id             UUID PRIMARY KEY,
    user_id        UUID           NOT NULL,
    wallet_id      UUID           NOT NULL,
    amount         NUMERIC(17, 2) NOT NULL,
    operation_type VARCHAR(10)    NOT NULL,
    created_at     timestamptz DEFAULT current_timestamp
)