INSERT INTO public.roles(role_id, role_name)
VALUES (1, 'client');
INSERT INTO public.roles(role_id, role_name)
VALUES (2, 'admin');
INSERT INTO public.users(user_id, email, name, role_id)
VALUES (1, 'user1@gmail.com', 'user1', 1);
INSERT INTO public.users(user_id, email, name, role_id)
VALUES (2, 'user2@gmail.com', 'user2', 1);
INSERT INTO public.items(item_name, item_price, item_description)
VALUES ('item1', 2, 'description item1');
INSERT INTO public.items(item_name, item_price, item_description)
VALUES ('item2', 3, 'description item2');
INSERT INTO public.items(item_name, item_price, item_description)
VALUES ('item3', 4, 'description item3');
INSERT INTO public.items(item_name, item_price, item_description)
VALUES ('item4', 5, 'description item4');
INSERT INTO public.orders(user_id, adding_time, total_cost)
VALUES (1, now(), 90);
INSERT INTO public.orders(user_id, adding_time, total_cost)
VALUES (2, now(), 90);