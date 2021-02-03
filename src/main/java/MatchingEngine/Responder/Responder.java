package MatchingEngine.Responder;


import MatchingEngine.Logger;
import MatchingEngine.Messaging.MailBox;
import MatchingEngine.Messaging.MailBoxImpl;
import MatchingEngine.Messaging.ResponseMessage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Responder extends Thread {

    private Logger logger = new Logger("Responder");
    private Map<Long, DataOutputStream> outputStreams = new ConcurrentHashMap<>();
    public MailBox<ResponseMessage> mailBox = new MailBoxImpl<>(10000, ResponseMessage::new);

    public void addClient(long clientId, OutputStream output) {
        DataOutputStream dataOutputStream = new DataOutputStream(output);
        outputStreams.put(clientId, dataOutputStream);
        logger.info("output stream linked for client " + clientId);
    }

    public void run() {
        logger.info("Starting Responder");
        boolean processMessages = true;
        while(processMessages) {
            ResponseMessage msg = mailBox.get();
            if (msg.getType() == ResponseMessage.Type.KILL) {
                processMessages = false;
            }
            else {
                long clientId = msg.getOwner();
                try {
                    logger.info("Got " + msg.getType() + " for client " + clientId + ": " + msg.getContents());
                    DataOutputStream output = outputStreams.get(clientId);
                    StringBuilder sb = new StringBuilder();     // not optimal from garbage pov...
                    switch (msg.getType()) {
                        case ORDER_ACK: sb.append("ACK "); break;
                        case ORDER_INFO: sb.append("INFO "); break;
                        case TRADE_REPORT: sb.append("TRADE_REPORT "); break;
                        case TRADE: sb.append("TRADE "); break;
                        case CANCEL: sb.append("CANCEL "); break;
                        case BOOK: sb.append("BOOK "); break;
                        case ERROR: sb.append("ERROR "); break;
                        default: sb.append("UNKNWON: "); break;
                    }
                    sb.append(msg.getContents()).append("\n\r");
                    if (output != null) {
                        output.writeBytes(sb.toString());
                    } else {
                        logger.warn("Got response for dead client: " + clientId);
                        //TODO - notify reader that output is dead
                    }
                } catch (IOException e) {
                    logger.error("Client " + clientId + " caused an error, killing!");
                    // TODO - notify reader that output is dead
                }
            }
        }
    }

    public MailBox<ResponseMessage> getMailBox() {
        return mailBox;
    }
}
