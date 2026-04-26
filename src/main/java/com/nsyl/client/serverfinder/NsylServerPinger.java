/*
 * Copyright (c) 2025 NSYL and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package com.nsyl.client.serverfinder;

import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerData.Type;
import net.minecraft.client.multiplayer.ServerStatusPinger;

public class NsylServerPinger
{
	// Shared thread pool ใช้ร่วมกันทุก pinger แทนสร้าง thread ใหม่ทุกครั้ง
	private static final int POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;
	private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(
		POOL_SIZE, new ThreadFactory()
		{
			private final AtomicInteger count = new AtomicInteger(0);
			@Override
			public Thread newThread(Runnable r)
			{
				Thread t = new Thread(r,
					"NSYL Server Pinger #" + count.incrementAndGet());
				t.setDaemon(true);
				t.setPriority(Thread.MIN_PRIORITY);
				return t;
			}
		});
	
	private ServerData server;
	private volatile boolean done = false;
	private volatile boolean failed = false;
	
	public void ping(String ip)
	{
		ping(ip, 25565);
	}
	
	public void ping(String ip, int port)
	{
		server = new ServerData("", ip + ":" + port, Type.OTHER);
		THREAD_POOL.submit(() -> pingInCurrentThread(ip, port));
	}
	
	private void pingInCurrentThread(String ip, int port)
	{
		ServerStatusPinger pinger = new ServerStatusPinger();
		System.out.println("Pinging " + ip + ":" + port + "...");
		
		try
		{
			pinger.pingServer(server, () -> {}, () -> {});
			System.out.println("Ping successful: " + ip + ":" + port);
			
		}catch(UnknownHostException e)
		{
			System.out.println("Unknown host: " + ip + ":" + port);
			failed = true;
			
		}catch(Exception e2)
		{
			System.out.println("Ping failed: " + ip + ":" + port);
			failed = true;
		}
		
		pinger.removeAll();
		done = true;
	}
	
	public boolean isStillPinging()
	{
		return !done;
	}
	
	public boolean isWorking()
	{
		return !failed;
	}
	
	public String getServerIP()
	{
		return server.ip;
	}
}
