package analyze;

import analyze.PaifuAnalyzer;
import official.OfficialGameInfo;
import official.OfficialPaifuUtil;
import tenhou.TenhouPaifu;
import tenhou.TenhouPaifuUtil;

import java.util.List;
import java.util.Map;

public class App {

    public static void main (String[] args) {
        List<OfficialGameInfo> officialGameInfoList = OfficialPaifuUtil.generateOfficialGameInfoList(
                "L001_S001_0061_01A", "L001_S001_0070_01A");
        Map<String, TenhouPaifu> tenhouPaifuMap = TenhouPaifuUtil.generateTenhouPaifuMap(officialGameInfoList);
//        PaifuAnalyzer.fun1a(officialGameInfoList, tenhouPaifuMap);
        PaifuAnalyzer.fun1b(officialGameInfoList, tenhouPaifuMap);

//        List<OfficialGameInfo> officialGameInfoList = OfficialPaifuUtil.generateOfficialGameInfoList(
//                "L001_S001_0001_01A", "L001_S019_0091_02A");
//        Map<String, TenhouPaifu> tenhouPaifuMap = TenhouPaifuUtil.generateTenhouPaifuMap(officialGameInfoList);
////        PaifuAnalyzer.fun24_2(officialGameInfoList, tenhouPaifuMap);
////        PaifuAnalyzer.fun29(officialGameInfoList, tenhouPaifuMap);
//        PaifuAnalyzer.fun33(officialGameInfoList, tenhouPaifuMap);
    }

}
