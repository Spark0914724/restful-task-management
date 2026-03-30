CREATE TABLE tasks (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    status     VARCHAR(20)  NOT NULL DEFAULT 'NEW',
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL
);