-- Inicjalizacja baz danych PostgreSQL dla projektu LearnWords

-- Baza dla user-service
CREATE DATABASE user_management;
GRANT ALL PRIVILEGES ON DATABASE user_management TO admin;

-- Baza dla deck-service  
CREATE DATABASE deck;
GRANT ALL PRIVILEGES ON DATABASE deck TO admin;

-- Baza dla koog-service (AI)
CREATE DATABASE koog;
GRANT ALL PRIVILEGES ON DATABASE koog TO admin;

-- Baza outbox jest tworzona automatycznie przez POSTGRES_DB

\echo 'Wszystkie bazy danych zostały utworzone pomyślnie!'
