FROM openjdk:11.0.2-stretch

RUN apt-get update && apt-get install -y php-cli maven

WORKDIR /app

COPY src/ /app/src
COPY pom.xml /app
RUN mvn install
COPY generate_languages.php /app

CMD ["sh", "-c", "java -jar target/ci-language-tool.jar /app/application/language && php generate_languages.php"]
