package org.ontobot;

import com.google.gson.JsonElement;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Populator {

    private Set<Map.Entry<String, JsonElement>> cache;

    public Populator() {

    }

    public void popFunc() {
        String path = "src/main/java/org/ontobot/individuals.json";
        this.cache = JsonFileReader.getIndividuals(path);
        for (Map.Entry<String, JsonElement> entry : Objects.requireNonNull(cache)) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
            System.out.println("------");
        }
    }
}
