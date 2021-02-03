package MatchingEngine;

import MatchingEngine.Manager.Manager;
import MatchingEngine.Messaging.OrderMessage;
import MatchingEngine.Messaging.ResponseMessage;
import MatchingEngine.OrderBook.Book;
import MatchingEngine.Responder.Responder;
import MatchingEngine.OrderBook.Side;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicLong;

public class Server {

    private static int PORT = 9887;

    private static AtomicLong idGenerator = new AtomicLong(0);
    private static AtomicLong OrderIDs = new AtomicLong(0);


    public static class ClientThread extends Thread {

        Logger logger = new Logger("Client");

        private OrderMessage template = new OrderMessage();
        private ResponseMessage response = new ResponseMessage();

        private boolean validate(String[] parts) {
            return parts.length == 4 && parts[2].equals("@") && isNumber(parts[1]) && isNumber(parts[3]);
        }

        private boolean isNumber(String str) {
            for(int i = 0; i < str.length(); i++) {
                if (!Character.isDigit(str.charAt(i))) {
                    return false;
                }
            }
            return true;
        }


        public ClientThread(long clientId, InputStream inputStream, Book book, Manager manager, Responder responder) {
            logger.info("waiting for commands: b/s <qty> @ <price>");
            logger.info("for example: to buy 100 @ price 23:  b 100 @ 23  (only integer values supported)");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            while (true) {
                try {
                    String input = reader.readLine();
                    logger.info("received: " + input);
                    String[] parts = input.split(" ");
                    switch (parts[0].charAt(0)) {
                        case 'b' :  // b 100 @ 23
                            // TODO - add validation of format
                            if (validate(parts)) {
                                book.getMailBox().putCopy(template.buildNewOrder(OrderIDs.getAndIncrement(),
                                        clientId, Side.BUY, Long.parseLong(parts[1]), Long.parseLong(parts[3])));
                            }
                            else {
                                responder.getMailBox().putCopy(response.buildError(clientId, "Bad Input " + input));
                            }
                            break;
                        case 's':
                            if (validate(parts)) {
                                book.getMailBox().putCopy(template.buildNewOrder(OrderIDs.getAndIncrement(),
                                        clientId, Side.SELL, Long.parseLong(parts[1]), Long.parseLong(parts[3])));
                            } else {
                                responder.getMailBox().putCopy(response.buildError(clientId, "Bad Input " + input));
                            }
                            break;
                        case 'g':
                            manager.getMailBox().putCopy(template.buildOrderInfo(clientId, Long.parseLong(parts[1])));
                            break;
                        case 't':
                            manager.getMailBox().putCopy(template.buildTradeReport(clientId, Long.parseLong(parts[1])));
                        case 'o':
                            manager.getMailBox().putCopy(template.buildOrderBook(clientId));
                            break;
                        case 'k':
                            book.getMailBox().putCopy(template.buildKill());
                            return;
                        default:
                            responder.getMailBox().putCopy(response.buildError(clientId, "Bad Input"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }


    public static void main(String[] args) {
        Logger logger = new Logger("Server");
        logger.info("Starting");
        logger.info("Server started, listening for connections at port " + PORT);
        logger.info("connect using telnet or nc 'nc -v localhost " + PORT + "'");
        Manager manager = new Manager();
        Book book = new Book();
        Responder responder = new Responder();
        manager.connect(responder.getMailBox());
        book.connect(manager.getMailBox());
        responder.start();
        manager.start();
        book.start();

        try {
            ServerSocket listener = new ServerSocket(PORT);
            while (true) {
                long clientId = idGenerator.incrementAndGet();
                Socket connection = listener.accept();
                InputStream inputStream = connection.getInputStream();
                OutputStream output = connection.getOutputStream();
                responder.addClient(clientId, output);
                logger.info("got client " + clientId + " connected");
                new ClientThread(clientId, inputStream, book, manager, responder).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
