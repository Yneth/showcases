package ua.abond.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import ua.abond.guice.controller.IdiotController;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new ServiceModule());
        IdiotController instance = injector.getInstance(IdiotController.class);
        instance.dumbDelegation();

        Some some = injector.getInstance(Some.class);
        some.some();
    }
}
