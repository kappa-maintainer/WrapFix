package gkappa.wrapfix;

import org.apache.commons.lang3.tuple.Pair;

public class Reset extends Pair<Integer, Integer> {
    int i;
    int ri;
    public Reset(int i, int ri) {
        this.i = i; //reset(§r) index in str
        this.ri = ri; //reset(§r) index in format
    }
    @Override
    public Integer getLeft() {
        return i;
    }

    @Override
    public Integer getRight() {
        return ri;
    }

    @Override
    public Integer setValue(Integer integer) {
        return null;
    }
}