package org.spring.simpletgbot.component;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;

    public UpdateConsumer() {
        this.telegramClient = new OkHttpTelegramClient("token");
    }

    @Override
    public void consume(Update update) {
        if (update.getMessage() != null) {  // Обработка обычных сообщений
            handleRegularMessage(update);
        } else if (update.getBusinessMessage() != null) { // Обработка бизнес-сообщений
            handleBusinessMessage(update);
        } else if (update.hasCallbackQuery()) {
            MaybeInaccessibleMessage message = update.getCallbackQuery().getMessage();
            String businessConnectionId = extractBusinessConnectionId(message);
            if (businessConnectionId != null) {
                handleBusinessCommands(update, businessConnectionId);
            } else {
                handleRegularCommands();
            }
        }
    }

    // Метод для извлечения businessConnectionId через рефлексию
    private String extractBusinessConnectionId(MaybeInaccessibleMessage message) {
        try {
            // Используем рефлексию для доступа к скрытому полю
            Field field = message.getClass().getDeclaredField("businessConnectionId");
            field.setAccessible(true);
            return (String) field.get(message);
        } catch (Exception e) {
            return null;
        }
    }

    private void handleRegularMessage(Update update) {
        long chatId = update.getMessage().getChatId();
        String text = "Hello from regular chat! You write: " + update.getMessage().getText();
        sendMessage(chatId, text);
    }

    private void handleBusinessMessage(Update update) {
        long chatId = update.getBusinessMessage().getChat().getId();
        String businessConnectionId = update.getBusinessMessage().getBusinessConnectionId();
        String text = "Hello from business chat! You write: " + update.getBusinessMessage().getText();

        sendBusinessMessage(chatId, text, businessConnectionId);

        System.out.println("handler business message");
        System.out.println(chatId);
        System.out.println(businessConnectionId);
    }

    private void handleBusinessCommands(Update update, String businessConnectionId) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        long chatId = callbackQuery.getMessage().getChatId();
        User user = callbackQuery.getFrom();
        String data = callbackQuery.getData();

        switch (data) {
            case "info" -> sendBusinessInfo(chatId, user, businessConnectionId);
            case "random" -> sendBusinessRandom(chatId, businessConnectionId);
            case "picture" -> sendBusinessPicture(chatId, businessConnectionId);
        }
    }

    @SneakyThrows
    private void sendBusinessInfo(Long chatId, User from, String businessConnectionId) {
        String text = "Info: " + from.getFirstName() + " " + from.getLastName() +
                "\nUsername: " + from.getUserName() +
                "\nID: " + from.getId() +
                "\nType: " + (from.getIsBot() ? "Bot" : "Human") +
                "\nLanguage: " + from.getLanguageCode() +
                "\nPremium: " + (from.getIsPremium() != null ? "Yes" : "No");

        SendMessage builder = SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .businessConnectionId(businessConnectionId)
                .build();

        telegramClient.execute(builder);
    }

    private void sendBusinessRandom(long chatId, String businessConnectionId) {
        int randomInt = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
        sendBusinessMessage(chatId, String.valueOf(randomInt), businessConnectionId);
    }

    private void sendBusinessPicture(long chatId, String businessConnectionId) {
        new Thread(() -> {
            try {
                // Путь к локальному файлу
                File imageFile = new File("/Users/kamchatka/maxresdefault.jpg");

                // Проверяем существование файла
                if (!imageFile.exists()) {
                    throw new FileNotFoundException("Image file not found: " + imageFile.getAbsolutePath());
                }

                // Создаем InputFile
                InputFile inputFile = new InputFile(imageFile);


                SendPhoto sendPhoto = SendPhoto
                        .builder()
                        .chatId(chatId)
                        .photo(inputFile)
                        .caption("You like it? xyz")
                        .businessConnectionId(businessConnectionId)
                        .build();

                telegramClient.execute(sendPhoto);
            } catch (IOException | TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void handleRegularCommands() {

    }

    @SneakyThrows
    private void sendMessage(long chatId, String text) {
        SendMessage builder = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();

        InlineKeyboardButton button1 = InlineKeyboardButton
                .builder()
                .text("Get information")
                .callbackData("info")
                .build();
        InlineKeyboardButton button2 = InlineKeyboardButton
                .builder()
                .text("Random")
                .callbackData("random")
                .build();
        InlineKeyboardButton button3 = InlineKeyboardButton
                .builder()
                .text("Get picture")
                .callbackData("picture")
                .build();

        KeyboardRow row1 = new KeyboardRow("Get information", "Random", "Get picture");
        List<KeyboardRow> keyboardRows = List.of(row1);

        List<InlineKeyboardRow> inlineKeyboardRows = List.of(
                new InlineKeyboardRow(button1),
                new InlineKeyboardRow(button2),
                new InlineKeyboardRow(button3)
        );

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(inlineKeyboardRows);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(keyboardRows);

        builder.setReplyMarkup(inlineKeyboardMarkup);
        builder.setReplyMarkup(replyKeyboardMarkup);
        telegramClient.execute(builder);
    }

    @SneakyThrows
    private void sendBusinessMessage(long chatId, String text, String businessConnectionId) {
        SendMessage builder = SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .businessConnectionId(businessConnectionId)
                .build();

        InlineKeyboardButton button1 = InlineKeyboardButton
                .builder()
                .text("Get information")
                .callbackData("info")
                .build();
        InlineKeyboardButton button2 = InlineKeyboardButton
                .builder()
                .text("Random")
                .callbackData("random")
                .build();
        InlineKeyboardButton button3 = InlineKeyboardButton
                .builder()
                .text("Get picture")
                .callbackData("picture")
                .build();

        List<InlineKeyboardRow> inlineKeyboardRows = List.of(
                new InlineKeyboardRow(button1),
                new InlineKeyboardRow(button2),
                new InlineKeyboardRow(button3)
        );

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(inlineKeyboardRows);

        builder.setReplyMarkup(inlineKeyboardMarkup);
        telegramClient.execute(builder);
    }
}