FROM eclipse-temurin:21-jre-alpine

COPY target/storage-svc*.jar /app/storage-svc.jar
WORKDIR /app
#Expose server port
EXPOSE 8080
#Expose gRPC port
EXPOSE 80
CMD ["java", "-jar", "storage-svc.jar"]