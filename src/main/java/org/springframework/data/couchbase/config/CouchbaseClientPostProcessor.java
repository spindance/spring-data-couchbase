package org.springframework.data.couchbase.config;

import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;

import java.util.Properties;


public class CouchbaseClientPostProcessor extends InstantiationAwareBeanPostProcessorAdapter {

  @Override
  public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName)  {
    Properties systemProperties = System.getProperties();
    systemProperties.put("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.SLF4JLogger");
    System.setProperties(systemProperties);

    return null;
  }

}
