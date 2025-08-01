package com.configcompare.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FileContent {
    private String name;
    private String path;
    private String sha;
    private long size;
    private String url;
    @JsonProperty("html_url")
    private String htmlUrl;
    @JsonProperty("git_url")
    private String gitUrl;
    @JsonProperty("download_url")
    private String downloadUrl;
    private String type;
    private String content;
    private String encoding;

    // Constructors
    public FileContent() {}

    public FileContent(String name, String path, String content) {
        this.name = name;
        this.path = path;
        this.content = content;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public void setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getFileExtension() {
        if (name != null && name.contains(".")) {
            return name.substring(name.lastIndexOf(".") + 1).toLowerCase();
        }
        return "";
    }

    public boolean isConfigFile() {
        String extension = getFileExtension();
        return extension.equals("json") || extension.equals("yml") || 
               extension.equals("yaml") || extension.equals("properties");
    }

    @Override
    public String toString() {
        return "FileContent{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", sha='" + sha + '\'' +
                ", size=" + size +
                ", url='" + url + '\'' +
                ", htmlUrl='" + htmlUrl + '\'' +
                ", gitUrl='" + gitUrl + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", type='" + type + '\'' +
                ", content='" + content + '\'' +
                ", encoding='" + encoding + '\'' +
                '}';
    }
} 