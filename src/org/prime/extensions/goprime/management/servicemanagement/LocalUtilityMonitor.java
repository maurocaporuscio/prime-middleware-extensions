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

package org.prime.extensions.goprime.management.servicemanagement;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import org.prime.core.comm.addressing.AURI;
import org.prime.core.comm.addressing.CURI;
import org.prime.description.Description;
import org.prime.description.rdl.QoS;
import org.prime.extensions.goprime.management.assemblymanagement.AssemblyUtilityMonitor;
import org.prime.extensions.goprime.management.assemblymanagement.GossipManager;
import org.prime.extensions.goprime.management.assemblymanagement.Metrics;
import org.prime.extensions.goprime.management.assemblymanagement.UtilityManager;

public class LocalUtilityMonitor extends UtilityManager implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7613316158744285746L;

	
	public LocalUtilityMonitor(Description desc){
		this.type = desc.getAURI();
		this.instance = desc.getCURI();
		this.reliability = desc.getQoS().getReliability();
		this.availability = desc.getQoS().getAvailability();
		this.cost = desc.getQoS().getCost();
		this.cost_rate = desc.getQoS().getCostRate();
		this.response_time = desc.getQoS().getResponseTime();
		this.structural = desc.getQoS().getStructural();
	}
	
	
	public LocalUtilityMonitor(AURI type, CURI instance, double reliability, double availability, double cost, int cost_rate, double response, double structural){
		this.type = type;
		this.instance = instance;
		this.reliability = reliability;
		this.availability = availability;
		this.cost = cost;
		this.cost_rate = cost_rate;
		this.response_time = response;
		this.structural = structural;
	}
	
	public LocalUtilityMonitor(AURI type, CURI instance, QoS qos){
		this.type = type;
		this.instance = instance;
		this.reliability = qos.getReliability();
		this.availability = qos.getAvailability();
		this.cost = qos.getCost();
		this.cost_rate = qos.getCostRate();
		this.response_time = qos.getResponseTime();
		this.structural = qos.getStructural();
	}
	
	
	
	public LocalUtilityMonitor(AssemblyUtilityMonitor info){
		this.type = info.getType();
		this.instance = info.getInstance();
		this.reliability = info.getUtility(Metrics.RELIABILITY);
		this.availability = info.getUtility(Metrics.AVAILABILITY);
		this.cost = info.getUtility(Metrics.COST);
		this.cost_rate = (info.getUtility(Metrics.COST_RATE) == 0.0) ? 0 : 1;
		this.response_time = info.getUtility(Metrics.RESPONSE_TIME);
		this.structural = info.getUtility(Metrics.STRUCTURAL);
	}
	

	public LocalUtilityMonitor(LocalUtilityMonitor info){
		this.type = info.type;
		this.instance = info.instance;
		this.reliability = info.reliability;
		this.availability = info.availability;
		this.cost = info.cost;
		this.cost_rate = info.cost_rate;
		this.response_time = info.response_time;
		this.structural = info.structural;
	}
	
	public LocalUtilityMonitor setCompaundUtilities(Collection<AssemblyUtilityMonitor> dependences){
		LocalUtilityMonitor res = new LocalUtilityMonitor(this);
		
		this.setCompaundReliabilityUtility(res, dependences);
		this.setCompaundAvailabilityUtility(res, dependences);
		this.setCompaundCostUtility(res, dependences);
		this.setCompaundResponseTimeUtility(res, dependences);
		
		return res;
	}
	
	
	
	/**
	 * Calculates the Reliability-based utility, supposing that the number 
	 * of times S' is invoked during the execution of S is n
	 * @param internalUtility is the utility of service S
	 * @param dependences the set of services S is bound to 
	 * 
	 */
	private void setCompaundReliabilityUtility(LocalUtilityMonitor res, Collection<AssemblyUtilityMonitor> dependences){
		
		Iterator<AssemblyUtilityMonitor> i = dependences.iterator();
		double others = 1;
		
		while (i.hasNext()){
			AssemblyUtilityMonitor s = i.next();
			if (s.getInstance().equals(GossipManager.UNBOUND.getInstance()) || (s.getUtility(Metrics.RELIABILITY) == null)){
				res.setUtility(Metrics.RELIABILITY, null);
				return;
			}else
				others *= Math.pow(s.getUtility(Metrics.RELIABILITY), s.getTimes());
		}
		
		res.setUtility(Metrics.RELIABILITY, this.reliability * others);
				
	}
	
	/**
	 * Calculates the Availability-based utility, supposing that the number 
	 * of times S' is invoked during the execution of S is n
	 * @param internalUtility is the utility of service S
	 * @param dependences the set of services S is bound to 
	 * 
	 */
	private void setCompaundAvailabilityUtility(LocalUtilityMonitor res, Collection<AssemblyUtilityMonitor> dependences){
		
		Iterator<AssemblyUtilityMonitor> i = dependences.iterator();
		double others = 1;
		
		while (i.hasNext()){
			AssemblyUtilityMonitor s = i.next();
			if (s.getInstance().equals(GossipManager.UNBOUND.getInstance()) || (s.getUtility(Metrics.AVAILABILITY) == null)){
				res.setUtility(Metrics.AVAILABILITY, null);
				return;
			}else
				others *= s.getUtility(Metrics.AVAILABILITY);
		}
		
		res.setUtility(Metrics.AVAILABILITY, this.availability * others);
				
	}
	
	
	
	/**
	 * Calculates the Cost-based utility, supposing that the number 
	 * of times S' is invoked during the execution of S is n
	 * @param internalUtility is the utility of service S
	 * @param dependences the set of services S is bound to 
	 * 
	 */
	private void setCompaundCostUtility(LocalUtilityMonitor res, Collection<AssemblyUtilityMonitor> dependences){
		
		Iterator<AssemblyUtilityMonitor> i = dependences.iterator();
		double others = 0;
		
		while (i.hasNext()){
			AssemblyUtilityMonitor s = i.next();
			if (s.getInstance().equals(GossipManager.UNBOUND.getInstance()) || (s.getUtility(Metrics.COST) == null)){
				res.setUtility(Metrics.COST, null);
				return;
			}else
				if (s.getCostRate() == 0)
					others += s.getUtility(Metrics.COST);
				else others += s.getUtility(Metrics.COST) * s.getTimes();
		}
		
		res.setUtility(Metrics.COST, this.cost + others);
				
	}
	
	/**
	 * Calculates the Cost-based utility, supposing that the number 
	 * of times S' is invoked during the execution of S is n
	 * @param internalUtility is the utility of service S
	 * @param dependences the set of services S is bound to 
	 * 
	 */
	private void setCompaundResponseTimeUtility(LocalUtilityMonitor res, Collection<AssemblyUtilityMonitor> dependences){
		
		Iterator<AssemblyUtilityMonitor> i = dependences.iterator();
		double others = 0;
		
		while (i.hasNext()){
			AssemblyUtilityMonitor s = i.next();
			if (s.getInstance().equals(GossipManager.UNBOUND.getInstance()) || (s.getUtility(Metrics.RESPONSE_TIME) == null)){
				res.setUtility(Metrics.RESPONSE_TIME, null);
				return;
			}else
				others += s.getUtility(Metrics.RESPONSE_TIME) * s.getTimes();
		}
		
		res.setUtility(Metrics.RESPONSE_TIME, this.response_time + others);
				
	}

}
