package com.library.borrow.controller;

import com.library.borrow.model.BorrowRecord;
import com.library.borrow.repository.BorrowRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class BorrowController {

    private final BorrowRepository repo;
    private final RestTemplate restTemplate;

    @Value("${book.service.url}")
    private String bookServiceUrl;

    @Value("${notification.service.url}")
    private String notificationServiceUrl;

    public BorrowController(BorrowRepository repo, RestTemplate restTemplate) {
        this.repo = repo;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/borrow")
    public ResponseEntity<?> borrowBook(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        Long bookId = request.get("bookId");

        // Update book availability to false
        restTemplate.put(bookServiceUrl + "/books/" + bookId + "/availability?available=false", null);

        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setBorrowedAt(LocalDateTime.now());
        BorrowRecord saved = repo.save(record);

        // Notify
        restTemplate.postForObject(notificationServiceUrl + "/notifications",
                Map.of("message", "Book " + bookId + " issued to user " + userId), Object.class);

        return ResponseEntity.ok(saved);
    }

    @PostMapping("/return")
    public ResponseEntity<?> returnBook(@RequestBody Map<String, Long> request) {
        Long bookId = request.get("bookId");

        return repo.findByBookIdAndReturnedAtIsNull(bookId).map(record -> {
            record.setReturnedAt(LocalDateTime.now());
            repo.save(record);

            // Update book availability to true
            restTemplate.put(bookServiceUrl + "/books/" + bookId + "/availability?available=true", null);

            // Notify
            restTemplate.postForObject(notificationServiceUrl + "/notifications",
                    Map.of("message", "Book " + bookId + " returned by user " + record.getUserId()), Object.class);

            return ResponseEntity.ok(record);
        }).orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/borrow")
    public ResponseEntity<?> getAll() { return ResponseEntity.ok(repo.findAll()); }
}
