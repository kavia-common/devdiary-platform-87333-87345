package com.example.devdiarybackend.integrations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example placeholder adapter used for scaffolding.
 */
public class DummyAdapter implements IntegrationAdapter {
    private static final Logger log = LoggerFactory.getLogger(DummyAdapter.class);
    private String cfg;

    @Override
    public String getKey() {
        return "dummy";
    }

    @Override
    public void connect(String configJson) {
        this.cfg = configJson;
        log.info("Connected dummy adapter with cfg length={}", configJson == null ? 0 : configJson.length());
    }

    @Override
    public void disconnect() {
        log.info("Disconnected dummy adapter");
        this.cfg = null;
    }

    @Override
    public void sync() {
        log.info("Dummy sync triggered");
    }
}
