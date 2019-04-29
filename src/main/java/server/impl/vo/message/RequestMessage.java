package server.impl.vo.message;

import lombok.Data;
import lombok.experimental.Accessors;
import server.impl.vo.chat.ChatUser;
import server.interfaces.Message;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * 클라이언트에서 서버로 요청을 보낼때 사용하는 객체입니다.
 * 요청에 필요한 정보들을 담고 있습니다.
 */
@Data
@Accessors(chain=true)
public class RequestMessage implements Message{

    SocketChannel senderSocketChannel;
    ByteBuffer byteBuffer;

    MessageState state;

    String chatRoomTitle;
    int ChatRoomId;

    ChatUser chatUser;
    String token;

    String message;

}
