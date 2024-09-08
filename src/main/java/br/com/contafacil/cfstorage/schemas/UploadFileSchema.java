package br.com.contafacil.cfstorage.schemas;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

public record UploadFileSchema(
        @RequestPart(value = "file") @NotBlank MultipartFile file
        ) {
}
