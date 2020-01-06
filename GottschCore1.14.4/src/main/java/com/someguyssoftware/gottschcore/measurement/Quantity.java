/**
 * 
 */
package com.someguyssoftware.gottschcore.measurement;

/**
 * A class to hold a minimum and maximum value for a value range.
 * @author Mark Gottschling on Jul 18, 2016
 *
 */
public class Quantity {
	private double min;
	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	private double max;
	
	
	/**
	 * 
	 */
	public Quantity() {
		
	}
	
	/**
	 * 
	 * @param q
	 */
	public Quantity(Quantity q) {
		setMin(q.getMin());
		setMax(q.getMax());
	}

	/**
	 * @param min
	 * @param max
	 */
	public Quantity(double min, double max) {
		super();
		this.min = min;
		this.max = max;
	}

	/**
	 * 
	 * @return
	 */
	public Quantity copy() {
		return new Quantity(this);
	}
	
	/**
	 * 
	 * @return
	 */
	public int getMinInt() {
		return (int) getMin();
	}
	
	/**
	 * 
	 * @return
	 */
	public int getMaxInt() {
		return (int) getMax();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Quantity [min=" + min + ", max=" + max + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(max);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(min);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Quantity other = (Quantity) obj;
		if (Double.doubleToLongBits(max) != Double.doubleToLongBits(other.max))
			return false;
		if (Double.doubleToLongBits(min) != Double.doubleToLongBits(other.min))
			return false;
		return true;
	}	
}
