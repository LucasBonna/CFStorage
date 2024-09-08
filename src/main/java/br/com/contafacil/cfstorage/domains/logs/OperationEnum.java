package br.com.contafacil.cfstorage.domains.logs;

import lombok.Getter;

@Getter
public enum OperationEnum {
    UPLOAD("upload"),
    DOWNLOAD("download");

    private final String operation;

    OperationEnum(String operation) {
        this.operation = operation;
    }
}
