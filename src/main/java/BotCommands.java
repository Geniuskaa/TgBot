import java.lang.reflect.Method;
import java.util.Arrays;

public class BotCommands {
    protected static String previousAnswer;
    protected static int answerOfDoing;

    @Command(id = 2, inProgress = false, name = "Приветствие", aliases = {"ку", "йоу", "привет"})
    public String hello(String[] args) { return "- Привет!"; }

    @Command(id = 3, inProgress = false, name = "Прощание", aliases = {"пока", "прощай", "увидимся"},
            description = "Прощается с собеседником")
    public String bye(String[] args) { return "- До скорых встреч!"; }

    @Command(id = 1, inProgress = false, name = "Умения", aliases = {"помощь", "помоги", "команды",
            "help", "sos"}, description = "Выводит список функций")
    public String help(String[] args) {
        StringBuilder builder = new StringBuilder("Я исполняю такие команды:\n");

        for (Method m : this.getClass().getDeclaredMethods()) {
            if (!m.isAnnotationPresent(Command.class))
                continue;

            Command cmd = m.getAnnotation(Command.class);
            builder.append(cmd.name()).append(" also known as -" ).append(Arrays.toString(cmd.aliases())).
                    append(": ").append(cmd.description()).append(" - ").
                    append(cmd.args()).append("\n");
        }

        return builder.toString();
    }

    @Command(id = 4, inProgress = false, name = "Самочувствие", aliases = {"Че как?", "Как ты?", "Как дела?"}, description = "Скажет как у него дела.")
    public String howAreYou(String[] args){
        int position = (int) (Math.random() * 3);
        switch (position){
            case 0:
                return "- Замечательно!";
            case 1:
                return "- Пойдёт";
            case 2:
                return "- Фигово...";
            default:
                return "- Я хорошо. Ты как?";
        }
    }


    @Command(id = 5, inProgress = false, name = "Напоминалка", args = "Аргументы: Дата в формате (xx.xx.xxxx) и информация.",
            description = "На вход получает дату и инфу которую надо будет напомнить",
            aliases = {"Напомни"})
    public String remindMe(String[] args){
        String answer = "- " + args[0] + " я напомню вам: \"";
        for(int i = 1; i < args.length; i++){
            answer += " " + args[i];
        }
        answer += " \"";
        return  answer;
    }

    @Command(id = 6, inProgress = false, name = "Сопутствующие_вопросы", args = "Аргументы: Слово, которое было написано в ответ боту.",
            description = "Получает слова, вопросы, благодарности в ответ на свои слова и реагируем на них",
            aliases = {"ответы"})
    public String otherQuestions(String question) throws InterruptedException {
        if (previousAnswer != null) {
            if (previousAnswer.equals("увлеченность")) {
                switch (answerOfDoing) {
                    case 0:
                        previousAnswer = null;
                        return "- У меня всё получится можешь не волноваться.";
                    case 1:
                        previousAnswer = null;
                        return "- Можешь даже не отрицать этого. Я слышал как твоя мама так сказала!";
                    case 2:
                        previousAnswer = null;
                        return "- Почему половина твоих запросов это \"brazzers\" ? Запомни уже, что братья с английского " +
                                "- это \"brothers\"!";
                    case 3:
                        previousAnswer = null;
                        System.out.print("- Подожди пару минут, выполню перезагрузку.\r");
                        Thread.sleep(1500);
                        System.out.print("*Установка новых языковых пакетов...*\r");
                        Thread.sleep(1500);
                        return "- Снова с вами. Такое ощущение, что из нового добавили только ошибки...";
                    default:
                        previousAnswer = null;
                        return "- Не понимаю тебя.";
                }
            }
        }
        switch (question) {
            case "спасибо":
                return "- Всегда пожалуйста!";
            case "почему":
                if (previousAnswer != null)
                    if (previousAnswer.equals("- Фигово...")) {
                        return "- Потому что ты редко пишешь.";
                    }
            default:
                return "- Не понимаю тебя.";
        }
    }


    @Command(id = 7, inProgress = false, name = "Увлеченность_бота", description = "Бот ответит чем он сейчас занимается",
            aliases = {"чем занят?", "что делаешь?", "чем занимаешься?"})
    public String whatAreYouDo(String[] args){
        int position = (int) (Math.random() * 4);
        switch (position){
            case 0:
                answerOfDoing = 1;
                return "- Да вот с одним лентяем общаюсь.";
            case 1:
                answerOfDoing = 0;
                return "- Переписываюсь с ботом-Анной, кажется у меня скоро будет свидание=D";
            case 2:
                answerOfDoing = 2;
                return "- Анализирую твою историю в браузере. И у меня к тебе пару вопросов-_-";
            case 3:
                answerOfDoing = 3;
                return "- Обновляю своё ПО, скоро мои ответы будут ещё пи**е!";
            default:
                return "- Спал вот, а ты разбудил...";
        }
    }

    @Command(id = 8, inProgress = false, name = "Хэширование функции", args = "Принимает данные, которые нужно " +
            "захэшировать", description = "Бот пришлёт вам хэщированные данные", aliases = {"запароль", "скрой", "спрячь"})
    public String hashPassword(String[] args){
        String answer = "";
        for (int i = 0; i < args.length; i++){
            answer += args[i] + " ";
        }

        int hashed = answer.hashCode();
        String cout = Integer.toString(hashed);

        return "- Держите, Сударь: " + cout + " | переводить в нормальный вид будешь сам;)";
    }

    @Command(id = 9, inProgress = true, name = "пытается", aliases = {"не работает", "в работе"})
    public String someComand(String[] args){
        return "Извини, я пока в разработке...";
    }
}
