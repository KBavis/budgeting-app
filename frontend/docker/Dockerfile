# build react application
FROM node:14-alpine AS build

WORKDIR /app
COPY package.json package-lock.json* ./
RUN npm ci --only-production
COPY . .
RUN npm run build

# production stage
FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx/nginx.conf /etc/nginx/nginx.conf 

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]