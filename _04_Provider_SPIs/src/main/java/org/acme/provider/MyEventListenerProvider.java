package org.acme.provider;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;

import java.util.List;

/**
 * Created by zhujie on 16/4/27.
 */
public class MyEventListenerProvider implements EventListenerProvider {

    private List<Event> events;

    public MyEventListenerProvider(List<Event> events) {
        this.events = events;
    }

    @Override
    public void onEvent(Event event) {
        events.add(event);
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {

    }

    @Override
    public void close() {

    }
}
