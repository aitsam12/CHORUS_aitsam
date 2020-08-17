# Aitsam version of CHORUS

## Building & Running

This framework is written in Scala and built using Maven. The code has been tested on windows but also work fine with Mac and Linux. 

To build the code:

`git clone https://github.com/uvm-plaid/chorus.git`

`cd chorus-master`

`mvn package`


## Running "hello.scala" which is my code for single query testing.

I created this `hello.scala` file in same folder as examples `src/main/scala/examples`.

 - Initial challenge is to build connection with database.
  Here I used MySQL for database connection. In `MySQL Workbench` I already have my desired tables.
 
 - I defined their schema in 'aitsam.yaml' `scr/test/resources` file. You can edit it according to your database.
  make sure your `MySQL workbench` is running when your run `hello.scala` file.

 - `src/main/scala/chorus/util/DB.scala` handle all database connection stuff. You have to provide details of your MySQL for connection.


### the builtin example in repository works with dummy data and where all rows are 1.