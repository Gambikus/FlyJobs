-- Создание перечисления для типов расписания
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'schedule_type') THEN
        CREATE TYPE schedule_type AS ENUM ('CRON', 'ONE_TIME', 'FIXED_DELAY');
    END IF;
END$$;

-- Создание перечисления для состояний задания
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'job_state') THEN
        CREATE TYPE job_state AS ENUM ('SCHEDULED', 'RUNNING', 'FINISHED', 'ERROR');
    END IF;
END$$;

-- Создание таблицы для хранения джоб
CREATE TABLE IF NOT EXISTS jobs (
    id SERIAL PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    schedule_type schedule_type NOT NULL,
    schedule_expression VARCHAR(255) NOT NULL,
    state job_state NOT NULL,
    class_name VARCHAR(255) NOT NULL,
    method_name VARCHAR(255) NOT NULL,
    retries INT NOT NULL DEFAULT 0
);