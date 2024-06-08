package org.jblib.scheduler;

import org.jblib.Constants;
import org.jblib.action.Action;
import org.jblib.action.ControlType;
import org.jblib.command.Command;
import org.jblib.subsystem.Subsystem;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Scheduler {
    // ======================= STATIC FIELDS =========================
    private static Scheduler instance;

    public static synchronized Scheduler getInstance() {
        if (instance == null) { instance = new Scheduler(); }
        return instance;
    }

    // ==================== INSTANCE FIELDS ===========================
    private final Map<Command, Action> commandActionMap = new LinkedHashMap<>();
    private final Map<Subsystem, Command> defaultCommands = new LinkedHashMap<>();
    private final Map<Action, Command> actionCommandMap = new LinkedHashMap<>();
    private final Set<Subsystem> periodicSubsystems = new HashSet<>();
    private final Map<Subsystem, Set<Command>> activeSubsystems = new LinkedHashMap<>();
    private final Set<Command> activeDefaultCommands = new HashSet<>();
    private final Set<Command> activeCommands = new HashSet<>();

    public Scheduler() {}

    public void registerDefaultCommand(Subsystem subsystem, Command command) { this.defaultCommands.put(subsystem, command); }
    public void registerPeriodic(Subsystem subsystem) {
        this.periodicSubsystems.add(subsystem);
    }
    public void registerCommand(Command command) {}

    public void registerAction(Action action, Command command) {
        this.actionCommandMap.put(action, command);
        this.commandActionMap.put(command, action);
    }

    public void runSchedulerLoop() throws InterruptedException {
        long loopStart = System.currentTimeMillis();

        // Loop through actions and add their commands activeCommands
        for(Map.Entry<Action, Command> actionEntry : actionCommandMap.entrySet()) {
            boolean actionState = actionEntry.getKey().checkCondition();
            if(actionState) {
                if((actionEntry.getKey().getControlType() == ControlType.ON_TRUE && !actionEntry.getKey().getPreviousValue()) || actionEntry.getKey().getControlType() == ControlType.WHILE_TRUE) {
                    for(Subsystem sub : actionEntry.getValue().getRequirements()) {
                        if(activeSubsystems.containsKey(sub)) {
                            Set<Command> commands = activeSubsystems.get(sub);
                            for(Command command : commands) {
                                if (command != actionEntry.getValue()) {
                                    command.end();
                                    commands.remove(command);
                                    activeCommands.remove(command);
                                }
                            }
                        }
                    }
                    activeCommands.add(actionEntry.getValue());
                    actionEntry.getValue().setup();
                }
            }
        }

        // Run the loop method of commands
        for (Command command : activeCommands) {
            command.loop();
        }

        // Loop through default commands
        for(Map.Entry<Subsystem,Command> entry : defaultCommands.entrySet()) {
            // Check if subsystem is being used by commands
            if(!activeSubsystems.containsKey(entry.getKey())) {
                if(activeDefaultCommands.contains(entry.getValue())) {
                    entry.getValue().loop();
                } else {
                    entry.getValue().runCommand();
                    activeDefaultCommands.add(entry.getValue());
                }
            }
        }
        // Loop through periodic
        // Loop through default commands
        for (Subsystem subsystem : periodicSubsystems) {
            subsystem.periodic();
        }

        // Remove On True Action commands from active Commands list
        for(Command command : activeCommands) {
            if(commandActionMap.get(command).getControlType() == ControlType.ON_TRUE) {
                command.end();
                activeCommands.remove(command);
            }
        }

        long timeElapsed = System.currentTimeMillis() - loopStart;

        //Check how long the previous section took, and if it was faster than loop interval, delay.
        if(timeElapsed < Constants.Scheduler.LOOP_INTERVAL_MILLISECONDS) {
            System.out.printf("Loop took %d milliseconds and will sleep for %d\n", timeElapsed, Constants.Scheduler.LOOP_INTERVAL_MILLISECONDS - timeElapsed);
            TimeUnit.MILLISECONDS.sleep(Constants.Scheduler.LOOP_INTERVAL_MILLISECONDS - timeElapsed);
        } else {
            System.out.println("Loop Overrun");
        }
    }
}
