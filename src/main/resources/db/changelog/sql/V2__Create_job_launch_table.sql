-- Создание таблицы для хранения джоб
CREATE TABLE IF NOT EXISTS job_launch (
    job_id uuid PRIMARY KEY,
    timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    status TEXT NOT NULL
);