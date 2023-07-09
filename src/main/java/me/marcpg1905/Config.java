package me.marcpg1905;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class Config {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final JsonNode CONFIG; static {
        try { CONFIG = MAPPER.readTree(new File("config.json")); }
        catch (IOException e) { throw new RuntimeException(e); }
    }

    public static String get(String field) {
        return CONFIG.get(field).asText();
    }
}
