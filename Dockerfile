FROM java:8-jdk

# install aws-cli
RUN apt-get update && apt-get install -y python-minimal gettext-base && \
    wget -nv "https://s3.amazonaws.com/aws-cli/awscli-bundle.zip" && \
    unzip awscli-bundle.zip && \
    ./awscli-bundle/install -i /usr/local/aws -b /usr/local/bin/aws

WORKDIR /usr/lib/java

ENV GRAILS_VERSION 3.2.8

RUN wget https://github.com/grails/grails-core/releases/download/v${GRAILS_VERSION}/grails-${GRAILS_VERSION}.zip && \
    unzip grails-${GRAILS_VERSION}.zip && \
    rm -rf grails-${GRAILS_VERSION}.zip && \
    ln -s grails-${GRAILS_VERSION} grails

ENV GRAILS_HOME /usr/lib/java/grails
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64
ENV PATH $GRAILS_HOME/bin:$PATH

RUN mkdir /app
COPY . /app
WORKDIR /app
RUN grails compile
EXPOSE 8080
CMD grails run-app