package server;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import server.impl.GenericServerStarter;
import server.impl.controller.SimpleMessageController;
import server.impl.io.SimpleMessageIO;
import server.interfaces.ServerStarter;
import server.ioc.DependencyControl;

import java.io.IOException;


/**
 * ChatServer 시작점
 */
public class Start {

    static Logger LOG = (Logger) LoggerFactory.getLogger(Start.class);

    public static void main(String[] args) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        LOG.setLevel(Level.WARN);

        try {
            DependencyControl dependencyControl = new DependencyControl();

            SimpleMessageIO simpleMessageIO = (SimpleMessageIO) dependencyControl.getObject(SimpleMessageIO.class);
            SimpleMessageController simpleMessageParser = (SimpleMessageController) dependencyControl.getObject(SimpleMessageController.class);

            ServerStarter genericServerStarter = new GenericServerStarter(4444, simpleMessageIO, simpleMessageParser);
            genericServerStarter.start();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
