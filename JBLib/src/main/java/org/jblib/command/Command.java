package org.jblib.command;

import org.jblib.scheduler.Scheduler;
import org.jblib.subsystem.Subsystem;

import java.util.*;

public class Command {
    private final Set<Subsystem> requirements = new HashSet<>();

    public void setup() {} // Runs once on command initialization
    public void loop() {} // Runs as long as isFinished == False
    public void end() {} // Runs once command ends or is interrupted

    protected Command(Subsystem... requirements) {
        this.requirements.addAll(Arrays.asList(requirements));
        Scheduler.getInstance().registerCommand(this);
    }

    public boolean isFinished() {
        return false;
    }

    public Set<Subsystem> getRequirements() {
        return requirements;
    }
}
