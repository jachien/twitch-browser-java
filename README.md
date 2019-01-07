Deprecated version of Twitch Browser that runs on Google App Engine. Latest versions at https://github.com/jachien/twitch-browser-webapp and https://github.com/jachien/twitch-browser-service.

Need to point JAVA_HOME to java 7 jdk until jetty dependency is updated to version that supports java 8
export JAVA_HOME=/usr/local/java/jdk7

### Build
    mvn package

### Run locally (Windows)
    mvn appengine:devserver

### Deploy (Windows):
    mvn appengine:update
