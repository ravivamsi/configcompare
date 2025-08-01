package com.configcompare.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class ConfigValidationService {

    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    public ValidationResult validateConfigFile(String content, String fileExtension) {
        ValidationResult result = new ValidationResult();
        result.setValid(true);
        
        try {
            switch (fileExtension.toLowerCase()) {
                case "json":
                    validateJson(content, result);
                    break;
                case "yml":
                case "yaml":
                    validateYaml(content, result);
                    break;
                case "properties":
                    validateProperties(content, result);
                    break;
                default:
                    result.setValid(false);
                    result.setErrorMessage("Unsupported file extension: " + fileExtension);
            }
        } catch (Exception e) {
            result.setValid(false);
            result.setErrorMessage("Validation error: " + e.getMessage());
        }
        
        return result;
    }

    private void validateJson(String content, ValidationResult result) {
        try {
            System.out.println("Validating JSON content:");
            System.out.println("Content length: " + content.length());
            System.out.println("Content preview: " + content.substring(0, Math.min(200, content.length())));
            
            // Check for common issues
            if (content == null || content.trim().isEmpty()) {
                result.setValid(false);
                result.setErrorMessage("JSON content is null or empty");
                return;
            }
            
            // Remove any BOM or extra characters
            String cleanContent = content.trim();
            if (cleanContent.startsWith("\uFEFF")) {
                cleanContent = cleanContent.substring(1);
                System.out.println("Removed BOM from content");
            }
            
            JsonNode jsonNode = jsonMapper.readTree(cleanContent);
            result.setParsedContent(jsonNode);
            System.out.println("JSON validation successful");
        } catch (IOException e) {
            System.err.println("JSON validation failed: " + e.getMessage());
            result.setValid(false);
            result.setErrorMessage("Invalid JSON format: " + e.getMessage());
        }
    }

    private void validateYaml(String content, ValidationResult result) {
        try {
            JsonNode yamlNode = yamlMapper.readTree(content);
            result.setParsedContent(yamlNode);
        } catch (IOException e) {
            result.setValid(false);
            result.setErrorMessage("Invalid YAML format: " + e.getMessage());
        }
    }

    private void validateProperties(String content, ValidationResult result) {
        try {
            Properties properties = new Properties();
            properties.load(new StringReader(content));
            
            // Convert Properties to Map for easier handling
            Map<String, String> propertiesMap = new HashMap<>();
            for (String key : properties.stringPropertyNames()) {
                propertiesMap.put(key, properties.getProperty(key));
            }
            
            result.setParsedContent(propertiesMap);
        } catch (IOException e) {
            result.setValid(false);
            result.setErrorMessage("Invalid Properties format: " + e.getMessage());
        }
    }

    public static class ValidationResult {
        private boolean valid;
        private String errorMessage;
        private Object parsedContent;

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public Object getParsedContent() {
            return parsedContent;
        }

        public void setParsedContent(Object parsedContent) {
            this.parsedContent = parsedContent;
        }
    }
} 