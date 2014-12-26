package de.kabuman.tinkerforge.services.config;

public class CfgEmail {
	
	private boolean emailRequested;
    private String host;
    private int port;
    private String user;
    private String password;
    private String sendFrom;
	private String[] sendTo;

	public CfgEmail(
			boolean emailRequested,
		    String host,
		    int port,
		    String user,
		    String password,
		    String sendFrom,
			String[] sendTo){
		this.emailRequested = emailRequested;
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
		this.sendFrom = sendFrom;
		this.sendTo =sendTo;
	}

	public String getHost() {
		return host;
	}

	public Integer getPort() {
		return port;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getSendFrom() {
		return sendFrom;
	}

	public String[] getSendTo() {
		return sendTo;
	}

	public boolean isEmailRequested() {
		return emailRequested;
	}


}
