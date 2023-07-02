package core.acc.api.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoucherInfo {
    private String vouNo;
    private Double vouTotal;
}
