FROM tomcat

COPY target/auth-1.0.0.war /usr/local/tomcat/webapps/ROOT.war
COPY target/auth-1.0.0.war/ /usr/local/tomcat/webapps/ROOT

EXPOSE 8090
