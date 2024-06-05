package org.jblib.broker;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.HashMap;

public class BrokerHandler implements Runnable{
    // ================================= Static =========================================
    private static BrokerHandler instance;

    public static synchronized BrokerHandler getInstance(String brokerAddress) throws MqttException {
        if (instance == null) { instance = new BrokerHandler(String.format("tcp://%s:1883", brokerAddress)); }
        return instance;
    }

    // ================================== Instance =====================================
    MemoryPersistence persistence = new MemoryPersistence();
    private final String clientID = "RpiBot";
    private final String brokerAddress;
    private final MqttClient client;


    public BrokerHandler(String brokerAddress) throws MqttException {
        this.brokerAddress = brokerAddress;
        client = new MqttClient(brokerAddress, clientID, persistence);
        client.connect();
    }

    public void publish(String topic, String content) throws MqttException {
        MqttMessage message = new MqttMessage(content.getBytes());
        message.setQos(2);
        this.client.publish(topic, message);
    }

    public void subscribe(String topic) throws MqttException {
        this.client.subscribe(topic, (topic1, msg) -> {
            System.out.println(new String(msg.getPayload()));
        });
    }

    public void closeClient() throws MqttException {
        this.client.close();
    }

    public MqttClient getClient() {
        return this.client;
    }

    public String getBrokerAddress() {
        return this.brokerAddress;
    }

    @Override
    public void run() {
        // Constantly Update Values to an internal data structure. Prob a hashmap.
        try {
            this.subscribe("test/topic");

            synchronized (this) {
                this.wait();
            }
        } catch (MqttException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public HashMap<String, String> getMap() {
        return new HashMap<>();
    }
}
