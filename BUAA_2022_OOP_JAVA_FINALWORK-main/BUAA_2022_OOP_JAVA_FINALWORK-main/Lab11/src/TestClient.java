import javax.xml.crypto.Data;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @author Yangtze
 * @version 1.0
 * @date 2022/11/24 22:36
 */

public class TestClient {
    public static void main(String[] args) {
        try {
            Socket s1 = new Socket("10.193.172.222", 8088);
            InputStream is = s1.getInputStream();
            DataInputStream dis = new DataInputStream(is);

            OutputStream os = s1.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);

            new MyClientReader(dis).start();
            new MyClientWriter(dos).start();

        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("网络连接异常，程序退出");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
