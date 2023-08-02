package com.owobot.core;

import com.owobot.OwoBot;
import com.owobot.modules.Module;
import com.owobot.utilities.Reflectional;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
        owoBot.getLog().info("Loaded module: {}", module.getNameUserFriendly());
    }

    public boolean isModuleLoaded(Module module) {
        return modules.contains(module);
    }

    public boolean isModuleLoaded(String name) {
        return modules.stream().anyMatch(module -> module.getName().equals(name));
    }

    public Set<String> getModuleNames() {
        return modules.stream().map(Module::getNameUserFriendly).collect(Collectors.toSet());
    }

    public Set<Module> getModules() {
        return Set.copyOf(modules);
    }
}
