package unused;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.File;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.*;

public class PaipuParser {

    public static final String PATH = System.getProperty("user.home");

    public static void main(String[] args) throws Exception {

        Map<String, String> input = new LinkedHashMap<>();
        input.put("a", "a1");
        input.put("b", "b1");
        System.out.println(StringEscapeUtils.escapeJson("xx:" + JSON.toJSONString(input)));


        // parse_20_21
        parse_20_21();

    }

    public static void parse_20_21() throws Exception {
        Scanner scanner = new Scanner(new File(PATH + "/Documents/Mleague数据/mleague_20-21_v2.txt"));
        PrintWriter pw = new PrintWriter(new File(PATH + "/Documents/Mleague数据/mleague_20-21_v3.txt"));
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.contains("<title>")) {
                String title = line.substring(line.indexOf("<title>")+7, line.indexOf("｜"));
                title = title.replaceAll("　牌譜　", "/");
                title = title.replaceAll("Mリーグ", "Mリーグ ");
                System.out.println(title);
                String proInfo;
                if (line.contains("東1")) {
                    proInfo = line.substring(line.lastIndexOf("content=\"")+9, line.indexOf("東1"));
                    proInfo = proInfo.replaceAll(".*出場選手  ", "");
                    proInfo = "C1 " + proInfo;
                } else if (line.contains("東１局")) {
                    proInfo = line.substring(line.lastIndexOf("content=\"")+9, line.indexOf("東１局"));
                    proInfo = proInfo.replaceAll(".*出場選手  ", "");
                    proInfo = "C2 " + proInfo;
                } else {
                    proInfo = scanner.nextLine();
                    proInfo = "C3 " + proInfo;
                }
                proInfo = proInfo.replaceAll(".+️\uD83D\uDE94", "");
                proInfo = proInfo
                        .replaceAll("プロ", "")
                        .replaceAll("EX風林火山", "")
                        .replaceAll("KONAMI麻雀格闘.楽部", "")
                        .replaceAll("セガサミーフェニックス", "")
                        .replaceAll("U-N....+Pirates", "")
                        .replaceAll("KADOKAWAサクラナイツ", "")
                        .replaceAll("TEAM　RAIDEN", "")
                        .replaceAll("赤坂ドリブンズ", "")
                        .replaceAll("渋谷ABEMAS", "")
                        .replaceAll("\\s+", " ")
                        .trim();

                System.out.println(proInfo);
            }
        }
        scanner.close();
        pw.close();
    }

    public static void parse_19_20() throws Exception {

        Scanner scanner = new Scanner(new File(PATH + "/Documents/Mleague数据/mleague4.txt"));
        PrintWriter pw = new PrintWriter(new File(PATH + "/Documents/Mleague数据/mleague5.txt"));
        Stack<String> stack = new Stack<>();
        List<String> results = new ArrayList<>();
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.contains("牌譜")) {
                pw.println(line.replaceAll(" 牌譜　", " "));
            } else if (line.contains("pt")) {
                results.add(line.replaceAll(".着.", "").replaceAll("\t", " "));
                if (results.size() == 4) {
                    pw.println(String.join("; ", results));
                    pw.println();
                    results.clear();
                }
//                  results.add(line.replaceAll("\t", " "));
//                if (results.size() == 4) {
//                    pw.println(String.join("\n", results));
//                    pw.println();
//                    results.clear();
//                }
            } else if (line.contains("tenhou")) {
                stack.push(URLDecoder.decode(line, "utf-8"));
            } else if (line.contains("本場")) {
                pw.println("/** " + line.replaceAll("\t", " "));
                pw.println(stack.pop());
                pw.println("*/");
                pw.println();
            } else if (line.isEmpty()) {
                pw.println();
                pw.flush();
            } else {
                System.out.println(line);
            }
        }
        scanner.close();
        pw.close();
    }
}
