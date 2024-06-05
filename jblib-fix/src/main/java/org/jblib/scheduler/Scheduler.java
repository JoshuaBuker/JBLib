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
    private final Map<Subsystem, Set<Command>> requirementMap = new LinkedHashMap<>();
    private final Map<Subsystem, Command> defaultCommands = new LinkedHashMap<>();
    private final Map<Action, Command> actions = new LinkedHashMap<>();
    private final Set<Subsystem> periodicSubsystems = new HashSet<>();
    private final Map<Subsystem, Set<Command>> activeSubsystems = new LinkedHashMap<>();
//    private final Set<Command> pendingSetupsWhile = new HashSet<>();
    private final Set<Command> activeOnCommands = new HashSet<>();
    private final Set<Command> activeDefaultCommands = new HashSet<>();
//    private final Set<Command> pendingSetupsOn = new HashSet<>();
    private final Set<Command> activeWhileCommands = new HashSet<>();
//    private final Set<Command> activeLoopsOn = new HashSet<>();

    public Scheduler() {}

//    public Map<Set<Subsystem>, Command> getRequirementMap() { return this.requirementMap; }
//    public Map<Subsystem, Command> getDefaultCommands() { return this.defaultCommands; }
//    public Set<Subsystem> getPeriodicSubsystems() { return this.periodicSubsystems; }
//    public Set<Command> getCommandsWhile() { return this.pendingSetupsWhile; }
//    public Set<Subsystem> getActiveSubsystems() { return this.activeSubsystems; }
//    public Set<Command> getCommandsOn() { return this.activeLoopsWhile; }
//    public Map<Action, Command> getActions() { return this.actions; }
//    public Set<Command> getActiveCommands() { return this.activeCommands; }

    public void registerDefaultCommand(Subsystem subsystem, Command command) { this.defaultCommands.put(subsystem, command); }
    public void registerAction(Action action, Command command) { this.actions.put(action, command); }
    public void registerPeriodic(Subsystem subsystem) {
        this.periodicSubsystems.add(subsystem);
    }

    public void registerCommand(Command command) {
        for(Subsystem subsystem : command.getRequirements()) {
            Set<Command> set = requirementMap.get(subsystem);
            if(set == null) {
                set = new HashSet<>(List.of(command));
                requirementMap.put(subsystem, set);
            } else {
                set.add(command);
            }
        }
    }

    public void runSchedulerLoop() throws InterruptedException {
        long loopStart = System.currentTimeMillis();

        // Loop through actions and add their commands activeCommands
        for(Map.Entry<Action, Command> actionEntry : actions.entrySet()) {

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
