package org.jblib.controller;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.jblib.Constants;
import org.jblib.broker.BrokerHandler;
import org.jblib.scheduler.Scheduler;

public class GenericHID {
    private int port;

    public GenericHID(int port) {
        this.port = port;
    }

    public double getAxisValue(int axis) throws MqttException {
        return 0.0; // TODO: Make Data Structure in Broker Class
    }

    public boolean getButtonState(int buttonID) {
        return false; // TODO: Make Data Structure in Broker Class
    }
}
