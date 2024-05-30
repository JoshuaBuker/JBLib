package org.jblib;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.jblib.Broker.BrokerHandler;
import org.jblib.command.Command;
import org.jblib.scheduler.Scheduler;
import org.jblib.subsystem.Subsystem;

public class Main {
    public static void main(String[] args) throws MqttException {
        Subsystem drivetrain = new Drivetrain();
        Command drive = new Drive(drivetrain);
        BrokerHandler broker = BrokerHandler.getInstance("192.168.4.1");

        drivetrain.register();

        System.out.println(drive.getRequirements().toString());
        System.out.println(Scheduler.getInstance().getPeriodicSubsystems().toString());
        System.out.println(Scheduler.getInstance().getRequirementMap().toString());

        Thread brokerThread = new Thread(broker);
        brokerThread.start();
        System.out.println(String.format("tcp://%s:1883", "192.168.4.1"));
    }
}