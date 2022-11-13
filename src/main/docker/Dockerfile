FROM gradle as build
COPY . /app
RUN cd /app && gradle build --info

FROM amazoncorretto:19.0.1-alpine3.16
RUN mkdir -p /app/
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.9.0/wait /wait
COPY --from=build /app/build/libs/demo-0.1-all.jar /app/
RUN mv /app/demo* /app/demo.jar && chmod +x /wait
WORKDIR /app