package ws.cogito.governance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ws.cogito.governance.model.AuditEvent;
import ws.cogito.governance.model.AuditEvents;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unit test the JSON Transreption
 * @author jeremydeane
 */
public class JSONTransreptionTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final String expectedJSON = "{\"application\":\"Claims\",\"time\":\"201110201650\",\"message\":\"Bodily Injury\"}";
	private final AuditEvent expectedPOJO = new AuditEvent ("Billing", "201210201650", "Late Payment");

	@Test
	public void toJSONTest() throws Exception {
		
		//Test AuditEvent Transreption
		AuditEvent auditEvent = new AuditEvent
				("Claims", "201110201650", "Bodily Injury");
		
		String actaulJSON = objectMapper.writeValueAsString(auditEvent);	
		
		assertEquals(expectedJSON, actaulJSON);
		
		//Test AuditEvets Transreption
		AuditEvent auditEvent2 = new AuditEvent
				("Claims", "201210201650", "Commercial Vehicle");			
		
		List<URL> auditEventLocations = new ArrayList<URL>();
		
		auditEventLocations.add (auditEvent.getAuditEventLocation("localhost",8080, "/spring-auditor"));
		auditEventLocations.add (auditEvent2.getAuditEventLocation("localhost",8080, "/spring-auditor"));
		
		AuditEvents auditEvents = new AuditEvents ("Claims", auditEventLocations);
		
		String auditEventsJSON = objectMapper.writeValueAsString(auditEvents);	
		
		assertTrue(auditEventsJSON.contains("{\"application\":\"Claims\",\"events\":"));
	}
	
	@Test
	public void toPOJOTest() throws Exception {
		
		//Test AuditEvent Transreption
		String auditEvent = "{\"message\":\"Late Payment\",\"time\":\"201210201650\",\"application\":\"Billing\"}";
		
		AuditEvent actualPOJO = (AuditEvent)objectMapper.readValue(auditEvent, AuditEvent.class);
		
		assertEquals(expectedPOJO, actualPOJO);
		
		//Test AuditEvents Transreption
		StringBuffer json = new StringBuffer();
		json.append("{\"application\":\"Claims\",\"events\":[");
		json.append("\"http://localhost:8080/spring-auditor/audit/event/Claims-201110201650\",");
		json.append("\"http://localhost:8080/spring-auditor/audit/event/Claims-201210201650\"");
		json.append("]}");
		
		AuditEvents auditEvents = (AuditEvents)objectMapper.readValue(json.toString(), AuditEvents.class);
		
		assertTrue(auditEvents.getEvents().size() == 2);
	}
}