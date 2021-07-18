package com.stephenschafer.timesheet.report;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ReportDetailRow implements Comparable<ReportDetailRow> {
	private final List<ReportProject> projects;
	private final Date time;
	private final long duration;

	@Override
	public int compareTo(final ReportDetailRow that) {
		final Iterator<ReportProject> thisIter = this.projects.iterator();
		final Iterator<ReportProject> thatIter = that.projects.iterator();
		while (thisIter.hasNext()) {
			if (!thatIter.hasNext()) {
				return 1;
			}
			final String thisCode = thisIter.next().getCode();
			final String thatCode = thatIter.next().getCode();
			final int diff = thisCode.compareTo(thatCode);
			if (diff != 0) {
				return diff;
			}
		}
		if (thatIter.hasNext()) {
			return -1;
		}
		return 0;
	}
}
