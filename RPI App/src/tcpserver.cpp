#include "tcpserver.hpp"
#define PORT 2222

pthread_mutex_t *tcpserver::queue_lock;
pthread_cond_t *tcpserver::queue_sig;
std::queue<char> *tcpserver::_queue;
i2cDriver * i2c;
int tcpserver::_socket;
bool tcpserver::active;
int tcpidle = 10;
int tcpinterval = 10;

tcpserver::tcpserver(pthread_mutex_t *m, std::queue<char> *q, pthread_cond_t *c, i2cDriver *d)
{
    _queue = q;
    queue_lock = m;
    queue_sig = c;
    i2c = d;
}

tcpserver::~tcpserver() {}

void *tcpserver::recievethread(void *)
{
    while (active)
    {
        char buffer[1] = {0};
        if (read(_socket, buffer, 1) < 1)
        {
            std::cout << "Client disconnected" << std::endl;
            active = false;
        }
        else
        {
            std::cout << "message recieved" << std::endl;
            i2c->send(*buffer);
            //TODO seend data to
        }
    }
    pthread_exit(NULL);
}
void *tcpserver::sendthread(void *)
{
    while (active)
    {

        pthread_mutex_lock(queue_lock);
        while (!_queue->empty())
        {
            char message = _queue->front();
            _queue->pop();
            send(_socket, &message, 1, 0);
            std::cout << "Sent message: " + message << std::endl;
        }
        pthread_cond_wait(queue_sig, queue_lock);
        pthread_mutex_unlock(queue_lock);
    }
    pthread_exit(NULL);
}

void *tcpserver::start(void *)
{
    std::cout << "tcp_server starting" << std::endl;
    int server_fd;
    struct sockaddr_in address;
    int opt = 1;
    int addrlen = sizeof(address);
    pthread_t threads[2];
    // Creating socket file descriptor
    if ((server_fd = socket(AF_INET, SOCK_STREAM, 0)) == 0)
    {
        perror("socket failed");
        exit(EXIT_FAILURE);
    }

    // Forcefully attaching socket to the port 2222
    if (setsockopt(server_fd, SOL_SOCKET, SO_REUSEADDR | SO_REUSEPORT | SO_KEEPALIVE,
                   &opt, sizeof(opt)))
    {
        perror("setsockopt");
        exit(EXIT_FAILURE);
    }

    //Set idle seconds before keepalive packages are send
    if (setsockopt(server_fd, IPPROTO_TCP, TCP_KEEPIDLE, &tcpidle, sizeof(tcpidle)) < 0)
    {
        perror("setsockopt");
        exit(EXIT_FAILURE);
    }

    //set seconds between keepalive packages
    if (setsockopt(server_fd, IPPROTO_TCP, TCP_KEEPINTVL, &tcpinterval, sizeof(tcpinterval)) < 0)
    {
        perror("setsockopt");
        exit(EXIT_FAILURE);
    }
    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    address.sin_port = htons(PORT);

    // Forcefully attaching socket to the port 2222
    if (bind(server_fd, (struct sockaddr *)&address,
             sizeof(address)) < 0)
    {
        perror("bind failed");
        exit(EXIT_FAILURE);
    }
    if (listen(server_fd, 3) >= 0)
    {

        while (true)
        {

            std::cout << "Accepting connections" << std::endl;
            if ((_socket = accept(server_fd, (struct sockaddr *)&address,
                                  (socklen_t *)&addrlen)) >= 0)
            {
                std::cout << "Client connected" << std::endl;

                active = true;

                pthread_create(&threads[0], NULL, sendthread, NULL);

                pthread_create(&threads[1], NULL, recievethread, NULL);

                while (active)
                {
                }
                pthread_cond_signal(queue_sig);

                // wait for threads to close in case client disconnects
                pthread_join(threads[0], NULL);
                pthread_join(threads[1], NULL);
                close(_socket);
            }
        }
    }
    pthread_exit(NULL);
}
