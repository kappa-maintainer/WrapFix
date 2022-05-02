package gkappa.wrapfix;

import java.util.ArrayList;
import java.util.List;

public class CJKTextHelper {
    public static String[] Splitter(String s) {
        StringBuilder word = new StringBuilder();
        List<String> result = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if(c == ' ') {
                word.append(c);
                result.add(word.toString());
                word = new StringBuilder();
                continue;
            }
            if(isCharCJK(c)) {
                if (word.length() > 0) {
                    result.add(word.toString());
                    word = new StringBuilder();
                }
                word.append(c);
                result.add(word.toString());
                word = new StringBuilder();
                continue;
            }
            word.append(c);

        }
        result.add(word.toString());
        return  result.toArray(new String[0]);
    }

    public static boolean isCharCJK(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        Character.UnicodeScript sc = Character.UnicodeScript.of(c);
        return ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS
                || ub == Character.UnicodeBlock.VERTICAL_FORMS
                || sc == Character.UnicodeScript.HAN;


    }
}
