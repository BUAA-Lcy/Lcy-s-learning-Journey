
public class Dog {
    Dog (int year){
        System.out.println("i'm "+year+ " years old");
    }
    Dog(int year,String name){
        this(year);
        System.out.println("my name is "+name);

    }
    void bark(int time){
        System.out.println("wang wang");
    }
    void bark(String hehe){
        System.out.println("wu~ ");
    }
    void bark(int time,String haha){
        System.out.println("dog");
    }
    void bark(String haha,int time){
        System.out.println("dog");
    }

    public static void main(String[] args){
        Dog yang =new Dog(6,"duxunchi");
        yang.bark(1);
        yang.bark("haha");
        yang.bark(1,"haha");
        yang.bark("haha",1);
    }

}