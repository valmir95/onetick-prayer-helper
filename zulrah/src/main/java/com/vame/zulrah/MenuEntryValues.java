package com.vame.zulrah;

public class MenuEntryValues {
    //String option, String target, int identifier, int opcode, int param1, int param2, boolean forceLeftClick
    private String option;
    private String target;
    private int identifier;
    private int opcode;
    private int param1;
    private int param2;
    private boolean forceLeftClick;

    public MenuEntryValues(String option, String target, int identifier, int opcode, int param1, int param2, boolean forceLeftClick) {
        this.option = option;
        this.target = target;
        this.identifier = identifier;
        this.opcode = opcode;
        this.param1 = param1;
        this.param2 = param2;
        this.forceLeftClick = forceLeftClick;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public int getOpcode() {
        return opcode;
    }

    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }

    public int getParam1() {
        return param1;
    }

    public void setParam1(int param1) {
        this.param1 = param1;
    }

    public int getParam2() {
        return param2;
    }

    public void setParam2(int param2) {
        this.param2 = param2;
    }

    public boolean isForceLeftClick() {
        return forceLeftClick;
    }

    public void setForceLeftClick(boolean forceLeftClick) {
        this.forceLeftClick = forceLeftClick;
    }
}
