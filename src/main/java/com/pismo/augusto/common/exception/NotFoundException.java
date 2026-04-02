package com.pismo.augusto.common.exception;

import java.util.UUID;

public class NotFoundException extends RuntimeException {
    public NotFoundException(UUID uuid) {
        super("Entity %s not found".formatted(uuid));
    }

    public NotFoundException(Object id) {
        super("Entity %s not found".formatted(id));
    }
}
