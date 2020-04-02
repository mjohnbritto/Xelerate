package com.suntecgroup.traceablity.service;

import org.springframework.http.ResponseEntity;

import com.suntecgroup.traceablity.Beans.EventBean;

public interface TraceService {

	public ResponseEntity<String> searchBUK(EventBean eventBean, boolean tryOnce);

	public ResponseEntity<String> getTraceability(EventBean eventBean);

}
