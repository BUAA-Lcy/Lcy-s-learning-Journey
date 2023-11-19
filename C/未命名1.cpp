#include <iostream>
#include <mutex>
#include <condition_variable>
#include <thread>

std::mutex mtx;  // 互斥锁，保护共享资源的修改
std::condition_variable cv_read, cv_write;  // 读者和写者的条件变量
int readers = 0;  // 当前正在读的读者数量
int writers = 0;  // 当前正在写的写者数量
int waiting_writers = 0;  // 正在等待的写者数量

void read() {
    std::unique_lock<std::mutex> lock(mtx);  // 获取互斥锁
    while (writers > 0 || waiting_writers > 0) {  // 如果有写者正在写或有等待的写者，则等待
        cv_read.wait(lock);
    }
    readers++;  // 开始读
    std::cout << "读者开始读..." << std::endl;
    lock.unlock();  // 释放互斥锁，允许其他读者同时读

    // 模拟读操作
    std::this_thread::sleep_for(std::chrono::seconds(1));

    lock.lock();  // 重新获取互斥锁
    readers--;  // 结束读
    std::cout << "读者结束读..." << std::endl;

    if (readers == 0) {  // 如果没有读者了，则唤醒可能的写者
        cv_write.notify_one();
    }
}

void write() {
    std::unique_lock<std::mutex> lock(mtx);  // 获取互斥锁
    waiting_writers++;  // 等待写者数量加一
    while (readers > 0 || writers > 0) {  // 如果有读者正在读或有写者正在写，则等待
        cv_write.wait(lock);
    }
    waiting_writers--;  // 等待写者数量减一
    writers++;  // 开始写
    std::cout << "写者开始写..." << std::endl;
    lock.unlock();  // 释放互斥锁，允许其他写者同时写

    // 模拟写操作
    std::this_thread::sleep_for(std::chrono::seconds(1));

    lock.lock();  // 重新获取互斥锁
    writers--;  // 结束写
    std::cout << "写者结束写..." << std::endl;

    if (waiting_writers > 0) {  // 如果还有等待的写者，则优先考虑唤醒写者
        cv_write.notify_one();
    } else {  // 否则，唤醒可能的读者
        cv_read.notify_all();
    }
}

int main() {
    // 创建多个读者和写者线程
    for (int i = 0; i < 5; ++i) {
        std::thread(read).detach();
        std::thread(write).detach();
    }

    std::this_thread::sleep_for(std::chrono::seconds(10));  // 等待线程完成
    return 0;
    
    }
