DROP PROCEDURE IF EXISTS Transfer;

DELIMITER //
CREATE PROCEDURE Transfer (IN param_execution VARCHAR(5))
BEGIN

SET @executionId = param_execution;

START TRANSACTION;

/* Create an entry for each method. */
INSERT INTO Method_Info
(execution, method)
SELECT c.execution, c.method
FROM Collected_Information_Import c
WHERE c.execution = @executionId
GROUP BY c.methodHash, c.method;

/* Create an entry for each testcase. */
INSERT INTO Testcase_Info
(execution, testcase)
SELECT c.execution, c.testcase
FROM Collected_Information_Import c
WHERE c.execution = @executionId
GROUP BY c.testcaseHash, c.testcase;

/* Create an entry for each entry in Collected_Information. */
INSERT INTO Relation_Info
(execution, methodId, testcaseId)
SELECT c.execution, mi.methodId, ti.testcaseId
FROM Collected_Information_Import c
INNER JOIN Method_Info mi
ON c.execution = mi.execution
AND c.methodHash = mi.methodHash
AND c.method = mi.method
INNER JOIN Testcase_Info ti
ON c.execution = ti.execution
AND c.testcaseHash = ti.testcaseHash
AND c.testcase = ti.testcase
WHERE c.execution = @executionId;

/* Create an entry for each return value generator. */
INSERT INTO RetValGen_Info
(execution, retValGen)
SELECT DISTINCT tri.execution, tri.retValGen
FROM Test_Result_Import tri
WHERE tri.execution = @executionId;

/* Create an entry for each entry in Test_Result. */
INSERT INTO Test_Result_Info
(execution, methodId, testcaseId, retValGenId, killed)
SELECT t.execution, mapping.methodId, mapping.testcaseId, rvg.retValGenId, t.killed
FROM Test_Result_Import t
INNER JOIN V_Name_Mapping mapping
ON mapping.execution = t.execution
AND mapping.methodHash = t.methodHash
AND mapping.testcaseHash = t.testcaseHash
AND mapping.method = t.method
AND mapping.testcase = t.testcase
INNER JOIN RetValGen_Info rvg
ON mapping.execution = rvg.execution
AND t.retValGenHash = rvg.retValGenHash
AND t.retValGen = rvg.retValGen
WHERE t.execution = @executionId;

/* Create an entry for each entry in Test_Abort. */
INSERT INTO Method_Test_Abort_Info
(execution, methodId, retValGenId)
SELECT mi.execution, mi.methodId, rvg.retValGenId
FROM Test_Abort_Import t
INNER JOIN Method_Info mi
ON t.execution = mi.execution
AND t.methodHash = mi.methodHash
AND t.method = mi.method
INNER JOIN RetValGen_Info rvg
ON t.execution = rvg.execution
AND t.retValGen = rvg.retValGen
WHERE t.execution = @executionId;

/* Enrich data with stack information. */
UPDATE Relation_Info ri
INNER JOIN V_Name_Mapping mapping
ON ri.execution = mapping.execution
AND ri.methodId = mapping.methodId
AND ri.testcaseId = mapping.testcaseId
INNER JOIN Stack_Info_Import sii
ON sii.execution = mapping.execution
AND sii.methodHash = mapping.methodHash
AND sii.testcaseHash = mapping.testcaseHash
AND sii.method = mapping.method
AND sii.testcase = mapping.testcase
SET ri.minStackDistance = sii.minStackDistance,
ri.maxStackDistance = sii.maxStackDistance
WHERE sii.execution = @executionId;

/* Enrich data with method information: instructions. */
UPDATE Method_Info mi
INNER JOIN Method_Info_Import mix
ON mi.execution = mix.execution
AND mi.methodHash = mix.methodHash
AND mi.method = mix.method
SET mi.instructions = mix.intValue
WHERE mix.execution = @executionId
AND mix.valueName = 'instructions';

/* Enrich data with method information: modifier. */
UPDATE Method_Info mi
INNER JOIN Method_Info_Import mix
ON mi.execution = mix.execution
AND mi.methodHash = mix.methodHash
AND mi.method = mix.method
SET mi.modifier = mix.stringValue
WHERE mix.execution = @executionId
AND mix.valueName = 'modifier';

/* Enrich data with test information: instructions. */
UPDATE Testcase_Info ti
INNER JOIN Testcase_Info_Import tix
ON ti.execution = tix.execution
AND ti.testcaseHash = tix.testcaseHash
AND ti.testcase = tix.testcase
SET ti.instructions = tix.intValue
WHERE tix.execution = @executionId
AND tix.valueName = 'instructions';

/* Enrich data with test information: assertions. */
UPDATE Testcase_Info ti
INNER JOIN Testcase_Info_Import tix
ON ti.execution = tix.execution
AND ti.testcaseHash = tix.testcaseHash
AND ti.testcase = tix.testcase
SET ti.assertions = tix.intValue
WHERE tix.execution = @executionId
AND tix.valueName = 'assertions';

/* Mark the execution as processed. */
UPDATE Execution_Information ei
SET ei.processed = 1
WHERE ei.execution = @executionId;

COMMIT;

/* List potentially non-unique entries. */
SELECT 'Method_Info.method' AS location, @executionId AS execution, m.methodHash
FROM Method_Info m
WHERE m.execution = @executionId
GROUP BY m.methodHash
HAVING COUNT(*) > 1
UNION ALL
SELECT 'TestCase_Info.testcase' AS location, @executionId AS execution, t.testcaseHash
FROM Testcase_Info t
WHERE t.execution = @executionId
GROUP BY t.testcaseHash
HAVING COUNT(*) > 1
UNION ALL
SELECT 'RetValGen_Info.retValGen' AS location, @executionId AS execution, r.retValGenHash
FROM RetValGen_Info r
WHERE r.execution = @executionId
GROUP BY r.retValGenHash
HAVING COUNT(*) > 1;

END //
DELIMITER ;