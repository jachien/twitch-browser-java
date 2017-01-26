need to point JAVA_HOME to java 7 jdk until jetty dependency is updated to version that supports java 8

build:
    mvn package
run locally (windows):
    mvn appengine:devserver
deploy (windows):
    mvn appengine:update
