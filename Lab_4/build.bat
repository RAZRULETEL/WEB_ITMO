cd eureka
call ./gradlew bootJar
docker build -t eureka .
cd ..
cd gateway-microservice
call ./gradlew bootJar
docker build -t lab4/gateway .
cd ..
cd auth-microservice
call ./gradlew bootJar
docker build -t lab4/auth .
cd ..
cd points-microservice
call ./gradlew bootJar
docker build -t lab4/points .
cd ..
cd front
docker build -t lab4/front .
cd ..