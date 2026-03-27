Before starting the project, make sure you have the following installed:

- **Docker** (version 20.10 or higher)
- **Docker Compose** (version 2.0 or higher)
- **Java 25** (if running backend locally)
- **Node.js 20+** (if running frontend locally)
- **Maven 3.9+** (if running backend locally)

## Quick Start with Docker Compose

### 1. Create Environment Variables File

Create a `.env` file in the root directory (`diplom-work-Ivailoboyadzhiev/`) with env.example


### 2. Set Up OAuth2 Providers

#### Google OAuth2 Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the **Google+ API**
4. Go to **Credentials** → **Create Credentials** → **OAuth 2.0 Client ID**
5. Configure the OAuth consent screen
6. Set **Authorized redirect URIs** to:
   - `http://localhost:8080/login/oauth2/code/google` (for local development)
   - `https://your-domain.com/login/oauth2/code/google` (for production)
7. Copy the **Client ID** and **Client Secret** to your `.env` file

#### Facebook OAuth2 Setup

1. Go to [Facebook Developers](https://developers.facebook.com/)
2. Create a new app
3. Add **Facebook Login** product
4. Go to **Settings** → **Basic**
5. Add **Valid OAuth Redirect URIs**:
   - `http://localhost:8080/login/oauth2/code/facebook` (for local development)
   - `https://your-domain.com/login/oauth2/code/facebook` (for production)
6. Copy the **App ID** and **App Secret** to your `.env` file

### 3. Start the Application

From the root directory, run:

docker-compose up --build

This will:
- Build the backend and frontend Docker images
- Start PostgreSQL database
- Start the backend service (Spring Boot)
- Start the frontend service (Nginx with React app)

### 4. Access the Application

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **Database**: localhost:5432 (credentials from `.env`)

