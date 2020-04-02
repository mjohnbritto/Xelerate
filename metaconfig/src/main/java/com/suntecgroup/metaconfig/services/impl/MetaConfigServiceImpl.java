package com.suntecgroup.metaconfig.services.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.suntecgroup.metaconfig.constant.MetaConstant;
import com.suntecgroup.metaconfig.model.Data;
import com.suntecgroup.metaconfig.model.ErrorDetail;
import com.suntecgroup.metaconfig.model.KeyRequest;
import com.suntecgroup.metaconfig.model.MetaConfig;
import com.suntecgroup.metaconfig.model.Property;
import com.suntecgroup.metaconfig.model.Response;

/**
 * MetaConfigServiceImpl implements the methods for access the property
 * that configured in the yaml file
 *
 */
@Service
public class MetaConfigServiceImpl implements MetaConfigService {

  private final Logger logger = LoggerFactory.getLogger(MetaConfigServiceImpl.class);

  @Autowired
  private Environment env;

  private final Yaml yaml = new Yaml();

  public Response<Data> getPropertyValues(final KeyRequest metafile) {

    String FILEPATH =  env.getProperty("metaconfig.path");    
    List<Property> properties = new ArrayList<Property>();
    Data data = new Data(); 
    MetaConfig metaconfig = null;
    try {
	    if(StringUtils.isNotBlank(FILEPATH)) {
		    Reader reader = null;
		    reader = new FileReader(FILEPATH + metafile.getAppname() + MetaConstant.YAML_FILE_EXT);    
		    metaconfig = yaml.loadAs(reader, MetaConfig.class);
	    } else {
	    	Resource resource = new ClassPathResource(metafile.getAppname() + MetaConstant.YAML_FILE_EXT);
	    	metaconfig = yaml.loadAs(resource.getInputStream(), MetaConfig.class);
	    }
    } catch (IOException fne) {     
	      logger.error("Exception occured when accessing property...", fne);
	      logger.error(fne.getMessage());
	      ErrorDetail error = new ErrorDetail();
	      error.setMessage("Could not find APP name!");  
	      data.setError(error);
	      return new Response<Data>(MetaConstant.FAILURE_MSG_CODE, MetaConstant.FAILURE_MSG, data);
	}

    Property property[] = metaconfig.getProperty();

    if (metafile.isAllKeys()) {
      properties = Arrays.asList(property);
    } else {
      List<String> keys = metafile.getKeys();
      for (String key : keys) {
        for (Property prop : property) {
          if (key.equals(prop.getKey())) {
            properties.add(prop);
          }
        }
      }
    }

    if(properties.size() == 0 ){
      ErrorDetail error = new ErrorDetail();
      error.setMessage("Could not find key!");  
      data.setError(error);
      return new Response<Data>(MetaConstant.FAILURE_MSG_CODE, MetaConstant.FAILURE_MSG, data);
    }    

    data.setProperties(properties);
    return new Response<Data>(MetaConstant.SUCCESS_MSG_CODE, MetaConstant.SUCCESS_MSG, data);
  }

  public Response<String> createProperty(final String appname, final MetaConfig metaconfig) {

    String FILEPATH =  env.getProperty("metaconfig.path");
    String fileNmae = FILEPATH + appname + MetaConstant.YAML_FILE_EXT;
    Writer writer = null;
    File file = null;     
    final String startFile = "---\nproperty:\n- key: \"donotuse\"\n\n";
    boolean isNewApp = false;
    Path path = Paths.get(fileNmae);    
    file = new File(fileNmae);

    try {

      if(!Files.exists(path)){           
        isNewApp = true;
      }

      writer = new FileWriter(file, true);

      if(isNewApp){
        writer.write(startFile); 
      }

      /*DumperOptions options = new DumperOptions();
      options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
      options.setPrettyFlow(true);*/

      Representer representer = new Representer();
      representer.addClassTag(Property.class, Tag.MAP);

      final YAMLFactory yamlFactory = new YAMLFactory().configure(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID, false)
          .configure(YAMLGenerator.Feature.USE_NATIVE_OBJECT_ID, false)
          .configure(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID, false)
          .configure(YAMLGenerator.Feature.WRITE_DOC_START_MARKER, false);          

      ObjectMapper mapper = new ObjectMapper(yamlFactory);

      String yaml = mapper.writeValueAsString(metaconfig);     
      yaml = yaml.replaceAll("property:", "");
      yaml = "#--------------------------------------------#".concat(yaml);      
      writer.write(yaml);
    } catch (IOException e) {      
      logger.error("Exception occured when creating/updating property...", e);
      logger.error(e.getMessage());     
      return new Response<String>(MetaConstant.FAILURE_MSG_CODE, MetaConstant.FAILURE_MSG, "Failed to create/update property!");
    }finally{
      try {
        writer.close();
      } catch (IOException e) {
    	  logger.error("Exception occurred:"+ e.getMessage(), e);
      }
    }
    return new Response<String>(MetaConstant.SUCCESS_MSG_CODE, MetaConstant.SUCCESS_MSG, "Property Created");
  }

  public boolean commentProperty(final String appname, final Property property) {

    String FILEPATH =  env.getProperty("metaconfig.path");
    String fileName = FILEPATH + appname + MetaConstant.YAML_FILE_EXT;
    String key = property.getKey();        
    String search = "- key: \"" + key + "\"";
    String replace = "#- key: \"" + key + "\"";    
    Path path = Paths.get(fileName);
    Charset charset = StandardCharsets.UTF_8;
    try {
      String content = new String(Files.readAllBytes(path), charset);
      content = content.replaceAll(search, replace);          
      Files.write(path, content.getBytes(charset));          
    } catch (IOException e) {
      logger.error("Exception occured when commenting property...",e);
      logger.error(e.getMessage());          
      return false;
    }    
    return true;
  }

}