package org.ontobot;

import com.google.gson.JsonArray;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws OWLOntologyCreationException, FileNotFoundException, OWLOntologyStorageException {
        String filepath = "C://GitHub/owl-API/owl-api/src/main/java/org/ontobot/newresponse.json";
        JsonArray taxonomies = JsonFileReader.GetTaxonomies(filepath);
        JsonArray ops = JsonFileReader.GetOps(filepath);
        String[] concepts = JsonFileReader.GetConcepts(filepath);


        // Define the set of classes, relationships, and attributes to add
        String[] classNames = {"Person", "Employee", "Student"};
        String[] relationshipNames = {"hasSupervisor", "hasStudentAdvisor"};
        String[] attributeNames = {"hasAge", "hasSalary"};

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
//        ontoBuilder.getConsistencyResult();
//
//        // generate final stage ontology
//        ontoBuilder.build(concepts, taxonomies, ops);
//        ontoBuilder.saveGeneratedOntology();

    }
}
