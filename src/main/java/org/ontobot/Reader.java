//public class JsonReader {
//    public static void main(String[] args) {
//        Gson gson = new Gson();
//        try {
//            JsonObject jsonObject = gson.fromJson(new FileReader("data.json"), JsonObject.class);
//            JsonArray conceptsArray = jsonObject.getAsJsonObject("msg").getAsJsonArray("concepts");
//            JsonArray taxonomyArray = jsonObject.getAsJsonObject("msg").getAsJsonArray("taxonomy");
//
//            List<Concept> concepts = new ArrayList<>();
//            for (JsonElement conceptElement : conceptsArray) {
//                String concept = conceptElement.getAsString();
//                concepts.add(new Concept(concept));
//            }
//
//            List<ClassData> classes = new ArrayList<>();
//            for (JsonElement classElement : taxonomyArray) {
//                JsonObject classObj = classElement.getAsJsonObject();
//                String className = classObj.get("class_name").getAsString();
//                int level = classObj.get("level").getAsInt();
//                List<Attribute> attributes = new ArrayList<>();
//                if (classObj.has("attributes")) {
//                    JsonArray attributesArray = classObj.getAsJsonArray("attributes");
//                    for (JsonElement attributeElement : attributesArray) {
//                        JsonObject attributeObj = attributeElement.getAsJsonObject();
//                        String name = attributeObj.get("name").getAsString();
//                        String datatype = attributeObj.get("datatype").getAsString();
//                        String restrictions = attributeObj.get("restrictions").getAsString();
//                        boolean functional = attributeObj.get("functional").getAsBoolean();
//                        attributes.add(new Attribute(name, datatype, restrictions, functional));
//                    }
//                }
//                List<String[]> disjointClasses = new ArrayList<>();
//                if (classObj.has("disjoint")) {
//                    JsonArray disjointArray = classObj.getAsJsonArray("disjoint");
//                    for (JsonElement disjointElement : disjointArray) {
//                        JsonArray disjointClassArray = disjointElement.getAsJsonArray();
//                        String[] disjointClassesArray = new String[disjointClassArray.size()];
//                        for (int i = 0; i < disjointClassArray.size(); i++) {
//                            disjointClassesArray[i] = disjointClassArray.get(i).getAsString();
//                        }
//                        disjointClasses.add(disjointClassesArray);
//                    }
//                }
//                List<ClassData> subClasses = new ArrayList<>();
//                if (classObj.has("sub_classes")) {
//                    JsonArray subClassesArray = classObj.getAsJsonArray("sub_classes");
//                    for (JsonElement subClassElement : subClassesArray) {
//                        ClassData subClass = getClassDataFromJson(subClassElement.getAsJsonObject());
//                        subClasses.add(subClass);
//                    }
//                }
//                ClassData classData = new ClassData(className, level, attributes, disjointClasses, subClasses);
//                classes.add(classData);
//            }
//
//            System.out.println("Concepts:");
//            for (Concept concept : concepts) {
//                System.out.println(concept.getName());
//            }
//            System.out.println("\nClasses:");
//            for (ClassData classData : classes) {
//                System.out.println(classData.getClassName() + " (level " + classData.getLevel() + ")");
//                if (!classData.getAttributes().isEmpty()) {
//                    System.out.println("Attributes:");
//                    for (Attribute attribute : classData.getAttributes()) {
//                        System.out.println("- " + attribute.getName() + " (" + attribute.getDatatype() + ")");
//                    }
//                }
//                if (!classData.getDisjointClasses().isEmpty()) {
//                    System.out.println("Disjoint classes:");
//                    for (String[] disjointClasses : classData.get
