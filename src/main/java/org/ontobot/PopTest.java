package org.ontobot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Set;

public class PopTest {

    public static void main(String[] args) throws FileNotFoundException, OWLOntologyStorageException, OWLOntologyCreationException {
        String filepath = "src/main/java/org/ontobot/newresponse.json";
        String instancePath = "src/main/java/org/ontobot/individuals.json";

        JsonArray taxonomies = JsonFileReader.GetTaxonomies(filepath);
        JsonArray ops = JsonFileReader.GetOps(filepath);
        String[] concepts = JsonFileReader.GetConcepts(filepath);
        String sessionID = JsonFileReader.getSessionID(filepath);
        Set<Map.Entry<String, JsonElement>> instanceMsgObjectData = JsonFileReader.getIndividuals(instancePath);

        OntoBuilder ontoBuilder = new OntoBuilder();
        ontoBuilder.build(concepts, taxonomies, ops);
        ontoBuilder.getConsistencyResult();

        OWLOntology ontology = ontoBuilder.getOntology();
        IRI ontologyIRI = ontoBuilder.getIRI();

        InstanceGenerator instanceGenerator = new InstanceGenerator(ontology, ontologyIRI);
        instanceGenerator.addInstances(instanceMsgObjectData);
    }
}