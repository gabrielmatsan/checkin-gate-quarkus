CREATE TYPE event_status AS ENUM ('draft', 'published', 'cancelled', 'completed');

ALTER TABLE events ADD COLUMN status event_status NOT NULL DEFAULT 'draft';
