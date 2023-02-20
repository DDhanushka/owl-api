package org.example;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class CreateOnto {
    public static void main(String[] args) {
        // Create an OWL ontology manager
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        try {
            // Create an OWL ontology
            OWLOntology ontology = manager.createOntology(IRI.create("http://example.com/ontology"));

            OWLDataFactory factory = manager.getOWLDataFactory();

            // Create a subclass of an existing class
            OWLClass parentClass = factory.getOWLClass(IRI.create("http://example.com/ontology#ParentClass"));
            OWLClass childClass = factory.getOWLClass(IRI.create("http://example.com/ontology#ChildClass"));
            OWLSubClassOfAxiom subclassAxiom = factory.getOWLSubClassOfAxiom(childClass, parentClass);
            manager.addAxiom(ontology, subclassAxiom);

            // Create a new class
            OWLClass newClass = factory.getOWLClass(IRI.create("http://example.com/ontology#NewClass"));
            OWLDeclarationAxiom declarationAxiom = factory.getOWLDeclarationAxiom(newClass);
            manager.addAxiom(ontology, declarationAxiom);

            // Save the ontology to a file
            File fileout = new File("src/OWLOutput/owl-file2.xml");
            manager.saveOntology(ontology, new FunctionalSyntaxDocumentFormat(), new FileOutputStream(fileout));

        } catch (OWLOntologyCreationException | OWLOntologyStorageException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
