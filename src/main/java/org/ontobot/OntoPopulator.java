package org.ontobot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class OntoPopulator {
    private OWLOntologyManager manager;
    private OWLDataFactory dataFactory;
    private IRI ontologyIRI;
    private OWLOntology ontology;
    private Hashtable<String, OWLClass> hashMap;

    public OntoPopulator(OWLOntologyManager owlOntologyManager, OWLOntology owlOntology, Hashtable<String, OWLClass> hashtable, IRI iri) throws OWLOntologyCreationException {
        this.manager = owlOntologyManager;
        this.dataFactory = this.manager.getOWLDataFactory();
        this.ontology = owlOntology;
        this.hashMap = hashtable;
        this.ontologyIRI = iri;
    }
    public void populate(Set<Map.Entry<String, JsonElement>> instances) throws Exception{
        OWLNamedIndividual individual = null;
        OWLDataPropertyAssertionAxiom dataPropAssertion = null;
        OWLDataProperty dataProp = null;
        OWLLiteral dataPropLiteral = null;
        Set<Map.Entry<String, JsonElement>> objectMembersSet = null;

        for (Map.Entry<String, JsonElement> entry : Objects.requireNonNull(instances)){
            String className = entry.getKey();
            OWLClass owlClass = this.hashMap.get(className);
            JsonArray jsonArray = entry.getValue().getAsJsonArray();

            System.out.println(entry.toString());

            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                objectMembersSet = jsonObject.entrySet();


                for (Map.Entry<String, JsonElement> individualsProperty : Objects.requireNonNull((objectMembersSet))) {
                    if (Objects.equals(individualsProperty.getKey(), "# Object Name")) {
                        //  Create an individual for the Student class
                        individual = this.dataFactory.getOWLNamedIndividual(createIRI(individualsProperty.getValue().getAsString()));

                        // Add the individual and data property assertions to the ontology
                        if (individual != null) {
                            this.manager.addAxiom(this.ontology, this.dataFactory.getOWLDeclarationAxiom(individual));
                        }
                        if (individual != null) {
                            this.manager.addAxiom(this.ontology, this.dataFactory.getOWLClassAssertionAxiom(owlClass, individual));
                        }

                    } else{
                        //  Assign data properties to the individual
                        dataProp = this.dataFactory.getOWLDataProperty(createIRI(individualsProperty.getKey()));
                        JsonElement individualProperty = individualsProperty.getValue();

                        // check if the JsonElement is a string
                        if (individualProperty.isJsonPrimitive() && individualProperty.getAsJsonPrimitive().isString()) {
                            String jsonString = individualProperty.getAsString();
                            dataPropLiteral = this.dataFactory.getOWLLiteral(replaceSpaceWithUnderscore(jsonString), OWL2Datatype.XSD_STRING);
                        } else if (individualProperty.isJsonPrimitive() && individualProperty.getAsJsonPrimitive().isNumber()){
                            dataPropLiteral = this.dataFactory.getOWLLiteral(replaceSpaceWithUnderscore(individualsProperty.getValue().getAsString()), OWL2Datatype.XSD_INT);
                        } else if (individualProperty.isJsonPrimitive() && individualProperty.getAsJsonPrimitive().isBoolean()){
                            dataPropLiteral = this.dataFactory.getOWLLiteral(replaceSpaceWithUnderscore(individualsProperty.getValue().getAsString()), OWL2Datatype.XSD_BOOLEAN);
                        } else {
                            dataPropLiteral = this.dataFactory.getOWLLiteral(replaceSpaceWithUnderscore(individualsProperty.getValue().getAsString()), OWL2Datatype.XSD_FLOAT);
                        }

                        if (individual != null) {
                            dataPropAssertion = this.dataFactory.getOWLDataPropertyAssertionAxiom(dataProp, individual, dataPropLiteral);
                        }

                        assert dataPropAssertion != null;
                        this.manager.addAxiom(this.ontology, dataPropAssertion);
                    }

                }

            }
        }


    }

    public File saveOntology(String sessionID) throws FileNotFoundException, OWLOntologyStorageException {
        // Save the ontology to a file
        String outputOwlFileName = "OWL-OUT-"+sessionID.substring(1, sessionID.length() - 1)+".owl";
        File fileOut = new File("src/OWLPopulate/" + outputOwlFileName);
        this.manager.saveOntology(this.ontology, new FunctionalSyntaxDocumentFormat(), new FileOutputStream(fileOut));
        return fileOut;
    }

    private String replaceSpaceWithUnderscore(String str) {
        return str.replace(" ", "_");
    }

    private IRI createIRI(String value) {
        return IRI.create(this.ontologyIRI + "#" + replaceSpaceWithUnderscore(value));
    }
}
