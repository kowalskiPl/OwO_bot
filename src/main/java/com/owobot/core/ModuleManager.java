package com.owobot.core;

import com.owobot.OwoBot;
import com.owobot.modules.Module;
import com.owobot.utilities.Reflectional;

import java.util.LinkedHashSet;
import java.util.Set;

public class ModuleManager extends Reflectional {
    private final Set<Module> modules;

    public ModuleManager(OwoBot owoBot) {
        super(owoBot);
        modules = new LinkedHashSet<>();
    }

    public void loadModule(Module module) {
        modules.add(module);
        owoBot.getCommandCache().loadCommandSet(module.getName(), module.getCommands());
        owoBot.getCommandListenerStack().registerListener(module.getCommandListeners());
    }

    public boolean isModuleLoaded(Module module) {
        return modules.contains(module);
    }
}
