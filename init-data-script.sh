#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username postgres --dbname postgres <<-EOSQL
  CREATE TABLE users
  (
    userid character varying NOT NULL,
    password character varying NOT NULL,
    mateid character varying NOT NULL,
    telephone bigint NOT NULL,
    CONSTRAINT pkuserid PRIMARY KEY (userId)
  );

  CREATE TABLE secrets
  (
    secretid character varying NOT NULL,
    owner character varying NOT NULL,
    secret character varying NOT NULL,
    CONSTRAINT pksecretid PRIMARY KEY (secretid)
  );

  CREATE TABLE secret_perms
  (
    secretid character varying NOT NULL,
    userid character varying NOT NULL
  );

  CREATE TABLE sessions
  (
    userid character varying NOT NULL,
    sessionid character varying NOT NULL,
    ttl date
  );

  CREATE TABLE tokens
  (
    userid character varying NOT NULL,
    token character varying NOT NULL,
    ttl date
  );

  insert into users(userid, password, mateid, telephone) values
  ('user1','pass1','user2',31892839489),
  ('user2','pass2','user1',31892839489),
  ('user3','pass3','user2',31892839489);

  insert into secrets(secretid, owner, secret) values
  ('secret1','user1','shiiiii secret1'),
  ('secret2','user1','shiiiii secret2'),
  ('secret3','user2','shiiiii secret3'),
  ('secret4','user3','shiiiii secret4');

  insert into secret_perms(secretid, userid) values
  ('secret1','user2'),
  ('secret1','user3'),
  ('secret4','user2');

  insert into tokens(userid, token, ttl) values
  ('admin','admin1234',NULL);
EOSQL