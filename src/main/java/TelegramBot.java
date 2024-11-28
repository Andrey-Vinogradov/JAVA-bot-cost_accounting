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
    private static final String SAY_HELLO_BTN = "Скажи привет!!!";
    private static final String HOW_ARE_YOU_BTN = "Как дела???";

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

        KeyboardRow row = new KeyboardRow(); //добавляем ряд кнопок
        row.add(SAY_HELLO_BTN); // одна кнопка
        row.add(HOW_ARE_YOU_BTN);//вторая кнопка
        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(row); //добавляем ряд row в список rows

        if (text.equals(SAY_HELLO_BTN)) {// Если прислано "Скажи привет!!!" или нажата кнопка "Скажи привет!!!" то
            sendMessage.setText("Привет!");
        } else {
            sendMessage.setText(text); // иначе пользователю отдаётся то же сообщение, что он прислал
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
            e.printStackTrace();
        }
    }
}