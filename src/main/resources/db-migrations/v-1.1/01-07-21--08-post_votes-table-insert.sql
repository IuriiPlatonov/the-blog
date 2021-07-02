insert into post_votes(time, value, post_id, user_id)
values (CAST('2021-07-01 15:34:09.000' AS DateTime), 1, 1, 2),
       (CAST('2021-07-01 15:34:09.000' AS DateTime), -1, 1, 1),
       (CAST('2021-07-01 15:34:09.000' AS DateTime), 1, 1, 3),
       (CAST('2021-07-01 15:34:09.000' AS DateTime), -1, 1, 2),
       (CAST('2021-07-01 15:34:09.000' AS DateTime), 1, 4, 1),
       (CAST('2021-07-01 15:34:09.000' AS DateTime), 1, 4, 3);
