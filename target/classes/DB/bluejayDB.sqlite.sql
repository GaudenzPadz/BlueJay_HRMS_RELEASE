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
	PRIMARY KEY("id"),
	FOREIGN KEY("department_id") REFERENCES "department"("department_id")
);
CREATE TABLE IF NOT EXISTS "attendance" (
	"id"	INTEGER,
	"date"	TEXT,
	"employee_id"	INTEGER,
	"name"	TEXT,
	"work_type"	TEXT,
	"status"	TEXT,
	"time_in"	INTEGER,
	"clock_IN_Note"	TEXT,
	"time_out"	INTEGER,
	"clock_OUT_Note"	TEXT,
	"overtime"	INTEGER,
	"grossPay"	INTEGER,
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "users" (
	"ID"	INTEGER,
	"employee_ID"	INTEGER,
	"name"	TEXT,
	"username"	TEXT,
	"password"	TEXT,
	"role"	TEXT,
	PRIMARY KEY("ID")
);
CREATE TABLE IF NOT EXISTS "employment_type" (
	"id"	INTEGER,
	"type"	TEXT NOT NULL UNIQUE,
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "employees" (
	"id"	INTEGER NOT NULL,
	"employee_id"	TEXT,
	"first_name"	TEXT,
	"middle_name"	TEXT,
	"last_name"	TEXT,
	"address"	TEXT,
	"department_id"	INTEGER,
	"employment_type_id"	INTEGER,
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
	PRIMARY KEY("id" AUTOINCREMENT),
	FOREIGN KEY("employment_type_id") REFERENCES "employment_type"("id"),
	FOREIGN KEY("work_type_id") REFERENCES "types"("id"),
	FOREIGN KEY("department_id") REFERENCES "department"("department_id")
);
INSERT INTO "deductions" ("id","SSS","PAG_IBIG","PHILHEALTH","advanced") VALUES (1,570,100,500,NULL);
INSERT INTO "department" ("department_id","department_name","department_description") VALUES (1,'Welding Department','Welders Department');
INSERT INTO "department" ("department_id","department_name","department_description") VALUES (2,'Human Resources Department','HR Team');
INSERT INTO "types" ("id","department_id","work_type","abbreviation","wage") VALUES (1,1,'Shielded Metal Arc Welding','SMAW',500);
INSERT INTO "types" ("id","department_id","work_type","abbreviation","wage") VALUES (2,1,'Gas Tungsten Arc Welding','GTAW',900);
INSERT INTO "types" ("id","department_id","work_type","abbreviation","wage") VALUES (3,1,'Flux-cored Arc Welding','FCAW',900);
INSERT INTO "types" ("id","department_id","work_type","abbreviation","wage") VALUES (4,1,'Gas Metal Arc Welding ','GMAW',1000);
INSERT INTO "types" ("id","department_id","work_type","abbreviation","wage") VALUES (5,2,'HR Head','HR',10000);
INSERT INTO "users" ("ID","employee_ID","name","username","password","role") VALUES (0,NULL,'ADMIN','admin','admin','ADMIN');
INSERT INTO "employment_type" ("id","type") VALUES (1,'Full Time');
INSERT INTO "employment_type" ("id","type") VALUES (2,'Part Time');
INSERT INTO "employment_type" ("id","type") VALUES (3,'Project Based');
COMMIT;