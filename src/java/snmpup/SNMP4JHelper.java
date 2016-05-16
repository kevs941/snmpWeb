/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package snmpup;

/**
 *
 * @author lelguea
 */

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
public class SNMP4JHelper
{
public static final String READ_COMMUNITY = "public";
public static final String WRITE_COMMUNITY= "private";
public static final int mSNMPVersion =0; // 0 represents SNMP version=1
//public static final String OID_UPS_OUTLET_GROUP1 ="1.3.6.1.4.1.318.1.1.1.12.3.2.1.3.1";
//public static final String OID_UPS_BATTERY_CAPACITY="1.3.6.1.4.1.318.1.1.1.2.2.1.0";
public static final String OID="1.3.6.1.2.1.1.1.0";
public static final String SNMP_PORT="161";

public static void main(String[] args) {
    try {
        String strIPAddress = "172.25.3.50";

        String Datos =snmpGet(strIPAddress,READ_COMMUNITY,OID);
        System.out.println(Datos);
        Datos =snmpGetNext(strIPAddress,READ_COMMUNITY,OID);
        System.out.println(Datos);
        Datos =snmpGetNextOID(strIPAddress,READ_COMMUNITY,OID);
        System.out.println(Datos);
    } catch (Exception e) {
        System.out.println(e.toString());
    }
}
/*
* The following code valid only SNMP version1. This
* method is very useful to set a parameter on remote device.
*/
public void snmpSet(String strAddress, String community, String strOID, int Value)
{
strAddress= strAddress+"/"+SNMP_PORT;
Address targetAddress = GenericAddress.parse(strAddress);
Snmp snmp;
try
{
TransportMapping transport = new DefaultUdpTransportMapping();
snmp = new Snmp(transport);
transport.listen();
CommunityTarget target = new CommunityTarget();
target.setCommunity(new OctetString(community));
target.setAddress(targetAddress);
target.setRetries(2);
target.setTimeout(5000);
target.setVersion(SnmpConstants.version1);
PDU pdu = new PDU();
pdu.add(new VariableBinding(new OID(strOID), new Integer32(Value)));
pdu.setType(PDU.SET);
ResponseListener listener = new ResponseListener() {
public void onResponse(ResponseEvent event) {
// Always cancel async request when response has been received
// otherwise a memory leak is created! Not canceling a request
// immediately can be useful when sending a request to a broadcast
// address.
((Snmp)event.getSource()).cancel(event.getRequest(), this);
System.out.println("Set Status is:"+event.getResponse().getErrorStatusText());
}
};
snmp.send(pdu, target, null, listener);
snmp.close();
}
catch (Exception e)
{
e.printStackTrace();
}
}
/*
* The code is valid only SNMP version1. SnmpGet method
* return Response for given OID from the Device.
*/
public static String snmpGet(String strAddress, String community, String strOID) {

    String str="";

    try {
        OctetString community2 = new OctetString(community);
        strAddress= strAddress+"/"+SNMP_PORT;
        //System.out.println(strAddress);
        Address targetaddress = new UdpAddress(strAddress);
        TransportMapping transport = new DefaultUdpTransportMapping();
        transport.listen();
        CommunityTarget comtarget = new CommunityTarget();
        comtarget.setCommunity(community2);
        comtarget.setVersion(SnmpConstants.version2c);
        comtarget.setAddress(targetaddress);
        comtarget.setRetries(2);
        comtarget.setTimeout(1000);
        PDU pdu = new PDU();
        ResponseEvent response;
        Snmp snmp;
        pdu.add(new VariableBinding(new OID(strOID)));
        pdu.setType(PDU.GET);
        snmp = new Snmp(transport);
        response = snmp.get(pdu,comtarget);
        //System.out.println("----> "+response.getRequest().toString());
        //System.out.println("----> "+response.getResponse().toString());
        if(response != null) {
            if(response.getResponse().getErrorStatusText().equalsIgnoreCase("Success")) {
                PDU pduresponse=response.getResponse();
                str=pduresponse.getVariableBindings().firstElement().toString();
                if(str.contains("=")) {
                    int len = str.indexOf("=");
                    str=str.substring(len+1, str.length());
                }
            }
        } else {
            System.out.println("Feeling like a TimeOut occured ");
        }
        
         snmp.close();
    } catch(Exception e) {
        System.err.println(e.toString());
    }

    //System.out.println("Response="+str);
    return str.trim();
    }

/*
* The code is valid only SNMP version1. SnmpGet method
* return Response for given OID from the Device.
*/
public static String snmpGetNext(String strAddress, String community, String strOID) {

    String str="";

    try {
        OctetString community2 = new OctetString(community);
        strAddress= strAddress+"/"+SNMP_PORT;
        //System.out.println(strAddress);
        Address targetaddress = new UdpAddress(strAddress);
        TransportMapping transport = new DefaultUdpTransportMapping();
        transport.listen();
        CommunityTarget comtarget = new CommunityTarget();
        comtarget.setCommunity(community2);
        comtarget.setVersion(SnmpConstants.version2c);
        comtarget.setAddress(targetaddress);
        comtarget.setRetries(2);
        comtarget.setTimeout(1000);

        ResponseEvent response;
        Snmp snmp;
        //pdu.add(new VariableBinding(new OID(strOID)));
        //pdu.setType(PDU.GETNEXT);

        // Create the PDU object
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(strOID))); //Querying GetNext of sysDescr will get the sysObjectID OID value
        pdu.setRequestID(new Integer32(1));
        pdu.setType(PDU.GETNEXT);


        snmp = new Snmp(transport);
        response = snmp.getNext(pdu,comtarget);
        //System.out.println("----> "+response.getRequest().toString());
        //System.out.println("----> "+response.getResponse().toString());
        if(response != null) {
            if(response.getResponse().getErrorStatusText().equalsIgnoreCase("Success")) {
                PDU pduresponse=response.getResponse();
                str=pduresponse.getVariableBindings().firstElement().toString();
                if(str.contains("=")) {
                    int len = str.indexOf("=");
                    str=str.substring(len+1, str.length());
                }
            }
        } else {
            System.out.println("Feeling like a TimeOut occured ");
        }
        
         snmp.close();
    } catch(Exception e) {
        System.err.println(e.toString());
    }

    //System.out.println("Response="+str);
    return str.trim();
    }

public static String snmpGetNextOID(String strAddress, String community, String strOID) {

    String str="";

    try {
        OctetString community2 = new OctetString(community);
        strAddress= strAddress+"/"+SNMP_PORT;
        //System.out.println(strAddress);
        Address targetaddress = new UdpAddress(strAddress);
        TransportMapping transport = new DefaultUdpTransportMapping();
        transport.listen();
        CommunityTarget comtarget = new CommunityTarget();
        comtarget.setCommunity(community2);
        comtarget.setVersion(SnmpConstants.version2c);
        comtarget.setAddress(targetaddress);
        comtarget.setRetries(2);
        comtarget.setTimeout(1000);

        ResponseEvent response;
        Snmp snmp;
        //pdu.add(new VariableBinding(new OID(strOID)));
        //pdu.setType(PDU.GETNEXT);

        // Create the PDU object
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(strOID))); //Querying GetNext of sysDescr will get the sysObjectID OID value
        pdu.setRequestID(new Integer32(1));
        pdu.setType(PDU.GETNEXT);


        snmp = new Snmp(transport);
        response = snmp.getNext(pdu,comtarget);
        //System.out.println("----> "+response.getRequest().toString());
        //System.out.println("----> "+response.getResponse().toString());
        if(response != null) {
            if(response.getResponse().getErrorStatusText().equalsIgnoreCase("Success")) {
                PDU responsePDU=response.getResponse();
                if (responsePDU != null)  {
                    int errorStatus = responsePDU.getErrorStatus();
                    int errorIndex = responsePDU.getErrorIndex();
                    String errorStatusText = responsePDU.getErrorStatusText();

                    if (errorStatus == PDU.noError)  {
                        //System.out.println("Snmp GetNext Response for sysObjectID = " + responsePDU.getVariableBindings());
                        VariableBinding v= responsePDU.get(0);
                        str=v.getOid().toString();

                    }  else  {
                        System.out.println("Error: Request Failed");
                        System.out.println("Error Status = " + errorStatus);
                        System.out.println("Error Index = " + errorIndex);
                        System.out.println("Error Status Text = " + errorStatusText);
                    }
                }  else  {
                    System.out.println("Error: GetNextResponse PDU is null");
                }
            }
        } else {
            System.out.println("Feeling like a TimeOut occured ");
        }

         snmp.close();
    } catch(Exception e) {
        System.err.println(e.toString());
    }

    //System.out.println("Response="+str);
    return str.trim();
    }




}