# SQLImporter
A Java SQL dump importer, using apache commons

usage: sqlimporter
 -i <driver> <connectionString> <file>   imports SQL Dump file using the
                                         JDBC Connection string
 -v                                      prints the version
 
 
You still need to link your Database driver somewhere.
 
example : 

-i com.mysql.jdbc.Driver jdbc:mysql://host/databse dump.sql

http://commons.apache.org/
