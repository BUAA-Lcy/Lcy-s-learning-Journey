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

SqStack CreateStack(int maxsize);   //����һ����СΪMaxSize��ջ
int IsFull(SqStack s);   //�ж�ջ�Ƿ�Ϊ����
int IsEmpty(SqStack s);   //�ж�ջ�Ƿ�Ϊ��
int Push(SqStack s, int a);   //��ջ����ջ
int Pop(SqStack s);   //��ջ����ջ


SqStack CreateStack(int maxsize) {   //����һ��˳��ջ
	struct SqSnode* s = (struct SqSnode*)malloc(sizeof(struct SqSnode));
	s->data = (int*)malloc(maxsize * sizeof(int));
	s->MaxSize = maxsize;
	s->top = -1;
	return s;
}

int IsFull(SqStack s) {   //�ж�ջ�Ƿ�Ϊ����
	return s->top == s->MaxSize - 1;

}
int IsEmpty(SqStack s) {   //�ж�ջ�Ƿ�Ϊ��
	return s->top == -1;
}

int Push(SqStack s, int a) {   //��ջ����ջ
	if (IsFull(s))
		return 0;
	else {
		s->data[++(s->top)] = a;
		return 1;
	}	
}

int Pop(SqStack s) {   //��ջ����ջ
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
