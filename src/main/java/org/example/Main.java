package org.example;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {

//        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
//        File fileout = new File("owl-file.xml");
//        IRI pizza = IRI.create("https://protege.stanford.edu/ontologies/pizza/pizza.owl");
//        OWLOntology o = man.loadOntology(pizza);
//        man.saveOntology(o, new FunctionalSyntaxDocumentFormat(), new FileOutputStream(fileout));
//        System.out.println(o);

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        IRI ontologyIRI = IRI.create("http://www.co-ode.org/ontologies/testont.owl");
        IRI documentIRI = IRI.create("file:/tmp/MyOnt.owl");

        SimpleIRIMapper mapper = new SimpleIRIMapper(ontologyIRI, documentIRI);
        manager.addIRIMapper(mapper);

        OWLOntology ontology = manager.createOntology(ontologyIRI);

        OWLDataFactory factory = manager.getOWLDataFactory();

        OWLClass clasA = factory.getOWLClass(IRI.create(ontologyIRI + "#A"));
        OWLClass clasB = factory.getOWLClass(IRI.create(ontologyIRI + "#B"));

        OWLAxiom axiom = factory.getOWLSubClassOfAxiom(clasA, clasB);
        AddAxiom addAxiom = new AddAxiom(ontology, axiom);

        manager.applyChange(addAxiom);

        for (OWLClass cls : ontology.getClassesInSignature()) {
            System.out.println("Referenced class:" + cls);
        }
        File fileout = new File("src/OWLOutput/owl-file.xml");

        manager.saveOntology(ontology, new FunctionalSyntaxDocumentFormat(), new FileOutputStream(fileout));


//        saveOntology(o, new FunctionalSyntaxDocumentFormat(), new FileOutputStream(fileout));

//        get("/", (req, res) -> "Hello World");
//
//        get("/owl", (req, res) -> "Owler");
    }

}