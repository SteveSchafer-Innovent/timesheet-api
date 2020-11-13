package com.stephenschafer.timesheet.report;

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
public class ReportSummaryRow implements Comparable<ReportSummaryRow> {
	private final List<ReportSummaryProject> projects;
	private final List<Long> durations;

	public long getTotalDuration() {
		long total = 0;
		for (final Long duration : durations) {
			total += duration.longValue();
		}
		return total;
	}

	@Override
	public int compareTo(final ReportSummaryRow that) {
		final Iterator<ReportSummaryProject> thisIter = this.projects.iterator();
		final Iterator<ReportSummaryProject> thatIter = that.projects.iterator();
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