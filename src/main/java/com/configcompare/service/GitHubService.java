package com.configcompare.service;

import com.configcompare.model.Branch;
import com.configcompare.model.FileContent;
import com.configcompare.model.Repository;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class GitHubService {

    @Value("${github.api.base-url}")
    private String baseUrl;

    @Value("${github.api.timeout}")
    private int timeout;

    public List<Repository> getRepositories(String username, String patToken) {
        List<Repository> repositories = new ArrayList<>();
        
        try {
            int page = 1;
            int perPage = 100; // Maximum allowed by GitHub API
            boolean hasMorePages = true;
            
            while (hasMorePages) {
                System.out.println("Fetching repositories page " + page);
                
                HttpResponse<JsonNode> response = Unirest.get(baseUrl + "/user/repos")
                        .queryString("page", page)
                        .queryString("per_page", perPage)
                        .queryString("sort", "updated")
                        .queryString("direction", "desc")
                        .header("Authorization", "token " + patToken)
                        .header("Accept", "application/vnd.github.v3+json")
                        .header("User-Agent", "ConfigCompare-App")
                        .asJson();

                System.out.println("Response status: " + response.getStatus());
                
                if (response.isSuccess()) {
                    JSONArray reposArray = response.getBody().getArray();
                    System.out.println("Repositories in page " + page + ": " + reposArray.length());
                    
                    if (reposArray.length() == 0) {
                        hasMorePages = false;
                    } else {
                        for (int i = 0; i < reposArray.length(); i++) {
                            JSONObject repoObj = reposArray.getJSONObject(i);
                            Repository repo = new Repository();
                            repo.setId(repoObj.getString("id"));
                            repo.setName(repoObj.getString("name"));
                            repo.setFullName(repoObj.getString("full_name"));
                            repo.setDescription(repoObj.optString("description"));
                            repo.setHtmlUrl(repoObj.getString("html_url"));
                            repo.setCloneUrl(repoObj.getString("clone_url"));
                            repo.setLanguage(repoObj.optString("language"));
                            repo.setDefaultBranch(repoObj.getString("default_branch"));
                            repo.setPrivate(repoObj.getBoolean("private"));
                            
                            JSONObject ownerObj = repoObj.getJSONObject("owner");
                            repo.setOwner(ownerObj.getString("login"));
                            
                            repositories.add(repo);
                            System.out.println("Added repository: " + repo.getName() + " (" + repo.getFullName() + ")");
                        }
                        
                        // Check if there are more pages
                        if (reposArray.length() < perPage) {
                            hasMorePages = false;
                        } else {
                            page++;
                        }
                    }
                } else {
                    System.err.println("Failed to fetch repositories. Status: " + response.getStatus());
                    System.err.println("Error response: " + response.getBody());
                    hasMorePages = false;
                }
            }
            
            System.out.println("Total repositories loaded: " + repositories.size());
        } catch (Exception e) {
            System.err.println("Exception while fetching repositories: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch GitHub repositories: " + e.getMessage(), e);
        }
        
        return repositories;
    }

    public List<Branch> getBranches(String username, String patToken, String repoFullName) {
        List<Branch> branches = new ArrayList<>();
        
        try {
            String url = baseUrl + "/repos/" + repoFullName + "/branches";
            System.out.println("Fetching branches from: " + url);
            
            HttpResponse<JsonNode> response = Unirest.get(url)
                    .header("Authorization", "token " + patToken)
                    .header("Accept", "application/vnd.github.v3+json")
                    .header("User-Agent", "ConfigCompare-App")
                    .asJson();

            System.out.println("Response status: " + response.getStatus());
            System.out.println("Response body: " + response.getBody());

            if (response.isSuccess()) {
                JSONArray branchesArray = response.getBody().getArray();
                System.out.println("Number of branches found: " + branchesArray.length());
                
                for (int i = 0; i < branchesArray.length(); i++) {
                    JSONObject branchObj = branchesArray.getJSONObject(i);
                    Branch branch = new Branch();
                    branch.setName(branchObj.getString("name"));
                    
                    JSONObject commitObj = branchObj.getJSONObject("commit");
                    Branch.Commit commit = new Branch.Commit();
                    commit.setSha(commitObj.getString("sha"));
                    commit.setUrl(commitObj.getString("url"));
                    branch.setCommit(commit);
                    
                    branches.add(branch);
                    System.out.println("Added branch: " + branch.getName());
                }
            } else {
                System.err.println("Failed to fetch branches. Status: " + response.getStatus());
                System.err.println("Error response: " + response.getBody());
            }
        } catch (Exception e) {
            System.err.println("Exception while fetching branches: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch GitHub branches: " + e.getMessage(), e);
        }
        
        return branches;
    }

    public List<FileContent> getDirectoryContents(String username, String patToken, String repoFullName, String branch, String path) {
        List<FileContent> files = new ArrayList<>();
        
        try {
            String apiPath = "/repos/" + repoFullName + "/contents";
            if (path != null && !path.isEmpty()) {
                apiPath += "/" + path;
            }
            
            HttpResponse<JsonNode> response = Unirest.get(baseUrl + apiPath)
                    .queryString("ref", branch)
                    .header("Authorization", "token " + patToken)
                    .header("Accept", "application/vnd.github.v3+json")
                    .header("User-Agent", "ConfigCompare-App")
                    .asJson();

            if (response.isSuccess()) {
                JsonNode body = response.getBody();
                if (body.isArray()) {
                    // Directory contents
                    JSONArray filesArray = body.getArray();
                    for (int i = 0; i < filesArray.length(); i++) {
                        JSONObject fileObj = filesArray.getJSONObject(i);
                        FileContent file = new FileContent();
                        file.setName(fileObj.getString("name"));
                        file.setPath(fileObj.getString("path"));
                        file.setSha(fileObj.getString("sha"));
                        file.setSize(fileObj.getLong("size"));
                        file.setUrl(fileObj.getString("url"));
                        file.setHtmlUrl(fileObj.getString("html_url"));
                        file.setGitUrl(fileObj.getString("git_url"));
                        file.setDownloadUrl(fileObj.optString("download_url"));
                        file.setType(fileObj.getString("type"));
                        
                        files.add(file);
                    }
                } else {
                    // Single file
                    JSONObject fileObj = body.getObject();
                    FileContent file = new FileContent();
                    file.setName(fileObj.getString("name"));
                    file.setPath(fileObj.getString("path"));
                    file.setSha(fileObj.getString("sha"));
                    file.setSize(fileObj.getLong("size"));
                    file.setUrl(fileObj.getString("url"));
                    file.setHtmlUrl(fileObj.getString("html_url"));
                    file.setGitUrl(fileObj.getString("git_url"));
                    file.setDownloadUrl(fileObj.optString("download_url"));
                    file.setType(fileObj.getString("type"));
                    file.setContent(fileObj.optString("content"));
                    file.setEncoding(fileObj.optString("encoding"));
                    
                    files.add(file);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch GitHub directory contents: " + e.getMessage(), e);
        }
        
        return files;
    }

    public String getFileContent(String username, String patToken, String repoFullName, String branch, String filePath) {
        try {
            System.out.println("Fetching file content for: " + filePath);
            System.out.println("Repository: " + repoFullName);
            System.out.println("Branch: " + branch);
            
            HttpResponse<JsonNode> response = Unirest.get(baseUrl + "/repos/" + repoFullName + "/contents/" + filePath)
                    .queryString("ref", branch)
                    .header("Authorization", "token " + patToken)
                    .header("Accept", "application/vnd.github.v3+json")
                    .header("User-Agent", "ConfigCompare-App")
                    .asJson();

            System.out.println("File content response status: " + response.getStatus());

            if (response.isSuccess()) {
                JSONObject fileObj = response.getBody().getObject();
                String content = fileObj.getString("content");
                String encoding = fileObj.getString("encoding");
                
                System.out.println("File encoding: " + encoding);
                System.out.println("Content length: " + content.length());
                System.out.println("Content preview: " + content.substring(0, Math.min(200, content.length())));
                
                if ("base64".equals(encoding)) {
                    // Clean the base64 content by removing newlines and whitespace
                    String cleanContent = content.replaceAll("\\s+", "");
                    System.out.println("Cleaned base64 content length: " + cleanContent.length());
                    
                    try {
                        String decodedContent = new String(Base64.getDecoder().decode(cleanContent));
                        System.out.println("Decoded content length: " + decodedContent.length());
                        System.out.println("Decoded content preview: " + decodedContent.substring(0, Math.min(200, decodedContent.length())));
                        return decodedContent;
                    } catch (IllegalArgumentException e) {
                        System.err.println("Base64 decoding failed: " + e.getMessage());
                        System.err.println("Original content: " + content);
                        System.err.println("Cleaned content: " + cleanContent);
                        throw new RuntimeException("Failed to decode base64 content: " + e.getMessage(), e);
                    }
                } else {
                    return content;
                }
            } else {
                System.err.println("Failed to fetch file content. Status: " + response.getStatus());
                System.err.println("Error response: " + response.getBody());
            }
        } catch (Exception e) {
            System.err.println("Exception while fetching file content: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch GitHub file content: " + e.getMessage(), e);
        }
        
        return null;
    }

    public List<FileContent> getConfigFiles(String username, String patToken, String repoFullName, String branch, String path) {
        List<FileContent> allFiles = getDirectoryContents(username, patToken, repoFullName, branch, path);
        List<FileContent> configFiles = new ArrayList<>();
        
        for (FileContent file : allFiles) {
            if (file.isConfigFile()) {
                configFiles.add(file);
            }
        }
        
        return configFiles;
    }
} 