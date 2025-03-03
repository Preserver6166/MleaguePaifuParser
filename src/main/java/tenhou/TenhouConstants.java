package tenhou;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

import java.util.LinkedHashMap;
import java.util.Map;

public class TenhouConstants {

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
     * TODO 部分未出现的役种需录入
     * TODO 复合役满的情况未录入
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
}
