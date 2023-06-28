package unused;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class MLeagueParser {

    public static final String PATH = System.getProperty("user.home");

    private static final String[] PRO_NAMES = {
            "多井", "泷沢", "寿人", "傻饼", "近藤",
            "贤王", "黑沢", "总帅", "塔罗", "少爷",
            "胜又", "亚树", "圣人", "茅森", "船长",
            "村上", "石桥", "鱼谷", "高宫", "大熊",
            "白鳥", "沢崎", "蓝子", "内川", "忍者",
            "太太", "老和", "丸子", "模特", "吾王",
            "伊达", "厨子", "东城", "瑠美", "本田"
    };

    private static final String[] WEEKDAYS = {
        "周日", "周一", "周二", "周三", "周四", "周五", "周六"
    };

    private static final String[] GENDER_TYPES = {
       "罗汉局", "娘教三子", "混合双打", "三娘教子", "观音局"
    };

    private static final String[] PRO_GENDERS = {
            "M", "M", "M", "M", "M",
            "M", "F", "M", "M", "M",
            "M", "F", "M", "F", "M",
            "M", "M", "F", "F", "M",
            "M", "M", "F", "M", "M",
            "F", "F", "F", "F", "M",
            "F", "M", "F", "F", "M"
    };

//赤坂=DR, 涩谷=AB, 樱花=SK, 火山=EX, 格斗=FC, 凤凰=PN, 雷电=TR, 海盗=UP
//    private static final String[] PRO_TEAMS = {
//            "AB", "FC", "FC", "UP", "PN",
//            "DR", "TR", "FC", "DR", "AB",
//            "EX", "EX", "TR", "PN", "UP",
//            "DR", "UP", "FN", "FC", "TR",
//            "AB", "SK", "AB", "SK", "FC",
//            "UP", "PN", "DR", "SK", "SK",
//            "FC", "EX", "FN", "EX", "TR"
//    };

    // 选手间打点数据
    private static final float[][] PRO_POINTS = new float[PRO_NAMES.length][PRO_NAMES.length];

    // 选手间比赛次数
    private static final int[][] PRO_GAMENUMS = new int[PRO_NAMES.length][PRO_NAMES.length];

    // 比赛维度数据
    private static final Set<Integer> GAMEIDS = new TreeSet<>();
    private static final Map<Integer, GameInfo> GAMEID_GAMEINFO = new HashMap<>();
    private static final Map<Integer, Set<Integer>> PRO_GAMEIDSET = new HashMap<>();

    // calculateVar
    private static final float[] PRO_AVGPOINTS = new float[PRO_NAMES.length]; // 平均打点
    private static final float[] PRO_VARPOINTS = new float[PRO_NAMES.length]; // 表现方差

    // calculateWeekDay
    private static final Map<Integer, float[]> PRO_WEEKPOINTS = new HashMap<>();
    private static final Map<Integer, int[]> PRO_WEEKGAMENUMS = new HashMap<>();

    // calculateDistance
    private static final float[][] PRO_DISTANCE = new float[PRO_NAMES.length][PRO_NAMES.length];

    // calculateGender
    private static final Map<Integer, float[]> PRO_GENDERPOINTS = new HashMap<>();
    private static final Map<Integer, int[]> PRO_GENDERGAMENUMS = new HashMap<>();

    // calculateMaybeOne
    private static final float[][] PRO_RANK_SCORE_DIFF = new float[3][PRO_NAMES.length];
    private static final int[][] PRO_RANK_GAMENUMS = new int[3][PRO_NAMES.length];

    static {
        try {
            Scanner scanner = new Scanner(new File(PATH + "/Documents/Mleague数据/pro_pro"));
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                int pro1 = Integer.parseInt(line.substring(1, line.indexOf("]")));
                int pro2 = Integer.parseInt(line.substring(line.indexOf("][") + 2, line.indexOf("]", line.indexOf("][") + 2)));
                if (pro1 > PRO_NAMES.length || pro2 > PRO_NAMES.length) {
                    continue;
                }
                JSONObject data = JSONObject.parseObject(line.substring(line.indexOf("]{") + 1));
                float point = data.getFloat("point");
                JSONArray gamesInfo = data.getJSONArray("games");
                int games = gamesInfo.size();
                PRO_GAMENUMS[pro1-1][pro2-1] = games;
                PRO_GAMENUMS[pro2-1][pro1-1] = games;
                PRO_POINTS[pro1-1][pro2-1] = point;
                PRO_POINTS[pro2-1][pro1-1] = 0 - point;

                for (int i = 0; i < gamesInfo.size(); i++) {
                    GameInfo game = gamesInfo.getJSONObject(i).toJavaObject(GameInfo.class);
                    int id = game.getId();
                    if (!GAMEIDS.contains(id)) {
                        GAMEIDS.add(id);
                        GAMEID_GAMEINFO.put(id, game);
                    }
                    ProGameInfo[] pros = game.getPros();
                    for (int j = 0; j < pros.length; j++) {
                        ProGameInfo proGameInfo = pros[j];
                        int proId = proGameInfo.getId();
                        if (!PRO_GAMEIDSET.containsKey(proId)) {
                            PRO_GAMEIDSET.put(proId, new HashSet<>());
                            PRO_WEEKPOINTS.put(proId, new float[7]);
                            PRO_WEEKGAMENUMS.put(proId, new int[7]);
                            PRO_GENDERPOINTS.put(proId, new float[5]);
                            PRO_GENDERGAMENUMS.put(proId, new int[5]);
                        }
                        PRO_GAMEIDSET.get(proId).add(id);
                    }
                }
            }

            for (Integer gameId: GAMEIDS) {
                GameInfo gameInfo = GAMEID_GAMEINFO.get(gameId);
                ProGameInfo[] pros = gameInfo.getPros();
                Arrays.sort(pros, (o1, o2) -> {
                    if (o1.getPoint() > o2.getPoint()) {
                        return -1;
                    } else if (o1.getPoint() == o2.getPoint()) {
                        return 0;
                    } else {
                        return 1;
                    }
                });
                Set<Float> gameScoreSet = Arrays.stream(pros).map(
                        pro -> pro.getPoint()).collect(Collectors.toSet());

                switch (gameScoreSet.size()) {
                    case 1: break; // 所有人同分，暂时不存在此情况
                    case 2: break; // 有三人同分，暂时不存在此情况
                    case 3: {
                        if (pros[0].getPoint() == pros[1].getPoint()) {
                            // 同分1位
                            PRO_RANK_SCORE_DIFF[1][pros[2].getId() - 1] += (pros[0].getPoint() - pros[2].getPoint() - 40);
                            PRO_RANK_SCORE_DIFF[2][pros[3].getId() - 1] += (pros[2].getPoint() - pros[3].getPoint() - 20);
                            PRO_RANK_GAMENUMS[0][pros[0].getId() - 1]++;
                            PRO_RANK_GAMENUMS[0][pros[1].getId() - 1]++;
                            PRO_RANK_GAMENUMS[1][pros[2].getId() - 1]++;
                            PRO_RANK_GAMENUMS[2][pros[3].getId() - 1]++;
                        } else if (pros[1].getPoint() == pros[2].getPoint()) {
                            // 同分2位
                            PRO_RANK_SCORE_DIFF[2][pros[3].getId() - 1] += (pros[1].getPoint() - pros[3].getPoint() - 30);
                            PRO_RANK_GAMENUMS[1][pros[1].getId() - 1]++;
                            PRO_RANK_GAMENUMS[1][pros[2].getId() - 1]++;
                            PRO_RANK_GAMENUMS[2][pros[3].getId() - 1]++;
                        } else {
                            // 同分3位
                            PRO_RANK_SCORE_DIFF[0][pros[1].getId() - 1] += (pros[0].getPoint() - pros[1].getPoint() - 40);
                            PRO_RANK_GAMENUMS[0][pros[1].getId() - 1]++;
                            PRO_RANK_GAMENUMS[2][pros[2].getId() - 1]++;
                            PRO_RANK_GAMENUMS[2][pros[3].getId() - 1]++;
                        }
                        break;
                    }
                    case 4: {
                        PRO_RANK_SCORE_DIFF[0][pros[1].getId() - 1] += (pros[0].getPoint() - pros[1].getPoint() - 40);
                        PRO_RANK_SCORE_DIFF[1][pros[2].getId() - 1] += (pros[1].getPoint() - pros[2].getPoint() - 20);
                        PRO_RANK_SCORE_DIFF[2][pros[3].getId() - 1] += (pros[2].getPoint() - pros[3].getPoint() - 20);
                        PRO_RANK_GAMENUMS[0][pros[1].getId() - 1]++;
                        PRO_RANK_GAMENUMS[1][pros[2].getId() - 1]++;
                        PRO_RANK_GAMENUMS[2][pros[3].getId() - 1]++;
                        break; //
                    }
                    default:
                }
            }
            scanner.close();

        } catch (Exception ex) {
            System.out.println("加载信息失败");
        }
    }

    public static void main(String[] args) throws Exception {

        // calculateOpp(); // 计算赢分最多、输分最多、对局最多的选手
        // calculateVar(); // 计算自身方差、对不同选手的点数方差
        // calculateWeekDay(); // 计算一周不同日期的成绩
        // calculateDistance(); // TODO 计算选手间相似度
        // calculateGender(); // 计算对不同性别对手的战绩
        calculateMaybeOne();

        if (System.currentTimeMillis() > 0) {
            return;
        }

    }

    private static void calculateMaybeOne() {

        for (int i=0; i<PRO_NAMES.length; i++) {
            System.out.println(PRO_NAMES[i] + "\t" +
                    new BigDecimal(PRO_RANK_SCORE_DIFF[0][i]).setScale(2, RoundingMode.HALF_UP).floatValue() * 1000 + "\t" +
                    PRO_RANK_GAMENUMS[0][i] + "\t" +
                    new BigDecimal(PRO_RANK_SCORE_DIFF[1][i]).setScale(2, RoundingMode.HALF_UP).floatValue() * 1000 + "\t" +
                    PRO_RANK_GAMENUMS[1][i] + "\t" +
                    new BigDecimal(PRO_RANK_SCORE_DIFF[2][i]).setScale(2, RoundingMode.HALF_UP).floatValue() * 1000 + "\t" +
                    PRO_RANK_GAMENUMS[2][i]);
        }

    }

    private static void calculateGender() {
        for (int i=0; i<PRO_NAMES.length; i++) {
            for(Integer gameId : GAMEID_GAMEINFO.keySet()) {
                GameInfo gameInfo = GAMEID_GAMEINFO.get(gameId);
                ProGameInfo[] pros = gameInfo.getPros();
                int genderType = getGenderType(pros);
                for (int j=0; j<4; j++) {
                    if (pros[j].getId()-1 == i) {
                        PRO_GENDERPOINTS.get(pros[j].getId())[genderType] += pros[j].getPoint();
                        PRO_GENDERGAMENUMS.get(pros[j].getId())[genderType]++;
                    }
                }
            }
        }
        for (int i=1; i<=PRO_NAMES.length; i++) {

            float[] genderPoints = PRO_GENDERPOINTS.get(i);
            int[] genderGameNums = PRO_GENDERGAMENUMS.get(i);
            String genderMsgFormat = "%s：%d局%+.1fpt";
            StringBuilder sb = new StringBuilder();
            sb.append(PRO_NAMES[i - 1]).append("\t");

            for (int j = 0; j < genderPoints.length; j++) {
//                System.out.println(String.format(
//                        genderMsgFormat, GENDER_TYPES[j], genderGameNums[j], genderPoints[j]));
            }

            float malePoints = 0.0f;
            int maleGameNums = 0;
            float femalePoints = 0.0f;
            int femaleGameNums = 0;
            if (PRO_GENDERS[i-1].equals("M")) {
                malePoints = genderPoints[0] + genderPoints[1] * 2 / 3 + genderPoints[2] / 3;
                maleGameNums = genderGameNums[0] + genderGameNums[1] + genderGameNums[2];
                femalePoints = genderPoints[1] / 3 + genderPoints[2] * 2 / 3 + genderPoints[3];
                femaleGameNums = genderGameNums[1] + genderGameNums[2] + genderGameNums[3];
            } else {
                malePoints = genderPoints[1] + genderPoints[2] * 2 / 3 + genderPoints[3] / 3;
                maleGameNums = genderGameNums[1] + genderGameNums[2] + genderGameNums[3];
                femalePoints = genderPoints[2] / 3 + genderPoints[3] * 2 / 3 + genderPoints[4];
                femaleGameNums = genderGameNums[2] + genderGameNums[3] + genderGameNums[4];
            }
            sb.append(new BigDecimal(malePoints).setScale(1, RoundingMode.HALF_UP).floatValue());
            sb.append("\t");
            sb.append(new BigDecimal(femalePoints).setScale(1, RoundingMode.HALF_UP).floatValue());
            System.out.println(sb.toString());
        }
    }

    private static void calculateDistance() {
        //            for(int i=0; i<PRO_NAMES.length; i++) {
//                float minDistance = 0.0f;
//                float maxDistance = 0.0f;
//                int minDistancePro = -1;
//                int maxDistancePro = -1;
//                for (int j=0; j<PRO_NAMES.length; j++) {
//                    BigDecimal sum = new BigDecimal(0.0f);
//                    if (i != j) {
//                        for (int k=0; k<PRO_NAMES.length; k++) {
//                            double x = PRO_GAMENUMS[i][k] == 0 ? (PRO_WEIGHTEDPOINTS[i] - PRO_WEIGHTEDPOINTS[k]) : (PRO_POINTS[i][k] / PRO_GAMENUMS[i][k]);
//                            double y = PRO_GAMENUMS[j][k] == 0 ? (PRO_WEIGHTEDPOINTS[j] - PRO_WEIGHTEDPOINTS[k]) : (PRO_POINTS[j][k] / PRO_GAMENUMS[j][k]);
//                            sum = sum.add(new BigDecimal(Math.pow(x - y, 2)));
//                            //sum = sum.add(new BigDecimal(Math.abs(x - y)));
//                            //sum = sum.add(new BigDecimal(Math.pow(Math.abs(x-y), 0.25d)));
//                        }
//                        float distance = new BigDecimal(Math.sqrt(sum.doubleValue())).setScale(1, RoundingMode.HALF_UP).floatValue();
//                        //float distance = sum.setScale(1, RoundingMode.HALF_UP).floatValue();
//                        PRO_Distance[i][j] = distance;
//                        if (minDistance == 0.0f || distance < minDistance) {
//                            minDistance = distance;
//                            minDistancePro = j;
//                        }
//                        if (maxDistance == 0.0f || distance > maxDistance) {
//                            maxDistance = distance;
//                            maxDistancePro = j;
//                        }
//                    }
//                }
//                System.out.println("与[" + PRO_NAMES[i] + "]最相似的是[" + PRO_NAMES[minDistancePro] + "], 距离为" + minDistance);
//                System.out.println("与[" + PRO_NAMES[i] + "]最不像的是[" + PRO_NAMES[maxDistancePro] + "], 距离为" + maxDistance);
//            }
// TODO 带负数的置信度计算
//            float p_value = maxPointGameNum * 1.0f / totalGameNum;
//            float n_value = totalPoints;
//            if (totalPoints > 0.0f) {
//                float diff = (maxPoints / maxPointGameNum) / ((totalPoints - maxPoints) / (totalGameNum - maxPointGameNum)) - 1;
////                float uv_upper_limit = n_value * p_value + (float) (2.33f * (Math.sqrt(n_value * p_value * (1 - p_value))));
//                double sig = (diff - n_value * p_value) / Math.sqrt(n_value * p_value * (1 - p_value));
//                if (sig > 2.33d) {
//                    System.out.println("[" + PRO_NAMES[i] + "]赢分最多的选手是[" + PRO_NAMES[maxPointPro] + "], 为" + maxPointGameNum + "局" + maxPoints);
//                }
//            }

//            System.out.println("共" + GAMEID_GAMEINFO.size() + "局");
    }

    private static void calculateOpp() {
        for (int i = 0; i < PRO_NAMES.length; i++) {
            int maxPointPro = -1;
            int minPointPro = -1;
            int maxGamePro = -1;

            float maxPoints = 0f;
            float minPoints = 0f;
            int maxPointGameNum = 0;
            int minPointGameNum = 0;

            int maxGameNum = 0;
            float maxGamePoints = 0f;

            int totalGameNum = 0;
            float totalPoints = 0f;

            for (int j = 0; j < PRO_NAMES.length; j++) {
                totalGameNum += PRO_GAMENUMS[i][j];
                totalPoints += PRO_POINTS[i][j];
                if (i != j) {
                    if (maxPoints < PRO_POINTS[i][j]) {
                        maxPointPro = j;
                        maxPoints = PRO_POINTS[i][j];
                        maxPointGameNum = PRO_GAMENUMS[i][j];
                    }
                    if (minPoints > PRO_POINTS[i][j]) {
                        minPointPro = j;
                        minPoints = PRO_POINTS[i][j];
                        minPointGameNum = PRO_GAMENUMS[i][j];
                    }
                    if (maxGameNum < PRO_GAMENUMS[i][j]) {
                        maxGamePro = j;
                        maxGameNum = PRO_GAMENUMS[i][j];
                        maxGamePoints = PRO_POINTS[i][j];
                    }
                }
            }

//                System.out.println("[" + PRO_NAMES[i] + "]对局最多的选手是[" + PRO_NAMES[maxGamePro] + "], 为" + maxGameNum + "局" + maxGamePoints);
//                System.out.println("[" + PRO_NAMES[i] + "]赢分最多的选手是[" + PRO_NAMES[maxPointPro] + "], 为" + maxPointGameNum + "局" + maxPoints);
//                System.out.println("[" + PRO_NAMES[i] + "]输分最多的选手是[" + PRO_NAMES[minPointPro] + "], 为" + minPointGameNum + "局" + minPoints);
        }
    }

    private static void calculateWeekDay() {

        for (int i=0; i<PRO_NAMES.length; i++) {
            for(Integer gameId : GAMEID_GAMEINFO.keySet()) {
                GameInfo gameInfo = GAMEID_GAMEINFO.get(gameId);
                ProGameInfo[] pros = gameInfo.getPros();
                int weekDay = getWeekDay(gameInfo.getTime());
                for (int j=0; j<4; j++) {
                    if (pros[j].getId()-1 == i) {
                        PRO_WEEKPOINTS.get(pros[j].getId())[weekDay] += pros[j].getPoint();
                        PRO_WEEKGAMENUMS.get(pros[j].getId())[weekDay] ++;
                    }
                }
            }
        }
        for (int i=1; i<=PRO_NAMES.length; i++) {

            float[] weekPoints = PRO_WEEKPOINTS.get(i);
            int[] weekGameNums = PRO_WEEKGAMENUMS.get(i);
            String weekMsgFormat = "[%s]%s：%d局%+.1fpt";

            for (int j=0; j<weekPoints.length; j++) {
                if (weekGameNums[j] != 0) {
                    System.out.println(String.format(
                            weekMsgFormat, PRO_NAMES[i-1], WEEKDAYS[j], weekGameNums[j], weekPoints[j]));
                }
            }
        }
    }

    private static void calculateVar() {
        for (int i=0; i<PRO_NAMES.length; i++) {
            float pointSum = 0.0f;
            float pointSum2 = 0.0f;
            int count = 0;

            float selfSum = 0.0f;
            float selfSum2 = 0.0f;
            int selfCount = 0;

            for (int j=0; j<PRO_NAMES.length; j++) {
                pointSum += PRO_POINTS[i][j];
                pointSum2 += PRO_POINTS[i][j] * PRO_POINTS[i][j];
                count += PRO_GAMENUMS[i][j];
            }
            float avgPoint = new BigDecimal(pointSum / count).
                    setScale(3, RoundingMode.HALF_UP).floatValue();
            float var = new BigDecimal(pointSum2 / count - Math.pow(avgPoint, 2)).
                    setScale(3, RoundingMode.HALF_UP).floatValue();
            //System.out.println(PRO_NAMES[i] + "平均打点:" + avgPoint + ", 方差:" + var);
            for(Integer gameId : GAMEID_GAMEINFO.keySet()) {
                GameInfo gameInfo = GAMEID_GAMEINFO.get(gameId);
                ProGameInfo[] pros = gameInfo.getPros();
                int weekDay = getWeekDay(gameInfo.getTime());
                for (int j=0; j<4; j++) {
                    if (pros[j].getId()-1 == i) {
                        selfSum += pros[j].getPoint();
                        selfSum2 += pros[j].getPoint() * pros[j].getPoint();
                        selfCount++;
                    }
                }
            }
            float selfAvg = new BigDecimal(selfSum / selfCount).
                    setScale(3, RoundingMode.HALF_UP).floatValue();
            float selfVar = new BigDecimal(selfSum2 / selfCount - Math.pow(selfAvg, 2)).
                    setScale(3, RoundingMode.HALF_UP).floatValue();
            System.out.println(PRO_NAMES[i] + "\t" + avgPoint + "\t" + var + "\t" + selfAvg + "\t" + selfVar);

        }
    }

    public static int getWeekDay(String time){

        SimpleDateFormat inputSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputSdf = new SimpleDateFormat("EE");

        try {
            Date date = inputSdf.parse(time);
            String week = outputSdf.format(date);
            if (week.equals("周日")) {
                return 0;
            } else if (week.equals("周一")) {
                return 1;
            } else if (week.equals("周二")) {
                return 2;
            } else if (week.equals("周三")) {
                return 3;
            } else if (week.equals("周四")) {
                return 4;
            } else if (week.equals("周五")) {
                return 5;
            } else if (week.equals("周六")) {
                return 6;
            } else {
                return -1;
            }
        } catch (Exception ex) {
            return -1;
        }
    }

    private static int getGenderType(ProGameInfo[] pros) {
        int genderType = 0;
        for (int i=0; i<pros.length; i++) {
            int proId = pros[i].getId();
            if (PRO_GENDERS[proId-1].equals("F")) {
                genderType++;
            }
        }
        return genderType;
    }
}


