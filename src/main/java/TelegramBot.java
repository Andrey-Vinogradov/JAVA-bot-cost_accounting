import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

public class TelegramBot extends TelegramLongPollingBot {

    private static final String ADD_EXPENSE_BTN = "Добавить расходы";
    private static final String SHOW_CATEGORIES_BTN = "Показать категории";
    private static final String SHOW_EXPENSES_BTN = "Показать расходы";

    private static final Map<String, List<Integer>> EXPENSES = new HashMap<>();
    // Создаём сущность (Map<>) типа
    // Транспорт -> [10, 20, 33]
    // Продукты -> [11, 15, 5]
    // ... и т.д.


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
        switch (text){
            case SHOW_CATEGORIES_BTN -> sendMessage.setText(getFormattedCategories());//Вывод всех категорий
            case SHOW_EXPENSES_BTN -> sendMessage.setText(getFormattedExpenses());
            case ADD_EXPENSE_BTN -> sendMessage.setText("Введите имя категории и сумму через пробел");
            default -> {
                //Мы ждём от пользователя сообщение типа: "Транспорт 100"
                String[] expense = text.split(" ");
                if(expense.length == 2){ //Проверяем, что количество введённых слов равно 2
                    String category = expense[0];
//                    if(!EXPENSES.containsKey(category)){//Если категория не имеется, то будем создавать
//                        EXPENSES.put(category, new ArrayList<>());

                    EXPENSES.putIfAbsent(category, new ArrayList<>());// То же, что и вверху, только через спец оператор
                    //Если категория имеется, то будем добавлять в категорию трату
                    Integer sum = Integer.parseInt(expense[1]);
                    EXPENSES.get(category).add(sum);
                } else{
                    sendMessage.setText("Похоже Вы не верно ввели трату :(");
                }

            }
        }
        KeyboardRow row1 = new KeyboardRow(); //добавляем ряд кнопок
        row1.add(ADD_EXPENSE_BTN); // одна кнопка
        KeyboardRow row2 = new KeyboardRow(); //добавляем ряд кнопок
        row2.add(SHOW_CATEGORIES_BTN);//вторая кнопка
        KeyboardRow row3 = new KeyboardRow();
        row3.add(SHOW_EXPENSES_BTN);//третья кнопка

        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(row1); //добавляем ряд row1 в список rows
        rows.add(row2); //добавляем ряд row2 в список rows
        rows.add(row3); //добавляем ряд row3 в список rows

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(rows);
        replyKeyboardMarkup.setResizeKeyboard(true); // Устанавливаем параметры клавиатуры
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setSelective(true);

        ReplyKeyboardMarkup keyboard = buildKeyboard(
                List.of(ADD_EXPENSE_BTN,
                        SHOW_CATEGORIES_BTN,
                        SHOW_EXPENSES_BTN
                )
        );
        sendMessage.setReplyMarkup(keyboard); //ответ на нажатие кнопки

        try {
            execute(sendMessage); // ответ пользователю в телеграм обратно
        } catch (TelegramApiException e) {
            System.err.println("!!!Error!!! sending failed: " + e.getMessage());
            e.printStackTrace();//обработка ошибок
        }
    }







    //Функция для создания клавиатуры бота
    private ReplyKeyboardMarkup buildKeyboard(List<String> buttonNames){
        List<KeyboardRow> rows = new ArrayList<>();//создаём список кнопок
        for(String buttonName : buttonNames){
            final KeyboardRow row = new KeyboardRow();//создаём ряд
            row.add(buttonName);//добавляем кнопку в ряд
            rows.add(row);//добавляем ряд в список рядов
        }
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setKeyboard(rows);
        return keyboard;
    }
    //Функция для вывода названий категорий в удобном формате
    private  String getFormattedCategories(){
        Set<String> categories = EXPENSES.keySet();
        if(categories.isEmpty()) return "Пока нет ни одной категории";//обработка случая пока нет категорий
        return String.join( "\n", EXPENSES.keySet());
    }
    //Функция для вывода расходов в удобном формате
    private  String getFormattedExpenses(){
        Set<Map.Entry<String, List<Integer>>> expensesPerCategories = EXPENSES.entrySet();
        if(expensesPerCategories.isEmpty()) return "Пока нет расходов";//если не введены расходы

        String formatedResult = "";
        for(Map.Entry<String, List<Integer>> category : EXPENSES.entrySet()){
            String categoryExpenses = "";
            for(Integer expense : category.getValue()){
                categoryExpenses += expense + " ";
            }
            formatedResult += category.getKey()+ ": " + categoryExpenses + "\n";
        }
        return formatedResult;
    }
}