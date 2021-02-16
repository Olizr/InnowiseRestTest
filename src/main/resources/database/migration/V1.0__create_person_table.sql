CREATE TABLE persons (
	id SERIAL PRIMARY KEY,
	username VARCHAR NOT NULL,
	password VARCHAR NOT NULL,
	first_name VARCHAR NOT NULL,
	last_name VARCHAR NOT NULL,
	birth_date DATE NOT NULL,
	is_deleted BOOLEAN NOT NULL
);