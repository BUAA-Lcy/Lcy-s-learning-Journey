#include<stdio.h>
#include<string.h>
int main()
{
	char s[200];
	scanf("%s",s);
	int i=0,j=0;
	for(i=0;s[i]!='\0';i++)
	{
		int flag=0,sam=0;
		if('a'<=s[i-1]&&s[i-1]<='z'&&'a'<=s[i+1]&&s[i+1]<='z')sam=1;
		if('A'<=s[i-1]&&s[i-1]<='Z'&&'A'<=s[i+1]&&s[i+1]<='Z')sam=1;
		if('0'<=s[i-1]&&s[i-1]<='9'&&'0'<=s[i+1]&&s[i+1]<='9')sam=1;
		if(s[i]=='-'&&s[i+1]>s[i-1]&&sam==1)
		{
			int j=1;
			for(j=1;s[i-1]+j<s[i+1];j++){
				printf("%c",s[i-1]+j);
			}
		}
		else printf("%c",s[i]);
	}
	
	
	return 0;
}
