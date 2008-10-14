/**
 * @author rpolli
 */
package org.osaf.caldav4j.methods;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osaf.caldav4j.BaseTestCase;
import org.osaf.caldav4j.util.CaldavStatus;
import org.osaf.caldav4j.util.ICalendarUtils;

public class PutGetTestGoogle extends BaseTestCase {
    private static final Log log = LogFactory.getLog(PutGetTestGoogle.class);
    private CalDAV4JMethodFactory methodFactory = new CalDAV4JMethodFactory();
     
    public  String COLLECTION_PATH = CALDAV_SERVER_WEBDAV_ROOT
            + "/" + COLLECTION;
    
    protected void setUp() throws Exception {
        super.setUp();
        // mkdir(COLLECTION_PATH);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        del(COLLECTION_PATH + "/" + BaseTestCase.ICS_GOOGLE_DAILY_NY_5PM);
        //del(COLLECTION_PATH);
    }

    public void testAddRemoveCalendarResource() throws Exception{
        HttpClient http = createHttpClient();
        HostConfiguration hostConfig = createHostConfiguration();

        Calendar cal = getCalendarResource(BaseTestCase.ICS_GOOGLE_DAILY_NY_5PM_PATH);
        if (cal == null) {
        	throw new Exception("can't find in CLASSPATH:" 
        			+ BaseTestCase.ICS_GOOGLE_DAILY_NY_5PM_PATH);
        }
        PutMethod put = methodFactory.createPutMethod();
        put.setIfNoneMatch(true);
        put.setAllEtags(true);
        put.setRequestBody(cal);
        COLLECTION_PATH = COLLECTION_PATH.replaceAll("/+", "/");
        
        System.out.println("putting " + COLLECTION_PATH + "/" + BaseTestCase.ICS_GOOGLE_DAILY_NY_5PM);
        put.setPath(COLLECTION_PATH + "/" + BaseTestCase.ICS_GOOGLE_DAILY_NY_5PM);
        http.executeMethod(hostConfig, put);
        int statusCode = put.getStatusCode();
        // google uses SC_NO_CONTENT instead of SC_CREATED
        assertEquals("Status code for put:", CaldavStatus.SC_NO_CONTENT, statusCode);


        //ok, so we created it...let's make sure it's there!
        GetMethod get = methodFactory.createGetMethod();
        get.setPath(COLLECTION_PATH + "/" + BaseTestCase.ICS_GOOGLE_DAILY_NY_5PM);
        http.executeMethod(hostConfig, get);
        statusCode = get.getStatusCode();
        assertEquals("Status code for get: ", CaldavStatus.SC_OK, statusCode);
        
        //now let's make sure we can get the resource body as a calendar
        Calendar calendar = get.getResponseBodyAsCalendar();
        VEvent event = ICalendarUtils.getFirstEvent(calendar);
        String uid = ICalendarUtils.getUIDValue(event);
        assertEquals(ICS_GOOGLE_DAILY_NY_5PM_UID, uid);
        
        //let's make sure that a subsequent put with "if-none-match: *" fails
        put = methodFactory.createPutMethod();
        put.setIfNoneMatch(true);
        put.setAllEtags(true);
        put.setRequestBody(cal);
        put.setPath(COLLECTION_PATH + "/" + BaseTestCase.ICS_GOOGLE_DAILY_NY_5PM);
        http.executeMethod(hostConfig, put);
        statusCode = put.getStatusCode();

      //was CaldavStatus.SC_PRECONDITION_FAILED but gcalendar doesn't support if-tag and preconditions
        assertEquals("Status code for put:",
                CaldavStatus.SC_CONFLICT, statusCode);  
   }
    

}