package server.ioc;

import server.impl.controller.SimpleMessageController;
import server.impl.io.SimpleMessageIO;
import server.impl.repository.ChatRoomRepository;
import server.impl.repository.ChatUserRepository;
import server.impl.repository.LoginChatUserRepository;
import server.impl.repository.TokenRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * 원시적인 디펜던시 인젝션을 위해서 사용합니다.
 * TODO: server.interface.Bean -> 구현해야합니다.
 */
public class DependencyControl {
    private Map<Class, Object> container;

    private void init(){
        this.container = new HashMap<>();


        //#1 simpleMessageParser
        //SimpleMessageController dependency
        SimpleMessageIO simpleMessageIO = new SimpleMessageIO();
        ChatUserRepository chatUserRepository = new ChatUserRepository();
        ChatRoomRepository chatRoomRepository = new ChatRoomRepository();
        LoginChatUserRepository loginChatUserRepository = new LoginChatUserRepository();
        TokenRepository tokenRepository = new TokenRepository();

        SimpleMessageController simpleMessageParser = new SimpleMessageController(chatUserRepository, loginChatUserRepository, chatRoomRepository, tokenRepository, simpleMessageIO );
        //END : SimpleMessageController dependency

        this.container.put(SimpleMessageController.class, simpleMessageParser);

        this.container.put(SimpleMessageIO.class, simpleMessageIO);
        this.container.put(ChatUserRepository.class, chatUserRepository);
        this.container.put(ChatRoomRepository.class, chatRoomRepository);
        this.container.put(LoginChatUserRepository.class, loginChatUserRepository);

    }

    public DependencyControl() {
        this.init();
    }

    public <T> Object getObject(T cls){
        return this.container.get(cls);
    }
}
