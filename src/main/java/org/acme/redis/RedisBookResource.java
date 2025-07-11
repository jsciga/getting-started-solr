package org.acme.redis;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.Book;

import java.util.UUID;
import java.util.logging.Logger;

@Path("/redis-books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RedisBookResource {

    private static final Logger LOGGER = Logger.getLogger(RedisBookResource.class.getName());

    @Inject
    BookRedisService bookRedisService;

    @POST
    public Response createBookViaRedis(Book book) {
        try {
            if (book.getId() == null) {
                book.setId(UUID.randomUUID().toString());
            }

            LOGGER.info("Creating book via Redis: " + book.getId());

            bookRedisService.saveBook(book);

            return Response.status(Response.Status.CREATED)
                    .entity(book)
                    .build();
        } catch (Exception e) {
            LOGGER.severe("Error creating book via Redis: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating book via Redis: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateBookViaRedis(@PathParam("id") String id, Book book) {
        try {
            book.setId(id);

            LOGGER.info("Updating book via Redis: " + id);

            bookRedisService.saveBook(book);

            return Response.ok(book).build();
        } catch (Exception e) {
            LOGGER.severe("Error updating book via Redis: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating book via Redis: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/batch")
    public Response createBooksViaRedis(Book[] books) {
        try {
            LOGGER.info("Creating batch of books via Redis: " + books.length);

            for (Book book : books) {
                if (book.getId() == null) {
                    book.setId(UUID.randomUUID().toString());
                }
                bookRedisService.saveBook(book);
            }

            return Response.status(Response.Status.CREATED)
                    .entity("Successfully created " + books.length + " books via Redis")
                    .build();
        } catch (Exception e) {
            LOGGER.severe("Error creating books batch via Redis: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating books batch via Redis: " + e.getMessage())
                    .build();
        }
    }
}
