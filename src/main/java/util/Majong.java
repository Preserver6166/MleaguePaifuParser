package util;

import java.util.*;
import java.util.stream.Collectors;

public class Majong {

    //全ての牌をとりあえず配列に突っ込む
    public static final Map<Integer, Map<String, Integer>> HAIPAI = new HashMap<>();

    static {
        //マンズ 萬
        int c = 1;
        for (int m_num = 1; m_num <= 9; m_num++){
            for (int m_cnt = 1;m_cnt <= 4;m_cnt++){
                HAIPAI.put(c, new HashMap<String,Integer>());
                HAIPAI.get(c).put("num", m_num);
                HAIPAI.get(c).put("type", 1);
                c++;
            }
        }

        //ピンズ 筒
        for (int p_num = 1; p_num <= 9; p_num++){
            for (int p_cnt = 1;p_cnt <= 4;p_cnt++){
                HAIPAI.put(c, new HashMap<String,Integer>());
                HAIPAI.get(c).put("num", p_num);
                HAIPAI.get(c).put("type", 2);
                c++;
            }
        }

        //ソウズ 索
        for (int s_num = 1; s_num <= 9; s_num++){
            for (int s_cnt = 1;s_cnt <= 4;s_cnt++){
                HAIPAI.put(c, new HashMap<String,Integer>());
                HAIPAI.get(c).put("num", s_num);
                HAIPAI.get(c).put("type", 3);
                c++;
            }
        }

        //字牌
        for (int j_num = 1; j_num <= 7; j_num++){
            for (int j_cnt = 1;j_cnt <= 4;j_cnt++){
                HAIPAI.put(c, new HashMap<String,Integer>());
                HAIPAI.get(c).put("num", j_num);
                HAIPAI.get(c).put("type", 4);
                c++;
            }
        }
    }


    public static void main(String[] args) {

        Integer hai_c = 0;
//        List<Integer> hais = new ArrayList<Integer>();
        //全部で136牌
        
//        while (true){
//            Random rnd = new Random();
//            int ran = rnd.nextInt(136) + 1;
//            if (hais.contains(ran)){
//                continue;
//            }
//
//            hais.add(ran);
//
//            if (hai_c >= 12){
//                break;
//            }
//            hai_c++;
//        }
//        Collections.sort(hais);

//        String[] haisInString = {"1m","3m","3m","6m","9m","9m","2p","3p","4p","1s","5S","8s","7z"};
//        String[] haisInString = {"1m","3m","6m","7m","7m","7m","7m","9p","1s","4s","2z","6z","7z"};
        String[] haisInString = {"1m","6m","9m","8p","5S","7s","9s","2z","3z","4z","6z","7z","7z"};
        List<Integer> hais = convertHais(Arrays.stream(haisInString).collect(Collectors.toList()));
        //テスト用
        /*
        hais.add(2);
        hais.add(4);
        hais.add(6);
        hais.add(7);
        hais.add(13);
        hais.add(15);
        hais.add(17);
        hais.add(19);
        hais.add(20);
        hais.add(23);
        hais.add(25);
        hais.add(28);
        hais.add(35);
        */
        
        System.out.println(hais);
        //牌の表示
        haiDisp(HAIPAI, hais);


        //チートイツチェック
        int chitoi_shanten = chitoiCheck(HAIPAI, hais);
        //国士チェック
        int kokushi_shanten = kokushiCheck(HAIPAI, hais);
        //面子チェック
        int mentsh_shanten = mentshCheck(HAIPAI, hais);
        
        System.out.println("チートイツ");
        System.out.println(chitoi_shanten);
        System.out.println("国士");
        System.out.println(kokushi_shanten);
        System.out.println("通常");
        System.out.println(mentsh_shanten);
    }
    
    public static Integer chitoiCheck(Map<Integer, Map<String,Integer>> haipai, List<Integer> hais) {
        int chitoi_toitsu_count = 0;
        int before_hai_num = 0;
        int before2_hai_num = 0;
        int type_count = 1;
        //トイツの数をとりあえず数える
        for (int hai_num: hais){
            Map<String,Integer> hai_info = haipai.get(hai_num);
            if (
                haipai.containsKey(before_hai_num)
            ){
                Map<String,Integer> before_hai_info = haipai.get(before_hai_num);
                if (
                    hai_info.get("num") == before_hai_info.get("num") &&
                    hai_info.get("type") == before_hai_info.get("type")
                ){
                    if (
                        !haipai.containsKey(before2_hai_num)
                    ){
                        chitoi_toitsu_count++;
                    } else {
                        Map<String,Integer> before2_hai_info = haipai.get(before2_hai_num);
                        if (
                            hai_info.get("num") != before2_hai_info.get("num") ||
                            hai_info.get("type") != before2_hai_info.get("type")
                        ){
                            chitoi_toitsu_count++;
                        }
                    }
                } else {
                    type_count++;
                }
            }
            before2_hai_num = before_hai_num;
            before_hai_num = hai_num;
        }
        
        int type_cal = 0;
        if (type_count < 7){
             type_cal = 7 - type_count;
        }
        return 6 - chitoi_toitsu_count + type_cal;
    }

    public static Integer kokushiCheck(Map<Integer, Map<String,Integer>> haipai, List<Integer> hais) {
        int kokushi_count = 0;
        int before_hai_num = 0;
        boolean kokushi_toitsu_check = false;
        //一・九・字牌の種類を数える。＋トイツのチェックも
        for (int hai_num: hais){
            Map<String,Integer> hai_info = haipai.get(hai_num);
            if (
                hai_info.get("type") == 4 ||
                hai_info.get("num") == 1 ||
                hai_info.get("num") == 9
            ){
                if (
                    haipai.containsKey(before_hai_num)
                ){
                    Map<String,Integer> before_hai_info = haipai.get(before_hai_num);
                    if (
                        hai_info.get("num") == before_hai_info.get("num") &&
                        hai_info.get("type") == before_hai_info.get("type")
                    ){
                        kokushi_toitsu_check = true;
                    } else {
                        kokushi_count++;
                    }
                } else {
                    kokushi_count++;
                }
            }
            before_hai_num = hai_num;
        }
        
        if (kokushi_toitsu_check == true){
            kokushi_count++;
        }
        return 13 - kokushi_count;
    }

    public static Integer mentshCheck(Map<Integer, Map<String,Integer>> haipai, List<Integer> hais) {
        //まずは、暗刻・トイツを探す
        Map<Integer, Map<String,Integer>> haicheck = new HashMap<Integer, Map<String,Integer>>();
        for (int i = 0 ; i < hais.size() ; i++){
            Integer hai_num = hais.get(i);
            Map<String,Integer> hai_info = haipai.get(hai_num);
            boolean kasanari = false;
            for(Map.Entry<Integer, Map<String,Integer>> e : haicheck.entrySet()) {
                if (
                    hai_info.get("num") == e.getValue().get("num") &&
                    hai_info.get("type") == e.getValue().get("type")
                ){
                    haicheck.get(e.getKey()).put("count",e.getValue().get("count") + 1);
                    kasanari = true;
                }
            }
            if (kasanari == false){
                haicheck.put(i, new HashMap<String,Integer>());
                haicheck.get(i).put("count", 1);
                haicheck.get(i).put("type", hai_info.get("type"));
                haicheck.get(i).put("num", hai_info.get("num"));
            }
        }
        
        Map<String,Integer> base_except_hai = new HashMap<String,Integer>();
        
        base_except_hai.put("toitsu", 0);
        base_except_hai.put("anko", 0);
        
        //計算方法としては順子・暗刻・対子・塔子の順で探して行って、型を確定するものをベースとする。
        Integer baseShanten = shantenCalc(haipai, hais, base_except_hai, false);
        //ここでアンコ・トイツを優先して外すものを探す
        List<Integer> toitsu_check = new ArrayList<Integer>();
        List<Integer> anko_check = new ArrayList<Integer>();
        
        for(Map.Entry<Integer, Map<String,Integer>> f : haicheck.entrySet()) {
            if (f.getValue().get("count") == 1){
                continue;
            }
            
            boolean toitsu_flag = false;
            boolean anko_flag = false;
            //とりあえず外で宣言する
            Integer toitsuShanten = 13;
            Integer ankoShanten = 13;
            if (f.getValue().get("count") >= 2){
                //2枚外す
                List<Integer> hais_check_toitsu = new ArrayList<Integer>(hais);
                List<Integer> used_hai_toitsu = new ArrayList<Integer>();
                Integer toitsu_counter = 0;
                for (int t = 0 ; t < hais_check_toitsu.size() ; t++){
                    Integer hai_num = hais.get(t);
                    Map<String,Integer> hai_info = haipai.get(hai_num);
                    if (
                        hai_info.get("num") == f.getValue().get("num") &&
                        hai_info.get("type") == f.getValue().get("type") &&
                        toitsu_counter < 2
                    ){
                        used_hai_toitsu.add(hai_num);
                        toitsu_counter++;
                    }
                }
                
                Collections.sort(used_hai_toitsu);
                Collections.reverse(used_hai_toitsu);
                for (int toitsu_k = 0 ; toitsu_k < used_hai_toitsu.size() ; toitsu_k++){
                    hais_check_toitsu.remove(used_hai_toitsu.get(toitsu_k));
                }
                //トイツを一つ外した状態で計算をしてみてシャンテン数が下がるかチェック
                Map<String,Integer> toitsu_except_hai = new HashMap<String,Integer>();
                toitsu_except_hai.put("toitsu", 1);
                toitsu_except_hai.put("anko", 0);
                toitsuShanten = shantenCalc(haipai, hais_check_toitsu, toitsu_except_hai, false);
                if (baseShanten > toitsuShanten){
                    toitsu_flag = true;
                }
            }
            
            if (f.getValue().get("count") >= 3){
                //3枚外す
                List<Integer> hais_check_anko = new ArrayList<Integer>(hais);
                List<Integer> used_hai_anko = new ArrayList<Integer>();
                Integer anko_counter = 0;
                for (int t = 0 ; t < hais_check_anko.size() ; t++){
                    Integer hai_num = hais.get(t);
                    Map<String,Integer> hai_info = haipai.get(hai_num);
                    if (
                        hai_info.get("num") == f.getValue().get("num") &&
                        hai_info.get("type") == f.getValue().get("type") &&
                        anko_counter < 3
                    ){
                        used_hai_anko.add(hai_num);
                        anko_counter++;
                    }
                }
                
                Collections.sort(used_hai_anko);
                Collections.reverse(used_hai_anko);
                for (int anko_k = 0 ; anko_k < used_hai_anko.size() ; anko_k++){
                    hais_check_anko.remove(used_hai_anko.get(anko_k));
                }
                //アンコを一つ外した状態で計算をしてみてシャンテン数が下がるかチェック
                Map<String,Integer> anko_except_hai = new HashMap<String,Integer>();
                anko_except_hai.put("toitsu", 0);
                anko_except_hai.put("anko", 1);
                ankoShanten = shantenCalc(haipai, hais_check_anko, anko_except_hai, false);
                if (
                    baseShanten > ankoShanten &&
                    toitsuShanten > ankoShanten
                ){
                    anko_flag = true;
                }
            }
            
            if (anko_flag == true){
                anko_check.add(f.getKey());
            } else if (toitsu_flag == true){
                toitsu_check.add(f.getKey());
            }
        }
        
        //除外した方がシャンテン数が小さくなるものについて、のぞいてから実際にシャンテン数を計算する
        List<Integer> hais_check_last = new ArrayList<Integer>(hais);
        List<Integer> except_hai = new ArrayList<Integer>();
        for (int except_toitsu_k = 0 ; except_toitsu_k < toitsu_check.size() ; except_toitsu_k++){
            Integer except_toitsu_counter = 0;
            for (int except_toitsu_t = 0 ; except_toitsu_t < hais_check_last.size() ; except_toitsu_t++){
                Integer toitsu_hai_num = hais.get(except_toitsu_t);
                Map<String,Integer> toitsu_hai_info = haipai.get(toitsu_hai_num);
                if (
                    toitsu_hai_info.get("num") == haicheck.get(toitsu_check.get(except_toitsu_k)).get("num") &&
                    toitsu_hai_info.get("type") == haicheck.get(toitsu_check.get(except_toitsu_k)).get("type") &&
                    except_toitsu_counter < 2
                ){
                    except_hai.add(toitsu_hai_num);
                    except_toitsu_counter++;
                }
            }
            
        }
        
        for (int except_anko_k = 0 ; except_anko_k < anko_check.size() ; except_anko_k++){
            Integer except_anko_counter = 0;
            for (int except_anko_t = 0 ; except_anko_t < hais_check_last.size() ; except_anko_t++){
                Integer anko_hai_num = hais.get(except_anko_t);
                Map<String,Integer> anko_hai_info = haipai.get(anko_hai_num);
                if (
                    anko_hai_info.get("num") == haicheck.get(anko_check.get(except_anko_k)).get("num") &&
                    anko_hai_info.get("type") == haicheck.get(anko_check.get(except_anko_k)).get("type") &&
                    except_anko_counter < 3
                ){
                    except_hai.add(anko_hai_num);
                    except_anko_counter++;
                }
            }
            
        }
        
        Collections.sort(except_hai);
        Collections.reverse(except_hai);
        for (int anko_k = 0 ; anko_k < except_hai.size() ; anko_k++){
            hais_check_last.remove(except_hai.get(anko_k));
        }
        Map<String,Integer> except_hai_last = new HashMap<String,Integer>();
        except_hai_last.put("toitsu", toitsu_check.size());
        except_hai_last.put("anko", anko_check.size());
        
        return shantenCalc(haipai, hais_check_last, except_hai_last, true);
    }

    private static Integer shantenCalc(Map<Integer, Map<String,Integer>> haipai, List<Integer> hais, Map<String,Integer> except_hai, Boolean result){
        Integer except_toitsu = except_hai.get("toitsu");
        Integer except_anko = except_hai.get("anko");
        //順子は下からチェックした場合と上からチェックした場合でメンツの構成が変わり、その影響でシャンテン数にも影響が出ることがあるため、
        //昇順降順両方でチェックを行って小さい方のシャンテンを取る
        
        List<Integer> hais_base_1 = new ArrayList<Integer>(hais);
        //順子チェック
        List<Integer> hai1_check1 = shuntsuCheck(haipai, hais_base_1, 1);
        //暗刻チェック
        List<Integer> hai1_check2 = ankoCheck(haipai, hai1_check1);
        Integer mentsu_check_nokori1 = hai1_check2.size();
        Integer mentsu1 = (13 - except_toitsu * 2 - except_anko * 3 - mentsu_check_nokori1 ) / 3;
        //対子チェック
        List<Integer> hai1_check3 = toitsuCheck(haipai, hai1_check2);
        
        Integer toitsu_check_nokori1 = hai1_check3.size();
        Integer toitsu1 =  (mentsu_check_nokori1 - toitsu_check_nokori1 ) / 2;
        //塔子チェック
        List<Integer> hai1_check4 = tatsuCheck(haipai, hai1_check3);
        
        Integer tatsh_check_nokori1 = hai1_check4.size();
        Integer tatsu1 =  (toitsu_check_nokori1 - tatsh_check_nokori1 ) / 2;
        mentsu1 += except_anko;
        toitsu1 += except_toitsu;
        
        Integer shanten1 = 8 - mentsu1 * 2 - (tatsu1 + toitsu1);
        if (tatsu1 + toitsu1 + mentsu1 > 5){
            shanten1 += tatsu1 + toitsu1 + mentsu1 - 5;
        }

        if (mentsu1 < 4 &&shanten1 == 0 && toitsu1 == 0 && tatsu1 + toitsu1 + mentsu1 >= 5){
            //面子が4つそろっておらず対子がなく、さらにメンツの数は揃っている場合はシャンテン数を1増やす。(ターツターツではシャンテン数が足りない)
            shanten1 += 1;
        }
        
        if (toitsu1 + tatsu1 == 6){
            shanten1 -= 1;
        }

        List<Integer> hais_base_2 = new ArrayList<Integer>(hais);
        //順子チェック
        List<Integer> hai2_check1 = shuntsuCheck(haipai, hais_base_2, -1);
        
        //暗刻チェック
        List<Integer> hai2_check2 = ankoCheck(haipai, hai2_check1);
        Integer mentsu_check_nokori2 = hai2_check2.size();
        Integer mentsu2 = (13 - except_toitsu * 2 - except_anko * 3 - mentsu_check_nokori2 ) / 3;
        //対子チェック
        List<Integer> hai2_check3 = toitsuCheck(haipai, hai2_check2);
        
        Integer toitsu_check_nokori2 = hai2_check3.size();
        Integer toitsu2 =  (mentsu_check_nokori2 - toitsu_check_nokori2 ) / 2;
        //塔子チェック
        List<Integer> hai2_check4 = tatsuCheck(haipai, hai2_check3);
        
        Integer tatsh_check_nokori2 = hai2_check4.size();
        Integer tatsu2 =  (toitsu_check_nokori2 - tatsh_check_nokori2 ) / 2;
        mentsu2 += except_anko;
        toitsu2 += except_toitsu;
        
        Integer shanten2 = 8 - mentsu2 * 2 - (tatsu2 + toitsu2);
        //メンツなどが6つ以上は不要なので
        if (tatsu2 + toitsu2 + mentsu2 > 5){
            shanten2 += tatsu2 + toitsu2 + mentsu2 - 5;
        }
        
        if (mentsu2 < 4 &&shanten2 == 0 && toitsu2 == 0 && tatsu2 + toitsu2 + mentsu2 >= 5){
            //面子が4つそろっておらず対子がなく、さらにメンツの数は揃っている場合はシャンテン数を1増やす。(ターツターツではシャンテン数が足りない)
            shanten2 += 1;
        }
        
        if (toitsu2 + tatsu2 == 6){
            shanten2 -= 1;
        }

        Integer mentsu = 0;
        Integer toitsu = 0;
        Integer tatsu = 0;
        Integer shanten = 0;
        if (shanten1 < shanten2){
            mentsu = mentsu1;
            toitsu = toitsu1;
            tatsu = tatsu1;
            shanten = shanten1;
        } else {
            mentsu = mentsu2;
            toitsu = toitsu2;
            tatsu = tatsu2;
            shanten = shanten2;
        }
        
        if (result == true){
//            System.out.println("面子");
//            System.out.println(mentsu);
//            System.out.println("対子");
//            System.out.println(toitsu);
//            System.out.println("ターツ");
//            System.out.println(tatsu);
        }

        return shanten;
    }
    
    private static List<Integer> ankoCheck(Map<Integer, Map<String,Integer>> haipai, List<Integer> hais) {
        int before_hai_num = 0;
        int before2_hai_num = 0;
        List<Integer> used_hais = new ArrayList<Integer>();
        
        for (int i = 0 ; i < hais.size() ; i++){
            Integer hai_num = hais.get(i);
            //1枚目・2枚目は無視
            if (
                !haipai.containsKey(before_hai_num) || 
                !haipai.containsKey(before2_hai_num) ||
                used_hais.indexOf(before_hai_num) != -1
            ){
                before2_hai_num = before_hai_num;
                before_hai_num = hai_num;
                continue;
            }
            Map<String,Integer> hai_info = haipai.get(hai_num);
            Map<String,Integer> before2_hai_info = haipai.get(before2_hai_num);
            if (
                hai_info.get("num") == before2_hai_info.get("num") &&
                hai_info.get("type") == before2_hai_info.get("type")
            ){
                used_hais.add(hai_num);
                used_hais.add(before_hai_num);
                used_hais.add(before2_hai_num);
            }
            
            before2_hai_num = before_hai_num;
            before_hai_num = hai_num;
        }
        
        Collections.sort(used_hais);
        Collections.reverse(used_hais);
        for (int k = 0 ; k < used_hais.size() ; k++){
            hais.remove(used_hais.get(k));
        }
        
        return hais;
    }
    
    
    private static List<Integer> toitsuCheck(Map<Integer, Map<String,Integer>> haipai, List<Integer> hais) {
        int before_hai_num = 0;
        List<Integer> used_hais = new ArrayList<Integer>();
        
        for (int i = 0 ; i < hais.size() ; i++){
            Integer hai_num = hais.get(i);
            //1枚目は無視
            if (
                !haipai.containsKey(before_hai_num) || 
                used_hais.indexOf(before_hai_num) != -1
            ){
                before_hai_num = hai_num;
                continue;
            }
            Map<String,Integer> hai_info = haipai.get(hai_num);
            Map<String,Integer> before_hai_info = haipai.get(before_hai_num);
            if (
                hai_info.get("num") == before_hai_info.get("num") &&
                hai_info.get("type") == before_hai_info.get("type")
            ){
                used_hais.add(hai_num);
                used_hais.add(before_hai_num);
            }
            before_hai_num = hai_num;
        }
        
        Collections.sort(used_hais);
        Collections.reverse(used_hais);
        for (int k = 0 ; k < used_hais.size() ; k++){
            hais.remove(used_hais.get(k));
        }
        
        return hais;
    }
    
    private static List<Integer> shuntsuCheck(Map<Integer, Map<String,Integer>> haipai, List<Integer> hais, Integer direction) {
        List<Integer> used_hais = new ArrayList<Integer>();
        if (direction == -1){
            Collections.reverse(hais);
        }
        for (int i = 0 ; i < hais.size() ; i++){
            Integer hai_num = hais.get(i);
            Map<String,Integer> hai_info = haipai.get(hai_num);
            //字牌は無視
            if (hai_info.get("type") == 4){
                continue;
            }
            
            //+1と+2がいたらunsetしちゃう
            Integer shuntsu1 = -1;
            Integer shuntsu2 = -1;
            for (int j = 0 ; j < hais.size() ; j++){
                Integer check_hai_num = hais.get(j);
                Map<String,Integer> check_hai_info = haipai.get(check_hai_num);
                if (
                    shuntsu1 == -1 &&
                    //未使用
                    used_hais.indexOf(check_hai_num) == -1 && 
                    hai_info.get("type") == check_hai_info.get("type") &&
                    hai_info.get("num") == check_hai_info.get("num") + 1 * direction
                ){
                   shuntsu1 = check_hai_num;
               }
                if (
                    shuntsu2 == -1 &&
                    //未使用
                    used_hais.indexOf(check_hai_num) == -1 && 
                    hai_info.get("type") == check_hai_info.get("type") &&
                    hai_info.get("num") == check_hai_info.get("num") + 2 * direction
                ){
                   shuntsu2= check_hai_num;
               }
            }
            if (shuntsu1 != -1 && shuntsu2 != -1){
                used_hais.add(hai_num);
                used_hais.add(shuntsu1);
                used_hais.add(shuntsu2);
            }
            
        }
        
        Collections.sort(used_hais);
        Collections.reverse(used_hais);
        for (int k = 0 ; k < used_hais.size() ; k++){
            hais.remove(used_hais.get(k));
        }
        
        return hais;
    }
    
    private static List<Integer> tatsuCheck(Map<Integer, Map<String,Integer>> haipai, List<Integer> hais) {
        List<Integer> used_hais = new ArrayList<Integer>();
        for (int i = 0 ; i < hais.size() ; i++){
            Integer hai_num = hais.get(i);
            Map<String,Integer> hai_info = haipai.get(hai_num);
            //字牌は無視
            if (hai_info.get("type") == 4){
                continue;
            }
            
            //+1か+2がいたらunsetしちゃう
            Integer shuntsu1 = -1;
            Integer shuntsu2 = -1;
            for (int j = 0 ; j < hais.size() ; j++){
                Integer check_hai_num = hais.get(j);
                Map<String,Integer> check_hai_info = haipai.get(check_hai_num);
                if (
                    shuntsu1 == -1 &&
                    //未使用
                    used_hais.indexOf(check_hai_num) == -1 && 
                    hai_info.get("type") == check_hai_info.get("type") &&
                    hai_info.get("num") == check_hai_info.get("num") + 1
                ){
                   shuntsu1 = check_hai_num;
               }
                if (
                    shuntsu2 == -1 &&
                    //未使用
                    used_hais.indexOf(check_hai_num) == -1 && 
                    hai_info.get("type") == check_hai_info.get("type") &&
                    hai_info.get("num") == check_hai_info.get("num") + 2
                ){
                   shuntsu2= check_hai_num;
               }
            }
            if (shuntsu1 != -1 || shuntsu2 != -1){
                used_hais.add(hai_num);
                used_hais.add(shuntsu1);
                used_hais.add(shuntsu2);
            }
            
        }
        
        Collections.sort(used_hais);
        Collections.reverse(used_hais);
        for (int k = 0 ; k < used_hais.size() ; k++){
            hais.remove(used_hais.get(k));
        }
        
        return hais;
    }

    public static List<Integer> convertHais(List<String> haisInString) {
        int[] existedHaiPai = new int[34]; // 9 + 9 + 9 + 7
        List<Integer> ret = new ArrayList<>();
        for (String haiInString: haisInString) {
            int num = haiInString.charAt(0) - 48;
            int posFactor;
            if (haiInString.endsWith("m") || haiInString.endsWith("M")) {
                posFactor = 0;
            } else if (haiInString.endsWith("p") || haiInString.endsWith("P")) {
                posFactor = 9;
            } else if (haiInString.endsWith("s") || haiInString.endsWith("S")) {
                posFactor = 18;
            } else {
                posFactor = 27;
            }

            int haiPai = posFactor * 4 + (num - 1) * 4 + existedHaiPai[posFactor + num - 1] + 1;
            ret.add(haiPai);
            existedHaiPai[posFactor + num - 1]++;
        }
        return ret;
    }

    private static void haiDisp(Map<Integer, Map<String,Integer>> haipai, List<Integer> hais) {
        //牌の表示
        String numText = "";
        for (int hai_num: hais){
            numText += "|";
            Map<String,Integer> hai_info = haipai.get(hai_num);
            if (hai_info.get("type") == 4) {
                if (hai_info.get("num") == 1){
                    numText += "東";
                } else if (hai_info.get("num") == 2){
                    numText += "南";
                } else if (hai_info.get("num") == 3){
                    numText += "西";
                } else if (hai_info.get("num") == 4){
                    numText += "北";
                } else if (hai_info.get("num") == 5){
                    numText += "　";
                } else if (hai_info.get("num") == 6){
                    numText += "發";
                } else if (hai_info.get("num") == 7){
                    numText += "中";
                }
            } else {
                if (hai_info.get("num") == 1){
                    numText += "一";
                } else if (hai_info.get("num") == 2){
                    numText += "二";
                } else if (hai_info.get("num") == 3){
                    numText += "三";
                } else if (hai_info.get("num") == 4){
                    numText += "四";
                } else if (hai_info.get("num") == 5){
                    numText += "五";
                } else if (hai_info.get("num") == 6){
                    numText += "六";
                } else if (hai_info.get("num") == 7){
                    numText += "七";
                } else if (hai_info.get("num") == 8){
                    numText += "八";
                } else if (hai_info.get("num") == 9){
                    numText += "九";
                }
            }
        }
        numText += "|";
        
        String typeText = "";
        for (int hai_num: hais){
            typeText += "|";
            Map<String,Integer> hai_info = haipai.get(hai_num);
            if (hai_info.get("type") == 1){
                typeText += "萬";
            } else if (hai_info.get("type") == 2) {
                typeText += "筒";
            } else if (hai_info.get("type") == 3) {
                typeText += "索";
            } else if (hai_info.get("type") == 4) {
                typeText += "　";
            }
        }
        typeText += "|";
        
        System.out.println(numText);
        System.out.println(typeText);
    }
    
}
