package br.com.contafacil.cfstorage.schemas;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class DownloadBatchSchema {
    @NotEmpty(message = "O array de UUIDs não pode estar vazio")
    @Size(min = 1, max = 100, message = "O número de UUIDs deve estar entre 1 e 100")
    private List<String> fileIdList;

    public DownloadBatchSchema() {}

    public DownloadBatchSchema(List<String> fileIdList) {
        this.fileIdList = fileIdList;
    }

    @Override
    public String toString() {
        return "DownloadBatchSchema{" +
                "fileIdList=" + fileIdList +
                '}';
    }
}