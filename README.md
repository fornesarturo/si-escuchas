# ¿Si Escuchas?

This project aims to connect multiple Spotify players to have a joined listening experience.

## Local Testing From Source

### Required variables

The following file is needed in your local version of the project for this project to run correctly:

* `src/main/resources/application.properties` contains *required* Spring-related variables.

```
cors.allowed_origin=<YOUR_FRONTEND_URL>
spring.data.mongodb.uri=<YOUR_MONGODB_CONNECTION_URI>
spotify.CLIENT_SECRET=<YOUR_SPOTIFY_APP_CLIENT_SECRET>
spotify.CLIENT_ID=<YOUR_SPOTIFY_APP_CLIENT_ID>
spotify.REDIRECT_URI=<YOUR_SPOTIFY_REDIRECT_URI>
```

### MongoDB

A connection to a **MongoDB** database is required. If you're running MongoDB locally or using
Docker, your `spring.data.mongodb.uri` should look something like this: 
`mongodb://localhost:27017/<DB_NAME>`

A `docker-compose.yaml` file is included in this repo to help with the creation of a **MongoDB** 
database.

```shell
# Make sure you're current working directory is
# your local copy of this repo.
$ pwd
# Should output something like .../si-escuchas

# Then just deploy the container.
$ docker-compose up

# You can delete the container using the down
# command.
$ docker-compose down
```

Before running the project connect to your **MongoDB** database
and create a capped collection `messages` to use **Tailable Cursors** as 
infinite streams.

```shell
# Inside Mongo shell or your preferred GUI
# set your working DB or create a new one
> use <DB_NAME>

# Then create the "messages" collection
> db.createCollection("messages", { capped: true, size: 5000000, max: 10000 });
```

### Run the project

**¿Si escuchas?** is built using gradle, to run the project locally 
execute gradle task `bootRun`. The following logs indicate the 
server is up and listening to your requests:

```
2020-08-01 21:42:17.983 INFO 19564 --- [ main] o.s.b.web.embedded.netty.NettyWebServer : Netty started on port(s): 8080
2020-08-01 21:42:17.985 INFO 19564 --- [ main] com.siescuchas.ApiApplicationKt         : Started ApiApplicationKt in 2.287 seconds (JVM running for 2.543)
```

### Using si-escuchas-web

To use the frontend along with the API you'll have to set up a tunnel 
to provide SSL for **Secure Cookies** to work with your browser. You 
could do this using [ngrok](https://ngrok.com/).

```shell
$ ./ngrok http 8080
```

Copy the https prefixed URL and register it as an allowed callback URL 
in your Spotify App, with the added `/callback` as such: 
`https://XXXXXXXXXXX.ngrok.io/callback`.

Also copy the URL `https://XXXXXXXXXXX.ngrok.io` into your local copy 
of **si-escuchas-web**'s `.env.development` file as the variable 
`VUE_APP_API_URL`. 

#### Temporal addition

In the file `src/main/kotlin/com/siescuchas/controllers/SpotifyLoginController.kt` 
change line 85 `val webAppUrl = "https://si-escuchas.netlify.app"` to your **si-escuchas-web**
URL, for localhost: `val webAppUrl = "http://localhost:8081"`.