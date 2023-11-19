#include<stdio.h>
typedef struct{
	int value;
	struct process *L;
}semaphore;
int main(){
	semaphore a={1,NULL};
	semaphore b ; b.value = 2;
	
	printf("%d\n%d",a.value,b.value); 
	
	
	
	
	return 0;
} 
