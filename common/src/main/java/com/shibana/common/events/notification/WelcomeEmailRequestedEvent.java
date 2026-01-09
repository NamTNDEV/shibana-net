package com.shibana.common.events.notification;

public record WelcomeEmailRequestedEvent(
        String name,
        String email
) {
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
}
