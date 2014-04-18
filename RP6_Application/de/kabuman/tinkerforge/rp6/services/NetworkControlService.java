package de.kabuman.tinkerforge.rp6.services;




public interface NetworkControlService {
	public String getSignalStrengthCP();
	public String getSignalStrengthRC();
	public String getSignalStrengthRP6();
	public String getChibiAddressCP();
	public String getChibiAddressRP6();
	public String getChibiAddressRC();
	public String getChibiMasterAddressCP();
	public String getChibiMasterAddressRP6();
	public String getChibiMasterAddressRC();
	public String getChibiErrorLogCP();
	public String getChibiErrorLogRC();
	public String getChibiErrorLogRP6();
	
	public boolean isSignalToWeakRP6(short thresholdSignalStrength);
}
