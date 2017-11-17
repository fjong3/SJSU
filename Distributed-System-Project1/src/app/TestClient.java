package app;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import com.google.protobuf.ByteString;

import client.CommInit;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import logger.Logger;
import pipe.common.Common.Chunk;
import pipe.common.Common.Header;
import pipe.common.Common.Node;
import pipe.common.Common.Request;
import pipe.common.Common.TaskType;
import pipe.common.Common.WriteBody;
import routing.MsgInterface.Message;
import routing.MsgInterface.Route;
import routing.Pipe.CommandMessage;

public class TestClient {

	
	static String host;
	static int port;
	static ChannelFuture channel;
	
	static EventLoopGroup group;
	static final int chunkSize = 1024; // MAX BLOB STORAGE = Math,pow(2,15) -1 = 65535 Bytes 
	
	
	public static void init(String host_received, int port_received)
	{
		host = host_received;
		port = port_received;
		group = new NioEventLoopGroup();
		try {
			CommInit si = new CommInit(false);
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class).handler(si);
			b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
			b.option(ChannelOption.TCP_NODELAY, true);
			b.option(ChannelOption.SO_KEEPALIVE, true);


			// Make the connection attempt.
			 channel = b.connect(host, port).syncUninterruptibly();

			
			// want to monitor the connection to the server s.t. if we loose the
			// connection, we can try to re-establish it.
			// ClientClosedListener ccl = new ClientClosedListener(this);
			// channel.channel().closeFuture().addListener(ccl);

			System.out.println(channel.channel().localAddress() + " -> open: " + channel.channel().isOpen()
					+ ", write: " + channel.channel().isWritable() + ", reg: " + channel.channel().isRegistered());

		} catch (Throwable ex) {
			System.out.println("failed to initialize the client connection " + ex.toString());
			ex.printStackTrace();
		}

	}
	
/*	public static void writeFile(File f){
		
			int partCounter = 0;
			byte[] data;
			byte[] remainingData;
			Logger.DEBUG("File Size : " + f.length());
			long fileSize = f.length();
			int chunks = (int) Math.ceil(f.length()/(double)chunkSize);
			
			if(chunks == 0){
				data = new byte[(int) f.length()];
			}
			else{
				data = new byte[chunkSize];
			}
			
			try (BufferedInputStream bis = new BufferedInputStream(
			new FileInputStream(f))) {//try-with-resources to ensure closing stream
				
			String name = f.getName();
			//int tmp = chunks;
			ByteString byteStringData;
			while(fileSize > 0){
				Logger.DEBUG("Bytes to read : " + bis.read(data) );
				Logger.DEBUG("File LEft : " + fileSize);
				Chunk.Builder chunk = Chunk.newBuilder();
				
				if(fileSize < chunkSize){
					remainingData = new byte[(int) fileSize];
					Logger.DEBUG("Last Bytes read : " + bis.read(remainingData));
					byteStringData = ByteString.copyFrom(remainingData);
					fileSize = 0;
					chunk.setChunkId(partCounter);
					chunk.setChunkSize(remainingData.length);
					chunk.setChunkData(byteStringData);
				}
				else{
					fileSize -= chunkSize;
					byteStringData = ByteString.copyFrom(data);
					chunk.setChunkId(partCounter);
					chunk.setChunkSize(data.length);
					chunk.setChunkData(byteStringData);
				}
				
				Logger.DEBUG(byteStringData.toStringUtf8());
				
				CommandMessage.Builder command = CommandMessage.newBuilder();
				Request.Builder msg = Request.newBuilder();
				msg.setRequestType(TaskType.WRITEFILE);
				WriteBody.Builder rwb  = WriteBody.newBuilder();
				rwb.setFileExt(f.getName().substring(f.getName().lastIndexOf(".") + 1));
				rwb.setFilename(f.getName());
				rwb.setNumOfChunks(chunks);
				Header.Builder header= Header.newBuilder();
				header.setNodeId(999); // 999 = Client
				header.setTime(System.currentTimeMillis());
				command.setHeader(header);
				
				rwb.setChunk(chunk);
				msg.setRwb(rwb);
				Node.Builder node = Node.newBuilder();
				node.setHost("localhost");
				node.setPort(4888);
				node.setNodeId(999);
				msg.setClient(node);
				command.setRequest(msg);
				partCounter++;
				CommandMessage commandMessage = command.build();
				
				channel.channel().writeAndFlush(commandMessage);
				
				if (channel.isDone() && channel.isSuccess()) {
					System.out.println("Msg sent succesfully:");
				}
			}
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
}	*/
	public static void writeMessage(String message,String destination_id){
		Message.Builder msg=Message.newBuilder();
		msg.setType(Message.Type.SINGLE);
		msg.setSenderId("dhanashree");
		msg.setPayload(message);
		msg.setReceiverId(destination_id);
		msg.setTimestamp("10:01");
		msg.setAction(Message.ActionType.POST);
		
		Route.Builder route= Route.newBuilder();
		route.setId(123);
		route.setPath(Route.Path.MESSAGE);
		route.setMessage(msg);
		Route routeMessage= route.build();
		
		channel.channel().writeAndFlush(routeMessage);
		
		if (channel.isDone() && channel.isSuccess()) {
			System.out.println("Msg sent succesfully:");
		}
	}
	

	public static void main(String[] args) {
		
		String host = "127.0.0.1";
		int port = 4167;
		
		System.out.println("Sent the message");
		TestClient.init(host, port);
		File file = new File("runtime/log.txt");
		TestClient.writeMessage("Hello Dhanashree","client1");
	
		while(true){
			
		}
		// TODO Auto-generated method stub
		
	}


}
