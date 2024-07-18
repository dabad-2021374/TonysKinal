package org.denisabad.controller;

import com.mysql.jdbc.MysqlDataTruncation;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.swing.JOptionPane;
import org.denisabad.bean.Producto;
import org.denisabad.db.Conexion;
import org.denisabad.main.Principal;

public class ProductoController implements Initializable {

    private enum operaciones {
        NUEVO, GUARDAR, ELIMINAR, ACTUALIZAR, CANCELAR, NINGUNO
    };
    private operaciones tipoOperacion = operaciones.NINGUNO;
    private Principal escenarioPrincipal;
    private ObservableList<Producto> listaProducto;

    @FXML
    private TextField txtCodigoProducto;
    @FXML
    private TextField txtNombreProducto;
    @FXML
    private TextField txtCantidadProducto;
    @FXML
    private TableView tblProductos;
    @FXML
    private TableColumn colCodigoProducto;
    @FXML
    private TableColumn colNombreProducto;
    @FXML
    private TableColumn colCantidadProducto;
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
        tblProductos.setItems(getProducto());
        colCodigoProducto.setCellValueFactory(new PropertyValueFactory<Producto, Integer>("codigoProducto"));
        colNombreProducto.setCellValueFactory(new PropertyValueFactory<Producto, String>("nombreProducto"));
        colCantidadProducto.setCellValueFactory(new PropertyValueFactory<Producto, Integer>("cantidad"));
    }

    public ObservableList<Producto> getProducto() {
        ArrayList<Producto> lista = new ArrayList<Producto>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_ListarProductos");
            ResultSet resultado = procedimiento.executeQuery();
            while (resultado.next()) {
                lista.add(new Producto(resultado.getInt("codigoProducto"),
                        resultado.getString("nombreProducto"),
                        resultado.getInt("cantidad")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaProducto = FXCollections.observableArrayList(lista);
    }

    public void seleccionarElemento() {
        if (tblProductos.getSelectionModel().getSelectedItem() != null) {
            txtCodigoProducto.setText(String.valueOf(((Producto) tblProductos.getSelectionModel().getSelectedItem()).getCodigoProducto()));
            txtNombreProducto.setText(((Producto) tblProductos.getSelectionModel().getSelectedItem()).getNombreProducto());
            txtCantidadProducto.setText(String.valueOf(((Producto) tblProductos.getSelectionModel().getSelectedItem()).getCantidad()));
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
                tipoOperacion = ProductoController.operaciones.GUARDAR;
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
                tipoOperacion = ProductoController.operaciones.NINGUNO;
                cargarDatos();
                break;
        }
    }

    public void guardar() {
        String nameProd = txtNombreProducto.getText();
        nameProd = nameProd.replaceAll(" ", "");
        String cantidad = txtCantidadProducto.getText();
        cantidad = cantidad.replaceAll(" ", "");
        if (nameProd.length() == 0) {
            JOptionPane.showMessageDialog(null, "Todos los campos deben ser llenados");
        } else {
            if (cantidad.length() == 0) {
                JOptionPane.showMessageDialog(null, "Todos los campos deben ser llenados");
            } else {
                Producto registro = new Producto();
                registro.setNombreProducto(txtNombreProducto.getText());
                registro.setCantidad(Integer.parseInt(txtCantidadProducto.getText()));
                try {
                    PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_AgregarProducto(?, ?)");
                    procedimiento.setString(1, registro.getNombreProducto());
                    procedimiento.setInt(2, registro.getCantidad());
                    procedimiento.executeQuery();
                    listaProducto.add(registro);
                } catch (java.lang.NumberFormatException e) {
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(null, "La cantidad solo puede ser de tipo númerico", "Aviso", JOptionPane.WARNING_MESSAGE);
                } catch (Exception e) {
                    e.printStackTrace();
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
                tipoOperacion = ProductoController.operaciones.NINGUNO;
                break;
            default:
                if (tblProductos.getSelectionModel().getSelectedItem() != null) {
                    int respuesta = JOptionPane.showConfirmDialog(null, "Está seguro de eliminar el registro?", "Eliminar Producto", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (respuesta == JOptionPane.YES_OPTION) {
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_EliminarProducto(?)");
                            procedimiento.setInt(1, ((Producto) tblProductos.getSelectionModel().getSelectedItem()).getCodigoProducto());
                            procedimiento.execute();
                            listaProducto.remove(tblProductos.getSelectionModel().getSelectedIndex());
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
                if (tblProductos.getSelectionModel().getSelectedItem() != null) {
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    imgEditar.setImage(new Image("/org/denisabad/image/actualizar.png"));
                    imgReporte.setImage(new Image("/org/denisabad/image/cancelar.png"));
                    activarControles();
                    tipoOperacion = ProductoController.operaciones.ACTUALIZAR;
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
                tipoOperacion = ProductoController.operaciones.NINGUNO;
                cargarDatos();
                break;
        }
    }

    public void actualizar() {
        String nameProd = txtNombreProducto.getText();
        nameProd = nameProd.replaceAll(" ", "");
        String cantidad = txtCantidadProducto.getText();
        cantidad = cantidad.replaceAll(" ", "");
        if (nameProd.length() == 0) {
            JOptionPane.showMessageDialog(null, "Todos los campos deben ser llenados");
        } else {
            if (cantidad.length() == 0) {
                JOptionPane.showMessageDialog(null, "Todos los campos deben ser llenados");
            } else {
                try {
                    PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_EditarProducto(?, ?, ?)");
                    Producto registro = (Producto) tblProductos.getSelectionModel().getSelectedItem();
                    registro.setNombreProducto(txtNombreProducto.getText());
                    registro.setCantidad(Integer.parseInt(txtCantidadProducto.getText()));
                    procedimiento.setInt(1, registro.getCodigoProducto());
                    procedimiento.setString(2, registro.getNombreProducto());
                    procedimiento.setInt(3, registro.getCantidad());
                    procedimiento.execute();
                } catch (java.lang.NumberFormatException e) {
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(null, "La cantidad solo puede ser de tipo númerico", "Aviso", JOptionPane.WARNING_MESSAGE);
                } catch (Exception e) {
                    e.printStackTrace();
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
                tipoOperacion = ProductoController.operaciones.NINGUNO;
                tblProductos.getSelectionModel().clearSelection();
                break;
        }
    }

    public void deseleccionar() {
        tblProductos.getSelectionModel().clearSelection();
        limpiarControles();
    }

    public void desactivarControles() {
        txtCodigoProducto.setEditable(false);
        txtNombreProducto.setEditable(false);
        txtCantidadProducto.setEditable(false);
    }

    public void activarControles() {
        txtCodigoProducto.setEditable(false);
        txtNombreProducto.setEditable(true);
        txtCantidadProducto.setEditable(true);
    }

    public void limpiarControles() {
        txtCodigoProducto.clear();
        txtNombreProducto.clear();
        txtCantidadProducto.clear();
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
