package official;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

import java.util.LinkedHashMap;
import java.util.Map;

public class Constants {

    /**
     * 官方牌谱文件存放目录
     */
    public static final String PATH = System.getProperty("user.home");
    public static final String FILENAME_PREFIX_18_22 = PATH + "/Documents/Mleague数据/doc_official_18-22_data/";
    public static final String FILENAME_PREFIX_22_23 = PATH + "/Documents/Mleague数据/doc_official_22-23_data/";
    public static final String FILENAME_PREFIX_23_24 = PATH + "/Documents/Mleague数据/doc_official_23-24_data/";
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
            URL_PATTERN_2023_REGULAR, URL_PATTERN_2023_SEMI, URL_PATTERN_2023_FINAL
    };

    public static final String[] SEASON_NAMES = {
            "18-19赛季 常规赛", "18-19赛季 决赛",
            "19-20赛季 常规赛", "19-20赛季 半决赛", "19-20赛季 决赛",
            "20-21赛季 常规赛", "20-21赛季 半决赛", "20-21赛季 决赛",
            "21-22赛季 常规赛", "21-22赛季 半决赛", "21-22赛季 决赛",
            "22-23赛季 常规赛", "22-23赛季 半决赛", "22-23赛季 决赛",
            "23-24赛季 常规赛", "23-24赛季 半决赛", "23-24赛季 决赛",
    };

    public static int[] GAMENUMS = {
            140, 24, // 2018-2019赛季没有半决赛
            180, 24, 12,
            180, 24, 12,
            180, 24, 12,
            188, 30, 16,
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

    /**
     * 官方牌谱与天凤牌谱的tile转换关系
     */
    public static final Map<String, Integer> PAI_NAME_MAPPING_RAW = new LinkedHashMap<>() {{
        put("1m", 11); put("2m", 12); put("3m", 13);
        put("4m", 14); put("5m", 15); put("6m", 16);
        put("7m", 17); put("8m", 18); put("9m", 19);
        put("1p", 21); put("2p", 22); put("3p", 23);
        put("4p", 24); put("5p", 25); put("6p", 26);
        put("7p", 27); put("8p", 28); put("9p", 29);
        put("1s", 31); put("2s", 32); put("3s", 33);
        put("4s", 34); put("5s", 35); put("6s", 36);
        put("7s", 37); put("8s", 38); put("9s", 39);
        put("1z", 41); put("2z", 42); put("3z", 43); put("4z", 44); // 东南西北
        put("5z", 45); put("6z", 46); put("7z", 47); // 白发中
        put("5M", 51); put("5P", 52); put("5S", 53); // aka
    }};

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
        put(51, 1); put(52, 1); put(53, 1);
    }};

    public static final BiMap<String, Integer> PAI_NAME_MAPPING =
            new ImmutableBiMap.Builder<String, Integer>().putAll(PAI_NAME_MAPPING_RAW).build();

    /**
     * dora与dora指示牌的转换关系(天凤牌谱的tile表示)
     */
    public static final Map<Integer, Integer> DORA_INDICATOR_MAPPING = new LinkedHashMap<>() {{
        put(11, 19); put(12, 11); put(13, 12);
        put(14, 13); put(15, 14); put(16, 15);
        put(17, 16); put(18, 17); put(19, 18);
        put(51, 14);
        put(21, 29); put(22, 21); put(23, 22);
        put(24, 23); put(25, 24); put(26, 25);
        put(27, 26); put(28, 27); put(29, 28);
        put(52, 24);
        put(31, 39); put(32, 31); put(33, 32);
        put(34, 33); put(35, 34); put(36, 35);
        put(37, 36); put(38, 37); put(39, 38);
        put(53, 34);
        put(41, 44); put(42, 41); put(43, 42); put(44, 43);
        put(45, 47); put(46, 45); put(47, 46);
    }};

    /**
     * FAN_MAPPING 官方牌谱与天凤牌谱的役种转换关系(非役满)
     * YAKUMAN_MAPPING 官方牌谱与天凤牌谱的役种转换关系(役满)
     * TODO 补充役种
     */
    public static final Map<String, String> FAN_MAPPING = new LinkedHashMap<>() {{
        put("リーチ", "立直"); put("一発", "一発"); put("門前自摸", "門前清自摸和");
        put("平和", "平和"); put("一盃口", "一盃口"); put("タンヤオ", "断幺九");
        put("発", "役牌 發"); put("白", "役牌 白"); put("中", "役牌 中");
        put("自東", "自風 東"); put("自南", "自風 南");
        put("場東", "場風 東"); put("場南", "場風 南");
        put("西", "自風 西"); put("北", "自風 北"); // 半庄战场风只为東、南
        put("ドラ", "ドラ"); put("赤", "赤ドラ"); put("裏ドラ", "裏ドラ");
        put("ダブルリーチ", "両立直"); put("嶺上開花", "嶺上開花");
        put("河底撈魚", "河底撈魚"); put("海底摸月", "海底摸月");
        put("一気通貫", "一気通貫"); put("三色同順", "三色同順"); put("三色同刻", "三色同刻");
        put("チャンタ", "混全帯幺九"); put("純チャン", "純全帯幺九");
        put("三暗刻", "三暗刻"); put("小三元", "小三元");
        put("混一色", "混一色"); put("七対子", "七対子"); put("対々和", "対々和");
        put("清一色", "清一色");
        put("槍槓", "槍槓"); put("混老頭", "混老頭"); put("二盃口", "二盃口");
    }};

    public static final Map<String, String> YAKUMAN_MAPPING = new LinkedHashMap<>() {{
        put("国士無双", "国士無双(役満)");
        put("大三元", "大三元(役満)");
        put("四暗刻", "四暗刻(役満)");
    }};

    /**
     * NAGA评分模版
     */
    public static final String NAGA_PATTERN_1 = "/** NAGA度：%s 80.0; %s 80.0; %s 80.0; %s 80.0 */";
    public static final String NAGA_PATTERN_2 = "/** 一致率：%s 70.0%%; %s 70.0%%; %s 70.0%%; %s 70.0%% */";
    public static final String NAGA_PATTERN_3 = "/** 恶手率：%s 5.0%%; %s 5.0%%; %s 5.0%%; %s 5.0%% */";

}
