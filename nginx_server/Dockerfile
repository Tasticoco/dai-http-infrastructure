FROM nginx:stable

# Copy the default.conf file to the container
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Create a volume for the website directory
VOLUME /var/www/html

# Copy the website files to the container
COPY  website/MHApi /var/www/html

# Expose port 80
EXPOSE 80