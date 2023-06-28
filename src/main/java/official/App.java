package official;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import org.apache.commons.lang3.StringUtils;
import tenhou.KyokuLog;
import tenhou.TenhouPaifu;
import tenhou.TenhouRule;
import util.Majong;

import java.io.File;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static official.ProInfo.PRO_INFO;

public class App {

    /** TODO
     2019/10/17 第1試合 佐々木寿人 vs 小林剛 vs 鈴木たろう vs 黒沢咲
     黒沢咲 +57.4pt; 小林剛 +16.9pt; 佐々木寿人 -25.3pt; 鈴木たろう -49.0pt
     官方牌谱：https://viewer.ml-log.jp/web/viewer?gameid=L001_S003_0011_01A
     天凤牌谱：
     */
    public static final String PATH = System.getProperty("user.home");

    public static final String FILENAME_PREFIX_18_22 = PATH + "/Documents/Mleague数据/doc_official_18-22_data/";
    public static final String FILENAME_PREFIX_22_23 = PATH + "/Documents/Mleague数据/doc_official_22-23_data/";

    public static final String URL_PATTERN_2018_REGULAR = "L001_S001_00%s_0%dA";
    public static final String URL_PATTERN_2018_FINAL = "L001_S002_00%s_0%dA";
    public static final String URL_PATTERN_2019_REGULAR = "L001_S003_00%s_0%dA";
    public static final String URL_PATTERN_2019_SEMI = "L001_S004_00%s_0%dA";
    public static final String URL_PATTERN_2019_FINAL = "L001_S005_00%s_0%dA";
    public static final String URL_PATTERN_2020_REGULAR = "L001_S007_00%s_0%dA";
    public static final String URL_PATTERN_2020_SEMI = "L001_S008_00%s_0%dA";
    public static final String URL_PATTERN_2020_FINAL = "L001_S009_00%s_0%dA";
    public static final String URL_PATTERN_2021_REGULAR = "L001_S010_00%s_0%dA";
    public static final String URL_PATTERN_2021_SEMI = "L001_S011_00%s_0%dA";
    public static final String URL_PATTERN_2021_FINAL = "L001_S012_00%s_0%dA";
    public static final String URL_PATTERN_2022_REGULAR = "L001_S013_00%s_0%dA";
    public static final String URL_PATTERN_2022_SEMI = "L001_S014_00%s_0%dA";
    public static final String URL_PATTERN_2022_FINAL = "L001_S015_00%s_0%dA";

    public static final String[] SEASONS = {
            URL_PATTERN_2018_REGULAR, URL_PATTERN_2018_FINAL,
            URL_PATTERN_2019_REGULAR, URL_PATTERN_2019_SEMI, URL_PATTERN_2019_FINAL,
            URL_PATTERN_2020_REGULAR, URL_PATTERN_2020_SEMI, URL_PATTERN_2020_FINAL,
            URL_PATTERN_2021_REGULAR, URL_PATTERN_2021_SEMI, URL_PATTERN_2021_FINAL,
            URL_PATTERN_2022_REGULAR, URL_PATTERN_2022_SEMI, URL_PATTERN_2022_FINAL
    };

    public static final String[] SEASON_NAMES = {
            "18-19赛季 常规赛", "18-19赛季 决赛",
            "19-20赛季 常规赛", "19-20赛季 半决赛", "19-20赛季 决赛",
            "20-21赛季 常规赛", "20-21赛季 半决赛", "20-21赛季 决赛",
            "21-22赛季 常规赛", "21-22赛季 半决赛", "21-22赛季 决赛",
            "22-23赛季 常规赛", "22-23赛季 半决赛", "22-23赛季 决赛"
    };

    public static int[] GAMEDAYS = {
            140, 24, // 2018-2019赛季没有半决赛
            180, 24, 12,
            180, 24, 12,
            180, 24, 12,
            188, 30, 16
    };

    public static final List<OfficialGameInfo> OFFICIAL_GAME_INFO_LIST = new ArrayList<>();
    public static final Map<String, TenhouPaifu> TENHOU_PAIFU_MAP = new LinkedHashMap<>();

    public static final String TIME_START_SIGNAL = "<div class=\"gameInfo\" id=\"dateTime\">";
    public static final String TIME_END_SIGNAL = "</div>";
    public static final String TIME_STRING_DEMO = "2018/10/01 (月) 19:15 〜 20:53";

    public static final String PAIFU_START_SIGNAL = "UMP_PLAYER.init(true, true, '";
    public static final String PAIFU_END_SIGHAL = "', autoplay);";

    public static final String FAN_RON_PATTERN = "%d符%d飜%d点";
    public static final String FAN_TSUMO_ZI_PATTERN = "%d符%d飜%d-%d点";
    public static final String FAN_TSUMO_QIN_PATTERN = "%d符%d飜%d点∀";
    public static final String FAN_DETAIL_PATTERN = "%s(%d飜)";

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

    // TODO 补充役种
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

    static {
        try {
            Iterator<OfficialGameInfo.OfficialGameInfoBuilder> iterator = new GameFileIterator().iterator();
            while(iterator.hasNext()) {
                OfficialGameInfo.OfficialGameInfoBuilder officialGameInfoBuilder = iterator.next();
                String fileName = officialGameInfoBuilder.build().getFileName();
//                if (!fileName.equals("L001_S013_0062_02A")) {
//                    continue;
//                }
//                if (fileName.compareTo("L001_S003_0070_02A") <= 0) {
//                    continue;
//                }
//                if (fileName.compareTo("L001_S001_0010_01A") >= 0) {
//                    continue;
//                }
                assert fileName.length() == 18;
                String fileNamePrefix = (fileName.contains("S013") || fileName.contains("S014") || fileName.contains("S015")) ?
                        FILENAME_PREFIX_22_23 : FILENAME_PREFIX_18_22;
                File file = new File(fileNamePrefix + fileName);
                if (file.exists()) {
                    officialGameInfoBuilder.dayIndex(fileName.charAt(16) - 48);

                    Scanner scanner = new Scanner(file);
                    while(scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if (line.contains(TIME_START_SIGNAL)) {
                            parseTime(officialGameInfoBuilder, line);
                        } else if (line.contains(PAIFU_START_SIGNAL)) {
                            parsePaifu(officialGameInfoBuilder, line);
                        }
                    }
                    scanner.close();

                    OfficialGameInfo gameInfo = officialGameInfoBuilder.build();
                    OFFICIAL_GAME_INFO_LIST.add(gameInfo);
                    try {
                        TenhouPaifu tenhouPaifu = o2t(gameInfo);
                        TENHOU_PAIFU_MAP.put(fileName, tenhouPaifu);
//                        System.out.println(fileName + "\t" + tenhouPaifu.getLog().size());
//                        printGame(fileName, gameInfo, tenhouPaifu);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    System.out.println(fileName + " does not exist");
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("GAME_INFOS size:" + OFFICIAL_GAME_INFO_LIST.size());
    }

    private static void printGame(String fileName, OfficialGameInfo gameInfo, TenhouPaifu tenhouPaifu) {
        System.out.println("++++++++" + fileName + "++++++++");
        System.out.println(gameInfo.getGameBrief());
        System.out.println(gameInfo.getGameResult());
        System.out.println();
        String naga1 = "/** NAGA度：%s 80.0; %s 80.0; %s 80.0; %s 80.0 */";
        String naga2 = "/** 一致率：%s 70.0%%; %s 70.0%%; %s 70.0%%; %s 70.0%% */";
        String naga3 = "/** 恶手率：%s 5.0%%; %s 5.0%%; %s 5.0%%; %s 5.0%% */";
        System.out.println(String.format(naga1,
                PRO_INFO.get(gameInfo.getProNames()[0]).getProNameBrief(),
                PRO_INFO.get(gameInfo.getProNames()[1]).getProNameBrief(),
                PRO_INFO.get(gameInfo.getProNames()[2]).getProNameBrief(),
                PRO_INFO.get(gameInfo.getProNames()[3]).getProNameBrief()));
        System.out.println(String.format(naga2,
                PRO_INFO.get(gameInfo.getProNames()[0]).getProNameBrief(),
                PRO_INFO.get(gameInfo.getProNames()[1]).getProNameBrief(),
                PRO_INFO.get(gameInfo.getProNames()[2]).getProNameBrief(),
                PRO_INFO.get(gameInfo.getProNames()[3]).getProNameBrief()));
        System.out.println(String.format(naga3,
                PRO_INFO.get(gameInfo.getProNames()[0]).getProNameBrief(),
                PRO_INFO.get(gameInfo.getProNames()[1]).getProNameBrief(),
                PRO_INFO.get(gameInfo.getProNames()[2]).getProNameBrief(),
                PRO_INFO.get(gameInfo.getProNames()[3]).getProNameBrief()));
        System.out.println("/** NAGA牌谱及分析详情：");
        System.out.println("https://naga.dmv.nico/htmls/053227e0e24c174441bd302c0f0f1680063eadbc24d0888148191b2f1673b517v2_2_2.html?tw=0");
        System.out.println("*/");
        System.out.println();
        System.out.println("/** 官方牌谱：");
        System.out.println("https://viewer.ml-log.jp/web/viewer?gameid=" + fileName);
        System.out.println("*/");
        System.out.println();
        System.out.println("/** 天凤牌谱：（未经双重校验）");
        System.out.println("https://tenhou.net/5/#json=" + JSONObject.toJSONString(tenhouPaifu));
        System.out.println("*/");
        System.out.println();
        for (String kyokuBrief: tenhouPaifu.getKyokuBrief()) {
            System.out.println(kyokuBrief);
        }
        System.out.println();

    }

    public static void main (String[] args) throws Exception {
        // fun19();
        // fun20();
        fun21();
        // System.out.println(TENHOU_PAIFU_MAP.get("L001_S003_0001_02A").toString());
    }

    private static void fun21() {
        for(ProInfo proInfo: PRO_INFO.values()) {
            Double avgShanten = new BigDecimal(Double.valueOf(proInfo.getHaipaiShanten()) / proInfo.getKyokuCount())
                    .setScale(3, RoundingMode.HALF_UP).doubleValue();
            Double avgDora = new BigDecimal(Double.valueOf(proInfo.getHaipaiDoraCount()) / proInfo.getKyokuCount())
                    .setScale(3, RoundingMode.HALF_UP).doubleValue();
            System.out.println(proInfo.getProNameBrief() + "\t" + avgShanten + "\t" + avgDora + "\t" + proInfo.getKyokuCount());
        }
    }

    private static void fun20() {
        Map<String, Integer> proName_nagaPoint = new LinkedHashMap<>() {{
            put("渋川難波", 904); put("岡田紗佳", 894); put("仲林圭", 894); put("内川幸太郎", 892);
            put("鈴木優", 885); put("魚谷侑未", 884); put("小林剛", 881); put("本田朋広", 880);
            put("松本吉弘", 870); put("白鳥翔", 868); put("滝沢和典", 866); put("東城りお", 865);
            put("園田賢", 865); put("鈴木たろう", 864); put("堀慎吾", 863); put("二階堂亜樹", 862);
            put("伊達朱里紗", 862); put("高宮まり", 860); put("茅森早香", 857); put("佐々木寿人", 856);
            put("多井隆晴", 853); put("丸山奏子", 849); put("松ヶ瀬隆弥", 848); put("日向藍子", 848);
            put("勝又健志", 844); put("瀬戸熊直樹", 843); put("瑞原明奈", 843); put("近藤誠一", 839);
            put("村上淳", 839); put("黒沢咲", 838); put("萩原聖人", 825); put("二階堂瑠美", 813);
        }};

        Map<String, Integer> proName_pt = new LinkedHashMap<>();
        for(OfficialGameInfo officialGameInfo: OFFICIAL_GAME_INFO_LIST) {
            String[] proNames = officialGameInfo.getProNames();
            String[] proPoints = officialGameInfo.getProPoints();
            for (int i=0; i<proNames.length; i++) {
                proName_pt.putIfAbsent(proNames[i], 0);
                int point = new BigDecimal(Double.parseDouble(proPoints[i]) * 10).intValue();
                proName_pt.put(proNames[i], proName_pt.get(proNames[i]) + point);
            }
        }

        for (String proName: proName_nagaPoint.keySet()) {
            System.out.println(proName + "\t" + proName_nagaPoint.get(proName) + "\t" + proName_pt.get(proName));
        }
    }

    /**
     * 把官方牌谱转换成天凤再生牌谱
     {
     "title":[
     "Mリーグ2019　ファイナルシリーズ 6/23(火)",
     "第2試合"
     ],
     "name":[
     "沢崎-228.2",
     "多井+127.9",
     "魚谷+175.5",
     "小林+183.5"
     ],
     "rule":{
     "disp":"Mリーグ2019　FINAL 12/12　6/23 第2試合",
     "aka":1
     },
     "log":[
     [
     [1,0,0],[25000,28900,21100,25000], // 1代表東2局, 0代表0本场, 0代表供托
     [24],[31], // 宝牌指示牌, 里宝牌指示牌
     [11,12,15,19,24,29,32,53,41,42,43,44,47], // 本局庄家起手牌
     [12,46,43,25,37,35,22,33,41,18,32,17,14,25,38], // 本局庄家摸的牌
     [32,15,12,12,11,19,41,60,43,35,29,32,43,47,42], // 本局庄家出的牌
     [13,13,14,18,19,27,27,28,31,36,36,39,45],
     [37,47,31,27,38,25,37,19,15,36,29,26,21,23,34,51],
     [31,60,60,45,14,13,13,39,60,28,60,18,60,60,19,19],
     [11,13,14,17,23,27,28,28,33,42,42,46,47],
     [24,46,34,37,34,36,26,21,22,18,11,32,23,38,43,12],
     [46,60,11,17,47,42,28,33,21,60,60,60,60,"r42",60],
     [11,14,15,16,18,21,28,33,35,39,45,46,47],
     [44,24,17,21,39,43,41,16,12,32,42,19,45,26,22],
     [21,39,45,60,60,44,60,46,28,35,60,43,16,47,45],
     [
     "和了",
     [-1300,-2600,6200,-1300],
     [2,2,2,"20符4飜1300-2600点","立直(1飜)","門前清自摸和(1飜)","断幺九(1飜)","平和(1飜)"]
     ]
     ]
     ]
     }
     */
    public static TenhouPaifu o2t(OfficialGameInfo gameInfo) throws Exception {

        TenhouPaifu.TenhouPaifuBuilder tenhouPaifuBuilder = TenhouPaifu.builder();

        String[] title = new String[2];
        title[0] = gameInfo.getSeason();
        title[1] = gameInfo.getGameName();
        tenhouPaifuBuilder.title(title);

        tenhouPaifuBuilder.name(gameInfo.getProNames());

        TenhouRule tenhouRule = new TenhouRule();
        tenhouRule.setDisp(gameInfo.getGameName()); // 似乎没有必要填这个参数
        tenhouRule.setAka(1);

        tenhouPaifuBuilder.rule(tenhouRule);

        List<List<Object>> log = new ArrayList<>();
        tenhouPaifuBuilder.log(log);

        List<String> kyokuBriefList = new ArrayList<>();
        tenhouPaifuBuilder.kyokuBrief(kyokuBriefList);

//        Map<String, List<Integer>> haipaiShantenInfo = new HashMap<>();
//        tenhouPaifuBuilder.haipaiShantenInfo(haipaiShantenInfo);
//
//        Map<String, List<Integer>> haipaiDoraInfo = new HashMap<>();
//        tenhouPaifuBuilder.haipaiDoraInfo(haipaiDoraInfo);

        KyokuLog kyokuLog = null;

        List<OfficialPaifuLog> officialPaifuLogList = gameInfo.getOfficialPaifuLogList();
        Iterator<OfficialPaifuLog> iterator = officialPaifuLogList.iterator();
        String openStatus = "";
        int lastSutehaiProIndex = -1;
        List<String> ponRecordList = new ArrayList<>(); // 处理
        boolean[] tenPaiProNumInfo = new boolean[4];

        while (iterator.hasNext()) {
            OfficialPaifuLog officialPaifuLog = iterator.next();
            String cmd = officialPaifuLog.getCmd();
            String[] args = officialPaifuLog.getArgs();

            if (cmd.equals("area") || cmd.equals("player") || cmd.equals("dice") || cmd.equals("gamestart")) {
                // do nothing
            }

            if (cmd.equals("kyokustart")) {
                // 亲家是B0, 场风是2z, 各家的自风
                // ["0", "B0", "0", "0", "2z", "4z", "1z", "2z", "3z"]
                kyokuLog = new KyokuLog();
                assert args.length == 9;
                int kyokuIndex = calculateKyokuIndex(args[4], args[5]);
                kyokuLog.setKyokuStartInfo(
                        kyokuIndex, Integer.valueOf(args[2]), Integer.valueOf(args[3]) / 1000);
            }

            if (cmd.equals("point")) {
                assert args.length == 2;
                int proIndex = args[0].charAt(0) - 65;
                if (args[1].startsWith("=")) {
                    int pointValue = Integer.valueOf(args[1].substring(1));
                    kyokuLog.setKyokuStartPointInfo(proIndex, pointValue);
                } else if (args[1].startsWith("+") || args[1].startsWith("-")) {
                    int pointValue = Integer.valueOf(args[1]);
                    kyokuLog.appendKyokuEndPointInfo(proIndex, pointValue);
                } else {
                    // should not go into here
                }
            }

            if (cmd.equals("dora")) {
                // [6p, 5p] 宝牌和宝牌指示牌
                assert args.length == 2;
                // 天凤牌谱中只需要保存宝牌指示牌
                if (PAI_NAME_MAPPING.get(args[1]) == null) {
                    String kyokuStartInfo = kyokuLog.getKyokuStartInfoInStringFormat();
                    System.out.println(gameInfo.getFileName() + " " + kyokuStartInfo + " 漏记dora" +
                            " ID:" + officialPaifuLog.getId());
                } else {
                    int paiName = PAI_NAME_MAPPING.get(args[1]);
                    kyokuLog.getDoraInfo().add(paiName);
                }
            }

            if (cmd.equals("haipai")) {
                assert args.length == 2;
                int proIndex = args[0].charAt(0) - 65;
                for (int i=0; i<args[1].length(); i+=2) {
                    int paiName = PAI_NAME_MAPPING.get(args[1].substring(i, i+2));
                    kyokuLog.setHaipaiInfo(proIndex, paiName);
                }
            }

            if (cmd.equals("tsumo")) {
                assert args.length == 3;
                // [A0, 69, 5s] 选手, 剩余牌数, 摸到的牌
                int proIndex = args[0].charAt(0) - 65;
                // 官方牌谱常见错误1: 漏记摸牌
                if (PAI_NAME_MAPPING.get(args[2]) == null) {
                    String kyokuStartInfo = kyokuLog.getKyokuStartInfoInStringFormat();
                    System.out.println(gameInfo.getFileName() + " " + kyokuStartInfo + " 漏记" +
                            PRO_INFO.get(gameInfo.getProNames()[proIndex]).getProNameBrief() + "摸牌" +
                            " ID:" + officialPaifuLog.getId());
                } else {
                    int paiName = PAI_NAME_MAPPING.get(args[2]);
                    kyokuLog.appendTsumoInfo(proIndex, paiName);
                }
            }

            if (cmd.equals("sutehai")) {
                assert args.length == 2 || args.length == 3 || args.length == 4;
                int proIndex = args[0].charAt(0) - 65;
                lastSutehaiProIndex = proIndex;
                if (PAI_NAME_MAPPING.get(args[1]) == null) {
                    String kyokuStartInfo = kyokuLog.getKyokuStartInfoInStringFormat();
                    System.out.println(gameInfo.getFileName() + " " + kyokuStartInfo + " 漏记" +
                            PRO_INFO.get(gameInfo.getProNames()[proIndex]).getProNameBrief() + "出牌" +
                            " ID:" + officialPaifuLog.getId());
                } else {
                    int paiName = PAI_NAME_MAPPING.get(args[1]);
                    if (args.length == 2) {
                        kyokuLog.appendSutehaiInfo(proIndex, paiName);
                    } else if (args.length == 3) {
                        if (args[2].equals("tsumogiri")) {
                            kyokuLog.appendSutehaiInfo(proIndex, 60);
                        } else if (args[2].equals("richi")) {
                            kyokuLog.appendSutehaiInfo(proIndex, "r" + paiName);
                        } else {
                            // should not go into here
                        }
                    } else if (args.length == 4) {
                        // 模切立直
                        kyokuLog.appendSutehaiInfo(proIndex, "r60");
                    } else {
                        // should not go into here
                    }
                }
            }

            if (cmd.equals("say")) {
                assert args.length == 2;
                int proIndex = args[0].charAt(0) - 65;
                // official [ron=7m, A0, 7700, 40, リーチ, 1, 河底撈魚, 1, 発, 1])
                // tenhou ["和了",[9700,-7700,0,0],[0,1,0,"40符3飜7700点","立直(1飜)","役牌 發(1飜)","河底撈魚(1飜)"]]
                if (args[1].equals("ron")) {
                    // 后续会紧跟一个open
                } else if (args[1].equals("tsumo")) {
                    // 后续会紧跟一个open
                } else if (args[1].equals("chi")) {
                    // 后续会紧跟一个open
                    openStatus = "c";
                } else if (args[1].equals("pon")) {
                    // 后续会紧跟一个open
                    openStatus = "p";
                } else if (args[1].equals("kan")){
                    openStatus = "k";
                } else if (args[1].equals("richi")) {
                    // 后续会紧接一个sutehai
                } else if (args[1].equals("tenpai")) {
                    tenPaiProNumInfo[proIndex] = true;
                } else if (args[1].equals("noten")) {
                    // do nothing
                } else {
                    // System.out.println("There is a " + args[1]);
                }
            }

            if (cmd.equals("richi")) {
                // do nothing
                int proIndex = args[0].charAt(0) - 65;
                kyokuLog.appendKyokuEndPointInfo(proIndex, 1000);
            }

            if (cmd.equals("open")) {
                int proIndex = args[0].charAt(0) - 65;
                if (openStatus.equals("c")) { // 只能从上家吃，所以不需要判断lastSutehaiProIndex
                    // ["A0", "<4p5p>", "6p"]
                    int targetPaiName = PAI_NAME_MAPPING.get(args[2]);
                    StringBuilder sb = new StringBuilder(openStatus);
                    sb.append(targetPaiName);
                    int sourcePaiName1 = PAI_NAME_MAPPING.get(args[1].substring(1, 3));
                    sb.append(sourcePaiName1);
                    int sourcePaiName2 = PAI_NAME_MAPPING.get(args[1].substring(3, 5));
                    sb.append(sourcePaiName2);
                    kyokuLog.appendTsumoInfo(proIndex, sb.toString());
                } else if (openStatus.equals("p")) { // 杠的情况还未加入
                    int targetPaiName = PAI_NAME_MAPPING.get(args[2]);
                    StringBuilder sb = new StringBuilder();
                    int sourcePaiName1 = PAI_NAME_MAPPING.get(args[1].substring(1, 3));
                    int sourcePaiName2 = PAI_NAME_MAPPING.get(args[1].substring(3, 5));
                    int indexDiff = calculateProIndexDiff(proIndex, lastSutehaiProIndex);
                    if (indexDiff == 1) { // 上家
                        sb.append(openStatus);
                        sb.append(targetPaiName);
                        sb.append(sourcePaiName1);
                        sb.append(sourcePaiName2);
                    } else if (indexDiff == 2) { // 对家
                        sb.append(sourcePaiName1);
                        sb.append(openStatus);
                        sb.append(targetPaiName);
                        sb.append(sourcePaiName2);
                    } else if (indexDiff == 3) { // 下家
                        sb.append(sourcePaiName1);
                        sb.append(sourcePaiName2);
                        sb.append(openStatus);
                        sb.append(targetPaiName);
                    }
                    ponRecordList.add(sb.toString());
                    kyokuLog.appendTsumoInfo(proIndex, sb.toString());
                } else if (openStatus.equals("k")) {
                    StringBuilder sb = new StringBuilder();
                    if (args.length == 2) { // 暗杠
                        int sourcePaiName1 = PAI_NAME_MAPPING.get(args[1].substring(1, 3));
                        int sourcePaiName2 = PAI_NAME_MAPPING.get(args[1].substring(3, 5));
                        int sourcePaiName3 = PAI_NAME_MAPPING.get(args[1].substring(5, 7));
                        int sourcePaiName4 = PAI_NAME_MAPPING.get(args[1].substring(7, 9));
                        sb.append(sourcePaiName1);
                        sb.append(sourcePaiName2);
                        sb.append(sourcePaiName3);
                        sb.append("a");
                        sb.append(sourcePaiName4);
                        kyokuLog.appendSutehaiInfo(proIndex, sb.toString());
                    } else {
                        int targetPaiName = PAI_NAME_MAPPING.get(args[2]);
                        String kanRecord = convertPonRecordIfPossible(ponRecordList, targetPaiName);
                        if (kanRecord != null) {
                            // 加杠
                            kyokuLog.appendSutehaiInfo(proIndex, kanRecord);
                            lastSutehaiProIndex = proIndex;
                        } else {
                            // System.out.println(gameInfo.getFileName());
                            // 明杠之后, 要在sutehai里加个0
                            int sourcePaiName1 = PAI_NAME_MAPPING.get(args[1].substring(1, 3));
                            int sourcePaiName2 = PAI_NAME_MAPPING.get(args[1].substring(3, 5));
                            int sourcePaiName3 = PAI_NAME_MAPPING.get(args[1].substring(5, 7));
                            int indexDiff = calculateProIndexDiff(proIndex, lastSutehaiProIndex);
                            if (indexDiff == 1) { // 上家
                                sb.append("m");
                                sb.append(targetPaiName);
                                sb.append(sourcePaiName1);
                                sb.append(sourcePaiName2);
                                sb.append(sourcePaiName3);
                            } else if (indexDiff == 2) { // 对家
                                sb.append(sourcePaiName1);
                                sb.append("m");
                                sb.append(sourcePaiName2);
                                sb.append(targetPaiName);
                                sb.append(sourcePaiName3);
                            } else if (indexDiff == 3) { // 下家
                                sb.append(sourcePaiName1);
                                sb.append(sourcePaiName2);
                                sb.append(sourcePaiName3);
                                sb.append("m");
                                sb.append(targetPaiName);
                            }
                            kyokuLog.appendTsumoInfo(proIndex, sb.toString());
                            kyokuLog.appendSutehaiInfo(proIndex, Integer.valueOf(0));
                        }
                    }
                } else {
                    // do nothing
                }
                openStatus = "";
            }

            if (cmd.equals("uradora")) {
                if (StringUtils.isEmpty(args[1])) {
                    // do nothing
                } else {
                    int paiName = PAI_NAME_MAPPING.get(args[1]);
                    kyokuLog.getUraInfo().add(paiName);
                }
            }

            if (cmd.equals("agari")) {
                // System.out.println(String.join(",", args));
                kyokuLog.setKyokuEndResult("和了");
                List<String> fanList = new ArrayList<>();
                int commentBias = (args[0].startsWith("comment") || args[1].startsWith("comment")) ? 1 : 0;
                boolean isRon = (args[0].startsWith("ron") || args[1].startsWith("ron"));
                int ronBias = isRon ? 1 : 0;
                // [ron=7m, A0, 7700, 40, リーチ, 1, 河底撈魚, 1, 発, 1]
                // [D0, 1300, 20, 門前自摸, 1, 平和, 1]
                // [B0, 役満, 0, 国士無双, 1]
                int proIndex = args[ronBias + commentBias].charAt(0) - 65;
                kyokuLog.appendKyokuEndDetail(proIndex); // 和牌家
                kyokuLog.appendKyokuEndDetail(isRon ? lastSutehaiProIndex : proIndex); // 放铳家
                kyokuLog.appendKyokuEndDetail(proIndex); // 和牌家
                boolean isQin = (proIndex == kyokuLog.getKyokuStartInfo()[0] % 4);
                int fu = Integer.valueOf(args[2 + ronBias + commentBias]);
                int totalFan = 0;
                for (int i=3+ronBias+commentBias; i<args.length; i+=2) {
                    if (YAKUMAN_MAPPING.get(args[i]) != null) {
                        fanList.add(YAKUMAN_MAPPING.get(args[i]));
                    } else {
                        int currentFan = Integer.valueOf(args[i + 1]);
                        totalFan += currentFan;
                        if (args[i].equals("東") || args[i].equals("南")) {
                            assert currentFan == 1;
                            if (args[i].equals("東") && kyokuLog.getKyokuStartInfo()[0] <= 3) {
                                fanList.add(String.format(FAN_DETAIL_PATTERN,
                                        FAN_MAPPING.get("場" + args[i]), 1));
                            } else if (args[i].equals("南") && kyokuLog.getKyokuStartInfo()[0] > 3) {
                                fanList.add(String.format(FAN_DETAIL_PATTERN,
                                        FAN_MAPPING.get("場" + args[i]), 1));
                            } else {
                                fanList.add(String.format(FAN_DETAIL_PATTERN,
                                        FAN_MAPPING.get("自" + args[i]), 1));
                            }
                        } else if (args[i].equals("ダブ東") || args[i].equals("ダブ南")) {
                            String fanName = args[i].substring(2,3);
                            fanList.add(String.format(FAN_DETAIL_PATTERN,
                                    FAN_MAPPING.get("自" + fanName), 1));
                            fanList.add(String.format(FAN_DETAIL_PATTERN,
                                    FAN_MAPPING.get("場" + fanName), 1));
                        } else {
                            fanList.add(String.format(FAN_DETAIL_PATTERN, FAN_MAPPING.get(args[i]), currentFan));
                        }
                    }
                }

                String pointInfo = generatePointInfo(args[1 + ronBias + commentBias], isRon, isQin, fu, totalFan);
                kyokuLog.appendKyokuEndDetail(pointInfo);
                for(String fan: fanList) {
                    kyokuLog.appendKyokuEndDetail(fan);
                }
                String kyokuBrief = generateKyokuBriefIfAgari(args[1+ronBias+commentBias], isRon, isQin,
                        kyokuLog.getKyokuStartInfo(), gameInfo.getProNames(), proIndex, lastSutehaiProIndex);
                kyokuLog.setKyokuBrief(kyokuBrief);

            }

            if (cmd.equals("ryukyoku")) {
                int tenPaiProNum = (tenPaiProNumInfo[0] ? 1 : 0) + (tenPaiProNumInfo[1] ? 1 : 0) +
                        (tenPaiProNumInfo[2] ? 1 : 0) + (tenPaiProNumInfo[3] ? 1 : 0);
                if (tenPaiProNum == 0) {
                    kyokuLog.setKyokuEndResult("全員不聴");
                } else if (tenPaiProNum == 4) {
                    kyokuLog.setKyokuEndResult("全員聴牌");
                } else {
                    kyokuLog.setKyokuEndResult("流局");
                }
                String kyokuBrief = generateKyokuBriefIfRyukyoku(tenPaiProNumInfo,
                        kyokuLog.getKyokuStartInfo(), gameInfo.getProNames());
                kyokuLog.setKyokuBrief(kyokuBrief);
            }

            if (cmd.equals("kyokuend")) {
                if (kyokuLog.getKyokuEndResult().equals("流局")) {
                    int[] kyokuEndPointInfo = Arrays.stream(kyokuLog.getKyokuEndPointInfo()).sorted().toArray();
                    if(kyokuEndPointInfo[0] == 0 && kyokuEndPointInfo[1] == 0 &&
                            kyokuEndPointInfo[2] == 0 && kyokuEndPointInfo[3] == 0) {

                    } else if (kyokuEndPointInfo[0] == -3000 && kyokuEndPointInfo[1] == 1000 &&
                            kyokuEndPointInfo[2] == 1000 && kyokuEndPointInfo[3] == 1000) {

                    } else if (kyokuEndPointInfo[0] == -1500 && kyokuEndPointInfo[1] == -1500 &&
                            kyokuEndPointInfo[2] == 1500 && kyokuEndPointInfo[3] == 1500) {

                    } else if (kyokuEndPointInfo[0] == -1000 && kyokuEndPointInfo[1] == -1000 &&
                            kyokuEndPointInfo[2] == -1000 && kyokuEndPointInfo[3] == 3000) {

                    } else {
                        throw new Exception(gameInfo.getFileName());
                    }
                }
                List<Object> objectList = kyokuLog.convertToObjectList();
                log.add(objectList);
                openStatus = "";
                lastSutehaiProIndex = -1;
                ponRecordList.clear();
                tenPaiProNumInfo[0] = false;
                tenPaiProNumInfo[1] = false;
                tenPaiProNumInfo[2] = false;
                tenPaiProNumInfo[3] = false;
                kyokuBriefList.add(kyokuLog.getKyokuBrief());

                for (int i=0; i<kyokuLog.getHaipaiInfo().length; i++) {
                    List<String> haipaiInfo = Arrays.stream(kyokuLog.getHaipaiInfo()[i]).mapToObj(
                            haipai -> PAI_NAME_MAPPING.inverse().get(haipai)).collect(Collectors.toList());
                    List<Integer> hais = Majong.convertHais(haipaiInfo);
                    int minShanten = Math.min(Majong.kokushiCheck(Majong.HAIPAI, hais),
                            Math.min(Majong.chitoiCheck(Majong.HAIPAI, hais), Majong.mentshCheck(Majong.HAIPAI, hais)));
                    int akaCount = Long.valueOf(haipaiInfo.stream().filter(haipai ->
                            haipai.contains("M") || haipai.contains("P") || haipai.contains("S")).count()).intValue();
                    final int doraIndicator = kyokuLog.getDoraInfo().get(0);
                    int doraCount = Long.valueOf(haipaiInfo.stream().filter(haipai ->
                            DORA_INDICATOR_MAPPING.get(PAI_NAME_MAPPING.get(haipai).intValue()) == doraIndicator).count()).intValue();
                    PRO_INFO.get(gameInfo.getProNames()[i]).addKyokuCount();
                    PRO_INFO.get(gameInfo.getProNames()[i]).addHaipaiShanten(minShanten);
                    PRO_INFO.get(gameInfo.getProNames()[i]).addHaipaiDoraCount(akaCount + doraCount);
//                    System.out.println("doraIndicator:" + PAI_NAME_MAPPING.inverse().get(doraIndicator));
//                    System.out.println(String.join("", haipaiInfo));
//                    System.out.println("shanten:" + minShanten);
//                    System.out.println("doraCount:" + (akaCount + doraCount));
//                    System.out.println("++++++");
                }
                // System.out.println("++++++++");
                // System.out.println(JSONObject.toJSONString(log));
            }
        }

        return tenhouPaifuBuilder.build();
    }

    /**
     * 解析每位选手初战到吃一的累计时间
     */
    public static void fun19() throws Exception {

        String[] proNamesInOrder = {
                "勝又健志", "和久津晶", "渋川難波", "小林剛",
                "藤崎智", "佐々木寿人", "高宮まり", "二階堂亜樹",
                "朝倉康心", "萩原聖人", "茅森早香", "仲林圭",
                "岡田紗佳", "鈴木優", "内川幸太郎", "日向藍子",
                "松ヶ瀬隆弥", "黒沢咲", "伊達朱里紗", "堀慎吾",
                "二階堂瑠美", "魚谷侑未", "前原雄大", "東城りお",
                "沢崎誠", "滝沢和典", "松本吉弘", "瀬戸熊直樹",
                "鈴木たろう", "白鳥翔", "園田賢", "丸山奏子",
                "本田朋広", "近藤誠一", "多井隆晴", "瑞原明奈",
                "村上淳", "石橋伸洋"
        };

        Map<String, OfficialGameInfo> proName_firstGameDate = new LinkedHashMap<>();
        Map<String, OfficialGameInfo> proName_firstRank0Date = new LinkedHashMap<>();
        Map<String, Integer> proName_interval = new LinkedHashMap<>();
        Map<String, Integer> proName_gameNum = new LinkedHashMap<>();

        for (OfficialGameInfo gameInfo: OFFICIAL_GAME_INFO_LIST) {
            String[] proNames = gameInfo.getProNames();
            int[] proRanks = gameInfo.getProRanks();
            for (int i=0; i<proNames.length; i++) {
                if (!proName_firstGameDate.containsKey(proNames[i])) {
                    proName_firstGameDate.put(proNames[i], gameInfo);
                    proName_interval.put(proNames[i], 0);
                    proName_gameNum.put(proNames[i], 0);
                }
                if (!proName_firstRank0Date.containsKey(proNames[i])) {
                    Integer oldInterval = proName_interval.get(proNames[i]);
                    proName_interval.put(proNames[i], oldInterval + gameInfo.getInterval());
                    Integer oldGameNum = proName_gameNum.get(proNames[i]);
                    proName_gameNum.put(proNames[i], oldGameNum + 1);
                    if (proRanks[i] == 0) {
                        proName_firstRank0Date.put(proNames[i], gameInfo);
                    }
                }
            }
        }

        String pattern19_1 = "%s\t%s\t%s\t%d\t%d";
        Arrays.stream(proNamesInOrder).forEach(proName -> {
            System.out.println(
                    String.format(pattern19_1,
                            proName,
                            proName_firstGameDate.get(proName).getGameName(),
                            proName_firstRank0Date.get(proName).getGameName(),
                            proName_interval.get(proName),
                            proName_gameNum.get(proName)
                    )
            );
        });

        String pattern19_2 = "%s\n%s\n%s\n%d个\n%d分钟";
        Arrays.stream(proNamesInOrder).forEach(proName -> {
            System.out.println(
                    String.format(pattern19_2,
                            proName,
                            proName_firstGameDate.get(proName).getGameName(),
                            proName_firstRank0Date.get(proName).getGameName(),
                            proName_gameNum.get(proName),
                            proName_interval.get(proName)
                    )
            );
        });
    }

    /**
     * @throws Exception
     * 生成官网的牌谱URL
     */
    public static void generatePaifuUrl() throws Exception {

        PrintWriter pw = new PrintWriter(new File(PATH + "/Documents/Mleague数据/official_urls.txt"));

        for (int i = 0; i < SEASONS.length; i++) {
            String season = SEASONS[i];
            int gameDays = GAMEDAYS[i];
            for (int j = 1; j <= gameDays; j++) {
                String gameDayString = j < 10 ? ("0" + j) : ("" + j);
                pw.println(String.format(season, gameDayString, 1));
                pw.println(String.format(season, gameDayString, 2));
                if (i == 1) { //2018赛季决赛出现过一天打三个半庄
                    pw.println(String.format(season, gameDayString, 3));
                }
            }
            pw.flush();
        }

        pw.close();
    }

    private static void parsePaifu(OfficialGameInfo.OfficialGameInfoBuilder builder, String line) {
        int statIndex = line.indexOf(PAIFU_START_SIGNAL);
        int endIndex = line.indexOf(PAIFU_END_SIGHAL, statIndex);
        String paifuString = line.substring(statIndex + PAIFU_START_SIGNAL.length(), endIndex);
        List<OfficialPaifuLog> officialPaifuLogList = JSONArray.parseArray(paifuString, OfficialPaifuLog.class);
        String[] proNames = new String[4];
        String[] proPoints = new String[4];
        int[] proRanks = new int[4];

        for (OfficialPaifuLog officialPaifuLog : officialPaifuLogList) {
            String cmd = officialPaifuLog.getCmd();
            String[] args = officialPaifuLog.getArgs();

            if (cmd.equals("player")) {
                assert args.length == 4;
                // 有些牌谱的选手姓和名中间有空格，有些牌谱没有；这里统一去掉空格
                proNames[args[0].charAt(0) - 65] = args[1].replaceAll(" ", "");
            }

            if (cmd.equals("gameend")) {
                // assert args.length == 12;
                // args = ["D0", "63.5", "B0", "6.3", "C0", "-21.5", "A0", "-48.3", "D0_rank=0", "B0_rank=1", "C0_rank=2", "A0_rank=3"]
                proPoints[args[0].charAt(0) - 65] = args[1];
                proPoints[args[2].charAt(0) - 65] = args[3];
                proPoints[args[4].charAt(0) - 65] = args[5];
                proPoints[args[6].charAt(0) - 65] = args[7];
                if (args.length == 12) {
                    proRanks[args[8].charAt(0) - 65] = args[8].charAt(8) - 48;
                    proRanks[args[9].charAt(0) - 65] = args[9].charAt(8) - 48;
                    proRanks[args[10].charAt(0) - 65] = args[10].charAt(8) - 48;
                    proRanks[args[11].charAt(0) - 65] = args[11].charAt(8) - 48;
                    validatePointAndRank(proPoints, proRanks);
                }
            }
        }
        builder.officialPaifuLogList(officialPaifuLogList);
        builder.proNames(proNames);
        builder.proPoints(proPoints);
        builder.proRanks(proRanks);
    }

    private static void validatePointAndRank(String[] proPoints, int[] proRanks) {
        // TODO 校验排名
    }

    private static void parseTime(OfficialGameInfo.OfficialGameInfoBuilder builder, String line) {
        int startIndex = line.indexOf(TIME_START_SIGNAL);
        int endIndex = line.indexOf(TIME_END_SIGNAL, startIndex);
        String timeString = line.substring(startIndex + TIME_START_SIGNAL.length(), endIndex);
        assert timeString.length() == TIME_STRING_DEMO.length();
        String gameDate = timeString.substring(0, 10);
        String gameWeekDay = timeString.substring(12, 13);
        String startTimeString = timeString.substring(15, 20);
        String endTimeString = timeString.substring(23, 28);
        int interval = calculateInterval(startTimeString, endTimeString);

        builder.gameDate(gameDate);
        builder.gameWeekDay(gameWeekDay);
        builder.startTime(startTimeString);
        builder.endTime(endTimeString);
        builder.interval(interval);
    }

    private static int calculateInterval(String startTimeString, String endTimeString) {

        assert startTimeString.length() == 5;
        assert endTimeString.length() == 5;

        int startHour = Integer.parseInt(startTimeString.substring(0, 2));
        int startMinute = Integer.parseInt(startTimeString.substring(3, 5));
        int endHour = Integer.parseInt(endTimeString.substring(0, 2));
        int endMinute = Integer.parseInt(endTimeString.substring(3, 5));

        if (endHour < startHour) {
            endHour = endHour + 24;
        }

        return (endHour - startHour) * 60 + (endMinute - startMinute);
    }

    private static int calculateKyokuIndex(String cf, String zf) { // 根据场风和自风计算当前局情况
        int zfIndex = (5 - Integer.valueOf(zf.substring(0, 1))) % 4;

        if (cf.equals("2z")) {
            zfIndex += 4;
        }
        return zfIndex;
    }

    private static int calculateProIndexDiff(int proIndex, int lastSutehaiProIndex) {
        return proIndex > lastSutehaiProIndex ?
                (proIndex - lastSutehaiProIndex) :
                (4 + proIndex - lastSutehaiProIndex);
    }

    public static String generatePointInfo(String officialPointInfo, boolean isRon, boolean isQin, int fu, int totalFan) {
        String tenhouPointInfo;
        if (isRon) {
            if (officialPointInfo.equals("満貫")) {
                tenhouPointInfo = isQin ? "満貫12000点" : "満貫8000点";
            } else if (officialPointInfo.equals("跳満")) {
                tenhouPointInfo = isQin ? "跳満18000点" : "跳満12000点";
            } else if (officialPointInfo.equals("倍満")) {
                tenhouPointInfo = isQin ? "倍満24000点" : "倍満16000点";
            } else if (officialPointInfo.equals("三倍満")) {
                tenhouPointInfo = isQin ? "三倍満36000点" : "三倍満24000点";
            } else if (officialPointInfo.equals("役満")) {
                tenhouPointInfo = isQin ? "役満48000点" : "役満32000点";
            } else {
                tenhouPointInfo = String.format(
                        FAN_RON_PATTERN, fu, totalFan, Integer.valueOf(officialPointInfo));
            }
        } else {
            if (officialPointInfo.equals("満貫")) {
                tenhouPointInfo = isQin ? "満貫4000点∀" : "満貫2000-4000点";
            } else if (officialPointInfo.equals("跳満")) {
                tenhouPointInfo = isQin ? "跳満6000点∀" : "跳満3000-6000点";
            } else if (officialPointInfo.equals("倍満")) {
                tenhouPointInfo = isQin ? "倍満8000点∀" : "倍満4000-8000点";
            } else if (officialPointInfo.equals("三倍満")) {
                tenhouPointInfo = isQin ? "三倍満12000点∀" : "三倍満6000-12000点";
            } else if (officialPointInfo.equals("役満")) {
                tenhouPointInfo = isQin ? "役満16000点∀" : "役満8000-16000点";
            } else {
                if (isQin) {
                    int allPoint = (int) (Math.ceil(Double.valueOf(officialPointInfo) / 300) * 100);
                    tenhouPointInfo = String.format(
                            FAN_TSUMO_QIN_PATTERN, fu, totalFan, allPoint);
                } else {
                    int ziPoint = (int) (Math.ceil(Double.valueOf(officialPointInfo) / 400) * 100);
                    int qinPoint = (int) (Math.ceil(Double.valueOf(officialPointInfo) / 200) * 100);
                    tenhouPointInfo = String.format(
                            FAN_TSUMO_ZI_PATTERN, fu, totalFan, ziPoint, qinPoint);
                }
            }
        }
        return tenhouPointInfo;
    }

    private static String generateKyokuBriefIfAgari(String officialPointInfo, boolean isRon, boolean isQin,
                                                    int[] kyokuStartInfo, String[] proNames,
                                                    int proIndex, int lastSutehaiProIndex) {
        int point;
        if (officialPointInfo.equals("満貫")) {
            point = isQin ? 12000 : 8000;
        } else if (officialPointInfo.equals("跳満")) {
            point = isQin ? 18000 : 12000;
        } else if (officialPointInfo.equals("倍満")) {
            point = isQin ? 24000 : 16000;
        } else if (officialPointInfo.equals("三倍満")) {
            point = isQin ? 36000 : 24000;
        } else if (officialPointInfo.equals("役満")) {
            point = isQin ? 48000 : 32000;
        } else {
            point = Integer.valueOf(officialPointInfo);
        }
        if (isRon) {
            String kyokuBriefRonPattern = "/** %s%d局%d本场 供托%d点 %s %d ← %s */";
            String kyokuBrief = String.format(kyokuBriefRonPattern,
                    kyokuStartInfo[0] >= 4 ? "南" : "東",
                    kyokuStartInfo[0]%4 + 1,
                    kyokuStartInfo[1],
                    kyokuStartInfo[2] * 1000,
                    PRO_INFO.get(proNames[proIndex]).getProNameBrief(),
                    point,
                    PRO_INFO.get(proNames[lastSutehaiProIndex]).getProNameBrief());
            return kyokuBrief;
        } else if (isQin) {
            String kyokuBriefTsumoPattern = "/** %s%d局%d本场 供托%d点 %s %dall */";
            String kyokuBrief = String.format(kyokuBriefTsumoPattern,
                    kyokuStartInfo[0] >= 4 ? "南" : "東",
                    kyokuStartInfo[0] % 4 + 1,
                    kyokuStartInfo[1],
                    kyokuStartInfo[2] * 1000,
                    PRO_INFO.get(proNames[proIndex]).getProNameBrief(),
                    (int) (Math.ceil(Double.valueOf(point) / 300) * 100));
            return kyokuBrief;
        } else {
            String kyokuBriefTsumoPattern = "/** %s%d局%d本场 供托%d点 %s %d/%d */";
            String kyokuBrief = String.format(kyokuBriefTsumoPattern,
                    kyokuStartInfo[0] >= 4 ? "南" : "東",
                    kyokuStartInfo[0] % 4 + 1,
                    kyokuStartInfo[1],
                    kyokuStartInfo[2] * 1000,
                    PRO_INFO.get(proNames[proIndex]).getProNameBrief(),
                    (int) (Math.ceil(Double.valueOf(point) / 400) * 100),
                    (int) (Math.ceil(Double.valueOf(point) / 200) * 100));
            return kyokuBrief;
        }
    }

    private static String generateKyokuBriefIfRyukyoku(
            boolean[] tenPaiProNumInfo, int[] kyokuStartInfo, String[] proNames) {
        String kyokuBriefRyukyokuPattern = "/** %s%d局%d本场 供托%d点 （流局）%s */";
        int[] ryukyokuPoint = {3000, 1500, 1000};
        int tenPaiProNum = (tenPaiProNumInfo[0] ? 1 : 0) + (tenPaiProNumInfo[1] ? 1 : 0) +
                (tenPaiProNumInfo[2] ? 1 : 0) + (tenPaiProNumInfo[3] ? 1 : 0);
        if (tenPaiProNum == 0) {
            String kyokuBrief = String.format(kyokuBriefRyukyokuPattern,
                    kyokuStartInfo[0] >= 4 ? "南" : "東",
                    kyokuStartInfo[0] % 4 + 1,
                    kyokuStartInfo[1],
                    kyokuStartInfo[2] * 1000,
                    "全員不聴");
            return kyokuBrief;
        } else if (tenPaiProNum == 4) {
            String kyokuBrief = String.format(kyokuBriefRyukyokuPattern,
                    kyokuStartInfo[0] >= 4 ? "南" : "東",
                    kyokuStartInfo[0] % 4 + 1,
                    kyokuStartInfo[1],
                    kyokuStartInfo[2] * 1000,
                    "全員聴牌");
            return kyokuBrief;
        } else {
            List<String> tenPaiProNameList = new ArrayList<>();
            for (int i = 0; i < tenPaiProNumInfo.length; i++) {
                if (tenPaiProNumInfo[i]) {
                    tenPaiProNameList.add(PRO_INFO.get(proNames[i]).getProNameBrief());
                }
            }
            String kyokuBrief = String.format(kyokuBriefRyukyokuPattern,
                    kyokuStartInfo[0] >= 4 ? "南" : "東",
                    kyokuStartInfo[0] % 4 + 1,
                    kyokuStartInfo[1],
                    kyokuStartInfo[2] * 1000,
                    String.join("·", tenPaiProNameList) + " " + ryukyokuPoint[tenPaiProNum - 1]);
            return kyokuBrief;
        }
    }

    private static String convertPonRecordIfPossible(List<String> ponRecordList, int targetPaiName) {
        for (String ponRecord : ponRecordList) {
            int sourcePaiName = Integer.valueOf(ponRecord.substring(5, 7));
            if ((sourcePaiName == targetPaiName) ||
                    (sourcePaiName == 51 && targetPaiName == 15) ||
                    (sourcePaiName == 52 && targetPaiName == 25) ||
                    (sourcePaiName == 53 && targetPaiName == 35) ||
                    (sourcePaiName == 15 && targetPaiName == 51) ||
                    (sourcePaiName == 25 && targetPaiName == 52) ||
                    (sourcePaiName == 35 && targetPaiName == 53)) {
                String kanRecord = ponRecord.replaceAll("p", "k") + targetPaiName;
                return kanRecord;
            }
        }
        return null;
    }

    /**
     * 牌谱名称的迭代器
     */
    static class GameFileIterator implements Iterable<OfficialGameInfo.OfficialGameInfoBuilder> {

        @Override
        public Iterator<OfficialGameInfo.OfficialGameInfoBuilder> iterator() {
            return new Iterator<>() {

                private int cur = -1;

                @Override
                public boolean hasNext() {
                    return cur < Arrays.stream(GAMEDAYS).sum() - 1;
                }

                @Override
                public OfficialGameInfo.OfficialGameInfoBuilder next() {
                    cur++;
                    int tempCur = cur;
                    for (int i = 0; i < GAMEDAYS.length; i++) {
                        if (tempCur > GAMEDAYS[i] - 1) {
                            tempCur = tempCur - GAMEDAYS[i];
                        } else {
                            String season = SEASONS[i];
                            int gameDay;
                            int index;
                            if (i == 1) {
                                gameDay = tempCur / 3 + 1;
                                index = tempCur % 3 + 1;
                            } else {
                                gameDay = tempCur / 2 + 1;
                                index = tempCur % 2 + 1;
                            }
                            String gameDayString = gameDay < 10 ? ("0" + gameDay) : ("" + gameDay);
                            OfficialGameInfo.OfficialGameInfoBuilder officialGameInfoBuilder = OfficialGameInfo.builder();
                            officialGameInfoBuilder.fileName(String.format(season, gameDayString, index));
                            officialGameInfoBuilder.season(SEASON_NAMES[i]);
                            officialGameInfoBuilder.seasonIndex(i);
                            return officialGameInfoBuilder;
                        }
                    }
                    return null; // should not reach here
                }
            };
        }
    }
}

