AUTOCOMMIT OFF;

-- \* CREATE DATABASE Course_Manager; *\

CREATE TABLE Students
	( 
		StudentId integer NOT NULL,
		FirstName varchar(32) NOT NULL,
		LastName  varchar(32) NOT NULL
	);
ALTER TABLE Students
	ADD CONSTRAINT pk_Students PRIMARY KEY (StudentId);

-- \**************************************************************\

CREATE TABLE Subjects
	(
		SubjectCode character(6) NOT NULL,
		SubjectName varchar(32) NOT NULL
	);

ALTER TABLE Subjects
	ADD CONSTRAINT pk_Subjects PRIMARY KEY (SubjectCode);

-- \**************************************************************\

CREATE TABLE Assessments
	(
		SubjectCode character(6) NOT NULL,
		AssessmentCode character(4) NOT NULL,
		AssessmentWeight integer NOT NULL
	);
	
ALTER TABLE Assessments
	ADD CONSTRAINT pk_Assessments PRIMARY KEY (SubjectCode, AssessmentCode);
ALTER TABLE Assessments
	ADD CONSTRAINT Subjects_fk FOREIGN KEY (SubjectCode) REFERENCES Subjects (SubjectCode);

-- \**************************************************************\
	
CREATE TABLE Marks
	(
		StudentId integer NOT NULL,
		SubjectCode character(6) NOT NULL,
		AssessmentCode character(4) NOT NULL,
		Mark integer
	);

ALTER TABLE Marks
	ADD CONSTRAINT pk_Marks PRIMARY KEY (StudentId, SubjectCode, AssessmentCode);
ALTER TABLE Marks
	ADD CONSTRAINT Students_fk FOREIGN KEY (StudentId) REFERENCES Students (StudentId);
ALTER TABLE Marks
	ADD CONSTRAINT Assessments_fk FOREIGN KEY (SubjectCode, AssessmentCode) REFERENCES Assessments (SubjectCode, AssessmentCode);

-- \**************************************************************\
	
COMMIT;
	