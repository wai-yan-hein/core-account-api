package core.acc.api.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Id;

@Data
public class VDescription {
    private String description;
    private String compCode;
}
