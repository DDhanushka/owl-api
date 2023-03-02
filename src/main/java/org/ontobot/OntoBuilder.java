package org.ontobot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

public class OntoBuilder {
    private OWLOntologyManager manager;
    private OWLDataFactory dataFactory;
    private IRI ontologyIRI;
    private OWLOntology ontology;
    private Hashtable<String, OWLClass> hashMap = new Hashtable<>();
    private List<String> addedConcepts = new ArrayList<>();

    public OntoBuilder() throws OWLOntologyCreationException {
        // Create the OWLOntologyManager and the OWLDataFactory
        this.manager = OWLManager.createOWLOntologyManager();
        this.dataFactory = manager.getOWLDataFactory();

        // Create the ontology and the namespace IRI
        this.ontologyIRI = IRI.create("http://example.com/ontology");
        this.ontology = this.manager.createOntology(this.ontologyIRI);
    }

    public void build(String[] concepts, JsonArray taxonomies){
        try {
            // Define concepts
            for (String concept : concepts) {
                String class_name = concept.substring(1, concept.length() - 1);
                System.out.println(class_name);
                OWLClass clazz = this.dataFactory.getOWLClass(IRI.create(this.ontologyIRI + "#" + class_name.replace(" ", "_")));
                this.hashMap.put(class_name, clazz);
                this.manager.addAxiom(this.ontology, this.dataFactory.getOWLDeclarationAxiom(clazz));
            }

            // Define Taxonomies with Data properties
            for (JsonElement taxonomy: taxonomies) {
                JsonObject classObject = taxonomy.getAsJsonObject();
                String className = classObject.get("class_name").getAsString(); // superClass
                JsonArray attributes = classObject.get("attributes").getAsJsonArray();

                if (!addedConcepts.contains(className)){
                    for (JsonElement attr : attributes) {
                        JsonObject attrObj = attr.getAsJsonObject();
                        String name = attrObj.get("name").getAsString();
                        String type = attrObj.get("datatype").getAsString();
                        boolean isFunctional = attrObj.get("functional").getAsBoolean();

                        OWLDataProperty dataProperty = this.dataFactory.getOWLDataProperty(IRI.create(this.ontologyIRI + "#" + name.replace(" ", "_")));

                        if (isFunctional){
                            OWLFunctionalDataPropertyAxiom axiom = this.dataFactory.getOWLFunctionalDataPropertyAxiom(dataProperty);
                            manager.addAxiom(this.ontology, axiom);
                        }

                        OWLDataPropertyDomainAxiom domainProperty = this.dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, this.hashMap.get(className));
                        OWLDataPropertyRangeAxiom rangeProperty = this.dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, this.dataFactory.getOWLDatatype(getPropertyType(type)));
                        manager.addAxiom(this.ontology, domainProperty);
                        manager.addAxiom(this.ontology, rangeProperty);
                    }
                }

                if (classObject.has("sub_classes")){
                    JsonArray subClasses = classObject.get("sub_classes").getAsJsonArray();
                    for (JsonElement subClass: subClasses) {
                        JsonObject subClassObject = subClass.getAsJsonObject();
                        String subClassName = subClassObject.get("class_name").getAsString(); // subClass
                        JsonArray subAttributes = subClassObject.get("attributes").getAsJsonArray();
                        OWLClass subClazz = this.hashMap.get(subClassName);
                        OWLClass supClazz = this.hashMap.get(className);
                        this.manager.addAxiom(this.ontology, this.dataFactory.getOWLSubClassOfAxiom(subClazz, supClazz));

                        if (!addedConcepts.contains(subClassName)){
                            for (JsonElement attr : subAttributes) {
                                JsonObject attrObj = attr.getAsJsonObject();
                                String name = attrObj.get("name").getAsString();
                                String type = attrObj.get("datatype").getAsString();
                                boolean isFunctional = attrObj.get("functional").getAsBoolean();

                                OWLDataProperty dataProperty = dataFactory.getOWLDataProperty(IRI.create(this.ontologyIRI + "#" + name.replace(" ", "_")));

                                if (isFunctional){
                                    OWLFunctionalDataPropertyAxiom axiom = this.dataFactory.getOWLFunctionalDataPropertyAxiom(dataProperty);
                                    manager.addAxiom(this.ontology, axiom);
                                }

                                OWLDataPropertyDomainAxiom domainProperty = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, this.hashMap.get(subClassName));
                                OWLDataPropertyRangeAxiom rangeProperty = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataFactory.getOWLDatatype(getPropertyType(type)));
                                manager.addAxiom(this.ontology, domainProperty);
                                manager.addAxiom(this.ontology, rangeProperty);
                            }
                        }
                        this.addedConcepts.add(subClassName);
                    }

                    this.addedConcepts.add(className);
                }

            }

            // Define Object properties


            // Save the ontology to a file and check the consistency
            saveOntology(this.ontology);
            checkConsistency(this.ontology);

        }catch (Exception e){
            System.out.println(e.toString());
        }
    }

    private OWL2Datatype getPropertyType(String type){
        switch (type){
            case "integer":
                return OWL2Datatype.XSD_INT;

            case "string":
                return OWL2Datatype.XSD_STRING;

            case "boolean":
                return OWL2Datatype.XSD_BOOLEAN;

            case "float":
                return OWL2Datatype.XSD_FLOAT;

            case "date/time":
                return OWL2Datatype.XSD_DATE_TIME;

            case "duration":
                return OWL2Datatype.XSD_LANGUAGE;

            default:
                return OWL2Datatype.XSD_ANY_URI;
        }
    }

    private void checkConsistency(OWLOntology fetchedOntology){
        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(fetchedOntology);

        // check consistency
        boolean isConsistent = reasoner.isConsistent();
        if (isConsistent) {
            System.out.println("The ontology is consistent.");
        } else {
            System.out.println("The ontology is inconsistent.");
            // print the unsatisfiable classes
            Set<OWLClass> unsatisfiableClasses = reasoner.getUnsatisfiableClasses().getEntities();
            System.out.println("Unsatisfiable classes: " + unsatisfiableClasses);
        }

        // dispose the reasoner
        reasoner.dispose();
    }

    private void saveOntology(OWLOntology fetchedOntology) throws FileNotFoundException, OWLOntologyStorageException {
        // Save the ontology to a file
        String outputOwlFileName = "OWL-OUT.owl";
        File fileOut = new File("C://GitHub/owl-API/owl-api/src/OWLOutput/" + outputOwlFileName);
        this.manager.saveOntology(fetchedOntology, new FunctionalSyntaxDocumentFormat(), new FileOutputStream(fileOut));
    }
}
