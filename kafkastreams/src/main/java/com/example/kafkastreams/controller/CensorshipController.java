package com.example.kafkastreams.controller;

import com.example.kafkastreams.service.CensorshipService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.Profile;

import java.util.Set;

@RestController
@Profile("processor")
@RequestMapping("/censorship")
public class CensorshipController {

    // Сервис для управления списком цензурируемых слов
    private final CensorshipService censorshipService;

    // Конструктор для внедрения зависимости
    public CensorshipController(CensorshipService censorshipService) {
        this.censorshipService = censorshipService;
    }

    /**
     * Добавление слова в список цензурируемых
     * Пример запроса: POST /censorship/add?word=example
     *
     * @param word слово для добавления в список
     * @return подтверждение операции
     */
    @PostMapping("/add")
    public ResponseEntity<String> addCensoredWord(@RequestParam String word) {
        censorshipService.addCensoredWord(word);
        return ResponseEntity.ok("Word added to censored list: " + word);
    }

    /**
     * Удаление слова из списка цензурируемых
     * Пример запроса: DELETE /censorship/remove?word=example
     *
     * @param word слово для удаления из списка
     * @return подтверждение операции
     */
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeCensoredWord(@RequestParam String word) {
        censorshipService.removeCensoredWord(word);
        return ResponseEntity.ok("Word removed from censored list: " + word);
    }

    /**
     * Получение текущего списка цензурируемых слов
     * Пример запроса: GET /censorship/list
     *
     * @return множество слов, находящихся в списке цензуры
     */
    @GetMapping("/list")
    public ResponseEntity<Set<String>> listCensoredWords() {
        Set<String> words = censorshipService.getCensoredWords();
        return ResponseEntity.ok(words);
    }
}
