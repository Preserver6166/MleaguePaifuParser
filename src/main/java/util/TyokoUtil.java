package util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import official.OfficialGameInfo;
import official.OfficialPaifuUtil;
import tenhou.KyokuLog;
import tenhou.TenhouPaifu;
import tenhou.TenhouRule;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static official.OfficialConstants.PATH;

public class TyokoUtil {

    public static final String TYOKO_PAIFU_PATH = PATH + "/Documents/Mleague数据/t-yoko_data/";
    public static final String TYOKO_PAIFU_PREFIX = "https://tenhou.net/6/#json=";
    public static final String TYOKO_PAIFU_SUFFIX = "&ts=0";

    public static void main (String[] args) {
        List<OfficialGameInfo> officialGameInfoList = OfficialPaifuUtil.generateOfficialGameInfoList(
                "L001_S001_0061_01A", "L001_S001_0070_02A");
        for (OfficialGameInfo officialGameInfo: officialGameInfoList) {
            String fileName = officialGameInfo.getFileName();
            TenhouPaifu paifu = readPaifuFromFile(fileName);
            // System.out.println(fileName);
            System.out.println(JSONObject.toJSONString(paifu));
        }
    }

    public static TenhouPaifu readPaifuFromFile(String fileName) {
        TenhouPaifu.TenhouPaifuBuilder tenhouPaifuBuilder = TenhouPaifu.builder();
        tenhouPaifuBuilder.fileName(fileName);
        List<KyokuLog> kyokuLogList = new ArrayList<>();
        tenhouPaifuBuilder.log(kyokuLogList);
        boolean titleStatus = false;
        try {
            Scanner scanner = new Scanner(new File(TYOKO_PAIFU_PATH + fileName));
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith(TYOKO_PAIFU_PREFIX) && line.endsWith(TYOKO_PAIFU_SUFFIX)) {
                    JSONObject gameJson = JSONObject.parseObject(
                            line.substring(TYOKO_PAIFU_PREFIX.length(), line.length()-TYOKO_PAIFU_SUFFIX.length()));
                    if (!titleStatus) {
                        titleStatus = true;
                        String[] title = gameJson.getJSONArray("title").toArray(new String[0]);
                        String[] name = gameJson.getJSONArray("name").toArray(new String[0]);
                        tenhouPaifuBuilder.title(title);
                        tenhouPaifuBuilder.name(name);
                        TenhouRule tenhouRule = new TenhouRule();
                        tenhouRule.setDisp("");
                        tenhouRule.setAka(gameJson.getJSONObject("rule").getInteger("aka"));
                        tenhouPaifuBuilder.rule(tenhouRule);
                    }
                    for (int i=0; i<gameJson.getJSONArray("log").size(); i++) {
                        JSONArray kyokuJson = gameJson.getJSONArray("log").getJSONArray(i);
                        Integer[] kyokuStartInfo = kyokuJson.getJSONArray(0).toArray(new Integer[0]);
                        Integer[] kyokuStartPointInfo = kyokuJson.getJSONArray(1).toArray(new Integer[0]);
                        List<Integer> doraInfo = kyokuJson.getJSONArray(2).toJavaList(Integer.class);
                        List<Integer> uraInfo = kyokuJson.getJSONArray(3).toJavaList(Integer.class);
                        KyokuLog kyokuLog = new KyokuLog();
                        kyokuLog.setKyokuStartInfo(kyokuStartInfo[0], kyokuStartInfo[1], kyokuStartInfo[2]);
                        for (int j=0; j<4; j++) {
                            kyokuLog.setKyokuStartPointInfo(j, kyokuStartPointInfo[j]);
                        }
                        kyokuLog.getDoraInfo().addAll(doraInfo);
                        kyokuLog.getUraInfo().addAll(uraInfo);
                        for(int j=4; j<=13; j+=3) {
                            JSONArray haipaiJson = kyokuJson.getJSONArray(j);
                            for(int k=0; k<haipaiJson.size(); k++) {
                                kyokuLog.getHaipaiInfo()[j/3-1][k] = haipaiJson.getInteger(k);
                            }
                            JSONArray tsumoJson = kyokuJson.getJSONArray(j+1);
                            kyokuLog.getTsumoInfo()[j/3-1].addAll(tsumoJson.toJavaList(Object.class));
                            JSONArray sutehaiJson = kyokuJson.getJSONArray(j+2);
                            kyokuLog.getSutehaiInfo()[j/3-1].addAll(sutehaiJson.toJavaList(Object.class));
                        }
                        JSONArray kyokuResultJson = kyokuJson.getJSONArray(16);
                        kyokuLog.setKyokuEndResult(kyokuResultJson.getString(0));
                        if (kyokuResultJson.size()>1) {
                            for (int j = 0; j < 4; j++) {
                                kyokuLog.getKyokuEndPointInfo()[j] = kyokuResultJson.getJSONArray(1).getInteger(j);
                            }
                        }
                        if (kyokuResultJson.size()>2) {
                            kyokuLog.getKyokuEndDetail().addAll(kyokuResultJson.getJSONArray(2).toJavaList(Object.class));
                        }
                        kyokuLogList.add(kyokuLog);
                    }
                } else {
                    System.out.println("ERROR LINE:" + line);
                }
            }
        } catch (Exception ex) {
            return null;
        }

        return tenhouPaifuBuilder.build();
    }

}
