import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
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
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new Bot());

        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(PORT))) {
            while (true) {
                serverSocket.accept();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BotCommands bc;
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

        Message message = update.getMessage();
        String chatId = String.valueOf(message.getChatId());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        try {
            String answer = (String) m.invoke(this, (Object) args);
            if(m.isAnnotationPresent(Command.class)){
                Command cmd = m.getAnnotation(Command.class);
                String name = cmd.name().toLowerCase();
                if(!name.equals("умения")){
                    if(name.equals("напоминалка")) {
                        bc.previousAnswer = "напоминалка";
                        sendMessage.setText(answer);
                    }
                    if (name.equals("увлеченность_бота")){
                        bc.previousAnswer = "увлеченность";
                        sendMessage.setText(answer);
                    }
                    bc.previousAnswer = answer;
                }
            }
            sendMessage.setText(answer);
        } catch (Exception e) {
            bc.previousAnswer = null;
            sendMessage.setText("Что-то пошло не так, попробуйте ещё раз");
        }

        execute(sendMessage);

    }

}
