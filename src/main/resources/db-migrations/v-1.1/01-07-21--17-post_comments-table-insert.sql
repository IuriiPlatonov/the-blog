insert into post_comments(text, time, parent_id, post_id, user_id)
values ('Афтор! ДАВАЙ ЕЩЕ!', CAST('2021-07-01 15:34:09.000' AS DateTime), null, 1, 2),
       ('Ждите, скоро напишу шедевр.', CAST('2021-07-01 15:34:09.000' AS DateTime), 1, 1, 1),
       ('Посоветуйте, пожалуйста, как в googledocs форматировать абзац, чтобы wordtohtml.net превратил
        его в цитату blockqoute.', CAST('2021-07-01 15:34:09.000' AS DateTime), null, 4, 3),
       ('Класс, спасибо.', CAST('2021-07-01 15:34:09.000' AS DateTime), null, 4, 1),
       ('You\'re welcome', CAST('2021-07-01 15:34:09.000' AS DateTime), 4, 4, 2),
       ('В который раз повторю: хороший и интересный пост прочитают в любое время (даже утром 1-ого января)',
        CAST('2021-07-01 15:34:09.000' AS DateTime), null, 4, 3),
       ('Согласен', CAST('2021-07-01 15:34:09.000' AS DateTime), 7, 4, 2),
       ('А я не согласен', CAST('2021-07-01 15:34:09.000' AS DateTime), 7, 4, 3),
       ('Скучно', CAST('2021-07-01 15:34:09.000' AS DateTime), null, 1, 3),
       ('Но кажется я здесь один', CAST('2021-07-01 15:34:09.000' AS DateTime), null, 1, 3);
