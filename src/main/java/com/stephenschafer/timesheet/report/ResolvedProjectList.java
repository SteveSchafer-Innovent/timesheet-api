package com.stephenschafer.timesheet.report;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResolvedProjectList
		implements Comparable<ResolvedProjectList>, Iterable<ResolvedProject> {
	private final List<ResolvedProject> list;

	public ResolvedProjectList(final List<ResolvedProject> list) {
		if (list == null) {
			throw new NullPointerException("list may not be null");
		}
		this.list = new ArrayList<>(list);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof ResolvedProjectList)) {
			return false;
		}
		final ResolvedProjectList that = (ResolvedProjectList) obj;
		return this.list.equals(that.list);
	}

	@Override
	public int hashCode() {
		return list.hashCode();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("[");
		String sep = "";
		for (final ResolvedProject proj : list) {
			sb.append(sep);
			sep = ", ";
			sb.append(proj);
		}
		sb.append("]");
		return sb.toString();
	}

	@Override
	public Iterator<ResolvedProject> iterator() {
		final List<ResolvedProject> list = new ArrayList<>(this.list);
		return list.iterator();
	}

	@Override
	public int compareTo(final ResolvedProjectList that) {
		final Iterator<ResolvedProject> iter1 = this.iterator();
		final Iterator<ResolvedProject> iter2 = that.iterator();
		while (iter1.hasNext()) {
			if (!iter2.hasNext()) {
				return 1;
			}
			final ResolvedProject proj1 = iter1.next();
			final ResolvedProject proj2 = iter2.next();
			final int result = proj1.compareTo(proj2);
			if (result != 0) {
				return result;
			}
		}
		if (iter2.hasNext()) {
			return -1;
		}
		return 0;
	}

	public int size() {
		return list.size();
	}
}
