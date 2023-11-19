#include <iostream>
#include <mutex>
#include <condition_variable>
#include <thread>

std::mutex mtx;  // ������������������Դ���޸�
std::condition_variable cv_read, cv_write;  // ���ߺ�д�ߵ���������
int readers = 0;  // ��ǰ���ڶ��Ķ�������
int writers = 0;  // ��ǰ����д��д������
int waiting_writers = 0;  // ���ڵȴ���д������

void read() {
    std::unique_lock<std::mutex> lock(mtx);  // ��ȡ������
    while (writers > 0 || waiting_writers > 0) {  // �����д������д���еȴ���д�ߣ���ȴ�
        cv_read.wait(lock);
    }
    readers++;  // ��ʼ��
    std::cout << "���߿�ʼ��..." << std::endl;
    lock.unlock();  // �ͷŻ�������������������ͬʱ��

    // ģ�������
    std::this_thread::sleep_for(std::chrono::seconds(1));

    lock.lock();  // ���»�ȡ������
    readers--;  // ������
    std::cout << "���߽�����..." << std::endl;

    if (readers == 0) {  // ���û�ж����ˣ����ѿ��ܵ�д��
        cv_write.notify_one();
    }
}

void write() {
    std::unique_lock<std::mutex> lock(mtx);  // ��ȡ������
    waiting_writers++;  // �ȴ�д��������һ
    while (readers > 0 || writers > 0) {  // ����ж������ڶ�����д������д����ȴ�
        cv_write.wait(lock);
    }
    waiting_writers--;  // �ȴ�д��������һ
    writers++;  // ��ʼд
    std::cout << "д�߿�ʼд..." << std::endl;
    lock.unlock();  // �ͷŻ���������������д��ͬʱд

    // ģ��д����
    std::this_thread::sleep_for(std::chrono::seconds(1));

    lock.lock();  // ���»�ȡ������
    writers--;  // ����д
    std::cout << "д�߽���д..." << std::endl;

    if (waiting_writers > 0) {  // ������еȴ���д�ߣ������ȿ��ǻ���д��
        cv_write.notify_one();
    } else {  // ���򣬻��ѿ��ܵĶ���
        cv_read.notify_all();
    }
}

int main() {
    // ����������ߺ�д���߳�
    for (int i = 0; i < 5; ++i) {
        std::thread(read).detach();
        std::thread(write).detach();
    }

    std::this_thread::sleep_for(std::chrono::seconds(10));  // �ȴ��߳����
    return 0;
    
    }
