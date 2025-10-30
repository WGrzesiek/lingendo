FROM nginx:1.29.1

RUN rm /etc/nginx/conf.d/default.conf

COPY default.conf /etc/nginx/conf.d/default.conf
COPY monitoring /etc/nginx/html/monitoring
COPY .htpasswd /etc/nginx/.htpasswd


EXPOSE 80
