use timesheet;

drop table if exists event;
create table timesheet.event (
	id int NOT NULL auto_increment,
	time datetime NOT NULL,
	comment text(65535),
	offset tinyint(3) DEFAULT 0,
	user int NOT NULL,
	datetime datetime,
	PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


drop table if exists project_date;
create table project_date (
	user_id int not null,
	project_id int not null,
	date date not null,
	time bigint not null,
	checked boolean not null,
	primary key (user_id, project_id, date)
);

CREATE TABLE file (
	id int NOT NULL auto_increment,
	name varchar(128),
	PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE upload (
	id int NOT NULL auto_increment,
	file_id int NOT NULL,
	date_time datetime NOT NULL,
	PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE netsuite_client (
	id int NOT NULL auto_increment,
	name varchar(64) NOT NULL,
	PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE netsuite_project (
	id int NOT NULL auto_increment,
	client_id int NOT NULL,
	name varchar(64) NOT NULL,
	PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE netsuite_task (
	id int NOT NULL auto_increment,
	project_id int NOT NULL,
	name varchar(64) NOT NULL,
	PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE .netsuite_event (
	id int NOT NULL auto_increment,
	date date NOT NULL,
	task_id int NOT NULL,
	notes text(65535),
	hours float,
	upload_id int NOT NULL,
	PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

show tables;
select * from project_date;
select max(id) from netsuite_event;
select * from file where id > 131;
select * from upload;
select * from netsuite_event where id > 2077;

