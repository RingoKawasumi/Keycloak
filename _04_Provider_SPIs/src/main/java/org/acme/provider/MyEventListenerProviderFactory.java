package org.acme.provider;

import org.keycloak.Config;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhujie on 16/4/27.
 */
public class MyEventListenerProviderFactory implements EventListenerProviderFactory {

    private List<Event> events;

    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        return new MyEventListenerProvider(events);
    }

    @Override
    public void init(Config.Scope scope) {
        int max = scope.getInt("max");
        events = new ArrayList<>(max);
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return "my-event-listener";
    }
}
