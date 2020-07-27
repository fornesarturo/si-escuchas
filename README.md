# ¿Si Escuchas?

This project aims to connect multiple Spotify players to have a joined listening experience.

## Required

The following files are needed in your local version of the project for this project to run correctly:

* `src/main/resources/application.properties` contains Spring-related variables.

```
spring.data.mongodb.uri=<YOUR_MONGODB_CONNECTION_URI>
```

* `src/main/resources/local.properties` contains Spotify-related 

```
CLIENT_SECRET=<YOUR_SPOTIFY_APP_CLIENT_SECRET>
CLIENT_ID=<YOUR_SPOTIFY_APP_CLIENT_ID>
REDIRECT_URI=<YOUR_SPOTIFY_REDIRECT_URI>
```