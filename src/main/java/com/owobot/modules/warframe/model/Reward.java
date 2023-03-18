package com.owobot.modules.warframe.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class Reward {
    private String name;
    private String rarity;
    private double dropChance;
}
