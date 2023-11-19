#include<stdio.h>
#include<stdbool.h>
#include <stdlib.h>

typedef struct LNode {
	int data;
	struct LNode* next;
}LNode, *LinkList;

LinkList InstallList_HeadInsert() {   //头插法建立单链表
	LinkList L = (LNode*)malloc(sizeof(LNode));
	L->next = NULL;    //引发了异常: 写入访问权限冲突。
	int e;
	scanf("%d", &e);
	while (e != 0) {
		LNode* s = (LNode*)malloc(sizeof(LNode));
		s->data = e;
		s->next = L->next;
		L->next = s;
		scanf("%d", &e);
	}
	return L;
}

int main(void)
{
	LinkList L1 = InstallList_HeadInsert();

	LNode* p1 = L1;
	LNode* p2 = L1;
	int k;
	scanf("%d", &k);
	int i;
	for (i = 0; i < k; i++) {
		if (p1 != NULL) {
			p1 = p1->next;
		}
		else
			break;
	}
	if (i != k) {
		return false;
	}
	else {
		while (p1 != NULL) {
			p1 = p1->next;
			p2 = p2->next;
		}
	}
	printf("%d", p2->data);
	return 0;
}
