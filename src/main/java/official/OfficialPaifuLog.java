package official;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class OfficialPaifuLog {

    private String time; // 牌谱元素录入的时间戳，不是实际进行此动作的时间戳
    private int id;

    private String cmd;
    private String[] args;

}
