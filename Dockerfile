FROM eclipse-temurin:21-jre-alpine

COPY target/storage-svc*.jar /app/storage-svc.jar
WORKDIR /app
#Expose server port
EXPOSE 50030
#Expose gRPC port
EXPOSE 50035
CMD ["java", "-jar", "storage-svc.jar"]