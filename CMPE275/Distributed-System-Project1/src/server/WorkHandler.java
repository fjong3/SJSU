/**
 * Copyright 2016 Gash.
 *
 * This file and intellectual content is protected under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package server;




import logger.Logger;
import server.raft.FollowerState;
import server.raft.NodeState;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

//import pipe.common.Common.Failure;

import pipe.work.Work.WorkMessage;
import routing.MsgInterface.Route;



/**
 * The message handler processes json messages that are delimited by a 'newline'
 * 
 * TODO replace println with logging!
 * 
 * @author gash
 * 
 */
public class WorkHandler extends SimpleChannelInboundHandler<WorkMessage> {
//	protected static Logger logger = LoggerFactory.getLogger("work");
	protected ServerState state;
	protected boolean debug = false;

	public WorkHandler(ServerState state) {
		if (state != null) {
			this.state = state;
		}
	}

	/**
	 * override this method to provide processing behavior. T
	 * 
	 * @param msg
	 */
	public void handleMessage(WorkMessage msg, Channel channel) {
		 
		if (msg == null) {
			// TODO add logging
			System.out.println("ERROR: Unexpected content - " + msg);
			return;
		}

		if (debug)
			PrintUtil.printWork(msg);

		// TODO How can you implement this without if-else statements?
		try {
			if (msg.hasTrivialPing()) {
				Logger.DEBUG(" The node: " + msg.getTrivialPing().getNodeId() + " Is Active to this IP: "
						+ msg.getTrivialPing().getIP());
				Logger.DEBUG("Currrent Term " + NodeState.currentTerm);
				
				NodeState.getInstance().getServerState().getEmon().getOutboundEdges()
						.getNode(msg.getTrivialPing().getNodeId()).setChannel(channel);

			} else if (msg.hasHeartBeatPacket() && msg.getHeartBeatPacket().hasHeartbeat()) {
				System.out.println(
						"Heart Beat Packet recieved from " + msg.getHeartBeatPacket().getHeartbeat().getLeaderId());

				WorkMessage.Builder work = WorkMessage.newBuilder();
				work.setUnixTimeStamp(ServerUtils.getCurrentUnixTimeStamp());
				NodeState.getInstance().getState().handleHeartBeat(msg);

				// channel.write(work.build());

			} else if (msg.hasHeartBeatPacket() && msg.getHeartBeatPacket().hasHeartBeatResponse()) {
				Logger.DEBUG(
						"Response is Received from " + msg.getHeartBeatPacket().getHeartBeatResponse().getNodeId());
				NodeState.getState().handleHeartBeatResponse(msg);
			}

			else if (msg.hasVoteRPCPacket() && msg.getVoteRPCPacket().hasRequestVoteRPC()) {
				Logger.DEBUG("Vote Request recieved");
				WorkMessage voteResponse = NodeState.getInstance().getState().handleRequestVoteRPC(msg);
				channel.write(voteResponse);
			} else if (msg.hasVoteRPCPacket() && msg.getVoteRPCPacket().hasResponseVoteRPC()) {
                   //todo
			} else if (msg.hasAppendEntriesPacket() && msg.getAppendEntriesPacket().hasAppendEntries()) {

				NodeState.getInstance().getState().handleAppendEntries(msg);
			}
			else if (msg.hasTrivialPing()) {
				Logger.DEBUG(" The node: " + msg.getTrivialPing().getNodeId() + " Is Active to this IP: "
						+ msg.getTrivialPing().getIP());
				Logger.DEBUG("Currrent Term " + NodeState.currentTerm);
				
				NodeState.getInstance().getServerState().getEmon().getOutboundEdges()
						.getNode(msg.getTrivialPing().getNodeId()).setChannel(channel);

			}
			
					
			

		} catch (Exception e) {
			e.printStackTrace();

		}

		System.out.flush();	
			
			
			
	}
	
	

	/**
	 * a message was received from the server. Here we dispatch the message to
	 * the client's thread pool to minimize the time it takes to process other
	 * messages.
	 * 
	 * @param ctx
	 *            The channel the message was received from
	 * @param msg
	 *            The message
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WorkMessage msg) throws Exception {
		
		handleMessage(msg, ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	//	Logger.error("Unexpected exception from downstream.", cause);
		ctx.close();
	}

}
/*
		if(msg.hasPath()){
			String path = msg.getPath().toString().toLowerCase();
			System.out.println("hasPath:  " + path);
			if(msg.hasNetworkDiscoveryPacket()) {
				NodeState.getInstance().getState().handleNetworkDiscoveryPacketEntries(msg);
			} else if( path.equals("message") ) { //msg.hasUser()
				NodeState.getInstance().getState().handleMessageEntries(msg);
			} else if( path.equals("user")){ //msg.hasMessage()
				NodeState.getInstance().getState().handleUserEntries(msg);
			} else if( path.equals("messages_request")){ //msg.hasMessage()
				System.out.println("hasPath:  " + msg.toString());
				Route routeMessage= msg.toBuilder().setPath(Route.Path.MESSAGES_RESPONSE).build();
				channel.write(routeMessage);
			} else if( path.equals("messages_response")){
				System.out.println("hasPath:  " + msg.toString());
			}
		} else if (msg.hasWorkMessage()) {

*/