package org.ontobot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import java.io.File;
import java.util.*;

import java.io.FileNotFoundException;

public class PopTest {

//    private Hashtable<String, OWLClass> cache = new Hashtable<>();

//    private Set<Map.Entry<String, JsonElement>> cache;

    public static void main(String[] args) throws FileNotFoundException, OWLOntologyStorageException, OWLOntologyCreationException {
        String filepath = "src/main/java/org/ontobot/newresponse.json";
        JsonArray taxonomies = JsonFileReader.GetTaxonomies(filepath);
        JsonArray ops = JsonFileReader.GetOps(filepath);
        String[] concepts = JsonFileReader.GetConcepts(filepath);
        String sessionID = JsonFileReader.getSessionID(filepath);

        assert taxonomies != null;
        // Load the ontology
//        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
//        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new File("src/OWLOutput/OWL-OUT-961e33b-29d3-47ad-89a2-34d04dfff39bab.owl"));
        OntoBuilder ontoBuilder = new OntoBuilder();
        ontoBuilder.build(concepts, taxonomies, ops);
        ontoBuilder.getConsistencyResult();
//        ontoBuilder.saveGeneratedOntology(sessionID);

        OWLOntology ontology = ontoBuilder.getOntology();
        IRI ontologyIRI = ontoBuilder.getIRI();

        System.out.println(ontologyIRI);

        Populator populator = new Populator();
        populator.popFunc(ontologyIRI);
    }
}