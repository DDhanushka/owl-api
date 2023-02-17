package org.example;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.*;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException {
        System.out.println("Hello world!");
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.loadOntology(IRI.create("https://protege.stanford.edu/ontologies/pizza/pizza.owl"));
        ontology.saveOntology(new FunctionalSyntaxDocumentFormat(), System.out);
        get("/", (req, res) -> "Hello World");

    }
}