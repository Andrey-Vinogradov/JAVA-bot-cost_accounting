import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class TelegramBot extends TelegramLongPollingBot {

    private static final String ADD_EXPENSE_BTN = "Добавить расходы";
    private static final String SHOW_CATEGORIES = "Показать категории";
    private static final String SHOW_EXPENSES = "Показать расходы";

    @Override
    public String getBotUsername() {
        return "Learning_bot";
    }

    @Override
    public String getBotToken() {
        return "7928999505:AAGXR_o4dkQHk-q83sNdPG51DMgUIRTNva8";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) { //проверка, что в сообщении есть сообщение и оно корректн.
            System.out.println("-Unsupported update");
            return; //если сообщение не корректно, то выход, ждём следующего
        }
        Message message = update.getMessage(); //текст сообщения кладём в переменную message

        User from = message.getFrom(); //берём пользователя сообщения
        String text = message.getText(); //текст сообщения кладём в переменную text
        String logMessage = from.getUserName() + " :" + text; //имя пользователя + текст пользователя
        System.out.println(logMessage);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString()); // Устанавливаем ID чата

        KeyboardRow row1 = new KeyboardRow(); //добавляем ряд кнопок
        row1.add(ADD_EXPENSE_BTN); // одна кнопка
        KeyboardRow row2 = new KeyboardRow(); //добавляем ряд кнопок
        row2.add(SHOW_CATEGORIES);//вторая кнопка
        KeyboardRow row3 = new KeyboardRow();
        row3.add(SHOW_EXPENSES);//третья кнопка

        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(row1); //добавляем ряд row1 в список rows
        rows.add(row2); //добавляем ряд row2 в список rows
        rows.add(row3); //добавляем ряд row3 в список rows

        switch (text){
            case SHOW_CATEGORIES -> sendMessage.setText("Транспорт\nПродукты");
            case SHOW_EXPENSES -> sendMessage.setText("Транспорт: 300\nПродукты: 100");
            default -> sendMessage.setText("Я не знаю такой команды");
        }

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(rows);
        replyKeyboardMarkup.setResizeKeyboard(true); // Устанавливаем параметры клавиатуры
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setSelective(true);
        sendMessage.setReplyMarkup(replyKeyboardMarkup); //ответ на нажатие кнопки

        try {
            execute(sendMessage); // ответ пользователю в телеграм обратно
        } catch (TelegramApiException e) {
            System.err.println("!!!Error!!! sending failed: " + e.getMessage());
            e.printStackTrace();//обработка ошибок
        }
    }
}