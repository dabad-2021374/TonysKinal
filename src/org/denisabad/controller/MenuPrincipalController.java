package org.denisabad.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import org.denisabad.main.Principal;


public class MenuPrincipalController implements Initializable{
    private Principal escenarioPrincipal; 
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void ventanaProgramador(){
        escenarioPrincipal.ventanaProgramador();
    }
    
    public void ventanaEmpresa(){
        escenarioPrincipal.ventanaEmpresa();
    }
    
    public void ventanaProducto(){
        escenarioPrincipal.ventanaProducto();
    }
    
    public void ventanaTipoPlato(){
        escenarioPrincipal.ventanaTipoPlato();
    }
    
    public void ventanaTipoEmpleado(){
        escenarioPrincipal.ventanaTipoEmpleado();
    }
    
    public void ventanaProductohasPlato(){
        escenarioPrincipal.ventanaProductohasPlato();
    }
    
    public void ventanaServiciohasPlato(){
        escenarioPrincipal.ventanaServiciohasPlato();
    }
    
    public void ventanaServiciohasEmpleado(){
        escenarioPrincipal.ventanaServiciohasEmpleado();
    }
    
    public void login(){
        escenarioPrincipal.login();
    }
    
    public void ventanaUsuario(){
        escenarioPrincipal.ventanaUsuario();
    }
}
