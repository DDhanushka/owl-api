package org.ontobot;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class Ex5 {
    public static void main(String[] args) {

        IRI ontologyIRI = IRI.create("http://example.com/ontology");

        try {


            // Create the ontology manager and OWL factory
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLDataFactory factory = manager.getOWLDataFactory();
            OWLOntology ontology = manager.createOntology(ontologyIRI);

// Create the classes for the ontology
            OWLClass person = factory.getOWLClass(IRI.create("http://example.com/ontology#Person"));
            OWLClass student = factory.getOWLClass(IRI.create("http://example.com/ontology#Student"));
            OWLClass teacher = factory.getOWLClass(IRI.create("http://example.com/ontology#Teacher"));
            OWLClass course = factory.getOWLClass(IRI.create("http://example.com/ontology#Course"));
            OWLClass university = factory.getOWLClass(IRI.create("http://example.com/ontology#University"));

// Define the subclass relationships
            OWLSubClassOfAxiom studentSubClass = factory.getOWLSubClassOfAxiom(student, person);
            manager.addAxiom(ontology, studentSubClass);

            OWLSubClassOfAxiom teacherSubClass = factory.getOWLSubClassOfAxiom(teacher, person);
            manager.addAxiom(ontology, teacherSubClass);

// Add disjoint properties to the subclass axioms
            OWLDisjointClassesAxiom disjointClassesAxiom = factory.getOWLDisjointClassesAxiom(student, teacher);
            manager.addAxiom(ontology, disjointClassesAxiom);

// Define the object properties
            OWLObjectProperty hasStudent = factory.getOWLObjectProperty(IRI.create("http://example.com/ontology#hasStudent"));
            OWLObjectProperty hasTeacher = factory.getOWLObjectProperty(IRI.create("http://example.com/ontology#hasTeacher"));
            OWLObjectProperty teachesCourse = factory.getOWLObjectProperty(IRI.create("http://example.com/ontology#teachesCourse"));

// Define the domain and range for the object properties

            OWLObjectPropertyDomainAxiom hasStudentDomain = factory.getOWLObjectPropertyDomainAxiom(hasStudent, university);
            OWLObjectPropertyRangeAxiom hasStudentRange = factory.getOWLObjectPropertyRangeAxiom(hasStudent, student);
            manager.addAxiom(ontology, hasStudentDomain);
            manager.addAxiom(ontology, hasStudentRange);

            OWLObjectPropertyDomainAxiom hasTeacherDomain = factory.getOWLObjectPropertyDomainAxiom(hasTeacher, university);
            OWLObjectPropertyRangeAxiom hasTeacherRange = factory.getOWLObjectPropertyRangeAxiom(hasTeacher, teacher);
            manager.addAxiom(ontology, hasTeacherDomain);
            manager.addAxiom(ontology, hasTeacherRange);

            OWLObjectPropertyDomainAxiom teachesCourseDomain = factory.getOWLObjectPropertyDomainAxiom(teachesCourse, teacher);
            OWLObjectPropertyRangeAxiom teachesCourseRange = factory.getOWLObjectPropertyRangeAxiom(teachesCourse, course);
            manager.addAxiom(ontology, teachesCourseDomain);
            manager.addAxiom(ontology, teachesCourseRange);

// Define the data properties
            OWLDataProperty hasName = factory.getOWLDataProperty(IRI.create("http://example.com/ontology#hasName"));
            OWLDataProperty hasAge = factory.getOWLDataProperty(IRI.create("http://example.com/ontology#hasAge"));
            OWLDataProperty hasCourseCode = factory.getOWLDataProperty(IRI.create("http://example.com/ontology#hasCourseCode"));

// Define the domain and range for the data properties
            OWLDataPropertyDomainAxiom hasNameDomain = factory.getOWLDataPropertyDomainAxiom(hasName, person);
            OWLDataPropertyRangeAxiom hasNameRange = factory.getOWLDataPropertyRangeAxiom(hasName, factory.getOWLDatatype(OWL2Datatype.XSD_STRING));
            manager.addAxiom(ontology, hasNameDomain);
            manager.addAxiom(ontology, hasNameRange);

            OWLDataPropertyDomainAxiom hasAgeDomain = factory.getOWLDataPropertyDomainAxiom(hasAge, person);
            OWLDataPropertyRangeAxiom hasAgeRange = factory.getOWLDataPropertyRangeAxiom(hasAge, factory.getOWLDatatype(OWL2Datatype.XSD_INT));
            manager.addAxiom(ontology, hasAgeDomain);
            manager.addAxiom(ontology, hasAgeRange);

            OWLDataPropertyDomainAxiom hasCourseCodeDomain = factory.getOWLDataPropertyDomainAxiom(hasCourseCode, course);
            OWLDataPropertyRangeAxiom hasCourseCodeRange = factory.getOWLDataPropertyRangeAxiom(hasCourseCode, factory.getOWLDatatype(OWL2Datatype.XSD_STRING));
            manager.addAxiom(ontology, hasCourseCodeDomain);
            manager.addAxiom(ontology, hasCourseCodeRange);
// Save the ontology to a file
            File fileout = new File("src/OWLOutput/owl-file-5.xml");
            manager.saveOntology(ontology, new FunctionalSyntaxDocumentFormat(), new FileOutputStream(fileout));

        } catch (OWLOntologyCreationException | OWLOntologyStorageException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
