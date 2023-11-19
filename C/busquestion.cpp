#include<stdio.h>
#include<stdbool.h>
#include <stdlib.h>
#include<string.h>
#include<math.h>
#include<malloc.h>
#include<ctype.h>
typedef struct SqSnode{
	int top;
	int MaxSize;
	int* data;
}* SqStack;

SqStack CreateStack(int maxsize);   //建立一个大小为MaxSize的栈
int IsFull(SqStack s);   //判断栈是否为已满
int IsEmpty(SqStack s);   //判断栈是否为空
int Push(SqStack s, int a);   //从栈顶入栈
int Pop(SqStack s);   //从栈顶出栈


SqStack CreateStack(int maxsize) {   //建立一个顺序栈
	struct SqSnode* s = (struct SqSnode*)malloc(sizeof(struct SqSnode));
	s->data = (int*)malloc(maxsize * sizeof(int));
	s->MaxSize = maxsize;
	s->top = -1;
	return s;
}

int IsFull(SqStack s) {   //判断栈是否为已满
	return s->top == s->MaxSize - 1;

}
int IsEmpty(SqStack s) {   //判断栈是否为空
	return s->top == -1;
}

int Push(SqStack s, int a) {   //从栈顶入栈
	if (IsFull(s))
		return 0;
	else {
		s->data[++(s->top)] = a;
		return 1;
	}	
}

int Pop(SqStack s) {   //从栈顶出栈
	if (IsEmpty(s))
		return 0;
	else
		return s->data[(s->top)--];
}
int main(void)
{
	SqStack S = CreateStack(10);
	printf("%d\n", IsEmpty(S));
	Push(S, 2);
	Push(S, 3);
	Push(S, 4);
	Push(S, 5);
	Push(S, 6);
	Push(S, 7);
	Push(S, 8);
	Push(S, 9);
	printf("%d\n", IsFull(S));
	Push(S, 10);
	Push(S, 11);
	printf("%d\n", IsFull(S));
	while (!IsEmpty(S)) {
		printf("%d\n", Pop(S));
	}
	printf("\n%d", IsEmpty(S));
	return 0;
}
