package in.boot.generator.Controller;


import in.boot.generator.models.ProjectMetadata;
import in.boot.generator.service.GeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/project")
public class GeneratorController {

    @Autowired
    private GeneratorService generatorService;

    @PostMapping("/generate")
    public ResponseEntity<StreamingResponseBody> generateProject(@RequestBody ProjectMetadata metadata) {

        String projectPath = System.getProperty("java.io.tmpdir") + "/GeneratedProject-" + System.currentTimeMillis();
        generatorService.createProjectStructure(projectPath, metadata.getPackageName(), metadata.getDomainName(), metadata.getDescription(), metadata.getDependencies());

        StreamingResponseBody responseBody = out -> {
            try (ZipOutputStream zos = new ZipOutputStream(out)) {
                Files.walk(Paths.get(projectPath)).filter(Files::isRegularFile).forEach(filePath -> {
                    Path targetFile = Paths.get(projectPath).relativize(filePath);
                    ZipEntry zipEntry = new ZipEntry(targetFile.toString());
                    try {
                        zos.putNextEntry(zipEntry);
                        Files.copy(filePath, zos);
                        zos.closeEntry();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } finally {
                // Clean up generated files
                generatorService.deleteDirectory(new File(projectPath));
            }
        };

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=generated-project.zip");

        return ResponseEntity.ok().headers(headers).body(responseBody);
    }

}

