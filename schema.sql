CREATE DATABASE IF NOT EXISTS productivity_coach;
USE productivity_coach;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    preferences TEXT,
    goals TEXT
);

CREATE TABLE IF NOT EXISTS tasks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    user_id INT NOT NULL,
    category VARCHAR(50),
    priority VARCHAR(20),
    deadline DATE,
    status VARCHAR(20),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS productivity_sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    task_id INT NOT NULL,
    start_time DATETIME,
    end_time DATETIME,
    duration_minutes INT,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);
