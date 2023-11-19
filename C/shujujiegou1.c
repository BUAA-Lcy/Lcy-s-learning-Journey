#include<stdio.h>
int index(char s[],char t[])
{
	int i,j,k;
	for(i=0;s[i]!='\0';i++)
	{
		for(j=i,k=0;t[k]!="\0"&&s[j]==t[k];j++,k++);
		if(t[k]=='\0')return i;
	}
	return -1;
 } 
 int main()
 {
 	char s[100]="helloabcdello";
 	char t[100]="abc";
 	printf("%d",index(s,t));
 	
 	
 	return 0;
 }
