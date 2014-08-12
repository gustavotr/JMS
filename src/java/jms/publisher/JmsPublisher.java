package jms.publisher;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicPublisher;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JmsPublisher {

    private TopicPublisher publisher;
    private TopicSession session;
    private TopicConnection connect;

    public JmsPublisher(String factoryName, String topicName)
            throws JMSException, NamingException {
        Context jndiContext = new InitialContext();
        TopicConnectionFactory factory = (TopicConnectionFactory) jndiContext.lookup(factoryName);
        Topic topic = (Topic) jndiContext.lookup(topicName);
        this.connect = factory.createTopicConnection();
        this.session = connect.createTopicSession(false,
                Session.AUTO_ACKNOWLEDGE);
        this.publisher = session.createPublisher(topic);
    }

    public void publish(String message) throws JMSException {
        TextMessage textMsg = this.session.createTextMessage();
        textMsg.setText(message);
        System.out.println("PUBLISHING MESSAGE: " + message);
        this.publisher.publish(textMsg);
    }

    public void close() throws JMSException {
        this.connect.close();
    }

    public static void main(String[] args) throws Exception {
        JmsPublisher publisher;

        if (args.length != 2) {
            publisher = new JmsPublisher("ConnectionFactory",
                    "topic/flightStatus");
        } else {
            publisher = new JmsPublisher(args[0], args[1]);
        }

        publisher.publish("30JUL08 1032 TAKEOFF JJ8011 FLN GRU");
        Thread.sleep(5000);
        publisher.publish("30JUL08 1120 LANDING RG2541 FLN POA");
        Thread.sleep(5000);
        publisher.publish("30JUL08 1155 TAKEOFF G94321 FLN CGH");
        publisher.close();
        System.exit(0);
    }
}
