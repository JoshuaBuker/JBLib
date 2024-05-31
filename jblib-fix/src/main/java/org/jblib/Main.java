package org.jblib;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.jblib.broker.BrokerHandler;
import org.jblib.camerastream.CameraStream;
import org.jblib.command.Command;
import org.jblib.scheduler.Scheduler;
import org.jblib.subsystem.Subsystem;

public class Main {
    public static void main(String[] args) throws MqttException {
        Subsystem drivetrain = new Drivetrain();
        Command drive = new Drive(drivetrain);

        drivetrain.register();

        System.out.println(drive.getRequirements().toString());
        System.out.println(Scheduler.getInstance().getPeriodicSubsystems().toString());
        System.out.println(Scheduler.getInstance().getRequirementMap().toString());

        Thread brokerThread = new Thread(BrokerHandler.getInstance("192.168.4.1"));
        brokerThread.start();
        System.out.println(String.format("tcp://%s:1883", "192.168.4.1"));

        Thread cameraThread = new Thread(new CameraStream(5000));
        cameraThread.start();
    }
}