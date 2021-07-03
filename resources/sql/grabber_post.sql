/*Создаем таблицу данных хранения информации о вакансиях сайта  
https://www.sql.ru/forum/job-offers */

CREATE TABLE post (
	id SERIAL PRIMARY KEY,
	name VARCHAR(255), -- имя вакансии
	text TEXT, -- текст вакансии
	link VARCHAR(255) UNIQUE, --  ссылка на вакансию
	created TIMESTAMP -- дата первого поста
);	