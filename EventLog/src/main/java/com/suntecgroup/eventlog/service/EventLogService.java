
package com.suntecgroup.eventlog.service;

import java.util.List;

import com.suntecgroup.eventlog.beans.EventBean;



/**
 * @author madala.s
 *
 */

public interface EventLogService {

	public abstract String createEventLog(EventBean eventBean);
	public abstract String getFlowFileUUIDBySessionId(EventBean eventBean);
	public abstract String getClusterNodeIDBySessionId(EventBean eventBean);
	public abstract List<EventBean> getWholeSession(EventBean eventBean);

}
