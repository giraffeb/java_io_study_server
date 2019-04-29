package server.impl.vo.chat;

import lombok.Data;
import lombok.experimental.Accessors;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Data
@Accessors(chain = true)
public class ChatRoom {


    int chatRoomId;
    String chatRoomTitle;

    Map<SocketChannel, ChatUser> chatUserList;

    public void init(){

        this.chatUserList = new LinkedHashMap<>();
    }

    public ChatRoom() {
        init();
    }

    public boolean isEmpty(){
        return this.chatUserList.isEmpty();
    }

    public List<SocketChannel> getUserSocketChannelList(){
        if(isEmpty()){
            return null;
        }

        return new ArrayList<>(this.chatUserList.keySet());
    }

    public ChatRoom addChatUser(ChatUser newUser, SocketChannel socketChannel){
        this.chatUserList.put(socketChannel, newUser );
        return this;
    }

}
