package org.acme;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends CrudRepository<Book, String> {

    List<Book> findByTitle(String title);

    List<Book> findByAuthor(String author);

    List<Book> findByPublishYear(Integer publishYear);

    Optional<Book> findByIsbn(String isbn);
}