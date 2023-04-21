package org.ontobot;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import spark.Request;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class JsonReqBodyReader {

    private final JsonObject msg;

    public JsonReqBodyReader(Request request) {
        Gson gson = new Gson();
        JsonObject body = gson.fromJson(request.body(), JsonObject.class);
        this.msg = body.getAsJsonObject("msg");
    }

    public JsonArray GetTaxonomies() {
        return msg.getAsJsonArray("taxonomy");
    }

    public String[] GetConcepts() {
        JsonArray jsonArray = msg.getAsJsonArray("concepts");

        String[] stringArray = new String[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            stringArray[i] = jsonArray.get(i).toString();
        }
        return stringArray;
    }

    public JsonArray GetObjectProps() {
        return msg.getAsJsonArray("op");
    }

    public String getSessionID() {
        return msg.get("sessionID").getAsString();

    }

    public Set<Map.Entry<String, JsonElement>> getIndividuals() {
        JsonObject individuals = this.msg.getAsJsonObject("populate");
        // return msg object from json
        return individuals.entrySet();
    }

}