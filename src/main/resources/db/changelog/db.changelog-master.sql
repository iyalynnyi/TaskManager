CREATE TABLE task (
                       id BIGSERIAL PRIMARY KEY,
                       title VARCHAR(100) NOT NULL,
                       description TEXT,
                       status VARCHAR(50) NOT NULL,
                       priority VARCHAR(50) NOT NULL,
                       created_date TIMESTAMP NOT NULL,
                       updated_date TIMESTAMP,
                       due_date TIMESTAMP,
                       assignee VARCHAR(50),
                       reporter VARCHAR(50)
);
