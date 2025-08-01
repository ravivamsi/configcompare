package com.configcompare.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Branch {
    private String name;
    private Commit commit;
    @JsonProperty("protected")
    private boolean isProtected;

    // Constructors
    public Branch() {}

    public Branch(String name) {
        this.name = name;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public void setProtected(boolean isProtected) {
        this.isProtected = isProtected;
    }

    @Override
    public String toString() {
        return "Branch{" +
                "name='" + name + '\'' +
                ", commit=" + commit +
                ", isProtected=" + isProtected +
                '}';
    }

    public static class Commit {
        private String sha;
        private String url;

        public Commit() {}

        public Commit(String sha, String url) {
            this.sha = sha;
            this.url = url;
        }

        public String getSha() {
            return sha;
        }

        public void setSha(String sha) {
            this.sha = sha;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public String toString() {
            return "Commit{" +
                    "sha='" + sha + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }
} 