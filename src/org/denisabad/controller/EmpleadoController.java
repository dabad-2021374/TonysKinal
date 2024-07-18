package org.denisabad.controller;

import java.awt.Toolkit;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import org.denisabad.bean.Empleado;
import org.denisabad.bean.TipoEmpleado;
import org.denisabad.db.Conexion;
import org.denisabad.main.Principal;

public class EmpleadoController implements Initializable {

    private Principal escenarioPrincipal;

    private enum operaciones {
        NUEVO, GUARDAR, ELIMINAR, ACTUALIZAR, CANCELAR, NINGUNO
    };
    private operaciones tipoOperacion = operaciones.NINGUNO;

    private ObservableList<Empleado> listaEmpleado;
    private ObservableList<TipoEmpleado> listaTipoEmpleado;

    @FXML
    private TextField txtCodigoEmpleado;
    @FXML
    private TextField txtNumeroEmpleado;
    @FXML
    private TextField txtApellidosEmpleado;
    @FXML
    private TextField txtNombresEmpleado;
    @FXML
    private TextField txtDireccionEmpleado;
    @FXML
    private TextField txtTelefonoContacto;
    @FXML
    private TextField txtGradoCocinero;
    @FXML
    private ComboBox cmbCodigoTipoEmpleado;
    @FXML
    private TableView tblEmpleados;
    @FXML
    private TableColumn colCodigoEmpleado;
    @FXML
    private TableColumn colNumeroEmpleado;
    @FXML
    private TableColumn colApellidosEmpleado;
    @FXML
    private TableColumn colNombresEmpleado;
    @FXML
    private TableColumn colDireccionEmpleado;
    @FXML
    private TableColumn colTelefonoContacto;
    @FXML
    private TableColumn colGradoCocinero;
    @FXML
    private TableColumn colCodigoTipoEmpleado;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnEliminar;
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
        desactivarControles();
        cmbCodigoTipoEmpleado.setItems(getTipoEmpleado());
    }

    public void cargarDatos() {
        try {
            tblEmpleados.setItems(getEmpleado());
            colCodigoEmpleado.setCellValueFactory(new PropertyValueFactory<Empleado, Integer>("codigoEmpleado"));
            colNumeroEmpleado.setCellValueFactory(new PropertyValueFactory<Empleado, Integer>("numeroEmpleado"));
            colApellidosEmpleado.setCellValueFactory(new PropertyValueFactory<Empleado, String>("apellidosEmpleado"));
            colNombresEmpleado.setCellValueFactory(new PropertyValueFactory<Empleado, String>("nombresEmpleado"));
            colDireccionEmpleado.setCellValueFactory(new PropertyValueFactory<Empleado, String>("direccionEmpleado"));
            colTelefonoContacto.setCellValueFactory(new PropertyValueFactory<Empleado, String>("telefonoContacto"));
            colGradoCocinero.setCellValueFactory(new PropertyValueFactory<Empleado, String>("gradoCocinero"));
            colCodigoTipoEmpleado.setCellValueFactory(new PropertyValueFactory<Empleado, Integer>("codigoTipoEmpleado"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ObservableList<Empleado> getEmpleado() {
        ArrayList<Empleado> lista = new ArrayList<Empleado>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_ListarEmpleados()");
            ResultSet resultado = procedimiento.executeQuery();
            while (resultado.next()) {
                lista.add(new Empleado(resultado.getInt("codigoEmpleado"),
                        resultado.getInt("numeroEmpleado"),
                        resultado.getString("apellidosEmpleado"),
                        resultado.getString("nombresEmpleado"),
                        resultado.getString("direccionEmpleado"),
                        resultado.getString("telefonoContacto"),
                        resultado.getString("gradoCocinero"),
                        resultado.getInt("codigoTipoEmpleado")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaEmpleado = FXCollections.observableArrayList(lista);
    }

    public ObservableList<TipoEmpleado> getTipoEmpleado() {
        ArrayList<TipoEmpleado> lista = new ArrayList<TipoEmpleado>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_ListarTipoEmpleados");
            ResultSet resultado = procedimiento.executeQuery();
            while (resultado.next()) {
                lista.add(new TipoEmpleado(resultado.getInt("codigoTipoEmpleado"),
                        resultado.getString("descripcion")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaTipoEmpleado = FXCollections.observableArrayList(lista);
    }

    public void seleccionarElemento() {
        if (tblEmpleados.getSelectionModel().getSelectedItem() != null) {
            txtCodigoEmpleado.setText(String.valueOf(((Empleado) tblEmpleados.getSelectionModel().getSelectedItem()).getCodigoEmpleado()));
            txtNumeroEmpleado.setText(String.valueOf(((Empleado) tblEmpleados.getSelectionModel().getSelectedItem()).getNumeroEmpleado()));
            txtApellidosEmpleado.setText(((Empleado) tblEmpleados.getSelectionModel().getSelectedItem()).getApellidosEmpleado());
            txtNombresEmpleado.setText(((Empleado) tblEmpleados.getSelectionModel().getSelectedItem()).getNombresEmpleado());
            txtDireccionEmpleado.setText(((Empleado) tblEmpleados.getSelectionModel().getSelectedItem()).getDireccionEmpleado());
            txtTelefonoContacto.setText(((Empleado) tblEmpleados.getSelectionModel().getSelectedItem()).getTelefonoContacto());
            txtGradoCocinero.setText(((Empleado) tblEmpleados.getSelectionModel().getSelectedItem()).getGradoCocinero());
            cmbCodigoTipoEmpleado.getSelectionModel().select(buscarTipoEmpleado(((Empleado) tblEmpleados.getSelectionModel().getSelectedItem()).getCodigoTipoEmpleado()));
        } else {
            JOptionPane.showMessageDialog(null, "Selecciona un campo que tenga datos.");
        }
    }

    public TipoEmpleado buscarTipoEmpleado(int codigoTipoEmpleado) {
        TipoEmpleado resultado = null;
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_BuscarTipoEmpleado(?)");
            procedimiento.setInt(1, codigoTipoEmpleado);
            ResultSet registro = procedimiento.executeQuery();
            while (registro.next()) {
                resultado = new TipoEmpleado(registro.getInt("codigoTipoEmpleado"),
                        registro.getString("descripcion"));
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
        String numEmpleado = txtNumeroEmpleado.getText();
        String apeEmple = txtApellidosEmpleado.getText();
        String nomEmple = txtNombresEmpleado.getText();
        String direcc = txtDireccionEmpleado.getText();
        String telContac = txtTelefonoContacto.getText();
        numEmpleado = numEmpleado.replaceAll(" ", "");
        apeEmple = apeEmple.replaceAll(" ", "");
        nomEmple = nomEmple.replaceAll(" ", "");
        direcc = direcc.replaceAll(" ", "");
        telContac = telContac.replaceAll(" ", "");
        if (numEmpleado.length() == 0) {
            JOptionPane.showMessageDialog(null, "Debe colocar el número de empleado");
        } else {
            if (apeEmple.length() == 0) {
                JOptionPane.showMessageDialog(null, "Debe colocar los apellidos del empleado");
            } else {
                if (nomEmple.length() == 0) {
                    JOptionPane.showMessageDialog(null, "Debe colocar los nombres del empleado");
                } else {
                    if (direcc.length() == 0) {
                        JOptionPane.showMessageDialog(null, "Debe colocar la dirección del empleado");
                    } else {
                        if (telContac.length() == 0) {
                            JOptionPane.showMessageDialog(null, "Debe colocar el teléfono del empleado");
                        } else {
                            if (cmbCodigoTipoEmpleado.getSelectionModel().isEmpty()) {
                                JOptionPane.showMessageDialog(null, "Debe seleccionar el código del tipo empleado");
                            } else {
                                try {
                                    Empleado registro = new Empleado();
                                    registro.setNumeroEmpleado(Integer.parseInt(txtNumeroEmpleado.getText()));
                                    registro.setNombresEmpleado(txtNombresEmpleado.getText());
                                    registro.setApellidosEmpleado(txtApellidosEmpleado.getText());
                                    registro.setDireccionEmpleado(txtDireccionEmpleado.getText());
                                    registro.setTelefonoContacto(txtTelefonoContacto.getText());
                                    registro.setGradoCocinero(txtGradoCocinero.getText());
                                    registro.setCodigoTipoEmpleado(((TipoEmpleado) cmbCodigoTipoEmpleado.getSelectionModel().getSelectedItem()).getCodigoTipoEmpleado());
                                    PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarEmpleado(?,?,?,?,?,?,?)}");
                                    procedimiento.setInt(1, registro.getNumeroEmpleado());
                                    procedimiento.setString(2, registro.getApellidosEmpleado());
                                    procedimiento.setString(3, registro.getNombresEmpleado());
                                    procedimiento.setString(4, registro.getDireccionEmpleado());
                                    procedimiento.setString(5, registro.getTelefonoContacto());
                                    procedimiento.setString(6, registro.getGradoCocinero());
                                    procedimiento.setInt(7, registro.getCodigoTipoEmpleado());
                                    procedimiento.execute();
                                    listaEmpleado.add(registro);
                                } catch (java.lang.NumberFormatException e) {
                                    Toolkit.getDefaultToolkit().beep();
                                    JOptionPane.showMessageDialog(null, "El valor del número de empleado solo puede ser de tipo númerico", "Aviso", JOptionPane.WARNING_MESSAGE);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
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
                if (tblEmpleados.getSelectionModel().getSelectedItem() != null) {
                    int respuesta = JOptionPane.showConfirmDialog(null, "Está seguro de eliminar el registro?", "¿Eliminar el empleado?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (respuesta == JOptionPane.YES_OPTION) {
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_EliminarEmpleado(?)");
                            procedimiento.setInt(1, ((Empleado) tblEmpleados.getSelectionModel().getSelectedItem()).getCodigoEmpleado());
                            procedimiento.execute();
                            listaEmpleado.remove(tblEmpleados.getSelectionModel().getSelectedIndex());
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
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento;)");
                }
        }
    }

    public void editar() {
        switch (tipoOperacion) {
            case NINGUNO:
                if (tblEmpleados.getSelectionModel().getSelectedItem() != null) {
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    imgEditar.setImage(new Image("/org/denisabad/image/actualizar.png"));
                    imgReporte.setImage(new Image("/org/denisabad/image/cancelar.png"));
                    activarControles();
                    tipoOperacion = operaciones.ACTUALIZAR;
                    cmbCodigoTipoEmpleado.setDisable(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento;)");
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
        String numEmpleado = txtNumeroEmpleado.getText();
        String apeEmple = txtApellidosEmpleado.getText();
        String nomEmple = txtNombresEmpleado.getText();
        String direcc = txtDireccionEmpleado.getText();
        String telContac = txtTelefonoContacto.getText();
        numEmpleado = numEmpleado.replaceAll(" ", "");
        apeEmple = apeEmple.replaceAll(" ", "");
        nomEmple = nomEmple.replaceAll(" ", "");
        direcc = direcc.replaceAll(" ", "");
        telContac = telContac.replaceAll(" ", "");
        if (numEmpleado.length() == 0) {
            JOptionPane.showMessageDialog(null, "Debe colocar el número de empleado");
        } else {
            if (apeEmple.length() == 0) {
                JOptionPane.showMessageDialog(null, "Debe colocar los apellidos del empleado");
            } else {
                if (nomEmple.length() == 0) {
                    JOptionPane.showMessageDialog(null, "Debe colocar los nombres del empleado");
                } else {
                    if (direcc.length() == 0) {
                        JOptionPane.showMessageDialog(null, "Debe colocar la dirección del empleado");
                    } else {
                        if (telContac.length() == 0) {
                            JOptionPane.showMessageDialog(null, "Debe colocar el teléfono del empleado");
                        } else {
                            try {
                                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_EditarEmpleado(?,?,?,?,?,?,?)");
                                Empleado registro = (Empleado) tblEmpleados.getSelectionModel().getSelectedItem();
                                registro.setNumeroEmpleado(Integer.parseInt(txtNumeroEmpleado.getText()));
                                registro.setNombresEmpleado(txtNombresEmpleado.getText());
                                registro.setApellidosEmpleado(txtApellidosEmpleado.getText());
                                registro.setDireccionEmpleado(txtDireccionEmpleado.getText());
                                registro.setTelefonoContacto(txtTelefonoContacto.getText());
                                registro.setGradoCocinero(txtGradoCocinero.getText());
                                procedimiento.setInt(1, registro.getCodigoEmpleado());
                                procedimiento.setInt(2, registro.getNumeroEmpleado());
                                procedimiento.setString(3, registro.getApellidosEmpleado());
                                procedimiento.setString(4, registro.getNombresEmpleado());
                                procedimiento.setString(5, registro.getDireccionEmpleado());
                                procedimiento.setString(6, registro.getTelefonoContacto());
                                procedimiento.setString(7, registro.getGradoCocinero());
                                procedimiento.execute();
                            } catch (java.lang.NumberFormatException e) {
                                Toolkit.getDefaultToolkit().beep();
                                JOptionPane.showMessageDialog(null, "El valor del número de empleado solo puede ser de tipo númerico", "Aviso", JOptionPane.WARNING_MESSAGE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    public void reporte() {
        switch (tipoOperacion) {
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
                tblEmpleados.getSelectionModel().clearSelection();
                break;
        }
    }

    public void deseleccionar() {
        tblEmpleados.getSelectionModel().clearSelection();
        limpiarControles();
    }

    public void desactivarControles() {
        txtCodigoEmpleado.setEditable(false);
        txtNumeroEmpleado.setEditable(false);
        txtApellidosEmpleado.setEditable(false);
        txtNombresEmpleado.setEditable(false);
        txtDireccionEmpleado.setEditable(false);
        txtTelefonoContacto.setEditable(false);
        txtGradoCocinero.setEditable(false);
        cmbCodigoTipoEmpleado.setDisable(true);
    }

    public void activarControles() {
        txtCodigoEmpleado.setEditable(false);
        txtNumeroEmpleado.setEditable(true);
        txtApellidosEmpleado.setEditable(true);
        txtNombresEmpleado.setEditable(true);
        txtDireccionEmpleado.setEditable(true);
        txtTelefonoContacto.setEditable(true);
        txtGradoCocinero.setEditable(true);
        cmbCodigoTipoEmpleado.setDisable(false);
    }

    public void limpiarControles() {
        txtCodigoEmpleado.clear();
        txtNumeroEmpleado.clear();
        txtApellidosEmpleado.clear();
        txtNombresEmpleado.clear();
        txtDireccionEmpleado.clear();
        txtTelefonoContacto.clear();
        txtGradoCocinero.clear();
        cmbCodigoTipoEmpleado.setValue(null);
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

    public void ventanaTipoEmpleado() {
        escenarioPrincipal.ventanaTipoEmpleado();
    }
}
