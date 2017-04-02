package ua.abond.guice.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import ua.abond.guice.service.IdiotService;

@Singleton
public class IdiotController {
    private final IdiotService idiotService;

    @Inject
    public IdiotController(IdiotService idiotService) {
        this.idiotService = idiotService;
    }

    public void dumbDelegation() {
        idiotService.doStupidStuff();
    }
}
