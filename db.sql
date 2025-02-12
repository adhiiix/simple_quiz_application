create database quiz;
create table questions (
	question_id int,
	questions varchar(100),
	option1 varchar(50),
	option2 varchar(50),
	option3 varchar(50),
	option4 varchar(50)
);

insert into questions VALUES ('1','what is java ?','Programming Language','Game','Software','None Of These');