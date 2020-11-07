package me.bytebeats.tool;

import com.github.promeg.pinyinhelper.Pinyin;

/**
 * @author <a href="https://github.com/bytebeats">bytebeats</a>
 * @email <happychinapc@gmail.com>
 * @since 2020/8/19 16:48
 */
public class PinyinUtils {
    public static String toPinyin(String input) {
        StringBuilder pinyins = new StringBuilder();
        for (char ch : input.toCharArray()) {
            char[] pys = Pinyin.toPinyin(ch).toLowerCase().toCharArray();
            if (pys.length > 0) {
                pys[0] = Character.toUpperCase(pys[0]);
                pinyins.append(String.valueOf(pys));
            }
        }
        return pinyins.toString();
    }
}
