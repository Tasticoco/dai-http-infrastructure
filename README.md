# HTTP Infrastucture
___
>### Authors
>Arthur Junod & Edwin Häffner
>

## Introduction

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

TODO documentation

