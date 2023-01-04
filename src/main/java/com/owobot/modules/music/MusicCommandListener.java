package com.owobot.modules.music;

import com.owobot.OwoBot;
import com.owobot.commands.Command;
import com.owobot.commands.CommandListener;
import com.owobot.utilities.Reflectional;

public class MusicCommandListener extends Reflectional implements CommandListener {
    public MusicCommandListener(OwoBot owoBot) {
        super(owoBot);
    }

    @Override
    public boolean onCommand(Command command) {
        return false;
    }
}
