package com.stephenschafer.timesheet;

public class RawProject implements Comparable<RawProject> {
	private final int id;
	private final boolean isRoot;
	private final int parentId;
	private final String code;
	private final long minDuration;
	private final long round;
	private final int bigtimeProjectId;
	private final int bigtimeTaskId;
	private final String bigtimeDescription;

	public RawProject(final int id, final boolean isRoot, final int parentId, final String code,
			final long minDuration, final long round, final int bigtimeProjectId,
			final int bigtimeTaskId, final String bigtimeDescription) {
		this.id = id;
		this.isRoot = isRoot;
		this.parentId = parentId;
		this.code = code;
		this.minDuration = minDuration;
		this.round = round;
		this.bigtimeProjectId = bigtimeProjectId;
		this.bigtimeTaskId = bigtimeTaskId;
		this.bigtimeDescription = bigtimeDescription;
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

	public long getMinDuration() {
		return minDuration;
	}

	public long getRound() {
		return round;
	}

	public int getBigtimeProjectId() {
		return bigtimeProjectId;
	}

	public int getBigtimeTaskId() {
		return bigtimeTaskId;
	}

	public String getBigtimeDescription() {
		return bigtimeDescription;
	}
}
