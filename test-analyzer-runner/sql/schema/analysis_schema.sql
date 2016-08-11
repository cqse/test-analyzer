-- DROP TABLE IF EXISTS Method_Info;
-- DROP TABLE IF EXISTS Testcase_Info;
-- DROP TABLE IF EXISTS Relation_Info;
-- DROP TABLE IF EXISTS RetValGen_Info;
-- DROP TABLE IF EXISTS Test_Result_Info;
-- DROP TABLE IF EXISTS Method_Test_Abort_Info;

CREATE TABLE Method_Info
(
	methodId INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	execution VARCHAR(5) NOT NULL REFERENCES Execution_Information(execution),
	method VARCHAR(1024) NOT NULL COLLATE UTF8_BIN,
    instructions INT(8),
    modifier VARCHAR(10),
	methodHash VARCHAR(32) GENERATED ALWAYS AS (MD5(method)) VIRTUAL
);

CREATE TABLE Testcase_Info
(
	testcaseId INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	execution VARCHAR(5) NOT NULL REFERENCES Execution_Information(execution),
	testcase VARCHAR(1024) NOT NULL COLLATE UTF8_BIN,
    instructions INT(8),
    assertions INT(8),
    testcaseHash VARCHAR(32) GENERATED ALWAYS AS (MD5(testcase)) VIRTUAL
);

CREATE TABLE Relation_Info
(
	execution VARCHAR(5) NOT NULL REFERENCES Execution_Information(execution),
	methodId INT(11) NOT NULL REFERENCES Method_Info(methodId),
	testcaseId INT(11) NOT NULL REFERENCES Testcase_Info(testcaseId),
	minStackDistance INT(8),
	maxStackDistance INT(8),
	-- the execution does not need to be part of the primary key since methodId and testcaseId are already unique
	PRIMARY KEY (methodId, testcaseId)
);

CREATE TABLE RetValGen_Info
(
	retValGenId INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	execution VARCHAR(5) NOT NULL REFERENCES Execution_Information(execution),
	retValGen VARCHAR(256) NOT NULL,
	retValGenHash VARCHAR(32) GENERATED ALWAYS AS (MD5(retValGen)) VIRTUAL
);

CREATE TABLE Test_Result_Info
(
	resultId INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	execution VARCHAR(5) NOT NULL REFERENCES Execution_Information(execution),
	methodId INT(11) NOT NULL REFERENCES Method_Info(methodId),
	testcaseId INT(11) NOT NULL REFERENCES Testcase_Info(testcaseId),
	retValGenId INT(11) NOT NULL REFERENCES RetValGen_Info(retValGenId),
	killed TINYINT(1) NOT NULL
);

CREATE TABLE Method_Test_Abort_Info
(
	abortId INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	execution VARCHAR(5) NOT NULL REFERENCES Execution_Information(execution),
	methodId INT(11) NOT NULL REFERENCES Method_Info(methodId),
	retValGenId INT(11) NOT NULL REFERENCES RetValGen_Info(retValGenId)
);

CREATE INDEX idx_aly_mi_1 ON Method_Info(execution, methodHash);
CREATE INDEX idx_aly_ti_1 ON Testcase_Info(execution, testcaseHash);
CREATE INDEX idx_aly_ri_1 ON Relation_Info(execution, methodId, testcaseId);
CREATE INDEX idx_aly_ri_2 ON Relation_Info(testcaseId);
CREATE INDEX idx_aly_rvgi_1 ON RetValGen_Info(execution, retValGenHash);
CREATE INDEX idx_aly_tri_1 ON Test_Result_Info(execution, methodId, testcaseId);
CREATE INDEX idx_aly_tri_2 ON Test_Result_Info(testcaseId);

ALTER TABLE Relation_Info ADD CONSTRAINT uc_aly_ri_1 UNIQUE (execution, methodId, testcaseId);
ALTER TABLE Test_Result_Info ADD CONSTRAINT uc_aly_tri_1 UNIQUE (execution, methodId, testcaseId, retValGenId);
ALTER TABLE Method_Test_Abort_Info ADD CONSTRAINT uc_aly_mai_1 UNIQUE (execution, methodId, retValGenId);