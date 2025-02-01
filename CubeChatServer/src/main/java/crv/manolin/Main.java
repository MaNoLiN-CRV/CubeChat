package crv.manolin;

import crv.manolin.sockets.MultiPortServer;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        MultiPortServer server = new MultiPortServer();
        server.startServer();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Press enter to stop the server");
        scanner.nextLine();
        server.stopServer();
    }
}