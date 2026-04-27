package com.klu.controller;

import com.klu.model.Question;
import com.klu.model.Article;
import com.klu.model.User;
import com.klu.repository.QuestionRepository;
import com.klu.repository.ArticleRepository;
import com.klu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@CrossOrigin(origins = "*")
public class ApiController {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    private Map<String, String> otpStorage = new ConcurrentHashMap<>();

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
    public Map<String, String> login(@RequestBody User user) {
        User existingUser = userRepository.findByUsername(user.getUsername())
             .orElseThrow(() -> new RuntimeException("Invalid username or password"));
        
        if (!existingUser.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStorage.put(existingUser.getUsername(), otp);

        try {
            SimpleMailMessage m = new SimpleMailMessage();
            m.setTo(existingUser.getEmail());
            m.setSubject("Your Login OTP");
            m.setText("Your OTP for AgriConnect is: " + otp);
            mailSender.send(m);
            System.out.println("OTP successfully sent to " + existingUser.getEmail() + ": " + otp);
        } catch (Exception e) {
            System.out.println("Mail send failed, but OTP is: " + otp); // Fallback for local testing
        }

        return Map.of("status", "OTP_SENT", "email", existingUser.getEmail());
    }

    @PostMapping("/verify-otp")
    public User verifyOtp(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String username = body.get("username");
        String otp = body.get("otp");

        if (otp != null && otp.equals(otpStorage.get(username))) {
            otpStorage.remove(username); // prevent reuse
            User user = userRepository.findByUsername(username).orElseThrow();
            
            // Establish the required Server Session!
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);

            return user;
        }
        throw new RuntimeException("Invalid OTP");
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