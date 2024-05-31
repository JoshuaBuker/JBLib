package org.jblib.camerastream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class CameraStream implements Runnable {
    static {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        nu.pattern.OpenCV.loadLocally();
    }

    private int port;
    private VideoCapture camera;

    public CameraStream(int port) {
        this.port = port;
        camera = new VideoCapture(0);
        camera.set(Videoio.CAP_PROP_FRAME_WIDTH, 320);
        camera.set(Videoio.CAP_PROP_FRAME_HEIGHT, 240);
    }

    @Override
    public void run() {
        if (!camera.isOpened()) {
            System.out.println("Error: Could not open video device.");
            return;
        }

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", new StreamHandler());
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
            System.out.println("Server started on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class StreamHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Mat frame = new Mat();
            MatOfByte matOfByte = new MatOfByte();

            while (true) {
                if (camera.read(frame)) {
                    Imgcodecs.imencode(".jpg", frame, matOfByte);
                    byte[] imageData = matOfByte.toArray();

                    exchange.getResponseHeaders().set("Content-Type", "image/jpeg");
                    exchange.sendResponseHeaders(200, imageData.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(imageData, 0, imageData.length);
                    os.close();
                } else {
                    System.out.println("Failed to capture image");
                    break;
                }
            }
        }
    }
}

