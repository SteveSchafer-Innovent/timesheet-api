package com.stephenschafer.timesheet.report;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResolvedProject implements Comparable<ResolvedProject> {
	private final int id;
	private final ResolvedProject parent;
	private final String code;
	private final Map<Date, AtomicLong> dateTotals = new HashMap<>();
	private final AtomicInteger count = new AtomicInteger(0);

	public ResolvedProject(final int id, final ResolvedProject parent, final String code) {
		this.id = id;
		this.parent = parent;
		if (code == null) {
			throw new NullPointerException("code may not be null");
		}
		this.code = code;
	}

	private ResolvedProject(final ResolvedProject original) {
		this.id = original.id;
		this.parent = original.parent == null ? null : new ResolvedProject(original.parent);
		this.code = original.code;
		for (final Date key : original.dateTotals.keySet()) {
			final AtomicLong value = original.dateTotals.get(key);
			this.dateTotals.put(key, new AtomicLong(value.get()));
		}
		this.count.set(original.count.get());
	}

	@Override
	public ResolvedProject clone() {
		return new ResolvedProject(this);
	}

	public boolean isRoot() {
		return parent == null;
	}

	public int getId() {
		return id;
	}

	public ResolvedProject getParent() {
		return parent;
	}

	public String getCode() {
		return code;
	}

	@Override
	public int compareTo(final ResolvedProject that) {
		final List<ResolvedProject> thisPath = this.getPath();
		final List<ResolvedProject> thatPath = that.getPath();
		final Iterator<ResolvedProject> thisIter = thisPath.iterator();
		final Iterator<ResolvedProject> thatIter = thatPath.iterator();
		while (thisIter.hasNext()) {
			if (!thatIter.hasNext()) {
				return 1;
			}
			final ResolvedProject thisProj = thisIter.next();
			final ResolvedProject thatProj = thatIter.next();
			final int result = thisProj.code.toLowerCase().compareTo(thatProj.code.toLowerCase());
			if (result != 0) {
				return result;
			}
		}
		if (thatIter.hasNext()) {
			return -1;
		}
		return 0;
	}

	public String getQualifiedCode() {
		if (parent != null) {
			return parent.getQualifiedCode() + "." + code;
		}
		return code;
	}

	public int getLevel() {
		if (parent != null) {
			return parent.getLevel() + 1;
		}
		return 1;
	}

	public void addDuration(final Date date, final long duration) {
		addDuration(dateTotals, date, duration);
		count.incrementAndGet();
	}

	private static void addDuration(final Map<Date, AtomicLong> map, final Date date,
			final long duration) {
		if (date == null) {
			throw new NullPointerException("date may not be null");
		}
		final DecimalFormat df = new DecimalFormat("#0.00");
		log.info("adding " + df.format(duration / 1000.0 / 60.0 / 60.0) + " to " + date);
		AtomicLong accumulator = map.get(date);
		if (accumulator == null) {
			accumulator = new AtomicLong(0);
			map.put(date, accumulator);
		}
		accumulator.addAndGet(duration);
	}

	public int getCount() {
		return count.intValue();
	}

	public boolean inherits(final ResolvedProject project) {
		if (this.equals(project)) {
			return true;
		}
		if (parent == null) {
			return false;
		}
		return parent.inherits(project);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof ResolvedProject)) {
			return false;
		}
		final ResolvedProject that = (ResolvedProject) obj;
		return this.id == that.id;
	}

	@Override
	public int hashCode() {
		return Integer.valueOf(id).hashCode();
	}

	@Override
	public String toString() {
		return getQualifiedCode();
	}

	public List<ResolvedProject> getPath() {
		final LinkedList<ResolvedProject> list = new LinkedList<>();
		populatePath(list);
		return list;
	}

	private void populatePath(final LinkedList<ResolvedProject> list) {
		list.addFirst(this);
		if (parent != null) {
			parent.populatePath(list);
		}
	}

	public void copyAccumulators(final ResolvedProject that) {
		clearAccumulators();
		final List<ResolvedProject> thisPath = this.getPath();
		final List<ResolvedProject> thatPath = that.getPath();
		final Iterator<ResolvedProject> thisIter = thisPath.iterator();
		final Iterator<ResolvedProject> thatIter = thatPath.iterator();
		while (thisIter.hasNext()) {
			if (!thatIter.hasNext()) {
				break;
			}
			final ResolvedProject thisProj = thisIter.next();
			final ResolvedProject thatProj = thatIter.next();
			if (thisProj.getId() != thatProj.getId()) {
				break;
			}
			thisProj.dateTotals.clear();
			thisProj.dateTotals.putAll(thatProj.dateTotals);
			thisProj.count.set(thatProj.count.get());
		}
	}

	public void clearAccumulators() {
		this.dateTotals.clear();
		this.count.set(0);
		if (parent != null) {
			parent.clearAccumulators();
		}
	}

	public boolean matches(final ResolvedProject that) {
		final List<ResolvedProject> thisPath = this.getPath();
		final List<ResolvedProject> thatPath = that.getPath();
		final Iterator<ResolvedProject> thisIter = thisPath.iterator();
		final Iterator<ResolvedProject> thatIter = thatPath.iterator();
		while (thatIter.hasNext()) {
			final ResolvedProject thatProj = thatIter.next();
			if (!thisIter.hasNext()) {
				return false;
			}
			final ResolvedProject thisProj = thisIter.next();
			if (thisProj.getId() != thatProj.getId()) {
				return false;
			}
		}
		return true;
	}

	public Map<Date, Long> getDateTotals() {
		final Map<Date, Long> map = new HashMap<>();
		for (final Date date : this.dateTotals.keySet()) {
			final AtomicLong value = this.dateTotals.get(date);
			map.put(date, Long.valueOf(value.get()));
		}
		return map;
	}
}
