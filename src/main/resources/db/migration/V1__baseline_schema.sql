-- Initial database schema baseline
-- This migration creates the baseline for existing database schema

-- User table already exists, this is just documentation
-- CREATE TABLE IF NOT EXISTS `user` (
--     id BIGINT PRIMARY KEY AUTO_INCREMENT,
--     full_name VARCHAR(100),
--     email VARCHAR(255) UNIQUE NOT NULL,
--     password VARCHAR(255),
--     project_size INT DEFAULT 0
-- );

-- Add indexes for performance
CREATE INDEX IF NOT EXISTS idx_user_email ON `user`(email);

-- Project table indexes
CREATE INDEX IF NOT EXISTS idx_project_name ON project(name);
CREATE INDEX IF NOT EXISTS idx_project_category ON project(category);

-- Issue table indexes
CREATE INDEX IF NOT EXISTS idx_issue_status ON issue(status);
CREATE INDEX IF NOT EXISTS idx_issue_priority ON issue(priority);
CREATE INDEX IF NOT EXISTS idx_issue_project_id ON issue(projectId);
CREATE INDEX IF NOT EXISTS idx_issue_due_date ON issue(dueDate);

