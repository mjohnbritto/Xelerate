package com.suntecgroup.traceablity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.suntecgroup.traceablity.Beans.Constants;
import com.suntecgroup.traceablity.Beans.EventBean;
import com.suntecgroup.traceablity.serviceImpl.TraceServiceImpl;

@CrossOrigin(origins = "*", maxAge = 3000)
@RestController
@RequestMapping("/traceability/api/")
public class TraceController {

	@Autowired
	private TraceServiceImpl traceServiceImpl;

	@RequestMapping(method = RequestMethod.POST, value = "/searchBUK", consumes = Constants.applicationJson)
	public ResponseEntity<String> searchBUK(@RequestBody EventBean eventBean) {
		return traceServiceImpl.searchBUK(eventBean, true);
	}

	@RequestMapping(value = "/getTraceData", method = RequestMethod.POST)
	public ResponseEntity<String> getTraceablity(@RequestBody EventBean eventBean) {
		return traceServiceImpl.getTraceability(eventBean);
	}

	@RequestMapping(value = "/getOperatorList", method = RequestMethod.POST)
	public ResponseEntity<String> getTraceablity1(@RequestBody EventBean eventBean) {
		return traceServiceImpl.getTraceability(eventBean);
	}
}
