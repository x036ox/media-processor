FROM openjdk:21-jdk-slim
WORKDIR /VideoProcessor
COPY ./target/MediaProcessor-0.1.jar /VideoProcessor
RUN apt-get update && \
    apt-get install -y ffmpeg
EXPOSE 8081
CMD ["java", "-jar", "MediaProcessor-0.1.jar"]