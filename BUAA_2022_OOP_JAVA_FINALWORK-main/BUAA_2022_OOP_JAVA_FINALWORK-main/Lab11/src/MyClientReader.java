import java.io.DataInputStream;
import java.io.IOException;

/**
 * @author Yangtze
 * @version 1.0
 * @date 2022/11/24 22:59
 */

public class MyClientReader extends Thread{
    private DataInputStream dis;
    public MyClientReader(DataInputStream dis) {
        this.dis = dis;
    }
    public void run(){
        String info;
        try{
            while(true){
                info = dis.readUTF();
                System.out.println("�Է�˵��" + info);
                if(info.equals("bye")){
                    System.out.println("�Է����ߣ������˳�");
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
