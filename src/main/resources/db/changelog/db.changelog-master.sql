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

CREATE TABLE tag (
                      id BIGSERIAL PRIMARY KEY,
                      name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE task_tags (
                           task_id BIGINT NOT NULL,
                           tag_id BIGINT NOT NULL,
                           PRIMARY KEY (task_id, tag_id),
                           FOREIGN KEY (task_id) REFERENCES task(id) ON DELETE CASCADE,
                           FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
);
