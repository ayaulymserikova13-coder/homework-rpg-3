package com.narxoz.rpg.hero;


public interface Hero {
    String getName();
    int getPower();
    void receiveDamage(int amount);
    boolean isAlive();
}
