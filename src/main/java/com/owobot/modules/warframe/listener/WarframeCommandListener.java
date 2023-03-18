package com.owobot.modules.warframe.listener;

import com.owobot.commands.Command;
import com.owobot.commands.CommandListener;
import com.owobot.modules.warframe.AllRewardsDatabase;

import java.io.IOException;

public class WarframeCommandListener implements CommandListener {
    private final AllRewardsDatabase rewardsDatabase;

    public WarframeCommandListener() {
        try {
            rewardsDatabase = new AllRewardsDatabase();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean onCommand(Command command) {
        return false;
    }
}
