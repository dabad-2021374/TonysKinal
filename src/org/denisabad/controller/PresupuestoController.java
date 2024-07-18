package org.denisabad.controller;

import com.mysql.jdbc.MysqlDataTruncation;
import eu.schudt.javafx.controls.calendar.DatePicker;
import java.awt.Toolkit;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
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
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import org.denisabad.bean.Empresa;
import org.denisabad.bean.Presupuesto;
import org.denisabad.db.Conexion;
import org.denisabad.main.Principal;
import org.denisabad.report.GenerarReporte;

public class PresupuestoController implements Initializable {

    private Principal escenarioPrincipal;

    private enum operaciones {
        GUARDAR, ELIMINAR, ACTUALIZAR, NINGUNO
    };
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Presupuesto> listaPresupuesto;
    private ObservableList<Empresa> listaEmpresa;
    private DatePicker fecha;
    @FXML
    private TextField txtCodigoPresupuesto;
    @FXML
    private TextField txtCantidadPresupuesto;
    @FXML
    private GridPane grpFecha;
    @FXML
    private ComboBox cmbCodigoEmpresa;
    @FXML
    private TableView tblPresupuestos;
    @FXML
    private TableColumn colCodigoPresupuesto;
    @FXML
    private TableColumn colFechaSolicitud;
    @FXML
    private TableColumn colCantidadPresupuesto;
    @FXML
    private TableColumn colCodigoEmpresa;
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
        fecha = new DatePicker(Locale.ENGLISH);
        fecha.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fecha.getCalendarView().todayButtonTextProperty().set("Today");
        fecha.getCalendarView().setShowWeeks(false);
        //fecha.getStylesheets().add("/org/denisabad/resource/TonysKinal.css");
        grpFecha.add(fecha, 3, 0);
        cmbCodigoEmpresa.setItems(getEmpresa());
        desactivarControles();
    }

    public void cargarDatos() {
        tblPresupuestos.setItems(getPresupuesto());
        colCodigoPresupuesto.setCellValueFactory(new PropertyValueFactory<Presupuesto, Integer>("codigoPresupuesto"));
        colFechaSolicitud.setCellValueFactory(new PropertyValueFactory<Presupuesto, Date>("fechaSolicitud"));
        colCantidadPresupuesto.setCellValueFactory(new PropertyValueFactory<Presupuesto, Double>("cantidadPresupuesto"));
        colCodigoEmpresa.setCellValueFactory(new PropertyValueFactory<Presupuesto, Integer>("codigoEmpresa"));
    }

    public void seleccionarElemento() {
        if (tblPresupuestos.getSelectionModel().getSelectedItem() != null) {
            txtCodigoPresupuesto.setText(String.valueOf(((Presupuesto) tblPresupuestos.getSelectionModel().getSelectedItem()).getCodigoPresupuesto()));
            fecha.selectedDateProperty().set(((Presupuesto) tblPresupuestos.getSelectionModel().getSelectedItem()).getFechaSolicitud());
            txtCantidadPresupuesto.setText(String.valueOf(((Presupuesto) tblPresupuestos.getSelectionModel().getSelectedItem()).getCantidadPresupuesto()));
            cmbCodigoEmpresa.getSelectionModel().select(buscarEmpresa(((Presupuesto) tblPresupuestos.getSelectionModel().getSelectedItem()).getCodigoEmpresa()));
        } else {
            JOptionPane.showMessageDialog(null, "Selecciona un campo que tenga datos.");
        }
    }

    public Empresa buscarEmpresa(int codigoEmpresa) {
        Empresa resultado = null;
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_BuscarEmpresa(?)");
            procedimiento.setInt(1, codigoEmpresa);
            ResultSet registro = procedimiento.executeQuery();
            while (registro.next()) {
                resultado = new Empresa(registro.getInt("codigoEmpresa"),
                        registro.getString("nombreEmpresa"),
                        registro.getString("direccion"),
                        registro.getString("telefono"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultado;
    }

    public ObservableList<Presupuesto> getPresupuesto() {
        ArrayList<Presupuesto> lista = new ArrayList<Presupuesto>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_ListarPresupuestos()");
            ResultSet resultado = procedimiento.executeQuery();
            while (resultado.next()) {
                lista.add(new Presupuesto(resultado.getInt("codigoPresupuesto"),
                        resultado.getDate("fechaSolicitud"),
                        resultado.getDouble("cantidadPresupuesto"),
                        resultado.getInt("codigoEmpresa")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaPresupuesto = FXCollections.observableArrayList(lista);
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
        String presupuesto = txtCantidadPresupuesto.getText();
        presupuesto = presupuesto.replaceAll(" ", "");
        if (presupuesto.length() == 0 || cmbCodigoEmpresa.getSelectionModel().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Todos los campos deben ser llenados");
        } else {
            try {
                Presupuesto registro = new Presupuesto();
                registro.setFechaSolicitud(fecha.getSelectedDate());
                registro.setCantidadPresupuesto(Double.parseDouble(txtCantidadPresupuesto.getText()));
                registro.setCodigoEmpresa(((Empresa) cmbCodigoEmpresa.getSelectionModel().getSelectedItem()).getCodigoEmpresa());
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_AgregarPresupuesto(?, ?, ?)");
                procedimiento.setDate(1, new java.sql.Date(registro.getFechaSolicitud().getTime()));
                procedimiento.setDouble(2, registro.getCantidadPresupuesto());
                procedimiento.setInt(3, registro.getCodigoEmpresa());
                procedimiento.execute();
                listaPresupuesto.add(registro);
            } catch (MysqlDataTruncation e) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "Verificar el número de caracteres", "Aviso", JOptionPane.WARNING_MESSAGE);
            } catch (NullPointerException e) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "Debe rellenar la fecha", "Aviso", JOptionPane.WARNING_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
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
                if (tblPresupuestos.getSelectionModel().getSelectedItem() != null) {
                    int respuesta = JOptionPane.showConfirmDialog(null, "Está seguro de eliminar el registro?", "Eliminar Presupuesto", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (respuesta == JOptionPane.YES_OPTION) {
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_EliminarPresupuesto(?)");
                            procedimiento.setInt(1, ((Presupuesto) tblPresupuestos.getSelectionModel().getSelectedItem()).getCodigoPresupuesto());
                            procedimiento.execute();
                            listaPresupuesto.remove(tblPresupuestos.getSelectionModel().getSelectedIndex());
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
                if (tblPresupuestos.getSelectionModel().getSelectedItem() != null) {
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    imgEditar.setImage(new Image("/org/denisabad/image/actualizar.png"));
                    imgReporte.setImage(new Image("/org/denisabad/image/cancelar.png"));
                    activarControles();
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                    cmbCodigoEmpresa.setDisable(true);
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
        String presupuesto = txtCantidadPresupuesto.getText();
        presupuesto = presupuesto.replaceAll("", " ");
        if (presupuesto.length() == 0) {
            JOptionPane.showMessageDialog(null, "Todos los campos deben ser llenados");
        } else {
            try {
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_EditarPresupuesto(?,?,?,?)");
                Presupuesto registro = (Presupuesto) tblPresupuestos.getSelectionModel().getSelectedItem();
                registro.setFechaSolicitud(fecha.getSelectedDate());
                registro.setCantidadPresupuesto(Double.parseDouble(txtCantidadPresupuesto.getText()));
                registro.setCodigoEmpresa(((Empresa) cmbCodigoEmpresa.getSelectionModel().getSelectedItem()).getCodigoEmpresa());
                procedimiento.setInt(1, registro.getCodigoPresupuesto());
                procedimiento.setDate(2, new java.sql.Date(registro.getFechaSolicitud().getTime()));
                procedimiento.setDouble(3, registro.getCantidadPresupuesto());
                procedimiento.setInt(4, registro.getCodigoEmpresa());
                procedimiento.execute();
            } catch (MysqlDataTruncation e) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "verificar el número de caracteres", "Aviso", JOptionPane.WARNING_MESSAGE);
            } catch (java.lang.NumberFormatException e) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "El valor del presupuesto solo puede ser de tipo númerico", "Aviso", JOptionPane.WARNING_MESSAGE);
            } catch (NullPointerException e) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "Debe rellenar la fecha", "Aviso", JOptionPane.WARNING_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void reporte() {
        switch (tipoDeOperacion) {
            case NINGUNO:
                if (tblPresupuestos.getSelectionModel().getSelectedItem() != null) {
                    imprimirReporte();
                } else {
                    JOptionPane.showMessageDialog(null, "Debe seleccionar la empresa de la cual quiere el reporte");
                }
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
                tipoDeOperacion = operaciones.NINGUNO;
                tblPresupuestos.getSelectionModel().clearSelection();
                break;
        }
    }

    public void imprimirReporte() {
        Map parametros = new HashMap();
        int codEmpresa = Integer.valueOf(((Empresa) cmbCodigoEmpresa.getSelectionModel().getSelectedItem()).getCodigoEmpresa());
        parametros.put("codEmpresa", codEmpresa);
        parametros.put("Imagen", GenerarReporte.class.getResource("/org/denisabad/image/LogoDos.png"));
//        parametros.put("SUBREPORT_DIR", GenerarReporte.class.getResource("/org/denisabad/report/SubReportePresupuesto.jasper"));
        GenerarReporte.mostrarReporte("ReportePresupuesto.jasper", "Reporte de Presupuesto", parametros);
    }

    public void deseleccionar() {
        tblPresupuestos.getSelectionModel().clearSelection();
        limpiarControles();
    }

    public void desactivarControles() {
        txtCodigoPresupuesto.setEditable(false);
        txtCantidadPresupuesto.setEditable(false);
        fecha.setDisable(true);
        cmbCodigoEmpresa.setDisable(true);
    }

    public void activarControles() {
        txtCodigoPresupuesto.setEditable(false);
        txtCantidadPresupuesto.setEditable(true);
        fecha.setDisable(false);
        cmbCodigoEmpresa.setDisable(false);
    }

    public void limpiarControles() {
        txtCodigoPresupuesto.clear();
        txtCantidadPresupuesto.clear();
        fecha.setSelectedDate(null);
        cmbCodigoEmpresa.setValue(null);
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

    public void ventanaEmpresa() {
        escenarioPrincipal.ventanaEmpresa();
    }
}
