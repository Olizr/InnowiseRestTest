CREATE TABLE roles (
  id SERIAL PRIMARY KEY,
  person_id INT NOT NULL,
  role VARCHAR(50) NOT NULL,
  FOREIGN KEY (person_id) REFERENCES persons(id)
);