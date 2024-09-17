package in.boot.generator.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class GeneratorService {
    public void createProjectStructure(String projectPath, String packageName, String domainName, String description, String dependencies) {
        createDirectories(projectPath, packageName);
        createPomXml(projectPath, domainName, dependencies);
        createApplicationClass(projectPath, packageName);
        createApplicationProperties(projectPath);
        createReadme(projectPath, description);
        createGitignore(projectPath);
        createHelpMd(projectPath);
        createMavenWrapperFiles(projectPath);
    }

    public void createDirectories(String projectPath, String packageName) {
        String srcMainJavaPath = projectPath + "/src/main/java/" + packageName.replace('.', '/');
        String srcMainResourcesPath = projectPath + "/src/main/resources";
        String srcTestJavaPath = projectPath + "/src/test/java/" + packageName.replace('.', '/');
        String mavenWrapperPath = projectPath + "/.mvn/wrapper";

        new File(srcMainJavaPath).mkdirs();
        new File(srcMainResourcesPath).mkdirs();
        new File(srcTestJavaPath).mkdirs();
        new File(mavenWrapperPath).mkdirs();
    }

    public void createPomXml(String projectPath, String domainName, String dependencies) {
        String pomContent = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0\n" +
                "                             http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "    <modelVersion>4.0.0</modelVersion>\n" +
                "    <groupId>" + domainName + "</groupId>\n" +
                "    <artifactId>demo</artifactId>\n" +
                "    <version>1.0.0</version>\n" +
                "    <parent>\n" +
                "        <groupId>org.springframework.boot</groupId>\n" +
                "        <artifactId>spring-boot-starter-parent</artifactId>\n" +
                "        <version>2.6.2</version>\n" +
                "        <relativePath/> <!-- lookup parent from repository -->\n" +
                "    </parent>\n" +
                "    <dependencies>\n" + dependencies +
                "        <dependency>\n" +
                "            <groupId>org.springframework.boot</groupId>\n" +
                "            <artifactId>spring-boot-starter-web</artifactId>\n" +
                "        </dependency>\n" +
                "        <dependency>\n" +
                "            <groupId>org.springframework.boot</groupId>\n" +
                "            <artifactId>spring-boot-starter-test</artifactId>\n" +
                "            <scope>test</scope>\n" +
                "        </dependency>\n" +
                "    </dependencies>\n" +
                "</project>";

        try {
            Files.write(Paths.get(projectPath + "/pom.xml"), pomContent.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createApplicationClass(String projectPath, String packageName) {
        String packagePath = packageName.replace('.', '/');
        String classContent = "package " + packageName + ";\n\n" +
                "import org.springframework.boot.SpringApplication;\n" +
                "import org.springframework.boot.autoconfigure.SpringBootApplication;\n\n" +
                "@SpringBootApplication\n" +
                "public class Application {\n" +
                "    public static void main(String[] args) {\n" +
                "        SpringApplication.run(Application.class, args);\n" +
                "    }\n" +
                "}";

        try {
            Files.write(Paths.get(projectPath + "/src/main/java/" + packagePath + "/Application.java"), classContent.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createApplicationProperties(String projectPath) {
        String propertiesContent = "spring.application.name=demo\n" +
                "server.port=8080";

        try {
            Files.write(Paths.get(projectPath + "/src/main/resources/application.properties"), propertiesContent.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createReadme(String projectPath, String description) {
        String readmeContent = "# Project Description\n\n" + description;

        try (FileWriter writer = new FileWriter(projectPath + "/README.md")) {
            writer.write(readmeContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createGitignore(String projectPath) {
        String gitignoreContent = "target/\n" +
                "*.log\n" +
                "*.class\n" +
                ".classpath\n" +
                ".project\n" +
                ".settings/\n" +
                "*.iml\n" +
                "*.ipr\n" +
                "*.iws";

        try (FileWriter writer = new FileWriter(projectPath + "/.gitignore")) {
            writer.write(gitignoreContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createHelpMd(String projectPath) {
        String helpContent = "# Help\n\n" +
                "This is a generated Spring Boot project. Use `mvnw` or `mvnw.cmd` to build the project.";

        try (FileWriter writer = new FileWriter(projectPath + "/Help.md")) {
            writer.write(helpContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createMavenWrapperFiles(String projectPath) {
        String mavenWrapperPropertiesContent = "distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.6.3/apache-maven-3.6.3-bin.zip";
        String mvnwContent = "#!/bin/sh\n" +
                "BASEDIR=$(dirname \"$0\")\n" +
                "java -jar \"$BASEDIR/.mvn/wrapper/maven-wrapper.jar\" \"$@\"";
        String mvnwCmdContent = "@echo off\n" +
                "setlocal\n" +
                "set BASEDIR=%~dp0\n" +
                "java -jar \"%BASEDIR%\\.mvn\\wrapper\\maven-wrapper.jar\" %*";

        try {
            Files.write(Paths.get(projectPath + "/.mvn/wrapper/maven-wrapper.properties"), mavenWrapperPropertiesContent.getBytes());
            Files.write(Paths.get(projectPath + "/mvnw"), mvnwContent.getBytes());
            Files.write(Paths.get(projectPath + "/mvnw.cmd"), mvnwCmdContent.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }
}
