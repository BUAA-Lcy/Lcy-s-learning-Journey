#include<bits/stdc++.h>
using namespace std;
int main(){

	priority_queue<int>heap1;
	priority_queue<int,vector<int>,greater<int> >heap2; 
	priority_queue <int,vector<int>,greater<int> > q;//�������,С����
	priority_queue <int,vector<int>,less<int> >p;  //�������,�󶥶�

	int n;
	scanf("%d",&n);
	while(n--){
		int m;
		scanf("%d",&m);
		heap1.push(m); 
	}
	while(!heap1.empty()){
		printf("%d",heap1.top());
		heap1.pop();
	}
	return 0;
}
