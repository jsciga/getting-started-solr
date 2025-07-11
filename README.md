# Getting Started with Solr, Redis, and Quarkus

This project demonstrates a Java application using Quarkus with Apache Solr for search and Redis for caching and messaging.

## Key Features

*   **Direct Solr Interaction**: Endpoints under `/books` for immediate, consistent data operations.
*   **Redis-driven Interaction**: Endpoints under `/redis-books` that use Redis for faster write operations, with asynchronous data propagation to Solr.

---

## Core Classes

*   `Book`: The main entity representing a book.
*   `BookRepository`: Data access layer for direct Solr interaction, using `jakarta.data.repository.CrudRepository`.
*   `BookResource`: JAX-RS resource exposing REST endpoints for direct Solr interaction (`/books`).
*   `BookRedisService`: Service for interacting with Redis, publishing messages to the `book-updates` channel.
*   `RedisBookResource`: JAX-RS resource exposing REST endpoints that use Redis (`/redis-books`).
*   `BookSubscriberService`: Background service that listens to Redis messages and updates the Solr index.

---

## REST API Endpoints

### Direct Solr Endpoints (`/books`)

*   `POST /books`: Creates a new book.
*   `GET /books`: Retrieves all books.
*   `GET /books/{id}`: Retrieves a book by ID.
*   `PUT /books/{id}`: Updates a book.
*   `DELETE /books/{id}`: Deletes a book.
*   `GET /books/search/...`: Search endpoints for title, author, year, and ISBN.

### Redis-driven Endpoints (`/redis-books`)

*   `POST /redis-books`: Creates a book asynchronously via Redis.
*   `PUT /redis-books/{id}`: Updates a book asynchronously via Redis.
*   `POST /redis-books/batch`: Creates a batch of books asynchronously.

---

## Getting Started

You will need Java 17+, Maven, and Docker installed.

The application uses **Quarkus Dev Services for Docker Compose** to automatically manage the Apache Solr and Redis instances.

### Running the Application

Start the application in development mode:

```bash
./mvnw quarkus:dev
```

Quarkus will automatically start the necessary containers.
*   Application: `http://localhost:8080`
*   Swagger UI: `http://localhost:8080/q/swagger-ui`
