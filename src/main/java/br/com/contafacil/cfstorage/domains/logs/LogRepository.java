package br.com.contafacil.cfstorage.domains.logs;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LogRepository extends JpaRepository<LogEntity, UUID> {
}
