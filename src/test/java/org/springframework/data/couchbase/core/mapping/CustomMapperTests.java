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

package org.springframework.data.couchbase.core.mapping;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.TestApplicationConfig;
import org.springframework.data.couchbase.core.convert.CustomConversions;
import org.springframework.data.couchbase.core.convert.MappingCouchbaseConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;

/**
 * Tests to verify custom mapping logic.
 *
 * @author Michael Nitschinger
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestApplicationConfig.class)
public class CustomMapperTests {

  @Autowired
  private MappingCouchbaseConverter converter;

  @Test
  public void shouldWriteWithCustomConverter() {
    List<Object> converters = new ArrayList<Object>();
    converters.add(DateToStringConverter.INSTANCE);
    converter.setCustomConversions(new CustomConversions(converters));

    Date date = new Date();
    BlogPost post = new BlogPost();
    post.created = date;

    CouchbaseDocument doc = new CouchbaseDocument();
    converter.write(post, doc);

    assertEquals(date.toString(), doc.getPayload().get("created"));
  }

  @Test
  public void shouldReadWithCustomConverter() {
    List<Object> converters = new ArrayList<Object>();
    converters.add(IntegerToStringConverter.INSTANCE);
    converter.setCustomConversions(new CustomConversions(converters));

    CouchbaseDocument doc = new CouchbaseDocument();
    doc.getPayload().put("content", 10);
    Counter loaded = converter.read(Counter.class, doc);
    System.out.println(loaded.content);
  }

  public class BlogPost {
    @Id
    public String id = "key";

    @Field
    public Date created;
  }

  public class Counter {
    @Field
    public String content;
  }

  public static enum IntegerToStringConverter implements Converter<Integer, String> {
    INSTANCE;

    @Override
    public String convert(Integer source) {
      return source % 2 == 0 ? "even" : "odd";
    }
  }

  public static enum DateToStringConverter implements Converter<Date, String> {
    INSTANCE;

    public static Format FORMATTER = new SimpleDateFormat("yyyy HH");

    @Override
    public String convert(Date source) {
      return source.toString();
    }
  }

}
