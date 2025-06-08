package com.garage.util;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public abstract class Middleware {
    
    private Middleware next;

    public static Middleware link(Middleware first, Middleware... chain) {
        Middleware head = first;
        for (Middleware nextInChain: chain) {
            head.next = nextInChain;
            head = nextInChain;
        }
        return first;
    }
    

    public abstract boolean process(Map<String, Object> events);

    protected boolean next(Map<String, Object> events) {
        if (next == null) {
            return true;
        }
        
        return next.process(events);
    }

}
