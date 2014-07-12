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

package org.prime.extensions.goprime.management.assemblymanagement;

import java.util.Collection;
import java.util.Map;

import org.prime.core.comm.ICommGateway;
import org.prime.core.comm.addressing.AURI;
import org.prime.dns.IMatchMaker;
import org.prime.extensions.goprime.management.servicemanagement.LocalUtilityMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class GossipManagerSemantic extends GossipManager{
	
	protected  Logger log = (Logger)LoggerFactory.getLogger(this.getClass().getName());
	
	
	IMatchMaker matchmaker;

	
	
	public GossipManagerSemantic(ICommGateway gateway, IMatchMaker matchmaker, LocalUtilityMonitor localinfo, Map<AURI, AssemblyUtilityMonitor> dependences, long time) {
		super(gateway, localinfo, dependences, time);
		this.matchmaker = matchmaker;
	}

	private double checkDependencies(AURI type){
		if (this.dependences == null) 
			return 0;
		
		for(AURI s: this.dependences.keySet()){
			double res = this.matchmaker.matchInstance(type, s);
			if (res > 0)
				return res;
		}
		
		return 0;
	}
	
		
	private AssemblyUtilityMonitor checkPredecessors(LocalUtilityMonitor s){
		if (dependences == null) 
			return null;
		
		for(AURI key: this.dependences.keySet()){
			if (this.matchmaker.matchInstance(key, s.getType()) > 0){
				AssemblyUtilityMonitor tmp = dependences.get(key);
				if (tmp.getInstance().equals(UNBOUND.getInstance()))
					return null;
				
				//Se s e' un servizio a cui sono gia connesso, aggiorno la sua utility
				try{
					if(s.compareTo(tmp) != 0){
						AssemblyUtilityMonitor n = new AssemblyUtilityMonitor(s, tmp.getTimes(), tmp.getMetrics(), tmp.getRank());
						n.setType(key);
						dependences.remove(key);
						dependences.put(key, n);
						return n;
					}else return tmp;
				}catch(ClassCastException e){
					return tmp;
				}		
			}
		}
		
		return null;
		
	}
	
	
	private void substituteDependency(AssemblyUtilityMonitor o, LocalUtilityMonitor n, double rank){
		AssemblyUtilityMonitor sub = new AssemblyUtilityMonitor(n, o.getTimes(), o.getMetrics(), rank);
		sub.setType(o.getType());
		dependences.remove(o.getType());
		dependences.put(sub.getType(), sub);
	}
	
	private void updateDependency(LocalUtilityMonitor n, double rank){
		
		for(AURI key: this.dependences.keySet()){
			if (this.matchmaker.matchInstance(key, n.getType()) > 0){
				AssemblyUtilityMonitor o = dependences.remove(key);
				AssemblyUtilityMonitor sub = new AssemblyUtilityMonitor(n, o.getTimes(), o.getMetrics(), rank);
				sub.setType(key);
				dependences.put(sub.getType(), sub);
				
				return ;
			}
		}
		
		
		
	}
	

	@Override
	public void handleGossipMessages(Collection<LocalUtilityMonitor> info){
		//@SuppressWarnings("unchecked")
		//Vector<LocalQoS> info = (Vector<LocalQoS>) msg.getPayload();
		//ResourceInfo s_j = info.get(0);                   	//S_j
		
		for(LocalUtilityMonitor s: info){             			//for all S \in B
			//LocalQoS s = info.get(i);
			double depRank = checkDependencies(s.getType());
			if (depRank > 0){            		//if t[S] \in d[S_i] 

				AssemblyUtilityMonitor s_j = checkPredecessors(s);
				if (s_j != null){ 									//if S \in Pred[S_i]
					double subRank = matchmaker.matchInstance(s_j.getType(), s.getType());
					if (subRank > 0){
						if(s_j.better(s)){			             //if U(S) > S(S_j)
							this.substituteDependency(s_j, s, subRank);  //Pred[S_i] <- Pred[S_i] \ {S_j} U {S}
						}
					}
				} else
					//if (s.getUtility() != null)
					this.updateDependency(s, depRank);		//Pred[S_i] <- Pred[S_i] U {S}
			}	
		}
		
	}

	

	
}
