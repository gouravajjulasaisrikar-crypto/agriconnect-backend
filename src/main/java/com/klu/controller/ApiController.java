package com.klu.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class ApiController {

    List<Map<String, String>> questions = new ArrayList<>();

    public ApiController() {
        Map<String, String> q1 = new HashMap<>();
        q1.put("title", "How to improve crop yield?");
        q1.put("author", "Farmer Raj");

        Map<String, String> q2 = new HashMap<>();
        q2.put("title", "Best fertilizers?");
        q2.put("author", "Farmer Ravi");

        questions.add(q1);
        questions.add(q2);
    }

    @GetMapping("/questions")
    public List<Map<String, String>> getQuestions() {
        return questions;
    }

    @PostMapping("/questions")
    public Map<String, String> addQuestion(@RequestBody Map<String, String> question) {
        questions.add(question);
        return question;
    }
}