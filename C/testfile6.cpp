#include<stdio.h>
int getint(){
    int n = 0;
    scanf("%d",&n);
    return n;
}
int main(){
	int a = 0;
	a = getint();
	printf("%d",a);
    return 0;

} 
