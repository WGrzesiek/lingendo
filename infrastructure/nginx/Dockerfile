FROM nginx:alpine

COPY nginx.conf /etc/nginx/nginx.conf
COPY monitoring /etc/nginx/html/monitoring
COPY .htpasswd /etc/nginx/.htpasswd

EXPOSE 80