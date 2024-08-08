package official;

import java.util.Arrays;
import java.util.Iterator;

import static official.Constants.*;

/**
 * 牌谱名称的迭代器
 */
public class OfficialGameFileIterator implements Iterable<OfficialGameInfo.OfficialGameInfoBuilder> {

    @Override
    public Iterator<OfficialGameInfo.OfficialGameInfoBuilder> iterator() {
        return new Iterator<>() {

            private int cur = -1;

            @Override
            public boolean hasNext() {
                return cur < Arrays.stream(GAMENUMS).sum() - 1;
            }

            @Override
            public OfficialGameInfo.OfficialGameInfoBuilder next() {
                cur++;
                int tempCur = cur;
                for (int i = 0; i < GAMENUMS.length; i++) {
                    if (tempCur > GAMENUMS[i] - 1) {
                        tempCur = tempCur - GAMENUMS[i];
                    } else {
                        String season = SEASONS[i];
                        int gameNum;
                        int index;
                        if (i == 1) {
                            gameNum = tempCur / 3 + 1;
                            index = tempCur % 3 + 1;
                        } else {
                            gameNum = tempCur / 2 + 1;
                            index = tempCur % 2 + 1;
                        }
                        String gameNumString;
                        if (gameNum < 10) {
                            gameNumString = "000" + gameNum;
                        } else if (gameNum < 100) {
                            gameNumString = "00" + gameNum;
                        } else if (gameNum < 1000) {
                            gameNumString = "0" + gameNum;
                        } else {
                            gameNumString = "" + gameNum;
                        }
                        OfficialGameInfo.OfficialGameInfoBuilder officialGameInfoBuilder = OfficialGameInfo.builder();
                        officialGameInfoBuilder.fileName(String.format(season, gameNumString, index));
                        officialGameInfoBuilder.season(SEASON_NAMES[i]);
                        officialGameInfoBuilder.seasonIndex(i);
                        return officialGameInfoBuilder;
                    }
                }
                return null; // should not reach here
            }
        };
    }

}
