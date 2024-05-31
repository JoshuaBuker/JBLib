package org.example;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class CameraServer {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(5000), 0);
        server.createContext("/video_feed", new MJPEGHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started at http://192.168.4.1:5000/");
    }

    static class MJPEGHandler implements HttpHandler {
        private VideoCapture camera;

        public MJPEGHandler() {
            camera = new VideoCapture(1); // Use the first camera available
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Headers headers = exchange.getResponseHeaders();
            headers.set("Content-Type", "multipart/x-mixed-replace; boundary=frame");
            exchange.sendResponseHeaders(200, 0);

            OutputStream outputStream = exchange.getResponseBody();
            Mat frame = new Mat();

            while (camera.read(frame)) {
                BufferedImage image = matToBufferedImage(frame);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", baos);
                byte[] frameBytes = baos.toByteArray();

                outputStream.write(("--frame\r\nContent-Type: image/jpeg\r\n\r\n").getBytes());
                outputStream.write(frameBytes);
                outputStream.write("\r\n".getBytes());

                // Adjust frame rate as needed (e.g., 30 FPS)
                try {
                    Thread.sleep(1000 / 60);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            outputStream.close();
            camera.release();
        }

        private BufferedImage matToBufferedImage(Mat mat) {
            int type = BufferedImage.TYPE_BYTE_GRAY;
            if (mat.channels() > 1) {
                type = BufferedImage.TYPE_3BYTE_BGR;
            }
            int bufferSize = mat.channels() * mat.cols() * mat.rows();
            byte[] b = new byte[bufferSize];
            mat.get(0, 0, b); // get all the pixels
            BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
            final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            System.arraycopy(b, 0, targetPixels, 0, b.length);
            return image;
        }
    }
}
