import org.springframework.web.bind.annotation.*;
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
        Path tempFile = Files.createTempFile("upload-", file.getOriginalFilename());
        file.transferTo(tempFile);

        s3Service.uploadFile(file.getOriginalFilename(), tempFile);

        return "Uploaded!";
    }

    @GetMapping("/download")
    public String download(@RequestParam String key) throws Exception {
        Path destination = Path.of("downloads/" + key);
        s3Service.downloadFile(key, destination);

        return "Downloaded!";
    }
}