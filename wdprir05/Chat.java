package Rabbit;

import java.util.Scanner;

public class Chat {
    static public void main(String[] args) throws Exception {
        Scanner input = new Scanner(System.in);
        System.out.println("[*] Specify server name");
        String host_name= input.nextLine();
        System.out.println("[*] Specify username");
        String username= input.nextLine();
        new Thread(new ReceiveMsgs("localhost")).start();
        new Thread(new SendMsgs("localhost",username)).start();
        System.out.println("[*] Waiting for messages. To exit press CTRL+C");
        System.out.println("[*] Send your first message");
    }
}
