package official;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

import java.util.LinkedHashMap;
import java.util.Map;

public class OfficialConstants {

    /**
     * 官方牌谱文件存放目录
     */
    public static final String PATH = System.getProperty("user.home");
    public static final String FILENAME_PREFIX_V2 = PATH + "/Documents/Mleague数据/doc_official_data_v2/";

    /**
     * 官方牌谱文件名模版
     */
    public static final String URL_PATTERN_2018_REGULAR = "L001_S001_%s_0%dA";
    public static final String URL_PATTERN_2018_FINAL = "L001_S002_%s_0%dA";
    public static final String URL_PATTERN_2019_REGULAR = "L001_S003_%s_0%dA";
    public static final String URL_PATTERN_2019_SEMI = "L001_S004_%s_0%dA";
    public static final String URL_PATTERN_2019_FINAL = "L001_S005_%s_0%dA";
    public static final String URL_PATTERN_2020_REGULAR = "L001_S007_%s_0%dA";
    public static final String URL_PATTERN_2020_SEMI = "L001_S008_%s_0%dA";
    public static final String URL_PATTERN_2020_FINAL = "L001_S009_%s_0%dA";
    public static final String URL_PATTERN_2021_REGULAR = "L001_S010_%s_0%dA";
    public static final String URL_PATTERN_2021_SEMI = "L001_S011_%s_0%dA";
    public static final String URL_PATTERN_2021_FINAL = "L001_S012_%s_0%dA";
    public static final String URL_PATTERN_2022_REGULAR = "L001_S013_%s_0%dA";
    public static final String URL_PATTERN_2022_SEMI = "L001_S014_%s_0%dA";
    public static final String URL_PATTERN_2022_FINAL = "L001_S015_%s_0%dA";
    public static final String URL_PATTERN_2023_REGULAR = "L001_S016_%s_0%dA";
    public static final String URL_PATTERN_2023_SEMI = "L001_S017_%s_0%dA";
    public static final String URL_PATTERN_2023_FINAL = "L001_S018_%s_0%dA";
    public static final String URL_PATTERN_2024_REGULAR = "L001_S019_%s_0%dA";
    public static final String URL_PATTERN_2024_SEMI = "L001_S020_%s_0%dA";
    public static final String URL_PATTERN_2024_FINAL = "L001_S021_%s_0%dA";

    /**
     * SEASONS 赛季顺序
     * SEASON_NAMES 赛季名称
     * GAMENUMS 赛季比赛场数
     */
    public static final String[] SEASONS = {
            URL_PATTERN_2018_REGULAR, URL_PATTERN_2018_FINAL,
            URL_PATTERN_2019_REGULAR, URL_PATTERN_2019_SEMI, URL_PATTERN_2019_FINAL,
            URL_PATTERN_2020_REGULAR, URL_PATTERN_2020_SEMI, URL_PATTERN_2020_FINAL,
            URL_PATTERN_2021_REGULAR, URL_PATTERN_2021_SEMI, URL_PATTERN_2021_FINAL,
            URL_PATTERN_2022_REGULAR, URL_PATTERN_2022_SEMI, URL_PATTERN_2022_FINAL,
            URL_PATTERN_2023_REGULAR, URL_PATTERN_2023_SEMI, URL_PATTERN_2023_FINAL,
            URL_PATTERN_2024_REGULAR, URL_PATTERN_2024_SEMI, URL_PATTERN_2024_FINAL
    };

    public static final String[] SEASON_NAMES = {
            "18-19赛季 常规赛", "18-19赛季 决赛",
            "19-20赛季 常规赛", "19-20赛季 半决赛", "19-20赛季 决赛",
            "20-21赛季 常规赛", "20-21赛季 半决赛", "20-21赛季 决赛",
            "21-22赛季 常规赛", "21-22赛季 半决赛", "21-22赛季 决赛",
            "22-23赛季 常规赛", "22-23赛季 半决赛", "22-23赛季 决赛",
            "23-24赛季 常规赛", "23-24赛季 半决赛", "23-24赛季 决赛",
            "24-25赛季 常规赛", "24-25赛季 半决赛", "24-25赛季 决赛",
    };

    public static int[] GAMENUMS = {
            140, 24, // 2018-2019赛季没有半决赛
            180, 24, 12,
            180, 24, 12,
            180, 24, 12,
            188, 30, 16,
            216, 30, 16,
            216, 30, 16
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
