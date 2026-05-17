import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/files")
public class FileController {

    private final S3Service s3Service;

    public FileController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();

        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("File name is required");
        }

        Path tempFile = Files.createTempFile("upload-", "-" + fileName);
        file.transferTo(tempFile);

        s3Service.uploadFile(fileName, tempFile);

        Files.deleteIfExists(tempFile);

        return "Uploaded!";
    }

    @GetMapping("/download")
    public String download(@RequestParam String key) throws Exception {
        Files.createDirectories(Path.of("downloads"));

        Path destination = Path.of("downloads", key);
        s3Service.downloadFile(key, destination);

        return "Downloaded!";
    }
}