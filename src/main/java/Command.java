import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    String name();
    int id();
    boolean inProgress() default true;
    String args() default "У данной функции нет аргументов."; // описание аргументов команды
    String description() default "No description";
    String[] aliases(); // Возможные названия команды
}