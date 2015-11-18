AUTOCOMMIT OFF;

CREATE DATABASE Course_Manager;

CREATE TABLE Students
	( 
		StudentId integer NOT NULL,
		FirstName varchar(255) NOT NULL,
		LastName  varchar(255) NOT NULL,
	);
ALTER TABLE Students
	ADD CONSTRAINT pk_Students PRIMARY KEY (StudentId);

-- \**************************************************************\

CREATE TABLE Subjects
	(
		SubjectCode character(6) NOT NULL,
		SubjectName varchar(255) NOT NULL,
	);

ALTER TABLE Subjects
	ADD CONSTRAINT pk_Subjects PRIMARY KEY (SubjectCode);

-- \**************************************************************\

CREATE TABLE Assessments
	(
		SubjectCode character(6) NOT NULL,
		AssessmentCode character(4) NOT NULL,
		AssessmentWeight integer NOT NULL,
	);
	
ALTER TABLE Assessments
	ADD CONSTRAINT pk_Assessments PRIMARY KEY (SubjectCode, AssessmentCode);
	ADD CONSTRAINT Subjects_fk FOREIGN KEY (SubjectCode) REFERENCES Subjects (SubjectCode);

-- \**************************************************************\
	
CREATE TABLE Marks
	(
		StudentId integer NOT NULL,
		AssessmentCode character(4) NOT NULL,
		Mark integer
	);