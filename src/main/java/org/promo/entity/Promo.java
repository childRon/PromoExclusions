package org.promo.entity;


/**
 * Entity of promotion, that have following properties:
 *  <p><strong>name</strong> - name of the promotion</p>
 *  <p><strong>priority</strong> - priority of the promotion</p>
 * */
public class Promo implements Comparable<Promo> {
	private String name;
	private int priority;

	public Promo(String name, int priority) {
		this.name = name;
		this.priority = priority;
	}

	@Override
	public int compareTo(Promo promoToCompare) {
		return this.getPriority() != promoToCompare.getPriority() ?
				this.getPriority() > promoToCompare.getPriority() ? 1 : -1 :
				this.getName().compareTo(promoToCompare.getName());
	}

	public String getName() {
		return this.name;
	}

	public int getPriority() {
		return this.priority;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Promo)) {
			return false;
		}
		Promo promo = (Promo) o;
		if (this.priority != promo.priority) {
			return false;
		}
		if (!this.name.equals(promo.name)) {
			return false;
		}
		return true;
	}

	public int hashCode() {
		int result = this.name.hashCode();
		result = 31 * result + this.priority;
		return result;
	}

	public String toString() {
		return String.format("%s:%s", this.name, this.priority);
	}
}