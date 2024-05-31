package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class VideoStreamViewer {

    private JFrame frame;
    private JLabel imageLabel;
    private JLabel fpsLabel;
    private String streamHost;
    private int streamPort;

    public VideoStreamViewer(String streamHost, int streamPort) {
        this.streamHost = streamHost;
        this.streamPort = streamPort;
        frame = new JFrame("Video Stream Viewer");
        imageLabel = new JLabel();
        fpsLabel = new JLabel("FPS: 0", SwingConstants.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(imageLabel, BorderLayout.CENTER);
        frame.add(fpsLabel, BorderLayout.SOUTH);
        frame.setSize(1280, 720);
        frame.setVisible(true);
    }

    public void start() {
        Timer timer = new Timer(0, (e) -> {
            long lastTime = System.nanoTime();
            int frames = 0;

            try {
                BufferedImage image = fetchImageFromStream();
                if (image != null) {
                    ImageIcon imageIcon = new ImageIcon(image);
                    imageLabel.setIcon(imageIcon);
                    frames++;
                }

                long currentTime = System.nanoTime();
                if (currentTime - lastTime >= 1_000_000_000) { // Update every second
                    int fps = frames;
                    frames = 0;
                    lastTime = currentTime;
                    fpsLabel.setText("FPS: " + fps);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        timer.setDelay(0); // Set to 0 for maximum refresh rate
        timer.start();
    }


    private BufferedImage fetchImageFromStream() throws IOException {
        URL url = new URL("http://" + streamHost + ":" + streamPort + "/stream.jpg");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.connect();

        try (InputStream inputStream = connection.getInputStream()) {
            return ImageIO.read(inputStream);
        }
    }

    public static void main(String[] args) {
        String streamHost = "192.168.4.1"; // Replace with your Raspberry Pi IP
        int streamPort = 5000; // Replace with your stream port
        VideoStreamViewer viewer = new VideoStreamViewer(streamHost, streamPort);
        viewer.start();
    }
}
