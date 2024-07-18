/*
    Nombre: Denis Sebastian Abad Santos
    Carnet: 2021374
    Sección: IN5AM
    Fecha de creación: 
        28/03/2023
    Fechas de modificaciones:
        11/04/2023 vista menú principal y vista programador
        16/04/2023 controllador empresa 
        17/04/2023 vista empresa
        18/04/2023 modelo de datos de empresas

*/

package org.denisabad.main;

import java.io.IOException;
import java.io.InputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.denisabad.controller.EmpleadoController;
import org.denisabad.controller.MenuPrincipalController;
import org.denisabad.controller.ProgramadorController;
import org.denisabad.controller.EmpresaController;
import org.denisabad.controller.LoginController;
import org.denisabad.controller.PlatoController;
import org.denisabad.controller.PresupuestoController;
import org.denisabad.controller.ProductoController;
import org.denisabad.controller.ProductohasPlatoController;
import org.denisabad.controller.ServicioController;
import org.denisabad.controller.ServiciohasEmpleadoController;
import org.denisabad.controller.ServiciohasPlatoController;
import org.denisabad.controller.TipoEmpleadoController;
import org.denisabad.controller.TipoPlatoController;
import org.denisabad.controller.UsuarioController;

public class Principal extends Application {
    private final String PAQUETE_VISTA = "/org/denisabad/view/";
    private Stage escenarioPrincipal; 
    private Scene escena;
    
    @Override
    public void start(Stage escenarioPrincipal) throws IOException {
        this.escenarioPrincipal = escenarioPrincipal;
        this.escenarioPrincipal.setTitle("Tony's Kinal 2023");
        escenarioPrincipal.getIcons().add(new Image("/org/denisabad/image/Icono.png"));
        escenarioPrincipal.setResizable(false);
        login();
        escenarioPrincipal.show();
    }
    
    public void menuPrincipal(){
        try{
            MenuPrincipalController menu = (MenuPrincipalController)cambiarEscena("MenuPrincipalView.fxml", 546, 465);
            menu.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }    
    }
    
    public void ventanaProgramador(){
        try{
           ProgramadorController programador = (ProgramadorController)cambiarEscena("ProgramadorView.fxml", 555, 438);
           programador.setEscenarioPrincipal(this);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaEmpresa(){
        try{
           EmpresaController empresa = (EmpresaController)cambiarEscena("EmpresaView.fxml", 780, 470);
           empresa.setEscenarioPrincipal(this);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaTipoEmpleado(){
        try {
            TipoEmpleadoController tipoEmpleado = (TipoEmpleadoController)cambiarEscena("TipoEmpleadoView.fxml", 780, 470);
            tipoEmpleado.setEscenarioPrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaTipoPlato(){
        try{
            TipoPlatoController tipoPlato = (TipoPlatoController)cambiarEscena("TipoPlatoView.fxml", 780, 470);
            tipoPlato.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();    
        }
    }
    
    public void ventanaProducto(){
        try{
            ProductoController producto = (ProductoController)cambiarEscena("ProductoView.fxml", 780, 470);
            producto.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();  
        }
    }
    
    public void ventanaPresupuesto(){
        try{
            PresupuestoController presupuesto = (PresupuestoController)cambiarEscena("PresupuestoView.fxml", 850, 470);
            presupuesto.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();  
        }
    }
    
    public void ventanaEmpleado(){
        try {
            EmpleadoController empleado = (EmpleadoController)cambiarEscena("EmpleadoView.fxml", 1100, 536);
            empleado.setEscenarioPrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaPlato(){
        try {
            PlatoController plato = (PlatoController)cambiarEscena("PlatoView.fxml", 1100, 536);
            plato.setEscenarioPrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaProductohasPlato(){
        try {
            ProductohasPlatoController prohasplato = (ProductohasPlatoController)cambiarEscena("ProductohasPlatoView.fxml", 780, 470);
            prohasplato.setEscenarioPrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaServicio(){
        try {
            ServicioController servicio = (ServicioController)cambiarEscena("ServicioView.fxml", 1100, 536);
            servicio.setEscenarioPrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaServiciohasPlato(){
        try {
            ServiciohasPlatoController servihasplato = (ServiciohasPlatoController)cambiarEscena("ServiciohasPlatoView.fxml", 780, 470);
            servihasplato.setEscenarioPrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaServiciohasEmpleado(){
        try {
            ServiciohasEmpleadoController servihasEmple = (ServiciohasEmpleadoController)cambiarEscena("ServiciohasEmpleadoView.fxml", 950, 470);
            servihasEmple.setEscenarioPrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void login(){
        try {
            LoginController login = (LoginController)cambiarEscena("LoginView.fxml", 700, 500);
            login.setEscePrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public void ventanaUsuario(){
        try {
            UsuarioController usuario = (UsuarioController)cambiarEscena("UsuarioView.fxml", 700, 420);
            usuario.setEscePrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
    public Initializable cambiarEscena(String fxml, int ancho, int alto) throws Exception{
        Initializable resultado = null;
        FXMLLoader cargadorFXML = new FXMLLoader();  
        InputStream archivo  = Principal.class.getResourceAsStream(PAQUETE_VISTA+fxml);
        cargadorFXML.setBuilderFactory(new JavaFXBuilderFactory());
        cargadorFXML.setLocation(Principal.class.getResource(PAQUETE_VISTA+fxml));
        escena = new Scene((AnchorPane)cargadorFXML.load(archivo),ancho,alto);
        //escenarioPrincipal.centerOnScreen();
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.sizeToScene();
        resultado = (Initializable)cargadorFXML.getController(); 
        return resultado;  
    }
}