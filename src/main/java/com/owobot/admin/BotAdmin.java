package com.owobot.admin;

import com.owobot.OwoBot;
import com.owobot.utilities.Reflectional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Set;

public class BotAdmin extends Reflectional {

    private static final Logger log = LoggerFactory.getLogger(BotAdmin.class);
    private static final AdminUser nullUser = new AdminUser();
    private final Set<AdminUser> botAdmins;

    public BotAdmin(OwoBot owoBot, Set<String> botAdmins) {
        super(owoBot);
        this.botAdmins = new LinkedHashSet<>();

        for (var admin : botAdmins) {
            try {
                this.botAdmins.add(new AdminUser(Long.parseLong(admin)));
            } catch (NumberFormatException e){
                log.warn("{} Invalid bot admin user Id, the Id has not been added to the whitelist!", admin);
            }
        }
    }

    public AdminUser getUserByID(String userId) {
        return getUserByID(Long.parseLong(userId));
    }

    public AdminUser getUserByID(long userId) {
        for (var admin : botAdmins){
            if (admin.getUserId() == userId)
                return admin;
        }

        return nullUser;
    }
}
