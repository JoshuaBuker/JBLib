package org.jblib.scheduler;

import org.jblib.action.Action;
import org.jblib.command.Command;
import org.jblib.subsystem.Subsystem;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Scheduler {
    // ======================= STATIC FIELDS =========================
    private static Scheduler instance;

    public static synchronized Scheduler getInstance() {
        if (instance == null) { instance = new Scheduler(); }
        return instance;
    }

    // ==================== INSTANCE FIELDS ===========================
    private final Map<Set<Subsystem>, Command> requirementMap = new LinkedHashMap<>();
    private final Map<Subsystem, Command> defaultCommands = new LinkedHashMap<>();
    private final Map<Set<Action>, Command> actions = new LinkedHashMap<>();
    private final Set<Subsystem> periodicSubsystems = new HashSet<>();
    private final Set<Command> pendingSetups = new HashSet<>();
    private final Set<Command> activeLoops = new HashSet<>();


    public Scheduler() {}

    public Map<Set<Subsystem>, Command> getRequirementMap() { return requirementMap; }
    public Map<Subsystem, Command> getDefaultCommands() { return defaultCommands; }
    public Set<Subsystem> getPeriodicSubsystems() { return periodicSubsystems; }
    public Map<Set<Action>, Command> getActions() { return actions; }
    public Set<Command> getPendingSetups() { return pendingSetups; }
    public Set<Command> getActiveLoops() { return activeLoops; }

    public void registerPeriodic(Subsystem subsystem) {
        this.periodicSubsystems.add(subsystem);
    }

    public void registerDefaultCommand(Subsystem subsystem, Command command) {
        this.defaultCommands.put(subsystem, command);
    }

    public void registerCommand(Command command) {
        this.requirementMap.put(command.getRequirements(), command);
    }

}
