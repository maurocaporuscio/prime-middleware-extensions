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

import java.io.Serializable;

import org.smscom.prime.core.comm.addressing.AURI;
import org.smscom.prime.core.comm.addressing.CURI;
import org.smscom.prime.extension.goprime.management.servicemanagement.LocalUtilityMonitor;

public class AssemblyUtilityMonitor extends UtilityManager implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2390212596582283661L;
	private Integer times;
	private Metrics metrics;
	private double rank;
	
	
	public AssemblyUtilityMonitor(AURI type, CURI instance, Integer times, Metrics metrics, double rank){
		this.type = type;
		this.instance = (instance == null)? new CURI("UNBOUND") : instance;
		this.times = (times == null)? 0 : times;
		this.metrics = (times == null)? Metrics.NONE : metrics;
		this.reliability = Double.MIN_VALUE;
		this.availability = Double.MIN_VALUE;
		this.cost = Double.MIN_VALUE;
		this.cost_rate = 0;
		this.response_time = Double.MIN_VALUE;
		this.structural = Double.MIN_VALUE;
		this.rank = 0;
	}
	
	
	public AssemblyUtilityMonitor(AssemblyUtilityMonitor base){
		this.type = base.type;
		this.instance = base.instance;
		this.reliability = base.reliability;
		this.availability = base.availability;
		this.cost = base.cost;
		this.cost_rate = base.cost_rate;
		this.response_time = base.response_time;
		this.structural = base.structural;
		this.times = base.times;
		this.metrics = base.metrics;
		this.rank = base.rank;
	}
	
	
	public AssemblyUtilityMonitor(LocalUtilityMonitor base, Integer times, Metrics metrics, double rank){
		this.type = base.type;
		this.instance = base.instance;
		this.reliability = base.reliability;
		this.availability = base.availability;
		this.cost = base.cost;
		this.cost_rate = base.cost_rate;
		this.response_time = base.response_time;
		this.structural = base.structural;
		this.times = times;
		this.metrics = metrics;
		this.rank = rank;
	}

	
	public Boolean better(LocalUtilityMonitor info) {
		// TODO Auto-generated method stub
		
		if (this.metrics == Metrics.NONE)
			return false;
		
		if ((this.getUtility(this.metrics) == null) && (info.getUtility(this.metrics) == null))
			return false;
		
		if (this.getUtility(this.metrics) == null ) //this is -inf 
			return true;
		
		if (info.getUtility(this.metrics) == null) //the other is -inf
			return false;
		
		if(this.getUtility(this.metrics) < info.getUtility(this.metrics))
			return true;
		else return false;
		
	}

	
	public void setRank(double rank) {
		this.rank = rank;
	}
	
	public double getRank() {
		return rank;
	}
	
	public Integer getTimes() {
		return times;
	}

	public void setTimes(Integer times) {
		this.times = times;
	}

	public int getCostRate() {
		return this.cost_rate;
	}


	public Metrics getMetrics() {
		return this.metrics;
	}
	
	public void setMetrics(Metrics metrics){
		this.metrics = metrics;
	}
	
}
