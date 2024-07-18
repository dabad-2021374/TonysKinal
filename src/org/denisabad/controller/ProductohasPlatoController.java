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
import org.denisabad.bean.Producto;
import org.denisabad.bean.ProductoHasPlato;
import org.denisabad.db.Conexion;
import org.denisabad.main.Principal;

public class ProductohasPlatoController implements Initializable {

    private enum operaciones {
        NUEVO, GUARDAR, ELIMINAR, ACTUALIZAR, CANCELAR, NINGUNO
    };
    private operaciones tipoOperacion = operaciones.NINGUNO;

    private ObservableList<ProductoHasPlato> listaProductohasPlato;
    private ObservableList<Plato> listaPlato;
    private ObservableList<Producto> listaProducto;

    private Principal escenarioPrincipal;
    private @FXML
    TextField txtProductosCodigoProducto;
    private @FXML
    ComboBox cmbCodigoPlato;
    private @FXML
    ComboBox cmbCodigoProducto;
    private @FXML
    Button btnNuevo;
    private @FXML
    Button btnReporte;
    private @FXML
    Button btnEliminar;
    private @FXML
    Button btnEditar;
    private @FXML
    TableView tblProductoshasPlatos;
    private @FXML
    TableColumn colProductosCodigoProducto;
    private @FXML
    TableColumn colCodigoPlato;
    private @FXML
    TableColumn colCodigoProducto;
    private @FXML
    ImageView imgNuevo;
    private @FXML
    ImageView imgReporte;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        desactivarControles();
        cargarDatos();
        btnEliminar.setDisable(true);
        btnEditar.setDisable(true);
        cmbCodigoPlato.setItems(getPlato());
        cmbCodigoProducto.setItems(getProducto());
    }

    public void cargarDatos() {
        try {
            tblProductoshasPlatos.setItems(getProductoHasPlatos());
            colProductosCodigoProducto.setCellValueFactory(new PropertyValueFactory<ProductoHasPlato, Integer>("Productos_codigoProducto"));
            colCodigoPlato.setCellValueFactory(new PropertyValueFactory<ProductoHasPlato, Integer>("codigoPlato"));
            colCodigoProducto.setCellValueFactory(new PropertyValueFactory<ProductoHasPlato, Integer>("codigoProducto"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ObservableList<ProductoHasPlato> getProductoHasPlatos() {
        ArrayList<ProductoHasPlato> lista = new ArrayList<ProductoHasPlato>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_ListarProductos_has_Platos()");
            ResultSet resultado = procedimiento.executeQuery();
            while (resultado.next()) {
                lista.add(new ProductoHasPlato(resultado.getInt("Productos_codigoProducto"),
                        resultado.getInt("codigoPlato"),
                        resultado.getInt("codigoProducto")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaProductohasPlato = FXCollections.observableArrayList(lista);
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
        if (tblProductoshasPlatos.getSelectionModel().getSelectedItem() != null) {
            txtProductosCodigoProducto.setText(String.valueOf(((ProductoHasPlato) tblProductoshasPlatos.getSelectionModel().getSelectedItem()).getProductos_codigoProducto()));
            cmbCodigoPlato.getSelectionModel().select(buscarPlato(((ProductoHasPlato) tblProductoshasPlatos.getSelectionModel().getSelectedItem()).getCodigoPlato()));
            cmbCodigoProducto.getSelectionModel().select(buscarProducto(((ProductoHasPlato) tblProductoshasPlatos.getSelectionModel().getSelectedItem()).getCodigoProducto()));
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

    public Producto buscarProducto(int codigoProducto) {
        Producto resultado = null;
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_BuscarProducto(?)");
            procedimiento.setInt(1, codigoProducto);
            ResultSet registro = procedimiento.executeQuery();
            while (registro.next()) {
                resultado = new Producto(registro.getInt("codigoProducto"),
                        registro.getString("nombreProducto"),
                        registro.getInt("cantidad"));
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
        String codSerHasServi = txtProductosCodigoProducto.getText();
        codSerHasServi = codSerHasServi.replaceAll(" ", "");
        if (codSerHasServi.length() == 0) {
            JOptionPane.showMessageDialog(null, "Debe colocar el código ProductosCodigoProducto");
        } else {
            if (cmbCodigoPlato.getSelectionModel().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Debe seleccionar el código del plato");
            } else {
                if (cmbCodigoProducto.getSelectionModel().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Debe seleccionar el código del producto");
                } else {
                    try {
                        ProductoHasPlato registro = new ProductoHasPlato();
                        registro.setProductos_codigoProducto(Integer.parseInt(txtProductosCodigoProducto.getText()));
                        registro.setCodigoPlato(((Plato) cmbCodigoPlato.getSelectionModel().getSelectedItem()).getCodigoPlato());
                        registro.setCodigoProducto(((Producto) cmbCodigoProducto.getSelectionModel().getSelectedItem()).getCodigoProducto());
                        PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_AgregarProducto_has_Plato(?,?,?)");
                        procedimiento.setInt(1, registro.getProductos_codigoProducto());
                        procedimiento.setInt(2, registro.getCodigoPlato());
                        procedimiento.setInt(3, registro.getCodigoProducto());
                        procedimiento.execute();
                        listaProductohasPlato.add(registro);
                    } catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e) {
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(null, "El código ProductosCodigoProducto ya existe", "Aviso", JOptionPane.WARNING_MESSAGE);
                    } catch (java.lang.NumberFormatException e) {
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(null, "El valor del código ProductosCodigoProducto solo puede ser de tipo númerico", "Aviso", JOptionPane.WARNING_MESSAGE);
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
                tblProductoshasPlatos.getSelectionModel().clearSelection();
                break;
        }
    }

    public void deseleccionar() {
        tblProductoshasPlatos.getSelectionModel().clearSelection();
        limpiarControles();
    }

    public void desactivarControles() {
        txtProductosCodigoProducto.setEditable(false);
        cmbCodigoPlato.setDisable(true);
        cmbCodigoProducto.setDisable(true);
    }

    public void activarControles() {
        txtProductosCodigoProducto.setEditable(true);
        cmbCodigoPlato.setDisable(false);
        cmbCodigoProducto.setDisable(false);
    }

    public void limpiarControles() {
        txtProductosCodigoProducto.clear();
        cmbCodigoProducto.setValue(null);
        cmbCodigoPlato.setValue(null);
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
