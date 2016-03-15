/*
 * Copyright (C) 2015 hcadavid
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.eci.pdsw.webappsintro.jdbc.example.basic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class JDBCExample {
    
    public static void main(String args[]){
        try {
            String url="jdbc:mysql://desarrollo.is.escuelaing.edu.co:3306/bdprueba";
            String driver="com.mysql.jdbc.Driver";
            String user="bdprueba";
            String pwd="bdprueba";
                        
            Class.forName(driver);
            Connection con=DriverManager.getConnection(url,user,pwd);
            con.setAutoCommit(false);
                 
            
            System.out.println("Valor total pedido 1:"+valorTotalPedido(con, 1));
            
            List<String> prodsPedido=nombresProductosPedido(con, 1);
            
            
            System.out.println("Productos del pedido 1:");
            System.out.println("-----------------------");
            for (String nomprod:prodsPedido){
                System.out.println(nomprod);
            }
            System.out.println("-----------------------");
            
            
            int suCodigoECI=12345678;
            registrarNuevoProducto(con, suCodigoECI, "SU NOMBRE", 99999999);            
            con.commit();
            
            cambiarNombreProducto(con, suCodigoECI, "EL NUEVO NOMBRE");
            con.commit();
            
            
            con.close();
                                   
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(JDBCExample.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    /**
     * Agregar un nuevo producto con los parámetros dados
     * @param con la conexión JDBC
     * @param codigo
     * @param nombre
     * @param precio
     * @throws SQLException 
     */
    public static void registrarNuevoProducto(Connection con, int codigo, String nombre,int precio) throws SQLException{
        //Crear preparedStatement
        //Asignar parámetros
        //usar 'execute'
        
            con.setAutoCommit(false);
            String consulta = "INSERT INTO ORD_PRODUCTOS VALUES (?, ?, ?)";
            PreparedStatement registrarProducto = con.prepareStatement(consulta);
            registrarProducto.setInt(1, codigo);
            registrarProducto.setString(2, "\""+nombre+"\"");
            registrarProducto.setInt(3, precio);
            registrarProducto.executeUpdate();            
            con.commit();
            
        
        
        con.commit();
        
    }
    
    /**
     * Consultar los nombres de los productos asociados a un pedido
     * @param con la conexión JDBC
     * @param codigoPedido el código del pedido
     * @return 
     */
    public static List<String> nombresProductosPedido(Connection con, int codigoPedido) {
        List<String> np=new LinkedList<>();
        
        //Crear prepared statement
        //asignar parámetros
        //usar executeQuery
        //Sacar resultados del ResultSet
        //Llenar la lista y retornarla
        try{
            String cosulta ="SELECT ORD_PRODUCTOS.nombre "
                    + "FROM ORD_PRODUCTOS INNER JOIN ORD_DETALLES_PEDIDO ON ORD_PRODUCTOS.codigo=ORD_DETALLES_PEDIDO.producto_fk "
                    + "INNER JOIN ORD_PEDIDOS ON ORD_DETALLES_PEDIDO.pedido_fk=ORD_PRODUCTOS.codigo "
                    + "WHERE ORD_PEDIDOS.codigo= ?";
            PreparedStatement listaProductosPedido = con.prepareStatement(cosulta);
            listaProductosPedido.setString(1, codigoPedido+"");
            ResultSet executeQuery = listaProductosPedido.executeQuery();
            while(executeQuery.next()){
                np.add(executeQuery.getString(1));
            }
            con.commit();
        }catch (SQLException e){
            np.clear();
        }
        return np;
    }

    
    /**
     * Calcular el costo total de un pedido
     * @param con
     * @param codigoPedido código del pedido cuyo total se calculará
     * @return el costo total del pedido (suma de: cantidades*precios)
     * @throws java.sql.SQLException
     */
    public static int valorTotalPedido(Connection con, int codigoPedido) throws SQLException{
        try{
            con.setAutoCommit(false);
            String consulta = "SELECT SUM(precio) FROM ORD_PRODUCTOS INNER JOIN ORD_DETALLES_PEDIDO ON ORD_PRODUCTOS.codigo=ORD_DETALLES_PEDIDO.producto_fk "
                    + "INNER JOIN ORD_PEDIDOS ON ORD_DETALLES_PEDIDO.pedido_fk=ORD_PRODUCTOS.codigo "
                    + "WHERE ORD_PEDIDOS.codigo= ?";
            PreparedStatement calcularValor = con.prepareStatement(consulta);
            calcularValor.setString(1, codigoPedido+"");
            ResultSet executeQuery = calcularValor.executeQuery();
            con.commit();
            if(executeQuery.next()){
                return executeQuery.getInt(1);
            }else return 0;
        }catch(SQLException e){
            return -1;
        }
        //Crear prepared statement
        //asignar parámetros
        //usar executeQuery
        //Sacar resultado del ResultSet

    }
    

    /**
     * Cambiar el nombre de un producto
     * @param con
     * @param codigoProducto codigo del producto cuyo nombre se cambiará
     * @param nuevoNombre el nuevo nombre a ser asignado
     */
    public static void cambiarNombreProducto(Connection con, int codigoProducto, 
            String nuevoNombre) throws SQLException{
        
        //Crear prepared statement
        //asignar parámetros
        //usar executeUpdate
        //verificar que se haya actualizado exactamente un registro
        String consulta = "UPDATE ORD_PRODUCTOS SET nombre= ? WHERE codigo= ?";
        PreparedStatement cambiarNombre = con.prepareStatement(consulta);
        cambiarNombre.setString(1,nuevoNombre);
        cambiarNombre.setInt(2,codigoProducto);        
        cambiarNombre.executeUpdate();
        con.commit();
    }
    
    
    
}
