package net.mat0u5.lifeseries.utils.other;

public class TimeInf extends Time {
	public TimeInf() {
		super(0L);
	}

	@Override
	public long getNanos() {
		return -1;
	}

	@Override
	public long getMillis() {
		return -1;
	}

	@Override
	public int getTicks() {
		return -1;
	}

	@Override
	public int getSeconds() {
		return -1;
	}

	@Override
	public int getMinutes() {
		return -1;
	}

	@Override
	public int getHours() {
		return -1;
	}

	@Override
	public Time tick() {
		return this;
	}

	@Override
	public Time add(Time time) {
		return this;
	}

	@Override
	public Time add(long time) {
		return this;
	}

	@Override
	public Time multiply(long scale) {
		return this;
	}

	@Override
	public boolean isPresent() {
		return true;
	}

	@Override
	public boolean isMultipleOf(Time interval) {
		return true;
	}

	@Override
	public Time diff(Time time2) {
		return Time.infinite();
	}

	@Override
	public boolean isLargerThan(Time time2) {
		return true;
	}

	@Override
	public boolean isSmallerThan(Time time2) {
		return false;
	}

	@Override
	public Time copy() {
		return Time.infinite();
	}

	@Override
	public String formatReadable() {
		return "infinite";
	}

	@Override
	public String formatLong() {
		return "infinite";
	}

	@Override
	public String format() {
		return "infinite";
	}
}
