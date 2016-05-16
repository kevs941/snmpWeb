/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snmpup;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.PDUFactory;
import org.snmp4j.util.TableUtils;

/**
 *
 * @author lelguea
 */
public class Util {
    
    public static List LeerTabla(String host, String community, String OID_Inicial) {
        
        Snmp snmp  = null;
        InetAddress hostAddress = null;
        TransportMapping transport;
        int maxRepetitions = 100;
              //  Create the SNMP Session
        try {
            transport = new DefaultUdpTransportMapping();
            snmp = new Snmp( transport );
        } catch ( IOException ioe ) {
            System.out.println( "Can not create transport  " +  ioe );
        }
        
        try {
            hostAddress = InetAddress.getByName( host );
            //System.err.println(hostAddress.getHostName());
        } catch ( UnknownHostException ue ) {
            System.out.println( "Can not get the host name, input: " +  host + ue );
        }
             // Setting up the target
        CommunityTarget target = new CommunityTarget();
        
        
        Address targetAddress = GenericAddress.parse(hostAddress.getHostAddress()+"/161");
        //System.out.println ("TargetAddress = " + targetAddress);
        //System.out.println ("Community = " + community);
        target.setAddress(targetAddress);
        target.setRetries(2);
        target.setTimeout(1000);
        target.setVersion(SnmpConstants.version2c);
        target.setCommunity(new OctetString(community));
        
        try {
            snmp.listen();
            //System.out.println("Connect to snmp");
        } catch ( IOException ioe ){
            System.out.println( "Can not connect to the host  " + ioe );
        }
              //      table options
        PDUFactory pF = new DefaultPDUFactory (PDU.GETNEXT);
        TableUtils tableUtils = new TableUtils(snmp, pF);
        tableUtils.setMaxNumRowsPerPDU(maxRepetitions);
        
        OID[] columns = new OID[1];
        
        columns[0] = new VariableBinding (new OID(OID_Inicial)).getOid() ;
        
        //System.out.println( "Vector Bulk SNMP oid= " + columns[0]);
        List snmpList =  tableUtils.getTable(target, columns,null,null); 
        
        //System.out.println("snmpList size : " + snmpList.size());
        
        try{
            snmp.close();
        } catch ( IOException ioe ) {
            System.out.println( "Can not close the snmp connection  " +  ioe );
        }
        
        return snmpList;

    }
    
}
