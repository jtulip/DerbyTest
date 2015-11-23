-- /* Create Students table */

CREATE TABLE Students
	( 
		StudentId integer NOT NULL,
		FirstName varchar(32) NOT NULL,
		LastName  varchar(32) NOT NULL
	);
	
ALTER TABLE Students
	ADD CONSTRAINT pk_Students PRIMARY KEY (StudentId);



-- /* Create Subjects table */

CREATE TABLE Subjects
	(
		SubjectCode character(6) NOT NULL,
		SubjectName varchar(64) NOT NULL
	);
ALTER TABLE Subjects
	ADD CONSTRAINT pk_Subjects PRIMARY KEY (SubjectCode);

	

-- /* Create Enrolments table */

CREATE TABLE Enrolments
	(
		SubjectCode character(6) NOT NULL,
		StudentId integer NOT NULL
	);
ALTER TABLE Enrolments
	ADD CONSTRAINT pk_Enrolments PRIMARY KEY (SubjectCode, StudentId);
ALTER TABLE Enrolments
	ADD CONSTRAINT Enrol_Subjects_fk FOREIGN KEY (SubjectCode) REFERENCES Subjects (SubjectCode);
ALTER TABLE Enrolments
	ADD CONSTRAINT Enrol_Students_fk FOREIGN KEY (StudentId) REFERENCES Students (StudentId);

	

-- /* Create Assessments table */

CREATE TABLE Assessments
	(
		SubjectCode character(6) NOT NULL,
		AssessmentCode character(4) NOT NULL,
		AssessmentWeight integer NOT NULL
	);
ALTER TABLE Assessments
	ADD CONSTRAINT pk_Assessments PRIMARY KEY (SubjectCode, AssessmentCode);
ALTER TABLE Assessments
	ADD CONSTRAINT Assess_Subjects_fk FOREIGN KEY (SubjectCode) REFERENCES Subjects (SubjectCode);



-- /* Create Marks table */

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
	ADD CONSTRAINT Assessments_fk FOREIGN KEY (SubjectCode, AssessmentCode) 
		REFERENCES Assessments (SubjectCode, AssessmentCode);


-- /* Insert test data */

INSERT INTO Subjects VALUES 
	('ITC203','OOSAD'), 
	('ITC205','PPP');
	
INSERT INTO Students VALUES 
	(1,'Jim', 'Brown'), 
	(2, 'Jack', 'Black'), 
	(3, 'Mary', 'Contrary');
	
INSERT INTO Enrolments VALUES 
	('ITC203',1), 
	('ITC203',3), 
	('ITC205',2),
	('ITC205',3);
	

