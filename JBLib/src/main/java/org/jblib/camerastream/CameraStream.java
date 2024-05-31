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

public class CameraStream implements Runnable {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private int port;

    public CameraStream(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", new StreamHandler());
            server.setExecutor(null);
            server.start();
            System.out.println("Server started on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class StreamHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            VideoCapture camera = new VideoCapture(0);
            camera.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);
            camera.set(Videoio.CAP_PROP_FRAME_HEIGHT, 480);

            if (!camera.isOpened()) {
                System.out.println("Error: Could not open video device.");
                return;
            }

            Mat frame = new Mat();
            MatOfByte matOfByte = new MatOfByte();
            while (camera.read(frame)) {
                Imgcodecs.imencode(".jpg", frame, matOfByte);
                byte[] imageData = matOfByte.toArray();

                t.getResponseHeaders().set("Content-Type", "image/jpeg");
                t.sendResponseHeaders(200, imageData.length);
                OutputStream os = t.getResponseBody();
                os.write(imageData, 0, imageData.length);
                os.close();
            }
            camera.release();
        }
    }
}
