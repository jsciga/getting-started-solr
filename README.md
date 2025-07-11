# Getting Started with Solr, Redis, and Quarkus

This project is a demonstration of a Java application built with Quarkus that integrates Apache Solr for powerful search capabilities and Redis for caching and messaging.

There are two main ways to interact with the application's data:
*   **Direct Solr Interaction (`/books`)**: Endpoints that interact directly with the `BookRepository` for immediate, consistent operations on the Solr index.
*   **Redis-driven Interaction (`/redis-books`)**: Endpoints that use Redis as an intermediary. Write operations are faster from the client's perspective, and the data is propagated to Solr asynchronously.

---

## Class Descriptions

Here is a detailed breakdown of the core classes in this project:

### `org.acme.Book`
This is the main entity class. It's a simple POJO (Plain Old Java Object) that represents a book.
*   **Fields**: `id` (String), `title` (String), `author` (String), `isbn` (String), `publishYear` (Integer), `description` (String).
*   **Annotations**: Uses `@Entity` and `@Column` from the Eclipse JNoSQL framework to map the object to a Solr document. The `id` field is marked with `@Id`.

### `org.acme.BookRepository`
An interface that extends `SolrRepository`. It is the data access layer for direct interaction with the Solr `books` core.
*   **Framework**: Eclipse JNoSQL.
*   **Functionality**: Provides standard CRUD (Create, Read, Update, Delete) methods out-of-the-box (e.g., `save()`, `findById()`, `deleteById()`).
*   **Custom Queries**: Includes custom-defined query methods to search for books based on specific fields like `title`, `author`, `publishYear`, and `isbn`.

### `org.acme.BookResource`
This JAX-RS resource exposes the REST endpoints for direct interaction with Solr. It injects the `BookRepository` to perform its operations.
*   **Path**: `/books`
*   **Methods**: Provides endpoints for all CRUD operations and for executing the custom search queries defined in the `BookRepository`.

### `org.acme.redis.BookRedisService`
This service class encapsulates all logic for interacting with Redis.
*   **Functionality**:
    *   Publishing messages to the `book-updates` Redis channel when a book is created or updated.
*   **Injection**: It uses the `RedisDataSource` from Quarkus to get a connection to the Redis server.

### `org.acme.redis.RedisBookResource`
This JAX-RS resource exposes REST endpoints that use Redis as the primary point of interaction.
*   **Path**: `/redis-books`
*   **Functionality**: It uses the `BookRedisService` to perform its actions. For example, when a new book is created via this resource then a message is published to trigger the update in Solr.

### `org.acme.redis.BookSubscriberService`
This service is responsible for listening to messages on the Redis `book-updates` channel and updating the Solr index accordingly.
*   **Functionality**:
    *   It runs in the background and connects to Redis on application startup.
    *   When it receives a message (containing a `Book` object in JSON format), it deserializes it and uses the `BookRepository` to save the book to Solr.
*   **Annotation**: Marked with `@ApplicationScoped` to ensure it's managed by CDI.

---

## REST API Endpoints

### Direct Solr Endpoints (`/books`)

*   `POST /books`
    *   **Description**: Creates a new book directly in Solr.
    *   **Request Body**: `Book` object (JSON).
    *   **Response**: `201 Created` with the saved `Book` object.
*   `GET /books`
    *   **Description**: Retrieves a list of all books from Solr.
    *   **Response**: `200 OK` with an array of `Book` objects.
*   `GET /books/{id}`
    *   **Description**: Retrieves a single book by its ID.
    *   **Response**: `200 OK` with the `Book` object or `404 Not Found`.
*   `PUT /books/{id}`
    *   **Description**: Updates an existing book in Solr.
    *   **Request Body**: `Book` object (JSON).
    *   **Response**: `200 OK` with the updated `Book` object or `404 Not Found`.
*   `DELETE /books/{id}`
    *   **Description**: Deletes a book from Solr by its ID.
    *   **Response**: `204 No Content` or `404 Not Found`.
*   `GET /books/search/title/{title}`
    *   **Description**: Searches for books by an exact title match.
    *   **Response**: `200 OK` with a list of matching `Book` objects.
*   `GET /books/search/author/{author}`
    *   **Description**: Searches for books by an exact author match.
    *   **Response**: `200 OK` with a list of matching `Book` objects.
*   `GET /books/search/year/{year}`
    *   **Description**: Searches for books by publication year.
    *   **Response**: `200 OK` with a list of matching `Book` objects.
*   `GET /books/search/isbn/{isbn}`
    *   **Description**: Finds a book by its unique ISBN.
    *   **Response**: `200 OK` with the `Book` object or `404 Not Found`.

### Redis-driven Endpoints (`/redis-books`)

*   `POST /redis-books`
    *   **Description**: Creates a book by publishing a message to update Solr asynchronously.
    *   **Request Body**: `Book` object (JSON).
    *   **Response**: `201 Created` with the `Book` object.
*   `GET /redis-books/{id}`
    *   **Description**: Retrieves a book directly from the Redis cache.
    *   **Response**: `200 OK` with the `Book` object or `404 Not Found` if not in cache.

---

## Getting Started

To run this application, you will need Java 17+, Maven, and Docker installed.

The application uses **Quarkus Dev Services for Docker Compose** to automatically start and stop the required **Apache Solr** instance during development, based on the `compose-devservices.yml` file.

### How Dev Services Work for Solr

When you run the application in development mode (`./mvnw quarkus:dev`), Quarkus automatically:
1.  Finds the `compose-devservices.yml` file.
2.  Starts the Solr container as defined in the file.
3.  Runs a one-off `schema-init` container to configure the Solr Book schema once Solr is healthy.
4.  Stops and removes the containers when you shut down the Quarkus application.

This means you **do not need to manually run `docker-compose`** for Solr.

### Running the Application
**Run the Quarkus Application**

    Start the application in development mode:
    ```bash
    ./mvnw quarkus:dev
    ```
    Quarkus will automatically start the Solr and Redis containers. The application will be available at `http://localhost:8080`. You can access the Swagger UI for interactive API documentation at `http://localhost:8080/q/swagger-ui`.

## Project Structure

*   `pom.xml`: Maven configuration, includes all dependencies for Quarkus, JNoSQL Solr, and Redis.
*   `compose-devservices.yml`: Defines the required services (Solr, Redis).
*   `src/main/resources/application.properties`: Configuration for Quarkus, Solr, and Redis.
*   `src/main/java/org/acme/`: Contains all the Java source code.
    *   `Book.java`: The data entity.
    *   `BookRepository.java`: Solr repository interface.
    *   `BookResource.java`: JAX-RS resource for direct Solr interaction.
    *   `redis/`: Sub-package for all Redis-related classes.

---