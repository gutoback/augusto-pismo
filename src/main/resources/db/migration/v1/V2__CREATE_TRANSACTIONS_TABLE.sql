CREATE TABLE OPERATION_TYPE
(
    id  UUID NOT NULL,
    description  VARCHAR(50) NOT NULL,
    should_negate_amount BOOL NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_operation_type PRIMARY KEY (id)
);

CREATE TABLE transactions
(
    id              UUID NOT NULL,
    account_id      UUID NOT NULL REFERENCES accounts(id),
    operation_id    UUID not null REFERENCES OPERATION_TYPE(id),
    event_date      TIMESTAMP NOT NULL,
    amount          NUMERIC(19,2) NOT NULL,
    CONSTRAINT pk_transactions PRIMARY KEY (id)
);

CREATE INDEX idx_transaction_account ON transactions(account_id,operation_id);

INSERT INTO OPERATION_TYPE(ID, DESCRIPTION, should_negate_amount) VALUES
(gen_random_uuid(), 'Normal Purchase',TRUE),
( gen_random_uuid(), 'Purchase with installments',TRUE),
(gen_random_uuid(), 'Withdrawal',TRUE),
(gen_random_uuid(),'Credit Voucher',FALSE);