all:
	mvn install:install-file -Dfile=lib/ojdbc8.jar -DgroupId=com.oracle -DartifactId=ojdbc8 -Dversion=8 -Dpackaging=jar
	mvn clean compile exec:java
