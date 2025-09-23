package common.pojo.investment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter@Builder

public class DealDetail {

    private int dealId;
    private double investmentAmount;
    private boolean reinvestment;
}


