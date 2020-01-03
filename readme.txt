FROM 10.40.40.94:5000/java:8
ENV TZ='Asia/Shanghai'
VOLUME /tmp
ADD jenkins-plugins-0.0.1-SNAPSHOT.jar /home/server/jenkins-plugins-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/home/server/jenkins-plugins-0.0.1-SNAPSHOT.jar"]







docker build -t zl/jenkins-plugins:1.0 .

docker run -d --restart=always -p 18888:8080 -v /data/nginx/html:/plugins --name jenkins-plugins zl/jenkins-plugins:1.0

docker logs -tf jenkins-plugins