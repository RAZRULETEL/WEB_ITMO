CREATE TABLE IF NOT EXISTS "users" (
  "login" text PRIMARY KEY NOT NULL,
  "password" text NOT NULL
);

CREATE TABLE IF NOT EXISTS "points" (
  "id" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  "x" double precision NOT NULL,
  "y" double precision NOT NULL,
  "r" double precision NOT NULL,
  "timestamp" bigint NOT NULL,
  "execution_time" double precision NOT NULL,
  "success" boolean NOT NULL
);