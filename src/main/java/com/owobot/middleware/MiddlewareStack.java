package com.owobot.middleware;

import com.owobot.commands.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MiddlewareStack {

    private static final Logger log = LoggerFactory.getLogger(MiddlewareStack.class);
    private final List<MiddlewareContainer> middlewareList;

    private Command command;

    private int index = -1;

    public MiddlewareStack() {
        this.middlewareList = new ArrayList<>();
    }

    public void buildMiddlewares(Command command, MiddlewareHandler handler) {
        this.command = command;
        List<String> args = command.getMiddlewares();
        if (args.isEmpty()) {
            log.warn("Command: " + command.getName() + " has no middlewares defined. Validation will be skipped");
            return;
        }

        for (String arg : args) {
            String[] split = arg.split(":");
            Middleware middlewareReference = handler.getMiddleware(split[0]);
            if (middlewareReference == null) {
                log.warn("Unrecognized or disabled middle: " + split[0]);
                continue;
            }

            if (split.length == 1){
                middlewareList.add(new MiddlewareContainer(middlewareReference));
                continue;
            }
            middlewareList.add(new MiddlewareContainer(middlewareReference, split[1].split(",")));
        }
    }

    public boolean next() {
        if (index == -1)
            index = middlewareList.size();

        if (index == 0)
            return true;

        MiddlewareContainer middlewareContainer = middlewareList.get(--index);
        log.info("Executing middleware {}", middlewareContainer.getMiddlewareReference());
        return middlewareContainer.getMiddlewareReference().handle(command, this, middlewareContainer.getArgs());
    }
}
