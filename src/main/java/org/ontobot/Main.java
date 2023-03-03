package org.ontobot;

import com.google.gson.JsonArray;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class Main {
    public static void main(String[] args) throws OWLOntologyCreationException {
        String filepath = "C://GitHub/owl-API/owl-api/src/main/java/org/ontobot/newresponse.json";
        JsonArray taxonomies = JsonFileReader.GetTaxonomies(filepath);
        String[] concepts = JsonFileReader.GetConcepts(filepath);
//        if (taxonomies != null)
//            JsonFileReader.RecPrint(taxonomies);


        // Define the set of classes, relationships, and attributes to add
        String[] classNames = {"Person", "Employee", "Student"};
        String[] relationshipNames = {"hasSupervisor", "hasStudentAdvisor"};
        String[] attributeNames = {"hasAge", "hasSalary"};

        assert taxonomies != null;
//        OntologyBuilder ontologyBuilder = new OntologyBuilder();
//        ontologyBuilder.Build(classNames, relationshipNames, attributeNames, taxonomies);

        OntoBuilder ontoBuilder = new OntoBuilder();
        ontoBuilder.build(concepts, taxonomies);
    }
}
