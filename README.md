# Config Compare Tool

A Spring Boot application for validating and comparing configuration files across GitHub and Bitbucket repositories.

## Features

- **Config Validation**: Validate JSON, YAML, and properties files for syntax correctness
- **File Comparison**: Compare configuration files within the same repository and branch
- **Branch Comparison**: Compare configuration files across different branches
- **Multi-Platform Support**: Support for both GitHub and Bitbucket
- **Modern UI**: Beautiful and responsive web interface built with Bootstrap and Thymeleaf

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- GitHub Personal Access Token (PAT)
- Bitbucket Personal Access Token (PAT)

## Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd configcompare
```

### 2. Build the Application

```bash
mvn clean install
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080/config-compare`

## Configuration

### Option 1: Web Interface (Recommended)

The application is designed to accept credentials through the web interface. Simply enter your username and PAT token when prompted.

### Option 2: Configuration File (Development Only)

For development and testing purposes, you can add default credentials to `src/main/resources/application.properties`:

```properties
# Default Credentials (Optional - for development/testing only)
# WARNING: Do not use these in production environments
github.default.username=your-github-username
github.default.pat-token=your-github-pat-token
bitbucket.default.username=your-bitbucket-username
bitbucket.default.pat-token=your-bitbucket-pat-token
```

**Security Warning**: Never commit credentials to version control. Use environment variables or secure configuration management in production.

### GitHub Setup

1. Go to GitHub Settings > Developer settings > Personal access tokens
2. Generate a new token with the following permissions:
   - `repo` (Full control of private repositories)
   - `read:org` (Read organization data)
3. Copy the token for use in the application

### Bitbucket Setup

1. Go to Bitbucket Admin > Access Management > API keys
2. Create a new API key with the following permissions:
   - `Repositories: Read`
   - `Projects: Read`
3. Copy the Organization ID and API key for use in the application

## Usage

### Home Page

The home page provides navigation to GitHub and Bitbucket sections with an overview of available features.

### GitHub Configuration

1. **Credentials**: Enter your GitHub username and Personal Access Token
2. **Config Validation**:
   - Select a repository from the dropdown
   - Choose a branch
   - Optionally specify a path
   - Click "Load Config Files" to see available configuration files
   - Click "Validate" on any file to check its syntax
3. **Compare Files**:
   - Select repository, branch, and optionally a path
   - Load files and select two files to compare
   - Click "Compare Files" to see differences
4. **Compare Branches**:
   - Select repository and two branches (source and target)
   - Load files and select a file to compare
   - Click "Compare Branches" to see differences between branches

### Bitbucket Configuration

1. **Credentials**: Enter your Bitbucket Organization ID and API Key
2. **Config Validation**:
   - Select a project from the dropdown
   - Select a repository from the dropdown
   - Choose a branch
   - Optionally specify a path
   - Click "Load Config Files" to see available configuration files
   - Click "Validate" on any file to check its syntax
3. **Compare Files**:
   - Select project, repository, branch, and optionally a path
   - Load files and select two files to compare
   - Click "Compare Files" to see differences
4. **Compare Branches**:
   - Select project, repository and two branches (source and target)
   - Load files and select a file to compare
   - Click "Compare Branches" to see differences between branches

## Supported File Types

- **JSON** (.json)
- **YAML** (.yml, .yaml)
- **Properties** (.properties)

## API Endpoints

### GitHub API

- `GET /api/github/repositories` - Get user repositories
- `GET /api/github/branches` - Get repository branches
- `GET /api/github/files` - Get directory contents
- `GET /api/github/config-files` - Get configuration files
- `POST /api/github/validate` - Validate a configuration file
- `POST /api/github/compare-files` - Compare two files
- `POST /api/github/compare-branches` - Compare file across branches

### Bitbucket API

- `GET /api/bitbucket/projects` - Get workspace projects
- `GET /api/bitbucket/repositories` - Get project repositories
- `GET /api/bitbucket/branches` - Get repository branches
- `GET /api/bitbucket/files` - Get directory contents
- `GET /api/bitbucket/config-files` - Get configuration files
- `POST /api/bitbucket/validate` - Validate a configuration file
- `POST /api/bitbucket/compare-files` - Compare two files
- `POST /api/bitbucket/compare-branches` - Compare file across branches

## Technology Stack

- **Backend**: Spring Boot 3.2.0, Java 17
- **Frontend**: Thymeleaf, Bootstrap 5, jQuery
- **HTTP Client**: Unirest
- **JSON Processing**: Jackson
- **YAML Processing**: SnakeYAML
- **Build Tool**: Maven

## Project Structure

```
src/
├── main/
│   ├── java/com/configcompare/
│   │   ├── controller/
│   │   │   ├── HomeController.java
│   │   │   ├── GitHubController.java
│   │   │   └── BitbucketController.java
│   │   ├── model/
│   │   │   ├── Repository.java
│   │   │   ├── Branch.java
│   │   │   └── FileContent.java
│   │   ├── service/
│   │   │   ├── GitHubService.java
│   │   │   ├── BitbucketService.java
│   │   │   ├── ConfigValidationService.java
│   │   │   └── ConfigComparisonService.java
│   │   └── ConfigCompareApplication.java
│   └── resources/
│       ├── templates/
│       │   ├── home.html
│       │   ├── github.html
│       │   └── bitbucket.html
│       └── application.properties
└── test/
```

## Security Considerations

- Personal Access Tokens are sent over HTTPS
- Tokens are not stored on the server
- All API calls use secure authentication
- Input validation is implemented for all user inputs

## Troubleshooting

### Common Issues

1. **Authentication Failed**: Ensure your PAT has the correct permissions
2. **Repository Not Found**: Check if the repository is accessible with your token
3. **File Not Found**: Verify the file path and branch name
4. **Validation Errors**: Check file syntax and format

### Logs

Application logs can be found in the console output. Enable debug logging by setting:

```properties
logging.level.com.configcompare=DEBUG
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.
