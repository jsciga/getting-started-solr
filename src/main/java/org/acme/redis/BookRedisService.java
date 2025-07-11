package org.acme.redis;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.bind.JsonbBuilder;
import org.acme.Book;

import java.util.logging.Logger;

@ApplicationScoped
public class BookRedisService {

    private static final Logger LOGGER = Logger.getLogger(BookRedisService.class.getName());

    private final PubSubCommands<String> pubSubCommands;

    public BookRedisService(RedisDataSource redisDataSource) {
        this.pubSubCommands = redisDataSource.pubsub(String.class);
    }

    public void saveBook(Book book) {
        try {
            String bookJson = JsonbBuilder.create().toJson(book);

            // Publish to book-updates channel
            pubSubCommands.publish("book-updates", bookJson);

            LOGGER.info("Book saved to Redis and published: " + book.getId());

        } catch (Exception e) {
            LOGGER.severe("Error saving book to Redis: " + e.getMessage());
            throw new RuntimeException("Error saving book to Redis", e);
        }
    }
}



