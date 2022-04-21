package gkappa.wrapfix;

import java.util.ArrayList;
import java.util.List;

public class StringSplitter {
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
            if(Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN) {
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
}
