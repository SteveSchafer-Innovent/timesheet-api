package com.stephenschafer.timesheet;

import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventProjectKey implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer eventId;
	private Integer projectId;

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof EventProjectKey)) {
			return false;
		}
		final EventProjectKey epk = (EventProjectKey) obj;
		return Objects.equals(eventId, epk.eventId) && Objects.equals(projectId, epk.projectId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(eventId, projectId);
	}
}