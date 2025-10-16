# BankBackend
Springboot Backend
Bank Application

A Banking System built using Spring Boot.

Database Schema
Customer Schema
CREATE TABLE customers (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100),
    email           VARCHAR(255) UNIQUE,
    phone           VARCHAR(20),
    dob             DATE,
    password        VARCHAR(255),
    address_line1   VARCHAR(255),
    address_line2   VARCHAR(255),
    city            VARCHAR(100),
    state           VARCHAR(100),
    postal_code     VARCHAR(20),
    country         VARCHAR(100),
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP
);

Fields

id: Primary Key (auto-generated)

first_name: Customer’s first name (NOT NULL)

last_name: Customer’s last name

email: Unique email for login

phone: Contact number

dob: Date of Birth

password: Plain text password (demo only, not secure)

address_line1, address_line2: Address details

city, state, postal_code, country: Location details

created_at: Record creation timestamp

updated_at: Record update timestamp


Account Schema
CREATE TABLE accounts (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_number  VARCHAR(32) UNIQUE NOT NULL,
    customer_id     BIGINT NOT NULL REFERENCES customers(id),
    account_type    VARCHAR(20) NOT NULL,
    balance         DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    opened_at       TIMESTAMP,
    interest_rate   DECIMAL(5,4),
    overdraft_limit DECIMAL(18,2),
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
);

Fields

id: Primary Key (auto-generated)

account_number: Unique account number (NOT NULL)

customer_id: Foreign Key → customers(id)

account_type: Account type (SAVINGS, CURRENT)

balance: Current account balance (NOT NULL, defaults 0.00)

opened_at: Account opening timestamp

interest_rate: Interest rate for savings accounts

overdraft_limit: Overdraft limit for current accounts

status: Account status (default = ACTIVE)


Transaction Schema
CREATE TABLE transactions (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id  BIGINT NOT NULL REFERENCES accounts(id),
    txn_type    VARCHAR(20) NOT NULL,
    amount      DECIMAL(18,2) NOT NULL,
    txn_date    TIMESTAMP,
    note        VARCHAR(255)
);

Fields

id: Primary Key (auto-generated)

account_id: Foreign Key → accounts(id)

txn_type: Transaction type (DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT)

amount: Transaction amount (NOT NULL)

txn_date: Transaction timestamp (auto-set if null)
