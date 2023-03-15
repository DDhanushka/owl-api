package org.ontobot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.providers.NamedIndividualProvider;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Populator {

    private Set<Map.Entry<String, JsonElement>> cache;

    public Populator() {

    }

    public void popFunc(IRI ontologyIRI) {
        System.out.println("====================== Population Running ======================");
        String path = "src/main/java/org/ontobot/individuals.json";
        this.cache = JsonFileReader.getIndividuals(path);
        for (Map.Entry<String, JsonElement> entry : Objects.requireNonNull(cache)) {

            System.out.println(entry.getKey());
            JsonArray jsonArray = entry.getValue().getAsJsonArray();

            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                String instanceName = String.valueOf(jsonObject.get("# Object Name"));
                Set<Map.Entry<String, JsonElement>> objectMembersSet = jsonObject.entrySet();

                for (Map.Entry<String, JsonElement> individualsProperty : Objects.requireNonNull((objectMembersSet))) {
                    System.out.println(individualsProperty.getKey() + " has " + individualsProperty.getValue());
                }

//                 Create an individual for the Student class
                NamedIndividualProvider factory = OWLManager.createOWLOntologyManager().getOWLDataFactory();
                OWLNamedIndividual individual = factory.getOWLNamedIndividual(ontologyIRI + instanceName);
//
//// Assign data properties to the individual
//                OWLDataProperty hasName = factory.getOWLDataProperty(IRI.create("http://example.com/ontology#hasName"));
//                OWLLiteral nameLiteral = factory.getOWLLiteral("John");
//                OWLDataPropertyAssertionAxiom hasNameAssertion = factory.getOWLDataPropertyAssertionAxiom(hasName, studentIndividual, nameLiteral);
//
//                OWLDataProperty hasAge = factory.getOWLDataProperty(IRI.create("http://example.com/ontology#hasAge"));
//                OWLLiteral ageLiteral = factory.getOWLLiteral(20);
//                OWLDataPropertyAssertionAxiom hasAgeAssertion = factory.getOWLDataPropertyAssertionAxiom(hasAge, studentIndividual, ageLiteral);
//
//// Add the individual and data property assertions to the ontology
//                manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(studentIndividual));
//                manager.addAxiom(ontology, factory.getOWLClassAssertionAxiom(studentClass, studentIndividual));
//                manager.addAxiom(ontology, hasNameAssertion);
//                manager.addAxiom(ontology, hasAgeAssertion);

            }
            System.out.println("------");
        }
    }
}
