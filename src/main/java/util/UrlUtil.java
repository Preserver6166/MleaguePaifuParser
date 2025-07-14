package util;

import java.io.File;
import java.io.PrintWriter;

import static official.OfficialConstants.*;

public class UrlUtil {

    /**
     * @throws Exception
     * 生成官网的牌谱URL
     */
    public static void generatePaifuUrl() throws Exception {

        PrintWriter pw = new PrintWriter(new File(PATH + "/Documents/Mleague数据/official_urls.txt"));

        for (int i = 0; i < SEASONS.length; i++) {
            String season = SEASONS[i];
            int gameNums = GAMENUMS[i];
            for (int j = 1; j <= gameNums; j++) {
                String gameNumString = j < 10 ? ("0" + j) : ("" + j);
                pw.println(String.format(season, gameNumString, 1));
                pw.println(String.format(season, gameNumString, 2));
                if (i == 1) { //2018赛季决赛出现过一天打三个半庄
                    pw.println(String.format(season, gameNumString, 3));
                }
            }
            pw.flush();
        }

        pw.close();
    }

}
