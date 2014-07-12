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

package org.prime.extensions.goprime.comm;

import org.prime.core.comm.IPrimeConnection;
import org.prime.core.comm.PrimeHTTPConnection;
import org.prime.core.comm.addressing.CURI;
import org.prime.extensions.goprime.management.assemblymanagement.GossipManager;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Manages HTTP connection towards remote resources.
 * @author Mauro Caporuscio
 *
 */
public class DynamicHTTPConnection extends PrimeHTTPConnection implements IPrimeConnection {
	
	protected  Logger log = (Logger)LoggerFactory.getLogger(this.getClass().getName());
	
	private GossipManager binder = null;
	private CURI curi;
	
	
	/**
	 * Creates an HTTPConnetion towards the destination 
	 * @param binder the dynamic Binder in charge of managing the connection
	 * @param curi the CURI identifying the destination
	 */
	public DynamicHTTPConnection(GossipManager binder, CURI curi){
		super(curi);
		this.curi = curi;
		this.binder = binder;
	}
	
	
	/**
	 * Creates an HTTPConnetion towards the destination 
	 * @param curi the CURI identifying the destination
	 */
	public DynamicHTTPConnection(CURI curi){
		super(curi);
		this.curi = curi;
		this.binder = null;
	}
	
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.core.comm.PrimeConnection#get()
	 */
	@Override
	public Representation get(){
		try{
			return super.get();
		}catch(Exception e){
			//If I'm using the dynamic binder then unbind the erroneous curi
			if (binder != null) binder.unbind(curi);
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.core.comm.PrimeConnection#get(org.restlet.data.MediaType)
	 */

	@Override
	public Representation get(MediaType contenttype){
		try{
			return super.get(contenttype);
		}catch(Exception e){
			//If I'm using the dynamic binder then unbind the erroneous curi
			if (binder != null) binder.unbind(curi);
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.core.comm.PrimeConnection#post(org.restlet.representation.Representation)
	 */

	@Override
	public Representation post(Representation r){
		try{
			return super.post(r);
		}catch(Exception e){
			e.printStackTrace();
			//If I'm using the dynamic binder then unbind the erroneous curi
			if (binder != null) binder.unbind(curi);
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.core.comm.PrimeConnection#post(org.restlet.representation.Representation, org.restlet.data.MediaType)
	 */
	@Override
	public Representation post(Representation r, MediaType contenttype){
		try{
			return super.post(r, contenttype);
		}catch(Exception e){
			e.printStackTrace();
			//If I'm using the dynamic binder then unbind the erroneous curi
			if (binder != null) binder.unbind(curi);
			return null;
		}
	}
	
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.core.comm.PrimeConnection#put(org.restlet.representation.Representation)
	 */
	@Override
	public Representation put(Representation r){
		try{
			return super.put(r);
		}catch(Exception e){
			//If I'm using the dynamic binder then unbind the erroneous curi
			if (binder != null) binder.unbind(curi);
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.core.comm.PrimeConnection#put(org.restlet.representation.Representation, org.restlet.data.MediaType)
	 */
	@Override
	public Representation put(Representation r, MediaType contenttype){
		try{
			return super.put(r, contenttype);
		}catch(Exception e){
			//If I'm using the dynamic binder then unbind the erroneous curi
			if (binder != null) binder.unbind(curi);
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.core.comm.PrimeConnection#options()
	 */

	@Override
	public Representation options(){
		try{
			return super.options();
		}catch(Exception e){
			//If I'm using the dynamic binder then unbind the erroneous curi
			if (binder != null) binder.unbind(curi);
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.core.comm.PrimeConnection#options(org.restlet.data.MediaType)
	 */

	@Override
	public Representation options(MediaType contenttype){
		try{
			return super.options(contenttype);
		}catch(Exception e){
			//If I'm using the dynamic binder then unbind the erroneous curi
			if (binder != null) binder.unbind(curi);
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.core.comm.PrimeConnection#delete()
	 */
	@Override
	public Representation delete(){
		try{
			return super.delete();
		}catch(Exception e){
			//If I'm using the dynamic binder then unbind the erroneous curi
			if (binder != null) binder.unbind(curi);
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.smscom.prime.core.comm.PrimeConnection#delete(org.restlet.data.MediaType)
	 */
	@Override
	public Representation delete(MediaType contenttype){
		try{
			return super.delete(contenttype);
		}catch(Exception e){
			//If I'm using the dynamic binder then unbind the erroneous curi
			if (binder != null) binder.unbind(curi);
			return null;
		}
	}
	
}
