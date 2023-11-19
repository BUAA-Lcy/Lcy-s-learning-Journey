class AAAA {
    private int p = 10;//new的时候赋值一次

    public AAAA(){
        System.out.println(p);//先执行 private int p = 10在执行 System.out.println(p)
        //结果：10
        this.p = 910;//这里在赋值一次
        System.out.println(p);
        //结果：910
    }

    public static void main(String[] args) {
        new AAAA();
    }
}
