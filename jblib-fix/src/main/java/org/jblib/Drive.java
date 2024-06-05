package org.jblib;

import org.jblib.command.Command;
import org.jblib.subsystem.Subsystem;

public class Drive extends Command {

    Drive(Subsystem... requirements) {
        super(requirements);
    }

    @Override
    public void setup() {
        System.out.println("Setup Print Statement");
    }

    @Override
    public void loop() {
        System.out.println("Loop Print Statement");
    }
}
