package org.acme;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {

    @Inject
    BookRepository bookRepository;

    @GET
    public List<Book> getAllBooks() {
        return bookRepository.findAll().toList();
    }

    @GET
    @Path("/{id}")
    public Response getBook(@PathParam("id") String id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            return Response.ok(book.get()).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    public Response createBook(Book book) {
        if (book.getId() == null) {
            book.setId(UUID.randomUUID().toString());
        }
        Book savedBook = bookRepository.save(book);
        return Response.status(Response.Status.CREATED).entity(savedBook).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateBook(@PathParam("id") String id, Book book) {
        Optional<Book> existingBook = bookRepository.findById(id);
        if (existingBook.isPresent()) {
            Book updatedBook = bookRepository.save(book);
            return Response.ok(updatedBook).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteBook(@PathParam("id") String id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            bookRepository.deleteById(id);
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/search/title/{title}")
    public List<Book> searchByTitle(@PathParam("title") String title) {
        return bookRepository.findByTitle(title);
    }

    @GET
    @Path("/search/author/{author}")
    public List<Book> searchByAuthor(@PathParam("author") String author) {
        return bookRepository.findByAuthor(author);
    }

    @GET
    @Path("/search/year/{year}")
    public List<Book> searchByYear(@PathParam("year") Integer year) {
        return bookRepository.findByPublishYear(year);
    }

    @GET
    @Path("/search/isbn/{isbn}")
    public Response searchByIsbn(@PathParam("isbn") String isbn) {
        Optional<Book> book = bookRepository.findByIsbn(isbn);
        if (book.isPresent()) {
            return Response.ok(book.get()).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
