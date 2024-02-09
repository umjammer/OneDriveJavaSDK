package de.tuberlin.onedrivesdk.common;

/**
 * Class can be implemented to receive Exceptions from other Threads
 */
public interface ExceptionEventHandler {

    void handle(Exception e);
    void handle(Object src, Exception e);
}
