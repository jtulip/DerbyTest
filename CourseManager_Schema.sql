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
		Mark float DEFAULT 0.0
	);
ALTER TABLE Marks
	ADD CONSTRAINT pk_Marks PRIMARY KEY (StudentId, SubjectCode, AssessmentCode);
ALTER TABLE Marks
	ADD CONSTRAINT Students_fk FOREIGN KEY (StudentId) REFERENCES Students (StudentId);
ALTER TABLE Marks
	ADD CONSTRAINT Assessments_fk FOREIGN KEY (SubjectCode, AssessmentCode) 
		REFERENCES Assessments (SubjectCode, AssessmentCode);
ALTER TABLE Marks
	ADD CONSTRAINT Mark_In_Range CHECK (Mark >= 0.0 AND Mark <= 100.0);


-- /* Insert test data */

INSERT INTO Subjects VALUES 
	('ITC203','Object Oriented Systems and Deesign'), 
	('ITC205','Professional Programming Practice'),
	('ITC303','Software Development Project 1');
	
INSERT INTO Students VALUES 
	(1,'Jim', 'Brown'), 
	(2, 'Jack', 'Black'), 
	(3, 'Mary', 'Contrary'),
	(4, 'Fred', 'Nurke'),
	(5, 'Edward', 'Seagoon');
	
INSERT INTO Enrolments VALUES 
	('ITC203',1), 
	('ITC203',3), 
	('ITC203',4),
	('ITC203',5),
	('ITC205',1),
	('ITC205',2),
	('ITC205',4),
	('ITC205',5),
	('ITC303',3),
	('ITC303',5),
	('ITC303',2);
	
INSERT INTO Assessments VALUES 
	('ITC203','Asg1',25), 
	('ITC203','Asg2',25), 
	('ITC203','Asg3',25),
	('ITC203','Prac',25),
	('ITC205','Asg1',30),
	('ITC205','Asg2',30),
	('ITC205','Exam',40),
	('ITC303','IOCM',25),
	('ITC303','PREM',25),
	('ITC303','PMAS',25);

INSERT INTO Marks VALUES 
	(1,'ITC203','Asg1',0), 
	(1,'ITC203','Asg2',0), 
	(1,'ITC203','Asg3',0), 
	(1,'ITC203','Prac',0),
	(4,'ITC203','Asg1',0), 
	(4,'ITC203','Asg2',0), 
	(4,'ITC203','Asg3',0), 
	(4,'ITC203','Prac',0),
	(5,'ITC203','Asg1',0), 
	(5,'ITC203','Asg2',0), 
	(5,'ITC203','Asg3',0), 
	(5,'ITC203','Prac',0),
	(3,'ITC203','Asg1',0), 
	(3,'ITC203','Asg2',0), 
	(3,'ITC203','Asg3',0), 
	(3,'ITC203','Prac',0);

