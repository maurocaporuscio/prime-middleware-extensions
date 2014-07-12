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

public abstract class UtilityManager implements Serializable, Comparable<AssemblyUtilityMonitor>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3557867785306055382L;
	
	
	protected AURI 		type;
	protected CURI 		instance;
	protected Double 	reliability;
	protected Double 	availability;
	protected Double	cost;
	protected Integer	cost_rate;
	protected Double	response_time;
	protected Double	structural;
	
	
	
	public AURI getType() {
		return type;
	}


	public void setType(AURI type) {
		this.type = type;
	}


	public CURI getInstance() {
		return instance;
	}


	public void setInstance(CURI instance) {
		this.instance = instance;
	}


	
	public Double getUtility(Metrics metrics) {
		switch (metrics){
			case RELIABILITY: 	return this.reliability;
			case AVAILABILITY: 	return this.availability;
			case COST:		  	if (this.cost != null) return -this.cost; else return this.cost;
			case COST_RATE:		if (this.cost_rate == 0) return 0.0; else return 1.0;
			case RESPONSE_TIME: if (this.response_time != null) return -this.response_time; else return this.response_time;
			case STRUCTURAL:    return this.structural;
			case NONE:			return 0.0;
		}
		return null;
	}
	
	public void setUtility(Metrics metrics, Double value) {
		switch (metrics){
			case RELIABILITY: 	this.reliability = value;
								break;
			case AVAILABILITY: 	this.availability = value;
								break;
			case COST:		  	if(value != null) this.cost  = -value;
									else this.cost = value;
								break;
								
			case COST_RATE:		this.cost_rate  = (value == 0.0)? 0 : 1;
								break;					
								
			case RESPONSE_TIME: if(value != null) this.response_time = -value;
									else this.response_time = value;
								break;
			case STRUCTURAL:    this.structural  = value;
								break;
			case NONE: 			return;
		}
	}
	
	protected boolean checkValues(){
		if (this.availability == null || 
				this.cost == null ||
				this.cost_rate == null ||
				this.reliability == null ||
				this.response_time == null ||
				this.structural == null)
			return false;
		return true;
	}
	
	
	@Override
	public int compareTo(AssemblyUtilityMonitor arg0) {
		AssemblyUtilityMonitor o = (AssemblyUtilityMonitor) arg0;
		if (! (this.checkValues() && o.checkValues()))
			throw new ClassCastException();
		
		if (this.instance.equals(o.instance)){ 
			if (this.availability == o.availability &&
					this.cost == o.cost &&
					this.cost_rate == o.cost_rate &&
					this.reliability == o.reliability &&
					this.response_time == o.response_time &&
					this.structural == o.structural )
				return 0;
			if (this.availability < o.availability ||
					this.cost  < o.cost ||
					this.reliability < o.reliability ||
					this.response_time < o.response_time ||
					this.structural < o.structural )
				return -1;
			if (this.availability > o.availability ||
					this.cost  > o.cost ||
					this.reliability > o.reliability ||
					this.response_time > o.response_time ||
					this.structural > o.structural )
				return 1;
		}else throw new ClassCastException();
		
		return 0;
	}
	
}
