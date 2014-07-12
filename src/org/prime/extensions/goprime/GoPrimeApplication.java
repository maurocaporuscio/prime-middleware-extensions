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

package org.prime.extensions.goprime;

import java.util.Map;

import org.prime.core.PrimeApplication;
import org.prime.core.comm.IPrimeConnection;
import org.prime.core.comm.addressing.AURI;
import org.prime.core.comm.addressing.CURI;
import org.prime.description.Description;
import org.prime.extensions.goprime.comm.DynamicHTTPConnection;
import org.prime.extensions.goprime.comm.protocol.GOPrimeProtocol;
import org.prime.extensions.goprime.management.assemblymanagement.AssemblyManager;
import org.prime.extensions.goprime.management.assemblymanagement.AssemblyUtilityMonitor;
import org.prime.extensions.goprime.management.assemblymanagement.GossipManager;
import org.prime.extensions.goprime.management.assemblymanagement.GossipManagerQoS;
import org.prime.extensions.goprime.management.assemblymanagement.GossipManagerSemantic;
import org.prime.extensions.goprime.management.assemblymanagement.Metrics;
import org.prime.extensions.goprime.management.servicemanagement.LocalUtilityMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Applications should extend this class in order to benefit from the PRIME framework.
 * @author Mauro Caporuscio
 *
 */
public class GoPrimeApplication extends PrimeApplication{

	
	protected  Logger log = (Logger)LoggerFactory.getLogger(this.getClass().getName());
		
	private AssemblyManager assemblym;
	
	private LocalUtilityMonitor localUtilityMonitor;
   
	
	

	/**
	 * 
	 * @param id PrimeApplication Identifier (serves as base URI for hosted resources)
	 * @throws Exception
	 */
	public GoPrimeApplication(String id)  throws Exception {
		super(id);
		
		this.assemblym = new AssemblyManager();
	}
	
	/**
	 * 
	 * @param id PrimeApplication Identifier (serves as base URI for hosted resources)
	 * @param httpPort
	 * @throws Exception
	 */
	public GoPrimeApplication(String id, int httpPort)  throws Exception {
		super(id, httpPort);
		
		this.assemblym = new AssemblyManager();
	}
		

	/**
	 * Activates the Opportunistic Binding procedure based on the Gossip Algorithm.
	 * @param dependences  Maps AURIs of interest to the CURIs satisfying the dependences 
	 */
	public void startGossipManagerQoS(LocalUtilityMonitor localUtilityMonitor, Map<AURI, AssemblyUtilityMonitor> dependences, long time){		
		this.localUtilityMonitor = localUtilityMonitor;
		GossipManager b = new GossipManagerQoS(this.gateway, localUtilityMonitor, dependences, time);
		b.start();
		assemblym.addGossipManager(localUtilityMonitor.getInstance(), b);
	}
	
	
	/**
	 * Activates the Opportunistic Binding procedure based on the Semantic Gossip Algorithm.
	 * @param dependences  Maps AURIs of interest to the CURIs satisfying the dependences 
	 */
	public void startGossipManagerSemantic(LocalUtilityMonitor localUtilityMonitor, Map<AURI, AssemblyUtilityMonitor> dependences, long time){
		this.localUtilityMonitor = localUtilityMonitor;
		GossipManager b = new GossipManagerSemantic(this.gateway, registry.getMatchMaker(), localUtilityMonitor, dependences, time);
		b.start();
		assemblym.addGossipManager(localUtilityMonitor.getInstance(), b);
	}
	

	
	public AssemblyManager getAssemblyManager(){
		return this.assemblym;
	}
	
	public LocalUtilityMonitor getLocalQoS() {
		return localUtilityMonitor;
	}

	public void setLocalQoS(LocalUtilityMonitor localUtilityMonitor) {
		this.localUtilityMonitor = localUtilityMonitor;
	}
	
	
	public void updateLocalQoS(CURI curi, Metrics metrics, Double value){
		Description d = this.registry.getDescription(curi);
		
		switch (metrics){
			case AVAILABILITY:  d.getQoS().setAvailability(value);
								break;
			case RESPONSE_TIME: d.getQoS().setResponseTime(value);
								break;
			case COST:			d.getQoS().setCost(value);
								break;						
			case RELIABILITY:   d.getQoS().setReliability(value);
								break;
			case STRUCTURAL:    d.getQoS().setStructural(value);
								break;
			case COST_RATE:     d.getQoS().setCostRate(value);
								break;
			default:            break;
		}
		
		this.assemblym.getGossipManager(curi).setLocalQoS(new LocalUtilityMonitor(d));
		//this.assemblym.getGossipManager(curi).update();
	}
	
	
	
	
	@Override
	public void startPrimeApplication() throws Exception{
		super.startPrimeApplication();
		
		this.dispatcher.addHandler(GOPrimeProtocol.GOPRIME_GOSSIP, this.assemblym);
		
	}
	
	
	
	
	
	/**
	 * Returns a HTTP connection towards the resource identified by target
	 * @param source  the CURI of the local resource 
	 * @param instance the CURI of the destination resource
	 * @return the PrimeConnection object managing the connection
	 */
	public IPrimeConnection getConnection(CURI source, CURI instance) {
		// TODO Auto-generated method stub
		return new DynamicHTTPConnection(this.assemblym.getGossipManager(source), instance);
	}	
}
