public class Person {
    String name;
    int age;
    String sex;
    Person(String name, int age, String sex){
        this.age=age;
        this.name=name;
        this.sex=sex;
    }
    public void setAge(int a){
        this.age=a;
    }
    public int getAge(){
        return age;
    }
    void work(){
        System.out.println("working ");
    }
    void showAge(){
        System.out.println("i'm "+age);
    }

}
class TestPerson {
    public static void main(String []args){
        Person x=new Person("xiaosong",18,"boy");
        x.setAge(19);
        System.out.println(x.name);
        System.out.println(x.getAge());
        Person y=new Person("panhongyang",18,"girl");
        y.setAge(65);
        System.out.println(y.getAge());
    }


}
