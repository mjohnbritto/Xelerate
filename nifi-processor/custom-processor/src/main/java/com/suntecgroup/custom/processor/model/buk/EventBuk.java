/* 
 * Copyright (C) Suntec Group Business Solutions - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential - 2018
 */
package com.suntecgroup.custom.processor.model.buk;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/*
 * Model class for holding BUK info
 * 
 * @version 1.0 - December 2018
 * @author John Britto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "buk"
})
public class EventBuk {

    @JsonProperty("buk")
    private List<Buk> buk = null;

    @JsonProperty("buk")
    public List<Buk> getBuk() {
        return this.buk;
    }

    @JsonProperty("buk")
    public void setBuk(List<Buk> buk) {
        this.buk = buk;
    }
    
    public void addBuk(Buk buk)
    {
    	if(this.buk == null)
    	{
    		this.buk = new ArrayList<Buk>();
    	}
    	this.buk.add(buk);
    }

}
