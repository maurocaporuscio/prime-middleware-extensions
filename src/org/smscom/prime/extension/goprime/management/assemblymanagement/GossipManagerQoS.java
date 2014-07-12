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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smscom.prime.core.comm.ICommGateway;
import org.smscom.prime.core.comm.addressing.AURI;
import org.smscom.prime.extension.goprime.management.servicemanagement.LocalUtilityMonitor;


public class GossipManagerQoS extends GossipManager {

	protected  Logger log = (Logger)LoggerFactory.getLogger(this.getClass().getName());
	
	
	public GossipManagerQoS(ICommGateway gateway, LocalUtilityMonitor localinfo,Map<AURI, AssemblyUtilityMonitor> dependences, long time) {
		super(gateway, localinfo, dependences, time);
		// TODO Auto-generated constructor stub
	}

	
	private void substituteDependency(AssemblyUtilityMonitor o, LocalUtilityMonitor n){
		AssemblyUtilityMonitor sub = new AssemblyUtilityMonitor(n, o.getTimes(), o.getMetrics(), 1);
		dependences.remove(o.getType());
		dependences.put(sub.getType(), sub);
	}
	
	
	
	private boolean checkDependencies(AURI type){
		if (this.dependences == null) 
			return false;
		if (this.dependences.get(type) == null)
			return false;
		return true;
	}
	
	private AssemblyUtilityMonitor checkPredecessors(LocalUtilityMonitor s){
		if (dependences == null) 
			return null;
		
		AssemblyUtilityMonitor tmp = dependences.get(s.getType());
		if (tmp.getInstance().equals(UNBOUND.getInstance()))
			return null;
		
		//Se s e' un servizio a cui sono gia connesso, aggiorno la sua utility
		try{
			if(s.compareTo(tmp) != 0){
				AssemblyUtilityMonitor n = new AssemblyUtilityMonitor(s, tmp.getTimes(), tmp.getMetrics(), 1);
				dependences.remove(n.getType());
				dependences.put(n.getType(), n);
				return n;
			}else return tmp;
		}catch(ClassCastException e){
			return tmp;
		}
	}
	
	
	private void updateDependency(LocalUtilityMonitor n){
		AssemblyUtilityMonitor o = dependences.get(n.getType());
		AssemblyUtilityMonitor sub = new AssemblyUtilityMonitor(n, o.getTimes(), o.getMetrics(), 1);
		dependences.put(sub.getType(), sub);
	}
	
	
	//Version 2
	public void handleGossipMessages(Collection<LocalUtilityMonitor> info){
		//@SuppressWarnings("unchecked")
		//Vector<LocalQoS> info = (Vector<LocalQoS>) msg.getPayload();
		//ResourceInfo s_j = info.get(0);                   	//S_j

		for(LocalUtilityMonitor s: info){             			//for all S \in B
			//LocalQoS s = info.get(i);
			if (checkDependencies(s.getType())){            		//if t[S] \in d[S_i] 

				AssemblyUtilityMonitor s_j = checkPredecessors(s);
				if (s_j != null){ 									//if S \in Pred[S_i]
					if (s_j.getType().equals(s.getType())){

						if(s_j.better(s)){			//if U(S) > S(S_j)
							this.substituteDependency(s_j, s); 	//Pred[S_i] <- Pred[S_i] \ {S_j} U {S} 
						}
					}
				} else
					//if (s.getUtility() != null)
					this.updateDependency(s);		//Pred[S_i] <- Pred[S_i] U {S}
			}	
		}

	}

}
