class cook{
    String name;
    int sex;
    public void begincook(){
        System.out.println(this.name+"开始做饭");
    }

}
class buyer{
    String name;
    int sex;
    public void begincook(){
        System.out.println(this.name+"买菜辣");
    }
}

public class Main {
    public static void main(String[] args) {
        buyer I=null,mama=null,gugu=null;
        I=new buyer();mama=new buyer();gugu=new buyer();
        I.name="Lcy";mama.name="mama";gugu.name="gagu";
        cook baba=null,shenshen=null;
        baba=new cook();shenshen=new cook();
        baba.name="baba";shenshen.name="shenshen";
        I.begincook();
        mama.begincook();
        gugu.begincook();
        baba.begincook();
        shenshen.begincook();



    }
}