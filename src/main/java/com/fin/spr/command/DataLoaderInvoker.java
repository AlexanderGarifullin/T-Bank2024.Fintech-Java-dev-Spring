package com.fin.spr.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;

@Component
@Slf4j
public class DataLoaderInvoker {

    private final ExecutorService fixedThreadPool;
    private final List<DataLoaderCommand> commands;

    @Autowired
    public DataLoaderInvoker(ExecutorService fixedThreadPool, List<DataLoaderCommand> list) {
        this.fixedThreadPool = fixedThreadPool;
        this.commands = list;
    }

    public void addCommand(DataLoaderCommand dataLoaderCommand) {
        commands.add(dataLoaderCommand);
    }

    public void executeCommands() {
        commands.forEach(command -> fixedThreadPool.submit(() -> {
            try {
                command.execute();
            } catch (Exception e) {
                log.error("Error executing initialization command: {}", e.getMessage(), e);
            }
        }));
        log.info("Data initialization completed with invokeAll.");
    }
}
