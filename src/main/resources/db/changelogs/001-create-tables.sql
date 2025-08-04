--liquibase formatted sql

--changeset dmitry_lysenko:001
CREATE TABLE role (
                      role_id SERIAL PRIMARY KEY,
                      role_name VARCHAR(100) NOT NULL UNIQUE
);
--rollback DROP TABLE IF EXISTS role;

--changeset dmitry_lysenko:002
CREATE TABLE "user" (
                        user_id SERIAL PRIMARY KEY,
                        email VARCHAR(150) NOT NULL UNIQUE,
                        name VARCHAR(100) NOT NULL,
                        role_id INT,
                        CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES role(role_id)
                            ON DELETE SET NULL
                            ON UPDATE CASCADE
);
--rollback DROP TABLE IF EXISTS "user";

--changeset dmitry_lysenko:003
CREATE TABLE item (
                      item_id SERIAL PRIMARY KEY,
                      item_name VARCHAR(200) NOT NULL,
                      item_price NUMERIC(10, 2) NOT NULL CHECK (item_price >= 0),
                      item_description TEXT,
                      item_photo TEXT
);
--rollback DROP TABLE IF EXISTS item;

--changeset dmitry_lysenko:004
CREATE TABLE "order" (
                         order_id SERIAL PRIMARY KEY,
                         user_id INT NOT NULL,
                         adding_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         total_cost NUMERIC(10, 2) NOT NULL CHECK (total_cost >= 0),
                         item_details JSONB NOT NULL,
                         CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES "user"(user_id)
                             ON DELETE CASCADE
                             ON UPDATE CASCADE
);
--rollback DROP TABLE IF EXISTS "order";

--changeset dmitry_lysenko:005
CREATE TABLE order_item (
                            order_id INT NOT NULL,
                            item_id INT NOT NULL,
                            PRIMARY KEY (order_id, item_id),
                            CONSTRAINT fk_orderitem_order FOREIGN KEY (order_id) REFERENCES "order"(order_id)
                                ON DELETE CASCADE
                                ON UPDATE CASCADE,
                            CONSTRAINT fk_orderitem_item FOREIGN KEY (item_id) REFERENCES item(item_id)
                                ON DELETE CASCADE
                                ON UPDATE CASCADE
);
--rollback DROP TABLE IF EXISTS order_item;