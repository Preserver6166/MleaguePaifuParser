package official;

import java.util.LinkedHashMap;
import java.util.Map;

public class OfficialConstants {

    /**
     * 官方牌谱文件存放目录
     */
    public static final String PATH = System.getProperty("user.home");
    public static final String FILENAME_PREFIX_V2 = PATH + "/Documents/Mleague数据/doc_official_data_v2/";

    /**
     * 官方牌谱文件名称列表
     */
    public static final String SCHEDULE_FILE = "src/main/resources/schedule";

    /**
     * 官方牌谱赛季映射关系表
     */
    public static final Map<String, String> SEASON_MAP = new LinkedHashMap<>() {{
        put("S001", "18-19赛季 常规赛"); put("S002", "18-19赛季 决赛");
        put("S003", "19-20赛季 常规赛"); put("S004", "19-20赛季 半决赛"); put("S005", "19-20赛季 半决赛");
        put("S007", "20-21赛季 常规赛"); put("S008", "20-21赛季 半决赛"); put("S009", "20-21赛季 半决赛");
        put("S010", "21-22赛季 常规赛"); put("S011", "21-22赛季 半决赛"); put("S012", "21-22赛季 半决赛");
        put("S013", "22-23赛季 常规赛"); put("S014", "22-23赛季 半决赛"); put("S015", "22-23赛季 半决赛");
        put("S016", "23-24赛季 常规赛"); put("S017", "23-24赛季 半决赛"); put("S018", "23-24赛季 半决赛");
        put("S019", "24-25赛季 常规赛"); put("S020", "24-25赛季 半决赛"); put("S021", "24-25赛季 半决赛");
        put("S022", "25-26赛季 常规赛"); put("S023", "25-26赛季 半决赛"); put("S024", "25-26赛季 半决赛");
    }};

    /**
     * 各赛季的比赛场数
     */
    public static int[] GAMENUMS = {
            140, 24, // 2018-2019赛季没有半决赛
            180, 24, 12,
            180, 24, 12,
            180, 24, 12,
            188, 30, 16,
            216, 30, 16,
            216, 30, 16,
            240
    };

    /**
     * 官方牌谱解析用字符串
     */
    public static final String TIME_START_SIGNAL = "<div class=\"gameInfo\" id=\"dateTime\">";
    public static final String TIME_END_SIGNAL = "</div>";
    public static final String TIME_STRING_DEMO = "2018/10/01 (月) 19:15 〜 20:53";
    public static final String PAIFU_START_SIGNAL = "UMP_PLAYER.init(true, true, '";
    public static final String PAIFU_END_SIGHAL = "', autoplay);";

    /**
     * 每局结果表示
     */
    public static final String FAN_RON_PATTERN = "%d符%d飜%d点";
    public static final String FAN_TSUMO_ZI_PATTERN = "%d符%d飜%d-%d点";
    public static final String FAN_TSUMO_QIN_PATTERN = "%d符%d飜%d点∀";
    public static final String FAN_DETAIL_PATTERN = "%s(%d飜)";

    public static final Map<Integer, Integer> PAI_LEGAL_COUNT = new LinkedHashMap<>() {{
        put(11, 4); put(12, 4); put(13, 4);
        put(14, 4); put(15, 3); put(16, 4);
        put(17, 4); put(18, 4); put(19, 4);
        put(21, 4); put(22, 4); put(23, 4);
        put(24, 4); put(25, 3); put(26, 4);
        put(27, 4); put(28, 4); put(29, 4);
        put(31, 4); put(32, 4); put(33, 4);
        put(34, 4); put(35, 3); put(36, 4);
        put(37, 4); put(38, 4); put(39, 4);
        put(41, 4); put(42, 4); put(43, 4); put(44, 4);
        put(45, 4); put(46, 4); put(47, 4);
        put(51, 1); put(52, 1); put(53, 1);
    }};

    /**
     * NAGA评分模版
     */
//    public static final String NAGA_PATTERN_1 = "/** NAGA度：%s 80.0; %s 80.0; %s 80.0; %s 80.0 */";
//    public static final String NAGA_PATTERN_2 = "/** 一致率：%s 70.0%%; %s 70.0%%; %s 70.0%%; %s 70.0%% */";
//    public static final String NAGA_PATTERN_3 = "/** 恶手率：%s 5.0%%; %s 5.0%%; %s 5.0%%; %s 5.0%% */";
    public static final String NAGA_PATTERN_NEW = "/** %s(%s): NAGA度80.0; 一致率70.0%%; 恶手率5.0%% */";

}
