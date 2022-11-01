# compile release
FROM timbru31/java-node:11-alpine-14 AS build_env
WORKDIR /app
RUN npm install shadow-cljs
RUN npm install react react-dom
COPY public /app/public
COPY src /app/src
COPY package-lock.json /app
COPY package.json /app
COPY shadow-cljs.edn /app

RUN npm run release

# serve content on nginx
FROM nginx:alpine AS production_env
WORKDIR /usr/share/nginx/html
RUN rm -rf ./*
COPY --from=0 /app/public .

ENTRYPOINT ["nginx", "-g", "daemon off;"]
