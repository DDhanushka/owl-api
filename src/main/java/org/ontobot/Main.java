package org.ontobot;

import com.google.gson.JsonArray;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import java.io.FileNotFoundException;

import static spark.Spark.port;
import static spark.Spark.post;

public class Main {
    public static void NotMain(String[] args) throws OWLOntologyCreationException, FileNotFoundException, OWLOntologyStorageException {
        String filepath = "src/main/java/org/ontobot/newresponse.json";
        JsonArray taxonomies = JsonFileReader.GetTaxonomies(filepath);
        JsonArray ops = JsonFileReader.GetOps(filepath);
        String[] concepts = JsonFileReader.GetConcepts(filepath);
        String sessionID = JsonFileReader.getSessionID(filepath);

        assert taxonomies != null;

        OntoBuilder ontoBuilder = new OntoBuilder();

        // generate taxonomy stage ontology
        //ontoBuilder.build(concepts, taxonomies);
        //ontoBuilder.saveGeneratedOntology();

//        // check consistency of taxonomy level stage
//        ontoBuilder.build(concepts, taxonomies);
//        ontoBuilder.getConsistencyResult();
//
//        // check consistency of simple op level stage and advanced level stage

        ontoBuilder.build(concepts, taxonomies, ops);
        ontoBuilder.getConsistencyResult();
//
//        // generate final stage ontology
//        ontoBuilder.build(concepts, taxonomies, ops);
        ontoBuilder.saveGeneratedOntology();

    }

    public static void main(String[] args) {
        port(8080);

        // check consistency of simple op level stage and advanced level stage
        post("/checkConsistency", (request, response) -> {
            JsonReqBodyReader jsonReqBodyReader = new JsonReqBodyReader(request);
            JsonArray taxonomies = jsonReqBodyReader.GetTaxonomies();
            JsonArray ops = jsonReqBodyReader.GetObjectProps().getAsJsonArray();
            String[] concepts = jsonReqBodyReader.GetConcepts();

            OntoBuilder ontoBuilder = new OntoBuilder();
            ontoBuilder.build(concepts, taxonomies, ops);
            ontoBuilder.saveGeneratedOntology();

            return ontoBuilder.getConsistencyResult();
        });

    }
}
