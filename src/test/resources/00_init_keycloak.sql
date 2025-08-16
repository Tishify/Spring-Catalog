-- создаём пользователя и базу для Keycloak
DO
$$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'keycloak') THEN
      CREATE ROLE keycloak LOGIN PASSWORD 'keycloak';
   END IF;
END
$$;

CREATE DATABASE keycloak
  OWNER keycloak
  ENCODING 'UTF8'
  LC_COLLATE 'C'
  LC_CTYPE 'C'
  TEMPLATE template0;

GRANT ALL PRIVILEGES ON DATABASE keycloak TO keycloak;
