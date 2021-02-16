CREATE DATABASE animal_hospital_invoice;

CREATE TABLE pets 
(
        pet_id SERIAL,
        pet_name varchar(64) NOT NULL,
        owner_id int NOT NULL,
        
        CONSTRAINT pk_pets PRIMARY KEY (pet_id)

);


CREATE TABLE owners
(
        owner_id SERIAL,
        owner_first_name varchar(64),
        owner_last_name varchar (64),
        address_id int NOT NULL,
        
        CONSTRAINT pk_owners PRIMARY KEY (owner_id)

);

ALTER TABLE pets
ADD CONSTRAINT fk_pets FOREIGN KEY (owner_id) REFERENCES owners(owner_id);

CREATE TABLE addresses
(
        address_id SERIAL,
        city varchar(64) NOT NULL,
        street varchar(64) NOT NULL,
        district varchar(64) NOT NULL,
        postal_code varchar(64) NOT NULL,
        
        CONSTRAINT pk_addresses PRIMARY KEY (address_id)
);

CREATE TABLE owner_addresses
(
        address_id int NOT NULL,
        owner_id int NOT NULL,
        
        CONSTRAINT fk1_owner_addresses FOREIGN KEY (address_id) REFERENCES addresses(address_id),
        CONSTRAINT fk2_owner_addresses FOREIGN KEY (owner_id) REFERENCES owners(owner_id)
);


CREATE TABLE invoices
(
        invoice_id SERIAL,
        invoice_date DATE NOT NULL,
        owner_id int NOT NULL,
        invoice_amount money NOT NULL, 
        isPaid boolean NOT NULL,
        
        CONSTRAINT pk_invoices PRIMARY KEY (invoice_id),
        CONSTRAINT fk_invoices FOREIGN KEY (owner_id) REFERENCES owners(owner_id)
       
);

CREATE TABLE procedures
(
        procedure_id SERIAL,
        pet_id int NOT NULL,
        procedure_name varchar(64) NOT NULL,
        invoice_id int NOT NULL, -- procedures will be performed and invoiced simultaneously
        
        CONSTRAINT pk_procedures PRIMARY KEY (procedure_id),
        CONSTRAINT fk1_pet_id FOREIGN KEY (pet_id) REFERENCES pets(pet_id),
        CONSTRAINT fk2_invoice_id FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id)
       
);

CREATE TABLE pets_invoices

(

        pet_id int NOT NULL,
        invoice_id int NOT NULL,
        
        CONSTRAINT fk1_pets_invoices FOREIGN KEY (pet_id) REFERENCES pets(pet_id),
        CONSTRAINT fk2_pets_invoices FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id)


);


CREATE TABLE pets_procedures
(
        pet_id int NOT NULL,
        procedure_id int NOT NULL,
        cost_for_procedure money NOT NULL, -- different procedures seem to cost different amounts, however, we do not want to assume why that might be, so each instance of a pet getting a procedure will have an entered cost
     
        
        
        CONSTRAINT fk1_pets_procedures FOREIGN KEY (pet_id) REFERENCES pets(pet_id),
        CONSTRAINT fk2_pets_procedures FOREIGN KEY (procedure_id) REFERENCES procedures(procedure_id)
  
);



