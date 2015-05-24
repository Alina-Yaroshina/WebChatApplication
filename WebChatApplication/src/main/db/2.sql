/*
-- Query: SELECT * FROM messages WHERE user_id = (SELECT id FROM users WHERE name = "alex")
LIMIT 0, 1000

-- Date: 2015-05-24 21:43
*/
INSERT INTO `messages` (`id`,`text`,`date`,`user_id`) VALUES (4,'hello','06-05-2015, 17:41:13',4);
INSERT INTO `messages` (`id`,`text`,`date`,`user_id`) VALUES (8,'pretty cool :3','06-05-2015, 17:42:10',4);
INSERT INTO `messages` (`id`,`text`,`date`,`user_id`) VALUES (13,'sorry guys, I am busy now(','06-05-2015, 17:43:12',4);
INSERT INTO `messages` (`id`,`text`,`date`,`user_id`) VALUES (15,'tomorrow is my deadline','06-05-2015, 17:43:30',4);
