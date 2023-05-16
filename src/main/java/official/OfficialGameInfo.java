package official;

import lombok.Builder;
import lombok.Data;

import java.util.List;

import static official.App.PRO_NAME_BRIEF;

@Builder
@Data
public class OfficialGameInfo {

    /**
     * 比赛索引象限信息
     */
    private String fileName;
    private int globalIndex; // 全局计算第几个本场
    private int seasonIndex; // 对应赛季第几个本场
    private int stageIndex; // 对应赛季阶段（常规赛、半决赛、决赛）第几个本场
    private int dayIndex; // 对应当天的第几回战
    private String season; // 赛季名称

    /**
     * 比赛时间象限信息
     */
    private String gameDate; // YYYY/mm/dd
    private String gameWeekDay; // 日月火水木金土
    private String startTime;
    private String endTime;
    private int interval; // 单位为分钟

    /**
     * 比赛结果象限信息
     */
    private String[] proNames;
    private String[] proPoints; // 成绩
    private int[] proRanks; // 顺位

    // other info
    private List<OfficialPaifuLog> officialPaifuLogList;

    public String getGameName() {
        String pattern = "%s（%s）第%d回战";
        return String.format(pattern, gameDate, gameWeekDay, dayIndex);
    }

    public String getGameBrief() {
        String pattern = "%s\n%s vs %s vs %s vs %s";
        return String.format(pattern, getGameName(), proNames[0], proNames[1], proNames[2], proNames[3]);
    }

    public String getGameResult() {
        String pattern = "%s %spt; %s %spt; %s %spt; %s %spt";
        return String.format(pattern,
                PRO_NAME_BRIEF.get(proNames[0]), proPoints[0],
                PRO_NAME_BRIEF.get(proNames[1]), proPoints[1],
                PRO_NAME_BRIEF.get(proNames[2]), proPoints[2],
                PRO_NAME_BRIEF.get(proNames[3]), proPoints[3]);
    }
}
