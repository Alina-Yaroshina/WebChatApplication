create table messages(
  id varchar(20) NOT NULL,
  user varchar(80) NOT NULL,
  messageText text NOT NULL,
  date datetime NOT NULL,
  edited boolean NOT NULL default 0,
  deleted boolean NOT NULL default 0,
  PRIMARY KEY (id)
);