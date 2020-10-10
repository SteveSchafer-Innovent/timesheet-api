package com.stephenschafer.timesheet.report;

public class RawProject implements Comparable<RawProject> {
	private final int id;
	private final boolean isRoot;
	private final int parentId;
	private final String code;

	public RawProject(final int id, final boolean isRoot, final int parentId, final String code) {
		this.id = id;
		this.isRoot = isRoot;
		this.parentId = parentId;
		this.code = code;
	}

	public int getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public boolean isRoot() {
		return isRoot;
	}

	public int getParentId() {
		return parentId;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof RawProject)) {
			return false;
		}
		final RawProject that = (RawProject) obj;
		return this.id == that.id;
	}

	@Override
	public int hashCode() {
		return Integer.valueOf(this.id).hashCode();
	}

	@Override
	public String toString() {
		return String.format("Project %s (%d)", code, Integer.valueOf(id));
	}

	@Override
	public int compareTo(final RawProject that) {
		if (this.code != null) {
			if (that.code != null) {
				return this.code.compareTo(that.code);
			}
			return 1;
		}
		if (that.code != null) {
			return -1;
		}
		return 0;
	}
}
