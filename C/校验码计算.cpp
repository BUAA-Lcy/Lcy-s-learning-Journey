#include<bits/stdc++.h>
using namespace std;
int main(){
	
	int n;scanf("%d",&n);
	while(n--){
		
	int a[20];
	int b[18]={0,7,9,10,5,8,4,2,1,6,3,7,9,10,5,8,4,2};
	//计算校验码 
	char s[20];
	scanf("%s",s);
	int sum=0;
	for(int i=1;i<=17;i++)
	{
		a[i]=s[i-1]-'0';
		printf("a[%d]=%d\n",i,a[i]);
	}
	for(int j=1;j<=17;j++)sum+=a[j]*b[j];
	
	int l=sum%11;
	if(l>=3)printf("校验码为%d\n",12-l);
	else{
		if(l==0)printf("校验码为1\n");
		else if(l==1)printf("校验码为0\n");
		else printf("校验码为X\n");
	}
	
}
	return 0;
	
}
