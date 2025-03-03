package analyze;

import lombok.Data;
import official.ProInfo;

import java.util.LinkedHashMap;
import java.util.Map;

import static official.ProInfo.PRO_INFO;

@Data
public class AnalyzeProInfo {

    private ProInfo proInfo;

    private int gameCount = 0; // 半庄数
    private int kyokuCount = 0; // 对局数（不是半庄数）

    private int haipaiDoraCount = 0; // 配牌dora数（宝牌+赤宝牌）
    private int haipaiShanten = 0; // 配牌向听数

    private int midTenpaiKyokuCount; // 局中至少听牌一次的对局数
    private int midTenpaiRoundCount; // 局中第一次听牌时的巡目X

    public AnalyzeProInfo(ProInfo proInfo) {
        this.proInfo = proInfo;
    }

    public void increaseGameCount() { this.gameCount ++; }

    public void increaseKyokuCount(int kyokuCount) {
        this.kyokuCount += kyokuCount;
    }

    public void increaseHaipaiDoraCount(int haipaiDoraCount) {
        this.haipaiDoraCount += haipaiDoraCount;
    }

    public void increaseHaipaiShanten(int haipaiShanten) {
        this.haipaiShanten += haipaiShanten;
    }

    public static Map<String, AnalyzeProInfo> initAnalyzeProInfoMap() {
        Map<String, AnalyzeProInfo> analyzeProInfoMap = new LinkedHashMap<>();
        for (Map.Entry<String, ProInfo> proInfoEntry : PRO_INFO.entrySet()) {
            analyzeProInfoMap.put(proInfoEntry.getKey(), new AnalyzeProInfo(proInfoEntry.getValue()));
        }
        return analyzeProInfoMap;
    }
}
