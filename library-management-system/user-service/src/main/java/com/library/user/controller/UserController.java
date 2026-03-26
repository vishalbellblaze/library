package com.library.user.controller;

import com.library.user.model.User;
import com.library.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository repo;

    public UserController(UserRepository repo) { this.repo = repo; }

    @PostMapping("/users")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(repo.save(user));
    }

    @GetMapping("/users")
    public List<User> getAll() { return repo.findAll(); }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
