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

#### GET /monsters/{id}

Fetches a monster by its ID.

Parameters:

id - The ID of the monster.

Response is similar to getAllMonsters.

#### GET /monsters/name/{name}

Fetches a monster by its name.

Parameters:

name - The name of the monster. (case insensitive)

Response:
Same as GET /monsters/{id}


#### DELETE /monsters/{id}


Deletes a monster by its ID.

Parameters:

id - The ID of the monster.

Response:
HTTP 204 No Content upon successful deletion.


#### PUT /monsters/{id}

Updates a monster by its ID.

Parameters:

id - The ID of the monster.

Request Body: JSON representation of the monster.

Response:
HTTP 200 OK upon successful update.


#### POST /monsters

Creates a new monster.

Request Body: JSON representation of the monster.

Response:
HTTP 201 Created upon successful creation.


#### GET /monsters/{id}/weakness

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

#### POST /monsters/{id}/hunted

Updates the stats of a monster by its ID.

Parameters:

id - The ID of the monster.
size - The new size of the monster.

Response:
HTTP 200 OK upon successful update.

TODO documentation

