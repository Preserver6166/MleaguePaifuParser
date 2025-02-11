package official;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import mahjongutils.models.Tile;
import mahjongutils.shanten.*;
import org.apache.commons.lang3.StringUtils;
import tenhou.KyokuLog;
import tenhou.TenhouPaifu;
import tenhou.TenhouRule;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static official.Constants.*;
import static official.PaifuAnalyzer.*;
import static official.ProInfo.PRO_INFO;
import static official.ProInfo.getProNameByBrief;

public class App {

    public static final List<OfficialGameInfo> OFFICIAL_GAME_INFO_LIST = new ArrayList<>();
    public static final Map<String, TenhouPaifu> TENHOU_PAIFU_MAP = new LinkedHashMap<>();

    static {
        try {
            Iterator<OfficialGameInfo.OfficialGameInfoBuilder> iterator = new OfficialGameFileIterator().iterator();
            while(iterator.hasNext()) {
                OfficialGameInfo.OfficialGameInfoBuilder officialGameInfoBuilder = iterator.next();
                String fileName = officialGameInfoBuilder.build().getFileName();
//                if (!fileName.equals("L001_S016_0039_02A")) {
//                    continue;
//                }
                if (fileName.compareTo("L001_S019_0081_01A") < 0) {
                    continue;
                }
                if (fileName.compareTo("L001_S019_0081_02A") > 0) {
                    continue;
                }
//                if (fileName.compareTo("L001_S001_0001_01A") < 0) {
//                    continue;
//                }
//                if (fileName.compareTo("L001_S019_0025_02A") > 0) {
//                    continue;
//                }
                assert fileName.length() == 18;
                String fileNamePrefix;
//                if ((fileName.contains("S013") || fileName.contains("S014") || fileName.contains("S015"))) {
//                    fileNamePrefix = FILENAME_PREFIX_22_23;
//                } else if (fileName.contains("S016")) {
//                    fileNamePrefix = FILENAME_PREFIX_23_24;
//                } else {
//                    fileNamePrefix = FILENAME_PREFIX_18_22;
//                }
                fileNamePrefix = FILENAME_PREFIX_V2;
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
                    try {
                        validateOfficialGameInfo(gameInfo);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    OFFICIAL_GAME_INFO_LIST.add(gameInfo);
                    try {
                        TenhouPaifu tenhouPaifu = o2t(gameInfo);
                        validateTenhouPaifu(tenhouPaifu);
//                        if (tenhouPaifu.getLog().size() == 8) {
//                            System.out.println("+++++" + fileName);
//                        }
                        TENHOU_PAIFU_MAP.put(fileName, tenhouPaifu);
                        // TODO 所有对PRO_INFO进行的统计操作后续整合到一起去
                        int kyokuCount = tenhouPaifu.getLog().size();
                        Arrays.stream(gameInfo.getProNames()).forEach(proName -> {
                            PRO_INFO.get(proName).addGameCount();
                            PRO_INFO.get(proName).addKyokuCount(kyokuCount);
                        });
                        tenhouPaifu.getLog().forEach(kyokuLog -> {
                            if (kyokuLog.getKyokuEndResult().equals("和了")) {
                                int agariProIndex = (Integer) kyokuLog.getKyokuEndDetail().get(0);
                                boolean isRichi = kyokuLog.getSutehaiInfo()[agariProIndex].stream().
                                        filter(obj -> obj instanceof String).
                                        map(obj -> (String) obj).
                                        anyMatch(obj -> obj.startsWith("r"));
                                if (isRichi) {
                                    // 判断有没有里
                                }
                            }
                        });
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
    }

    public static void main (String[] args) {
//        fun33();
        fun1();
    }

    /**
     2019/10/17 第1試合 佐々木寿人 vs 小林剛 vs 鈴木たろう vs 黒沢咲
     黒沢咲 +57.4pt; 小林剛 +16.9pt; 佐々木寿人 -25.3pt; 鈴木たろう -49.0pt
     */
    private static void printGame(String fileName, OfficialGameInfo gameInfo, TenhouPaifu tenhouPaifu) {
        System.out.println("++++++++" + fileName + "++++++++");
        System.out.println(gameInfo.getGameBrief());
        System.out.println(gameInfo.getGameResult());
        System.out.println();
        System.out.println("/** NAGA牌谱及分析详情：");
        System.out.println("https://naga.dmv.nico/htmls/053227e0e24c174441bd302c0f0f1680063eadbc24d0888148191b2f1673b517v2_2_2.html?tw=0");
        System.out.println("*/");
        System.out.println(String.format(NAGA_PATTERN_NEW,
                PRO_INFO.get(gameInfo.getProNames()[0]).getProNameBrief()));
        System.out.println(String.format(NAGA_PATTERN_NEW,
                PRO_INFO.get(gameInfo.getProNames()[1]).getProNameBrief()));
        System.out.println(String.format(NAGA_PATTERN_NEW,
                PRO_INFO.get(gameInfo.getProNames()[2]).getProNameBrief()));
        System.out.println(String.format(NAGA_PATTERN_NEW,
                PRO_INFO.get(gameInfo.getProNames()[3]).getProNameBrief()));
        System.out.println();
        System.out.println("/** 官方牌谱：");
        System.out.println("https://viewer.ml-log.jp/web/viewer?gameid=" + fileName);
        System.out.println("*/\n");
        System.out.println("/** 天凤牌谱：（未经双重校验）");
        System.out.println("https://tenhou.net/5/#json=" + JSONObject.toJSONString(tenhouPaifu));
        System.out.println("*/\n");
        for (String kyokuBrief: tenhouPaifu.getKyokuBrief()) {
            System.out.println(kyokuBrief);
        }
        System.out.println();

    }

    /*
     * 用来打印比赛信息的函数
     */
    private static void fun1() {
        if (OFFICIAL_GAME_INFO_LIST.size() > 30) {
            OFFICIAL_GAME_INFO_LIST.forEach(officialGameInfo -> {
                System.out.println(officialGameInfo.getFileName());
            });
            return;
        }
        OFFICIAL_GAME_INFO_LIST.forEach(officialGameInfo -> {
            printGame(officialGameInfo.getFileName(), officialGameInfo,
                    TENHOU_PAIFU_MAP.get(officialGameInfo.getFileName()));
        });
        System.out.println("GAME_INFOS size:" + OFFICIAL_GAME_INFO_LIST.size());
    }

    /*
     * 用来打印比赛时间和比赛ID等简略信息
     */
    private static void fun2() {
        for (int i=OFFICIAL_GAME_INFO_LIST.size()-1; i>=0; i--) {
            OfficialGameInfo officialGameInfo = OFFICIAL_GAME_INFO_LIST.get(i);
            System.out.println(officialGameInfo.getGameDate() + " #" +
                    officialGameInfo.getDayIndex() + "\t" +
                    officialGameInfo.getFileName());
        }
    }

    /**
     * 把官方牌谱转换成天凤再生牌谱
     {
        "title":["Mリーグ2019　ファイナルシリーズ 6/23(火)","第2試合"],
        "name":["沢崎-228.2","多井+127.9","魚谷+175.5","小林+183.5"],
        "rule":{
            "disp":"Mリーグ2019　FINAL 12/12　6/23 第2試合",
            "aka":1
        },
        "log":[
            [
                [1,0,0],[25000,28900,21100,25000], // 1代表東2局, 0代表0本场, 0代表供托
                [24],[31], // dora指示牌, ura指示牌
                [11,12,15,19,24,29,32,53,41,42,43,44,47], // 配牌
                [12,46,43,25,37,35,22,33,41,18,32,17,14,25,38], // 摸牌
                [32,15,12,12,11,19,41,60,43,35,29,32,43,47,42], // 出牌
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

        tenhouPaifuBuilder.fileName(gameInfo.getFileName());

        String[] title = new String[2];
        title[0] = gameInfo.getSeason();
        title[1] = gameInfo.getGameName();
        tenhouPaifuBuilder.title(title);

        tenhouPaifuBuilder.name(gameInfo.getProNames());

        TenhouRule tenhouRule = new TenhouRule();
        tenhouRule.setDisp(gameInfo.getGameName()); // 似乎没有必要填这个参数
        tenhouRule.setAka(1);
        tenhouPaifuBuilder.rule(tenhouRule);

        List<KyokuLog> log = new ArrayList<>();
        tenhouPaifuBuilder.log(log);

        List<String> kyokuBriefList = new ArrayList<>();
        tenhouPaifuBuilder.kyokuBrief(kyokuBriefList);

        KyokuLog kyokuLog = null;
        KyokuLog lastKyokuLog = null;

        List<OfficialPaifuLog> officialPaifuLogList = gameInfo.getOfficialPaifuLogList();
        Iterator<OfficialPaifuLog> iterator = officialPaifuLogList.iterator();
        String openStatus = "";
        int lastSutehaiProIndex = -1;
        List<String> ponRecordList = new ArrayList<>(); // 处理
        boolean[] tenpaiProNumInfo = new boolean[4];

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
                lastKyokuLog = kyokuLog;
                kyokuLog = new KyokuLog();
//                if (lastKyokuLog != null) {
//                    for (int i = 0; i <= 3; i++) {
//                        kyokuLog.setKyokuStartPointInfo(i,
//                                lastKyokuLog.getKyokuStartPointInfo()[i] + lastKyokuLog.getKyokuEndPointInfo()[i]);
//                    }
//                }
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
//                    if (pointValue != kyokuLog.getKyokuStartPointInfo()[proIndex]) {
//                        System.out.println("Expected: " + pointValue +
//                                ", but got: " + kyokuLog.getKyokuStartPointInfo()[proIndex]);
//                    }
                    kyokuLog.setKyokuStartPointInfo(proIndex, pointValue);
                } else if (args[1].startsWith("+") || args[1].startsWith("-")) {
                    int pointValue = Integer.valueOf(args[1]);
                    kyokuLog.appendKyokuEndPointInfo(proIndex, pointValue);
                } else {
                    // should not go into here
                }

            }

            if (cmd.equals("dora")) {
                // [6p, 5p] dora和dora指示牌
                assert args.length == 2;
                // 天凤牌谱中只需要保存dora指示牌
                // 官方牌谱常见错误1: 漏记dora
                if (PAI_NAME_MAPPING.get(args[1]) == null) {
                    String kyokuStartInfo = kyokuLog.getKyokuStartInfoInStringFormat();
                    System.out.println(gameInfo.getFileName() + " " + kyokuStartInfo + " 漏记dora" +
                            " ID：" + officialPaifuLog.getId());
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
//                    kyokuLog.initProHandRecords(proIndex, args[1]);
                }
            }

            if (cmd.equals("tsumo")) {
                assert args.length == 3;
                // [A0, 69, 5s] 选手, 剩余牌数, 摸到的牌
                int proIndex = args[0].charAt(0) - 65;
                // 官方牌谱常见错误2: 漏记摸牌
                if (PAI_NAME_MAPPING.get(args[2]) == null) {
                    String kyokuStartInfo = kyokuLog.getKyokuStartInfoInStringFormat();
                    if (args[2].equals("少牌")) { // 已标记的少牌
                        System.out.println(gameInfo.getFileName() + " " + kyokuStartInfo + " " +
                                PRO_INFO.get(gameInfo.getProNames()[proIndex]).getProNameBrief() + "少牌" +
                                " ID：" + officialPaifuLog.getId());
                    } else if (args[2].equals("导播")) { // 已标记的导播原因漏记摸牌
                        System.out.println(gameInfo.getFileName() + " " + kyokuStartInfo + " 漏记" +
                                PRO_INFO.get(gameInfo.getProNames()[proIndex]).getProNameBrief() + "摸牌（导播原因）" +
                                " ID：" + officialPaifuLog.getId());
                    } else {
                        System.out.println(gameInfo.getFileName() + " " + kyokuStartInfo + " 漏记" +
                                PRO_INFO.get(gameInfo.getProNames()[proIndex]).getProNameBrief() + "摸牌" +
                                " ID：" + officialPaifuLog.getId());
                    }
                    kyokuLog.appendTsumoInfo(proIndex, 1); // 追记一张不存在的牌
                } else {
                    int paiName = PAI_NAME_MAPPING.get(args[2]);
                    kyokuLog.appendTsumoInfo(proIndex, paiName);
                }
            }

            if (cmd.equals("sutehai")) {
                assert args.length == 2 || args.length == 3 || args.length == 4;
                int proIndex = args[0].charAt(0) - 65;
                lastSutehaiProIndex = proIndex;
                // 官方牌谱常见错误3: 漏记出牌
                if (PAI_NAME_MAPPING.get(args[1]) == null) {
                    String kyokuStartInfo = kyokuLog.getKyokuStartInfoInStringFormat();
                    if (args[1].equals("多牌")) { // 已标记的多牌
                        System.out.println(gameInfo.getFileName() + " " + kyokuStartInfo + " " +
                                PRO_INFO.get(gameInfo.getProNames()[proIndex]).getProNameBrief() + "多牌" +
                                " ID：" + officialPaifuLog.getId());
                    } else {
                        System.out.println(gameInfo.getFileName() + " " + kyokuStartInfo + " 漏记" +
                                PRO_INFO.get(gameInfo.getProNames()[proIndex]).getProNameBrief() + "出牌" +
                                " ID：" + officialPaifuLog.getId());
                    }
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
                    tenpaiProNumInfo[proIndex] = true;
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
                // TODO 此处要加立直数组
            }

            if (cmd.equals("open")) {
                int proIndex = args[0].charAt(0) - 65;
                if (openStatus.equals("c")) { // 只能从上家吃,所以不需要判断lastSutehaiProIndex
                    // ["A0", "<4p5p>", "6p"]
                    int targetPaiName = PAI_NAME_MAPPING.get(args[2]);
                    StringBuilder sb = new StringBuilder(openStatus);
                    sb.append(targetPaiName);
                    int sourcePaiName1 = PAI_NAME_MAPPING.get(args[1].substring(1, 3));
                    sb.append(sourcePaiName1);
                    int sourcePaiName2 = PAI_NAME_MAPPING.get(args[1].substring(3, 5));
                    sb.append(sourcePaiName2);
                    kyokuLog.appendTsumoInfo(proIndex, sb.toString());
                } else if (openStatus.equals("p")) {
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
                        if (sourcePaiName1 == 15 || sourcePaiName1 == 51) {
                            sb.append("151515a51");
                        } else if (sourcePaiName1 == 25 || sourcePaiName1 == 52) {
                            sb.append("252525a52");
                        } else if (sourcePaiName1 == 35 || sourcePaiName1 == 53) {
                            sb.append("353535a53");
                        } else {
                            sb.append(sourcePaiName1);
                            sb.append(sourcePaiName2);
                            sb.append(sourcePaiName3);
                            sb.append("a");
                            sb.append(sourcePaiName4);
                        }
                        kyokuLog.appendSutehaiInfo(proIndex, sb.toString());
                    } else {
                        int targetPaiName = PAI_NAME_MAPPING.get(args[2]);
                        String kanRecord = convertPonRecordIfPossible(ponRecordList, targetPaiName);
                        if (kanRecord != null) { // 加杠
                            kyokuLog.appendSutehaiInfo(proIndex, kanRecord);
                            lastSutehaiProIndex = proIndex;
                        } else { // 明杠之后, 要在sutehai里加个0
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
                    // 判断uradora是否记录正确的逻辑后移到validate函数
                } else if (args[1].equals("导播")) {
                    // 官方牌谱常见错误3: 导播原因漏记ura
                    // TODO 这段逻辑后续应当整合成统一的报错行为
//                    String kyokuStartInfo = kyokuLog.getKyokuStartInfoInStringFormat();
//                    System.out.println(gameInfo.getFileName() + " " + kyokuStartInfo + " 导播原因漏记ura" +
//                            " ID：" + officialPaifuLog.getId());
                } else {
                    int paiName = PAI_NAME_MAPPING.get(args[1]);
                    kyokuLog.getUraInfo().add(paiName);
                }
            }

            if (cmd.equals("agari")) {
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
                kyokuLog.appendKyokuEndDetail(isRon ? lastSutehaiProIndex : proIndex); // 放铳家或自摸家
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
                int tenPaiProNum = (tenpaiProNumInfo[0] ? 1 : 0) + (tenpaiProNumInfo[1] ? 1 : 0) +
                        (tenpaiProNumInfo[2] ? 1 : 0) + (tenpaiProNumInfo[3] ? 1 : 0);
                if (tenPaiProNum == 0) {
                    kyokuLog.setKyokuEndResult("全員不聴");
                } else if (tenPaiProNum == 4) {
                    kyokuLog.setKyokuEndResult("全員聴牌");
                } else {
                    kyokuLog.setKyokuEndResult("流局");
                }
                String kyokuBrief = generateKyokuBriefIfRyukyoku(tenpaiProNumInfo,
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
                        String kyokuStartInfo = kyokuLog.getKyokuStartInfoInStringFormat();
                        System.out.println(gameInfo.getFileName() + " " + kyokuStartInfo + " 流局收支不正确");
                    }
                }
                log.add(kyokuLog);
                kyokuBriefList.add(kyokuLog.getKyokuBrief());
                // 清理各种临时变量
                openStatus = "";
                lastSutehaiProIndex = -1;
                ponRecordList.clear();
                tenpaiProNumInfo[0] = false;
                tenpaiProNumInfo[1] = false;
                tenpaiProNumInfo[2] = false;
                tenpaiProNumInfo[3] = false;
                // System.out.println(JSONObject.toJSONString(log));
            }
        }

        return tenhouPaifuBuilder.build();
    }

    private static void parsePaifu(OfficialGameInfo.OfficialGameInfoBuilder builder, String line) {
        int statIndex = line.indexOf(PAIFU_START_SIGNAL);
        int endIndex = line.indexOf(PAIFU_END_SIGHAL, statIndex);
        String paifuString = line.substring(statIndex + PAIFU_START_SIGNAL.length(), endIndex);
        List<OfficialPaifuLog> officialPaifuLogList = JSONArray.parseArray(paifuString, OfficialPaifuLog.class);
        String[] proNames = new String[4];
        BigDecimal[] proPoints = new BigDecimal[4];
        int[] proRanks = new int[4];

        for (OfficialPaifuLog officialPaifuLog : officialPaifuLogList) {
            String cmd = officialPaifuLog.getCmd();
            String[] args = officialPaifuLog.getArgs();

            if (cmd.equals("player")) {
                assert args.length == 4;
                // 有些牌谱的选手姓和名中间有空格, 有些牌谱没有; 这里统一去掉空格
                proNames[args[0].charAt(0) - 65] = args[1].replaceAll(" ", "");
            }

            if (cmd.equals("gameend")) {
                // assert args.length == 12;
                // args = ["D0", "63.5", "B0", "6.3", "C0", "-21.5", "A0", "-48.3", "D0_rank=0", "B0_rank=1", "C0_rank=2", "A0_rank=3"]
                proPoints[args[0].charAt(0) - 65] = new BigDecimal(args[1]);
                proPoints[args[2].charAt(0) - 65] = new BigDecimal(args[3]);
                proPoints[args[4].charAt(0) - 65] = new BigDecimal(args[5]);
                proPoints[args[6].charAt(0) - 65] = new BigDecimal(args[7]);

                /**
                 * 计算rank
                 * 无人同分 0 2 4 6
                 * 两人同分 0 3 3 6/1 1 4 6/0 2 5 5
                 * 三人同分 2 2 2 6/0 4 4 4
                 * 四人同分 3 3 3 3
                 */
                List<Integer> originIndices = IntStream.range(0, proPoints.length).boxed()
                        .sorted((i1, i2) -> proPoints[i2].compareTo(proPoints[i1]))
                        .collect(Collectors.toList());
                Set<BigDecimal> uniqueProPoints = Arrays.stream(proPoints).collect(Collectors.toSet());
                for (int i=0; i<originIndices.size(); i++) {
                    proRanks[originIndices.get(i)] = 2*i;
                }
                if (uniqueProPoints.size() == 1) { // 四人同分
                    for(int i=0; i<proRanks.length; i++) {
                        proRanks[i] = 3;
                    }
                } else if (uniqueProPoints.size() == 2) { // 三人同分
                    if (proPoints[originIndices.get(0)].equals(proPoints[originIndices.get(2)])) {
                        proRanks[originIndices.get(0)] = 2;
                        proRanks[originIndices.get(1)] = 2;
                        proRanks[originIndices.get(2)] = 2;
                    } else if (proPoints[originIndices.get(1)].equals(proPoints[originIndices.get(3)])) {
                        proRanks[originIndices.get(1)] = 4;
                        proRanks[originIndices.get(2)] = 4;
                        proRanks[originIndices.get(3)] = 4;
                    } else {
                        // should not go into here
                    }
                } else if (uniqueProPoints.size() == 3) { // 两人同分
                    if (proPoints[originIndices.get(0)].equals(proPoints[originIndices.get(1)])) {
                        proRanks[originIndices.get(0)] = 1;
                        proRanks[originIndices.get(1)] = 1;
                    } else if (proPoints[originIndices.get(1)].equals(proPoints[originIndices.get(2)])) {
                        proRanks[originIndices.get(1)] = 3;
                        proRanks[originIndices.get(2)] = 3;
                    } else if (proPoints[originIndices.get(2)].equals(proPoints[originIndices.get(3)])) {
                        proRanks[originIndices.get(2)] = 5;
                        proRanks[originIndices.get(3)] = 5;
                    } else {
                        // should not go into here
                    }
                } else { // 无人同分
                    // do nothing
                }
            }
        }
        builder.officialPaifuLogList(officialPaifuLogList);
        builder.proNames(proNames);
        builder.proPoints(proPoints);
        builder.proRanks(proRanks);
    }

    private static void parseTime(OfficialGameInfo.OfficialGameInfoBuilder builder, String line) {
        int startIndex = line.indexOf(TIME_START_SIGNAL);
        int endIndex = line.indexOf(TIME_END_SIGNAL, startIndex);
        String timeString = line.substring(startIndex + TIME_START_SIGNAL.length(), endIndex);
        if (timeString.length() == TIME_STRING_DEMO.length()) {
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
        } else { // 偶有漏记比赛时间的情况出现，警告即可，不应当影响牌谱解析
            System.out.println(builder.build().getFileName() + " 漏记比赛时间");
        }
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

    // 根据场风和自风计算当前局情况
    private static int calculateKyokuIndex(String cf, String zf) {
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

    private static String generatePointInfo(String officialPointInfo, boolean isRon, boolean isQin, int fu, int totalFan) {
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
     * 对kyoku进行错误检查
     * 检查包括:
     * (1) 校验点数是否为10万点
     * (2) 校验新dora
     * (3) 校验uradora
     * (4) 校验立直前后有无除暗杠外的鸣牌行为
     * (5) 校验和牌时役种记录是否正确 TODO 目前仅校验立直
     * (6) 校验摸到或打出的牌是否合法
     * 检查不包括: (牌谱生成阶段即会发现的错误)
     * (1) 漏记dora
     * (2) 漏记摸牌
     * (3) 漏记出牌
     * 检查不包括: (逻辑上无法验证的一些错误)
     * (1) dora和新dora, uradora顺序错误
     * (2) 手切错记为摸切, 摸切错记为手切
     * (3) 摸牌或出牌记错但不影响结果
     */
    private static void validateTenhouPaifu(TenhouPaifu tenhouPaifu) {
        tenhouPaifu.getLog().forEach(kyokuLog -> {
            int[] kyokuStartInfo = kyokuLog.getKyokuStartInfo();
            int[] kyokuStartPointInfo = kyokuLog.getKyokuStartPointInfo();
            List<Integer> doraInfo = kyokuLog.getDoraInfo();
            List<Integer> uraInfo = kyokuLog.getUraInfo();
            int[][] haipaiInfo = kyokuLog.getHaipaiInfo();
            List<Object>[] tsumoInfo = kyokuLog.getTsumoInfo();
            List<Object>[] sutehaiInfo = kyokuLog.getSutehaiInfo();
            String kyokuEndResult = kyokuLog.getKyokuEndResult();
            int[] kyokuEndPointInfo = kyokuLog.getKyokuEndPointInfo();
            List<Object> kyokuEndDetail = kyokuLog.getKyokuEndDetail();

            String validateErrorPattern = String.format("%s %s%d局%d本场", tenhouPaifu.getFileName(),
                    kyokuStartInfo[0] >= 4 ? "南" : "東", kyokuStartInfo[0]%4 + 1, kyokuStartInfo[1]);
            int totalPoint = kyokuStartInfo[2] * 1000 +
                    kyokuStartPointInfo[0] + kyokuStartPointInfo[1] +
                    kyokuStartPointInfo[2] + kyokuStartPointInfo[3];

            /**
             *  校验点数是否为10万点
             */
            if(totalPoint != 100000) {
                String validateError = String.format("%s 总点数不为10w点", validateErrorPattern);
                System.out.println(validateError);
            }

            /**
             * 校验新dora
             */
            int doraBase = 1;
            int kanCount = 0;
            for (int i=0; i<sutehaiInfo.length; i++) {
                kanCount += tsumoInfo[i].stream().filter(obj -> obj instanceof String).map(Object::toString)
                        .filter(str -> str.contains("m")).count();
                kanCount += sutehaiInfo[i].stream().filter(obj -> obj instanceof String).map(Object::toString)
                        .filter(str -> str.contains("a") || str.contains("k")).count();
            }

            // 槍槓的时候不会翻新dora
            for (int i=4; i<kyokuEndDetail.size(); i++) {
                if (kyokuEndDetail.get(i).toString().startsWith("槍槓")) {
                    kanCount -= 1;
                    break;
                }
            }

            if (doraInfo.size() != doraBase + kanCount) {
                String validateError = String.format("%s dora数量与开杠次数不匹配", validateErrorPattern);
                System.out.println(validateError);
            }

            /**
             * 校验uradora
             */
            boolean isRichiAgari = false;
            // TODO 役满立直时不会记录立直役种，需要单独处理
            boolean isYakuman = false;
            if (kyokuEndResult.equals("流局")) { // 流局时, 只有dora没有ura
                if (uraInfo.size() != 0) {
                    String validateError = String.format("%s 流局时错记ura", validateErrorPattern);
                    System.out.println(validateError);
                }
            } else if (kyokuEndResult.equals("和了")) {
                isYakuman = kyokuEndDetail.get(3).toString().contains("役満");
                for(int i=4; i<kyokuEndDetail.size(); i++) {
                    if (kyokuEndDetail.get(i).toString().contains("立直")) {
                        isRichiAgari = true;
                    }
                }
                if (isRichiAgari) { //立直和了时, 会产生ura
                    if (uraInfo.size() != doraBase + kanCount) {
                        String validateError = String.format("%s 立直和了时漏记ura", validateErrorPattern);
                        System.out.println(validateError);
                    }
                } else { //非立直和了时, 不会产生ura
                    if (uraInfo.size() != 0 && !isYakuman) {
                        String validateError = String.format("%s 非立直和了时错记ura", validateErrorPattern);
                        System.out.println(validateError);
                    }
                }
            } else {
                // 除了流局与和了, 不应该出现第三种情况
            }

            /**
             * 校验立直前后有无除暗杠外的鸣牌行为
             */
            boolean[] isRichi = {false, false, false, false};
            boolean[] canRichi = {true, true, true, true};
            for (int i=0; i<sutehaiInfo.length; i++) {
                for (int j=0; j<sutehaiInfo[i].size(); j++) {
                    if (isRichi[i]) {
                        if (sutehaiInfo[i].get(j) instanceof Integer &&
                                ((Integer) sutehaiInfo[i].get(j)).intValue() != 60) {
                            // 立直之后所有切牌信息, 如果是数字, 那只能是60
                            String validateError = String.format("%s 立直后有非摸切行为", validateErrorPattern);
                            System.out.println(validateError);
                        } else if (sutehaiInfo[i].get(j) instanceof String &&
                                !sutehaiInfo[i].get(j).toString().contains("a")){
                            String validateError = String.format("%s 立直后有非暗杠的鸣牌行为", validateErrorPattern);
                            System.out.println(validateError);
                        }
                    } else if (canRichi[i]) {
                        if (sutehaiInfo[i].get(j) instanceof Integer) {
                            // do nothing
                        } else if (sutehaiInfo[i].get(j) instanceof String &&
                                sutehaiInfo[i].get(j).toString().contains("r")) {
                            isRichi[i] = true;
                        } else if (sutehaiInfo[i].get(j) instanceof String &&
                                !sutehaiInfo[i].get(j).toString().contains("a")) {
                            canRichi[i] = false;
                        }
                    } else {
                        if (sutehaiInfo[i].get(j) instanceof String &&
                                sutehaiInfo[i].get(j).toString().contains("r")) {
                            String validateError = String.format("%s 立直前有非暗杠的鸣牌行为", validateErrorPattern);
                            System.out.println(validateError);
                        }
                    }
                }
            }

            /**
             * 校验和牌时役种记录是否正确 TODO 目前仅校验立直
             */
            if (isRichiAgari) {
                int agariProIndex = (Integer) kyokuEndDetail.get(0);
                if (!isRichi[agariProIndex]) {
                    String validateError = String.format("%s 和牌役种与手牌不匹配（立直）", validateErrorPattern);
                    System.out.println(validateError);
                }
            }

            /**
             * 校验摸到或打出的牌是否合法 TODO 目前仅检测摸牌
             */
            int[] haipaiCounter = new int[54]; // 11-19, 21-29, 31-39, 51-53
            doraInfo.forEach(dora -> haipaiCounter[dora]++);
            uraInfo.forEach(ura -> haipaiCounter[ura]++);
            for (int i=0; i<haipaiInfo.length; i++) {
                for (int j=0; j<haipaiInfo[i].length; j++) {
                    haipaiCounter[haipaiInfo[i][j]]++;
                }
            }
            for (int i=0; i<tsumoInfo.length; i++) {
                for (int j=0; j<tsumoInfo[i].size(); j++) {
                    if (tsumoInfo[i].get(j) instanceof Integer) {
                        haipaiCounter[((Integer) tsumoInfo[i].get(j)).intValue()]++;
                    } else {
                        // do nothing
                    }
                }
            }
            PAI_LEGAL_COUNT.keySet().forEach(pai -> {
                int legalCount = PAI_LEGAL_COUNT.get(pai);
                int realCount = haipaiCounter[pai];
                if (realCount > legalCount) {
                    String validateError = String.format("%s 存在非法摸牌（%s）", validateErrorPattern,
                            PAI_NAME_MAPPING.inverse().get(pai));
                    System.out.println(validateError);
                }
            });
        });
    }

    private static void validateOfficialGameInfo(OfficialGameInfo gameInfo) {
        // TODO 校验排名
        BigDecimal[] proPoints = gameInfo.getProPoints();
        int[] proRanks = gameInfo.getProRanks();
        BigDecimal pointsTotal = proPoints[0]
                .add(proPoints[1])
                .add(proPoints[2])
                .add(proPoints[3]);
        if (pointsTotal.doubleValue() != 0.0d) {
            String validateError = String.format("%s pt总和不为0", gameInfo.getFileName());
            System.out.println(validateError);
        }
        int ranksTotal = Arrays.stream(proRanks).sum();
        if (ranksTotal != 12) {
            String validateError = String.format("%s rank总和不为12", gameInfo.getFileName());
            System.out.println(validateError);
        }
        // 01, 02, 03, 12, 13, 23
        outer:
        for (int i=0; i<proRanks.length; i++) {
            if (proRanks[i] < 0 || proRanks[i] > 6) {
                // 一般走不到这里
                String validateError = String.format("%s 存在非法rank", gameInfo.getFileName());
                System.out.println(validateError);
                break;
            }
            for (int j=i+1; j<proRanks.length; j++) {
                int rankDiff = proRanks[i] - proRanks[j];
                double ptDiff = proPoints[i].subtract(proPoints[j]).doubleValue();
                if ((rankDiff == 0 && ptDiff == 0) || rankDiff * ptDiff < 0) {
                    // do nothing
                } else {
                    String validateError = String.format("%s rank与pt不匹配", gameInfo.getFileName());
                    System.out.println(validateError);
                    break outer;
                }
            }
        }
    }
}

