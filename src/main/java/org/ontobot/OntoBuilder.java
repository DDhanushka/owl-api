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
import java.util.*;

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

    public void build(String[] concepts, JsonArray taxonomies, JsonArray... ops){
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
                JsonArray disjointConcepts = classObject.get("disjoint").getAsJsonArray();

                // set superclass data properties
                if (!addedConcepts.contains(className)){
                    for (JsonElement attr : attributes) {
                        JsonObject attrObj = attr.getAsJsonObject();
                        String name = attrObj.get("name").getAsString();
                        String type = attrObj.get("datatype").getAsString().toLowerCase();
                        boolean isFunctional = attrObj.get("functional").getAsBoolean();

                        OWLDataProperty dataProperty = this.dataFactory.getOWLDataProperty(IRI.create(this.ontologyIRI + "#" + name.replace(" ", "_")));
                        defineDataProperty(dataProperty, className, isFunctional, type);

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
                                String type = attrObj.get("datatype").getAsString().toLowerCase();
                                boolean isFunctional = attrObj.get("functional").getAsBoolean();

                                OWLDataProperty dataProperty = dataFactory.getOWLDataProperty(IRI.create(this.ontologyIRI + "#" + name.replace(" ", "_")));
                                defineDataProperty(dataProperty, subClassName, isFunctional, type);

                            }
                        }
                        this.addedConcepts.add(subClassName);
                    }

                    this.addedConcepts.add(className);
                }

                // set disjoint properties
                if (disjointConcepts.size() > 0){
                    List<OWLClassExpression> disjointList = new ArrayList<>();
                    for (JsonElement disjointSet: disjointConcepts) {
                        if (disjointSet.isJsonArray()) {
                            JsonArray jsonArray = disjointSet.getAsJsonArray(); // convert JsonElement to JsonArray

                            String[] stringArray = new String[jsonArray.size()]; // create new String array with same size as JsonArray

                            for (int i = 0; i < jsonArray.size(); i++) {
                                stringArray[i] = jsonArray.get(i).getAsString(); // convert each JsonElement to String and add to string array
                                disjointList.add(this.hashMap.get(stringArray[i]));
                            }

                            if (disjointList.size() > 0){
                                // create the disjoint classes axiom
                                OWLDisjointClassesAxiom axiom = this.dataFactory.getOWLDisjointClassesAxiom(disjointList);
                                // add the axiom to the ontology
                                manager.addAxiom(ontology, axiom);
                            }

                        }
                    }
                }

            }

            // Define Object properties
            if (ops.length == 0){
                System.out.println("It is empty array");
            }

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

    private void defineDataProperty(OWLDataProperty owlDataProperty, String className, boolean isFunctional, String type){
        if (isFunctional){
            OWLFunctionalDataPropertyAxiom axiom = this.dataFactory.getOWLFunctionalDataPropertyAxiom(owlDataProperty);
            this.manager.addAxiom(this.ontology, axiom);
        }

        OWLDataPropertyDomainAxiom domainProperty = dataFactory.getOWLDataPropertyDomainAxiom(owlDataProperty, this.hashMap.get(className));
        OWLDataPropertyRangeAxiom rangeProperty = dataFactory.getOWLDataPropertyRangeAxiom(owlDataProperty, dataFactory.getOWLDatatype(getPropertyType(type)));
        this.manager.addAxiom(this.ontology, domainProperty);
        this.manager.addAxiom(this.ontology, rangeProperty);
    }

    private void saveOntology(OWLOntology fetchedOntology) throws FileNotFoundException, OWLOntologyStorageException {
        // Save the ontology to a file
        String outputOwlFileName = "OWL-OUT.owl";
        File fileOut = new File("C://GitHub/owl-API/owl-api/src/OWLOutput/" + outputOwlFileName);
        this.manager.saveOntology(fetchedOntology, new FunctionalSyntaxDocumentFormat(), new FileOutputStream(fileOut));
    }
}
