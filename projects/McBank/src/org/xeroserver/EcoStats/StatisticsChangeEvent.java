package org.xeroserver.EcoStats;

public class StatisticsChangeEvent {
	
	public enum Cause{
		FURNACE_FUEL,
		FURNACE_RESULT,
		CRAFT_USAGE,
		MOB_DROP,
		BLOCK_BREAK,
		ENTITY_DESTROY,
		FIRE_BURN,
	}
	
	public enum Currency {
		COAL, IRON, GOLD, DIAS
	}
	
	public void setCause(Cause cause) {
		this.cause = cause;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	private Cause cause;
	private Currency currency;
	private int amount;
	
	public StatisticsChangeEvent(Cause cause) {
		this.cause = cause;
		amount = 0;
	}
	
	public boolean valid() {
		return amount != 0;
	}
	
	public Cause getCause() {
		return cause;
	}
	
	public Currency getCurrency() {
		return currency;
	}
	
	public int getAmount() {
		return amount;
	}

}
