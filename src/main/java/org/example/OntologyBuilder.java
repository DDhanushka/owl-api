package org.example;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class OntologyBuilder {

    public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException {

        // Create the OWLOntologyManager and the OWLDataFactory
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory dataFactory = manager.getOWLDataFactory();

        try {
            // Create the ontology and the namespace IRI
            IRI ontologyIRI = IRI.create("http://example.com/ontology");
            OWLOntology ontology = manager.createOntology(ontologyIRI);

            // Define the set of classes, relationships, and attributes to add
            String[] classNames = {"Person", "Employee", "Student"};
            String[] relationshipNames = {"hasSupervisor", "hasStudentAdvisor"};
            String[] attributeNames = {"hasAge", "hasSalary"};

            // Loop through the classes and add them to the ontology
            for (String className : classNames) {
                OWLClass clazz = dataFactory.getOWLClass(IRI.create(ontologyIRI + "#" + className));
                manager.addAxiom(ontology, dataFactory.getOWLDeclarationAxiom(clazz));
            }

            // Add subclass relationships
            OWLClass person = dataFactory.getOWLClass(IRI.create(ontologyIRI + "#Person"));
            OWLClass employee = dataFactory.getOWLClass(IRI.create(ontologyIRI + "#Employee"));
            OWLClass student = dataFactory.getOWLClass(IRI.create(ontologyIRI + "#Student"));
            manager.addAxiom(ontology, dataFactory.getOWLSubClassOfAxiom(employee, person));
            manager.addAxiom(ontology, dataFactory.getOWLSubClassOfAxiom(student, person));


            // Loop through the relationships and add them to the ontology
            for (String relationshipName : relationshipNames) {
                OWLObjectProperty property = dataFactory.getOWLObjectProperty(IRI.create(ontologyIRI + "#" + relationshipName));
                OWLClass domainClass = dataFactory.getOWLClass(IRI.create(ontologyIRI + "#Employee"));
                OWLClass rangeClass = dataFactory.getOWLClass(IRI.create(ontologyIRI + "#Person"));

                // Set minimum cardinality constraint to 1
                OWLCardinalityRestriction hasSupervisorMinCardinality = dataFactory.getOWLObjectMinCardinality(1, property, domainClass);
                manager.addAxiom(ontology, dataFactory.getOWLSubClassOfAxiom(domainClass, hasSupervisorMinCardinality));

                // Set maximum cardinality constraint to 1
                OWLCardinalityRestriction hasStudentAdvisorMaxCardinality = dataFactory.getOWLObjectMaxCardinality(1, property, domainClass);
                manager.addAxiom(ontology, dataFactory.getOWLSubClassOfAxiom(domainClass, hasStudentAdvisorMaxCardinality));

                manager.addAxiom(ontology, dataFactory.getOWLObjectPropertyDomainAxiom(property, domainClass));
                manager.addAxiom(ontology, dataFactory.getOWLObjectPropertyRangeAxiom(property, rangeClass));
            }

            // Loop through the attributes and add them to the ontology
            for (String attributeName : attributeNames) {
                OWLDataProperty property = dataFactory.getOWLDataProperty(IRI.create(ontologyIRI + "#" + attributeName));
                OWLClass domainClass = dataFactory.getOWLClass(IRI.create(ontologyIRI + "#Person"));
                manager.addAxiom(ontology, dataFactory.getOWLDataPropertyDomainAxiom(property, domainClass));
                manager.addAxiom(ontology, dataFactory.getOWLDataPropertyRangeAxiom(property, dataFactory.getOWLDatatype(OWL2Datatype.XSD_INT.getIRI())));
            }

            // Save the ontology to a file
            File fileout = new File("src/OWLOutput/owl-file4.xml");
            manager.saveOntology(ontology, new FunctionalSyntaxDocumentFormat(), new FileOutputStream(fileout));

        } catch (OWLOntologyCreationException | OWLOntologyStorageException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


    }
}
