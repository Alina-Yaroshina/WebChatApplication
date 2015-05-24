/*
-- Query: SELECT * FROM messages WHERE user_id = (SELECT id FROM users WHERE name = 'alina') AND text LIKE ('%hello%')
LIMIT 0, 1000

-- Date: 2015-05-24 21:58
*/
INSERT INTO `messages` (`id`,`text`,`date`,`user_id`) VALUES (1,'hello!!!','06-05-2015, 17:40:01',1);
