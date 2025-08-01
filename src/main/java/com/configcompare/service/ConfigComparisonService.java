package com.configcompare.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

@Service
public class ConfigComparisonService {

    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    public ComparisonResult compareFiles(String content1, String content2, String fileExtension) {
        ComparisonResult result = new ComparisonResult();
        
        try {
            Object parsed1 = parseContent(content1, fileExtension);
            Object parsed2 = parseContent(content2, fileExtension);
            
            if (parsed1 instanceof Map && parsed2 instanceof Map) {
                compareMaps((Map<String, Object>) parsed1, (Map<String, Object>) parsed2, result);
            } else if (parsed1 instanceof JsonNode && parsed2 instanceof JsonNode) {
                compareJsonNodes((JsonNode) parsed1, (JsonNode) parsed2, result);
            } else {
                // Fallback to line-by-line comparison for unstructured content
                compareTextContent(content1, content2, result);
            }
        } catch (Exception e) {
            // If parsing fails, fall back to line-by-line comparison
            compareTextContent(content1, content2, result);
        }
        
        return result;
    }
    
    private void compareTextContent(String content1, String content2, ComparisonResult result) {
        String[] lines1 = content1.split("\n");
        String[] lines2 = content2.split("\n");
        
        int maxLines = Math.max(lines1.length, lines2.length);
        
        for (int i = 0; i < maxLines; i++) {
            String line1 = i < lines1.length ? lines1[i] : "";
            String line2 = i < lines2.length ? lines2[i] : "";
            
            if (!line1.equals(line2)) {
                result.setHasDifferences(true);
                result.addDifference("Line " + (i + 1), line1, line2);
            }
        }
    }

    private Object parseContent(String content, String fileExtension) throws IOException {
        switch (fileExtension.toLowerCase()) {
            case "json":
                return jsonMapper.readTree(content);
            case "yml":
            case "yaml":
                return yamlMapper.readTree(content);
            case "properties":
                Properties properties = new Properties();
                properties.load(new StringReader(content));
                Map<String, String> map = new HashMap<>();
                for (String key : properties.stringPropertyNames()) {
                    map.put(key, properties.getProperty(key));
                }
                return map;
            default:
                throw new IllegalArgumentException("Unsupported file extension: " + fileExtension);
        }
    }

    private void compareMaps(Map<String, Object> map1, Map<String, Object> map2, ComparisonResult result) {
        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(map1.keySet());
        allKeys.addAll(map2.keySet());

        for (String key : allKeys) {
            Object value1 = map1.get(key);
            Object value2 = map2.get(key);

            if (!Objects.equals(value1, value2)) {
                result.setHasDifferences(true);
                result.addDifference(key, 
                    value1 != null ? value1.toString() : "null",
                    value2 != null ? value2.toString() : "null");
            }
        }
    }

    private void compareJsonNodes(JsonNode node1, JsonNode node2, ComparisonResult result) {
        if (node1.equals(node2)) {
            return;
        }

        if (node1.isObject() && node2.isObject()) {
            // Compare object fields
            Iterator<String> fieldNames = node1.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                JsonNode value1 = node1.get(fieldName);
                JsonNode value2 = node2.get(fieldName);
                
                if (!Objects.equals(value1, value2)) {
                    result.setHasDifferences(true);
                    result.addDifference(fieldName, 
                        value1 != null ? value1.toString() : "null",
                        value2 != null ? value2.toString() : "null");
                }
            }
            
            // Check for fields in node2 that are not in node1
            Iterator<String> fieldNames2 = node2.fieldNames();
            while (fieldNames2.hasNext()) {
                String fieldName = fieldNames2.next();
                if (!node1.has(fieldName)) {
                    result.setHasDifferences(true);
                    result.addDifference(fieldName, "null", node2.get(fieldName).toString());
                }
            }
        } else if (node1.isArray() && node2.isArray()) {
            // Compare arrays element by element
            int maxSize = Math.max(node1.size(), node2.size());
            for (int i = 0; i < maxSize; i++) {
                JsonNode value1 = i < node1.size() ? node1.get(i) : null;
                JsonNode value2 = i < node2.size() ? node2.get(i) : null;
                
                if (!Objects.equals(value1, value2)) {
                    result.setHasDifferences(true);
                    result.addDifference("[" + i + "]", 
                        value1 != null ? value1.toString() : "null",
                        value2 != null ? value2.toString() : "null");
                }
            }
        } else if (node1.isTextual() && node2.isTextual()) {
            // Compare text values
            String text1 = node1.asText();
            String text2 = node2.asText();
            if (!text1.equals(text2)) {
                result.setHasDifferences(true);
                result.addDifference("value", text1, text2);
            }
        } else if (node1.isNumber() && node2.isNumber()) {
            // Compare numeric values
            if (!node1.equals(node2)) {
                result.setHasDifferences(true);
                result.addDifference("value", node1.toString(), node2.toString());
            }
        } else if (node1.isBoolean() && node2.isBoolean()) {
            // Compare boolean values
            if (!node1.equals(node2)) {
                result.setHasDifferences(true);
                result.addDifference("value", node1.toString(), node2.toString());
            }
        } else {
            // Different types or mixed types
            result.setHasDifferences(true);
            result.addDifference("type", 
                node1.getNodeType().toString() + ": " + node1.toString(),
                node2.getNodeType().toString() + ": " + node2.toString());
        }
    }

    public static class ComparisonResult {
        private boolean hasDifferences;
        private List<Difference> differences = new ArrayList<>();

        public boolean isHasDifferences() {
            return hasDifferences;
        }

        public void setHasDifferences(boolean hasDifferences) {
            this.hasDifferences = hasDifferences;
        }

        public List<Difference> getDifferences() {
            return differences;
        }

        public void setDifferences(List<Difference> differences) {
            this.differences = differences;
        }

        public void addDifference(String key, String value1, String value2) {
            differences.add(new Difference(key, value1, value2));
        }

        public static class Difference {
            private String key;
            private String value1;
            private String value2;

            public Difference(String key, String value1, String value2) {
                this.key = key;
                this.value1 = value1;
                this.value2 = value2;
            }

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }

            public String getValue1() {
                return value1;
            }

            public void setValue1(String value1) {
                this.value1 = value1;
            }

            public String getValue2() {
                return value2;
            }

            public void setValue2(String value2) {
                this.value2 = value2;
            }
        }
    }
} 