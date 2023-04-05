package core.acc.api.model;

import lombok.Data;

import java.util.Date;

@Data
public class YearEnd {
    private String yeCompCode;
    private String compCode;
    private Date startDate;
    private Date endDate;
    private Date yearEndDate;
    private boolean batchLock;
    private boolean opening;
    private String createBy;
    private Date cratedDate;
    private Integer macId;
    private String message;
}
