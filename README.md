# Building and running

It's just `docker-compose up -d`

It will build the whole application and run it against 3 ScyllaDB nodes. First it will run the `Migrate` part and then `Sensor` and `Server` parts.

It's almost direct port of https://github.com/scylladb/care-pet/tree/master/java with the same structure and logics, but in Kotlin. 

Also, for the sake of convinience it's everything is in the Dockerfile, user doesn't need to have Java installed, as well as Gradle.
