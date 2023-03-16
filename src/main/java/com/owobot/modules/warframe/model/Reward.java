package com.owobot.modules.warframe.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Reward {
    private String name;
    private String rarity;
    private double dropChance;
}
