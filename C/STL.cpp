#include<bits/stdc++.h>
using namespace std;

struct student{
	int id;
	int age;
};
bool cmp(student x,student y){
	if(x.id>y.id)return true;
	else if(x.id<y.id)return false;
	else{
		if(x.age>y.age)return true;
		else return false;
	}
}
int main(){
	student stu[100];
	int num;
	scanf("%d",&num);
	for(int i=0;i<num;i++){
		scanf("%d%d",&stu[i].id,&stu[i].age);
	}
	sort(stu,stu+num,cmp);
	for(int i=0;i<num;i++){
		printf("%d%d\n",stu[i].id,stu[i].age);		
	}

	return 0;
} 
