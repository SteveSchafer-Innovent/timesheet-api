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
select * from netsuite_event where notes like '%mantis%1218%';
create table netsuite_event_1218 like netsuite_event;
insert into netsuite_event_1218 select * from netsuite_event where notes like '%mantis%1218%';
select * from netsuite_event_1218;
alter table netsuite_event_1218 add comma_count int;
alter table netsuite_event drop comma_count;
select notes, hours, length(notes) - length(replace(notes, ',', '')) from netsuite_event_1218;
update netsuite_event_1218 set comma_count = length(notes) - length(replace(notes, ',', ''));
select hours, hours / (comma_count + 1) from netsuite_event_1218;
select sum(hours), sum(hours / (comma_count + 1)) from netsuite_event_1218;

select * from project where code = '1218'
and parent_id = (select id from project where code = 'mantis'
and parent_id = (select id from project where code = 'boonli' 
and parent_id = (select id from project where code = 'innovent')))
;

select * from event e
inner join ((select event_id from event_project 
where project_id = (select id from project where code = '1218'
and parent_id = (select id from project where code = 'mantis'
and parent_id = (select id from project where code = 'boonli' 
and parent_id = (select id from project where code = 'innovent')))))) ep
on e.id = ep.event_id
order by e.time;

select max(id) from event;
ALTER TABLE timesheet.project ADD bigtime_project_id INT;
describe timesheet.project;

select * from netsuite_event;
select sum(hours) from netsuite_event where date = '2021-04-08';
select date, sum(hours) from netsuite_event group by date order by date;

alter table project add minimum_billable_hours float;
alter table project add round_daily_hours_to float;