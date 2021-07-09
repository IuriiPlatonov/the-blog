insert into users(code, email, is_moderator, name, password, photo, reg_time)
values ('kitty', 'pupkin@gmail.com', 1, 'Василий Пупкин',
        '$2y$12$q0FLX6yp0w87cJ83vlh6yOJ9H1Ermzcd9pcfn0/cRqcGbV/iSSUl2',
        'https://sun9-74.userapi.com/impg/8pDqtWW3j09akamEQvA0bGhmiyUAkPan9x49xQ/T8akA2ZvwDY.jpg?size=604x604&quality=96&sign=637e11c91e8c33fdfab55bd0b862d30b&type=album',
        CAST('2021-06-30 10:34:09.000' AS DateTime)),
       ('mother', 'lukyanenko@gmail.com', 0, 'Сергей Лукьяненко',
        '$2y$12$q0FLX6yp0w87cJ83vlh6yOJ9H1Ermzcd9pcfn0/cRqcGbV/iSSUl2',
        null, CAST('2021-07-01 02:01:10.000' AS DateTime)),
       ('HastaLaVista', 'arnold@gmail.com', 0, 'Арнольд Швайцнегер',
        '$2y$12$q0FLX6yp0w87cJ83vlh6yOJ9H1Ermzcd9pcfn0/cRqcGbV/iSSUl2',
        'https://images11.esquire.ru/upload/article/0dd/0ddad70a4db491873d0fbd98aef11436.jpg',
        CAST('2021-07-01 02:05:10.000' AS DateTime));