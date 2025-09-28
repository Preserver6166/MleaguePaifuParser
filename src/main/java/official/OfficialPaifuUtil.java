package official;

import com.alibaba.fastjson.JSONArray;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static official.OfficialConstants.*;

public class OfficialPaifuUtil {

    /**
     * startGame L001_S019_0082_01A
     * endGame L001_S019_0082_02A
     */
    public static List<OfficialGameInfo> generateOfficialGameInfoList(
            String startGame, String endGame) {
        List<String[]> officialFileInfoList = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File(SCHEDULE_FILE));
            while (scanner.hasNextLine()) {
                String[] officialFileInfo = scanner.nextLine().split("\t");
                String fileName = officialFileInfo[0];
                if (fileName.compareTo(startGame) >= 0) {
                    if (fileName.compareTo(endGame) <= 0) {
                        assert fileName.length() == 18;
                        officialFileInfoList.add(officialFileInfo);
                    } else {
                        break;
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Cannot find schedule file");
        }

        List<OfficialGameInfo> officialGameInfoList = new ArrayList<>();
        for (String[] officialFileInfo: officialFileInfoList) {
            String fileName = officialFileInfo[0];
            String dayInfo = officialFileInfo[1];
            File file = new File(FILENAME_PREFIX_V2 + fileName);
            if (file.exists()) {
                try {
                    OfficialGameInfo.OfficialGameInfoBuilder officialGameInfoBuilder =
                            OfficialGameInfo.builder();
                    officialGameInfoBuilder.fileName(fileName);
                    officialGameInfoBuilder.dayIndex(dayInfo.charAt(12) - 48);
                    // example: L001_S022_0008_02A
                    officialGameInfoBuilder.season(SEASON_MAP.get(fileName.split("_")[1]));
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
                    validateOfficialGameInfo(gameInfo);
                    officialGameInfoList.add(gameInfo);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                System.out.println(fileName + " does not exist");
            }
        }

//        Iterator<OfficialGameInfo.OfficialGameInfoBuilder> iterator = new OfficialGameFileIterator().iterator();
//        while(iterator.hasNext()) {
//            OfficialGameInfo.OfficialGameInfoBuilder officialGameInfoBuilder = iterator.next();
//            String fileName = officialGameInfoBuilder.build().getFileName();
//            if (fileName.compareTo(startGame) < 0) {
//                continue;
//            }
//            if (fileName.compareTo(endGame) > 0) {
//                continue;
//            }
//            assert fileName.length() == 18;
//            File file = new File(FILENAME_PREFIX_V2 + fileName);
//            if (file.exists()) {
//                try {
//                    officialGameInfoBuilder.dayIndex(fileName.charAt(16) - 48);
//                    Scanner scanner = new Scanner(file);
//                    while(scanner.hasNextLine()) {
//                        String line = scanner.nextLine();
//                        if (line.contains(TIME_START_SIGNAL)) {
//                            parseTime(officialGameInfoBuilder, line);
//                        } else if (line.contains(PAIFU_START_SIGNAL)) {
//                            parsePaifu(officialGameInfoBuilder, line);
//                        }
//                    }
//                    scanner.close();
//                    OfficialGameInfo gameInfo = officialGameInfoBuilder.build();
//                    validateOfficialGameInfo(gameInfo);
//                    officialGameInfoList.add(gameInfo);
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            } else {
//                System.out.println(fileName + " does not exist");
//            }
//        }
        return officialGameInfoList;
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
