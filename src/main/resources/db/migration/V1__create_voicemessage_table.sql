CREATE TABLE IF NOT EXISTS voice_message (
  id         UUID PRIMARY KEY,
  stream_id  VARCHAR(64) DEFAULT NULL,
  username   VARCHAR(64) NOT NULL,
  email      VARCHAR(128) NOT NULL,
  status     VARCHAR(32) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_voice_message_status_created
  ON voice_message(status, created_at);

CREATE INDEX IF NOT EXISTS idx_voice_message_stream_status
  ON voice_message(stream_id, status);