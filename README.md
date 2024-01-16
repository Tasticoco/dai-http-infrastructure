# HTTP Infrastucture
___
>### Authors
>Arthur Junod & Edwin Häffner
>

## Introduction

In this project, we will create a HTTP infrastructure using Docker and NGinx. We will also create a CRUD API using Javalin.
The theme of this project is the franchise Monster Hunter and we'll use our API to manage monsters. Check the API section for more information
about the operations we can do on monsters.

## Documentation

### NGinx configuration

Here is our configuration file for nginx, it is simply named 'nginx.conf' : 

```nginx configuration
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

```docker-compose
version: '3.8'

services:
  nginx:
    build:
      context: .
      dockerfile: Dockerfile
    image: nginx:stable
    ports:
      - "8080:80"
    volumes:
      - ./nginx.conf:/etc/nginx/conf.d/default.conf
      - ./website/troweld-html:/var/www/html

```

### API

#### Monster API


#### GET /monsters

Return a list of all monsters.
Parameters: None

Response example : 

```json
[
  {
    "{id}": {
      "name": "Barioth",
      "description": "The snow-white flying wyvern with huge tusks found in the frozen tundra...",
      "species": "FLYING_WYVERN",
      "element": "ICE",
      "weakness": {"FIRE": 3, "THUNDER": 2},
      "resistance": {"WATER": 4, "ICE": 4, "DRAGON": 1},
      "maxHP": 19200,
      // ... other attributes
    }
  }
]
```

#### GET api/monsters/{id}

Fetches a monster by its ID.

Parameters:

id - The ID of the monster.

Response is similar to getAllMonsters.

#### GET api/monsters/name/{name}

Fetches a monster by its name.

Parameters:

name - The name of the monster. (case insensitive)

Response:
Same as GET /monsters/{id}


#### DELETE api/monsters/{id}


Deletes a monster by its ID.

Parameters:

id - The ID of the monster.

Response:
HTTP 204 No Content upon successful deletion.


#### PUT api/monsters/{id}

Updates a monster by its ID.

Parameters:

id - The ID of the monster.

Request Body: JSON representation of the monster.

Response:
HTTP 200 OK upon successful update.


#### POST api/monsters

Creates a new monster.

Request Body: JSON representation of the monster.

Response:
HTTP 201 Created upon successful creation.


#### GET api/monsters/{id}/weakness

Fetches the weaknesses of a monster by its ID.

Parameters:

id - The ID of the monster.

Response:

```json

{
"FIRE": 3,
"THUNDER": 2
}
```

#### POST api/monsters/{id}/hunted

Updates the stats of a monster by its ID.

Parameters:

id - The ID of the monster.
size - The new size of the monster.

Response:
HTTP 200 OK upon successful update.

### Traeffik 

Here is now our updated `docker-compose.yml` file : 

```docker-compose
version: '3'
services:
  api:
    build: ./api

    expose:
      - "7000"
    labels:
      - "traefik.enable=true"
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
      - "traefik.http.routers.nginx.rule=Host(`localhost`)"


  reverse-proxy:
    image: traefik:v2.10
    command: --providers.docker
             --api.dashboard=true
             --api.insecure=true
    ports:
      - "80:80"
      - "8080:8080"
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
```

#### Explanation of the Configuration:



###### API Service:

- Built from a Dockerfile located in ./api.

- Exposes port 7000 within the Docker network (not mapped to the host so we can only access it with the Traefik configuration).

- Two Traefik labels are set up to define router rules for this service. It will be accessible through localhost under the /api path prefix.


###### Nginx Service:

- Built from a Dockerfile located in ./nginx_server.

- Uses the official nginx:stable image.

- Exposes port 80 within the Docker network.

- Traefik label sets up a router rule for this service to be accessible through localhost. (Just like on the API, but this time, localhost/ will show the Nginx server)


###### Reverse Proxy (Traefik):

- Uses the traefik:v2.10 image.

- Command arguments configure Traefik to use Docker as the provider and enable the dashboard with insecure access.

- Maps port 80 on the host to port 80 on the container and port 8080 on the host to Traefik's dashboard port 8080 on the container.

- Mounts the Docker socket to allow Traefik to listen to the Docker

#### Why a Reverse Proxy is Useful to Improve the Security of the Infrastructure:

A reverse proxy like Traefik improves security in several ways (as we've seen in our amazing DAI lessons) :

TODO : Actually give reasons why it's good :-) 


#### Accessing and Understanding the Traefik Dashboard:

To access the Traefik dashboard, you can navigate to http://localhost:8080 in your web browser. 
Since `--api.insecure=true` is set, it is accessible without HTTPS or authentication and for the purpose of this
lab, we don't think it's very useful to make it secure as we only launch this docker on the loopback network.

### Scalability :

By adding :
```dockerfile
    deploy:
      replicas: 2
```
Under both of our services `api` and `nginx`

We see that when we launch our Docker, we launch two instances of both of the services, and by launching the Docker with
this command as for an example: `docker compose up --scale api=3 -d`

We will get three instances of the api service instead of two.

### Sticky Session :

By adding :
```dockerfile
      - "traefik.http.services.api.loadbalancer.sticky.cookie=true"
      - "traefik.http.services.api.loadbalancer.sticky.cookie.name=monsterious_cookie"
```
Under the label section of our service `api`, Traefik will use the "monsterious_cookie" to ensure that additional requests 
from a client are routed to the same backend server that handled the initial request !

### HTTPS :

We put the key and the certificate in the `https` folder of this project. 
We generated the key with this command : 
`openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -sha256 -days 3650 -nodes -subj "/C=XX/ST=Vaud/L=YverdonLesBains/O=HEIGVD/OU=TIC/CN=DAIhttpInfrastructurMonsterHunterAPI"`











