
package com.suntecgroup.eventlog.beans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "nodeId",
    "address",
    "apiPort",
    "status",
    "heartbeat",
    "roles",
    "activeThreadCount",
    "queued",
    "events",
    "nodeStartTime"
})
public class Node {

    @JsonProperty("nodeId")
    private String nodeId;
    @JsonProperty("address")
    private String address;
    @JsonProperty("apiPort")
    private Integer apiPort;
    @JsonProperty("status")
    private String status;
    @JsonProperty("heartbeat")
    private String heartbeat;
    @JsonProperty("roles")
    private List<String> roles = null;
    @JsonProperty("activeThreadCount")
    private Integer activeThreadCount;
    @JsonProperty("queued")
    private String queued;
    @JsonProperty("events")
    private List<Event> events = null;
    @JsonProperty("nodeStartTime")
    private String nodeStartTime;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("nodeId")
    public String getNodeId() {
        return nodeId;
    }

    @JsonProperty("nodeId")
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    @JsonProperty("address")
    public String getAddress() {
        return address;
    }

    @JsonProperty("address")
    public void setAddress(String address) {
        this.address = address;
    }

    @JsonProperty("apiPort")
    public Integer getApiPort() {
        return apiPort;
    }

    @JsonProperty("apiPort")
    public void setApiPort(Integer apiPort) {
        this.apiPort = apiPort;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("heartbeat")
    public String getHeartbeat() {
        return heartbeat;
    }

    @JsonProperty("heartbeat")
    public void setHeartbeat(String heartbeat) {
        this.heartbeat = heartbeat;
    }

    @JsonProperty("roles")
    public List<String> getRoles() {
        return roles;
    }

    @JsonProperty("roles")
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @JsonProperty("activeThreadCount")
    public Integer getActiveThreadCount() {
        return activeThreadCount;
    }

    @JsonProperty("activeThreadCount")
    public void setActiveThreadCount(Integer activeThreadCount) {
        this.activeThreadCount = activeThreadCount;
    }

    @JsonProperty("queued")
    public String getQueued() {
        return queued;
    }

    @JsonProperty("queued")
    public void setQueued(String queued) {
        this.queued = queued;
    }

    @JsonProperty("events")
    public List<Event> getEvents() {
        return events;
    }

    @JsonProperty("events")
    public void setEvents(List<Event> events) {
        this.events = events;
    }

    @JsonProperty("nodeStartTime")
    public String getNodeStartTime() {
        return nodeStartTime;
    }

    @JsonProperty("nodeStartTime")
    public void setNodeStartTime(String nodeStartTime) {
        this.nodeStartTime = nodeStartTime;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
