package de.kabuman.common.services;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * Provides methods to resolve URL addr to IP addr <br>
 * 
 * Contains static methods for an easy use for simple resolve <br>
 */
public class InetService {

	private String url = null;
	private String ip = null;
	
	/**
	 * Constructor - Instantiates new Service <br>
	 * for resolving URL -> IP addr  <br>
	 * and further helper
	 */
	public InetService() {
	}
	
	/**
	 * Constructor - Instantiates new service <br> 
	 * Resolves the given host <br>
	 * 
	 * @param host - the host (url or ip addr)
	 * @throws UnknownHostException
	 */
	public InetService(String host) throws UnknownHostException {
		resolveURL(host);
	}
	
	
	/**
	 * Resolves the given host (URL or IP) <br>
	 * If host = valid IP addr then the same IP will be returned <br>
	 * If host = URL then the resolved IP will be returned  <br>
	 * 
	 * @param host - URL or IP
	 * 
	 * @return IP addr
	 * @throws UnknownHostException
	 */
	public String resolveURL(String host) throws UnknownHostException{
		if (isIP(host)){
			this.ip = host;
			this.url = null;
		} else {
			this.ip = InetAddress.getByName(host).getHostAddress();
			this.url = host;
		}
		return this.ip;
	}
	
	/**
	 * Resolves the known host again <br>
	 * If host = valid IP addr then the same IP will be returned <br>
	 * If host = URL then the new resolved IP will be returned <br>
	 * => to use if DSL Router changes his IP Addr e.g. <br>
	 * 
	 * @return IP addr
	 * @throws UnknownHostException
	 */
	public String resolveURL() throws UnknownHostException{
		if (url == null && ip == null){
			throw new IllegalArgumentException("InetService:resolveURL(): no URL and no IP addr was set");
		}
		if (url == null){
			return ip;
		} else {
			ip = InetAddress.getByName(url).getHostAddress();
			return ip;
		}
	}

	/**
	 * @return the former resolved IP addr
	 */
	public String getIP(){
		return ip;
	}
	
	/**
	 * @return the known URL
	 * Can be null if the given host was a valid IP addr
	 */
	public String getURL(){
		return url;
	}

	/**
	 * Returns true if the given host is a valid IP addr
	 * 
	 * @param host - the given host
	 * @return true: IP addr   false: not valid IP addr
	 */
	public static synchronized boolean isIP(String host){
		String[] hostParts = host.split("\\.");
		for (String hostPart : hostParts) {
			try {
				int j = Integer.parseInt(hostPart);
				if (j<1 || j>255){
					return false;
				} else {
					return true;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	/**
	 * Resolves the given host. <br>
	 * if host = URL then returns the resolved IP <br>
	 * if host = valid IP then returns the same IP <br>
	 * 
	 * @param host (URL or IP)
	 * @return
	 * @throws UnknownHostException
	 */
	public static synchronized String staticResolveURL(String host) throws UnknownHostException{
		if (isIP(host)){
			return InetAddress.getByName(host).getHostAddress();
		} else {
			return host;
		}
	}
}
