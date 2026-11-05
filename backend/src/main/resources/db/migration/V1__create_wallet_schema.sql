CREATE TABLE customers (
  id UUID PRIMARY KEY,
  name VARCHAR(80) NOT NULL,
  email VARCHAR(254) NOT NULL UNIQUE,
  phone VARCHAR(13),
  password_hash VARCHAR(255) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL
);
CREATE TABLE wallets (
  id UUID PRIMARY KEY,
  customer_id UUID NOT NULL UNIQUE REFERENCES customers(id) ON DELETE CASCADE,
  currency VARCHAR(3) NOT NULL DEFAULT 'USD',
  balance NUMERIC(19,2) NOT NULL DEFAULT 0 CHECK (balance >= 0),
  created_at TIMESTAMPTZ NOT NULL,
  version BIGINT NOT NULL DEFAULT 0
);
CREATE TABLE payment_transactions (
  id UUID PRIMARY KEY,
  wallet_id UUID NOT NULL REFERENCES wallets(id),
  name VARCHAR(120) NOT NULL,
  category VARCHAR(60) NOT NULL,
  type VARCHAR(40) NOT NULL,
  amount NUMERIC(19,2) NOT NULL CHECK (amount <> 0),
  status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED' CHECK (status IN ('PENDING','COMPLETED','FAILED','REFUNDED')),
  reference VARCHAR(32) NOT NULL UNIQUE,
  idempotency_key VARCHAR(100),
  created_at TIMESTAMPTZ NOT NULL,
  CONSTRAINT uq_tx_wallet_idempotency UNIQUE(wallet_id,idempotency_key)
);
CREATE INDEX idx_transaction_wallet_time ON payment_transactions(wallet_id,created_at DESC);
CREATE TABLE ledger_entries (
  id UUID PRIMARY KEY,
  transaction_id UUID NOT NULL REFERENCES payment_transactions(id),
  account VARCHAR(100) NOT NULL,
  amount NUMERIC(19,2) NOT NULL CHECK (amount <> 0),
  created_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_ledger_transaction ON ledger_entries(transaction_id);
CREATE TABLE fraud_reports (
  id UUID PRIMARY KEY,
  customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
  upi_id VARCHAR(64) NOT NULL,
  scam_type VARCHAR(40) NOT NULL,
  description VARCHAR(500) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
  created_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_fraud_report_created ON fraud_reports(created_at);
CREATE TABLE payment_cards (
  id UUID PRIMARY KEY,
  wallet_id UUID NOT NULL REFERENCES wallets(id),
  last4 VARCHAR(4) NOT NULL,
  brand VARCHAR(20) NOT NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','FROZEN','CANCELLED')),
  created_at TIMESTAMPTZ NOT NULL
);
