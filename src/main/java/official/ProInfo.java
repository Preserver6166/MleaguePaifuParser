package official;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class ProInfo {

    public static final Map<String, ProInfo> PRO_INFO = new LinkedHashMap<>() {{
        put("園田賢", new ProInfo("園田賢", "園田"));
        put("堀慎吾", new ProInfo("堀慎吾", "堀"));
        put("小林剛", new ProInfo("小林剛", "小林"));
        put("村上淳", new ProInfo("村上淳", "村上"));
        put("沢崎誠", new ProInfo("沢崎誠", "沢崎"));
        put("白鳥翔", new ProInfo("白鳥翔", "白鳥"));
        put("藤崎智", new ProInfo("藤崎智", "藤崎"));
        put("黒沢咲", new ProInfo("黒沢咲", "黒沢"));
        put("丸山奏子", new ProInfo("丸山奏子", "丸山"));
        put("前原雄大", new ProInfo("前原雄大", "前原"));
        put("勝又健志", new ProInfo("勝又健志", "勝又"));
        put("和久津晶", new ProInfo("和久津晶", "和久津"));
        put("多井隆晴", new ProInfo("多井隆晴", "多井"));
        put("岡田紗佳", new ProInfo("岡田紗佳", "岡田"));
        put("日向藍子", new ProInfo("日向藍子", "日向"));
        put("朝倉康心", new ProInfo("朝倉康心", "朝倉"));
        put("本田朋広", new ProInfo("本田朋広", "本田"));
        put("東城りお", new ProInfo("東城りお", "東城"));
        put("松本吉弘", new ProInfo("松本吉弘", "松本"));
        put("滝沢和典", new ProInfo("滝沢和典", "滝沢"));
        put("瑞原明奈", new ProInfo("瑞原明奈", "瑞原"));
        put("石橋伸洋", new ProInfo("石橋伸洋", "石橋"));
        put("茅森早香", new ProInfo("茅森早香", "茅森"));
        put("萩原聖人", new ProInfo("萩原聖人", "萩原"));
        put("近藤誠一", new ProInfo("近藤誠一", "近藤"));
        put("高宮まり", new ProInfo("高宮まり", "高宮"));
        put("魚谷侑未", new ProInfo("魚谷侑未", "魚谷"));
        put("二階堂亜樹", new ProInfo("二階堂亜樹", "亜樹"));
        put("二階堂瑠美", new ProInfo("二階堂瑠美", "瑠美"));
        put("伊達朱里紗", new ProInfo("伊達朱里紗", "伊達"));
        put("佐々木寿人", new ProInfo("佐々木寿人", "寿人"));
        put("内川幸太郎", new ProInfo("内川幸太郎", "内川"));
        put("松ヶ瀬隆弥", new ProInfo("松ヶ瀬隆弥", "松ヶ瀬"));
        put("瀬戸熊直樹", new ProInfo("瀬戸熊直樹", "瀬戸熊"));
        put("鈴木たろう", new ProInfo("鈴木たろう", "たろう"));
        put("渋川難波", new ProInfo("渋川難波", "渋川"));
        put("鈴木優", new ProInfo("鈴木優", "鈴木優"));
        put("仲林圭", new ProInfo("仲林圭", "仲林"));
        put("猿川真寿", new ProInfo("猿川真寿", "猿川"));
        put("菅原千瑛", new ProInfo("菅原千瑛", "菅原"));
        put("鈴木大介", new ProInfo("鈴木大介", "大介"));
        put("中田花奈", new ProInfo("中田花奈", "中田"));
        put("醍醐大", new ProInfo("醍醐大", "醍醐"));
        put("浅見真紀", new ProInfo("浅見真紀", "浅見"));
        put("渡辺太", new ProInfo("渡辺太", "渡辺"));
        put("竹内元太", new ProInfo("竹内元太", "竹内"));
        put("浅井堂岐", new ProInfo("浅井堂岐", "堂岐"));
    }};

    private String proName; // 全名
    private String proNameBrief; // 简称

    private int gameCount; // 半庄数
    private int kyokuCount; // 对局数（不是半庄数）
    private int haipaiDoraCount; // 配牌dora数（宝牌+赤宝牌）
    private int haipaiShanten; // 配牌向听数
    private int midTenpaiKyokuCount; // 局中至少听牌一次的对局数
    private int midTenpaiRoundCount; // 局中第一次听牌时的巡目X

    private StringBuilder richiAgariUraInfo = new StringBuilder();

    public ProInfo(String proName, String proNameBrief) {
        this.proName = proName;
        this.proNameBrief = proNameBrief;
        this.gameCount = 0;
        this.kyokuCount = 0;
        this.haipaiDoraCount = 0;
        this.haipaiShanten = 0;
    }

    public void addGameCount() { this.gameCount ++; }

    public void addKyokuCount(int kyokuCount) {
        this.kyokuCount += kyokuCount;
    }

    public void addHaipaiDoraCount(int haipaiDoraCount) {
        this.haipaiDoraCount += haipaiDoraCount;
    }

    public void addHaipaiShanten(int haipaiShanten) {
        this.haipaiShanten += haipaiShanten;
    }

    public void appendRichiAgariResult(int result) {
       this.richiAgariUraInfo.append(result);
    }

    public static String getProNameByBrief(String proNameBrief) {
        for (ProInfo proInfo: PRO_INFO.values()) {
            if (proInfo.getProNameBrief().equals(proNameBrief)) {
                return proInfo.getProName();
            }
        }
        return null; // should not go into here
    }

}
