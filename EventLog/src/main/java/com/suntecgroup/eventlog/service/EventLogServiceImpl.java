package com.suntecgroup.eventlog.service;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.suntecgroup.eventlog.beans.ClusterEntity;
import com.suntecgroup.eventlog.beans.EventBean;
import com.suntecgroup.eventlog.beans.Node;



/**
 * @author madala.s
 *
 */
@Service
public class EventLogServiceImpl  implements EventLogService{
	private static final Logger logger = LoggerFactory.getLogger(EventLogServiceImpl.class.getName());
	
	@Autowired
	private EventLogRespository eventDAO;

	@Autowired
	private Environment env;
	
	@Autowired
	@Qualifier("nifiBean")
	private RestTemplate restTemplate;
	
	private String url;
	private String isCluster;
	private ResponseEntity<ClusterEntity> clusterResponse = null;
	private ClusterEntity clusterEntity = null;
	
	/* (non-Javadoc)
	 * @see com.suntecgroup.EventLog.Service.EventLogService#createEventLog(com.suntecgroup.EventLog.Beans.EventBean, com.suntecgroup.EventLog.Repository.EventRepository)
	 */
	public String createEventLog(EventBean eventBean) {
		String hostname = eventBean.getClusterNodeId();
		eventBean.setClusterNodeId(resolveNodeId(hostname));
		return eventDAO.createEventLog(eventBean);
	}

	
	
	/* (non-Javadoc)
	 * @see com.suntecgroup.EventLog.Service.EventLogService#getBySessionId(com.suntecgroup.EventLog.Repository.EventRepository, com.suntecgroup.EventLog.Beans.EventBean)
	 */
	public String getFlowFileUUIDBySessionId(EventBean eventBean){
		return eventDAO.getFlowFileUUIDBySessionId(eventBean);
	}
	
	public String getClusterNodeIDBySessionId(EventBean eventBean){
		return eventDAO.getClusterNodeIDBySessionId(eventBean);
	}
	
	/* (non-Javadoc)
	 * @see com.suntecgroup.EventLog.Service.EventLogService#getWholeSession(com.suntecgroup.EventLog.Repository.EventRepository, com.suntecgroup.EventLog.Beans.EventBean)
	 */
	public List<EventBean> getWholeSession(EventBean eventBean){
		return eventDAO.getWholeSession(eventBean);	
	}
	
	private String resolveNodeId(String hostname) {
		String defRet = "";

		if(StringUtils.isEmpty(isCluster)) {
			isCluster = env.getProperty("deployment.iscluster");
		}
		if (!StringUtils.isEmpty(isCluster) && "true".equals(isCluster)) {

			try {
				if(clusterEntity == null) {
					url = env.getProperty("nifi.instance.url") + "" + env.getProperty("nifi.api.controller.cluster");
					clusterResponse = restTemplate.getForEntity(url, ClusterEntity.class);
					clusterEntity = clusterResponse.getBody();
				}
				if(clusterEntity != null) {
					Iterator<Node> it = clusterEntity.getCluster().getNodes().iterator();
					Node node = null;
					while (it.hasNext()) {
						node = it.next();
						if (node.getAddress().equalsIgnoreCase(hostname)) {
							return node.getNodeId();
						}
					}
				}
			} catch (Exception exception) {
				logger.error("Exception occurred:"+exception.getMessage(), exception);
			}
		}
		return defRet;
	}
}
