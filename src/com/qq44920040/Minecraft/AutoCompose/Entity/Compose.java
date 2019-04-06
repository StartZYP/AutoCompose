package com.qq44920040.Minecraft.AutoCompose.Entity;

public class Compose {

    public Compose(String disPlayerKey, String loreKey, int stack) {
        DisPlayerKey = disPlayerKey;
        LoreKey = loreKey;
        Stack = stack;
    }

    public String DisPlayerKey;
    public String LoreKey;
    public int Stack;

    @Override
    public String toString() {
        return "Compose{" +
                "DisPlayerKey='" + DisPlayerKey + '\'' +
                ", LoreKey='" + LoreKey + '\'' +
                ", Stack=" + Stack +
                '}';
    }

}
