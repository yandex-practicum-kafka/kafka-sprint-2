package com.example.kafkastreams.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Slf4j
@Service
@Profile("processor")
public class CensorshipService {

    /**
     * Путь к файлу со списком цензурных слов.
     * По умолчанию используется класс-представитель ресурса "classpath:censored_words.txt".
     */
    @Value("${censored.words.file.path:classpath:censored_words.txt}")
    private Resource censoredWordsResource;

    /**
     * Набор для хранения цензурных слов.
     * Использует потокобезопасную реализацию для возможных параллельных операций.
     */
    private final Set<String> censoredWords = new ConcurrentSkipListSet<>();

    /**
     * Метод вызывается после создания бина.
     * Загружает список цензурных слов из файла.
     */
    @PostConstruct
    public void initializeCensoredWords() {
        loadCensoredWordsFromFile();
    }

    /**
     * Получает сообщение и заменяет все цензурные слова на "***".
     *
     * @param message исходное сообщение
     * @return сообщение с цензурированными словами
     */
    public String censorMessage(String message) {
        String censoredMessage = message;
        for (String word : censoredWords) {
            // Используем паттерн, чтобы правильно экранировать слова, избегая ошибок при специальных символах
            String regex = "\\b" + java.util.regex.Pattern.quote(word) + "\\b";
            // Используем регулярное выражение для поиска слова игнорируя регистр
            censoredMessage = censoredMessage.replaceAll("(?i)" + regex, "***");
        }
        return censoredMessage;
    }

    /**
     * Добавляет новое слово в список цензурных слов и сохраняет изменения.
     *
     * @param word слово для добавления
     */
    public void addCensoredWord(String word) {
        censoredWords.add(word.toLowerCase());
        persistCensoredWords();
    }

    /**
     * Удаляет слово из списка цензурных слов и сохраняет изменения.
     *
     * @param word слово для удаления
     */
    public void removeCensoredWord(String word) {
        censoredWords.remove(word.toLowerCase());
        persistCensoredWords();
    }

    /**
     * Возвращает неизменяемое множество всех цензурных слов.
     *
     * @return множество цензурных слов
     */
    public Set<String> getCensoredWords() {
        return Collections.unmodifiableSet(censoredWords);
    }

    /**
     * Загружает список цензурных слов из файла по указанному ресурсу.
     * В случае отсутствия файла — создает новый.
     */
    private void loadCensoredWordsFromFile() {
        try {
            if (censoredWordsResource.exists()) {
                // Читаем все строки файла и добавляем слова в набор
                Files.readAllLines(Paths.get(censoredWordsResource.getURI()))
                    .forEach(word -> censoredWords.add(word.trim().toLowerCase()));
                log.info("Список цензурированных слов успешно загружен из файла.");
            } else {
                // Создаем новый файл, если он не найден
                Path path = Paths.get(censoredWordsResource.getURI());
                Files.createFile(path);
                log.info("Файл со списком цензурированных слов не найден, создан новый: {}", path.toString());
            }
        } catch (IOException e) {
            log.error("Ошибка при загрузке/создании файла со списком цензурированных слов: {}", e.getMessage(), e);
        }
    }

    /**
     * Сохраняет текущий список цензурных слов в файл.
     */
    private synchronized void persistCensoredWords() {
        try {
            Files.write(Paths.get(censoredWordsResource.getURI()), new ArrayList<>(censoredWords));
            log.info("Список цензурированных слов успешно сохранен в файл.");
        } catch (IOException e) {
            log.error("Ошибка при сохранении списка цензурированных слов в файл: {}", e.getMessage(), e);
        }
    }
}
