package server.raft;

import database.DatabaseService;
import logger.Logger;
import server.ServerUtils;
import server.edges.EdgeInfo;
import server.timer.NodeTimer;
import io.netty.channel.ChannelFuture;
import pipe.work.Work.WorkMessage;
import routing.MsgInterface.Route;
import routing.MsgInterface.User;
import routing.MsgInterface.Group;
import routing.MsgInterface.MessagesRequest;
import routing.MsgInterface.Message;
import pipe.common.Common.WriteBody;
import pipe.work.AppendEntriesRPC.AppendEntries.RequestType;
import pipe.work.VoteRPC.ResponseVoteRPC;

import java.util.List;

import static server.raft.ServerMessageUtils.prepareMessagesResponseBuilder;


public class FollowerState extends State implements Runnable{
	public static Boolean isHeartBeatRecieved = Boolean.FALSE;
	NodeTimer timer;
	
	private static FollowerState INSTANCE = null;
	Thread fThread = null;
	private FollowerState() {
		// TODO Auto-generated constructor stub
	}
	
	public static FollowerState getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new FollowerState();

		}
		return INSTANCE;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Follower state");
		logger.Logger.DEBUG("-----------------------FOLLOWER SERVICE STARTED ----------------------------");
		initFollower();
		
		/*
		 * What should happen to existing thread of execution after the node state changes
		 * to CANDIDATE?
		 * 
		 * Why do we need to check here for the value of node state with FOLLOWER?
		 */
		fThread = new Thread(){
		    public void run(){
				while (running) {
					while (NodeState.getInstance().getNodestate() == NodeState.FOLLOWER) {
					}
				}

		    }
		 };

		fThread.start();
//		ServerQueueService.getInstance().createGetQueue();
		
		
		
		
	}
	
	private void initFollower() {
		// TODO Auto-generated method stub

		timer = new NodeTimer();
         
		timer.schedule(new Runnable() {
			@Override
			public void run() {
				logger.Logger.DEBUG("CHanging state");
				NodeState.getInstance().setNodeState(NodeState.CANDIDATE);
			}
		}, ServerUtils.getElectionTimeout());

	}

	
	
	public void onReceivingHeartBeatPacket() {
		timer.reschedule(ServerUtils.getElectionTimeout());
	}

	@Override
	public WorkMessage handleRequestVoteRPC(WorkMessage workMessage) {

		if (workMessage.getVoteRPCPacket().getRequestVoteRPC().getTimeStampOnLatestUpdate() < NodeState.getTimeStampOnLatestUpdate()) {
			Logger.DEBUG(NodeState.getInstance().getServerState().getConf().getNodeId() + " has replied NO");
			return ServerMessageUtils.prepareResponseVoteRPC(ResponseVoteRPC.IsVoteGranted.NO);

		}
		Logger.DEBUG(NodeState.getInstance().getServerState().getConf().getNodeId() + " has replied YES");
		return ServerMessageUtils.prepareResponseVoteRPC(ResponseVoteRPC.IsVoteGranted.YES);

	}

	public void handleHeartBeat(WorkMessage wm) {
		Logger.DEBUG("HeartbeatPacket received from leader :" + wm.getHeartBeatPacket().getHeartbeat().getLeaderId());
		NodeState.currentTerm = wm.getHeartBeatPacket().getHeartbeat().getTerm();
		onReceivingHeartBeatPacket();
		WorkMessage heartBeatResponse = ServerMessageUtils.prepareHeartBeatResponse();

		for (EdgeInfo ei : NodeState.getInstance().getServerState().getEmon().getOutboundEdges().getMap().values()) {

			if (ei.isActive() && ei.getChannel() != null
					&& ei.getRef() == wm.getHeartBeatPacket().getHeartbeat().getLeaderId()) {

				Logger.DEBUG("Sent HeartBeatResponse to " + ei.getRef());
				ChannelFuture cf = ei.getChannel().writeAndFlush(heartBeatResponse);
				if (cf.isDone() && !cf.isSuccess()) {
					Logger.DEBUG("failed to send message (HeartBeatResponse) to server");
				}
			}
		}

	}
	
	
	
	//***************************************************************
	// Put message in the leaderQueue of Queue Server
	//***************************************************************
	
	public void handleReplicationMessage(Route msg) {
		Logger.DEBUG("Route packet received from client :" + msg.toString());
			for (EdgeInfo ei : NodeState.getInstance().getServerState().getEmon().getOutboundEdges().getMap().values()) {

				if (ei.isActive() && ei.getChannel() != null
						&& ei.getRef() == 0) {

					Logger.DEBUG("Sent Route Packet for replication to " + ei.getRef());
					ChannelFuture cf = ei.getChannel().writeAndFlush(msg);
					if (cf.isDone() && !cf.isSuccess()) {
						//TODO: add to failed messages queue
						Logger.DEBUG("failed to send message (Route) to Queue-server");
					}

				}
			}
		
		
	}

	@Override
	public void handleUserEntries(Route msg) {
		User.ActionType type = msg.getUser().getAction();
		System.out.println("handleUserEntries: " + type.toString());
		if (type == User.ActionType.REGISTER) {

		} else if (type == User.ActionType.ACCESS) {

		} else if (type == User.ActionType.DELETE) {

		}
	}
	@Override
	public void handleMessageEntries(Route msg) {
		//TODO: add return or pass channel to responsed
		Message.ActionType type = msg.getMessage().getAction();
		if( msg.hasMessagesRequest() ){
			//TODO: get values for message
			List test = DatabaseService.getInstance().getDb().getMessages("test","test2");
			Route route = prepareMessagesResponseBuilder("test","test2", test);
			//TODO: respond with route
			System.out.println("getMessages: " + test);
		} else {
			System.out.println("handleMessageEntries: " + type.toString() + " : " + Message.ActionType.POST);
			if (type == Message.ActionType.POST) {
				DatabaseService.getInstance().getDb().postMessage(msg.getMessage().getPayload(), msg.getMessage().getReceiverId(),msg.getMessage().getSenderId());
				handleReplicationMessage(msg);
			} else if (type == Message.ActionType.UPDATE) {

			} else if (type == Message.ActionType.DELETE) {

			}
		}
	}


	
	
	
	
	
/**
 * Actual Deletion
 */
	@Override
	public void handleAppendEntries(WorkMessage wm) {
		String key = wm.getAppendEntriesPacket().getAppendEntries().getImageMsg().getKey();
		byte[] image = wm.getAppendEntriesPacket().getAppendEntries().getImageMsg().getImageData().toByteArray();
		long unixTimeStamp = wm.getAppendEntriesPacket().getAppendEntries().getTimeStampOnLatestUpdate();
		RequestType type = wm.getAppendEntriesPacket().getAppendEntries().getRequestType();
		
		if (type == RequestType.GET) {
			DatabaseService.getInstance().getDb().get(key);
		} else if (type == RequestType.POST) {
			NodeState.setTimeStampOnLatestUpdate(unixTimeStamp);
			DatabaseService.getInstance().getDb().post(key, image, unixTimeStamp);
		} else if (type == RequestType.PUT) {
			NodeState.setTimeStampOnLatestUpdate(unixTimeStamp);
			DatabaseService.getInstance().getDb().put(key, image, unixTimeStamp);
		} else if (type == RequestType.DELETE) {
			NodeState.setTimeStampOnLatestUpdate(System.currentTimeMillis());
			DatabaseService.getInstance().getDb().delete(key);
		}
		
		Logger.DEBUG("Inserted entry with key " + key + " received from "
				+ wm.getAppendEntriesPacket().getAppendEntries().getLeaderId());
	}
	
	
	
	
	
	
	@Override
	public byte[] handleGetMessage(String key) {
		System.out.println("GET Request Processed by Node: " + NodeState.getInstance().getServerState().getConf().getNodeId());
		NodeState.updateTaskCount();
		return DatabaseService.getInstance().getDb().get(key);
	}


	@Override
	public void startService(State state) {

		running = Boolean.TRUE;
		cthread = new Thread((FollowerState) state);
		cthread.start();

	}

	@Override
	public void stopService() {
		running = Boolean.FALSE;
		if (cthread != null) {
            try {
                cthread.join();
            } catch (InterruptedException e) {
                Logger.DEBUG("Exception", e);
            }
            Logger.DEBUG("cthread successfully stopped.");
        } 
	}
	
	public void handleWriteFile(WriteBody msg) {
		
		System.out.println("POST Request Processed by Node: " + NodeState.getInstance().getServerState().getConf().getNodeId());
		Logger.DEBUG("Data for File : "+msg.getFilename());
		Logger.DEBUG("Data : "+msg.getChunk().getChunkData().toStringUtf8());
		NodeState.updateTaskCount();
		//NodeState.setTimeStampOnLatestUpdate(timestamp);
	//	String key = DatabaseService.getInstance().getDb().post(image, timestamp);
	
		//sendAppendEntries(msg);
		
	}

   
	

}
