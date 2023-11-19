import  java.util.*;
import  java.io.*;

class Music{
    String name;
    String type;
    Music(String name,String type){
        this.name=name;
        this.type=type;
    }
    public void print2(){
        System.out.println("this Music's name is "+this.name);
        System.out.println("this Music's type is "+this.type);
    }

}
class Phone{
    String brand;
    String type;
    Phone(String brand,String type){
        this.brand=brand;
        this.type=type;
    }
    public void print1(){
        System.out.println("this phone's brand is "+this.brand);
        System.out.println("this phone's type is "+this.type);
    }


}
public class MusicPhone{
    public static void main(String args[]){
        Phone a=new Phone("sanxing","Typec");
        Music b=new Music("blue","lightmusic");
        a.print1();
        b.print2();

    }

}
