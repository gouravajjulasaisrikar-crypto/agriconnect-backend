package com.klu.controller;

import com.klu.model.Question;
import com.klu.model.Article;
import com.klu.model.User;
import com.klu.repository.QuestionRepository;
import com.klu.repository.ArticleRepository;
import com.klu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class ApiController {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/questions")
    public List<Question> getQuestions() {
        return questionRepository.findAll();
    }

    @PostMapping("/questions")
    public Question addQuestion(@RequestBody Question question) {
        return questionRepository.save(question);
    }

    @PutMapping("/questions/{id}")
    public Question updateAnswer(@PathVariable Long id, @RequestBody Map<String,String> body) {
        Question q = questionRepository.findById(id).orElseThrow();
        q.setAnswer(body.get("answer"));
        return questionRepository.save(q);
    }

    @GetMapping("/articles")
    public List<Article> getArticles() {
        return articleRepository.findAll();
    }

    @PostMapping("/articles")
    public Article addArticle(@RequestBody Article article) {
        return articleRepository.save(article);
    }

    @PostMapping("/signup")
    public User signup(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        return userRepository.save(user);
    }

    @PostMapping("/login")
    public User login(@RequestBody User user) {
        User existingUser = userRepository.findByUsername(user.getUsername())
             .orElseThrow(() -> new RuntimeException("Invalid username or password"));
        
        if (!existingUser.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }
        return existingUser;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }

    @DeleteMapping("/articles/{id}")
    public void deleteArticle(@PathVariable Long id) {
        articleRepository.deleteById(id);
    }
}