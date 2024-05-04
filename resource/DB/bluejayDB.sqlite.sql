BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "deductions" (
	"id"	INTEGER,
	"SSS"	INTEGER,
	"PAG_IBIG"	INTEGER,
	"PHILHEALTH"	INTEGER,
	"advanced"	INTEGER,
	PRIMARY KEY("id")
);
CREATE TABLE IF NOT EXISTS "department" (
	"department_id"	INTEGER,
	"department_name"	TEXT,
	"department_description"	TEXT,
	PRIMARY KEY("department_id")
);
CREATE TABLE IF NOT EXISTS "employees" (
	"id"	INTEGER NOT NULL,
	"employee_id"	TEXT,
	"first_name"	TEXT,
	"middle_name"	TEXT,
	"last_name"	TEXT,
	"address"	TEXT,
	"department_id"	INTEGER,
	"work_type_id"	INTEGER,
	"rate"	INTEGER,
	"grossPay"	INTEGER,
	"netPay"	INTEGER,
	"gender"	TEXT,
	"tel_number"	TEXT,
	"email"	TEXT,
	"profile_image"	BLOB,
	"date_hired"	INTEGER,
	"DOB"	INTEGER,
	FOREIGN KEY("work_type_id") REFERENCES "types"("id"),
	FOREIGN KEY("department_id") REFERENCES "department"("department_id"),
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "payroll" (
	"id"	INTEGER,
	"employee_id"	INTEGER,
	"name"	TEXT,
	"Department"	TEXT,
	"workType"	TEXT,
	"grossPay"	INTEGER,
	"ratePerDay"	REAL,
	"daysWorked"	REAL,
	"overtimeHours"	REAL,
	"bonus"	REAL,
	"totalDeduction"	REAL,
	"netPay"	REAL,
	"created_at"	INTEGER,
	"updated_at"	INTEGER,
	PRIMARY KEY("id")
);
CREATE TABLE IF NOT EXISTS "types" (
	"id"	INTEGER,
	"department_id"	INTEGER,
	"work_type"	TEXT,
	"abbreviation"	TEXT,
	"wage"	INTEGER,
	FOREIGN KEY("department_id") REFERENCES "department"("department_id"),
	PRIMARY KEY("id")
);
CREATE TABLE IF NOT EXISTS "users" (
	"ID"	INTEGER,
	"name"	TEXT,
	"username"	TEXT,
	"password"	TEXT,
	"role"	TEXT,
	PRIMARY KEY("ID")
);
CREATE TABLE IF NOT EXISTS "attendance" (
	"id"	INTEGER,
	"date"	TEXT,
	"employee_id"	INTEGER,
	"name"	TEXT,
	"status"	TEXT,
	"time_in"	INTEGER,
	"time_out"	INTEGER,
	"overtime"	INTEGER,
	"note"	TEXT,
	"work_type"	TEXT,
	"clock_OUT_Note"	TEXT,
	"clock_IN_Note"	TEXT,
	PRIMARY KEY("id" AUTOINCREMENT)
);
INSERT INTO "deductions" ("id","SSS","PAG_IBIG","PHILHEALTH","advanced") VALUES (1,570,100,500,NULL);
INSERT INTO "department" ("department_id","department_name","department_description") VALUES (1,'Welding Department','Welders Department');
INSERT INTO "department" ("department_id","department_name","department_description") VALUES (2,'Human Resources Department','HR Team');
INSERT INTO "types" ("id","department_id","work_type","abbreviation","wage") VALUES (1,1,'Shielded Metal Arc Welding','SMAW',500);
INSERT INTO "types" ("id","department_id","work_type","abbreviation","wage") VALUES (2,1,'Gas Tungsten Arc Welding','GTAW',900);
INSERT INTO "types" ("id","department_id","work_type","abbreviation","wage") VALUES (3,1,'Flux-cored Arc Welding','FCAW',900);
INSERT INTO "types" ("id","department_id","work_type","abbreviation","wage") VALUES (4,1,'Gas Metal Arc Welding ','GMAW',1000);
INSERT INTO "types" ("id","department_id","work_type","abbreviation","wage") VALUES (5,2,'HR Head','HR',10000);
INSERT INTO "users" ("ID","name","username","password","role") VALUES (1,'USER','user','user','Employee');
INSERT INTO "users" ("ID","name","username","password","role") VALUES (2,'ADMIN','admin','admin','ADMIN');
COMMIT;
