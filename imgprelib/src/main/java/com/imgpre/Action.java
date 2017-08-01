package com.imgpre;

/**
 * Created by ola on 2017/7/27.
 */

public class Action {
    public static final int AC_COMMENT = 0;
    public static final int AC_OTHER = -1;
    private int currentAction = AC_OTHER;

    public Action(int action) {
        this.currentAction = action;
    }

    public int getAction() {
        return currentAction;
    }
}
