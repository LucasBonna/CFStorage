package br.com.contafacil.cfstorage.domains.files;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileRepository extends JpaRepository<FileEntity, UUID> {

    List<FileEntity> getFileEntityById(UUID id);

    default void updateDownloadCount(UUID fileId) {
        Optional<FileEntity> fileEntityOptional = findById(fileId);
        if (fileEntityOptional.isPresent()) {
            FileEntity fileEntity = fileEntityOptional.get();
            fileEntity.setDownloadCounter(fileEntity.getDownloadCounter() + 1);
            save(fileEntity);
        }
    }

}
