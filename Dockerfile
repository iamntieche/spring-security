FROM tomcat
WORKDIR webapps
COPY target/auth-1.0.0.war .
RUN rm -rf ROOT && mv auth-1.0.0.war ROOT.war
ENTRYPOINT["sh", "/usr/local/tomcat/bin/startup.sh"]
