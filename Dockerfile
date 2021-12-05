FROM tomcat

COPY target/spring-security-1.0.0.war /usr/local/tomcat/webapps/ROOT.war
COPY target/spring-security-1.0.0.war/ /usr/local/tomcat/webapps/ROOT

EXPOSE 8090
