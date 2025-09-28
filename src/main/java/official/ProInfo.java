package official;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class ProInfo {

    public static final Map<String, ProInfo> PRO_INFO = new LinkedHashMap<>() {{
        //AB
        put("多井隆晴", new ProInfo("多井隆晴", "多井", "守备"));
        put("白鳥翔", new ProInfo("白鳥翔", "白鳥", "守备"));
        put("松本吉弘", new ProInfo("松本吉弘", "松本"));
        put("日向藍子", new ProInfo("日向藍子", "日向", "守备"));
        //BS
        put("猿川真寿", new ProInfo("猿川真寿", "猿川", "门清"));
        put("菅原千瑛", new ProInfo("菅原千瑛", "菅原"));
        put("鈴木大介", new ProInfo("鈴木大介", "大介"));
        put("中田花奈", new ProInfo("中田花奈", "中田"));
        put("下石戟", new ProInfo("下石戟", "下石", "守备"));
        put("東城りお", new ProInfo("東城りお", "東城"));
        //DR
        put("園田賢", new ProInfo("園田賢", "園田"));
        put("村上淳", new ProInfo("村上淳", "村上", "门清"));
        put("鈴木たろう", new ProInfo("鈴木たろう", "たろう"));
        put("丸山奏子", new ProInfo("丸山奏子", "丸山"));
        put("浅見真紀", new ProInfo("浅見真紀", "浅見"));
        put("渡辺太", new ProInfo("渡辺太", "渡辺"));
        //EJ
        put("石井一馬", new ProInfo("石井一馬", "一馬", "守备"));
        put("三浦智博", new ProInfo("三浦智博", "三浦"));
        put("逢川恵夢", new ProInfo("逢川恵夢", "逢川"));
        put("HIRO柴田", new ProInfo("HIRO柴田", "HIRO", "门清"));
        //EX
        put("二階堂亜樹", new ProInfo("二階堂亜樹", "亜樹","守备"));
        put("勝又健志", new ProInfo("勝又健志", "勝又", "门清"));
        put("松ヶ瀬隆弥", new ProInfo("松ヶ瀬隆弥", "松ヶ瀬", "门清"));
        put("二階堂瑠美", new ProInfo("二階堂瑠美", "瑠美", "门清"));
        put("永井孝典", new ProInfo("永井孝典", "永井","守备"));
        put("内川幸太郎", new ProInfo("内川幸太郎", "内川"));
        //KC
        put("佐々木寿人", new ProInfo("佐々木寿人", "寿人", "门清"));
        put("高宮まり", new ProInfo("高宮まり", "高宮"));
        put("前原雄大", new ProInfo("前原雄大", "前原", "守备"));
        put("藤崎智", new ProInfo("藤崎智", "藤崎"));
        put("伊達朱里紗", new ProInfo("伊達朱里紗", "伊達"));
        put("滝沢和典", new ProInfo("滝沢和典", "滝沢", "守备"));
        //PX
        put("魚谷侑未", new ProInfo("魚谷侑未", "魚谷"));
        put("近藤誠一", new ProInfo("近藤誠一", "近藤", "守备"));
        put("茅森早香", new ProInfo("茅森早香", "茅森"));
        put("和久津晶", new ProInfo("和久津晶", "和久津"));
        put("醍醐大", new ProInfo("醍醐大", "醍醐", "守备"));
        put("竹内元太", new ProInfo("竹内元太", "竹内", "守备"));
        put("浅井堂岐", new ProInfo("浅井堂岐", "堂岐"));
        //RD
        put("萩原聖人", new ProInfo("萩原聖人", "萩原", "门清"));
        put("瀬戸熊直樹", new ProInfo("瀬戸熊直樹", "瀬戸熊", "门清"));
        put("黒沢咲", new ProInfo("黒沢咲", "黒沢", "门清"));
        put("本田朋広", new ProInfo("本田朋広", "本田", "副露"));
        //SK
        put("岡田紗佳", new ProInfo("岡田紗佳", "岡田"));
        put("沢崎誠", new ProInfo("沢崎誠", "沢崎", "守备"));
        put("堀慎吾", new ProInfo("堀慎吾", "堀"));
        put("渋川難波", new ProInfo("渋川難波", "渋川"));
        put("阿久津翔太", new ProInfo("阿久津翔太", "阿久津","守备"));
        //UP
        put("小林剛", new ProInfo("小林剛", "小林", "副露"));
        put("朝倉康心", new ProInfo("朝倉康心", "朝倉", "守备"));
        put("石橋伸洋", new ProInfo("石橋伸洋", "石橋", "守备"));
        put("瑞原明奈", new ProInfo("瑞原明奈", "瑞原"));
        put("鈴木優", new ProInfo("鈴木優", "鈴木優", "守备"));
        put("仲林圭", new ProInfo("仲林圭", "仲林"));
    }};

    private String proName; // 全名
    private String proNameBrief; // 简称
    private String nagaStyle; // NAGA类型

    public ProInfo(String proName, String proNameBrief, String nagaStyle) {
        this.proName = proName;
        this.proNameBrief = proNameBrief;
        this.nagaStyle = nagaStyle;
    }

    public ProInfo(String proName, String proNameBrief) {
        this(proName, proNameBrief, "标准");
    }

    public static String getProNameByBrief(String proNameBrief) {
        for (ProInfo proInfo: PRO_INFO.values()) {
            if (proInfo.getProNameBrief().equals(proNameBrief)) {
                return proInfo.getProName();
            }
        }
        return null; // should not go into here
    }

    public static String getProNagaStyleByBrief(String proNameBrief) {
        for (ProInfo proInfo: PRO_INFO.values()) {
            if (proInfo.getProNameBrief().equals(proNameBrief)) {
                return proInfo.getNagaStyle();
            }
        }
        return null; // should not go into here
    }

}
