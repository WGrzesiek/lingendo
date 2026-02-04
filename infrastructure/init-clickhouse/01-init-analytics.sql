-- Inicjalizacja ClickHouse dla statistics-service
-- Skrypt wykonywany przy pierwszym uruchomieniu
-- Tabele są tworzone przez migracje Flyway w statistics-service

CREATE DATABASE IF NOT EXISTS analytics;
