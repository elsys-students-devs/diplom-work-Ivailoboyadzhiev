FROM node:20-alpine AS builder
WORKDIR /app

COPY package*.json ./
ENV NPM_CONFIG_PRODUCTION=false
RUN npm install
RUN npm install -g vite@^5

COPY . .
RUN vite build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]