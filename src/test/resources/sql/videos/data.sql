TRUNCATE TABLE video RESTART IDENTITY CASCADE;

insert into video(id, name, user_id)
values (1, 'test1', 1),
       (2, 'test2', 2);

SELECT setval('video_id_seq', 3, false);