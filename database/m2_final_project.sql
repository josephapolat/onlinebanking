-- database m2_final_project
BEGIN TRANSACTION;

-- *************************************************************************************************
-- Drop all db objects in the proper order
-- *************************************************************************************************
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS accounts;

-- *************************************************************************************************
-- Create the tables and constraints
-- *************************************************************************************************

--users (name is pluralized because 'user' is a SQL keyword)
CREATE TABLE users (
	user_id SERIAL,
	username varchar(50) NOT NULL UNIQUE,
	password_hash varchar(200) NOT NULL,
	role varchar(50) NOT NULL,
	signer_id int NOT NULL,
	CONSTRAINT PK_user PRIMARY KEY (user_id)
);
CREATE TABLE customers (
	customer_id SERIAL,
	name varchar(50) NOT NULL,
	ssn varchar(9) NOT NULL UNIQUE,
	CONSTRAINT PK_customers PRIMARY KEY (customer_id)
);
CREATE TABLE accounts (
	account_id SERIAL,
	account_number varchar(9) NOT NULL UNIQUE,
	primary_signer varchar(9) NOT NULL,
	secondary_signer varchar(9),
	balance DOUBLE NOT NULL,
	account_nickname varchar(50),
	CONSTRAINT PK_accounts PRIMARY KEY (account_id)
);

-- *************************************************************************************************
-- Insert some sample starting data
-- *************************************************************************************************

-- Users
-- Password for all users is password
INSERT INTO
    users (username, password_hash, role, signer_id)
VALUES
    ('johndoe', '$2a$10$tmxuYYg1f5T0eXsTPlq/V.DJUKmRHyFbJ.o.liI1T35TFbjs2xiem','ROLE_USER', 1),
    ('janedoe', '$2a$10$tmxuYYg1f5T0eXsTPlq/V.DJUKmRHyFbJ.o.liI1T35TFbjs2xiem','ROLE_USER', 2),
    ('admin','$2a$10$tmxuYYg1f5T0eXsTPlq/V.DJUKmRHyFbJ.o.liI1T35TFbjs2xiem','ROLE_ADMIN', 3);

INSERT INTO
    customers (name, ssn)
VALUES
    ('John Doe', '111111111'),
    ('Jane Doe', '222222222'),
    ('Rob Roberts', '333333333'),
    ('Renee Roberts', '444444444'),
    ('Larry Lasso', '555555555'),
    ('Linda Lasso', '666666666');


INSERT INTO
    accounts (account_number, primary_signer, secondary_signer, balance)
VALUES
    ('147258369', '111111111', '222222222', 0.00),
    ('369258147', '222222222', '', 100.00);

COMMIT TRANSACTION;
