package common.pojo.kyc;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder

public class AccreditationAnswersRequest {

    private int userId;
    private Map<String, Integer> accreditationAnswers;
}


