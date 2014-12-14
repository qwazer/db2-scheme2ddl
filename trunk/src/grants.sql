SELECT GRANTOR,GRANTORTYPE
     , 'TABLE' AS OBJ_TYPE
     , TABSCHEMA AS OBJ_SCHEMA, TABNAME AS OBJ_NAME
     , CONTROLAUTH, ALTERAUTH, DELETEAUTH, INDEXAUTH, INSERTAUTH, REFAUTH, SELECTAUTH, UPDATEAUTH
     , NULL AS USAGEAUTH, NULL AS ALTERINAUTH, NULL AS CREATEINAUTH, NULL AS DROPINAUTH, NULL AS BINDAUTH, NULL AS EXECUTEAUTH
  FROM SYSCAT.TABAUTH
 WHERE GRANTEETYPE = ?
   AND GRANTEE = ?
 UNION ALL
SELECT GRANTOR,GRANTORTYPE
     , 'COLUMN' AS OBJ_TYPE
     , TABSCHEMA AS OBJ_SCHEMA, TABNAME AS OBJ_NAME
     , NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL
     , COLNAME, PRIVTYPE, GRANTABLE, NULL, NULL, NULL
  FROM SYSCAT.COLAUTH
 WHERE GRANTEETYPE = ?
   AND GRANTEE = ?
 UNION ALL
SELECT GRANTOR,GRANTORTYPE
     , 'INDEX' AS OBJ_TYPE
     , INDSCHEMA AS OBJ_SCHEMA, INDNAME AS OBJ_NAME
     , CONTROLAUTH, NULL, NULL, NULL, NULL, NULL, NULL, NULL
     , NULL, NULL, NULL, NULL, NULL, NULL
  FROM SYSCAT.INDEXAUTH
 WHERE GRANTEETYPE = ?
   AND GRANTEE = ?
 UNION ALL
SELECT GRANTOR,GRANTORTYPE
     , 'PACKAGE' AS OBJ_TYPE
     , PKGSCHEMA AS OBJ_SCHEMA, PKGNAME AS OBJ_NAME
     , CONTROLAUTH, NULL, NULL, NULL, NULL, NULL, NULL, NULL
     , NULL, NULL, NULL, NULL, BINDAUTH, EXECUTEAUTH
  FROM SYSCAT.PACKAGEAUTH
 WHERE GRANTEETYPE = ?
   AND GRANTEE = ?
 UNION ALL
SELECT GRANTOR,GRANTORTYPE
     , 'PROCEDURE' AS OBJ_TYPE
     , SCHEMA AS OBJ_SCHEMA, SPECIFICNAME AS OBJ_NAME
     , NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL
     , ROUTINETYPE, NULL, NULL, NULL, NULL, EXECUTEAUTH
  FROM SYSCAT.ROUTINEAUTH
 WHERE GRANTEETYPE = ?
   AND GRANTEE = ?
 UNION ALL
SELECT GRANTOR,GRANTORTYPE
     , 'SCHEMA' AS OBJ_TYPE
     , NULL AS OBJ_SCHEMA, SCHEMANAME AS OBJ_NAME
     , NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL
     , NULL, ALTERINAUTH, CREATEINAUTH, DROPINAUTH, NULL, NULL
  FROM SYSCAT.SCHEMAAUTH
 WHERE GRANTEETYPE = ?
   AND GRANTEE = ?
 UNION ALL
SELECT GRANTOR,GRANTORTYPE
     , 'SEQUENCE' AS OBJ_TYPE
     , SEQSCHEMA AS OBJ_SCHEMA, SEQNAME AS OBJ_NAME
     , NULL, ALTERAUTH, NULL, NULL, NULL, NULL, NULL, NULL
     , USAGEAUTH, NULL, NULL, NULL, NULL, NULL
  FROM SYSCAT.SEQUENCEAUTH
 WHERE GRANTEETYPE = ?
   AND GRANTEE = ?
 UNION ALL
SELECT GRANTOR,GRANTORTYPE
     , 'TABLESPACE' AS OBJ_TYPE
     , NULL AS OBJ_SCHEMA, TBSPACE AS OBJ_NAME
     , NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL
     , USEAUTH, NULL, NULL, NULL, NULL, NULL
  FROM SYSCAT.TBSPACEAUTH
 WHERE GRANTEETYPE = ?
   AND GRANTEE = ?
 UNION ALL
SELECT GRANTOR,GRANTORTYPE
     , 'VARIABLE' AS OBJ_TYPE
     , VARSCHEMA AS OBJ_SCHEMA, VARNAME AS OBJ_NAME
     , NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL
     , READAUTH AS USAGEAUTH, WRITEAUTH AS ALTERINAUTH, NULL, NULL, NULL, NULL
  FROM SYSCAT.VARIABLEAUTH
 WHERE GRANTEETYPE = ?
   AND GRANTEE = ?
 UNION ALL
SELECT GRANTOR,GRANTORTYPE
     , 'XML_SCHEMA' AS OBJ_TYPE
     , NULL AS OBJ_SCHEMA, CAST(OBJECTID AS VARCHAR(32)) AS OBJ_NAME
     , NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL
     , USAGEAUTH, NULL , NULL, NULL, NULL, NULL
  FROM SYSCAT.XSROBJECTAUTH
 WHERE GRANTEETYPE = ?
   AND GRANTEE = ?

 UNION ALL
SELECT GRANTOR,GRANTORTYPE
     , 'MODULE' AS OBJ_TYPE
     , MODULESCHEMA AS OBJ_SCHEMA, MODULENAME AS OBJ_NAME
     , NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL
     , NULL, NULL, NULL, NULL, NULL, EXECUTEAUTH
  FROM SYSCAT.MODULEAUTH
 WHERE GRANTEETYPE = ?
   AND GRANTEE = ?

ORDER BY OBJ_SCHEMA, OBJ_NAME, OBJ_TYPE
WITH UR ;