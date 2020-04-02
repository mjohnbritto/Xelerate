package com.suntecgroup.metaconfig.controller.runtime;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.suntecgroup.metaconfig.constant.MetaConstant;
import com.suntecgroup.metaconfig.model.Data;
import com.suntecgroup.metaconfig.model.KeyRequest;
import com.suntecgroup.metaconfig.model.MetaConfig;
import com.suntecgroup.metaconfig.model.Property;
import com.suntecgroup.metaconfig.model.Response;
import com.suntecgroup.metaconfig.services.impl.MetaConfigService;

@RestController
@CrossOrigin(origins = "*", maxAge = 3000)
@RequestMapping("/metaconfig/property")
public class MetaConfiController {

  private final Logger logger = LoggerFactory.getLogger(MetaConfiController.class);

  @Autowired
  private MetaConfigService metaconfigservice;

  /**
   * Access the property with single key
   * @param appname
   * @param key
   * @return
   */  
  @RequestMapping(value = "/key", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Response<Data> getProperty(@RequestParam(value = "appname") final String appname,
      @RequestParam(value = "key") final String key) {

    logger.info("Access single property...");
    KeyRequest metafile = new KeyRequest();
    List<String> keys = new ArrayList<String>();
    keys.add(key);
    metafile.setAppname(appname);
    metafile.setKeys(keys);
    metafile.setAllKeys(false);
    return metaconfigservice.getPropertyValues(metafile);
  }

  /**
   * Access the property with multiple keys that will passed as array
   * @param appname
   * @param keys
   * @return
   */  
  @RequestMapping(value = "/multiple/keys/{appname}", method = RequestMethod.POST, 
      consumes = "application/json", produces = "application/json")
  @ResponseBody
  public Response<Data> getListProperty(@PathVariable(value = "appname") final String appname,
      @RequestBody List<String> keys) {

    logger.info("Access multiple property...");
    KeyRequest metafile = new KeyRequest();
    metafile.setAppname(appname);
    metafile.setKeys(keys);
    metafile.setAllKeys(false);
    return metaconfigservice.getPropertyValues(metafile);
  }
  
  /**
   * Access all the property that configured in the yaml file 
   * @param appname
   * @return
   */

  @RequestMapping(value = "/all", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Response<Data> getAllProperty(@RequestParam(value = "appname") final String appname) {
    logger.info("Access All properties...");
    KeyRequest metafile = new KeyRequest();
    metafile.setAppname(appname);
    metafile.setAllKeys(true);
    return metaconfigservice.getPropertyValues(metafile);
  }
  
  /**
   * create a new property in the yaml file
   * @param appname
   * @param property
   * @return
   */
  @RequestMapping(value = "/create/{appname}", method = RequestMethod.POST, consumes = "application/json")
  @ResponseBody
  public Response<String> createProperty(@PathVariable(value = "appname") final String appname,
      @RequestBody final Property property) {

    logger.info("Create property...");

    if(!validation(appname, property)){
      return new Response<String>( MetaConstant.FAILURE_MSG_CODE,
          MetaConstant.FAILURE_MSG, 
          "App name or Key should not be empty!");
    }


    MetaConfig metaconfig = new MetaConfig();
    Property[] aaryPproperty = { property };
    metaconfig.setProperty(aaryPproperty);
    return metaconfigservice.createProperty(appname, metaconfig);
  }

  /**
   * update the existing property. If exists, comment the key and create a new 
   * key with updated values  
   * @param appname
   * @param property
   * @return
   */
  @RequestMapping(value = "/update/{appname}", method = RequestMethod.PUT, consumes = "application/json")
  @ResponseBody
  public Response<String> updateProperty(@PathVariable(value = "appname") final String appname,
      @RequestBody Property property) {
    
    logger.info("Update property...");
    
    if(!validation(appname, property)){
		logger.error("Validation failed: App name or Key should not be empty!");
		return new Response<String>( MetaConstant.FAILURE_MSG_CODE,
			MetaConstant.FAILURE_MSG, 
			"App name or Key should not be empty!");
    }
    
    boolean isCommented = metaconfigservice.commentProperty(appname, property);    
    if(!isCommented){
      return new Response<String>( MetaConstant.FAILURE_MSG_CODE,
          MetaConstant.FAILURE_MSG, 
          "Could not able to update Key!");
    }

    MetaConfig metaconfig = new MetaConfig();
    Property[] aaryPproperty = { property };
    metaconfig.setProperty(aaryPproperty);
    return metaconfigservice.createProperty(appname, metaconfig);
  } 

  private boolean validation(final String appname, final Property property){
     
    if(appname.equalsIgnoreCase("null")  ){      
      return false;
    }
    
    if(!(null!= appname && null != property )){      
      return false;
    }

    if(property.getKey().isEmpty()){
      return false;
    }
    
    return true;
  }  
}
