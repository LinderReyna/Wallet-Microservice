package com.jossemar.swap.microservice.event;

import lombok.Data;

@Data
public class Event <T> {
    private String id;
    private EventType type;
    private T data;
}
