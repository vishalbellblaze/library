package com.library.book.controller;

import com.library.book.model.Book;
import com.library.book.repository.BookRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class BookController {

    private final BookRepository repo;

    public BookController(BookRepository repo) { this.repo = repo; }

    @PostMapping("/books")
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        book.setAvailable(true);
        return ResponseEntity.ok(repo.save(book));
    }

    @GetMapping("/books")
    public List<Book> getAll() { return repo.findAll(); }

    @GetMapping("/books/{id}")
    public ResponseEntity<Book> getById(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/books/{id}/availability")
    public ResponseEntity<Book> updateAvailability(@PathVariable Long id, @RequestParam boolean available) {
        return repo.findById(id).map(book -> {
            book.setAvailable(available);
            return ResponseEntity.ok(repo.save(book));
        }).orElse(ResponseEntity.notFound().build());
    }
}
