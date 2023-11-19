import java.io.DataInputStream;
import java.io.IOException;

/**
 * @author Yangtze
 * @version 1.0
 * @date 2022/11/24 22:50
 * Ϊ�˱�֤һ�ο���˵��䣬����ʹ�ö��̣߳����߳�������
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
