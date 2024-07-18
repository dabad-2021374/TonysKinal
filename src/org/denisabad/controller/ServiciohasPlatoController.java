package org.denisabad.controller;

import java.awt.Toolkit;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.swing.JOptionPane;
import org.denisabad.bean.Plato;
import org.denisabad.bean.Servicio;
import org.denisabad.bean.ServicioHasPlato;
import org.denisabad.db.Conexion;
import org.denisabad.main.Principal;

public class ServiciohasPlatoController implements Initializable {

    private Principal escenarioPrincipal;

    private enum operaciones {
        NUEVO, GUARDAR, ELIMINAR, ACTUALIZAR, CANCELAR, NINGUNO
    };
    private operaciones tipoOperacion = operaciones.NINGUNO;

    private ObservableList<ServicioHasPlato> listaServiciohasPlato;
    private ObservableList<Plato> listaPlato;
    private ObservableList<Servicio> listaServicio;
    private @FXML
    TextField txtServiciosCodigoServicio;
    private @FXML
    ComboBox cmbCodigoPlato;
    private @FXML
    ComboBox cmbCodigoServicio;
    private @FXML
    Button btnNuevo;
    private @FXML
    Button btnReporte;
    private @FXML
    Button btnEliminar;
    private @FXML
    Button btnEditar;
    private @FXML
    TableView tblServicioshasPlatos;
    private @FXML
    TableColumn colServiciosCodigoServicio;
    private @FXML
    TableColumn colCodigoPlato;
    private @FXML
    TableColumn colCodigoServicio;
    private @FXML
    ImageView imgNuevo;
    private @FXML
    ImageView imgReporte;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoPlato.setItems(getPlato());
        cmbCodigoServicio.setItems(getServicio());
        btnEliminar.setDisable(true);
        btnEditar.setDisable(true);
    }

    public void cargarDatos() {
        try {
            tblServicioshasPlatos.setItems(getServicioHasPlatos());
            colServiciosCodigoServicio.setCellValueFactory(new PropertyValueFactory<ServicioHasPlato, Integer>("Servicios_codigoServicio"));
            colCodigoPlato.setCellValueFactory(new PropertyValueFactory<ServicioHasPlato, Integer>("codigoPlato"));
            colCodigoServicio.setCellValueFactory(new PropertyValueFactory<ServicioHasPlato, Integer>("codigoServicio"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ObservableList<ServicioHasPlato> getServicioHasPlatos() {
        ArrayList<ServicioHasPlato> lista = new ArrayList<ServicioHasPlato>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_ListarServicios_has_Platos()");
            ResultSet resultado = procedimiento.executeQuery();
            while (resultado.next()) {
                lista.add(new ServicioHasPlato(resultado.getInt("Servicios_codigoServicio"),
                        resultado.getInt("codigoPlato"),
                        resultado.getInt("codigoServicio")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaServiciohasPlato = FXCollections.observableArrayList(lista);
    }

    public ObservableList<Plato> getPlato() {
        ArrayList<Plato> lista = new ArrayList<Plato>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_ListarPlatos()");
            ResultSet resultado = procedimiento.executeQuery();
            while (resultado.next()) {
                lista.add(new Plato(resultado.getInt("codigoPlato"),
                        resultado.getInt("cantidad"),
                        resultado.getString("nombrePlato"),
                        resultado.getString("descripcion"),
                        resultado.getDouble("precioPlato"),
                        resultado.getInt("codigoTipoPlato")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaPlato = FXCollections.observableArrayList(lista);
    }

    public ObservableList<Servicio> getServicio() {
        ArrayList<Servicio> lista = new ArrayList<Servicio>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_ListarServicios()");
            ResultSet resultado = procedimiento.executeQuery();
            while (resultado.next()) {
                lista.add(new Servicio(resultado.getInt("codigoServicio"),
                        resultado.getDate("fechaServicio"),
                        resultado.getString("tipoServicio"),
                        resultado.getString("horaServicio"),
                        resultado.getString("lugarServicio"),
                        resultado.getString("telefonoContacto"),
                        resultado.getInt("codigoEmpresa")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaServicio = FXCollections.observableArrayList(lista);
    }

    public void seleccionarElemento() {
        if (tblServicioshasPlatos.getSelectionModel().getSelectedItem() != null) {
            txtServiciosCodigoServicio.setText(String.valueOf(((ServicioHasPlato) tblServicioshasPlatos.getSelectionModel().getSelectedItem()).getServicios_codigoServicio()));
            cmbCodigoPlato.getSelectionModel().select(buscarPlato(((ServicioHasPlato) tblServicioshasPlatos.getSelectionModel().getSelectedItem()).getCodigoPlato()));
            cmbCodigoServicio.getSelectionModel().select(buscarServicio(((ServicioHasPlato) tblServicioshasPlatos.getSelectionModel().getSelectedItem()).getCodigoServicio()));
        } else {
            JOptionPane.showMessageDialog(null, "Selecciona un campo que tenga datos.");
        }
    }

    public Plato buscarPlato(int codigoPlato) {
        Plato resultado = null;
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_BuscarPlato(?)");
            procedimiento.setInt(1, codigoPlato);
            ResultSet registro = procedimiento.executeQuery();
            while (registro.next()) {
                resultado = new Plato(registro.getInt("codigoPlato"),
                        registro.getInt("cantidad"),
                        registro.getString("nombrePlato"),
                        registro.getString("descripcion"),
                        registro.getDouble("precioPlato"),
                        registro.getInt("codigoTipoPlato"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultado;
    }

    public Servicio buscarServicio(int codigoServicio) {
        Servicio resultado = null;
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_BuscarServicio(?)");
            procedimiento.setInt(1, codigoServicio);
            ResultSet registro = procedimiento.executeQuery();
            while (registro.next()) {
                resultado = new Servicio(registro.getInt("codigoServicio"),
                        registro.getDate("fechaServicio"),
                        registro.getString("tipoServicio"),
                        registro.getString("horaServicio"),
                        registro.getString("lugarServicio"),
                        registro.getString("telefonoContacto"),
                        registro.getInt("codigoEmpresa"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultado;
    }

    public void nuevo() {
        switch (tipoOperacion) {
            case NINGUNO:
                activarControles();
                btnNuevo.setText("Guardar");
                btnReporte.setText("Cancelar");
                imgNuevo.setImage(new Image("/org/denisabad/image/guardar.png"));
                imgReporte.setImage(new Image("/org/denisabad/image/cancelar.png"));
                tipoOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                guardar();
                limpiarControles();
                desactivarControles();
                btnNuevo.setText("Nuevo");
                btnReporte.setText("Reporte");
                imgNuevo.setImage(new Image("/org/denisabad/image/agregar.png"));
                imgReporte.setImage(new Image("/org/denisabad/image/Reporte.png"));
                tipoOperacion = operaciones.NINGUNO;
                cargarDatos();
                break;
        }
    }

    public void guardar() {
        String codSerHasServi = txtServiciosCodigoServicio.getText();
        codSerHasServi = codSerHasServi.replaceAll(" ", "");
        if (codSerHasServi.length() == 0) {
            JOptionPane.showMessageDialog(null, "Debe colocar el código ServiciosCodigoServicio");
        } else {
            if (cmbCodigoPlato.getSelectionModel().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Debe seleccionar el código del plato");
            } else {
                if (cmbCodigoServicio.getSelectionModel().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Debe seleccionar el código del servicio");
                } else {
                    try {
                        ServicioHasPlato registro = new ServicioHasPlato();
                        registro.setServicios_codigoServicio(Integer.parseInt(txtServiciosCodigoServicio.getText()));
                        registro.setCodigoPlato(((Plato) cmbCodigoPlato.getSelectionModel().getSelectedItem()).getCodigoPlato());
                        registro.setCodigoServicio(((Servicio) cmbCodigoServicio.getSelectionModel().getSelectedItem()).getCodigoServicio());
                        PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_AgregarServicio_has_Plato(?,?,?)");
                        procedimiento.setInt(1, registro.getServicios_codigoServicio());
                        procedimiento.setInt(2, registro.getCodigoPlato());
                        procedimiento.setInt(3, registro.getCodigoServicio());
                        procedimiento.execute();
                        listaServiciohasPlato.add(registro);
                    } catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e) {
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(null, "El código ServiciosCodigoServicio ya existe", "Aviso", JOptionPane.WARNING_MESSAGE);
                    } catch (java.lang.NumberFormatException e) {
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(null, "El valor del código ServiciosCodigoServicio solo puede ser de tipo númerico", "Aviso", JOptionPane.WARNING_MESSAGE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void reporte() {
        switch (tipoOperacion) {
            case GUARDAR:
                limpiarControles();
                desactivarControles();
                deseleccionar();
                btnNuevo.setText("Nuevo");
                btnReporte.setText("Reporte");
                imgNuevo.setImage(new Image("/org/denisabad/image/editar.png"));
                imgReporte.setImage(new Image("/org/denisabad/image/Reporte.png"));
                tipoOperacion = operaciones.NINGUNO;
                tblServicioshasPlatos.getSelectionModel().clearSelection();
                break;
        }
    }

    public void deseleccionar() {
        tblServicioshasPlatos.getSelectionModel().clearSelection();
        limpiarControles();
    }

    public void desactivarControles() {
        txtServiciosCodigoServicio.setEditable(false);
        cmbCodigoPlato.setDisable(true);
        cmbCodigoServicio.setDisable(true);
    }

    public void activarControles() {
        txtServiciosCodigoServicio.setEditable(true);
        cmbCodigoPlato.setDisable(false);
        cmbCodigoServicio.setDisable(false);
    }

    public void limpiarControles() {
        txtServiciosCodigoServicio.clear();
        cmbCodigoPlato.setValue(null);
        cmbCodigoServicio.setValue(null);
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }

    public void menuPrincipal() {
        escenarioPrincipal.menuPrincipal();
    }
}
