package org.denisabad.controller;

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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.swing.JOptionPane;
import org.denisabad.bean.TipoPlato;
import org.denisabad.db.Conexion;
import org.denisabad.main.Principal;

public class TipoPlatoController implements Initializable {

    private Principal escenarioPrincipal;
    private ObservableList<TipoPlato> listaTipoPlato;

    private enum operaciones {
        NUEVO, GUARDAR, ELIMINAR, ACTUALIZAR, CANCELAR, NINGUNO
    };
    private operaciones tipoOperacion = operaciones.NINGUNO;

    @FXML
    private TextField txtCodigoTipoPlato;
    @FXML
    private TextField txtDescripcionTipoPlato;
    @FXML
    private TableView tblTipoPlatos;
    @FXML
    private TableColumn colCodigoTipoPlato;
    @FXML
    private TableColumn colDescripcionTipoPlato;
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
        tblTipoPlatos.setItems(getTipoPlato());
        colCodigoTipoPlato.setCellValueFactory(new PropertyValueFactory<TipoPlato, Integer>("codigoTipoPlato"));
        colDescripcionTipoPlato.setCellValueFactory(new PropertyValueFactory<TipoPlato, String>("descripcionTipo"));
    }

    public ObservableList<TipoPlato> getTipoPlato() {
        ArrayList<TipoPlato> lista = new ArrayList<TipoPlato>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_ListarTipoPlatos");
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
        if (tblTipoPlatos.getSelectionModel().getSelectedItem() != null) {
            txtCodigoTipoPlato.setText(String.valueOf(((TipoPlato) tblTipoPlatos.getSelectionModel().getSelectedItem()).getCodigoTipoPlato()));
            txtDescripcionTipoPlato.setText(((TipoPlato) tblTipoPlatos.getSelectionModel().getSelectedItem()).getDescripcionTipo());
        } else {
            JOptionPane.showMessageDialog(null, "Selecciona un campo que tenga datos.");
        }
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
                tipoOperacion = TipoPlatoController.operaciones.GUARDAR;
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
                tipoOperacion = TipoPlatoController.operaciones.NINGUNO;
                cargarDatos();
                break;
        }
    }

    public void guardar() {
        String descrip = txtDescripcionTipoPlato.getText();
        descrip = descrip.replaceAll(" ", "");
        if (descrip.length() == 0) {
            JOptionPane.showMessageDialog(null, "Todos los campos deben ser llenados");
        } else {
            TipoPlato registro = new TipoPlato();
            registro.setDescripcionTipo(txtDescripcionTipoPlato.getText());
            try {
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_AgregarTipoPlato(?)");
                procedimiento.setString(1, registro.getDescripcionTipo());
                procedimiento.executeQuery();
                listaTipoPlato.add(registro);
            } catch (Exception e) {
                e.printStackTrace();
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
                tipoOperacion = TipoPlatoController.operaciones.NINGUNO;
                break;
            default:
                if (tblTipoPlatos.getSelectionModel().getSelectedItem() != null) {
                    int respuesta = JOptionPane.showConfirmDialog(null, "Está seguro de eliminar el registro?", "Eliminar Producto", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (respuesta == JOptionPane.YES_OPTION) {
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_EliminarTipoPlato(?)");
                            procedimiento.setInt(1, ((TipoPlato) tblTipoPlatos.getSelectionModel().getSelectedItem()).getCodigoTipoPlato());
                            procedimiento.execute();
                            listaTipoPlato.remove(tblTipoPlatos.getSelectionModel().getSelectedIndex());
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
                if (tblTipoPlatos.getSelectionModel().getSelectedItem() != null) {
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    imgEditar.setImage(new Image("/org/denisabad/image/actualizar.png"));
                    imgReporte.setImage(new Image("/org/denisabad/image/cancelar.png"));
                    activarControles();
                    tipoOperacion = TipoPlatoController.operaciones.ACTUALIZAR;
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
                tipoOperacion = TipoPlatoController.operaciones.NINGUNO;
                cargarDatos();
                break;
        }
    }

    public void actualizar() {
        String descrip = txtDescripcionTipoPlato.getText();
        descrip = descrip.replaceAll(" ", "");
        if (descrip.length() == 0) {
            JOptionPane.showMessageDialog(null, "Todos los campos deben ser llenados");
        } else {
            try {
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_EditarTipoPlato(?, ?)");
                TipoPlato registro = (TipoPlato) tblTipoPlatos.getSelectionModel().getSelectedItem();
                registro.setDescripcionTipo(txtDescripcionTipoPlato.getText());
                procedimiento.setInt(1, registro.getCodigoTipoPlato());
                procedimiento.setString(2, registro.getDescripcionTipo());
                procedimiento.execute();
            } catch (Exception e) {
                e.printStackTrace();
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
                tipoOperacion = TipoPlatoController.operaciones.NINGUNO;
                tblTipoPlatos.getSelectionModel().clearSelection();
                break;
        }
    }

    public void deseleccionar() {
        tblTipoPlatos.getSelectionModel().clearSelection();
        limpiarControles();
    }

    public void desactivarControles() {
        txtCodigoTipoPlato.setEditable(false);
        txtDescripcionTipoPlato.setEditable(false);
    }

    public void activarControles() {
        txtCodigoTipoPlato.setEditable(false);
        txtDescripcionTipoPlato.setEditable(true);
    }

    public void limpiarControles() {
        txtCodigoTipoPlato.clear();
        txtDescripcionTipoPlato.clear();
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
    
    public void ventanaPlato(){
        escenarioPrincipal.ventanaPlato();
    }

}
