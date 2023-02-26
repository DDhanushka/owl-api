package org.ontobot;

import com.google.gson.JsonArray;

public class Main {
    public static void main(String[] args) {
        String filepath = "/home/ddhash/IdeaProjects/owl-api/src/main/java/org/ontobot/response.json";
        JsonArray taxonomies = JsonFileReader.GetTaxonomies(filepath);
//        if (taxonomies != null)
//            JsonFileReader.RecPrint(taxonomies);


        // Define the set of classes, relationships, and attributes to add
        String[] classNames = {"Person", "Employee", "Student"};
        String[] relationshipNames = {"hasSupervisor", "hasStudentAdvisor"};
        String[] attributeNames = {"hasAge", "hasSalary"};

        assert taxonomies != null;
        OntologyBuilder.Build(classNames, relationshipNames, attributeNames, taxonomies);


    }
}
