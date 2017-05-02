package ua.abond.spring.spel;

import lombok.Setter;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.Map;

@Setter
public class Main {
    public Map<String, List<String>> strings;

    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("spel-context.xml");

        Main main = (Main) applicationContext.getBean("main");
        System.out.println(main.strings);
        System.out.println(main.strings.get("ccc").get(0));

        applicationContext.close();
    }
}
