package com.blakebr0.ironjetpacks;
public class TagTest2 {
    public static void main(String[] args) {
        String s = "tag:c:ingots/copper";
        String[] parts = s.split(":");
        System.out.println(java.util.Arrays.toString(parts));
    }
}
