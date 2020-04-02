/*package com.suntecgroup.nifi.test;

import static org.junit.Assert.assertNotNull;
import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.suntecgroup.nifi.app.BPApplication;
import com.suntecgroup.nifi.frontend.beans.BPFlowRequest;
import com.suntecgroup.nifi.frontend.beans.BPFlowResponse;
import com.suntecgroup.nifi.frontend.beans.ConnectionsRequest;
import com.suntecgroup.nifi.frontend.beans.Data;
import com.suntecgroup.nifi.frontend.beans.ErrorDetails;
import com.suntecgroup.nifi.frontend.beans.Operations;
import com.suntecgroup.nifi.service.BPServiceInterface;
import com.suntecgroup.nifi.service.impl.BPServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {BPApplication.class})
@EnableConfigurationProperties
public class BPServiceTest extends AbstractTestNGSpringContextTests{

	@Autowired
	private BPServiceInterface bpService;

	@BeforeMethod
	public void initMocks(){
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testOperators() throws Exception{
		BPFlowRequest bPFlowRequest = null; 
		bPFlowRequest = createRequest(bPFlowRequest);
		BPFlowResponse response = null;
		//response = bpService.retrieveNifiTemplate(bPFlowRequest);
		assertNotNull(response);
		assertEquals("100", response.getStatusCode());
		assertEquals("Data Inserted Successfully in DB", response.getStatusDescription());

	}

	@Test
	public void requestOperationsNull() throws Exception{
		BPFlowRequest bPFlowRequest = new BPFlowRequest(); 
		bPFlowRequest = createRequest(bPFlowRequest);
		List<Operations> operations = new ArrayList<Operations>();
		bPFlowRequest.setOperations(operations);
		BPFlowResponse response = null;
		//response = bpService.retrieveNifiTemplate(bPFlowRequest);
		assertNotNull(response);
		for (ErrorDetails errorDetails : response.getErrorDetails()) {
			assertEquals("103", errorDetails.getStatusCode());
			assertEquals("Operations/Connectios are empty", errorDetails.getDetails());
		}


	}

	public void testBPException() throws Exception{
		BPFlowRequest bPFlowRequest = null; 
		bPFlowRequest = createRequest(bPFlowRequest);
		BPServiceImpl bpService= new BPServiceImpl();
		BPFlowResponse response = null;
		//response = bpService.retrieveNifiTemplate(bPFlowRequest);
		assertNotNull(response);
		for (ErrorDetails errorDetails : response.getErrorDetails()) {
			assertEquals("Exception occured while creating nifi Template for processors::", errorDetails.getStatusCode());
		}
	}

	private BPFlowRequest createRequest(BPFlowRequest bPFlowRequest) {
		bPFlowRequest = new BPFlowRequest();
		List<Operations> operationsList = new ArrayList<Operations>();
		List<ConnectionsRequest> connectionList = new ArrayList<ConnectionsRequest>();
		Operations startOperations = new Operations();
		Data startData= new Data();
		startOperations.setLocation("1158.4422472184442 -226.19435688024453");
		startOperations.setKey(1);
		startOperations.setType("start");
		startOperations.setCustomizable(true);
		startOperations.setOperatorName("Start");
		startOperations.setPath("green");
		startOperations.setCategory("circle");
		startData.setBusinessServiceUrl("http://10.112.67.33:85/cheque-collection-api/chequeCollection/getBankList");
		startData.setBusinessServiceMethod("GET");

		Operations endOperations = new Operations();
		Data endData= new Data();
		endOperations.setLocation("1058.4422472184442 -116.19435688024453");
		endOperations.setKey(2);
		endOperations.setType("end");
		endOperations.setCustomizable(true);
		endOperations.setOperatorName("End");
		endOperations.setPath("green");
		endOperations.setCategory("circle");
		endData.setBusinessServiceUrl("http://10.112.67.33:85/cheque-collection-api/chequeCollection/getBankList");
		endData.setBusinessServiceMethod("GET");

		Operations invokeBsOperations = new Operations();
		Data invokeBsData= new Data();
		invokeBsOperations.setLocation("1058.4422472184442 -116.19435688024453");
		invokeBsOperations.setKey(3);
		invokeBsOperations.setType("invoke");
		invokeBsOperations.setCustomizable(true);
		invokeBsOperations.setOperatorName("Invoke BS");
		invokeBsOperations.setPath("green");
		invokeBsOperations.setCategory("RoundedRectangle");
		invokeBsData.setBusinessServiceUrl("http://10.112.67.33:85/cheque-collection-api/chequeCollection/getBankList");
		invokeBsData.setBusinessServiceMethod("GET");

		ConnectionsRequest connectionsRequest1 = new ConnectionsRequest();
		connectionsRequest1.setSourceName(1);
		connectionsRequest1.setDestinationName(2);

		ConnectionsRequest connectionsRequest2 = new ConnectionsRequest();
		connectionsRequest2.setSourceName(2);
		connectionsRequest2.setDestinationName(3);

		connectionList.add(connectionsRequest1);
		connectionList.add(connectionsRequest2);

		operationsList.add(startOperations);
		operationsList.add(endOperations);
		operationsList.add(invokeBsOperations);

		bPFlowRequest.setBpName("BS_DEMO");
		bPFlowRequest.setEnableBoundedExecution(false);
		bPFlowRequest.setId("");
		bPFlowRequest.setProfileable(true);

		bPFlowRequest.setModel("go.GraphLinksModel");
		bPFlowRequest.setOperations(operationsList);
		bPFlowRequest.setConnections(connectionList);

		return bPFlowRequest;

	}


}
*/