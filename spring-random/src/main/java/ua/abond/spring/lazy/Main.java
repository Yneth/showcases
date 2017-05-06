package ua.abond.spring.lazy;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("app-context.xml");

        applicationContext.getBean("a");

        applicationContext.close();
    }
}
