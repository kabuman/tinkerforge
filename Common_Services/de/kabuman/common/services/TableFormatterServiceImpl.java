package de.kabuman.common.services;

/**
 * Implementation of a simple table model
 * 
 * Create a new row via constructor 
 * Fill the row with addValue(pos, value) with different values on different positions
 * Print row via getRow() 
 * Create a new row via newRow()
 * ...
 */
public class TableFormatterServiceImpl implements TableFormatterService {

	int rowLength = 0;
	StringBuffer row;
	
	/**
	 * Constructor<br>
	 * Set up the initial row length<br>
	 * Creates a first row with the given length<br>
	 * Throws IllegalArgumentException if parameter conditions are violated.
	 * 
	 * @param rowLength - the row length (rowLength > 0)
	 */
	public TableFormatterServiceImpl(int rowLength){
		if (rowLength <= 0){
			throw new IllegalArgumentException("TableFormatterServiceImpl:: 'rowLength <= 0' violation detected. rowLength="+rowLength);
		}

		this.rowLength = rowLength;
		row = createRow(rowLength);
	}
	
	/* (non-Javadoc)
	 * @see de.kabuman.common.services.TableFormatterService#newRow()
	 */
	public String newRow() {
		row = createRow(rowLength);
		return row.toString();
	}

	/* (non-Javadoc)
	 * @see de.kabuman.common.services.TableFormatterService#newRow(int, java.lang.String)
	 */
	public String newRow(int pos, String value) {
		row = createRow(rowLength);
		return addValue(pos, value);
	}

	/* (non-Javadoc)
	 * @see de.kabuman.common.services.TableFormatterService#addValue(int, java.lang.String)
	 */
	public String addValue (int pos, String value) {
		if (value == null){
			throw new IllegalArgumentException("TableFormatterServiceImpl.addValue:: 'value not null' violation detected.");
		}
		if (pos >= rowLength){
			throw new IllegalArgumentException("TableFormatterServiceImpl.addValue:: 'pos < rowLength' violation detected. pos="+pos+" rowLength="+rowLength);
		}
		if (pos < 0){
			throw new IllegalArgumentException("TableFormatterServiceImpl.addValue:: 'pos >= 0' violation detected. pos="+pos+" rowLength="+rowLength);
		}

		row.replace(pos, pos + value.length(), value);
		return row.toString();
	}

	/* (non-Javadoc)
	 * @see de.kabuman.common.services.TableFormatterService#getRow()
	 */
	public String getRow() {
		return row.toString();
	}

	/**
	 * Returns a fixed String filled with blanks
	 * 
	 * @param rowLength -the length of the string to create
	 * @return String - the created and with blanks filled String
	 */
	private StringBuffer createRow(int rowLength){
		StringBuffer sb = new StringBuffer(rowLength);
		for (int i = 0; i < rowLength; i++) {
			sb.append(' ');
		}
		return sb;
	}
	
}
