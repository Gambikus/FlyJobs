-- Создание таблицы для хранения джоб
CREATE TABLE IF NOT EXISTS jobs (
    id uuid PRIMARY KEY,
    execute_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    class_name VARCHAR(255) NOT NULL,
    method_name VARCHAR(255) NOT NULL
);