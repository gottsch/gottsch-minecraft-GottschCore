package com.someguyssoftware.gottschcore.bst;

public interface IInterval<D> extends Comparable<IInterval<D>> {

	int getStart();
	int getEnd();

	Integer getMin();
	void setMin(Integer min);

	Integer getMax();
	void setMax(Integer max);

	IInterval<D> getLeft();
	void setLeft(IInterval<D> left);

	IInterval<D> getRight();
	void setRight(IInterval<D> right);

	D getData();
	void setData(D data);

	abstract int compareTo(IInterval<D> o);
}
