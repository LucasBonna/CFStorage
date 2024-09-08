package br.com.contafacil.cfstorage.controllers;

import br.com.contafacil.cfstorage.schemas.DownloadBatchSchema;
import br.com.contafacil.cfstorage.schemas.UploadFileSchema;
import br.com.contafacil.cfstorage.services.StorageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/file")
public class StorageController {

    @Autowired
    StorageService storageService;

    @Value("${swagger.api-gateway-url}")
    private String swaggerApiGatewayUrl;

    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Map<String, String>> uploadFile(@Valid UploadFileSchema data) {
        try {
            String fileId = storageService.uploadFile(data.file());
            String fileUrl = swaggerApiGatewayUrl + "/file/download/" + fileId;
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("url", fileUrl);

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, String> errorResponse = Collections.singletonMap("status", "failed to upload file: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);        }
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileId) throws IOException {
        byte[] fileBytes = storageService.downloadFile(fileId);
        if (fileBytes == null) {
            return ResponseEntity.notFound().build();
        }

        String fileName = storageService.getFileName(fileId);
        if (fileName == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileBytes);
    }

    @PostMapping("/download/batch")
    public ResponseEntity<byte[]> downloadBatch(@Valid @RequestBody DownloadBatchSchema fileIdList) throws IOException {
        byte[] fileBytes = storageService.getDownloadBatchFile(fileIdList.getFileIdList());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + UUID.randomUUID() + ".zip");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileBytes);
    }
}
