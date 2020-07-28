FROM openjdk:8-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENV PORT=8080
ENV JAVA_OPTS=""
#ENTRYPOINT ["java","-jar","/app.jar", "-Dserver.port=$PORT"]
CMD java $JAVA_OPTS -Dserver.port=$PORT -jar /app.jar