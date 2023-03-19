package org.ontobot;

import com.google.gson.JsonArray;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;

import static spark.Spark.port;
import static spark.Spark.post;

public class Main {
    public static void runUsingFileRead(String[] args) throws OWLOntologyCreationException, FileNotFoundException, OWLOntologyStorageException {
        String filepath = "src/main/java/org/ontobot/newresponse.json";
        JsonArray taxonomies = JsonFileReader.GetTaxonomies(filepath);
        JsonArray ops = JsonFileReader.GetOps(filepath);
        String[] concepts = JsonFileReader.GetConcepts(filepath);
        String sessionID = JsonFileReader.getSessionID(filepath);

        assert taxonomies != null;

        OntoBuilder ontoBuilder = new OntoBuilder();
        ontoBuilder.build(concepts, taxonomies, ops);
        ontoBuilder.getConsistencyResult();
        ontoBuilder.saveGeneratedOntology(sessionID);

    }

    public static void main(String[] args) {
        port(8080);

//  http://java_backend_url/taxonomy/generate
//  generate taxonomy stage ontology

        post("/taxonomy/generate", (request, response) -> {
            JsonReqBodyReader jsonReqBodyReader = new JsonReqBodyReader(request);
            JsonArray taxonomies = jsonReqBodyReader.GetTaxonomies();
            String[] concepts = jsonReqBodyReader.GetConcepts();
            String sessionID = jsonReqBodyReader.getSessionID();

            OntoBuilder ontoBuilder = new OntoBuilder();
            ontoBuilder.build(concepts, taxonomies);

            // Get the path to the XML file on disk
            String filePath = String.valueOf(ontoBuilder.saveGeneratedOntology(sessionID));

            // Read the contents of the file into a byte array
            byte[] fileBytes = Files.readAllBytes(new File(filePath).toPath());

            // Set the content type to "application/xml"
            response.type("application/xml");

            // Write the file bytes to the response output stream
            response.raw().getOutputStream().write(fileBytes);

            // Return an empty string, since we've already written the response
            return "";
        });

//  http://java_backend_url/taxonomy/validate
//  check consistency of taxonomy level stage

        post("/taxonomy/validate", (request, response) -> {
            JsonReqBodyReader jsonReqBodyReader = new JsonReqBodyReader(request);
            JsonArray taxonomies = jsonReqBodyReader.GetTaxonomies();
            String[] concepts = jsonReqBodyReader.GetConcepts();

            OntoBuilder ontoBuilder = new OntoBuilder();
            ontoBuilder.build(concepts, taxonomies);

            return ontoBuilder.getConsistencyResult();
        });


//  http://java_backend_url/simpleOP/validate
//  check consistency of simple op level stage and advanced level stage

        post("/simpleOP/validate", (request, response) -> {
            JsonReqBodyReader jsonReqBodyReader = new JsonReqBodyReader(request);
            JsonArray taxonomies = jsonReqBodyReader.GetTaxonomies();
            JsonArray ops = jsonReqBodyReader.GetObjectProps().getAsJsonArray();
            String[] concepts = jsonReqBodyReader.GetConcepts();
            String sessionID = jsonReqBodyReader.getSessionID();

            OntoBuilder ontoBuilder = new OntoBuilder();
            ontoBuilder.build(concepts, taxonomies, ops);
            ontoBuilder.saveGeneratedOntology(sessionID);

            return ontoBuilder.getConsistencyResult();
        });

//  http://java_backend_url/completeOWL/validate
//  validate final stage ontology

        post("/completeOWL/validate", (request, response) -> {
            JsonReqBodyReader jsonReqBodyReader = new JsonReqBodyReader(request);
            JsonArray taxonomies = jsonReqBodyReader.GetTaxonomies();
            JsonArray ops = jsonReqBodyReader.GetObjectProps().getAsJsonArray();
            String[] concepts = jsonReqBodyReader.GetConcepts();
            String sessionID = jsonReqBodyReader.getSessionID();

            OntoBuilder ontoBuilder = new OntoBuilder();
            ontoBuilder.build(concepts, taxonomies, ops);
            ontoBuilder.saveGeneratedOntology(sessionID);

            return ontoBuilder.getConsistencyResult();
        });

//  http://java_backend_url/completeOWL/generate
//  generate final stage ontology

        post("/completed/generate", (request, response) -> {
            JsonReqBodyReader jsonReqBodyReader = new JsonReqBodyReader(request);
            JsonArray taxonomies = jsonReqBodyReader.GetTaxonomies();
            String[] concepts = jsonReqBodyReader.GetConcepts();
            String sessionID = jsonReqBodyReader.getSessionID();
            JsonArray ops = jsonReqBodyReader.GetObjectProps().getAsJsonArray();

            OntoBuilder ontoBuilder = new OntoBuilder();
            ontoBuilder.build(concepts, taxonomies, ops);

            // Get the path to the XML file on disk
            String filePath = String.valueOf(ontoBuilder.saveGeneratedOntology(sessionID));

            // Read the contents of the file into a byte array
            byte[] fileBytes = Files.readAllBytes(new File(filePath).toPath());

            // Set the content type to "application/xml"
            response.type("application/xml");

            // Write the file bytes to the response output stream
            response.raw().getOutputStream().write(fileBytes);

            // Return an empty string, since we've already written the response
            return "";
        });

    }
}
