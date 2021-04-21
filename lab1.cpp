#define _CRT_SECURE_NO_WARNINGS 1 
#define _WINSOCK_DEPRECATED_NO_WARNINGS 1 
#pragma once

#include <Winsock2.h>
#include <winsock.h>
#include <iphlpapi.h>
#include <cstdio>
#include <iostream>
#include <string>
#include <future>


using namespace std;

#pragma comment(lib, "iphlpapi.lib")
#pragma comment(lib, "ws2_32.lib")

// Получение имени
bool get_name(unsigned char* name, char dest[32])
{
    struct in_addr destip;
    struct hostent* info;

    // Передаем адрес
    destip.s_addr = inet_addr(dest);

    // Пытаемся получить имя
    info = gethostbyaddr((char*)&destip, 4, AF_INET);

    // Если не null, копируем в указатель name
    if (info != NULL)
    {
        strcpy((char*)name, info->h_name);
    }
    else
    {
        return false;
    }
    return true;
}

// Получение мак-адреса
bool get_mac(unsigned char* mac, char dest[32])
{
    struct in_addr destip;
    ULONG mac_address[2];
    ULONG mac_address_len = 6;

    // Передаем адрес
    destip.s_addr = inet_addr(dest);

    SendARP((IPAddr)destip.S_un.S_addr, 0, mac_address, &mac_address_len);

    // Если удалось получить
    if (mac_address_len)
    {
        BYTE* mac_address_buffer = (BYTE*)&mac_address;
        for (int i = 0; i < (int)mac_address_len; i++)
        {
            // Перевод в символьный формат
            mac[i] = (char)mac_address_buffer[i];
        }
    }
    else
    {
        return false;
    }
    return true;
}

// Получаем адрес, выводим имя и мас-адрес
string checkAddress(string addr) {

    // Хранит мак-адрес
    unsigned char mac[6] = { '\0' };
    // Хранит имя
    unsigned char name[100] = { '\0' };

    char result[256] = { '\0' };
    char* address = &addr[0];

    if (get_mac(mac, address))
    {
        sprintf(result, "IP: %s\tNAME: %s \tMAC-ADDRESS: %.2X-%.2X-%.2X-%.2X-%.2X-%.2X\n", address, (get_name(name, address)) ? (char*)name : "NONE", mac[0], mac[1], mac[2], mac[3], mac[4], mac[5]);
    }
    else
    {
        sprintf(result, "IP: %s\tNOT IN LOCAL NETROWRK\n", address);
    }
    return result;
}

int main()
{
    string addr = "";
    string ip;

    WSADATA ws;
    hostent* h;

    char buf[128];

    // Проверка на работу библиотеки
    // Инициализируем WinSock
    if (WSAStartup(MAKEWORD(1, 1), &ws) == 0)
    {
        if (gethostname(&buf[0], 128) == 0)
        {
            printf("Name: %s \n", buf);
            h = gethostbyname(&buf[0]);
            if (h != NULL)
            {
                printf("IP: %s \n", inet_ntoa(*(reinterpret_cast<in_addr*>(*(h->h_addr_list)))));
                ip = inet_ntoa(*(reinterpret_cast<in_addr*>(*(h->h_addr_list))));
            }
            else
            {
                printf("error");
            }
        }
    }
    cout << "------------------------------------------------------------------------------" << endl;

    int count = 0;
    int i = 0;
    while (count != 3)
    {
        addr += ip[i];
        if (ip[i] == '.')
        {
            count++;
        }
        i++;
    }

    // Идем по маскам сети 
    for (int i = 1; i < 256; )
    {
        future<string> f1 = async(launch::async, checkAddress, addr + to_string(i++));
        future<string> f2 = async(launch::async, checkAddress, addr + to_string(i++));
        future<string> f3 = async(launch::async, checkAddress, addr + to_string(i++));
        future<string> f4 = async(launch::async, checkAddress, addr + to_string(i++));
        future<string> f5 = async(launch::async, checkAddress, addr + to_string(i++));
        future<string> f6 = async(launch::async, checkAddress, addr + to_string(i++));
        future<string> f7 = async(launch::async, checkAddress, addr + to_string(i++));
        future<string> f8 = async(launch::async, checkAddress, addr + to_string(i++));
        future<string> f9 = async(launch::async, checkAddress, addr + to_string(i++));
        future<string> f10 = async(launch::async, checkAddress, addr + to_string(i++));
        future<string> f11 = async(launch::async, checkAddress, addr + to_string(i++));
        future<string> f12 = async(launch::async, checkAddress, addr + to_string(i++));
        future<string> f13 = async(launch::async, checkAddress, addr + to_string(i++));
        future<string> f14 = async(launch::async, checkAddress, addr + to_string(i++));
        future<string> f15 = async(launch::async, checkAddress, addr + to_string(i++));

        cout << f1.get();
        cout << f2.get();
        cout << f3.get();
        cout << f4.get();
        cout << f5.get();
        cout << f6.get();
        cout << f7.get();
        cout << f8.get();
        cout << f9.get();
        cout << f10.get();
        cout << f11.get();
        cout << f12.get(); 
        cout << f13.get();
        cout << f14.get();
        cout << f15.get();
    }

    printf("\nDone.\n");

    fflush(stdout);
    return 0;
}

