package tenhou;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

@Data
@JSONType(serializer = KyokuLog.KyokuLogSerializer.class)
public class KyokuLog {

    @Setter(AccessLevel.NONE)
    private int[] kyokuStartInfo = new int[3]; // [東1局, 0本场, 0供托]
    @Setter(AccessLevel.NONE)
    private int[] kyokuStartPointInfo = {25000, 25000, 25000, 25000};
    @Setter(AccessLevel.NONE)
    private List<Integer> doraInfo = new ArrayList<>(); // 随开杠可能变多
    @Setter(AccessLevel.NONE)
    private List<Integer> uraInfo = new ArrayList<>(); // 随开杠可能变多

    @Setter(AccessLevel.NONE)
    private int[][] haipaiInfo = new int[4][13]; // 起手牌，固定13张
    @Setter(AccessLevel.NONE)
    private int[] haipaiIndex = new int[4];
    @Setter(AccessLevel.NONE)
    private List<Object>[] tsumoInfo = new List[]{
            new ArrayList(), new ArrayList<>(), new ArrayList(), new ArrayList()
    }; // 各家摸牌
    @Setter(AccessLevel.NONE)
    private List<Object>[] sutehaiInfo = new List[]{
            new ArrayList(), new ArrayList<>(), new ArrayList(), new ArrayList()
    }; // 各家出的牌

    private String kyokuEndResult;

    @Setter(AccessLevel.NONE)
    private int[] kyokuEndPointInfo = new int[4]; // 局结束时的点数变化

    // ["和了",[-1300,-2600,6200,-1300],[2,2,2,"20符4飜1300-2600点","立直(1飜)","門前清自摸和(1飜)","断幺九(1飜)","平和(1飜)"]]]]
    // ["流局",[-1000,3000,-1000,-1000]]
    @Setter(AccessLevel.NONE)
    private List<Object> kyokuEndDetail = new ArrayList<>();

    private String kyokuBrief; // 東4局0本場 園田 2000all

    @JSONField(serialize = false)
    @Setter(AccessLevel.NONE)
    private List<String>[] proHandRecords = new ArrayList[4]; // 按巡目记录选手的手牌

    @JSONField(serialize = false)
    @Setter(AccessLevel.NONE)
    private List<String>[] proFuroRecords = new ArrayList[4]; // 按巡目记录选手的副露

    public void setKyokuStartInfo(int kyokuStartInfo1, int kyokuStartInfo2, int kyokuStartInfo3) {
        kyokuStartInfo[0] = kyokuStartInfo1;
        kyokuStartInfo[1] = kyokuStartInfo2;
        kyokuStartInfo[2] = kyokuStartInfo3;
    }

    public void initProHandRecords(int proIndex, String value) {
        if (proHandRecords[proIndex] == null) {
            proHandRecords[proIndex] = new ArrayList<>();
            proHandRecords[proIndex].add(value);
        } else {
            String oldValue = proHandRecords[proIndex].get(0);
            proHandRecords[proIndex].clear();
            proHandRecords[proIndex].add(oldValue + value);
        }
    }

    public void appendProHandRecords(int proIndex, String value) {
        String oldValue = proHandRecords[proIndex].get(proHandRecords[proIndex].size()-1);
    }

    public void reduceProHandRecords(int proIndex, String value) {

    }

    public void appendProFuroRecords(int index, String value) {
        proFuroRecords[index].add(value);
    }

    public void setKyokuStartPointInfo(int index, int value) {
        kyokuStartPointInfo[index] = value;
    }

    public void appendKyokuEndPointInfo(int index, int value) {
        kyokuEndPointInfo[index] += value;
    }

    public void setHaipaiInfo(int index, int value) {
        haipaiInfo[index][haipaiIndex[index]] = value;
        haipaiIndex[index] += 1;
    }

    public void appendTsumoInfo(int index, Object value) {
        tsumoInfo[index].add(value);
    }

    public void appendSutehaiInfo(int index, Object value) {
        sutehaiInfo[index].add(value);
    }

    public void appendKyokuEndDetail(Object value) {
        kyokuEndDetail.add(value);
    }

    public String getKyokuStartInfoInStringFormat() {
        String kyokuIndex = kyokuStartInfo[0] <= 3 ?
                ("東" + (kyokuStartInfo[0] + 1)) :
                ("南" + (kyokuStartInfo[0] - 3));
        return kyokuIndex + "局" + kyokuStartInfo[1] + "本场";
    }

    /**
     * 自定义json转换逻辑
     */
    public static class KyokuLogSerializer implements ObjectSerializer {

        @Override
        public void write(JSONSerializer jsonSerializer,
                          Object obj, Object fieldName, Type fieldType, int features) {
            KyokuLog kyokuLog = (KyokuLog) obj;
            List<Object> log = new ArrayList<>();
            log.add(kyokuLog.kyokuStartInfo);
            log.add(kyokuLog.kyokuStartPointInfo);
            log.add(kyokuLog.doraInfo);
            log.add(kyokuLog.uraInfo);
            for (int i=0; i<4; i++) {
                final int[] currentProHaipai = kyokuLog.haipaiInfo[i];
                List<Integer> wrapper = new AbstractList<>() {
                    @Override
                    public Integer get(int index) {
                        return Integer.valueOf(currentProHaipai[index]);
                    }

                    @Override
                    public int size() {
                        return currentProHaipai.length;
                    }

                    @Override
                    public Integer set(int index, Integer element) {
                        int oldValue = currentProHaipai[index];
                        currentProHaipai[index] = element.intValue();
                        return oldValue;
                    }
                };
                /**
                 * 对配牌进行自定义排序
                 * 11-15, 51, 16-25, 52, 26-35, 53, 36-47
                 */
                wrapper.sort((o1, o2) -> {
                    if ((o1.intValue() == 51 || o1.intValue() == 52 || o1.intValue() == 53) &&
                            (o2.intValue() == 51 || o2.intValue() == 52 || o2.intValue() == 53)) {
                        return o1.intValue() - o2.intValue();
                    } else if (o1.intValue() == 51) {
                        return o2.intValue() > 15 ? -1 : 1;
                    } else if (o1.intValue() == 52) {
                        return o2.intValue() > 25 ? -1 : 1;
                    } else if (o1.intValue() == 53) {
                        return o2.intValue() > 35 ? -1 : 1;
                    } else if (o2.intValue() == 51) {
                        return o1.intValue() > 15 ? 1 : -1;
                    } else if (o2.intValue() == 52) {
                        return o1.intValue() > 25 ? 1 : -1;
                    } else if (o2.intValue() == 53) {
                        return o1.intValue() > 35 ? 1 : -1;
                    } else {
                        return o1.intValue() - o2.intValue();
                    }
                });
                log.add(kyokuLog.haipaiInfo[i]);
                log.add(kyokuLog.tsumoInfo[i]);
                log.add(kyokuLog.sutehaiInfo[i]);
            }
            List<Object> kyokuEndInfo = new ArrayList<>();
            kyokuEndInfo.add(kyokuLog.kyokuEndResult);
            kyokuEndInfo.add(kyokuLog.kyokuEndPointInfo);
            if (kyokuLog.kyokuEndDetail.size() != 0) {
                kyokuEndInfo.add(kyokuLog.kyokuEndDetail);
            }
            log.add(kyokuEndInfo);
            SerializeWriter out = jsonSerializer.getWriter();
            out.config(SerializerFeature.DisableCheckSpecialChar, true);
            out.write(JSONObject.toJSONString(log));
        }

    }
}
