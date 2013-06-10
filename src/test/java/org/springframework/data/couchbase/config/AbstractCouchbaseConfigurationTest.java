/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.data.couchbase.config;

import com.couchbase.client.CouchbaseClient;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.TestApplicationConfig;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit test for {@link AbstractCouchbaseConfiguration}
 *
 * @author Michael Nitschinger
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestApplicationConfig.class)
public class AbstractCouchbaseConfigurationTest {

  @Autowired
  private static CouchbaseClient client;

  @Test
  public void usesConfigClassPackageAsBaseMappingPackage() throws Exception {
    AbstractCouchbaseConfiguration config = new SampleCouchbaseConfiguration();

    assertEquals(config.getMappingBasePackage(),
      SampleCouchbaseConfiguration.class.getPackage().getName());
    assertEquals(config.getInitialEntitySet().size(), 1);
    assertTrue(config.getInitialEntitySet().contains(Entity.class));
  }

  @Test
  public void appliesSLF4JLoggingProperty() throws Exception {
    ApplicationContext ctx =
      new AnnotationConfigApplicationContext(SampleCouchbaseConfiguration.class);

    String name = ctx.getBean("slf4jName", String.class);
    assertNotNull(name);
    assertEquals("net.spy.memcached.compat.log.SLF4JLogger", name);
  }

  @Configuration
  static class SampleCouchbaseConfiguration extends AbstractCouchbaseConfiguration {

    private String slf4jName = null;

    @Bean
    @Override
    public CouchbaseClient couchbaseClient() throws Exception {
      slf4jName = System.getProperty("net.spy.log.LoggerImpl");
      return client;
    }

    @Bean
    public String slf4jName() throws Exception  {
      return slf4jName;
    }

  }

  @Document
  static class Entity {
  }
}
