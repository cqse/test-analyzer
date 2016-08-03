-- DROP VIEW IF EXISTS V_Test_Result_Info;
-- DROP VIEW IF EXISTS V_Method_State_Info_Extended;
-- DROP VIEW IF EXISTS V_Method_State_Info;
-- DROP VIEW IF EXISTS V_Tested_Methods_Info;
-- DROP VIEW IF EXISTS V_Name_Mapping;
-- DROP TABLE IF EXISTS Method_Info;
-- DROP TABLE IF EXISTS Testcase_Info;
-- DROP TABLE IF EXISTS Relation_Info;
-- DROP TABLE IF EXISTS RetValGen_Info;
-- DROP TABLE IF EXISTS Test_Result_Info;
-- DROP TABLE IF EXISTS Method_Test_Abort_Info;

CREATE TABLE Method_Info
(
	methodId INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	execution VARCHAR(5) NOT NULL,
	method VARCHAR(1024) NOT NULL COLLATE UTF8_BIN,
    instructions INT(8),
    modifier VARCHAR(10)
);

CREATE TABLE Testcase_Info
(
	testcaseId INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	execution VARCHAR(5) NOT NULL,
	testcase VARCHAR(1024) NOT NULL COLLATE UTF8_BIN,
    instructions INT(8),
    assertions INT(8)
);

CREATE TABLE Relation_Info
(
	relationId INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	execution VARCHAR(5) NOT NULL,
	methodId INT(11) NOT NULL,
	testcaseId INT(11) NOT NULL,
	minStackDistance INT(8),
	maxStackDistance INT(8)
);

CREATE TABLE RetValGen_Info
(
	retValGenId INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	execution VARCHAR(5) NOT NULL,
	retValGen VARCHAR(256) NOT NULL
);

CREATE TABLE Test_Result_Info
(
	resultId INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	execution VARCHAR(5) NOT NULL,
	relationId INT(11) NOT NULL,
	retValGenId INT(11) NOT NULL,
	killed TINYINT(1) NOT NULL
);

CREATE TABLE Method_Test_Abort_Info
(
	abortId INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	execution VARCHAR(5) NOT NULL,
	methodId INT(11) NOT NULL,
	retValGenId INT(11) NOT NULL
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
	
/* Methods that were tested (they have a test result or all tests aborted). */
CREATE VIEW V_Tested_Methods_Info
(
	execution,
	methodId
) AS
	SELECT DISTINCT t.execution, mapping.methodId
	FROM Test_Result_Info t
	INNER JOIN V_Name_Mapping mapping
	ON t.relationId = mapping.relationId
	UNION
	SELECT DISTINCT m.execution, m.methodId
	FROM Method_Test_Abort_Info m;
	
/* Test result of methods that were successfully tested. */
CREATE VIEW V_Method_State_Info
(
	execution,
	methodId,
	method,
	killed
) AS 
	SELECT t.execution, mapping.methodId, mapping.method, SUM(t.killed) > 0
	FROM Test_Result_Info t
	INNER JOIN V_Name_Mapping mapping
	ON t.execution = mapping.execution
	AND t.relationId = mapping.relationId
	GROUP BY t.execution, mapping.methodId, mapping.method;

/** Test result of methods for which tests were started (and run successfully or were aborted). */
CREATE VIEW V_Method_State_Info_Extended
(
	execution,
	methodId,
	method,
	testCompleted,
	killed,
	aborted
) AS 
	SELECT tmi.execution, tmi.methodId, mapping.method, COUNT(tr.killed) > 0, COALESCE(SUM(tr.killed) > 0, 0), COUNT(ta.methodId) > 0
	FROM V_Tested_Methods_Info tmi
	INNER JOIN V_Name_Mapping mapping
	ON tmi.execution = mapping.execution
	AND tmi.methodId = mapping.methodId
	LEFT OUTER JOIN Test_Result_Info tr
	ON tr.execution = tmi.execution
	AND tr.relationId = mapping.relationId
	AND mapping.methodId = tmi.methodId
	LEFT OUTER JOIN Method_Test_Abort_Info ta
	ON ta.execution = tmi.execution
	AND ta.methodId = tmi.methodId
	GROUP BY tmi.execution, tmi.methodId, mapping.method;
	
/** Test result, extended by test and method ids and names. */
CREATE VIEW V_Test_Result_Info
(
	execution,
	relationId,
	methodId,
	testcaseId,
	method,
	testcase,
	retValGenId,
	killed
) AS 
	SELECT t.execution, t.relationId, mapping.methodId, mapping.testcaseId, mapping.method, mapping.testcase, t.retValGenId, t.killed
	FROM Test_Result_Info t
	INNER JOIN V_Name_Mapping mapping
	ON t.execution = mapping.execution
	AND t.relationId = mapping.relationId;
	
CREATE INDEX idx_aly_mi_1 ON Method_Info(execution);
CREATE INDEX idx_aly_mi_2 ON Method_Info(method(50));
CREATE INDEX idx_aly_ti_1 ON Testcase_Info(execution);
CREATE INDEX idx_aly_ti_2 ON Testcase_Info(testcase(50));
CREATE INDEX idx_aly_ri_1 ON Relation_Info(execution);
CREATE INDEX idx_aly_ri_2 ON Relation_Info(methodId);
CREATE INDEX idx_aly_ri_3 ON Relation_Info(testcaseId);
CREATE INDEX idx_aly_rvgi_1 ON RetValGen_Info(execution);
CREATE INDEX idx_aly_rvgi_2 ON RetValGen_Info(retValGen(50));
CREATE INDEX idx_aly_tri_1 ON Test_Result_Info(execution);
CREATE INDEX idx_aly_tri_2 ON Test_Result_Info(relationId);

ALTER TABLE Relation_Info ADD CONSTRAINT uc_aly_ri_1 UNIQUE (execution, methodId, testcaseId);
ALTER TABLE Test_Result_Info ADD CONSTRAINT uc_aly_tri_1 UNIQUE (execution, relationId, retValGenId);
ALTER TABLE Method_Test_Abort_Info ADD CONSTRAINT uc_aly_mai_1 UNIQUE (execution, methodId, retValGenId);