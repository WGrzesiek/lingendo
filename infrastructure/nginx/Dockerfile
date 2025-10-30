FROM nginx:alpine

COPY app.conf /etc/nginx/conf.d/default.conf
COPY monitoring /etc/nginx/html/monitoring
COPY .htpasswd /etc/nginx/.htpasswd



EXPOSE 80
