-- DROP TABLE IF EXISTS Execution_Information;
-- DROP TABLE IF EXISTS Collected_Information_Import;
-- DROP TABLE IF EXISTS Test_Result_Import;
-- DROP TABLE IF EXISTS Test_Abort_Import;
-- DROP TABLE IF EXISTS Stack_Info_Import;
-- DROP TABLE IF EXISTS Method_Info_Import;
-- DROP TABLE IF EXISTS Testcase_Info_Import;

CREATE TABLE Execution_Information
(
	id int(5) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	execution VARCHAR(5) NOT NULL,
	date date NOT NULL,
	project VARCHAR(64) NOT NULL,
	testType VARCHAR(64),
	description VARCHAR(512),
	notes VARCHAR(512),
	configurationContent text,
	processed TINYINT(1) NOT NULL DEFAULT 0
);

CREATE TABLE Collected_Information_Import
(
	id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	execution VARCHAR(5) NOT NULL,
	method VARCHAR(1024) NOT NULL COLLATE UTF8_BIN,
	testcase VARCHAR(1024) NOT NULL COLLATE UTF8_BIN,
	methodHash VARCHAR(32) GENERATED ALWAYS AS (MD5(method)) VIRTUAL,
    testcaseHash VARCHAR(32) GENERATED ALWAYS AS (MD5(testcase)) VIRTUAL
 );

CREATE TABLE Test_Result_Import
(
	id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	execution VARCHAR(5) NOT NULL,
	testcase VARCHAR(1024) NOT NULL COLLATE UTF8_BIN,
	method VARCHAR(1024) NOT NULL COLLATE UTF8_BIN,
	retValGen VARCHAR(256) NOT NULL,
	killed TINYINT(1) NOT NULL,
	assertErr TINYINT(1),
	exception VARCHAR(256),
	methodHash VARCHAR(32) GENERATED ALWAYS AS (MD5(method)) VIRTUAL,
    testcaseHash VARCHAR(32) GENERATED ALWAYS AS (MD5(testcase)) VIRTUAL,
    retValGenHash VARCHAR(32) GENERATED ALWAYS AS (MD5(retValGen)) VIRTUAL
);

CREATE TABLE Test_Abort_Import
(
	id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	execution VARCHAR(5) NOT NULL,
	method VARCHAR(1024) NOT NULL COLLATE UTF8_BIN,
	retValGen VARCHAR(256) NOT NULL,
	cause VARCHAR(32) NOT NULL,
	methodHash VARCHAR(32) GENERATED ALWAYS AS (MD5(method)) VIRTUAL
);

CREATE TABLE Stack_Info_Import
(
	id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	execution VARCHAR(5) NOT NULL,
	testcase VARCHAR(1024) NOT NULL COLLATE UTF8_BIN,
	method VARCHAR(1024) NOT NULL COLLATE UTF8_BIN,
	minStackDistance INT(8) NOT NULL,
	maxStackDistance INT(8) NOT NULL,
	methodHash VARCHAR(32) GENERATED ALWAYS AS (MD5(method)) VIRTUAL,
    testcaseHash VARCHAR(32) GENERATED ALWAYS AS (MD5(testcase)) VIRTUAL
);

CREATE TABLE Method_Info_Import
(
	id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	execution VARCHAR(5) NOT NULL,
	method VARCHAR(1024) NOT NULL COLLATE UTF8_BIN,
	intValue INT(8),
	stringValue VARCHAR(20) COLLATE UTF8_BIN,
	valueName VARCHAR(20) NOT NULL,
	methodHash VARCHAR(32) GENERATED ALWAYS AS (MD5(method)) VIRTUAL
);

CREATE TABLE Testcase_Info_Import
(
	id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	execution VARCHAR(5) NOT NULL,
	testcase VARCHAR(1024) COLLATE UTF8_BIN,
	intValue INT(8),
	stringValue VARCHAR(20) COLLATE UTF8_BIN,
	valueName VARCHAR(20) NOT NULL,
    testcaseHash VARCHAR(32) GENERATED ALWAYS AS (MD5(testcase)) VIRTUAL
);

CREATE INDEX idx_ei_1 ON Execution_Information(execution);
CREATE INDEX idx_ci_1 ON Collected_Information_Import(execution, methodHash, testcaseHash);
CREATE INDEX idx_ci_2 ON Collected_Information_Import(testcaseHash);
CREATE INDEX idx_tr_1 ON Test_Result_Import(execution, methodHash, testcaseHash, retValGenHash);
CREATE INDEX idx_tr_2 ON Test_Result_Import(testcaseHash);
CREATE INDEX idx_tr_3 ON Test_Result_Import(retValGenHash);
CREATE INDEX idx_ta_1 ON Test_Abort_Import(execution, methodHash);
CREATE INDEX idx_sii_1 ON Stack_Info_Import(execution, methodHash, testcaseHash);
CREATE INDEX idx_sii_2 ON Stack_Info_Import(testcaseHash);
CREATE INDEX idx_mii_1 ON Method_Info_Import(execution, methodHash);
CREATE INDEX idx_tii_1 ON Testcase_Info_Import(execution, testcaseHash);

ALTER TABLE Execution_Information ADD CONSTRAINT uc_ei_1 UNIQUE (execution);