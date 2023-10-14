FROM registry.access.redhat.com/ubi8/openjdk-11:latest
COPY --chown=jboss:root target/*.jar /deployments/.
EXPOSE 8080
USER 185
ENV AB_JOLOKIA_OFF=""
ENV JAVA_OPTS="-Djboss.bind.address=0.0.0.0 -Djboss.bind.address.management=0.0.0.0"
RUN chmod -R ug+rwX /deployments/.