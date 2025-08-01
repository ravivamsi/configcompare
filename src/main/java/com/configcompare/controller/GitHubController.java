package com.configcompare.controller;

import com.configcompare.model.Branch;
import com.configcompare.model.FileContent;
import com.configcompare.model.Repository;
import com.configcompare.service.ConfigComparisonService;
import com.configcompare.service.ConfigValidationService;
import com.configcompare.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/github")
public class GitHubController {

    @Autowired
    private GitHubService gitHubService;

    @Autowired
    private ConfigValidationService configValidationService;

    @Autowired
    private ConfigComparisonService configComparisonService;

    @Value("${github.default.username:}")
    private String defaultGitHubUsername;

    @Value("${github.default.pat-token:}")
    private String defaultGitHubPatToken;

    @GetMapping("/repositories")
    @ResponseBody
    public ResponseEntity<List<Repository>> getRepositories(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String patToken) {
        try {
            // Use default values if not provided
            String finalUsername = (username != null && !username.isEmpty()) ? username : defaultGitHubUsername;
            String finalPatToken = (patToken != null && !patToken.isEmpty()) ? patToken : defaultGitHubPatToken;
            
            if (finalUsername.isEmpty() || finalPatToken.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            List<Repository> repositories = gitHubService.getRepositories(finalUsername, finalPatToken);
            return ResponseEntity.ok(repositories);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/branches")
    @ResponseBody
    public ResponseEntity<List<Branch>> getBranches(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String patToken,
            @RequestParam String repoFullName) {
        try {
            System.out.println("Received request for branches:");
            System.out.println("Username: " + username);
            System.out.println("RepoFullName: " + repoFullName);
            
            // Use default values if not provided
            String finalUsername = (username != null && !username.isEmpty()) ? username : defaultGitHubUsername;
            String finalPatToken = (patToken != null && !patToken.isEmpty()) ? patToken : defaultGitHubPatToken;
            
            System.out.println("Final username: " + finalUsername);
            System.out.println("Final PAT token length: " + (finalPatToken != null ? finalPatToken.length() : 0));
            
            if (finalUsername.isEmpty() || finalPatToken.isEmpty()) {
                System.err.println("Username or PAT token is empty");
                return ResponseEntity.badRequest().build();
            }
            
            List<Branch> branches = gitHubService.getBranches(finalUsername, finalPatToken, repoFullName);
            System.out.println("Successfully retrieved " + branches.size() + " branches");
            return ResponseEntity.ok(branches);
        } catch (Exception e) {
            System.err.println("Exception in getBranches: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/files")
    @ResponseBody
    public ResponseEntity<List<FileContent>> getFiles(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String patToken,
            @RequestParam String repoFullName,
            @RequestParam String branch,
            @RequestParam(required = false) String path) {
        try {
            // Use default values if not provided
            String finalUsername = (username != null && !username.isEmpty()) ? username : defaultGitHubUsername;
            String finalPatToken = (patToken != null && !patToken.isEmpty()) ? patToken : defaultGitHubPatToken;
            
            if (finalUsername.isEmpty() || finalPatToken.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            List<FileContent> files = gitHubService.getDirectoryContents(finalUsername, finalPatToken, repoFullName, branch, path);
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/config-files")
    @ResponseBody
    public ResponseEntity<List<FileContent>> getConfigFiles(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String patToken,
            @RequestParam String repoFullName,
            @RequestParam String branch,
            @RequestParam(required = false) String path) {
        try {
            // Use default values if not provided
            String finalUsername = (username != null && !username.isEmpty()) ? username : defaultGitHubUsername;
            String finalPatToken = (patToken != null && !patToken.isEmpty()) ? patToken : defaultGitHubPatToken;
            
            if (finalUsername.isEmpty() || finalPatToken.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            List<FileContent> configFiles = gitHubService.getConfigFiles(finalUsername, finalPatToken, repoFullName, branch, path);
            return ResponseEntity.ok(configFiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/validate")
    @ResponseBody
    public ResponseEntity<ConfigValidationService.ValidationResult> validateFile(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String patToken,
            @RequestParam String repoFullName,
            @RequestParam String branch,
            @RequestParam String filePath) {
        try {
            // Use default values if not provided
            String finalUsername = (username != null && !username.isEmpty()) ? username : defaultGitHubUsername;
            String finalPatToken = (patToken != null && !patToken.isEmpty()) ? patToken : defaultGitHubPatToken;
            
            if (finalUsername.isEmpty() || finalPatToken.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            String content = gitHubService.getFileContent(finalUsername, finalPatToken, repoFullName, branch, filePath);
            if (content != null) {
                String fileExtension = filePath.substring(filePath.lastIndexOf(".") + 1);
                ConfigValidationService.ValidationResult result = configValidationService.validateConfigFile(content, fileExtension);
                return ResponseEntity.ok(result);
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/compare-files")
    @ResponseBody
    public ResponseEntity<ConfigComparisonService.ComparisonResult> compareFiles(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String patToken,
            @RequestParam String repoFullName,
            @RequestParam String branch,
            @RequestParam String filePath1,
            @RequestParam String filePath2) {
        try {
            // Use default values if not provided
            String finalUsername = (username != null && !username.isEmpty()) ? username : defaultGitHubUsername;
            String finalPatToken = (patToken != null && !patToken.isEmpty()) ? patToken : defaultGitHubPatToken;
            
            if (finalUsername.isEmpty() || finalPatToken.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            String content1 = gitHubService.getFileContent(finalUsername, finalPatToken, repoFullName, branch, filePath1);
            String content2 = gitHubService.getFileContent(finalUsername, finalPatToken, repoFullName, branch, filePath2);
            
            if (content1 != null && content2 != null) {
                String fileExtension = filePath1.substring(filePath1.lastIndexOf(".") + 1);
                ConfigComparisonService.ComparisonResult result = configComparisonService.compareFiles(content1, content2, fileExtension);
                return ResponseEntity.ok(result);
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/file-content")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getFileContent(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String patToken,
            @RequestParam String repoFullName,
            @RequestParam String branch,
            @RequestParam String filePath) {
        try {
            // Use default values if not provided
            String finalUsername = (username != null && !username.isEmpty()) ? username : defaultGitHubUsername;
            String finalPatToken = (patToken != null && !patToken.isEmpty()) ? patToken : defaultGitHubPatToken;
            
            if (finalUsername.isEmpty() || finalPatToken.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            String content = gitHubService.getFileContent(finalUsername, finalPatToken, repoFullName, branch, filePath);
            if (content != null) {
                Map<String, String> response = new HashMap<>();
                response.put("content", content);
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/compare-branches")
    @ResponseBody
    public ResponseEntity<ConfigComparisonService.ComparisonResult> compareBranches(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String patToken,
            @RequestParam String repoFullName,
            @RequestParam String sourceBranch,
            @RequestParam String targetBranch,
            @RequestParam String filePath) {
        try {
            // Use default values if not provided
            String finalUsername = (username != null && !username.isEmpty()) ? username : defaultGitHubUsername;
            String finalPatToken = (patToken != null && !patToken.isEmpty()) ? patToken : defaultGitHubPatToken;
            
            if (finalUsername.isEmpty() || finalPatToken.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            String content1 = gitHubService.getFileContent(finalUsername, finalPatToken, repoFullName, sourceBranch, filePath);
            String content2 = gitHubService.getFileContent(finalUsername, finalPatToken, repoFullName, targetBranch, filePath);
            
            if (content1 != null && content2 != null) {
                String fileExtension = filePath.substring(filePath.lastIndexOf(".") + 1);
                ConfigComparisonService.ComparisonResult result = configComparisonService.compareFiles(content1, content2, fileExtension);
                return ResponseEntity.ok(result);
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 