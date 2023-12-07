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
http {
    server {
        listen 80; # The port the server listens
        server_name vaches.ch; # The domain name of the server
        root /var/www/html; # The root directory of the server

        # The context that define what to fetch when being on vaches.ch/
        location / {
            try_files $uri $uri/ =404; # if the file isn't found, return 404
        }
    }
}
```

### Docker compose file


