package com.owobot.modules.warframe.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@RequiredArgsConstructor
public class Reward implements Comparable<Reward> {
    private String name;
    private String rarity;
    private double dropChance;

    @Override
    public int compareTo(@NotNull Reward o) {
        return Double.compare(dropChance, o.getDropChance());
    }
}
