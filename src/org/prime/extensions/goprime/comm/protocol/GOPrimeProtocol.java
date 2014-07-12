package org.prime.extensions.goprime.comm.protocol;

import org.prime.core.comm.protocol.PrimeProtocol;

public class GOPrimeProtocol extends PrimeProtocol{

	public static final GOPrimeProtocol GOPRIME_GOSSIP = new GOPrimeProtocol(5,"GOPRIME_GOSSIP","");
	
	
	
	public GOPrimeProtocol(int code, String name, String description) {
		super(code, name, description);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6813497563966781928L;
	
	
}
