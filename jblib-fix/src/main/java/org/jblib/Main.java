package org.jblib;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.jblib.action.ControlType;
import org.jblib.broker.BrokerHandler;
import org.jblib.command.Command;
import org.jblib.scheduler.Scheduler;
import org.jblib.subsystem.Subsystem;

public class Main {
    public static void main(String[] args) throws MqttException, InterruptedException {
        Scheduler scheduler = Scheduler.getInstance();

        Drivetrain drivetrain = new Drivetrain();

        drivetrain.register();
        drivetrain.setDefaultCommand(new Drive(drivetrain));

//        Thread brokerThread = new Thread(BrokerHandler.getInstance(Constants.Networking.MAIN_ADDRESS));
//        brokerThread.start();
//        System.out.printf("tcp://%s:1883%n", Constants.Networking.MAIN_ADDRESS);

        while (true) {
            scheduler.runSchedulerLoop();
        }



    }
}