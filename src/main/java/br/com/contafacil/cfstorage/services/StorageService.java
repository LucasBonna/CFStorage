package br.com.contafacil.cfstorage.services;

import br.com.contafacil.cfstorage.domains.files.FileEntity;
import br.com.contafacil.cfstorage.domains.files.FileRepository;
import br.com.contafacil.cfstorage.domains.logs.LogEntity;
import br.com.contafacil.cfstorage.domains.logs.LogRepository;
import br.com.contafacil.cfstorage.domains.logs.OperationEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class StorageService {

    private final S3Client s3Client;

    private final FileRepository fileRepository;

    private final LogRepository logRepository;

    @Value("${cloudflare.r2.bucket}")
    private String bucketName;

    public StorageService(S3Client s3Client, FileRepository fileRepository, LogRepository logRepository) {
        this.s3Client = s3Client;
        this.fileRepository = fileRepository;
        this.logRepository = logRepository;
    }

    @Transactional
    public String uploadFile(MultipartFile file) throws IOException {
        String fileId = UUID.randomUUID().toString();
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileId)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        LogEntity logEntity = LogEntity.builder()
                .fileId(UUID.fromString(fileId))
                .completed(true)
                .operation(OperationEnum.UPLOAD)
                .createdAt(LocalDateTime.now())
                .build();
        logRepository.save(logEntity);

        String fileName = file.getOriginalFilename();

        FileEntity fileEntity = FileEntity.builder()
                .id(UUID.fromString(fileId))
                .filename(fileName)
                .fileExtension(fileName.substring(fileName.lastIndexOf(".")+ 1))
                .downloadCounter(0)
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .deleteAt(null)
                .build();
        fileRepository.save(fileEntity);

        return fileId;
    }

    @Transactional
    public byte[] downloadFile(String fileId) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileId)
                .build();

        LogEntity logEntity = LogEntity.builder()
                .fileId(UUID.fromString(fileId))
                .operation(OperationEnum.DOWNLOAD)
                .completed(true)
                .createdAt(LocalDateTime.now())
                .build();
        logRepository.save(logEntity);

        fileRepository.updateDownloadCount(UUID.fromString(fileId));

        return s3Client.getObject(getObjectRequest).readAllBytes();
    }

    public String getFileName(String fileId) {
        Optional<FileEntity> fileEntityOptional = fileRepository.findById(UUID.fromString(fileId));
        if (!fileEntityOptional.isPresent()) {
            return null;
        }

        FileEntity fileEntity = fileEntityOptional.get();

        return fileId + "." + fileEntity.getFileExtension();
    }

    public byte[] getDownloadBatchFile(List<String> fileIdList) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

        for (String id : fileIdList) {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(id)
                    .build();

            ResponseInputStream<GetObjectResponse> fileStream = s3Client.getObject(getObjectRequest);

            String fileName = getFileName(id);
            if (fileName == null) {
                fileName = id;
            }

            zipOutputStream.putNextEntry(new ZipEntry(fileName));

            fileStream.transferTo(zipOutputStream);

            zipOutputStream.closeEntry();

            fileStream.close();
        }

        zipOutputStream.close();

        return byteArrayOutputStream.toByteArray();
    }
}
