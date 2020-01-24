package com.amir.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Prefrences {

	public static final String prefFileName = "prefs.properties";

    public static final String linuxFfmpegPathProp = "linuxFfmpegPath";
    public static final String windowsFfmpegPath = "windowsFfmpegPath";

  private Properties prop;
  private static Prefrences instance;


  private Prefrences() {
    load();
  }

  
  
  public static Prefrences getInstance() {
    if (instance == null)
    	instance = new Prefrences();
    return instance;
  }

  public String getProperty(String key, String defaultValue) {
    return prop.getProperty(key, defaultValue);
  }


  public void load() {
  
    prop = new Properties();
    InputStream input = null;
    try {
	  ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	  input = classLoader.getResourceAsStream(prefFileName);
      prop.load(input);
    } catch (IOException ex) {
      //Debug.debug(ex);
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (IOException e) {
          //Debug.debug(e);
        }
      }
    }

  }


}
