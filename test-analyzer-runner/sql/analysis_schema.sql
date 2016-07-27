-- DROP VIEW IF EXISTS V_Name_Mapping;
-- DROP TABLE IF EXISTS Method_Info;
-- DROP TABLE IF EXISTS Testcase_Info;
-- DROP TABLE IF EXISTS Relation_Info;
-- DROP TABLE IF EXISTS Test_Result_Info;
-- DROP TABLE IF EXISTS Method_Test_Abort_Info;

CREATE TABLE Method_Info
(
	methodId int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	execution varchar(5) NOT NULL,
	method varchar(1024) NOT NULL,
    instructions INT(8),
    modifier VARCHAR(10)
);

CREATE TABLE Testcase_Info
(
	testcaseId int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	execution varchar(5) NOT NULL,
	testcase varchar(1024) NOT NULL,
    instructions INT(8),
    assertions INT(8)
);

CREATE TABLE Relation_Info
(
	relationId int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	execution varchar(5) NOT NULL,
	methodId int(11) NOT NULL,
	testcaseId int(11) NOT NULL,
	minStackDistance INT(8),
	maxStackDistance INT(8)
);

CREATE TABLE Test_Result_Info
(
	resultId int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	execution varchar(5) NOT NULL,
	relationId int(11) NOT NULL,
	retValGen varchar(256) NOT NULL,
	killed tinyint(1) NOT NULL
);

CREATE TABLE Method_Test_Abort_Info
(
	abortId int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	execution varchar(5) NOT NULL,
	methodId int(11) NOT NULL,
	retValGen varchar(256) NOT NULL
);

/* Mapping between relationId, methodId, testcaseId and method (name) and testcase (name). */
CREATE VIEW V_Name_Mapping
(
	execution, 
	relationId, 
	methodId, 
	testcaseId, 
	method, 
	testcase
) AS
	SELECT ri.execution, ri.relationId, ri.methodId, ri.testcaseId, mi.method, ti.testcase
	FROM Relation_Info ri
	INNER JOIN Method_Info mi
	ON ri.execution = mi.execution
	AND ri.methodId = mi.methodId
	INNER JOIN Testcase_Info ti
	ON ri.execution = ti.execution
	AND ri.testcaseId = ti.testcaseId;
	
CREATE INDEX idx_aly_mi_1 ON Method_Info(execution);
CREATE INDEX idx_aly_mi_2 ON Method_Info(method(50));
CREATE INDEX idx_aly_ti_1 ON Testcase_Info(execution);
CREATE INDEX idx_aly_ti_2 ON Testcase_Info(testcase(50));
CREATE INDEX idx_aly_ri_1 ON Relation_Info(execution);
CREATE INDEX idx_aly_ri_2 ON Relation_Info(methodId);
CREATE INDEX idx_aly_ri_3 ON Relation_Info(testcaseId);
CREATE INDEX idx_aly_tri_1 ON Test_Result_Info(execution);
CREATE INDEX idx_aly_tri_2 ON Test_Result_Info(relationId);