package ua.abond.guice.service.impl;

import ua.abond.guice.service.IdiotService;

public class IdiotServiceImpl implements IdiotService {

    @Override
    public void doStupidStuff() {
        System.out.println("stupid");
    }
}
