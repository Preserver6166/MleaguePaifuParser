package analyze;

import com.alibaba.fastjson.JSONObject;
import mahjongutils.models.Tile;
import mahjongutils.shanten.*;
import official.OfficialGameInfo;
import official.ProInfo;
import tenhou.KyokuLog;
import tenhou.TenhouPaifu;
import util.Majong;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static official.OfficialConstants.*;
import static official.ProInfo.PRO_INFO;
import static tenhou.TenhouConstants.*;

public class PaifuAnalyzer {

    // TODO 所有对PRO_INFO进行的统计操作后续整合到一起去
//    int kyokuCount = tenhouPaifu.getLog().size();
//                        Arrays.stream(gameInfo.getProNames()).forEach(proName -> {
//        PRO_INFO.get(proName).addGameCount();
//        PRO_INFO.get(proName).addKyokuCount(kyokuCount);
//    });
//                        tenhouPaifu.getLog().forEach(kyokuLog -> {
//        if (kyokuLog.getKyokuEndResult().equals("和了")) {
//            int agariProIndex = (Integer) kyokuLog.getKyokuEndDetail().get(0);
//            boolean isRichi = kyokuLog.getSutehaiInfo()[agariProIndex].stream().
//                    filter(obj -> obj instanceof String).
//                    map(obj -> (String) obj).
//                    anyMatch(obj -> obj.startsWith("r"));
//            if (isRichi) {
//                // 判断有没有里
//            }
//        }
//    });

    /**
     * 用来打印比赛信息的函数
     */
    public static void fun1(List<OfficialGameInfo> officialGameInfoList, Map<String, TenhouPaifu> tenhouPaifuMap) {

        if (officialGameInfoList.size() > 30) {
            officialGameInfoList.forEach(officialGameInfo -> {
                System.out.println(officialGameInfo.getFileName());
            });
            return;
        }
        officialGameInfoList.forEach(officialGameInfo -> {
            printGame(officialGameInfo.getFileName(), officialGameInfo,
                    tenhouPaifuMap.get(officialGameInfo.getFileName()));
        });
        System.out.println("GAME_INFOS size:" + officialGameInfoList.size());

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
                PRO_INFO.get(gameInfo.getProNames()[0]).getProNameBrief(),
                PRO_INFO.get(gameInfo.getProNames()[0]).getNagaStyle()));
        System.out.println(String.format(NAGA_PATTERN_NEW,
                PRO_INFO.get(gameInfo.getProNames()[1]).getProNameBrief(),
                PRO_INFO.get(gameInfo.getProNames()[1]).getNagaStyle()));
        System.out.println(String.format(NAGA_PATTERN_NEW,
                PRO_INFO.get(gameInfo.getProNames()[2]).getProNameBrief(),
                PRO_INFO.get(gameInfo.getProNames()[2]).getNagaStyle()));
        System.out.println(String.format(NAGA_PATTERN_NEW,
                PRO_INFO.get(gameInfo.getProNames()[3]).getProNameBrief(),
                PRO_INFO.get(gameInfo.getProNames()[3]).getNagaStyle()));
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

    /**
     * 计划中的数据视频
     * TODO AL之王数据统计
     */
    public static void funUnLabeled() {

    }

    // 计算选手预期中里率和实际中里率
    public static void fun33(List<OfficialGameInfo> officialGameInfoList, Map<String, TenhouPaifu> tenhouPaifuMap) {

        Map<String, List<Integer>> pro_actUraNumList= new HashMap<>(); // 实际中里期望值
        Map<String, List<BigDecimal>> pro_calUraNumList= new HashMap<>(); // 预期中里期望值

        for (OfficialGameInfo gameInfo : officialGameInfoList) {
            String[] proNames = gameInfo.getProNames();
            for (int i=0; i<proNames.length; i++) {
                if (!pro_actUraNumList.containsKey(proNames[i])) {
                    pro_actUraNumList.put(proNames[i], new ArrayList<>());
                    pro_calUraNumList.put(proNames[i], new ArrayList<>());
                }
            }
            TenhouPaifu tenhouPaifu = tenhouPaifuMap.get(gameInfo.getFileName());
            for (KyokuLog kyokuLog : tenhouPaifu.getLog()) {
                if (kyokuLog.getKyokuEndResult().equals("和了")) {
                    int agariProIndex = -1;
                    for (int i=0; i<kyokuLog.getKyokuEndPointInfo().length; i++) {
                        if (kyokuLog.getKyokuEndPointInfo()[i] > 0) {
                            agariProIndex = i;
                            break;
                        }
                    }
                    if (agariProIndex < 0) {
                        System.out.println("和了对局没有和了选手");
                        continue;
                    }

                    List<Object> agariProTsumoInfo = kyokuLog.getTsumoInfo()[agariProIndex];
                    List<Object> agariProSuteHaiInfo = kyokuLog.getSutehaiInfo()[agariProIndex];
                    boolean isRichiAgari = false;
                    int[] agariProHandStat = new int[48]; // 47+1, 51~53处理掉
                    for (int i=0; i<agariProSuteHaiInfo.size(); i++) {
                        if (agariProSuteHaiInfo.get(i) instanceof Integer) {
                            int pai = (Integer) agariProSuteHaiInfo.get(i);
                            if (pai == 60) { // 摸切
                                pai = (Integer) agariProTsumoInfo.get(i);
                            }
                            dealWithPai(agariProHandStat, pai, false);
                        } else {
                            String paiString = (String) agariProSuteHaiInfo.get(i);
                            if (paiString.contains("r")) {
                                isRichiAgari = true;
                                int pai = Integer.valueOf(paiString.replaceAll("r",""));
                                if (pai == 60) {
                                    pai = (Integer) agariProTsumoInfo.get(i);
                                }
                                dealWithPai(agariProHandStat, pai, false);
                            } else {
                                // 暗杠，视为没出牌即可
                            }
                        }
                    }
                    if (!isRichiAgari) {
                        continue;
                    }

                    int[] yamaStat = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                            4, 4, 4, 4, 4, 4, 4, 4, 4, 0,
                            4, 4, 4, 4, 4, 4, 4, 4, 4, 0,
                            4, 4, 4, 4, 4, 4, 4, 4, 4, 0,
                            4, 4, 4, 4, 4, 4, 4}; // 11~19, 21~29, 31~39, 41~47
                    kyokuLog.getDoraInfo().forEach(pai -> {
                        dealWithPai(yamaStat, pai, false);
                    });
                    for (int i=0; i<4; i++) {
                        for (int j=0; j<13; j++) {
                            int pai = kyokuLog.getHaipaiInfo()[i][j];
                            dealWithPai(yamaStat, pai, false);
                            if (i == agariProIndex) {
                                dealWithPai(agariProHandStat, pai, true);
                            }
                        }
                        for (int j=0; j<kyokuLog.getTsumoInfo()[i].size(); j++) {
                            Object tsumoObj = kyokuLog.getTsumoInfo()[i].get(j);
                            if (tsumoObj instanceof String) {
                                String haipaiString = (String) tsumoObj;
                                if (haipaiString.contains("p") || haipaiString.contains("c") ||
                                        haipaiString.contains("k") || haipaiString.contains("m")) {
                                    // do nothing
                                } else {
                                    System.out.println("Unexpected: " + tsumoObj);
                                }
                            } else {
                                int pai = (Integer) tsumoObj;
                                dealWithPai(yamaStat, pai, false);
                                if (i == agariProIndex) {
                                    dealWithPai(agariProHandStat, pai, true);
                                }
                            }
                        }
                    }
                    // 如果是放铳，需要把放铳那张牌也加进来
                    int lastActProIndex = (Integer) kyokuLog.getKyokuEndDetail().get(1);
                    if (lastActProIndex == agariProIndex) {
                        // do nothing
                    } else {
                        List<Object> lastActProTsumoInfo = kyokuLog.getTsumoInfo()[lastActProIndex];
                        List<Object> lastActProSuteHaiInfo = kyokuLog.getSutehaiInfo()[lastActProIndex];

                        int pai;
                        if (lastActProSuteHaiInfo.get(lastActProSuteHaiInfo.size()-1) instanceof Integer) {
                            pai = (Integer) lastActProSuteHaiInfo.get(lastActProSuteHaiInfo.size()-1);
                        } else {
                            String paiString = (String) lastActProSuteHaiInfo.get(lastActProSuteHaiInfo.size()-1);
                            pai = Integer.valueOf(paiString.replaceAll("r", ""));
                        }
                        if (pai == 60) {
                            pai = (Integer) lastActProTsumoInfo.get(lastActProTsumoInfo.size()-1);
                        }
                        dealWithPai(agariProHandStat, pai, true);
                    }

                    int yamaCount = 0;
                    for (int i=11; i<=47; i++) {
                        if (i % 10 == 0) {
                            continue;
                        } else {
                            yamaCount += yamaStat[i];
                        }
                    }
                    int uraSize = kyokuLog.getUraInfo().size();
                    int totalComb = comb(yamaCount, uraSize == 0 ? 1 : uraSize);
                    int actUraCount = 0; // 实际中的里宝数
                    double calUraCount = 0.0d; // 理论中的里宝数

                    for (int i=0; i<uraSize; i++) { // 漏记里的情况，一般来说是没有中里
                        int ura = 0;
                        int uraIndicator = kyokuLog.getUraInfo().get(i);
                        if (uraIndicator >= 11 && uraIndicator <= 39) {
                            ura = uraIndicator % 9 + 10 + 9 * (uraIndicator / 10 - 1);
                        } else if (uraIndicator >= 41 && uraIndicator <= 44) {
                            ura = uraIndicator % 4 + 41;
                        } else if (uraIndicator >= 45 && uraIndicator <= 47) {
                            ura = (uraIndicator+1) % 3 + 45;
                        } else if (uraIndicator >= 51 && uraIndicator <= 53) {
                            ura = (uraIndicator - 50) * 10 + 6;
                        }
                        actUraCount += agariProHandStat[ura];
                    }

                    if (uraSize <= 1) { // 漏记ura而出现0的情况，视为只有1张ura
                        for (int i=0; i<yamaCount; i++) {
                            int ura1 = getUra(yamaStat, i+1);
                            calUraCount += agariProHandStat[ura1];
                        }
                    } else if (uraSize == 2) {
                        for (int i=0; i<yamaCount; i++) {
                            for (int j=i+1; j<yamaCount; j++) {
                                int ura1 = getUra(yamaStat, i+1);
                                int ura2 = getUra(yamaStat, j+1);
                                calUraCount += agariProHandStat[ura1];
                                calUraCount += agariProHandStat[ura2];
                            }
                        }
                    } else if (uraSize == 3) {
                        for (int i=0; i<yamaCount; i++) {
                            for (int j=i+1; j<yamaCount; j++) {
                                for (int k=j+1; k<yamaCount; k++) {
                                    int ura1 = getUra(yamaStat, i+1);
                                    int ura2 = getUra(yamaStat, j+1);
                                    int ura3 = getUra(yamaStat, k+1);
                                    calUraCount += agariProHandStat[ura1];
                                    calUraCount += agariProHandStat[ura2];
                                    calUraCount += agariProHandStat[ura3];
                                }
                            }
                        }
                    } else  {
                        System.out.println("uraSize " + uraSize + " case");
                    }
                    pro_actUraNumList.get(proNames[agariProIndex]).add(actUraCount);
                    pro_calUraNumList.get(proNames[agariProIndex]).add(new BigDecimal(calUraCount / totalComb));
                } else {
                    // do nothing
                }
            }
        }

        pro_actUraNumList.keySet().forEach(proName -> {
            int totalActUraNum = 0;
            int totalActUraRate = 0;
            BigDecimal totalCalUraNum = new BigDecimal(0.0d);
            for (Integer actUraNum: pro_actUraNumList.get(proName)) {
                totalActUraNum += actUraNum;
                if (actUraNum > 0) {
                    totalActUraRate++;
                }
            }
            for (BigDecimal calUraNum: pro_calUraNumList.get(proName)) {
                totalCalUraNum = totalCalUraNum.add(calUraNum);
            }
            System.out.println(proName + "\t" +
                totalActUraNum + "\t" +
                totalActUraRate + "\t" +
                totalCalUraNum.setScale(6, RoundingMode.HALF_EVEN) + "\t" +
                pro_actUraNumList.get(proName).size()
            );
        });
    }

    // 孟获
    public static void fun29(List<OfficialGameInfo> officialGameInfoList, Map<String, TenhouPaifu> tenhouPaifuMap) {
        Map<String, Integer> proPro_state = new HashMap<>();
        Map<String, List<String>> proPro_detail = new HashMap<>();
        for (OfficialGameInfo gameInfo : officialGameInfoList) {
            String[] proNames = gameInfo.getProNames();
            int[] proRanks = gameInfo.getProRanks();
            for (int i=0; i<proNames.length; i++) {
                for (int j=i+1; j<proNames.length; j++) {
                    String proPro = proNames[i].compareTo(proNames[j]) > 0 ?
                            (proNames[i] + "\t" + proNames[j]) : (proNames[j] + "\t" + proNames[i]);
                    if (!proPro_state.containsKey(proPro)) {
                        proPro_state.put(proPro, 0);
                        proPro_detail.put(proPro, new ArrayList<>());
                    }
                    int currentState = proPro_state.get(proPro);
                    String proProDetail =  gameInfo.getGameDate() + " #" + gameInfo.getDayIndex() + "    " +
                            PRO_INFO.get(proNames[i]).getProNameBrief() + " " + (proRanks[i]/2+1) + "位" + "    " +
                            PRO_INFO.get(proNames[j]).getProNameBrief() + " " + (proRanks[j]/2+1) + "位";
                    boolean winState = proNames[i].compareTo(proNames[j]) > 0 ?
                            (proRanks[i] < proRanks[j]) : (proRanks[j] < proRanks[i]);
                    boolean loseState = proNames[i].compareTo(proNames[j]) > 0 ?
                            (proRanks[i] > proRanks[j]) : (proRanks[j] > proRanks[i]);
                    if (winState) {
                        if (currentState >= 0) {
                            currentState++;
                            proPro_state.put(proPro, currentState);
                            proPro_detail.get(proPro).add(proProDetail);
                            if (currentState % 7 == 0) {
                                for (String subDetail: proPro_detail.get(proPro)) {
                                    System.out.println(subDetail);
                                }
                                proPro_detail.get(proPro).clear();
                            }
                        } else {
                            proPro_state.put(proPro, 1);
                            proPro_detail.get(proPro).clear();
                            proPro_detail.get(proPro).add(proProDetail);
                        }
                    } else if (loseState) {
                        if (currentState <= 0) {
                            currentState--;
                            proPro_state.put(proPro, currentState);
                            proPro_detail.get(proPro).add(proProDetail);
                            if (Math.abs(currentState) % 7 == 0) {
                                for (String subDetail: proPro_detail.get(proPro)) {
                                    System.out.println(subDetail);
                                }
                                proPro_detail.get(proPro).clear();
                            }
                        } else {
                            proPro_state.put(proPro, -1);
                            proPro_detail.get(proPro).clear();
                            proPro_detail.get(proPro).add(proProDetail);
                        }
                    } else { // 平手，连胜/连败中断
                        proPro_state.put(proPro, 0);
                        proPro_detail.get(proPro).clear();
                    }
                }
            }
        }
    }

    // 记谱错误
    public static void fun28(List<OfficialGameInfo> officialGameInfoList, Map<String, TenhouPaifu> tenhouPaifuMap) {
        Map<String, Integer> proName_errorCount = new HashMap<>();
        Map<String, Integer> proNameAndSeason_errorCount = new HashMap<>();
        PRO_INFO.keySet().forEach(proName -> {
            proName_errorCount.put(proName, 0);
        });
        try {
            Scanner scanner = new Scanner(new File(PATH + "/Downloads/2.txt"));
            while (scanner.hasNextLine()) {
                String[] params = scanner.nextLine().split(" ");
                String rawSeason = params[0].split("_")[1];
                String season;
                if (rawSeason.equals("S001") || rawSeason.equals("S002")) {
                    season = "18-19赛季";
                } else if (rawSeason.equals("S003") || rawSeason.equals("S004") || rawSeason.equals("S005")) {
                    season = "19-20赛季";
                } else if (rawSeason.equals("S007") || rawSeason.equals("S008") || rawSeason.equals("S009")) {
                    season = "20-21赛季";
                } else if (rawSeason.equals("S010") || rawSeason.equals("S011") || rawSeason.equals("S012")) {
                    season = "21-22赛季";
                } else if (rawSeason.equals("S013") || rawSeason.equals("S014") || rawSeason.equals("S015")) {
                    season = "22-23赛季";
                } else if (rawSeason.equals("S016") || rawSeason.equals("S017") || rawSeason.equals("S018")) {
                    season = "23-24赛季";
                } else {
                    season = "未知";
                }
                if (params[2].equals("ALL")) {
                    String[] proNames = tenhouPaifuMap.get(params[0]).getName();
                    for (int i=0; i<proNames.length; i++) {
                        proName_errorCount.put(proNames[i], proName_errorCount.get(proNames[i])+1);
                        String proNameAndSeason = proNames[i] + "\t" + season;
                        if (!proNameAndSeason_errorCount.containsKey(proNameAndSeason)) {
                            proNameAndSeason_errorCount.put(proNameAndSeason, 0);
                        }
                        proNameAndSeason_errorCount.put(proNameAndSeason,
                                proNameAndSeason_errorCount.get(proNameAndSeason) + 1);
                    }
                } else {
                    String proName = ProInfo.getProNameByBrief(params[2]);
                    proName_errorCount.put(proName, proName_errorCount.get(proName)+1);
                    String proNameAndSeason = proName + "\t" + season;
                    if (!proNameAndSeason_errorCount.containsKey(proNameAndSeason)) {
                        proNameAndSeason_errorCount.put(proNameAndSeason, 0);
                    }
                    proNameAndSeason_errorCount.put(proNameAndSeason,
                            proNameAndSeason_errorCount.get(proNameAndSeason) + 1);
                }
            }
//            ANALYZE_PRO_INFO.keySet().forEach(proName -> {
//                System.out.println(proName + "\t" +
//                        ANALYZE_PRO_INFO.get(proName).getGameCount() + "\t" +
//                        ANALYZE_PRO_INFO.get(proName).getKyokuCount() + "\t" +
//                        proName_errorCount.get(proName));
//            });
//            proNameAndSeason_errorCount.keySet().forEach(proNameAndSeason -> {
//                System.out.println(proNameAndSeason + "\t" +
//                        proNameAndSeason_errorCount.get(proNameAndSeason));
//            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // TODO 统计选手23-24赛季的通算战绩、战绩方差、平均NAGA度、NAGA度方差、半庄数

    /**
     * 统计园田贤经历的所有里3
     */
    public static void fun27(List<OfficialGameInfo> officialGameInfoList, Map<String, TenhouPaifu> tenhouPaifuMap) {
        for (OfficialGameInfo gameInfo : officialGameInfoList) {
            String[] proNames = gameInfo.getProNames();
            if (!String.join(",", proNames).contains("園田賢")) {
                continue;
            }
            TenhouPaifu tenhouPaifu = tenhouPaifuMap.get(gameInfo.getFileName());
            for (int i=0; i<tenhouPaifu.getLog().size(); i++) {
                KyokuLog kyokuLog = tenhouPaifu.getLog().get(i);
                if (kyokuLog.getKyokuEndResult().contains("流局")) {
                    // do nothing
                } else {
                    for (int j=4; j<kyokuLog.getKyokuEndDetail().size(); j++) {
                        String fanInfo = kyokuLog.getKyokuEndDetail().get(j).toString();
                        int uraIndex = fanInfo.indexOf("裏");
                        if (uraIndex >= 0) {
                            int uraFan = Integer.valueOf(fanInfo.substring(uraIndex+4, uraIndex+5));
                            if (uraFan >=3) {
                                System.out.println(gameInfo.getGameBrief());
                                System.out.println(kyokuLog.getKyokuBrief());
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 统计选手的最佳上分拍档、上分贡献、最佳下分拍档、下分贡献、半庄数
     */
    public static void fun32(List<OfficialGameInfo> officialGameInfoList, Map<String, TenhouPaifu> tenhouPaifuMap) {
        Map<String, BigDecimal> proComb_score = new LinkedHashMap<>();
        Map<String, BigDecimal> proA_score = new LinkedHashMap<>();
        Map<String, BigDecimal> proB_score = new LinkedHashMap<>();
        Map<String, Integer> proComb_num = new LinkedHashMap<>();
        for (OfficialGameInfo gameInfo : officialGameInfoList) {
            if (gameInfo.getFileName().contains("S001") ||
                    gameInfo.getFileName().contains("S003") ||
                    gameInfo.getFileName().contains("S007") ||
                    gameInfo.getFileName().contains("S010") ||
                    gameInfo.getFileName().contains("S013") ||
                    gameInfo.getFileName().contains("S016")) {
            } else {
                continue;
            }
            String[] proNames = gameInfo.getProNames();
            BigDecimal[] proPoints = gameInfo.getProPoints();
            String[] proNameCombs = new String[]{
                    (proNames[0] + "\t" + proNames[1]), (proNames[1] + "\t" + proNames[0]),
                    (proNames[0] + "\t" + proNames[2]), (proNames[2] + "\t" + proNames[0]),
                    (proNames[0] + "\t" + proNames[3]), (proNames[3] + "\t" + proNames[0]),
                    (proNames[1] + "\t" + proNames[2]), (proNames[2] + "\t" + proNames[1]),
                    (proNames[1] + "\t" + proNames[3]), (proNames[3] + "\t" + proNames[1]),
                    (proNames[2] + "\t" + proNames[3]), (proNames[3] + "\t" + proNames[2])
            };
            Arrays.stream(proNameCombs).forEach(proComb -> {
                if (!proComb_score.containsKey(proComb)) {
                    proComb_score.put(proComb, new BigDecimal(0));
                    proA_score.put(proComb, new BigDecimal(0));
                    proB_score.put(proComb, new BigDecimal(0));
                    proComb_num.put(proComb, 0);
                }
            });
            int[] proIndex = {0, 1, 0, 2, 0, 3, 1, 2, 1, 3, 2, 3};
            for (int i=0; i<proNameCombs.length; i+=2) {
                BigDecimal pt1 = proPoints[proIndex[i]];
                BigDecimal pt2 = proPoints[proIndex[i+1]];
                BigDecimal newScoreComb1 = proComb_score.get(proNameCombs[i]).add(pt1).add(pt2);
                BigDecimal newScoreComb2 = proComb_score.get(proNameCombs[i+1]).add(pt1).add(pt2);
                proComb_score.put(proNameCombs[i], newScoreComb1);
                proComb_score.put(proNameCombs[i+1], newScoreComb2);
                BigDecimal newScoreA1 = proA_score.get(proNameCombs[i]).add(pt1);
                BigDecimal newScoreA2 = proA_score.get(proNameCombs[i+1]).add(pt2);
                BigDecimal newScoreB1 = proB_score.get(proNameCombs[i+1]).add(pt2);
                BigDecimal newScoreB2 = proB_score.get(proNameCombs[i]).add(pt1);
                proA_score.put(proNameCombs[i], newScoreA1);
                proA_score.put(proNameCombs[i+1], newScoreA2);
                proB_score.put(proNameCombs[i+1], newScoreB1);
                proB_score.put(proNameCombs[i], newScoreB2);
                proComb_num.put(proNameCombs[i], proComb_num.get(proNameCombs[i])+1);
                proComb_num.put(proNameCombs[i+1], proComb_num.get(proNameCombs[i+1])+1);
            }
        }

//        String[] percent;
//        percent = calPercent(30,50);
//        System.out.println(percent[0]+"\t"+percent[1]);
//        percent = calPercent(-45,-55);
//        System.out.println(percent[0]+"\t"+percent[1]);
//        percent = calPercent(45,-80);
//        System.out.println(percent[0]+"\t"+percent[1]);
//        percent = calPercent(72,-33);
//        System.out.println(percent[0]+"\t"+percent[1]);
        for(String proComb: proComb_score.keySet()) {
            System.out.println(proComb + "\t" + proComb_score.get(proComb) + "\t" +
                    proA_score.get(proComb) + "\t" + proComb_num.get(proComb));
        }
    }

    /**
     * TODO 统计选手配牌平均向听、配牌平均dora、平均听牌巡目
     */
    public static void fun24_2(List<OfficialGameInfo> officialGameInfoList, Map<String, TenhouPaifu> tenhouPaifuMap) {
        Map<String, AnalyzeProInfo> analyzeProInfoMap = AnalyzeProInfo.initAnalyzeProInfoMap();

        officialGameInfoList.forEach(officialGameInfo -> {
            for (int i=0; i<officialGameInfo.getProNames().length; i++) {
                analyzeProInfoMap.get(officialGameInfo.getProNames()[i]).increaseGameCount();
            }
            TenhouPaifu tenhouPaifu = tenhouPaifuMap.get(officialGameInfo.getFileName());
            for (int i=0; i<officialGameInfo.getProNames().length; i++) {
                analyzeProInfoMap.get(officialGameInfo.getProNames()[i]).increaseKyokuCount(
                        tenhouPaifu.getLog().size());
            }
            tenhouPaifu.getLog().forEach(kyokuLog -> {
                for (int i=0; i<kyokuLog.getHaipaiInfo().length; i++) {
                    List<String> haipaiInfo = Arrays.stream(kyokuLog.getHaipaiInfo()[i]).mapToObj(
                            haipai -> PAI_NAME_MAPPING.inverse().get(haipai)).collect(Collectors.toList());
                    String haipaiString = String.join("", haipaiInfo);
                    List<Tile> tiles = Tile.Companion.parseTiles(haipaiString.toLowerCase());
                    ShantenResult rs1 = ShantenKt.shanten(tiles);
                    int minShanten = rs1.getShantenInfo().getShantenNum();
                    String proName = officialGameInfo.getProNames()[i];
                    AnalyzeProInfo analyzeProInfo = analyzeProInfoMap.get(proName);
                    int akaCount = Long.valueOf(haipaiInfo.stream().filter(haipai ->
                            haipai.contains("M") || haipai.contains("P") || haipai.contains("S")).count()).intValue();
                    final int doraIndicator = kyokuLog.getDoraInfo().get(0);
                    int doraCount = Long.valueOf(haipaiInfo.stream().filter(haipai ->
                            DORA_INDICATOR_MAPPING.get(PAI_NAME_MAPPING.get(haipai).intValue()) == doraIndicator).count()).intValue();
                    analyzeProInfo.increaseHaipaiShanten(minShanten);
                    analyzeProInfo.increaseHaipaiDoraCount(akaCount+doraCount);
                }
            });
        });

        analyzeProInfoMap.keySet().stream().forEach(proName -> {
            System.out.println(proName + "\t" +
                    analyzeProInfoMap.get(proName).getHaipaiShanten() + "\t" +
                    analyzeProInfoMap.get(proName).getHaipaiDoraCount() + "\t" +
                    analyzeProInfoMap.get(proName).getKyokuCount());
        });
    }

    /**
     * 统计NAGA度与战绩关系
     */
    public static void fun21(List<OfficialGameInfo> officialGameInfoList, Map<String, TenhouPaifu> tenhouPaifuMap) {
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
        for(OfficialGameInfo officialGameInfo: officialGameInfoList) {
            String[] proNames = officialGameInfo.getProNames();
            BigDecimal[] proPoints = officialGameInfo.getProPoints();
            for (int i=0; i<proNames.length; i++) {
                proName_pt.putIfAbsent(proNames[i], 0);
                int point = proPoints[i].multiply(new BigDecimal(10)).intValue();
                proName_pt.put(proNames[i], proName_pt.get(proNames[i]) + point);
            }
        }

        for (String proName: proName_pt.keySet()) {
            System.out.println(proName + "\t" + proName_nagaPoint.get(proName) + "\t" + proName_pt.get(proName));
        }
    }

    /**
     * 统计每位选手初战到吃一的累计时间
     */
    public static void fun19(List<OfficialGameInfo> officialGameInfoList, Map<String, TenhouPaifu> tenhouPaifuMap) throws Exception {

        String[] proNamesInOrder = PRO_INFO.keySet().toArray(new String[PRO_INFO.size()]);

        Map<String, OfficialGameInfo> proName_firstGameDate = new LinkedHashMap<>();
        Map<String, OfficialGameInfo> proName_firstRank0Date = new LinkedHashMap<>();
        Map<String, Integer> proName_interval = new LinkedHashMap<>();
        Map<String, Integer> proName_gameNum = new LinkedHashMap<>();

        for (OfficialGameInfo gameInfo: officialGameInfoList) {
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
        proName_firstGameDate.keySet().stream().forEach(proName -> {
            System.out.println(
                    String.format(pattern19_1,
                            proName,
                            proName_firstGameDate.get(proName).getGameName(),
                            proName_firstRank0Date.get(proName) == null ?
                                    "未知" : proName_firstRank0Date.get(proName).getGameName(),
                            proName_interval.get(proName),
                            proName_gameNum.get(proName)
                    )
            );
        });

        String pattern19_2 = "%s\n%s\n%s\n%d个\n%d分钟";
        proName_firstGameDate.keySet().stream().forEach(proName -> {
            System.out.println(
                    String.format(pattern19_2,
                            proName,
                            proName_firstGameDate.get(proName).getGameName(),
                            proName_firstRank0Date.get(proName) == null ?
                                    "未知" : proName_firstRank0Date.get(proName).getGameName(),
                            proName_gameNum.get(proName),
                            proName_interval.get(proName)
                    )
            );
            System.out.println();
        });
    }

    /**
     * @return
     */
    private static String[] calPercent(int partA, int partB) {
        int total = partA + partB;
        BigDecimal decimalA = new BigDecimal(partA).multiply(new BigDecimal(100));
        BigDecimal decimalB = new BigDecimal(partB).multiply(new BigDecimal(100));
        BigDecimal decimalT = new BigDecimal(total);
        if (total > 0) {
            return new String[]{
                String.format("%.2f%%", decimalA.divide(decimalT, 2, RoundingMode.HALF_EVEN).doubleValue()),
                String.format("%.2f%%", decimalB.divide(decimalT, 2, RoundingMode.HALF_EVEN).doubleValue()),
            };
        } else if (total < 0) {
            return calPercent(0-partA, 0-partB);
        } else { // total = 0
            if (partA > 0) {
                return new String[]{"100.00%, -100.00%"};
            } else if (partA < 0) {
                return new String[]{"-100.00%, 100.00%"};
            } else {
                return new String[]{"0.00%, 0.00%"};
            }
        }
    }

    private static int comb(int n, int k) {
        if (n < k || n <= 0 || k < 0 || k > 5) { // 里宝牌最多5张
            return 0;
        } else if (k == 0) {
            return 1;
        } else {
            int comb = n;
            for (int i=1; i<k; i++) {
                comb = comb * (n-i);
                comb = comb / (i+1);
            }
            return comb;
        }
    }

    private static int getUra(int[] targetStat, int index) {
        int currentIndex = 0;
        int uraIndicator = 0;
        for (int i=11; i<=47; i++) {
            if (i % 10 == 0) {
                continue;
            }
            if (targetStat[i] + currentIndex >= index) {
                uraIndicator = i;
                break;
            } else {
                currentIndex += targetStat[i];
            }
        }
        return getUra(uraIndicator);
    }

    private static int getUra(int uraIndicator) {
        int ura = 0;
        if (uraIndicator >= 11 && uraIndicator <= 39) {
            ura = uraIndicator % 9 + 10 + 9 * (uraIndicator / 10 - 1);
        } else if (uraIndicator >= 41 && uraIndicator <= 44) {
            ura = uraIndicator % 4 + 41;
        } else if (uraIndicator >= 45 && uraIndicator <= 47) {
            ura = (uraIndicator+1) % 3 + 45;
        } else if (uraIndicator >= 51 && uraIndicator <= 53) {
            ura = (uraIndicator - 50) * 10 + 6;
        }
        return ura;
    }

    private static void dealWithPai(int[] targetStat, int pai, boolean isAdd) {
        if (pai >= 51 && pai <= 53) {
            if (isAdd) {
                targetStat[pai * 10 - 495]++;
            } else {
                targetStat[pai * 10 - 495]--;
            }
        } else {
            if (isAdd) {
                targetStat[pai]++;
            } else {
                targetStat[pai]--;
            }
        }
    }
}
