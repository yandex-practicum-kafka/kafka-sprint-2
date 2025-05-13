package com.example.kafkastreams.controller;

import com.example.kafkastreams.service.BlockedUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.Profile;

import java.util.Map;
import java.util.Set;

@RestController
@Profile("processor")
@RequestMapping("/blocked-users")
public class BlockedUserController {

    // Сервис для управления списком заблокированных пользователей
    private final BlockedUserService blockedUserService;

    // Конструктор для внедрения зависимостей
    public BlockedUserController(BlockedUserService blockedUserService) {
        this.blockedUserService = blockedUserService;
    }

    /**
     * Добавить пользователя в список блокировок для заданного пользователя.
     * Пример запроса: POST /blocked-users/add?userId=1&blockedUserId=20
     *
     * @param userId        ID пользователя, который блокирует
     * @param blockedUserId ID пользователя, которого блокируют
     * @return подтверждение операции
     */
    @PostMapping("/add")
    public ResponseEntity<String> addBlockedUser(@RequestParam int userId, @RequestParam int blockedUserId) {
        blockedUserService.addBlockedUser(userId, blockedUserId);
        return ResponseEntity.ok("User " + blockedUserId + " blocked for user " + userId);
    }

    /**
     * Удалить пользователя из списка блокировок.
     * Пример запроса: DELETE /blocked-users/remove?userId=1&blockedUserId=20
     *
     * @param userId        ID пользователя, который снимает блокировку
     * @param blockedUserId ID пользователя, блокировка которого снимается
     * @return подтверждение операции
     */
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeBlockedUser(@RequestParam int userId, @RequestParam int blockedUserId) {
        blockedUserService.removeBlockedUser(userId, blockedUserId);
        return ResponseEntity.ok("User " + blockedUserId + " unblocked for user " + userId);
    }

    /**
     * Получить текущий список всех блокировок.
     * Пример запроса: GET /blocked-users/list
     *
     * @return карта пользователей и списков заблокированных ими пользователей
     */
    @GetMapping("/list")
    public ResponseEntity<Map<Integer, Set<Integer>>> listBlockedUsers() {
        Map<Integer, Set<Integer>> blockedUsers = blockedUserService.getBlockedUsers();
        return ResponseEntity.ok(blockedUsers);
    }
}
