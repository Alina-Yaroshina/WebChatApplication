/*
-- Query: SELECT * FROM messages WHERE user_id = (SELECT id FROM users WHERE name = 'alina') AND date LIKE ('06-05-2015%')
LIMIT 0, 1000

-- Date: 2015-05-24 21:57
*/
INSERT INTO `messages` (`id`,`text`,`date`,`user_id`) VALUES (1,'hello!!!','06-05-2015, 17:40:01',1);
INSERT INTO `messages` (`id`,`text`,`date`,`user_id`) VALUES (6,'how you doing?','06-05-2015, 17:41:40',1);
INSERT INTO `messages` (`id`,`text`,`date`,`user_id`) VALUES (11,'me too!','06-05-2015, 17:42:45',1);
