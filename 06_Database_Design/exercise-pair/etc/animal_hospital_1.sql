--BEGIN TRANSACTION;

--DROP DATABASE IF EXISTS animal_hospital_for_hh_report;
CREATE DATABASE animal_hospital_for_hh_report;
--each visit occurs on one day. If an owner comes in twice in the same day, that is two separate visits
--DROP TABLE IF EXISTS pets;
CREATE TABLE pets
(
        pet_id SERIAL,
        pet_name varchar(64) NOT NULL,
        pet_type varchar(64) NOT NULL,
        pet_age int NULL,
        owner_id int NOT NULL,
        
        CONSTRAINT pk_pets PRIMARY KEY (pet_id)
        --CONSTRAINT fk_owner_id FOREIGN KEY (owner_id) REFERENCES owners(owner_id)
);
--DROP TABLE IF EXISTS owners;
CREATE TABLE owners
(
        owner_id SERIAL,
        owner_first_name varchar(64) NOT NULL,
        owner_last_name varchar(64) NOT NULL, --we do not cater to Cher or Madonna
        
        CONSTRAINT pk_owners PRIMARY KEY (owner_id)
        
);

ALTER TABLE pets
ADD CONSTRAINT fk_owner_id FOREIGN KEY (owner_id) REFERENCES owners(owner_id);


--DROP TABLE IF EXISTS visits;
CREATE TABLE visits 
(
        visit_id SERIAL,
        visit_date DATE NOT NULL,
        owner_id int NOT NULL,
        
        CONSTRAINT pk_visits PRIMARY KEY (visit_id)

);

--DROP TABLE IF EXISTS pets_visits;
CREATE TABLE pets_visits
(

        pet_id int NOT NULL,
        visit_id int NOT NULL,
        
        CONSTRAINT fk1_pets_visits_id FOREIGN KEY (pet_id) REFERENCES pets(pet_id),
         CONSTRAINT fk2_pets_visits_id FOREIGN KEY (visit_id) REFERENCES visits(visit_id)

);

CREATE TABLE procedures
(
        procedure_id SERIAL,
        procedure_name varchar(64) NOT NULL,
        
        CONSTRAINT pk_procedures PRIMARY KEY (procedure_id)

);

CREATE TABLE visits_procedures
(

        visit_id int NOT NULL,
        procedure_id int NOT NULL,
        
        CONSTRAINT fk1_visits_procedures_id FOREIGN KEY (procedure_id) REFERENCES procedures(procedure_id),
         CONSTRAINT fk2_visits_procedures_id FOREIGN KEY (visit_id) REFERENCES visits(visit_id)
        
        
);


CREATE TABLE pets_procedures
(
      procedure_id int NOT NULL,
        pet_id int NOT NULL,

        CONSTRAINT fk1_pets_procedures_id FOREIGN KEY (procedure_id) REFERENCES procedures(procedure_id),
         CONSTRAINT fk2_pets_procedures_id FOREIGN KEY (pet_id) REFERENCES pets(pet_id)
);