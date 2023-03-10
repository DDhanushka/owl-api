package org.ontobot;

import com.google.gson.JsonArray;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.util.Set;

import java.io.FileNotFoundException;

public class PopTest {
    public static void main(String[] args) throws FileNotFoundException, OWLOntologyStorageException, OWLOntologyCreationException {
        String filepath = "src/main/java/org/ontobot/newresponse.json";
        JsonArray taxonomies = JsonFileReader.GetTaxonomies(filepath);
        JsonArray ops = JsonFileReader.GetOps(filepath);
        String[] concepts = JsonFileReader.GetConcepts(filepath);
        String sessionID = JsonFileReader.getSessionID(filepath);

        assert taxonomies != null;

//        OntoBuilder ontoBuilder = new OntoBuilder();
//        ontoBuilder.build(concepts, taxonomies, ops);
//        ontoBuilder.getConsistencyResult();
//        ontoBuilder.saveGeneratedOntology(sessionID);

        pop();
    }


    public static void pop() throws OWLOntologyCreationException {
        // Load the ontology
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new File("src/OWLOutput/OWL-OUT-961e33b-29d3-47ad-89a2-34d04dfff39bab.owl"));

        // Get the list of classes in the ontology
        Set<OWLClass> classes = ontology.getClassesInSignature();

        // For each class, get the list of its data properties
        for (OWLClass cls : classes) {
            Set<OWLDataProperty> dataProperties = cls.getDataPropertiesInSignature();

            // Print the class name and its data properties
            System.out.println("Class: " + cls.getIRI().getShortForm());
            for (OWLDataProperty prop : dataProperties) {
                System.out.println("\tData Property: " + prop.getIRI().getShortForm());
            }
        }
    }


}
