CREATE TABLE Document (
	id SERIAL PRIMARY KEY,
	title VARCHAR NOT NULL,
	status VARCHAR NOT NULL,
	creation_date DATE NOT NULL,
	execution_period DATE NOT NULL,
	customer_id INT REFERENCES Person (id),
	executor_id INT REFERENCES Person (id),
	is_deleted BOOLEAN NOT NULL
);