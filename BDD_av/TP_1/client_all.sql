DROP TABLE IF EXISTS client_hash CASCADE;
DROP TABLE IF EXISTS client_btree CASCADE;
DROP TABLE IF EXISTS client CASCADE;

CREATE TABLE client(
	numcli serial primary key,
	nom varchar(25),
	prenom varchar(25),
	prenom2 varchar(25),
	prenom3 varchar(25),
	age int,
	ville varchar(25),
	tel varchar(10)
);

CREATE TABLE client_hash(
	numcli serial primary key,
	nom varchar(25),
	prenom varchar(25),
	prenom2 varchar(25),
	prenom3 varchar(25),
	age int,
	ville varchar(25),
	tel varchar(10)
);


CREATE TABLE client_btree(
	numcli serial primary key,
	nom varchar(25),
	prenom varchar(25),
	prenom2 varchar(25),
	prenom3 varchar(25),
	age int,
	ville varchar(25),
	tel varchar(10)
);

create index b_age on client_btree using btree (age);
create index b_tel on client_btree using btree (tel);

create index h_age on client_hash using hash (age);
create index h_nom on client_hash using hash (tel);

---Filling table client :
\copy client_hash FROM 'clients_data.csv' csv;
\copy client FROM 'clients_data.csv' csv;
\copy client_btree FROM 'clients_data.csv' csv;