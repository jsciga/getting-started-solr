package org.acme.redis;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.json.bind.JsonbBuilder;
import org.acme.Book;
import org.acme.BookRepository;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@ApplicationScoped
public class BookSubscriberService {

    private static final String CHANNEL = "book-updates";

    private static final Logger LOGGER = Logger.getLogger(BookSubscriberService.class.getName());

    @Inject
    RedisDataSource redisDataSource;

    @Inject
    BookRepository bookRepository;

    private PubSubCommands<String> pubSubCommands;

    //invoked automatically after the start of app
    void onStart(@Observes StartupEvent ev) {
        this.pubSubCommands = redisDataSource.pubsub(String.class);
        startSubscription();
    }

    private void startSubscription() {
        CompletableFuture.runAsync(() -> {
            try {
                LOGGER.info("Starting Redis subscription for book updates...");

                pubSubCommands.subscribe(CHANNEL, bookJson -> {
                    try {
                        LOGGER.info("Received book update: " + bookJson);

                        Book book = JsonbBuilder.create().fromJson(bookJson, Book.class);

                        Book savedBook = bookRepository.save(book);

                        LOGGER.info("Book successfully indexed in Solr: " + savedBook.getId());

                    } catch (Exception e) {
                        LOGGER.severe("Error processing book update: " + e.getMessage());
                        e.printStackTrace();
                    }
                });

            } catch (Exception e) {
                LOGGER.severe("Error in Redis book updates subscription: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}


