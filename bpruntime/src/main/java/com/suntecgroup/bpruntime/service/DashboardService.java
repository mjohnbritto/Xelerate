package com.suntecgroup.bpruntime.service;

import java.util.List;

import com.suntecgroup.bpruntime.bean.adminconsole.ReplaySessionRequest;
import com.suntecgroup.bpruntime.bean.adminconsole.ReplaySessionResponse;

public interface DashboardService {

	public String getDashboardSessionDetails(String sessionId, String runNumber);

	public ReplaySessionResponse<List> replayTechnicalError(ReplaySessionRequest resumeSessionRequest);

	public void replayFailedEvents();
}
