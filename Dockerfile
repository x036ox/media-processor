FROM openjdk:21-jdk-slim
WORKDIR /media-processor
COPY ./target/media-processor-0.1.jar /media-processor
RUN apt-get update && \
    apt-get install -y ffmpeg
EXPOSE 8081
CMD ["java", "-jar", "media-processor-0.1.jar"]