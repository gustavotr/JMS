package jms.subscriber;

import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.jms.Session;
import javax.jms.MessageListener;

public class JmsSubscriber implements MessageListener {

    private TopicConnection connect;

    public JmsSubscriber(String factoryName, String topicName)
            throws JMSException, NamingException {
        Context jndiContext = new InitialContext();
        TopicConnectionFactory factory = (TopicConnectionFactory) jndiContext.lookup(factoryName);
        Topic topic = (Topic) jndiContext.lookup(topicName);
        this.connect = factory.createTopicConnection();
        TopicSession session = connect.createTopicSession(false,
                Session.AUTO_ACKNOWLEDGE);
        TopicSubscriber subscriber = session.createSubscriber(topic);
        subscriber.setMessageListener(this);
        connect.start();
    }

    public void onMessage(Message message) {
        try {
            TextMessage textMsg = (TextMessage) message;
            String text = textMsg.getText();
            System.out.println(text);
        } catch (JMSException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            new JmsSubscriber("ConnectionFactory",
                    "topic/flightStatus");
        } else {
            new JmsSubscriber(args[0], args[1]);
        }
    }
}
