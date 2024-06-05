package org.jblib;

import org.jblib.subsystem.Subsystem;

import java.util.concurrent.TimeUnit;

public class Drivetrain extends Subsystem {


    Drivetrain() {
        super();
    }


    @Override
    public void periodic() {
        System.out.println("Subsystem Periodic");
    }
}
