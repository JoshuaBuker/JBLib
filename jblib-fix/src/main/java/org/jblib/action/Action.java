package org.jblib.action;

import org.jblib.command.Command;
import org.jblib.controller.GenericHID;
import org.jblib.scheduler.Scheduler;

public class Action {
    private GenericHID controller;
    private int buttonID;
    private ControlType type;
    private boolean previousValue;

    public Action(GenericHID controller, int buttonID) {
        this.controller = controller;
        this.buttonID = buttonID;
    }

    public ControlType getControlType() { return this.type; }
    public boolean getPreviousValue() { return this.previousValue; }
    public GenericHID getController() { return this.controller; }
    public int getButtonID() { return this.buttonID; }

    public void onTrue(Command command) {
        Scheduler.getInstance().registerAction(this, command);
    }
    public void whileTrue(Command command) {
        Scheduler.getInstance().registerAction(this, command);
    }

    public boolean checkCondition() {
        return this.controller.getButtonState(this.buttonID);
    }
    public void setPreviousValue(boolean val) { this.previousValue = val; }


}
