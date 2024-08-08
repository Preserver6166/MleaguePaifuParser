package tenhou;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Builder
@Data
public class TenhouPaifu {

    @JSONField(ordinal = 1)
    private String[] title;

    @JSONField(ordinal = 2)
    private String[] name; // A0, B0, C0, D0

    @JSONField(ordinal = 3)
    private TenhouRule rule;

    @JSONField(ordinal = 4)
    private List<KyokuLog> log;

    @JSONField(serialize = false)
    private String fileName;

    @JSONField(serialize=false)
    private List<String> kyokuBrief;

}
