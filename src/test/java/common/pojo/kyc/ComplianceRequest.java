package common.pojo.kyc;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class ComplianceRequest {

    private boolean subjectToRegulatoryActions;
    private boolean rbiCompliance;
    private boolean wilfulDefaulter;
    private boolean termsAcknowledged;
}


