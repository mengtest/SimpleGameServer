package com.wq.gameServer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wq.gameServer.GameStart.RunnableServer;

public class Client implements RunnableServer{
	
	private String name;
	private String host;
	private int port;
	private List<String> handlers;
	Logger log = LoggerFactory.getLogger(Client.class);

	public void run() throws Exception {
		
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		Bootstrap b = new Bootstrap();
		b.remoteAddress(host, port);
		b.group(workerGroup);
		b.channel(NioSocketChannel.class);
		b.option(ChannelOption.SO_KEEPALIVE, true);
		b.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				for(String handlerName : getHandlers()){
					ch.pipeline().addLast((ChannelHandler)GameStart.context.getBean(handlerName));
				}
			}
		});
		
		// 启动连接
		ChannelFuture f = b.connect().sync();
		
		// 连接关闭处理
		f.channel().closeFuture().addListener(new ChannelFutureListener() {
			
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if(!future.isSuccess()){
					log.error("Close " + getName() + "failed!");
				}else{
					log.info("Close " + getName() + "succeed");
				}
				future.channel().eventLoop().shutdown();
			}
		});
	}
	
	public String getName() {
		if(name != null && !"".equals(name)){
			return name;
		}
		if(host != null && !"".equals(host)){
			return host + " : " +port;
		}
		return "";
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public List<String> getHandlers() {
		return handlers;
	}

	public void setHandlers(List<String> handlers) {
		this.handlers = handlers;
	}
}
