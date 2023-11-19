import java.io.DataInputStream;
import java.io.IOException;

/**
 * @author Yangtze
 * @version 1.0
 * @date 2022/11/24 22:50
 * 为了保证一次可以说多句，可以使用多线程，该线程用来读
 */

public class MyServerReader extends Thread{
    private DataInputStream dis;
    public MyServerReader(DataInputStream dis){
        this.dis = dis;
    }

    public void run(){
        String info;
        try{
            while(true){
                info = dis.readUTF();
                System.out.println("对方说：" + info);
                if(info.equals("bye")){
                    System.out.println("对方下线，程序退出");
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
