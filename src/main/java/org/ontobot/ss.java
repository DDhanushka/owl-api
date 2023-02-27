package org.ontobot;

//public class JsonReader {
//    public static <Concept, TaxonomyClass, DisjointClass> void main(String[] args) {
//        ArrayList<Concept> concepts = new ArrayList<>();
//        ArrayList<TaxonomyClass> taxonomy = new ArrayList<>();
//
//        JSONParser parser = new JSONParser();
//
//        try {
//            Object obj = parser.parse(new FileReader("/home/ddhash/IdeaProjects/owl-api/src/main/java/org/ontobot/response.json"));
//            JSONObject jsonObject = (JSONObject) obj;
//            JSONObject msg = (JSONObject) jsonObject.get("msg");
//            JSONArray conceptsArray = (JSONArray) msg.get("concepts");
//            JSONArray taxonomyArray = (JSONArray) msg.get("taxonomy");
//
//            for (int i = 0; i < conceptsArray.size(); i++) {
//                Concept concept;
//                concept = new Concept((String) conceptsArray.get(i));
//                concepts.add(concept);
//            }
//
//            for (int i = 0; i < taxonomyArray.size(); i++) {
//                JSONObject taxonomyObject = (JSONObject) taxonomyArray.get(i);
//                String className = (String) taxonomyObject.get("class_name");
//                int level = Integer.parseInt(taxonomyObject.get("level").toString());
//
//                ArrayList<Attribute> attributes = new ArrayList<>();
//                JSONArray attributesArray = (JSONArray) taxonomyObject.get("attributes");
//                for (int j = 0; j < attributesArray.size(); j++) {
//                    JSONObject attributeObject = (JSONObject) attributesArray.get(j);
//                    String id = (String) attributeObject.get("id");
//                    String name = (String) attributeObject.get("name");
//                    String datatype = (String) attributeObject.get("datatype");
//                    String restrictions = (String) attributeObject.get("restrictions");
//                    boolean functional = Boolean.parseBoolean(attributeObject.get("functional").toString());
//
//                    Attribute attribute = new Attribute(id, name);
//                    attributes.add(attribute);
//                }
//
//                ArrayList<DisjointClass> disjointClasses = new ArrayList<>();
//                JSONArray disjointArray = (JSONArray) taxonomyObject.get("disjoint");
//                for (int j = 0; j < disjointArray.size(); j++) {
//                    JSONArray disjointPair = (JSONArray) disjointArray.get(j);
//                    String disjoint1 = (String) disjointPair.get(0);
//                    String disjoint2 = (String) disjointPair.get(1);
//                    DisjointClass disjointClass;
//                    disjointClass = new DisjointClass(disjoint1, disjoint2);
//                    disjointClasses.add(disjointClass);
//                }
//            }
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}


//                ArrayList<SubClass> subClasses = new ArrayList<>();
//                JSONArray subClassArray = (JSONArray) taxonomyObject.get("sub_classes");
//                for (int j = 0; j < subClassArray.size(); j++) {
//                    JSONObject subClassObject = (JSONObject) subClassArray.get(j);
//                    String subClassName = (String) subClassObject.get("class_name");
//                    int subClassLevel = Integer.parseInt(subClassObject.get("level").toString());
//
//                    ArrayList<Attribute> subClassAttributes = new ArrayList<>();
//                    JSONArray subClassAttributesArray = (JSONArray) subClassObject.get("attributes");
//                    for (int k = 0; k < subClassAttributesArray.size(); k++) {
//                        JSONObject subClassAttributeObject = (JSONObject) subClassAttributesArray.get(k);
//                        String id = (String) subClassAttributeObject.get("id");
//                        String name = (String) subClassAttributeObject.get("name");
//                        String datatype = (String) subClassAttributeObject.get("datatype");
//                        String restrictions = (String) subClassAttributeObject.get("restrictions");
//                        boolean
