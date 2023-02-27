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
import java.util.Objects;

public class OntologyBuilder {

    private  OWLOntologyManager manager;
    private OWLDataFactory dataFactory ;
    private  IRI ontologyIRI;
    private  OWLOntology ontology;

    private OWLClass owlClass;

    public static void main(String[] args) {
    }

    public OntologyBuilder() throws OWLOntologyCreationException {
        // Create the OWLOntologyManager and the OWLDataFactory
        this.manager = OWLManager.createOWLOntologyManager();
        this.dataFactory = manager.getOWLDataFactory();

        // Create the ontology and the namespace IRI
        this.ontologyIRI = IRI.create("http://example.com/ontology");
        this.ontology = manager.createOntology(ontologyIRI);
    }

    public void Build(String[] classNames, String[] relationshipNames, String[] attributeNames, JsonArray taxonomies) {



        try {


//            // Loop through the classes and add them to the ontology
//            for (String className : classNames) {
//                OWLClass clazz = dataFactory.getOWLClass(IRI.create(ontologyIRI + "#" + className));
//                manager.addAxiom(ontology, dataFactory.getOWLDeclarationAxiom(clazz));
//            }
//
//            // Add subclass relationships
//            OWLClass person = dataFactory.getOWLClass(IRI.create(ontologyIRI + "#Person"));
//            OWLClass employee = dataFactory.getOWLClass(IRI.create(ontologyIRI + "#Employee"));
//            OWLClass student = dataFactory.getOWLClass(IRI.create(ontologyIRI + "#Student"));
//            manager.addAxiom(ontology, dataFactory.getOWLSubClassOfAxiom(employee, person));
//            manager.addAxiom(ontology, dataFactory.getOWLSubClassOfAxiom(student, person));

//            for (JsonElement taxo : taxonomies) {
//                JsonObject classObject = taxo.getAsJsonObject();
//                String class_name = classObject.get("class_name").getAsString();
//                int level = classObject.get("level").getAsInt();
//                //
//                OWLClass superClass = dataFactory.getOWLClass(IRI.create(ontologyIRI + "#" + class_name));
//                manager.addAxiom(ontology, dataFactory.getOWLDeclarationAxiom(superClass));
//                System.out.println(class_name + " - " + level);
//
//                if (classObject.has("sub_classes")) {
//                    for (JsonElement subTaxo : classObject.get("sub_classes").getAsJsonArray()) {
//                        JsonObject classObject2 = subTaxo.getAsJsonObject();
//                        String class_name2 = classObject2.get("class_name").getAsString();
//                        int level2 = classObject2.get("level").getAsInt();
//                        //
//                        OWLClass subClass = dataFactory.getOWLClass(IRI.create(ontologyIRI + "#" + class_name2));
//                        manager.addAxiom(ontology, dataFactory.getOWLSubClassOfAxiom(subClass, superClass));
//                        System.out.println(class_name2 + " - " + level2);
//                    }
//                    System.out.println("-----");
//                }
//
//            }

            AddClasses(taxonomies, "");


            // Loop through the relationships and add them to the ontology
//            for (String relationshipName : relationshipNames) {
//                OWLObjectProperty property = dataFactory.getOWLObjectProperty(IRI.create(ontologyIRI + "#" + relationshipName));
//                OWLClass domainClass = dataFactory.getOWLClass(IRI.create(ontologyIRI + "#Employee"));
//                OWLClass rangeClass = dataFactory.getOWLClass(IRI.create(ontologyIRI + "#Person"));
//
//                // Set minimum cardinality constraint to 1
//                OWLCardinalityRestriction hasSupervisorMinCardinality = dataFactory.getOWLObjectMinCardinality(1, property, domainClass);
//                manager.addAxiom(ontology, dataFactory.getOWLSubClassOfAxiom(domainClass, hasSupervisorMinCardinality));
//
//                // Set maximum cardinality constraint to 1
//                OWLCardinalityRestriction hasStudentAdvisorMaxCardinality = dataFactory.getOWLObjectMaxCardinality(1, property, domainClass);
//                manager.addAxiom(ontology, dataFactory.getOWLSubClassOfAxiom(domainClass, hasStudentAdvisorMaxCardinality));
//
//                manager.addAxiom(ontology, dataFactory.getOWLObjectPropertyDomainAxiom(property, domainClass));
//                manager.addAxiom(ontology, dataFactory.getOWLObjectPropertyRangeAxiom(property, rangeClass));
//            }
//
//            // Loop through the attributes and add them to the ontology
//            for (String attributeName : attributeNames) {
//                OWLDataProperty property = dataFactory.getOWLDataProperty(IRI.create(ontologyIRI + "#" + attributeName));
//                OWLClass domainClass = dataFactory.getOWLClass(IRI.create(ontologyIRI + "#Person"));
//                manager.addAxiom(ontology, dataFactory.getOWLDataPropertyDomainAxiom(property, domainClass));
//                manager.addAxiom(ontology, dataFactory.getOWLDataPropertyRangeAxiom(property, dataFactory.getOWLDatatype(OWL2Datatype.XSD_INT.getIRI())));
//            }

            // Save the ontology to a file
            String outputOwlFileName = "OWL-OUT.xml";
            File fileout = new File("src/OWLOutput/" + outputOwlFileName);
            manager.saveOntology(ontology, new FunctionalSyntaxDocumentFormat(), new FileOutputStream(fileout));

        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void AddClasses(JsonArray taxonomies, String superClazz) {

        for (JsonElement taxo : taxonomies) {
            JsonObject classObject = taxo.getAsJsonObject();
            String class_name = classObject.get("class_name").getAsString();
            int level = classObject.get("level").getAsInt();


            try {
                if (Objects.equals(superClazz, "")) {
                    System.out.println(class_name);
                    System.out.println(ontologyIRI + "#" + class_name);
                    OWLClass clazz = this.dataFactory.getOWLClass(IRI.create(ontologyIRI + "#" + class_name.replace(" ", "_")));
                    manager.addAxiom(this.ontology, dataFactory.getOWLDeclarationAxiom(clazz));
//                    System.out.println(ontologyIRI + "#" + "abc");
                } else {
                    OWLClass subClazz = dataFactory.getOWLClass(IRI.create(ontologyIRI + "#" + class_name.replace(" ", "_")));
                    OWLClass supClazz = dataFactory.getOWLClass(IRI.create(ontologyIRI + "#" + superClazz.replace(" ", "_")));
                    manager.addAxiom(this.ontology, dataFactory.getOWLSubClassOfAxiom(subClazz, supClazz));
                    System.out.println(class_name + " -> " + superClazz);

                }
            } catch (NullPointerException e) {
                System.out.println("NullPointerException thrown!"+e);
            }


            if (classObject.has("sub_classes")) {
                AddClasses(classObject.get("sub_classes").getAsJsonArray(), class_name);
                System.out.println("-----");
            }

        }
    }


}


