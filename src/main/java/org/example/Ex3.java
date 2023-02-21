package org.example;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import static org.semanticweb.owlapi.vocab.OWL2Datatype.XSD_INT;

public class Ex3 {
    public static void main(String[] args) {
        // Create an OWL ontology manager
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        IRI ontologyIRI = IRI.create("http://example.com/ontology");

        try {
            // Create an OWL ontology
            OWLOntology ontology = manager.createOntology(ontologyIRI);

            OWLDataFactory dataFactory = manager.getOWLDataFactory();

            // Create a subclass of an existing class
            OWLClass person = dataFactory.getOWLClass(IRI.create(ontologyIRI + "#Person"));
            OWLClass student = dataFactory.getOWLClass(IRI.create(ontologyIRI + "#Student"));
            OWLClass employee = dataFactory.getOWLClass(IRI.create(ontologyIRI + "#Employee"));

            // define the subclass relationships
            OWLSubClassOfAxiom studentSubClassOfPerson = dataFactory.getOWLSubClassOfAxiom(student, person);
            OWLSubClassOfAxiom employeeSubClassOfPerson = dataFactory.getOWLSubClassOfAxiom(employee, person);
            // add subclass axioms to ontology
            manager.addAxiom(ontology, studentSubClassOfPerson);
            manager.addAxiom(ontology, employeeSubClassOfPerson);

            // Create OWLObjectProperties for the relationships:
            OWLObjectProperty hasSupervisor = dataFactory.getOWLObjectProperty(IRI.create(ontologyIRI + "#hasSupervisor"));

            //  define the domain and range of the object properties:
            OWLClassExpression employeeOrStudent = dataFactory.getOWLObjectUnionOf(employee, student);
            OWLObjectPropertyDomainAxiom domainAxiom = dataFactory.getOWLObjectPropertyDomainAxiom(hasSupervisor, employeeOrStudent);

            OWLClassExpression personOrEmployee = dataFactory.getOWLObjectUnionOf(person, employee);
            OWLObjectPropertyRangeAxiom rangeAxiom = dataFactory.getOWLObjectPropertyRangeAxiom(hasSupervisor, personOrEmployee);
            // add to ontology
            manager.addAxiom(ontology, domainAxiom);
            manager.addAxiom(ontology, rangeAxiom);

            // Create OWLDataProperties for the attributes:
            OWLDataProperty hasAge = dataFactory.getOWLDataProperty(IRI.create("http://example.com/ontology#hasAge"));

            // define the domain and range of the data properties:
            OWLDataPropertyDomainAxiom ageDomainAxiom = dataFactory.getOWLDataPropertyDomainAxiom(hasAge, person);
            OWLDataPropertyRangeAxiom ageRangeAxiom = dataFactory.getOWLDataPropertyRangeAxiom(hasAge, dataFactory.getOWLDatatype(XSD_INT));

            manager.addAxiom(ontology, ageDomainAxiom);
            manager.addAxiom(ontology, ageRangeAxiom);

            // Save the ontology to a file
            File fileout = new File("src/OWLOutput/owl-file3.xml");
            manager.saveOntology(ontology, new FunctionalSyntaxDocumentFormat(), new FileOutputStream(fileout));

        } catch (OWLOntologyCreationException | OWLOntologyStorageException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
