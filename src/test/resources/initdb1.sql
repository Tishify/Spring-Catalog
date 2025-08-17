--liquibase formatted sql


CREATE TABLE "roles" (
                      role_id SERIAL PRIMARY KEY,
                      role_name VARCHAR(100) NOT NULL UNIQUE
);
--rollback DROP TABLE IF EXISTS role;

--changeset dmitry_lysenko:002
CREATE TABLE "users" (
                        user_id SERIAL PRIMARY KEY,
                        email VARCHAR(150) NOT NULL UNIQUE,
                        name VARCHAR(100) NOT NULL,
                        role_id INT,
                        CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES "roles"(role_id)
                            ON DELETE SET NULL
                            ON UPDATE CASCADE
);
--rollback DROP TABLE IF EXISTS "users";


CREATE TABLE "items" (
                      item_id SERIAL PRIMARY KEY,
                      item_name VARCHAR(200) NOT NULL,
                      item_price NUMERIC(10, 2) NOT NULL CHECK (item_price >= 0),
                      item_description TEXT
);
--rollback DROP TABLE IF EXISTS items;


CREATE TABLE "orders" (
                         order_id SERIAL PRIMARY KEY,
                         user_id INT NOT NULL,
                         adding_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         total_cost NUMERIC(10, 2) NOT NULL CHECK (total_cost >= 0),
                         CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES "users"(user_id)
                             ON DELETE CASCADE
                             ON UPDATE CASCADE
);
--rollback DROP TABLE IF EXISTS "orders";

--changeset dmitry_lysenko:005
CREATE TABLE "order_items" (
                            order_id INT NOT NULL,
                            item_id INT NOT NULL,
                            PRIMARY KEY (order_id, item_id),
                            CONSTRAINT fk_orderitem_order FOREIGN KEY (order_id) REFERENCES "orders"(order_id)
                                ON DELETE CASCADE
                                ON UPDATE CASCADE,
                            CONSTRAINT fk_orderitem_item FOREIGN KEY (item_id) REFERENCES "items"(item_id)
                                ON DELETE CASCADE
                                ON UPDATE CASCADE
);

--rollback DROP TABLE IF EXISTS order_items;\

CREATE TABLE "item_images" (
                            image_id SERIAL PRIMARY KEY,
                            item_id INT NOT NULL,
                            image BYTEA,
                            CONSTRAINT fk_itemimages_item FOREIGN KEY (item_id) REFERENCES "items"(item_id)
                                ON DELETE CASCADE
                                ON UPDATE CASCADE
);