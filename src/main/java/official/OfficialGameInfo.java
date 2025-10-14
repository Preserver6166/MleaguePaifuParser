package official;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

import static official.ProInfo.PRO_INFO;


@Builder
@Data
public class OfficialGameInfo {

    /**
     * 比赛索引信息
     */
    private String fileName;
    private int globalIndex; // 全局计算第几个本场
    private int seasonIndex; // 对应赛季第几个本场
    private int stageIndex; // 对应赛季阶段（常规赛、半决赛、决赛）第几个本场
    private int dayIndex; // 对应当天的第几回战
    private String season; // 赛季名称

    /**
     * 比赛时间信息
     */
    private String gameDate; // YYYY/mm/dd
    private String gameWeekDay; // 日月火水木金土
    private String startTime;
    private String endTime;
    private int interval; // 单位为分钟

    /**
     * 比赛结果信息
     */
    private String[] proNames;
    private BigDecimal[] proPoints; // 成绩
    private int[] proRanks; // 顺位

    /**
     * 比赛牌谱信息
     */
    private String nagaLink;

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
                PRO_INFO.get(proNames[0]).getProNameBrief(), proPoints[0],
                PRO_INFO.get(proNames[1]).getProNameBrief(), proPoints[1],
                PRO_INFO.get(proNames[2]).getProNameBrief(), proPoints[2],
                PRO_INFO.get(proNames[3]).getProNameBrief(), proPoints[3]);
    }

}
