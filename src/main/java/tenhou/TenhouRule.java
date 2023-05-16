package tenhou;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class TenhouRule {

    @JSONField(ordinal = 1)
    private String disp;

    @JSONField(ordinal = 2)
    private int aka;

}
