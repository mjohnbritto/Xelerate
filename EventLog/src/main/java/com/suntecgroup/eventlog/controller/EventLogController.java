package com.suntecgroup.eventlog.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.Gson;
import com.suntecgroup.eventlog.beans.EventBean;
import com.suntecgroup.eventlog.beans.ResponseDetails;
import com.suntecgroup.eventlog.constansts.Constants;
import com.suntecgroup.eventlog.service.EventLogService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author madala.s
 *
 */
@CrossOrigin(origins = "*", maxAge = 3000)
@RestController
@RequestMapping("/eventlogger/api/event")
public class EventLogController {
	
	private static final Logger logger = LoggerFactory.getLogger(EventLogController.class);
	@Autowired
	private EventLogService eventService;
	
	@Autowired
	private Constants constant;
	
	

		
	/**
	 * @param eventBean
	 * @return
	 */
	@SuppressWarnings("static-access")
	@RequestMapping(value = "/createEvent", method = RequestMethod.POST)
	public ResponseEntity<ResponseDetails> createEvent(@RequestBody String eventBean) {
	   	ResponseDetails message = new ResponseDetails();
		String res = null;
		Gson gson = new Gson();
		JSONArray object;
		try {
			object = new JSONArray(eventBean);
			for (int i = 0; i < object.length(); i++){
					JSONObject event = (JSONObject) object.get(i);
					if( null != event && event.length()>0){
					String jsonUserIdentifier = event.toString();
					EventBean bean = gson.fromJson(jsonUserIdentifier,
							EventBean.class);
					res = eventService.createEventLog(bean);
				}
			}
		} catch (JSONException e) {
			logger.error("Error message:"+e);
		}
		if (null != res && !res.isEmpty()) {
			message.setSuccessData(constant.EVENT_LOG_SUCCESS);
			return new ResponseEntity<>(message,
					HttpStatus.OK);
		} else {
			message.setSuccessData(constant.EVENT_LOG_FAIL);
			return new ResponseEntity<>(message, HttpStatus.OK);
		}
	}

	
	/**
	 * @param eventBean
	 * @return
	 */
	@SuppressWarnings("static-access")
	@RequestMapping(value = "/getFlowFileUUID", method = RequestMethod.POST)
	public ResponseEntity<ResponseDetails> getFlowFileUUIDBySessionId(@RequestBody EventBean eventBean) {
		ResponseDetails message = new ResponseDetails();
		String resData = eventService.getFlowFileUUIDBySessionId(eventBean);
		if (null != resData) {
			message.setSuccessData(resData);
			message.setErrormessage(null);
		} else {
			message.setSuccessData(null);
			message.setErrormessage(constant.DATA_NOT_FOUND);
		}
		return new ResponseEntity<>(message, HttpStatus.OK);
	}

	/**
	 * @param eventBean
	 * @return
	 */
	@SuppressWarnings("static-access")
	@RequestMapping(value = "/getClusterNodeID", method = RequestMethod.POST)
	public ResponseEntity<ResponseDetails> getClusterNodeIDBySessionId(@RequestBody EventBean eventBean) {
		ResponseDetails message = new ResponseDetails();
		String resData = eventService.getClusterNodeIDBySessionId(eventBean);
		if (null != resData) {
			message.setSuccessData(resData);
			message.setErrormessage(null);
		} else {
			message.setSuccessData(null);
			message.setErrormessage(constant.DATA_NOT_FOUND);
		}
		return new ResponseEntity<>(message, HttpStatus.OK);
	}

	/**
	 * @param eventBean
	 * @return
	 */
	@SuppressWarnings("static-access")
	@RequestMapping(value = "/getEventDetails", method = RequestMethod.POST)
	public ResponseEntity<ResponseDetails> getEventDetails(@RequestBody EventBean eventBean) {
		List<EventBean> resData = eventService.getWholeSession(eventBean);
		ResponseDetails message = new ResponseDetails();
		if (null != resData && !resData.isEmpty()) {
			String res = resData.toString();
			message.setSuccessData(res);
			message.setErrormessage(null);
		} else {
			message.setSuccessData(null);
			message.setErrormessage(constant.DATA_NOT_FOUND);
		}
		return new ResponseEntity<>(message, HttpStatus.OK);
	}
	
	 /**
	 * @param eventBean
	 * @return
	 * @throws JSONException
	 */
	@RequestMapping(value = "/getEventBuks", method = RequestMethod.POST)
    public ResponseEntity<String> getEventBuks(@RequestBody EventBean eventBean) throws JSONException {
		  List<EventBean> resData = eventService.getWholeSession(eventBean);
          JSONArray buks=null;
          JSONObject filterData= new JSONObject();
          List<JSONObject> res = new ArrayList<>();
          Gson gson = new Gson();
          	for (int i = 0; i <resData.size(); i++) {
          			if (null != resData.get(i)) {
          					String json = gson.toJson(resData.get(i));
          					JSONObject eventData = new JSONObject(json);
          					buks = eventData.getJSONArray("buk");
          			}
          			if (null != buks) {
          				filterData = new JSONObject();
          				for(int j=0;j<buks.length();j++){
          					filterData.put(buks.getJSONObject(j).get("attributeName").toString(), buks.getJSONObject(j).get("attributeValue"));
          				}
          				res.add(filterData);
          			}
          	}
        return new ResponseEntity<>(res.toString(), HttpStatus.OK);
	}

}
