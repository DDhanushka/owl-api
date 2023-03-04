package org.ontobot;

import com.google.gson.JsonArray;

import static spark.Spark.port;
import static spark.Spark.post;

public class Main {
    //    public static void main(String[] args) throws OWLOntologyCreationException {
//
////        get("/hello", (req, res)->"Hello, world");
//
//
//        String filepath = "src/main/java/org/ontobot/newresponse.json";
//        JsonArray taxonomies = JsonFileReader.GetTaxonomies(filepath);
//        String[] concepts = JsonFileReader.GetConcepts(filepath);
////        if (taxonomies != null)
////            JsonFileReader.RecPrint(taxonomies);
//
//
//        // Define the set of classes, relationships, and attributes to add
//        String[] classNames = {"Person", "Employee", "Student"};
//        String[] relationshipNames = {"hasSupervisor", "hasStudentAdvisor"};
//        String[] attributeNames = {"hasAge", "hasSalary"};
//
//        assert taxonomies != null;
////        OntologyBuilder ontologyBuilder = new OntologyBuilder();
////        ontologyBuilder.Build(classNames, relationshipNames, attributeNames, taxonomies);
//
//        OntoBuilder ontoBuilder = new OntoBuilder();
//        ontoBuilder.build(concepts, taxonomies);
//    }
    public static void main(String[] args) {
        port(8080);

        post("/taxonomy/generate", (request, response) -> {
            JsonReqBodyReader jsonReqBodyReader = new JsonReqBodyReader(request);
            JsonArray taxonomies = jsonReqBodyReader.GetTaxonomies();
            String[] concepts = jsonReqBodyReader.GetConcepts();

            OntoBuilder ontoBuilder = new OntoBuilder();
            ontoBuilder.build(concepts, taxonomies);

            return "success";
        });


    }
}
