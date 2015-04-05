
[ ![Download](https://api.bintray.com/packages/qwazer/maven/db2-scheme2ddl/images/download.svg) ](https://bintray.com/qwazer/maven/db2-scheme2ddl/_latestVersion) &nbsp; [![Build Status](https://travis-ci.org/qwazer/db2-scheme2ddl.svg?branch=master)](https://travis-ci.org/qwazer/db2-scheme2ddl)

### Description ###

**db2-scheme2ddl** - command line util for export IBM DB2  schema to set of ddl scripts. Provide a lot of configurations via basic command line options or advanced XML configuartion.

:warning: Keep in mind, that db2-scheme2ddl build around undocumended  `SYSPROC.DB2LK_GENERATE_DDL` procedure. So use it on your own risk.

### Benefits ###
**db2-scheme2ddl** give ability to filter undesirable information, separate DDL in different files, pretty format output.

### How to start with minimal configuration ###
Java must be installed on your computer.
For exporting oracle scheme you must provide
  * DB connection string
  * output directory
Usage example. Command
```
java -jar db2-scheme2ddl.jar -url scott/tiger@localhost:5000:SAMPLE -o C:/temp/db2-scheme/
```
will produce directory tree
```
 SCHEMA1/
        views/
              view1.sql
              view2.sql
        tables/
              table1.sql
        functions
                /f1.sql  
```

More command line options
```
java -jar db2-scheme2ddl.jar -help
...
Options: 
  -help, -h               print this message
  -url,                   DB connection URL
                          example: scott/tiger@localhost:5000:SAMPLE
  -o, --output,           output dir
  -p, --parallel,         number of parallel thread (default 4)
  -s, --schemas,          a comma separated list of schemas for processing
                          (works only if connected to oracle as sysdba)
  -c, --config,           path to scheme2ddl config file (xml)
  --stop-on-warning,      stop on getting DDL error (skip by default)
  -tc,--test-connection,  test db connection available
  -version,               print version info and exit
```


### How it is work inside? ###

  1. First, get list of all schemas from command line parameter, from congif or find all available schemas with query
```
SELECT table_schem from SYSIBM.SQLSCHEMAS 
```
  1. Invoke undocumented DB2 function for every schema
```
CALL SYSPROC.DB2LK_GENERATE_DDL('-e -z SAMPLE', ?)
```
  1. store second INOUT parameter for later use
  1. find primary object's DDL with query
```
select OP_SEQUENCE, SQL_STMT, OBJ_SCHEMA, OBJ_TYPE, OBJ_NAME, SQL_OPERATION 
                        FROM SYSTOOLS.DB2LOOK_INFO where OP_TOKEN=? and OBJ_SCHEMA=? and OBJ_TYPE=? and OBJ_NAME=?
```
  1. find depended object's DDL with additional filter parameter. For example, find indexes of table
```
SELECT * 
 FROM SYSTOOLS.DB2LOOK_INFO t 
 WHERE OBJ_TYPE = 'INDEX' 
      AND OP_TOKEN = ? 
      AND exists( 
    SELECT 1 
    FROM SYSCAT.INDEXES i 
    WHERE TABSCHEMA = ? AND TABNAME = ? AND i.INDNAME = t.OBJ_NAME ) 
```
  1. find grants from syscat.`*`auth tables, convert table rows to sql statements (Incomplete and potentially buggy code)

Unfortunately, SYSPROC.DB2LK\_GENERATE\_DDL doesn't accept -x parameter for DB version 9.7
