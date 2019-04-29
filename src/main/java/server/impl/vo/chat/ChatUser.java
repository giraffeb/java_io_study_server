package server.impl.vo.chat;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 사용자 정보를 기록할 객체.
 */
@Data
@Accessors(chain = true)
public class ChatUser implements Serializable {

    int uid;

    String id;
    String pw;

}
