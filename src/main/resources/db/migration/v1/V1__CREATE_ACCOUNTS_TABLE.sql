CREATE TABLE accounts
(
    id              UUID NOT NULL,
    document_number VARCHAR(255),
    CONSTRAINT pk_accounts PRIMARY KEY (id)
);

ALTER TABLE accounts
    ADD CONSTRAINT uc_accounts_document_number UNIQUE (document_number);