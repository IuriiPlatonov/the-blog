insert into posts(is_active, moderation_status, text, time, title, user_id, view_count, moderator_id)
values (1, 'ACCEPTED', '1-го января в 6 утра воскресенья мне пришла мысль поделиться с Хабра-сообществом о том, как
        писать статьи на Хабр, чтобы они попадали в Лучшее. За сутки, за неделю, месяц, и если вы сможете взломать 3000
        паролей или сделать комикс в духе Фриланс vs. Офис, то и в лучшее за все время!',
        CAST('2021-06-30 01:34:09.000' AS DateTime), 'Статья номер 1', 1, 7, 1),
       (0, 'NEW', 'А официально, началось все с Хабрахабра в 2011-ом году. Когда я, задолбавшийся разбирать индусские
        С++-вермишелины, отрапортовал в песочницу пост про избыточность С++. При этом не сильно рассчитывая на фидбек
        или инвайт, а что называется — просто выговориться. Каково было мое удивление, когда через пару дней мне прилетел
        не один, а 3 инвайта. Сам пост взлетел в топ Хабра получив 275 плюсов и висит в “С++ / Лучшее” до сих пор.',
        CAST('2021-07-01 05:34:09.000' AS DateTime), 'Статья номер 2', 2, 0, null),
       (0, 'DECLINED', 'Кто я такой чтобы не пить советовать? — Спросите вы. Не вдаваясь в фаллометрию, я просто люблю
        писать про IT, а зарабатываю на жизнь разработкой на .NET. За что Microsoft (хотя не только за это), выдал мне
        ачивку MVP и это мотивирует писать дальше.', CAST('2021-07-01 10:34:09.000' AS DateTime), 'Статья номер 3'
        , 3, 0, 1),
       (1, 'ACCEPTED', 'Так я встал на путь любительского IT-блоггерства. Потом были попытки создать свои тематические
        блоги про мобильную разработку, стартапы и IT-бизнес. Но времени постоянно постить и PR-ить блог не было,
        соответственно и трафика тоже полтора человека в день. Поэтому продолжал постить туда, где уже есть аудитория.
        Был посты на Цукерберг Позвонит (VC.ru), AIN.ua, Geektimes.ru, где-то еще писал, как пить дать. В общей
        сложности у меня более 50-ти публикаций на тему IT.', CAST('2021-07-01 15:34:09.000' AS DateTime),
        'Статья номер 4', 3, 2, 1);
