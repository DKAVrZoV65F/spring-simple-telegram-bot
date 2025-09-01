# 🤖 Simple Telegram Bot

Простой Telegram бот на Java 21 и Spring Boot 3+, поддерживающий как обычные чаты, так и бизнес-аккаунты ✨

## 🌟 Особенности

- ✅ Поддержка обычных чатов
- ✅ Работа с бизнес-аккаунтами Telegram
- ✅ Интерактивные кнопки и меню
- ✅ Отправка текстовых сообщений
- ✅ Отправка изображений
- ✅ Генерация случайных чисел
- ✅ Получение информации о пользователе

## 🛠 Технологии

- Java 21
- Spring Boot 3.5.4
- TelegramBots Library 9.0.0
- Lombok
- Maven

## 📦 Зависимости

Основные зависимости проекта:
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.telegram</groupId>
        <artifactId>telegrambots-client</artifactId>
        <version>9.0.0</version>
    </dependency>
    <dependency>
        <groupId>org.telegram</groupId>
        <artifactId>telegrambots-springboot-longpolling-starter</artifactId>
        <version>9.0.0</version>
    </dependency>
</dependencies>
```

## ⚙️ Настройка

1. Клонируйте репозиторий
2. Установите токен бота в классе `MyTelegramBot`:
```java
@Override
public String getBotToken() {
    return "your_bot_token_here";
}
```

3. Для отправки изображений укажите правильный путь к файлу в методе `sendBusinessPicture`:
```java
File imageFile = new File("path/to/your/image.jpg");
```

## 🚀 Запуск

1. Соберите проект:
```bash
mvn clean package
```

2. Запустите приложение:
```bash
java -jar target/SimpleTgBot-0.0.1-SNAPSHOT.jar
```

Или запустите напрямую через Maven:
```bash
mvn spring-boot:run
```

## 🎮 Использование

### Обычный чат:
- Отправьте любое сообщение боту
- Получите ответ с интерактивной клавиатурой
- Доступные команды:
  - ℹ️ Get information - информация о пользователе
  - 🎲 Random - случайное число
  - 🖼 Get picture - получить изображение

### Бизнес-чат:
- Все функции обычного чата
- Дополнительная поддержка бизнес-аккаунтов
- Специальная обработка бизнес-сообщений

## 📁 Структура проекта

```
src/
├── main/
│   ├── java/
│   │   └── org/spring/simpletgbot/
│   │       ├── component/
│   │       │   ├── MyTelegramBot.java
│   │       │   └── UpdateConsumer.java
│   │       └── SimpleTgBotApplication.java
│   └── resources/
└── pom.xml
```

## 🔧 Классы

- `MyTelegramBot` - основной класс бота, реализующий SpringLongPollingBot
- `UpdateConsumer` - обработчик входящих сообщений и callback'ов
- `SimpleTgBotApplication` - точка входа Spring Boot приложения

---

## 📄 Лицензия

Этот проект создан в учебных целях.
