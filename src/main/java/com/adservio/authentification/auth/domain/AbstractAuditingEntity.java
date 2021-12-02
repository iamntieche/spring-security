package com.adservio.authentification.auth.domain;

import java.io.Serializable;
import java.time.Instant;

import lombok.Data;

import javax.persistence.MappedSuperclass;



@MappedSuperclass
@Data
public abstract class AbstractAuditingEntity implements Serializable{

    private static final long serialVersionUID = 1L;
    private String createdBy;
    private Instant createdDate = Instant.now();
    private String lastModifiedBy;
    private Instant lastModifiedDate = Instant.now();


}
