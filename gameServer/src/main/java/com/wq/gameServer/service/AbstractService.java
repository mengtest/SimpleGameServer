package com.wq.gameServer.service;

import java.lang.reflect.Method;

import com.wq.entity.protobuf.Protocol.protocol;

public class AbstractService implements Service{
	
	@Override
	public void service(protocol msg) throws Exception {
		String msgName = msg.getName();
		if(msgName == null || "".equals(msgName)){
			throw new NullPointerException();
		}
		String[] names = msgName.split("_");
		if(names.length < 2){
			throw new IllegalArgumentException();
		}
		String serviceName = this.getClass().getSimpleName();
		if(!names[0].equals(serviceName)){
			throw new IllegalArgumentException();
		}
		Method method = this.getClass().getMethod(names[1], protocol.class);
		method.invoke(this, msg);
	}

}