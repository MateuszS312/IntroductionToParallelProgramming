# %%
import pika
import sys
import os
from threading import Thread

# %%
def sender(host_name,sender_name):
    connection = pika.BlockingConnection(
    pika.ConnectionParameters(host=host_name))
    channel = connection.channel()
    #we are sending messages to exchange they are then shared with all of the clients
    channel.exchange_declare(exchange='logs', exchange_type='fanout')
    while True:
        message=input()
        message=f"{sender_name}: {message}"
        channel.basic_publish(exchange='logs', routing_key='', body=message)
        # print(f" {sender_name}: {message}")



    connection.close()


# %%:
def reciver(host_name):
    connection = pika.BlockingConnection(
        pika.ConnectionParameters(host=host_name))
    channel = connection.channel()

    channel.exchange_declare(exchange='logs', exchange_type='fanout')

    result = channel.queue_declare(queue='', exclusive=True)
    queue_name = result.method.queue

    channel.queue_bind(exchange='logs', queue=queue_name)
    
    print('[*] Waiting for messages. To exit press CTRL+C')
    print('[*] Send your first message')
    def callback(ch, method, properties, body):
        print(f'{body.decode()}')

    channel.basic_consume(
        queue=queue_name, on_message_callback=callback, auto_ack=True)

    channel.start_consuming()


if __name__ == '__main__':
    try:
        print("[*] Specify server name")
        server_name="localhost"
        print("[*] Specify username:")
        username=input()
        thread = Thread(target =reciver,args=(("localhost",)))
        thread.start()
        sender("localhost",username)
    except KeyboardInterrupt:
        print('Interrupted')
        try:
            sys.exit(0)
        except SystemExit:
            os._exit(0)





# %%
