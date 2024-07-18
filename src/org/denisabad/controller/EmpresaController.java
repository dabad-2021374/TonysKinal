package org.denisabad.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.swing.JOptionPane;
import org.denisabad.bean.Empresa;
import org.denisabad.db.Conexion;
import org.denisabad.main.Principal;
import org.denisabad.report.GenerarReporte;

public class EmpresaController implements Initializable {

    private enum operaciones {
        NUEVO, GUARDAR, ELIMINAR, ACTUALIZAR, CANCELAR, NINGUNO
    };
    private operaciones tipoOperacion = operaciones.NINGUNO;
    private Principal escenarioPrincipal;
    private ObservableList<Empresa> listaEmpresa;

    @FXML
    private TextField txtCodigoEmpresa;
    @FXML
    private TextField txtNombreEmpresa;
    @FXML
    private TextField txtDireccionEmpresa;
    @FXML
    private TextField txtTelefonoEmpresa;
    @FXML
    private TableView tblEmpresas;
    @FXML
    private TableColumn colCodigoEmpresa;
    @FXML
    private TableColumn colNombreEmpresa;
    @FXML
    private TableColumn colDireccionEmpresa;
    @FXML
    private TableColumn colTelefonoEmpresa;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnReporte;
    @FXML
    private ImageView imgNuevo;
    @FXML
    private ImageView imgEliminar;
    @FXML
    private ImageView imgEditar;
    @FXML
    private ImageView imgReporte;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
    }

    public void cargarDatos() {
        tblEmpresas.setItems(getEmpresa());
        colCodigoEmpresa.setCellValueFactory(new PropertyValueFactory<Empresa, Integer>("codigoEmpresa"));
        colNombreEmpresa.setCellValueFactory(new PropertyValueFactory<Empresa, String>("nombreEmpresa"));
        colDireccionEmpresa.setCellValueFactory(new PropertyValueFactory<Empresa, String>("direccion"));
        colTelefonoEmpresa.setCellValueFactory(new PropertyValueFactory<Empresa, String>("telefono"));
    }

    public ObservableList<Empresa> getEmpresa() {
        ArrayList<Empresa> lista = new ArrayList<Empresa>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_ListarEmpresas");
            ResultSet resultado = procedimiento.executeQuery();
            while (resultado.next()) {
                lista.add(new Empresa(resultado.getInt("codigoEmpresa"),
                        resultado.getString("nombreEmpresa"),
                        resultado.getString("direccion"),
                        resultado.getString("telefono")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaEmpresa = FXCollections.observableArrayList(lista);
    }

    public void seleccionarElemento() {
//      txtCodigoEmpresa.setText(String.valueOf((Empresa)tblEmpresas.getSelectionModel().getSelectedItem()));
        if (tblEmpresas.getSelectionModel().getSelectedItem() != null) {
            txtCodigoEmpresa.setText(String.valueOf(((Empresa) tblEmpresas.getSelectionModel().getSelectedItem()).getCodigoEmpresa()));
            txtNombreEmpresa.setText(((Empresa) tblEmpresas.getSelectionModel().getSelectedItem()).getNombreEmpresa());
            txtDireccionEmpresa.setText(((Empresa) tblEmpresas.getSelectionModel().getSelectedItem()).getDireccion());
            txtTelefonoEmpresa.setText(((Empresa) tblEmpresas.getSelectionModel().getSelectedItem()).getTelefono());
        } else {
            JOptionPane.showMessageDialog(null, "Selecciona un campo que tenga datos.");
        }
    }

    public void nuevo() {
        switch (tipoOperacion) {
            case NINGUNO:
                activarControles();
                limpiarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                imgNuevo.setImage(new Image("/org/denisabad/image/guardar.png"));
                imgEliminar.setImage(new Image("/org/denisabad/image/cancelar.png"));
                tipoOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                guardar();
                limpiarControles();
                desactivarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                imgNuevo.setImage(new Image("/org/denisabad/image/agregar.png"));
                imgEliminar.setImage(new Image("/org/denisabad/image/eliminar.png"));
                tipoOperacion = operaciones.NINGUNO;
                cargarDatos();
                break;
        }
    }

    public void guardar() {
        String nombreEmpre = txtNombreEmpresa.getText();
        nombreEmpre = nombreEmpre.replaceAll(" ", "");
        String direcc = txtDireccionEmpresa.getText();
        direcc = direcc.replaceAll(" ", "");
        String telEmpre = txtTelefonoEmpresa.getText();
        telEmpre = telEmpre.replaceAll(" ", "");
        if (nombreEmpre.length() == 0) {
            JOptionPane.showMessageDialog(null, "Todos los campos deben ser llenados");
        } else {
            if (direcc.length() == 0) {
                JOptionPane.showMessageDialog(null, "Todos los campos deben ser llenados");
            } else {
                if (telEmpre.length() == 0) {
                    JOptionPane.showMessageDialog(null, "Todos los campos deben ser llenados");
                } else {
                    Empresa registro = new Empresa();
                    //registro.setCodigoEmpresa(Integer.parseInt(txtCodigoEmpresa.getText()));
                    registro.setNombreEmpresa(txtNombreEmpresa.getText());
                    registro.setDireccion(txtDireccionEmpresa.getText());
                    registro.setTelefono(txtTelefonoEmpresa.getText());
                    try {
                        PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_AgregarEmpresa(?, ?, ?)");
                        procedimiento.setString(1, registro.getNombreEmpresa());
                        procedimiento.setString(2, registro.getDireccion());
                        procedimiento.setString(3, registro.getTelefono());
                        procedimiento.execute();
                        listaEmpresa.add(registro);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void eliminar() {
        switch (tipoOperacion) {
            case GUARDAR:
                limpiarControles();
                desactivarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                imgNuevo.setImage(new Image("/org/denisabad/image/agregar.png"));
                imgEliminar.setImage(new Image("/org/denisabad/image/eliminar.png"));
                tipoOperacion = operaciones.NINGUNO;
                break;
            default:
                if (tblEmpresas.getSelectionModel().getSelectedItem() != null) {
                    int respuesta = JOptionPane.showConfirmDialog(null, "Está seguro de eliminar el registro?", "Eliminar Empresa", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (respuesta == JOptionPane.YES_OPTION) {
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_EliminarEmpresa(?)");
                            procedimiento.setInt(1, ((Empresa) tblEmpresas.getSelectionModel().getSelectedItem()).getCodigoEmpresa());
                            procedimiento.execute();
                            listaEmpresa.remove(tblEmpresas.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            deseleccionar();
                        } catch (SQLException e) {
                            JOptionPane.showMessageDialog(null, "No se puede eliminar porque esta relacionado con otro dato;(");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (respuesta == JOptionPane.NO_OPTION) {
                        desactivarControles();
                        deseleccionar();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento;) ");
                }
        }
    }

    public void editar() {
        switch (tipoOperacion) {
            case NINGUNO:
                if (tblEmpresas.getSelectionModel().getSelectedItem() != null) {
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    imgEditar.setImage(new Image("/org/denisabad/image/actualizar.png"));
                    imgReporte.setImage(new Image("/org/denisabad/image/cancelar.png"));
                    activarControles();
                    tipoOperacion = operaciones.ACTUALIZAR;
                } else {
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento;) ");
                }
                break;
            case ACTUALIZAR:
                actualizar();
                limpiarControles();
                desactivarControles();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                imgEditar.setImage(new Image("/org/denisabad/image/editar.png"));
                imgReporte.setImage(new Image("/org/denisabad/image/Reporte.png"));
                tipoOperacion = operaciones.NINGUNO;
                cargarDatos();
                break;
        }
    }

    public void actualizar() {
        String nombreEmpre = txtNombreEmpresa.getText();
        nombreEmpre = nombreEmpre.replaceAll(" ", "");
        String direcc = txtDireccionEmpresa.getText();
        direcc = direcc.replaceAll(" ", "");
        String telEmpre = txtTelefonoEmpresa.getText();
        telEmpre = telEmpre.replaceAll(" ", "");
        if (nombreEmpre.length() == 0) {
            JOptionPane.showMessageDialog(null, "Todos los campos deben ser llenados");
        } else {
            if (direcc.length() == 0) {
                JOptionPane.showMessageDialog(null, "Todos los campos deben ser llenados");
            } else {
                if (telEmpre.length() == 0) {
                    JOptionPane.showMessageDialog(null, "Todos los campos deben ser llenados");
                } else {
                    try {
                        PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_EditarEmpresa(?,?,?,?)");
                        Empresa registro = (Empresa) tblEmpresas.getSelectionModel().getSelectedItem();
                        registro.setNombreEmpresa(txtNombreEmpresa.getText());
                        registro.setDireccion(txtDireccionEmpresa.getText());
                        registro.setTelefono(txtTelefonoEmpresa.getText());
                        procedimiento.setInt(1, registro.getCodigoEmpresa());
                        procedimiento.setString(2, registro.getNombreEmpresa());
                        procedimiento.setString(3, registro.getDireccion());
                        procedimiento.setString(4, registro.getTelefono());
                        procedimiento.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void reporte() {
        switch (tipoOperacion) {
            case NINGUNO:
                imprimirReporte();
                deseleccionar();
                break;
            case ACTUALIZAR:
                limpiarControles();
                desactivarControles();
                deseleccionar();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                imgEditar.setImage(new Image("/org/denisabad/image/editar.png"));
                imgReporte.setImage(new Image("/org/denisabad/image/Reporte.png"));
                tipoOperacion = operaciones.NINGUNO;
                tblEmpresas.getSelectionModel().clearSelection();
                break;
        }
    }
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
        parametros.put("Imagen", GenerarReporte.class.getResource("/org/denisabad/image/LogoDos.png"));
        parametros.put("codigoEmpresa", null);
        GenerarReporte.mostrarReporte("ReporteEmpresas.jasper", "Reporte de Empresas", parametros);
    }

    public void deseleccionar() {
        tblEmpresas.getSelectionModel().clearSelection();
        limpiarControles();
    }

    public void desactivarControles() {
        txtCodigoEmpresa.setEditable(false);
        txtNombreEmpresa.setEditable(false);
        txtDireccionEmpresa.setEditable(false);
        txtTelefonoEmpresa.setEditable(false);
    }

    public void activarControles() {
        txtCodigoEmpresa.setEditable(false);
        txtNombreEmpresa.setEditable(true);
        txtDireccionEmpresa.setEditable(true);
        txtTelefonoEmpresa.setEditable(true);
    }

    public void limpiarControles() {
        txtCodigoEmpresa.clear();
        txtNombreEmpresa.clear();
        txtDireccionEmpresa.clear();
        txtTelefonoEmpresa.clear();
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
    
    public void ventanaPresupuesto(){
        escenarioPrincipal.ventanaPresupuesto();
    }
    
    public void ventanaServicio(){
        escenarioPrincipal.ventanaServicio();
    }
}
