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
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smscom.prime.core.comm.IMessageHandler;
import org.smscom.prime.core.comm.addressing.AURI;
import org.smscom.prime.core.comm.addressing.CURI;
import org.smscom.prime.core.comm.protocol.PrimeMessage;
import org.smscom.prime.extension.goprime.comm.protocol.GOPrimeProtocol;
import org.smscom.prime.extension.goprime.management.servicemanagement.LocalUtilityMonitor;


public class AssemblyManager implements IMessageHandler{

	protected  Logger log = (Logger)LoggerFactory.getLogger(this.getClass().getName());
	
	private Map<CURI, GossipManager> gossipManagers = null;
	
	
	public AssemblyManager(){
		this.gossipManagers = new HashMap<CURI, GossipManager>();	
	}
	
	@Override
	public void handleMessage(PrimeMessage pck) {
		
		if (pck.getType().equals(GOPrimeProtocol.GOPRIME_GOSSIP)){
			
			@SuppressWarnings("unchecked")
			Vector<LocalUtilityMonitor> v = (Vector<LocalUtilityMonitor>) pck.getPayload();
			
			log.debug("GOSSIPMESSAGEPACKET: ");
			
			if((this.gossipManagers != null) && !this.gossipManagers.isEmpty()){
				for(GossipManager b: this.gossipManagers.values()){
				  b.handleGossipMessages(v);
				}
			}
			
		}  	
	}
	
	/**
	 * Given a Resource CURI, returns the GossipManager object responsible for such a resource
	 * @param curi The Resource ID
	 * @return GossipManager object
	 */
	public GossipManager getGossipManager(CURI curi){
		return this.gossipManagers.get(curi);
	}
	
	/**
	 * Add the GossipManager object responsible for the Resource identified by curi
	 * @param curi The Resource ID
	 * @param gm The GossipManager object
	 */
	public void addGossipManager(CURI curi, GossipManager gm){
		gossipManagers.put(curi, gm);
	}
	
	
	/**
	 * Check whether dependences are solved or not. If the service has no dependences it is always true;
	 * @return true if dependences are satisfied
	 */
	public boolean isDependencesResolved(CURI key){
		if (this.gossipManagers.isEmpty())
			return true;
		
		GossipManager b = this.gossipManagers.get(key);
		return b.isDependencesResolved();
	}
	
	/**
	 * Returns the set of unresolved dependences
	 * @return a Collection of Strings identifying the unresolved dependences
	 */
	public Collection<AURI> getUnresolvedDependences(CURI key){
		return this.gossipManagers.get(key).getUnsolvedDependences();
	}
	
	
	/**
	 * Return the information of the resource actually bound to the required type
	 * @param type of the needed resource
	 * @return The information about the resource implementing the required type
	 */
	public AssemblyUtilityMonitor getAssemblyBinding(CURI key, AURI type){
		return this.gossipManagers.get(key).getBinding(type);
	}

}
