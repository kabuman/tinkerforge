package de.kabuman.common.services;

/**
 * Class for "CountDownService"
 *
 * CountDownService provides a helper class 
 * to administrate a counter to use in a loop
 */
public class CountDownServiceImpl implements CountDownService{
	
	// default values (used with usage of standard constructor)
	int from = 3;
	int fromForReset = 3;
	
	/**
	 * Constructor
	 * 
	 * @param from - the "from" init value
	 */
	public CountDownServiceImpl(int from){
		this.from = from;
		this.fromForReset = from;
	}
	
	/**
	 * Constructor
	 * works with default "from" value 
	 */
	public CountDownServiceImpl(){
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.common.services.CountDownService#down()
	 */
	public boolean down(){
		if (from-- <= 0){
			return true;
		} else { 
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see de.kabuman.common.services.CountDownService#isDown()
	 */
	public boolean isDown(){
		if (from <= 0){
			return true;
		} else {
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.common.services.CountDownService#setFrom(int)
	 */
	public void reset(int from){
		this.from = from;
		this.fromForReset = from;
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.common.services.CountDownService#reset()
	 */
	public void reset(){
		from = fromForReset;
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.common.services.CountDownService#stop()
	 */
	public void stop(){
		from = 0;
	}
}
