/**
 * This file is part of the PRIME middleware.
 * See http://www.erc-smscom.org
 * 
 * Copyright (C) 2008-2013 ERC-SMSCOM Project
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307,
 * USA, or send email
 * 
 * @author Mauro Caporuscio 
 */

package org.smscom.prime.extension.goprime.management.assemblymanagement;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smscom.prime.core.comm.ICommGateway;
import org.smscom.prime.core.comm.addressing.AURI;
import org.smscom.prime.core.comm.addressing.CURI;
import org.smscom.prime.extension.goprime.comm.protocol.GOPrimeProtocol;
import org.smscom.prime.extension.goprime.management.servicemanagement.LocalUtilityMonitor;



public abstract class GossipManager implements Runnable{
	
	protected  Logger log = (Logger)LoggerFactory.getLogger(this.getClass().getName());
	
	
	public static AssemblyUtilityMonitor UNBOUND = new AssemblyUtilityMonitor(new AURI("EMPTY"), new CURI("UNBOUND"), 0, Metrics.NONE, 0.0);
	

	protected Map<AURI, AssemblyUtilityMonitor> dependences = null;
	protected long time;
	protected LocalUtilityMonitor localUtilityMonitor;
	
	ICommGateway gateway;
	
	
	/** The blinker. */
	private volatile Thread blinker;
	
	
	protected GossipManager(ICommGateway gateway, LocalUtilityMonitor localUtilityMonitor, long time){
		this.localUtilityMonitor = localUtilityMonitor;
		this.time = time;
		this.dependences = null;	
		this.gateway = gateway;
		
	}
	
	
	protected GossipManager(ICommGateway gateway, LocalUtilityMonitor localUtilityMonitor, Map<AURI, AssemblyUtilityMonitor> dependences, long time){
		this.localUtilityMonitor = localUtilityMonitor;
		this.time = time;
		this.gateway = gateway;
		
		if (dependences != null){
			this.dependences = new HashMap<AURI, AssemblyUtilityMonitor>();
			Iterator<AURI> i = dependences.keySet().iterator();
			while(i.hasNext()){
				AURI a = i.next();
				AssemblyUtilityMonitor d = dependences.get(a);
				this.dependences.put(a, d);
			}
		}else this.dependences = null;	
	}
	
	
	
	/**
	 * 
	 * @param localUtilityMonitor
	 */
	public synchronized void setLocalQoS(LocalUtilityMonitor localUtilityMonitor){
		this.localUtilityMonitor = localUtilityMonitor;
	}
	
	
	
	/**
	 * Check whether the dependences are satisfied or not
	 * @return true if all the dependences are satisfied, false otherwise.
	 */
	public synchronized boolean isDependencesResolved(){
		if (this.dependences == null) 
			return true;
		
		Iterator<AURI> type = (Iterator<AURI>) this.dependences.keySet().iterator();
		while(type.hasNext()){
			AssemblyUtilityMonitor bind = this.dependences.get(type.next());
			if (bind.getInstance().equals(UNBOUND.getInstance()))
				return false;
		}
		return true;
	}
	
	
	public synchronized Collection<AURI> getUnsolvedDependences(){
		if (this.dependences == null) 
			return null;
		Vector<AURI> result = new Vector<AURI>();
		
		Iterator<AURI> dep = (Iterator<AURI>) this.dependences.keySet().iterator();
		while(dep.hasNext()){
			AURI type = dep.next();
			AssemblyUtilityMonitor bind = this.dependences.get(type);
			if (bind.getInstance().equals(UNBOUND.getInstance()))
				result.add(type);
		}
		return result;
	}
	
	
	
	
	
	
	
	/**
	 * Return the information of the resource actually bound to the required type
	 * @param type of the needed resource
	 * @return The information about the resource implementing the required type
	 */
	public synchronized AssemblyUtilityMonitor getBinding(AURI type){
		return this.dependences.get(type);
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Thread thisThread = Thread.currentThread();

		while (blinker == thisThread) {
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			this.update();
			
		}
	}
	
	/**
	 * Send binding info to all the followers
	 */
	public synchronized void update(){
		Vector<LocalUtilityMonitor> info = new Vector<LocalUtilityMonitor>();
		
		if (dependences != null){
			LocalUtilityMonitor me = localUtilityMonitor.setCompaundUtilities(dependences.values());
			
			//log.info("C_RESPONSE: " + me.getUtility(Metrics.RESPONSE_TIME));
			System.out.println(System.currentTimeMillis() + "\t" + me.getUtility(Metrics.RESPONSE_TIME));
			info.add(me);
			
			Iterator<AURI> i = dependences.keySet().iterator();
			while(i.hasNext()){
				AssemblyUtilityMonitor r = dependences.get(i.next());
				if (!r.getInstance().equals(UNBOUND.getInstance()))
					info.add(new LocalUtilityMonitor(r));
			}
		}else{
			info.add(this.localUtilityMonitor);
		}
		
		this.sendToNeighbors(info);
	}
	
	
	public LocalUtilityMonitor getUtility(){
		return this.localUtilityMonitor.setCompaundUtilities(dependences.values());
	}
	
	
	
	public void stop() {
		blinker = null;
	}



	public void start() {
		blinker = new Thread(this);
		blinker.start();
	}

	/**
	 * 
	 * @param curi
	 */
	public synchronized void unbind(CURI curi) {
		// TODO Auto-generated method stub
		Iterator<AssemblyUtilityMonitor> i = dependences.values().iterator();
		while (i.hasNext()){
			AssemblyUtilityMonitor info = i.next();
			if (info.getInstance().equals(curi)){
				//dependences.put(info.getType().toString(), UNBOUND);
				info.setInstance(new CURI("UNBOUND"));
			}
		}
		
	}
	
	/**
	 * Send local QoS info to all the neighbors
	 * @param info
	 */
	public synchronized void sendToNeighbors(Vector<LocalUtilityMonitor> info) {
		this.gateway.sendToNeighbors(localUtilityMonitor.getInstance(), GOPrimeProtocol.GOPRIME_GOSSIP, info);
		log.debug("dissemeinating my predecessors to neighbors");
	}
	
	
	
	/**
	 * 
	 * @param c
	 */
	public abstract void handleGossipMessages(Collection<LocalUtilityMonitor> c);

	
}
