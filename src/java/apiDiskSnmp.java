/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import snmpup.SNMP4JHelper;
import utilities.classDisk;

/**
 *
 * @author Nataniel
 */
@WebServlet(urlPatterns = {"/apiDiskSnmp"})
public class apiDiskSnmp extends HttpServlet {
    static public ArrayList<classDisk> arregloDiscos = new ArrayList<classDisk>();
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("image/jpeg");
        OutputStream salida = response.getOutputStream();
        int alto = 500; 
        int ancho = 800; 
        
        //Hacer un método que devuelva un el chart. 
        String disco = request.getParameter("disco");
            if((disco==null)||("".equals(disco))){
                disco = "1"; 
            }
            //Después de esta consulta, se quedará guardada la información en el arreglo "arregloDiscos"
            consulta("192.168.1.71"); 
            
         int discoInt = Integer.parseInt(disco); 
         JFreeChart chart = ((classDisk) arregloDiscos.get(discoInt)).createChart(((classDisk) arregloDiscos.get(discoInt)).createDataset()); 
        
         ChartUtilities.writeChartAsJPEG(salida,chart,ancho,alto);
        
        salida.close();
        }
    

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
    
    //Comienzan los métodos para manejo de SNMP 
     public void consulta(String ipAddress){
            System.out.println(ipAddress);
            String Comunidad="clases";
            String OIDInicial="1.3.6.1.2.1.25.2.3.1";
            classDisk objectDisk = new classDisk(""); 
            
            String OID=SNMP4JHelper.snmpGetNextOID(ipAddress,Comunidad,OIDInicial);            
            int iteracion;
            //Se repetirá el ciclo hasta que ya no se obtengan más datos. 
            String tempString; 
            classDisk discoTemporal = new classDisk(); 
            
            while(OID.contains(OIDInicial))
            {
                if(OID.contains(OIDInicial+".1")) 
                {                               
                    iteracion = 0;
                    while(OID.contains(OIDInicial+".1"))
                    {
                    	
                        discoTemporal = new classDisk(); 
                        tempString = SNMP4JHelper.snmpGet(ipAddress,Comunidad, OID);
                        discoTemporal.StorageIndex = tempString; 
                        OID=SNMP4JHelper.snmpGetNextOID(ipAddress,Comunidad,OID);                                 
                        arregloDiscos.add(discoTemporal); 
                    	iteracion++;
                    }

                }
                if(OID.contains(OIDInicial+".2")) 
                {                               
                    iteracion = 0;
                    while(OID.contains(OIDInicial+".2"))
                    {
                    	
                        tempString = SNMP4JHelper.snmpGet(ipAddress,Comunidad, OID);                        
                        ((classDisk) arregloDiscos.get(iteracion)).StorageType = tempString;
                        OID=SNMP4JHelper.snmpGetNextOID(ipAddress,Comunidad,OID);
                    	iteracion++;
                    }

                }
                
                if(OID.contains(OIDInicial+".3")) 
                {                               
                    iteracion = 0;
                    while(OID.contains(OIDInicial+".3"))
                    {
                    	tempString = SNMP4JHelper.snmpGet(ipAddress,Comunidad, OID);                        
                        ((classDisk) arregloDiscos.get(iteracion)).StorageDescr = tempString;
                        OID=SNMP4JHelper.snmpGetNextOID(ipAddress,Comunidad,OID);
                    	iteracion++;
                    	
                    }

                }
                if(OID.contains(OIDInicial+".4")) 
                {                               
                    iteracion = 0;
                    while(OID.contains(OIDInicial+".4"))
                    {
                    	tempString = SNMP4JHelper.snmpGet(ipAddress,Comunidad, OID);                        
                        ((classDisk) arregloDiscos.get(iteracion)).StorageAllocationUnits = Long.parseLong(tempString);
                        OID=SNMP4JHelper.snmpGetNextOID(ipAddress,Comunidad,OID);
                    	
                    	iteracion++;
                    }

                }
                if(OID.contains(OIDInicial+".5")) 
                {                               
                    iteracion = 0;
                    while(OID.contains(OIDInicial+".5"))
                    {
                    	tempString = SNMP4JHelper.snmpGet(ipAddress,Comunidad, OID);                        
                        ((classDisk) arregloDiscos.get(iteracion)).StorageSize = Long.parseLong(tempString)*((classDisk) arregloDiscos.get(iteracion)).StorageAllocationUnits/1024/1024/1024;
                        //objectDisk.StorageSize = objectDisk.StorageSize*objectDisk.StorageAllocationUnits/1024/1024/1024;   
                        OID=SNMP4JHelper.snmpGetNextOID(ipAddress,Comunidad,OID);
                    	iteracion++;
                    }

                }   
                
                if(OID.contains(OIDInicial+".6")) 
                {                               
                    iteracion = 0;
                    while(OID.contains(OIDInicial+".6"))
                    {
                    	tempString = SNMP4JHelper.snmpGet(ipAddress,Comunidad, OID);                        
                        ((classDisk) arregloDiscos.get(iteracion)).StorageUsed = Long.parseLong(tempString)*((classDisk) arregloDiscos.get(iteracion)).StorageAllocationUnits/1024/1024/1024;
                        OID=SNMP4JHelper.snmpGetNextOID(ipAddress,Comunidad,OID);
                    	iteracion++;
                    }

                }
                
                if(OID.contains(OIDInicial+".7")) 
                {                               
                    iteracion = 0;
                    while(OID.contains(OIDInicial+".7"))
                    {
                    	tempString = SNMP4JHelper.snmpGet(ipAddress,Comunidad, OID);                        
                        ((classDisk) arregloDiscos.get(iteracion)).StorageAllocationFailures = Long.parseLong(tempString);
                        OID=SNMP4JHelper.snmpGetNextOID(ipAddress,Comunidad,OID);
                    	iteracion++;
                    }

                }
                
                
                            
            /*
                    */
                        
    }
            
            //Recalculo de valores 
            
                
            
}
}
