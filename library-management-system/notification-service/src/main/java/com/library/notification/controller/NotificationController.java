package com.library.notification.controller;

import com.library.notification.model.Notification;
import com.library.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);
    private final NotificationRepository repo;

    public NotificationController(NotificationRepository repo) { this.repo = repo; }

    @PostMapping("/notifications")
    public ResponseEntity<Notification> create(@RequestBody Map<String, String> body) {
        Notification n = new Notification();
        n.setMessage(body.get("message"));
        n.setCreatedAt(LocalDateTime.now());
        log.info("NOTIFICATION: {}", n.getMessage());
        return ResponseEntity.ok(repo.save(n));
    }

    @GetMapping("/notifications")
    public List<Notification> getAll() { return repo.findAll(); }
}
