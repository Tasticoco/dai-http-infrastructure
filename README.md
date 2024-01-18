# HTTP Infrastructure
___
>### Authors
>Arthur Junod & Edwin Häffner
>

## Introduction

In this project, we will create a HTTP infrastructure using Docker and NGinx. We will also create a CRUD API using Javalin.
The theme of this project is the franchise Monster Hunter and we'll use our API to manage monsters. Check the API section for more information
about the operations we can do on monsters.

## How to use

First you will have to launch 

```sh
mvn clean package
```
in the api folder to create the .jar used in the Dockerfile.

Then you can launch
```sh
docker compose build
#and then
docker compose up -d
```
in the main folder to launch the docker compose and all the services.

## Documentation

All sections were made just after the step asking to make it was finished if you want to see the state of some sections at the end of the lab go to the [Release](#release) section.

### NGinx configuration

Here is our configuration file for nginx, it is simply named 'nginx.conf' : 

```nginx
# Our incredible nginx configuration file

# This is a http server
# We don't need to add the http directive because it is the default one
server {
    listen 80; # The port the server listens
    server_name vaches.ch; # The domain name of the server
    root /var/www/html; # The root directory of the server

    # The context that define what to fetch when being on vaches.ch/
    location / {
        try_files $uri $uri/ =404; # if the file isn't found, return 404
    }
}
```

### Docker compose file

Here is our docker compose file, it is simply named 'docker-compose.yml' :

```yml
version: '3.8'
#The services we want to host in our docker-compose (here nginx)
services:
  nginx:
    # The position of the DockerFile we have to build to get nginx
    build:
      context: .
      dockerfile: Dockerfile
    # The image we base our Docker service on
    image: nginx:stable
    # The ports we expose outside:inside our docker
    ports:
      - "8080:80"
    # We mount the config file of nginx and the html of the website in our docker volumes
    volumes:
      - ./nginx.conf:/etc/nginx/conf.d/default.conf
      - ./website/troweld-html:/var/www/html

```

### API

#### Monster API

All paths start with http://localhost:7000.

| HTTP   | Path                    | Do                                        | Response                                                                                      |
|--------|-------------------------|-------------------------------------------|-----------------------------------------------------------------------------------------------|
| GET    | /monsters               | Display all monsters                      | A json of all monsters                                                                        |
| GET    | /monsters/{id}          | Display a given monster                   | A json of the monster we choose with the parameter {id} or HTTP 404 if not found              |
| GET    | /monsters/name/{name}   | Display a given monster                   | A json of the monster matching the name from param {name} or HTTP 404 if not found            |
| GET    | /monsters/{id}/weakness | Display the weaknesses of a monster       | A json containing all the weaknesses of the monster matching {id} or HTTP 404 if not found    |
| POST   | /monsters               | Create a new monster                      | HTTP 200 OK if created or ERROR SERVER 500 if the datas given are not valid                   |
| PUT    | /monsters/{id}          | Replace a monster or update it            | HTTP 200 OK if changed or HTTP 404 if {id} not found or ERROR SERVER 500 if json not matching |
| PUT    | /monsters/{id}/hunted   | Update the stats of a monster when hunted | HTTP 200 OK if hunted or HTTP 404 if {id} not found                                           |
| DELETE | /monsters/{id}          | Delete a monster                          | HTTP 204 if deleted or HTTP 404 if {id} not found                                             |

##### Json response example

Here from GET /monsters
```json
{
  "0": {
    "name": "Barioth",
    "description": "The snow-white flying wyvern with huge tusks found in the frozen tundra. It uses its forelegs and tail to traverse ice with ease.",
    "species": "FLYING_WYVERN",
    "element": "ICE",
    "weakness": {
      "THUNDER": 2,
      "FIRE": 3
    },
    "resistance": {
      "ICE": 4,
      "WATER": 4,
      "DRAGON": 1
    },
    "maxHP": 19200,
    "investigationXP": 0,
    "biggestEncounter": 0,
    "smallestEncounter": 0,
    "nbHunted": 0,
    "investigationLvl": 0
  }
}
```

Here from GET /monsters/{id}/weakness

```json

{
"FIRE": 3,
"THUNDER": 2
}
```

Finally you can find a [Bruno](https://www.usebruno.com/) collection under the folder `MONSTER_TEST` that tests the different HTTP requests.

### Traeffik 

Here is now our updated `docker-compose.yml` file : 

```yml
version: '3'
# Our api service
services:
  api:
    build: ./api
    # We expose the port 7000 to communicate with traeffik
    expose:
      - "7000"
    labels:
      - "traefik.enable=true"
      # Allow us to access our api through http://localhost/api
      - "traefik.http.routers.api.rule=Host(`localhost`) && PathPrefix(`/api`)"

  nginx:
    build: ./nginx_server
    image: nginx:stable
    expose:
      - "80"
    volumes:
      - ./nginx_server/nginx.conf:/etc/nginx/conf.d/default.conf
      - ./nginx_server/website/troweld-html:/var/www/html
    labels:
      # Allow us to access the static web server http://localhost
      - "traefik.http.routers.nginx.rule=Host(`localhost`)"


  # The reverse proxy set up with traeffik
  reverse-proxy:
    image: traefik:v2.10
    # Command arguments configure Traefik to use Docker as the provider and enable the dashboard with insecure access.
    command: --providers.docker
             --api.dashboard=true
             --api.insecure=true
    # Ports of the proxy and then the dashboard
    ports:
      - "80:80"
      - "8080:8080"
    # Mounts the Docker socket to allow Traefik to listen to the Docker
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
```

#### Why a Reverse Proxy is Useful to Improve the Security of the Infrastructure:

A reverse proxy like Traefik improves security in several ways (as we've seen in our amazing DAI lessons) :

- Only the reverse proxy is exposed to the internet, so it's the only service that can be attacked from the outside.
- The reverse proxy can be configured to only allow certain requests to pass through to the internal services.
- We can manage secure HTTPS connections in the reverse proxy and the internal services don't need to worry about it.



#### Accessing and Understanding the Traefik Dashboard:

To access the Traefik dashboard, you can navigate to http://localhost:8080/dashboard in your web browser. 
Since `--api.insecure=true` is set, it is accessible without HTTPS or authentication and for the purpose of this
lab, we don't think it's very useful to make it secure as we only launch this docker on the loopback network.

### Scalability :

By adding :
```yml
    deploy:
      replicas: 2
```
Under both of our services `api` and `nginx`

We see that when we launch our Docker, we launch two instances of both of the services, and by launching the Docker with
this command as for an example: `docker compose up --scale api=3 -d`

We will get three instances of the api service instead of two.

### Sticky Session :

By adding :
```yml
      - "traefik.http.services.api.loadbalancer.sticky.cookie=true"
      - "traefik.http.services.api.loadbalancer.sticky.cookie.name=monsterious_cookie"
```
Under the label section of our service `api`, Traefik will use the "monsterious_cookie" to ensure that additional requests 
from a client are routed to the same backend server that handled the initial request !

We can easily verify that by checking the cookies in our browser, we can see that the cookie is set to the value of the
container id of the api service.

### HTTPS :

We put the key and the certificate in the `https` folder of this project. 
We generated the key with this command : 
`openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -sha256 -days 3650 -nodes -subj "/C=XX/ST=Vaud/L=YverdonLesBains/O=HEIGVD/OU=TIC/CN=DAIhttpInfrastructurMonsterHunterAPI"`

We then added the following lines to our `docker-compose.yml` file under the `nginx` and `api` service : 
```yml
      - "traefik.http.routers.api.entrypoints=https"
      - "traefik.http.routers.api.tls=true"
```
And we also exposed the port 443 to be able to access the https service :
```yml
    ports:
      - "443:443"
```

We finally added a new file, `traefik.yaml` to our project with the following content : 
```yaml
# traefik.yaml

providers:
  docker: {}

# Configure the 2 entry points used: HTTP and HTTPS
entryPoints:
  http:
    address: ":80"
  https:
    address: ":443"

# Enable the HTTPS protocol with a certificate
tls:
  certificates:
    - certFile: "/etc/traefik/certificates/cert.pem"
    - keyFile: "/etc/traefik/certificates/key.pem"

# Configure the Traefik API
api:
  dashboard: true
  insecure: true
```

And now we can access our API and our website with HTTPS !


### Management UI

We added [Portainer](https://github.com/portainer/portainer) to our docker compose. 

Portainer is a webapp that allow us to manipulate our containers from its UI.

```yml
version: '3'
services:

#... other services

  portainer:
    image: portainer/portainer-ce
    # Ports of the dashboard
    ports:
      - "9443:9443"
    # Volumes to listen to docker and store the data (p.ex. admin user)
    volumes:
      - data:/data
      - /var/run/docker.sock:/var/run/docker.sock
    restart: unless-stopped

volumes:
  data:
```
Then from Portainer you can connect to your local docker environment to access all the containers, images, etc. You can access the dashboard trough http://localhost:9443. You may have to create your admin user the first time you launch Portainer or if you tear down the volumes.

### Integration of the API in the static website

- In `nginx_server/website/MHApi`, we've made a simple HTML page that uses JavaScript to fetch the API
and display the monsters in the memory directly on the page. It can also add a monster to the API. 
- As we don't have a database, the data will be 
lost when the container is stopped. 
- Finally, we can search and show a monster by its id.

### Release

In this section we will comment the final state of some files.

#### Nginx

##### Dockerfile nginx

```Dockerfile
FROM nginx:stable

# Copy the default.conf file to the container
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Create a volume for the website directory
VOLUME /var/www/html

# Copy the website files to the container
COPY  website/MHApi /var/www/html

# Expose port 80
EXPOSE 80
```

##### Configuration file nginx

```nginx
server {
    # Port of nginx
    listen 80;
    # Name of the server of our website
    server_name monsterhunterctropbien.ch;
    # Root files of our website
    root /var/www/html;

    # File to charge for path "/"
    location / {
        try_files $uri $uri/ =404;
    }
}
```

#### API

##### Dockerfile API

```Dockerfile
# The image on which we build the api
FROM eclipse-temurin:17-alpine
COPY target/api-monsters.jar /api-monsters.jar
# This is the port that your javalin application will listen on
EXPOSE 7000
ENTRYPOINT ["java", "-jar", "/api-monsters.jar"]
```

##### Maven pom API

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>api</artifactId>
    <version>1.0-SNAPSHOT</version>

    <dependencies>
        <!--Simple logging for API-->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>2.0.7</version>
        </dependency>
        <!--Allow us to use javalin with Maven-->
        <dependency>
            <groupId>io.javalin</groupId>
            <artifactId>javalin</artifactId>
            <version>5.6.3</version>
        </dependency>
        <!--Library to transfrom Context into JSON-->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.15.0</version>
        </dependency>
    </dependencies>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <build>
        <!--Final name of our .jar API-->
        <finalName>
            api-monsters
        </finalName>
        <!--This block was found in a tutorial on-->
        <!--https://javalin.io/tutorials/docker-->
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.4.1</version>
                <configuration>
                    <transformers>
                        <transformer
                                implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                            <mainClass>heig.dai.http.api.Main</mainClass>
                        </transformer>
                    </transformers>
                    <filters>
                        <filter>
                            <artifact>*:*</artifact>
                            <excludes>
                                <exclude>META-INF/*.SF</exclude>
                                <exclude>META-INF/*.DSA</exclude>
                                <exclude>META-INF/*.RSA</exclude>
                            </excludes>
                        </filter>
                    </filters>
                </configuration>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```