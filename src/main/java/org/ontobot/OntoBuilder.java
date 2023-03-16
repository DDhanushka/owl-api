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

    private int seqNumber = 0;

    public OntoBuilder() throws OWLOntologyCreationException {
        // Create the OWLOntologyManager and the OWLDataFactory
        this.manager = OWLManager.createOWLOntologyManager();
        this.dataFactory = manager.getOWLDataFactory();

        // Create the ontology and the namespace IRI
        this.ontologyIRI = IRI.create("http://example.com/ontology");
        this.ontology = this.manager.createOntology(this.ontologyIRI);
    }

    public void build(String[] concepts, JsonArray taxonomies, JsonArray... ops) {
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
            for (JsonElement taxonomy : taxonomies) {
                JsonObject classObject = taxonomy.getAsJsonObject();
                String className = classObject.get("class_name").getAsString(); // superClass
                String level = classObject.get("level").getAsString();
                JsonArray attributes = classObject.get("attributes").getAsJsonArray();
                JsonArray disjointConcepts = classObject.get("disjoint").getAsJsonArray();
                JsonArray overlapConcepts = classObject.get("overlap").getAsJsonArray();

                // set superclass data properties
                if (!addedConcepts.contains(className)) {
                    // define comment
                    // Create the RDFS comment annotation
                    this.defineConceptComments(className, level);


                    for (JsonElement attr : attributes) {
                        JsonObject attrObj = attr.getAsJsonObject();
                        String name = attrObj.get("name").getAsString();
                        String type = attrObj.get("datatype").getAsString().toLowerCase();
                        boolean isFunctional = attrObj.get("functional").getAsBoolean();

                        OWLDataProperty dataProperty = this.dataFactory.getOWLDataProperty(IRI.create(this.ontologyIRI + "#" + name.replace(" ", "_")));
                        defineDataProperty(dataProperty, className, isFunctional, type);

                    }
                }


                if (classObject.has("sub_classes")) {
                    JsonArray subClasses = classObject.get("sub_classes").getAsJsonArray();
                    for (JsonElement subClass : subClasses) {
                        JsonObject subClassObject = subClass.getAsJsonObject();
                        String subClassName = subClassObject.get("class_name").getAsString(); // subClass
                        String subLevel = subClassObject.get("level").getAsString();
                        JsonArray subAttributes = subClassObject.get("attributes").getAsJsonArray();
                        OWLClass subClazz = this.hashMap.get(subClassName);
                        OWLClass supClazz = this.hashMap.get(className);
                        this.manager.addAxiom(this.ontology, this.dataFactory.getOWLSubClassOfAxiom(subClazz, supClazz));

                        if (!addedConcepts.contains(subClassName)) {
                            // define comment
                            // Create the RDFS comment annotation
                            this.defineConceptComments(subClassName, subLevel);


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
                if (disjointConcepts.size() > 0) {
                    List<OWLClassExpression> disjointList = new ArrayList<>();
                    for (JsonElement disjointSet : disjointConcepts) {
                        if (disjointSet.isJsonArray()) {
                            JsonArray jsonArray = disjointSet.getAsJsonArray(); // convert JsonElement to JsonArray

                            String[] stringArray = new String[jsonArray.size()]; // create new String array with same size as JsonArray

                            for (int i = 0; i < jsonArray.size(); i++) {
                                stringArray[i] = jsonArray.get(i).getAsString(); // convert each JsonElement to String and add to string array
                                disjointList.add(this.hashMap.get(stringArray[i]));
                            }

                            if (disjointList.size() > 0) {
                                // create the disjoint classes axiom
                                OWLDisjointClassesAxiom axiom = this.dataFactory.getOWLDisjointClassesAxiom(disjointList);
                                // add the axiom to the ontology
                                manager.addAxiom(ontology, axiom);
                            }

                            disjointList.clear();

                        }
                    }
                }

                // set overlap properties
                if (overlapConcepts.size() > 0) {
                    List<OWLClassExpression> overlapList = new ArrayList<>();

                    for (JsonElement overlapSet : overlapConcepts) {
                        if (overlapSet.isJsonArray()) {
                            JsonArray jsonArray = overlapSet.getAsJsonArray();

                            String[] stringArray = new String[jsonArray.size()]; // create new String array with same size as JsonArray

                            for (int i = 0; i < jsonArray.size(); i++) {
                                stringArray[i] = jsonArray.get(i).getAsString(); // convert each JsonElement to String and add to string array
                                overlapList.add(this.hashMap.get(stringArray[i]));
                            }

                            if (overlapList.size() > 0) {
                                // Create the union class
                                OWLClass unionClass = this.dataFactory.getOWLClass(IRI.create(this.ontologyIRI + "#" + "Union" + Integer.toString(seqNumber)));
                                seqNumber++;
                                // Create the disjoint union axiom
                                OWLDisjointUnionAxiom axiom = this.dataFactory.getOWLDisjointUnionAxiom(unionClass, overlapList);
                                manager.addAxiom(ontology, axiom);
                            }

                            overlapList.clear();
                        }
                    }


                }

            }

            // Define Object properties
            if (ops.length > 0) {
                for (JsonElement op : ops[0]) {
                    JsonObject opObject = op.getAsJsonObject();
                    String propertyName = opObject.get("op_name").getAsString();
                    String inversePropertyName = opObject.get("op_inverse").getAsString();
                    String comment = opObject.get("op_equal").getAsString();
                    String domain = opObject.get("op_domain").getAsString();
                    String range = opObject.get("op_range").getAsString();
                    JsonObject quantifier = opObject.get("quantifier").getAsJsonObject();
                    JsonObject constraints = opObject.get("constraints").getAsJsonObject();

                    OWLObjectProperty property = this.dataFactory.getOWLObjectProperty(IRI.create(this.ontologyIRI + "#" + propertyName.replace(" ", "_")));
                    OWLClass domainClass = this.dataFactory.getOWLClass(this.hashMap.get(domain));
                    OWLClass rangeClass = this.dataFactory.getOWLClass(this.hashMap.get(range));

                    manager.addAxiom(this.ontology, this.dataFactory.getOWLDeclarationAxiom(property));
                    manager.addAxiom(this.ontology, this.dataFactory.getOWLObjectPropertyDomainAxiom(property, domainClass));
                    manager.addAxiom(this.ontology, this.dataFactory.getOWLObjectPropertyRangeAxiom(property, rangeClass));

                    // define property characteristics such as symmetric and etc
                    this.definePropertyCharacteristics(constraints, property);

                    // define quantifiers for the defined property
                    // create an OWLClassExpression object for OP some/only Range concept
                    if (quantifier.get("some").getAsBoolean()) {
                        OWLClassExpression someExpression = this.dataFactory.getOWLObjectSomeValuesFrom(property, rangeClass);
                        // create an OWLAxiom object for domain subclassOf OP some domain
                        OWLAxiom axiom = this.dataFactory.getOWLSubClassOfAxiom(this.hashMap.get(domain), someExpression);
                        // add the axiom to the ontology
                        this.manager.applyChanges(new AddAxiom(this.ontology, axiom));
                    }
                    if (quantifier.get("only").getAsBoolean()) {
                        OWLClassExpression onlyExpression = this.dataFactory.getOWLObjectAllValuesFrom(property, rangeClass);
                        OWLAxiom axiom = this.dataFactory.getOWLSubClassOfAxiom(this.hashMap.get(domain), onlyExpression);
                        // add the axiom to the ontology
                        this.manager.applyChanges(new AddAxiom(this.ontology, axiom));
                    }


                    // add comments
                    if (comment.length() > 0) {
                        // Create the RDFS label annotation for the object property
                        OWLAnnotation labelAnnotation = this.dataFactory.getOWLAnnotation(
                                this.dataFactory.getRDFSLabel(),
                                this.dataFactory.getOWLLiteral(comment, "en"));

                        // Create the RDFS comment annotation for the object property
                        OWLAnnotation commentAnnotation = this.dataFactory.getOWLAnnotation(
                                this.dataFactory.getRDFSComment(),
                                this.dataFactory.getOWLLiteral(comment));

                        // Add the annotations to the object property
                        OWLAxiom labelAxiom = this.dataFactory.getOWLAnnotationAssertionAxiom(property.getIRI(), labelAnnotation);
                        OWLAxiom commentAxiom = this.dataFactory.getOWLAnnotationAssertionAxiom(property.getIRI(), commentAnnotation);
                        manager.applyChanges(Arrays.asList(new AddAxiom(ontology, labelAxiom), new AddAxiom(ontology, commentAxiom)));
                    }

                    // define inverse property
                    if (inversePropertyName.length() > 0) {
                        OWLObjectProperty inverseProperty = dataFactory.getOWLObjectProperty(IRI.create(this.ontologyIRI + "#" + inversePropertyName.replace(" ", "_")));
                        manager.addAxiom(this.ontology, dataFactory.getOWLInverseObjectPropertiesAxiom(property, inverseProperty));
                    } else {
                        // Create inverse object properties
                        OWLObjectProperty inverseOfProperty = dataFactory.getOWLObjectProperty(IRI.create(this.ontologyIRI + "#" + "_inverse_prop"));
//                        OWLObjectInverseOf inverseOfProperty = dataFactory.getOWLObjectInverseOf(property);
                        manager.addAxiom(this.ontology, dataFactory.getOWLInverseObjectPropertiesAxiom(property, inverseOfProperty));
                    }
                }
            }


        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public boolean getConsistencyResult() {
        return this.checkConsistency(this.ontology);
    }

    public File saveGeneratedOntology(String sessionID) throws FileNotFoundException, OWLOntologyStorageException {
        return this.saveOntology(this.ontology, sessionID);
    }

    private OWL2Datatype getPropertyType(String type) {
        switch (type) {
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

    private void definePropertyCharacteristics(JsonObject characteristics, OWLObjectProperty property) {
        if (characteristics.get("functional").getAsBoolean()) {
            manager.addAxiom(this.ontology, this.dataFactory.getOWLFunctionalObjectPropertyAxiom(property));
        }
        if (characteristics.get("inverseFunctional").getAsBoolean()) {
            manager.addAxiom(this.ontology, this.dataFactory.getOWLInverseFunctionalObjectPropertyAxiom(property));
        }
        if (characteristics.get("transitive").getAsBoolean()) {
            manager.addAxiom(this.ontology, this.dataFactory.getOWLTransitiveObjectPropertyAxiom(property));
        }
        if (characteristics.get("symmetric").getAsBoolean()) {
            manager.addAxiom(this.ontology, this.dataFactory.getOWLSymmetricObjectPropertyAxiom(property));
        }
        if (characteristics.get("asymmetric").getAsBoolean()) {
            manager.addAxiom(this.ontology, this.dataFactory.getOWLAsymmetricObjectPropertyAxiom(property));
        }
        if (characteristics.get("reflexive").getAsBoolean()) {
            manager.addAxiom(this.ontology, this.dataFactory.getOWLReflexiveObjectPropertyAxiom(property));
        }
        if (characteristics.get("irreflexive").getAsBoolean()) {
            manager.addAxiom(this.ontology, this.dataFactory.getOWLIrreflexiveObjectPropertyAxiom(property));
        }
    }

    private void defineConceptComments(String className, String level) {
        OWLAnnotation commentAnnotation = this.dataFactory.getOWLAnnotation(
                this.dataFactory.getRDFSComment(),
                this.dataFactory.getOWLLiteral("The Level : " + level));

        OWLAxiom commentAxiom = this.dataFactory.getOWLAnnotationAssertionAxiom(this.hashMap.get(className).getIRI(), commentAnnotation);
        this.manager.applyChange(new AddAxiom(this.ontology, commentAxiom));
    }

    private boolean checkConsistency(OWLOntology fetchedOntology) {
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

        return isConsistent;
    }

    private void defineDataProperty(OWLDataProperty owlDataProperty, String className, boolean isFunctional, String type) {
        if (isFunctional) {
            OWLFunctionalDataPropertyAxiom axiom = this.dataFactory.getOWLFunctionalDataPropertyAxiom(owlDataProperty);
            this.manager.addAxiom(this.ontology, axiom);
        }

        OWLDataPropertyDomainAxiom domainProperty = dataFactory.getOWLDataPropertyDomainAxiom(owlDataProperty, this.hashMap.get(className));
        OWLDataPropertyRangeAxiom rangeProperty = dataFactory.getOWLDataPropertyRangeAxiom(owlDataProperty, dataFactory.getOWLDatatype(getPropertyType(type)));
        this.manager.addAxiom(this.ontology, domainProperty);
        this.manager.addAxiom(this.ontology, rangeProperty);
    }

    private File saveOntology(OWLOntology fetchedOntology, String session) throws FileNotFoundException, OWLOntologyStorageException {
        // Save the ontology to a file
        String outputOwlFileName = "OWL-OUT-" + session.substring(1, session.length() - 1) + ".owl";
        File fileOut = new File("src/OWLOutput/" + outputOwlFileName);
        this.manager.saveOntology(fetchedOntology, new FunctionalSyntaxDocumentFormat(), new FileOutputStream(fileOut));
        return fileOut;
    }

    public OWLOntology getOntology() {
        return this.ontology;
    }

    public IRI getIRI() {
        return this.ontologyIRI;
    }
}
