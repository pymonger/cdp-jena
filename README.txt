/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 *                        NASA Jet Propulsion Laboratory
 *                      California Institute of Technology
 *                        (C) 2010  All Rights Reserved
 *
 * <LicenseText>
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */


--------------------------------------------------------------------------------
Maven build life cycle phases
--------------------------------------------------------------------------------

    * process-resources
    * compile
    * process-test-resources
    * test-compile
    * test
    * package
    * install
    * deploy
    * assembly


--------------------------------------------------------------------------------
Useful command lines
--------------------------------------------------------------------------------

# run the main driver using Maven exec
mvn clean compile exec:java -Dexec.mainClass="gov.nasa.jpl.cdp.jena.App" -Dexec.args="arg0 arg1 arg2"

# run unit test
mvn test

# compile all the Java files, run any tests, and package the deliverable code and resources into target/my-app-1.0.jar (assuming the artifactId is my-app and the version is 1.0.)
mvn package

# builds a project and places its binaries in the local repository.
mvn install

# create Eclipse project's ".classpath" file for new Maven projects.
# note that the m2eclipse Eclipse plugin does this automatically.
mvn eclipse:eclipse

# package the deliverable classes and all dependencies into
# target/my-app-1.0-jar-with-dependencies.jar
mvn assembly:assembly

--------------------------------------------------------------------------------
Example Usage
--------------------------------------------------------------------------------

############################################
# populate TDB-backed store
############################################

[gmanipon@dagoo resources]$ rm -rf /tmp/testTdbDir
[gmanipon@dagoo resources]$ cat manipon-family.ttl 
@prefix person: <http://person/> .
@prefix rel: <http://purl.org/vocab/relationship/> .

person:kenneth
      rel:spouseOf
              person:sharon .

person:gerald
      rel:parentOf
              person:koa , person:jonythyn , person:shayne ;
      rel:siblingOf
              person:kenneth , person:desiree ;
      rel:spouseOf
              person:monica .

person:jayden
      rel:childOf
              person:kenneth .
[gmanipon@dagoo resources]$ cdp_jena save -i manipon-family.ttl -t TURTLE -d
/tmp/testTdbDir
 WARN [main] (SetupTDB.java:755) - No BGP optimizer

############################################
# dump contents of TDB-backed store
############################################

[gmanipon@dagoo resources]$ cdp_jena dump -t TURTLE -d /tmp/testTdbDir
 WARN [main] (SetupTDB.java:755) - No BGP optimizer
@prefix person:  <http://person/> .
@prefix rel:     <http://purl.org/vocab/relationship/> .

person:gerald
      rel:parentOf person:shayne , person:jonythyn , person:koa ;
      rel:siblingOf person:desiree , person:kenneth ;
      rel:spouseOf person:monica .

person:jayden
      rel:childOf person:kenneth .

person:kenneth
      rel:spouseOf person:sharon .

############################################
# run sparql query
############################################

[gmanipon@dagoo resources]$ cat child-of.sparql 
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX person: <http://person/>
PREFIX rel: <http://purl.org/vocab/relationship/>
SELECT ?name WHERE {?name rel:childOf person:gerald}
[gmanipon@dagoo resources]$ cdp_jena query -d /tmp/testTdbDir -a
child-of.sparql 
 WARN [main] (SetupTDB.java:755) - No BGP optimizer
--------
| name |
========
--------

############################################
# get inferred model and save to TDB
############################################

[gmanipon@dagoo resources]$ cdp_jena rules -r family.rules -d /tmp/testTdbDir
-t TURTLE > inferred.ttl
 WARN [main] (SetupTDB.java:755) - No BGP optimizer
[gmanipon@dagoo resources]$ cat inferred.ttl 
@prefix person:  <http://person/> .
@prefix rel:     <http://purl.org/vocab/relationship/> .

person:gerald
      rel:inlawOf person:sharon ;
      rel:parentOf person:shayne , person:jonythyn , person:koa ;
      rel:siblingOf person:desiree , person:kenneth ;
      rel:spouseOf person:monica .

person:monica
      rel:inlawOf person:sharon , person:desiree , person:kenneth ;
      rel:parentOf person:shayne , person:jonythyn , person:koa ;
      rel:spouseOf person:gerald .

person:shayne
      rel:childOf person:gerald , person:monica ;
      rel:cousinOf person:jayden ;
      rel:siblingOf person:jonythyn , person:koa .

person:jonythyn
      rel:childOf person:gerald , person:monica ;
      rel:cousinOf person:jayden ;
      rel:siblingOf person:shayne , person:koa .

person:jayden
      rel:childOf person:sharon , person:kenneth ;
      rel:cousinOf person:shayne , person:jonythyn , person:koa .

person:koa
      rel:childOf person:gerald , person:monica ;
      rel:cousinOf person:jayden ;
      rel:siblingOf person:shayne , person:jonythyn .

person:sharon
      rel:inlawOf person:gerald , person:monica , person:desiree ;
      rel:parentOf person:jayden ;
      rel:spouseOf person:kenneth .

person:desiree
      rel:inlawOf person:monica , person:sharon ;
      rel:siblingOf person:gerald , person:kenneth .

person:kenneth
      rel:inlawOf person:monica ;
      rel:parentOf person:jayden ;
      rel:siblingOf person:gerald , person:desiree ;
      rel:spouseOf person:sharon .
[gmanipon@dagoo resources]$ cdp_jena save -i inferred.ttl -t TURTLE -d
/tmp/testTdbDir
 WARN [main] (SetupTDB.java:755) - No BGP optimizer

############################################
# query should now get results
############################################

[gmanipon@dagoo resources]$ cdp_jena query -d /tmp/testTdbDir -a
child-of.sparql 
 WARN [main] (SetupTDB.java:755) - No BGP optimizer
----------------------------
| name                     |
============================
| <http://person/koa>      |
| <http://person/jonythyn> |
| <http://person/shayne>   |
----------------------------

############################################
# restore original (cleaning out db)
############################################

[gmanipon@dagoo resources]$ cdp_jena save -i manipon-family.ttl -t TURTLE -d
/tmp/testTdbDir -c
 WARN [main] (SetupTDB.java:755) - No BGP optimizer
[gmanipon@dagoo resources]$ cdp_jena query -d /tmp/testTdbDir -a
child-of.sparql 
 WARN [main] (SetupTDB.java:755) - No BGP optimizer
--------
| name |
========
--------
[gmanipon@dagoo resources]$ cdp_jena dump -t TURTLE -d /tmp/testTdbDir
 WARN [main] (SetupTDB.java:755) - No BGP optimizer
@prefix person:  <http://person/> .
@prefix rel:     <http://purl.org/vocab/relationship/> .

person:gerald
      rel:parentOf person:shayne , person:jonythyn , person:koa ;
      rel:siblingOf person:desiree , person:kenneth ;
      rel:spouseOf person:monica .

person:jayden
      rel:childOf person:kenneth .

person:kenneth
      rel:spouseOf person:sharon .
