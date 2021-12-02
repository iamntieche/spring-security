package com.adservio.authentification.auth.domain;

import java.io.Serializable;
import java.util.Objects;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity(name = "Authority")
@Table(name = "authority")
public class AuthorityEntity implements Serializable {
     @Id
    private String name;
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuthorityEntity)) {
            return false;
        }
        return Objects.equals(name, ((AuthorityEntity) o).name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Authority{" +
            "name='" + name + '\'' +
            "}";
    }
}
