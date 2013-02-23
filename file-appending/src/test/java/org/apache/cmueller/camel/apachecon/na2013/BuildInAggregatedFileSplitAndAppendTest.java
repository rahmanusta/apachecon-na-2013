package org.apache.cmueller.camel.apachecon.na2013;

import java.util.concurrent.TimeUnit;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.StopWatch;
import org.junit.Test;

public class BuildInAggregatedFileSplitAndAppendTest extends CamelTestSupport {
	
    @Test
    public void measureBuildInAggregatedFileSplitAndAppend() throws Exception {
        getMockEndpoint("mock:end").setExpectedMessageCount(1);
        
        StopWatch watch = new StopWatch();
        
        context.startRoute("splitter");
        assertMockEndpointsSatisfied(1, TimeUnit.MINUTES);
        
        watch.stop();
        
        System.out.println("measureBuildInAggregatedFileSplitAndAppend duration: " + watch.taken() + "ms");
    }

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {
			public void configure() throws Exception {
				from("file://src/test/data?charset=UTF-8&noop=true&initialDelay=0").routeId("splitter").autoStartup(false)
				   .split(body().tokenize("\n"), new StringBuilderAggregatingStrategy()).streaming()
				   		.process(new Processor() {
							public void process(Exchange exchange) throws Exception {
								// do some processing here
							}
						})
				   .end()
				   .convertBodyTo(String.class)
				   .to("file://target?charset=UTF-8&fileExist=Append")
				   .to("mock:end");
			}
		};
	}
}