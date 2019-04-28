package com.xxg.websocket;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

/**
 * 用来查看linux日志的
 */
@ServerEndpoint("/hhlog")
public class HHWebsocketLinuxLog {
	public HHWebsocketLinuxLog() {
		System.out.println("创建日志websocket成功");
	}

	Process p;
	InputStream inputStream;

	/**
	 * 访问linux日志文件
	 */
	@OnOpen
	public void onOpen(Session session) {
		try {
			Runtime rt = Runtime.getRuntime();
			p = rt.exec("tail -f -n 10000 ~/hh.log");
			inputStream = p.getInputStream();
			BufferedReader b = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
			String line = null;
			while ((line = b.readLine()) != null) {
				session.getBasicRemote().sendText(line+"");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


//z

	/**
	 * WebSocket请求关闭
	 */
	@OnClose
	public void onClose() {
		try {
			if (inputStream != null)
				inputStream.close();
			if (p != null)
				p.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@OnError
	public void onError(Throwable thr) {
		thr.printStackTrace();
	}

	/**
	 * 收到客户端消息后调用的方法
	 *
	 * @param message 客户端发送过来的消息
	 * @param session 可选的参数
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws Exception
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		if ("stop".equalsIgnoreCase(message)) {
			onClose();
		}
	}

}
