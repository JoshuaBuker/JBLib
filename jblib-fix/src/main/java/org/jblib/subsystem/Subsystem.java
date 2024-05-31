package org.jblib.subsystem;

import org.jblib.command.Command;
import org.jblib.scheduler.Scheduler;

public class Subsystem {
    Command defaultCommand;

    protected Subsystem() {}

    public void periodic() {}

    public void setDefaultCommand(Command command) {
        this.defaultCommand = command;
        Scheduler.getInstance().registerDefaultCommand(this, command);
    }

    public Command getDefaultCommand() {
        return this.defaultCommand;
    }

    public void register() {
        Scheduler.getInstance().registerPeriodic(this);
    }
}
