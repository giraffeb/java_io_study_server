package server.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.interfaces.Bean;
import server.impl.controller.SimpleMessageController;
import server.impl.io.SimpleMessageIO;
import server.impl.repository.SimpleMessageQueue;
import server.impl.vo.message.RequestMessage;
import server.interfaces.Message;
import server.interfaces.ServerStarter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * ServerSocketChannel을 초기화하고,
 * 클라이언트 SocketChannel을 새로 등록하고 입력을 처리합니다.
 */
@Bean
public class GenericServerStarter implements ServerStarter {

    private static Logger LOG = LoggerFactory.getLogger(GenericServerStarter.class);

    private int serverPort;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    private SimpleMessageQueue simpleMessageQueue;

    private SimpleMessageIO simpleMessageIO;
    private SimpleMessageController simpleMessageController;


    @Override
    public void init() throws IOException {
        this.selector = Selector.open();

        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocketChannel.bind(new InetSocketAddress(this.serverPort));
        this.serverSocketChannel.configureBlocking(false);
        this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
    }

    /**
     * 필요한 객체들을 초기화하고, 사용자에게 받을 포트 정보를 입력한다.
     * @param serverPort
     * @throws IOException
     */
    public GenericServerStarter(int serverPort, SimpleMessageIO simpleMessageIO, SimpleMessageController simpleMessageParser) throws IOException {
        LOG.debug("CONSTRUCTOR");
        this.serverPort = serverPort;
        this.simpleMessageIO = simpleMessageIO;
        this.simpleMessageController = simpleMessageParser;

        init();
    }

    public GenericServerStarter() {
    }

    /**
     * select를 사용해서 요청되는 socketChannel을 등록하고
     * 입력을 받아들여서 하나의 메시지 단위로 만드는 역할을 수행함.
     *
     * 다른 비동기 서버에서 본것 처럼 싱글쓰레드로 처리하고
     * 로직이 필요한 부분을 멀티 쓰레드로 처리하자.
     * @throws IOException
     */
    @Override
    public void start() throws IOException {
        LOG.debug("START");
        int selectCount = 0;
        Set<SelectionKey> selectedKeys;
        Iterator<SelectionKey> iteratorSelectionKeys;
        SelectionKey curSelectionKey;

        SocketChannel curSocketChannel;
        ByteBuffer curByteBuffer;


        while(true){
            selectCount = this.selector.selectNow();

            if(selectCount == 0){
                continue;
            }

            selectedKeys = this.selector.selectedKeys();
            iteratorSelectionKeys = selectedKeys.iterator();

            while(iteratorSelectionKeys.hasNext()){

                curSelectionKey = iteratorSelectionKeys.next();

                if(curSelectionKey.isAcceptable()){

                    LOG.debug("ACCEPTABLE");

                    //요청이 들어오면 소켓채널을 생성하고, 버퍼를 할당함.
                    curSocketChannel = ((ServerSocketChannel)curSelectionKey.channel()).accept();
                    curSocketChannel.configureBlocking(false);
                    curSocketChannel.register(this.selector, SelectionKey.OP_READ, ByteBuffer.allocate(Message.MESSAGE_BUFFER_SIZE));

                }else if(curSelectionKey.isReadable()){
                    /**
                     * 데이터(RequestMessage)가 들어온 소켓채널과 바이트버퍼를 selector로부터 가져와서 로직을 부여한다.
                     * TODO: RequestMessage를 JobQueue에 담아서 멀티 쓰레드로 처리가능하도록 하자.
                     */
                    LOG.debug("READABLE");

                    curSocketChannel = (SocketChannel) curSelectionKey.channel();
                    curByteBuffer = (ByteBuffer) curSelectionKey.attachment();

                    RequestMessage requestMessage = this.simpleMessageIO.readRequestMessage(curSocketChannel, curByteBuffer);
                    if(requestMessage == null){
                        //소켓채이 종료된경우
                        LOG.debug("Socket Closed or No Data read");
                        break;
                    }

                    //요청이 메시지 발신자를 확인하기 위해서 소켓채널은 요청 메시지에 등록
                    requestMessage.setSenderSocketChannel(curSocketChannel)
                                    .setByteBuffer(curByteBuffer);
                    //요청 메시지 확인용
                    LOG.debug(requestMessage.toString());

                    //요청 메시지 분
                    this.simpleMessageController.parseMessageState(requestMessage);

                }else if(!curSelectionKey.isValid()){
                    curSelectionKey.cancel();
                }

                //이벤트 처리한 셀렉션 키 제
                iteratorSelectionKeys.remove();

            }

        }
    }

    @Override
    public void destroy() {
        //정상종료시 저장 처리 등을 위해서 합니다.
    }
}
