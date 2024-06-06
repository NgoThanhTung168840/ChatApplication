FROM ubuntu

RUN mkdir -p "/app" && \
    mkdir uploads

RUN apt update
RUN apt -y install openjdk-17-jdk
RUN apt -y install wget
RUN wget https://dlcdn.apache.org/maven/maven-3/3.8.8/binaries/apache-maven-3.8.8-bin.tar.gz
RUN tar -xzvf apache-maven-3.8.8-bin.tar.gz -C /opt
RUN echo "export M2_HOME=/opt/apache-maven-3.8.8" >> ~/.bashrc
RUN echo "export PATH=${M2_HOME}/bin:${PATH}" >> ~/.bashrc
RUN source ~/.bashrc

WORKDIR /app

COPY ./* ./

RUN mvn clean install

CMD ["mvn", "spring-boot:run","-Dspring-boot.run.profiles=dev"]