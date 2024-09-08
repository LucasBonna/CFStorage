package br.com.contafacil.cfstorage.domains.logs;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID fileId;

    @Enumerated(EnumType.STRING)
    private OperationEnum operation;

    private boolean completed;

    private LocalDateTime createdAt;
}
