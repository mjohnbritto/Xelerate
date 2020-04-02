package com.suntecgroup.eventlog.service;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.suntecgroup.eventlog.beans.EventBean;




/**
 * @author madala.s
 *
 */
@Repository
public interface EventLogRespository {
	public abstract String createEventLog(EventBean eventBean);
	public abstract String getFlowFileUUIDBySessionId(EventBean eventBean);
	public abstract String getClusterNodeIDBySessionId(EventBean eventBean);
	public abstract List<EventBean> getWholeSession(EventBean eventBean);
}
