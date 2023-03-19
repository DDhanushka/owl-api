package org.ontobot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class Populator {

    private final OWLDataFactory dataFactory;
    private final OWLOntologyManager manager;

    IRI ontologyIRI;

    OWLOntology ontology;

    public Populator(OWLOntology ontology, IRI ontologyIRI) {
        // Create the OWLOntologyManager and the OWLDataFactory
        this.manager = OWLManager.createOWLOntologyManager();
        this.dataFactory = manager.getOWLDataFactory();
        this.ontology = ontology;
        this.ontologyIRI = ontologyIRI;
    }

    public void instanceMaker(Set<Map.Entry<String, JsonElement>> instanceMsgObjectData) throws FileNotFoundException, OWLOntologyStorageException {
        OWLNamedIndividual individual = null;
        OWLDataPropertyAssertionAxiom dataPropAssertion = null;
        OWLDataProperty dataProp = null;
        OWLLiteral dataPropLiteral = null;
        Set<Map.Entry<String, JsonElement>> objectMembersSet = null;


        System.out.println("====================== Instance adder ======================");
        for (Map.Entry<String, JsonElement> entry : Objects.requireNonNull(instanceMsgObjectData)) {
            String className = entry.getKey();

            OWLClass owlClass = dataFactory.getOWLClass(IRI.create(ontologyIRI + "#" + className));

            System.out.println(entry.getKey());
            JsonArray jsonArray = entry.getValue().getAsJsonArray();

            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                objectMembersSet = jsonObject.entrySet();

                for (Map.Entry<String, JsonElement> individualsProperty : Objects.requireNonNull((objectMembersSet))) {
                    if (Objects.equals(individualsProperty.getKey(), "# Object Name")) {
                        //  Create an individual for the Student class
                        individual = dataFactory.getOWLNamedIndividual(createIRI("#", individualsProperty.getValue().getAsString()));
                    } else {
                        //  Assign data properties to the individual
                        dataProp = dataFactory.getOWLDataProperty(createIRI("#has", individualsProperty.getKey()));
                        dataPropLiteral = dataFactory.getOWLLiteral(replaceSpaceWithUnderscore(individualsProperty.getValue().getAsString()));
                        if (individual != null) {
                            dataPropAssertion = dataFactory.getOWLDataPropertyAssertionAxiom(dataProp, individual, dataPropLiteral);
                        }
                        // Add the individual and data property assertions to the ontology
                        if (individual != null) {
                            manager.addAxiom(ontology, dataFactory.getOWLDeclarationAxiom(individual));
                        }
                        if (individual != null) {
                            manager.addAxiom(ontology, dataFactory.getOWLClassAssertionAxiom(owlClass, individual));
                        }
                        assert dataPropAssertion != null;
                        manager.addAxiom(ontology, dataPropAssertion);
                    }
                }
            }
        }
        //  String outputOwlFileName = "OWL-INDIV-" + session.substring(1, session.length() - 1) + ".owl";
        File fileOut = new File("src/OWLOutput/" + "OWL-indvidual.owl");
        manager.saveOntology(ontology, new FunctionalSyntaxDocumentFormat(), new FileOutputStream(fileOut));
    }

    private static String replaceSpaceWithUnderscore(String str) {
        return str.replace(" ", "_");
    }

    private IRI createIRI(String prefix, String value) {
        return IRI.create(this.ontologyIRI + prefix + replaceSpaceWithUnderscore(value));
    }
}
