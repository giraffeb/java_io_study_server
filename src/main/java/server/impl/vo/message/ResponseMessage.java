package server.impl.vo.message;

import lombok.Data;
import lombok.experimental.Accessors;
import server.impl.vo.chat.ChatUser;
import server.interfaces.Message;

import java.util.List;
import java.util.Map;


/**
 * 클라이언트의 요청에 대한
 * 서버의 응답을 담는 객체입니다.
 */
@Data
@Accessors(chain = true)
public class ResponseMessage implements Message{

    MessageState messageState;
    String token;
    boolean correct;

    List<Map<String, String>> chatRoomNameList;

    int chatRoomId;
    String chatRoomTitle;

    String message;

    ChatUser receiveChatUser;
    ChatUser messageSendChatUser;

}
