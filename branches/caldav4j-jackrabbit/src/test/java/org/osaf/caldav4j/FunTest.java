package org.osaf.caldav4j;

import java.util.Vector;
import javax.xml.namespace.QName;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.parameter.Value;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osaf.caldav4j.methods.HttpClient;
import org.osaf.caldav4j.methods.PropFindMethod;

public class FunTest extends BaseTestCase {
    public FunTest() {
		super();
	}

	private static final Log log = LogFactory
            .getLog(FunTest.class);


    @Before
    public void setUp() throws Exception {
        super.setUp();
        try {
        	mkcalendar(COLLECTION_PATH);
        } catch (Exception e) {
			// TODO: handle exception
        	e.printStackTrace();
        	log.warn("MKCAL not supported?");
		}
        put(ICS_DAILY_NY_5PM, COLLECTION_PATH + "/" + ICS_DAILY_NY_5PM);
        put(ICS_ALL_DAY_JAN1, COLLECTION_PATH + "/" + ICS_ALL_DAY_JAN1);
        put(ICS_NORMAL_PACIFIC_1PM, COLLECTION_PATH + "/"
                + ICS_NORMAL_PACIFIC_1PM);
        put(ICS_SINGLE_EVENT, COLLECTION_PATH + "/" + ICS_SINGLE_EVENT);
        put(ICS_FLOATING_JAN2_7PM, COLLECTION_PATH + "/"
                + ICS_FLOATING_JAN2_7PM);
    }

    @After
    public void tearDown() throws Exception {
        del(COLLECTION_PATH + "/" + ICS_DAILY_NY_5PM);
        del(COLLECTION_PATH + "/" + ICS_ALL_DAY_JAN1);
        del(COLLECTION_PATH + "/" + ICS_NORMAL_PACIFIC_1PM);
        del(COLLECTION_PATH + "/" + ICS_SINGLE_EVENT);
        del(COLLECTION_PATH + "/" + ICS_FLOATING_JAN2_7PM);
        del(COLLECTION_PATH);
    }

    @Test
    public void testFun() throws Exception{
        HttpClient http = createHttpClient();
        HostConfiguration hostConfig = createHostConfiguration();

        PropFindMethod propFindMethod = new PropFindMethod(caldavCredential.home);
        QName propName = new QName(CalDAVConstants.NS_DAV, "resourcetype");
        propFindMethod.setDepth(PropFindMethod.DEPTH_INFINITY);
        propFindMethod.setPath(caldavCredential.home);
        //TODO propFindMethod.setType(PropFindMethod.PROPFIND_BY_PROPERTY);
        Vector<QName> v = new Vector<QName>();
        v.add(propName);
        //TODO ?? propFindMethod.setPropertyNames(v.elements());
        http.executeMethod(hostConfig, propFindMethod);
     
        for (DavProperty<?> eProp :propFindMethod.getResponseTable()){
                String nodeName = eProp.getName().getName();
                String localName = eProp.getName().getNamespace().getPrefix();
                String tagName = eProp.getValue().toString();
                String namespaceURI = eProp.getName().getNamespace().getURI();
                log.info("nodename: " + nodeName);            
        }
    }

    
    public static void main (String args[]){
        try {
            Recur recur  = new Recur("FREQ=HOURLY");
            DateTime startDate = new DateTime("20060101T010000Z");
            DateTime endDate =   new DateTime("20060105T050000Z");
            DateTime baseDate =  new DateTime("20050101T033300");
            DateList dateList 
                = recur.getDates(baseDate, startDate, endDate, Value.DATE_TIME);
            for (int x = 0; x < dateList.size(); x++){
                DateTime d = (DateTime) dateList.get(x);
                System.out.println(d);
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    
    


}