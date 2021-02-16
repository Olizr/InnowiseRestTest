CREATE TABLE Documents (
	id SERIAL PRIMARY KEY,
	title VARCHAR NOT NULL,
	status VARCHAR NOT NULL,
	creation_date DATE NOT NULL,
	execution_period DATE NOT NULL,
	customer_id INT REFERENCES persons (id),
	executor_id INT REFERENCES persons (id),
	is_deleted BOOLEAN NOT NULL
);