package org.jblib;

import org.jblib.command.Command;
import org.jblib.subsystem.Subsystem;

public class Drive extends Command {

    Drive(Subsystem... requirements) {
        super(requirements);

    }
}
