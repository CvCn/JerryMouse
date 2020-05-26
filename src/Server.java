import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 * 服务器端程序：
 *
 * 1. 监听一端口，等待客户接入； 2. 一旦有客户接入，就构造一个Socket会话对象； 3. 将这个会话交给线程处理，然后主程序继续监听。
 *
 * @author OKJohn
 * @version 1.0
 */

public class Server extends ServerSocket {

    public Server(int serverPort) throws IOException {
        // 用指定的端口构造一个ServerSocket
        super(serverPort);
        try {
            while (true) {
                // 监听一端口，等待客户接入
                Socket socket = accept();
                // 将会话交给线程处理
                new ServerThread(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(); // 关闭监听端口
        }
    }

    // inner-class ServerThread
    class ServerThread extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        // Ready to conversation
        public ServerThread(Socket s) throws IOException {
            this.socket = s;
            // 构造该会话中的输入输出流
            in = new BufferedReader(new InputStreamReader(socket
                    .getInputStream(), "GB2312"));
            out = new PrintWriter(socket.getOutputStream(), true);
            start();
        }

        // Execute conversation
        public void run() {
            try {

                // Communicate with client until "bye " received.
                while (true) {
                    // 通过输入流接收客户端信息
                    String line = in.readLine();
                    if (line == null || "".equals(line.trim())) { // 是否终止会话
                        break;
                    }
                    System.out.println("Received   message: " + line);

                }

                String a = "{\"status\":\"ok\"}";

                out.println("HTTP/1.0 200 OK");// 返回应答消息,并结束应答
                out.println("Content-Type:application/json");
                out.println("Content-Length:" + a.length());// 返回内容字节数
                out.println("Date:" + new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH).format(new Date()));//返回日期
                out.println();// 根据 HTTP 协议, 空行将结束头信息

                out.write(a);
                out.flush();
                out.close();
                in.close();
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    // main method
    public static void main(String[] args) throws IOException {
        new Server(2088);
    }
}