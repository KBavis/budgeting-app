# build react application
FROM node:22.1.0-alpine AS build

WORKDIR /app
COPY package.json package-lock.json* ./
RUN npm ci
COPY . .
RUN npm run build

# production stage
FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx/nginx.conf /etc/nginx/nginx.conf 

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]