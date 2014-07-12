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

public enum Metrics {
	RELIABILITY,
	AVAILABILITY,
	COST,
	COST_RATE,
	RESPONSE_TIME,
	STRUCTURAL,
	NONE;

	public static Metrics parseString(String stringValue) {
		// TODO Auto-generated method stub
		if (stringValue.equalsIgnoreCase("reliability"))
			return RELIABILITY;
		if (stringValue.equalsIgnoreCase("availability"))
			return AVAILABILITY;
		if (stringValue.equalsIgnoreCase("cost"))
			return COST;
		if (stringValue.equalsIgnoreCase("cost_rate"))
			return COST_RATE;
		if (stringValue.equalsIgnoreCase("response_time"))
			return RESPONSE_TIME;
		if (stringValue.equalsIgnoreCase("structural"))
			return STRUCTURAL;
		
		return NONE;
	}
}
