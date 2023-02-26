package org.ontobot;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.FileReader;
import java.io.IOException;

public class JsonFileReader {
    public static void main(String[] args) {
        Gson gson = new Gson();

        try (FileReader reader = new FileReader("/home/ddhash/IdeaProjects/owl-api/src/main/java/org/ontobot/response.json")) {
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            // use jsonObject to access the data
            JsonObject msg = jsonObject.getAsJsonObject("msg");
            JsonArray concepts = msg.getAsJsonArray("concepts");
            JsonArray taxonomies = msg.getAsJsonArray("taxonomy");

            recPrint(taxonomies);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void recPrint(JsonArray taxonomies) {
        for (JsonElement taxo : taxonomies) {
            JsonObject classObject = taxo.getAsJsonObject();
            String class_name = classObject.get("class_name").getAsString();
            int level = classObject.get("level").getAsInt();
            System.out.println(class_name + " - " + level);
            if (classObject.has("sub_classes")) {
                recPrint((JsonArray) classObject.get("sub_classes"));
                System.out.println("-----");
            }

        }
    }
}
