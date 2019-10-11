FROM docker.neg/ecbd/openjdk:8u181-jdk-alpine3.8
ENV PROJECT_BASE_DIR /opt/app
WORKDIR ${PROJECT_BASE_DIR}
COPY *.jar ${PROJECT_BASE_DIR}/
COPY *.sh ${PROJECT_BASE_DIR}/

RUN chmod +x *.sh
ENTRYPOINT ["bash","start.sh"]