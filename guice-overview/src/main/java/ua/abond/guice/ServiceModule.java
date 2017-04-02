package ua.abond.guice;

import com.google.inject.AbstractModule;
import ua.abond.guice.service.IdiotService;
import ua.abond.guice.service.impl.IdiotServiceImpl;

public class ServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IdiotService.class).to(IdiotServiceImpl.class);
    }
}
