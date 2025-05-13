package com.example.kafkastreams.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.CollectionType;

@Slf4j
@Service
@Profile("processor")
public class BlockedUserService {

    // Путь к файлу с данными о заблокированных пользователях (может быть переопределен в настройках)
    @Value("${blocked.users.file.path:classpath:blocked_users.json}")
    private Resource blockedUsersResource;

    // Структура, хранящая список заблокированных пользователей: пользователь -> множество заблокированных им пользователей
    private final Map<Integer, Set<Integer>> blockedUsers = new ConcurrentHashMap<>();

    // Объект для сериализации/десериализации JSON
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Инициализация данных о заблокированных пользователях после создания бина.
     */
    @PostConstruct
    public void initializeBlockedUsers() {
        loadBlockedUsersFromFile();
        if (blockedUsers.isEmpty()) {
            generateAndPersistBlockedUsers();
        }
        log.info("Текущий список заблокированных пользователей: {}", blockedUsers);
    }

    /**
     * Проверка, заблокирован ли пользователь senderId для пользователя userId.
     *
     * @param userId   пользователь, для которого проверяется блокировка
     * @param senderId пользователь, который может быть заблокирован
     * @return true, если senderId заблокирован для userId
     */
    public boolean isUserBlocked(int userId, int senderId) {
        log.info("Проверяем, не заблокирован ли пользователь {} пользователем {}", senderId, userId);
        return blockedUsers.containsKey(userId) && blockedUsers.get(userId).contains(senderId);
    }

    /**
     * Получение полного текущего списка заблокированных пользователей.
     *
     * @return структура пользователь -> множество заблокированных пользователей
     */
    public Map<Integer, Set<Integer>> getBlockedUsers() {
        return Collections.unmodifiableMap(blockedUsers);
    }

    /**
     * Добавление пользователя в список заблокированных для другого пользователя.
     *
     * @param userId         пользователь, для которого добавляется блокировка
     * @param blockedUserId пользователь, которого блокируют
     */
    public void addBlockedUser(int userId, int blockedUserId) {
        blockedUsers.computeIfAbsent(userId, k -> new ConcurrentSkipListSet<>()).add(blockedUserId);
        persistBlockedUsers();
    }

    /**
     * Удаление пользователя из списка заблокированных.
     *
     * @param userId         пользователь, для которого удаляется блокировка
     * @param blockedUserId пользователь, блокировка которого удаляется
     */
    public void removeBlockedUser(int userId, int blockedUserId) {
        if (blockedUsers.containsKey(userId)) {
            blockedUsers.get(userId).remove(blockedUserId);
            persistBlockedUsers();
        }
    }

    /**
     * Генерация случайного списка заблокированных пользователей для каждого пользователя и сохранение их.
     */
    private void generateAndPersistBlockedUsers() {
        for (int userId = 1; userId <= 100; userId++) {
            Random random = new Random();
            int numberOfBlockedUsers = random.nextInt(16); // 0 to 15
            Set<Integer> blockedUserIds = new HashSet<>();
            List<Integer> possibleBlockedUsers = new ArrayList<>();
            for (int i = 1; i <= 100; i++) {
                if (i != userId) {
                    possibleBlockedUsers.add(i);
                }
            }
            Collections.shuffle(possibleBlockedUsers);
            blockedUserIds.addAll(possibleBlockedUsers.subList(0, numberOfBlockedUsers));
            blockedUsers.put(userId, new ConcurrentSkipListSet<>(blockedUserIds));
        }
        persistBlockedUsers();
    }

    /**
     * Загрузка списка заблокированных пользователей из файла.
     */
	private void loadBlockedUsersFromFile() {
		try {
			if (blockedUsersResource.exists()) {
				log.info("Файл существует, загружаем из него данные.");
				// Чтение содержимого файла
				String content;
				try (InputStream inputStream = blockedUsersResource.getInputStream()) {
					content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
					log.info("Содержимое файла blocked_users.json: {}", content);
				}
				
				// Запрос JavaType
				JavaType integerType = mapper.getTypeFactory().constructType(Integer.class);
				JavaType setType = mapper.getTypeFactory().constructCollectionType(ConcurrentSkipListSet.class, integerType);
				MapType mapType = mapper.getTypeFactory().constructMapType(ConcurrentHashMap.class, integerType, setType);
	
				// Загрузка данных из файла
				Map<Integer, Set<Integer>> loadedBlockedUsers = mapper.readValue(blockedUsersResource.getInputStream(), mapType);
	
				blockedUsers.putAll(loadedBlockedUsers);
				log.info("Список заблокированных пользователей успешно загружен из файла: {}", blockedUsers);
			} else {
				log.info("Файл со списком заблокированных пользователей не найден, будет сгенерирован новый.");
			}
		} catch (IOException e) {
			log.error("Ошибка при загрузке списка заблокированных пользователей из файла: {}", e.getMessage());
		}
	}

    /**
     * Сохранение текущего состояния списка заблокированных пользователей в файл.
     */
	private synchronized void persistBlockedUsers() {
		try {
			// Получение файла из ресурса
			File file = blockedUsersResource.getFile();
			// Запись данных в файл
			mapper.writeValue(file, blockedUsers);
			log.info("Список заблокированных пользователей успешно сохранен в файл.");
		} catch (IOException e) {
			log.error("Ошибка при сохранении списка заблокированных пользователей: {}", e.getMessage(), e);
		}
	}
}
