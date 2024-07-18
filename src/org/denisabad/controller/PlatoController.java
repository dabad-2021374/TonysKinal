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
import org.denisabad.bean.Plato;
import org.denisabad.bean.TipoPlato;
import org.denisabad.db.Conexion;
import org.denisabad.main.Principal;

public class PlatoController implements Initializable {

    private Principal escenarioPrincipal;

    private enum operaciones {
        GUARDAR, ELIMINAR, ACTUALIZAR, NINGUNO
    }

    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Plato> listaPlato;
    private ObservableList<TipoPlato> listaTipoPlato;

    @FXML
    private TextField txtCodigoPlato;
    @FXML
    private TextField txtCantidadPlato;
    @FXML
    private TextField txtNombrePlato;
    @FXML
    private TextField txtDescripcionPlato;
    @FXML
    private TextField txtPrecioPlato;
    @FXML
    private ComboBox cmbCodigoTipoPlato;
    @FXML
    private TableView tblPlatos;
    @FXML
    private TableColumn colCodigoPlato;
    @FXML
    private TableColumn colCantidadPlato;
    @FXML
    private TableColumn colNombrePlato;
    @FXML
    private TableColumn colDescripcionPlato;
    @FXML
    private TableColumn colPrecioPlato;
    @FXML
    private TableColumn colCodigoTipoPlato;
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
        desactivarControles();
        cmbCodigoTipoPlato.setItems(getTipoPlato());
    }

    public void cargarDatos() {
        try {
            tblPlatos.setItems(getPlato());
            colCodigoPlato.setCellValueFactory(new PropertyValueFactory<Plato, Integer>("codigoPlato"));
            colCantidadPlato.setCellValueFactory(new PropertyValueFactory<Plato, Integer>("cantidad"));
            colNombrePlato.setCellValueFactory(new PropertyValueFactory<Plato, String>("nombrePlato"));
            colDescripcionPlato.setCellValueFactory(new PropertyValueFactory<Plato, String>("descripcion"));
            colPrecioPlato.setCellValueFactory(new PropertyValueFactory<Plato, Double>("precioPlato"));
            colCodigoTipoPlato.setCellValueFactory(new PropertyValueFactory<Plato, Integer>("codigoTipoPlato"));
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public ObservableList<TipoPlato> getTipoPlato() {
        ArrayList<TipoPlato> lista = new ArrayList<TipoPlato>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_ListarTipoPlatos()");
            ResultSet resultado = procedimiento.executeQuery();
            while (resultado.next()) {
                lista.add(new TipoPlato(resultado.getInt("codigoTipoPlato"),
                        resultado.getString("descripcionTipo")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaTipoPlato = FXCollections.observableArrayList(lista);
    }

    public void seleccionarElemento() {
        if (tblPlatos.getSelectionModel().getSelectedItem() != null) {
            txtCodigoPlato.setText(String.valueOf(((Plato) tblPlatos.getSelectionModel().getSelectedItem()).getCodigoPlato()));
            txtCantidadPlato.setText(String.valueOf(((Plato) tblPlatos.getSelectionModel().getSelectedItem()).getCantidad()));
            txtNombrePlato.setText(((Plato) tblPlatos.getSelectionModel().getSelectedItem()).getNombrePlato());
            txtDescripcionPlato.setText(((Plato) tblPlatos.getSelectionModel().getSelectedItem()).getDescripcion());
            txtPrecioPlato.setText(String.valueOf(((Plato) tblPlatos.getSelectionModel().getSelectedItem()).getPrecioPlato()));
            cmbCodigoTipoPlato.getSelectionModel().select(buscarTipoPlato(((Plato) tblPlatos.getSelectionModel().getSelectedItem()).getCodigoTipoPlato()));
        } else {
            JOptionPane.showMessageDialog(null, "Selecciona un campo que tenga datos.");
        }
    }

    public TipoPlato buscarTipoPlato(int codigoTipoPlato) {
        TipoPlato resultado = null;
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_BuscarTipoPlato(?)");
            procedimiento.setInt(1, codigoTipoPlato);
            ResultSet registro = procedimiento.executeQuery();
            while (registro.next()) {
                resultado = new TipoPlato(registro.getInt("codigoTipoPlato"),
                        registro.getString("descripcionTipo"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultado;
    }

    public void nuevo() {
        switch (tipoDeOperacion) {
            case NINGUNO:
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                imgNuevo.setImage(new Image("/org/denisabad/image/guardar.png"));
                imgEliminar.setImage(new Image("/org/denisabad/image/cancelar.png"));
                tipoDeOperacion = operaciones.GUARDAR;
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
                tipoDeOperacion = operaciones.NINGUNO;
                cargarDatos();
                break;
        }
    }

    public void guardar() {
        String cantiPlato = txtCantidadPlato.getText();
        String nomPlato = txtNombrePlato.getText();
        String descri = txtDescripcionPlato.getText();
        String precio = txtPrecioPlato.getText();
        cantiPlato = cantiPlato.replaceAll(" ", "");
        nomPlato = nomPlato.replaceAll(" ", "");
        descri = descri.replaceAll(" ", "");
        precio = precio.replaceAll(" ", "");
        if (cantiPlato.length() == 0) {
            JOptionPane.showMessageDialog(null, "Debe colocar la cantidad del plato");
        } else {
            if (nomPlato.length() == 0) {
                JOptionPane.showMessageDialog(null, "Debe colocar el nombre del plato");
            } else {
                if (descri.length() == 0) {
                    JOptionPane.showMessageDialog(null, "Debe colocar la descripcion del plato");
                } else {
                    if (precio.length() == 0) {
                        JOptionPane.showMessageDialog(null, "Debe colocar el precio del plato");
                    } else {
                        if (cmbCodigoTipoPlato.getSelectionModel().isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Debe seleccionar el código del tipo plato");
                        } else {
                            try {
                                Plato registro = new Plato();
                                registro.setCantidad(Integer.parseInt(txtCantidadPlato.getText()));
                                registro.setNombrePlato(txtNombrePlato.getText());
                                registro.setDescripcion(txtDescripcionPlato.getText());
                                registro.setPrecioPlato(Double.parseDouble(txtPrecioPlato.getText()));
                                registro.setCodigoTipoPlato(((TipoPlato) cmbCodigoTipoPlato.getSelectionModel().getSelectedItem()).getCodigoTipoPlato());
                                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_AgregarPlato(?,?,?,?,?)");
                                procedimiento.setInt(1, registro.getCantidad());
                                procedimiento.setString(2, registro.getNombrePlato());
                                procedimiento.setString(3, registro.getDescripcion());
                                procedimiento.setDouble(4, registro.getPrecioPlato());
                                procedimiento.setInt(5, registro.getCodigoTipoPlato());
                                procedimiento.execute();
                                listaPlato.add(registro);
                            } catch (java.lang.NumberFormatException e) {
                                Toolkit.getDefaultToolkit().beep();
                                JOptionPane.showMessageDialog(null, "El valor de la cantidad o del precio solo puede ser de tipo númerico", "Aviso", JOptionPane.WARNING_MESSAGE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    public void eliminar() {
        switch (tipoDeOperacion) {
            case GUARDAR:
                limpiarControles();
                desactivarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                imgNuevo.setImage(new Image("/org/denisabad/image/agregar.png"));
                imgEliminar.setImage(new Image("/org/denisabad/image/eliminar.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                break;
            default:
                if (tblPlatos.getSelectionModel().getSelectedItem() != null) {
                    int respuesta = JOptionPane.showConfirmDialog(null, "Está seguro de eliminar el registro?", "Eliminar el plato", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (respuesta == JOptionPane.YES_OPTION) {
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_EliminarPlato(?)");
                            procedimiento.setInt(1, ((Plato) tblPlatos.getSelectionModel().getSelectedItem()).getCodigoPlato());
                            procedimiento.execute();
                            listaPlato.remove(tblPlatos.getSelectionModel().getSelectedIndex());
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
        switch (tipoDeOperacion) {
            case NINGUNO:
                if (tblPlatos.getSelectionModel().getSelectedItem() != null) {
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    imgEditar.setImage(new Image("/org/denisabad/image/actualizar.png"));
                    imgReporte.setImage(new Image("/org/denisabad/image/cancelar.png"));
                    activarControles();
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                    cmbCodigoTipoPlato.setDisable(true);
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
                tipoDeOperacion = operaciones.NINGUNO;
                cargarDatos();
                break;
        }
    }

    public void actualizar() {
        String cantiPlato = txtCantidadPlato.getText();
        String nomPlato = txtNombrePlato.getText();
        String descri = txtDescripcionPlato.getText();
        String precio = txtPrecioPlato.getText();
        cantiPlato = cantiPlato.replaceAll(" ", "");
        nomPlato = nomPlato.replaceAll(" ", "");
        descri = descri.replaceAll(" ", "");
        precio = precio.replaceAll(" ", "");
        if (cantiPlato.length() == 0) {
            JOptionPane.showMessageDialog(null, "Debe colocar la cantidad del plato");
        } else {
            if (nomPlato.length() == 0) {
                JOptionPane.showMessageDialog(null, "Debe colocar el nombre del plato");
            } else {
                if (descri.length() == 0) {
                    JOptionPane.showMessageDialog(null, "Debe colocar la descripcion del plato");
                } else {
                    if (precio.length() == 0) {
                        JOptionPane.showMessageDialog(null, "Debe colocar el precio del plato");
                    } else {
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_EditarPlato(?,?,?,?,?)");
                            Plato registro = (Plato) tblPlatos.getSelectionModel().getSelectedItem();
                            registro.setCantidad(Integer.parseInt(txtCantidadPlato.getText()));
                            registro.setNombrePlato(txtNombrePlato.getText());
                            registro.setDescripcion(txtDescripcionPlato.getText());
                            registro.setPrecioPlato(Double.parseDouble(txtPrecioPlato.getText()));
                            registro.setCodigoTipoPlato(((TipoPlato) cmbCodigoTipoPlato.getSelectionModel().getSelectedItem()).getCodigoTipoPlato());
                            procedimiento.setInt(1, registro.getCodigoPlato());
                            procedimiento.setInt(2, registro.getCantidad());
                            procedimiento.setString(3, registro.getNombrePlato());
                            procedimiento.setString(4, registro.getDescripcion());
                            procedimiento.setDouble(5, registro.getPrecioPlato());
                            procedimiento.execute();
                        } catch (java.lang.NumberFormatException e) {
                            Toolkit.getDefaultToolkit().beep();
                            JOptionPane.showMessageDialog(null, "El valor de la cantidad o del precio solo puede ser de tipo númerico", "Aviso", JOptionPane.WARNING_MESSAGE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public void reporte() {
        switch (tipoDeOperacion) {
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
                tipoDeOperacion = operaciones.NINGUNO;
                tblPlatos.getSelectionModel().clearSelection();
                break;
        }
    }

    public void deseleccionar() {
        tblPlatos.getSelectionModel().clearSelection();
        limpiarControles();
    }

    public void desactivarControles() {
        txtCodigoPlato.setEditable(false);
        txtCantidadPlato.setEditable(false);
        txtNombrePlato.setEditable(false);
        txtDescripcionPlato.setEditable(false);
        txtPrecioPlato.setEditable(false);
        cmbCodigoTipoPlato.setDisable(true);
    }

    public void activarControles() {
        txtCodigoPlato.setEditable(false);
        txtCantidadPlato.setEditable(true);
        txtNombrePlato.setEditable(true);
        txtDescripcionPlato.setEditable(true);
        txtPrecioPlato.setEditable(true);
        cmbCodigoTipoPlato.setDisable(false);
    }

    public void limpiarControles() {
        txtCodigoPlato.clear();
        txtCantidadPlato.clear();
        txtNombrePlato.clear();
        txtDescripcionPlato.clear();
        txtPrecioPlato.clear();
        cmbCodigoTipoPlato.setValue(null);
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

    public void ventanaTipoPlato() {
        escenarioPrincipal.ventanaTipoPlato();
    }

}
