package gkappa.wrapfix;

import com.ibm.icu.text.BreakIterator;

import java.util.ArrayList;
import java.util.List;

public class CJKTextHelper {
    public static String[] Splitter(String s) {
        WrapFix.BREAK_ITERATOR.setText(s);
        WrapFix.BREAK_ITERATOR.first();
        List<String> result = new ArrayList<>();
        int next, current;
        while (WrapFix.BREAK_ITERATOR.current() != BreakIterator.DONE) {
            current = WrapFix.BREAK_ITERATOR.current();
            next = WrapFix.BREAK_ITERATOR.next();
            if (next > 0)
                result.add(s.substring(current, next));
            else
                break;
        }
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
