import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.HashMap;



public class Bot extends TelegramLongPollingBot {
    private static final String PORT = System.getenv("PORT");
    @SneakyThrows
    public void run() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Bot());
        }catch (TelegramApiException t){
            t.printStackTrace();
        }


        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(PORT))) {
            while (true) {
                serverSocket.accept();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BotCommands bc = new BotCommands();
    private static final HashMap<String, Method> commands = new HashMap<>();

    public Bot() {
        BotCommands bc = new BotCommands();
        for(Method m : bc.getClass().getDeclaredMethods()){
            if(!m.isAnnotationPresent(Command.class)){
                continue;
            }
            Command cmd = m.getAnnotation(Command.class);
            if(cmd.inProgress()){ // проверка
                continue;
            }
            commands.put(cmd.name().toLowerCase(), m);
            for(String name : cmd.aliases()){
                commands.put(name.toLowerCase(), m);
            }
        }

    }

    @Override
    public String getBotUsername() {
        return "badamtsss_bot";
    }

    @Override
    public String getBotToken() {
        return "1728302320:AAFxSeV7TMgecrvBDa-qEwlIiG1ZoX2YnC8";
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage())
            return;

        String chatId = String.valueOf(update.getMessage().getChatId());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        Message message = update.getMessage();

        String text = message.getText();


        if (text.isEmpty()) {
            sendMessage.setText("- Я вас не понимаю");
        }

        String[] args = new String[]{};

        String command = text.toLowerCase();
        if(commands.get(command) == null){
            String[] splitted = command.split(" ");
            command = splitted[0].toLowerCase();
            args = Arrays.copyOfRange(splitted, 1, splitted.length);
        }

        Method m = commands.get(command);


        if(m == null) {
            String[] st = command.split(",|\\.|!|\\?"); // Закину сюда непонятные ответы для дальнейшей обработки
            try {
                sendMessage.setText(bc.otherQuestions(st[0]));
                try {
                    execute(sendMessage);
                    return;
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            String answer = (String) m.invoke(bc, (Object) args);
            if(m.isAnnotationPresent(Command.class)){
                Command cmd = m.getAnnotation(Command.class);
                String name = cmd.name().toLowerCase();
                if(!name.equals("умения")){
                    if(name.equals("напоминалка")) {

                        sendMessage.setText(answer);
                    }
                    if (name.equals("увлеченность_бота")){

                        sendMessage.setText(answer);
                    }

                }
            }
            sendMessage.setText(answer);
        }catch (NullPointerException n){
            sendMessage.setText("Словили нул поинтер");
        }
        catch (Exception e) {

            sendMessage.setText("Что-то пошло не так, попробуйте ещё раз");
        }

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

}
