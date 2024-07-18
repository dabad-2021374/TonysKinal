package org.denisabad.controller;

import eu.schudt.javafx.controls.calendar.DatePicker;
import java.awt.Toolkit;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import org.denisabad.bean.Servicio;
import org.denisabad.db.Conexion;
import org.denisabad.main.Principal;
import org.denisabad.report.GenerarReporte;

public class ServicioController implements Initializable {

    private Principal escenarioPrincipal;

    private enum operaciones {
        GUARDAR, ACTUALIZAR, ELIMINAR, NINGUNO
    };
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Servicio> listaServicio;
    private ObservableList<Empresa> listaEmpresa;
    private DatePicker fecha;
    @FXML
    private TextField txtCodigoServicio;
    @FXML
    private TextField txtTipoServicio;
    @FXML
    private TextField txtHoras;
    @FXML
    private TextField txtMinutos;
    @FXML
    private TextField txtSegundos;
    @FXML
    private TextField txtLugarServicio;
    @FXML
    private TextField txtTelefonoContacto;
    @FXML
    private ComboBox cmbCodigoEmpresa;
    @FXML
    private GridPane grpFecha;
    @FXML
    private TableView tblServicios;
    @FXML
    private TableColumn colCodigoServicio;
    @FXML
    private TableColumn colFechaServicio;
    @FXML
    private TableColumn colTipoServicio;
    @FXML
    private TableColumn colHoraServicio;
    @FXML
    private TableColumn colLugarServicio;
    @FXML
    private TableColumn colTelefonoContacto;
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
        desactivarControles();
        fecha = new DatePicker(Locale.ENGLISH);
        fecha.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fecha.getCalendarView().todayButtonTextProperty().set("Today");
        fecha.getCalendarView().setShowWeeks(false);
        fecha.setDisable(true);
        grpFecha.add(fecha, 1, 1);
        cmbCodigoEmpresa.setItems(getEmpresa());
    }

    public void cargarDatos() {
        tblServicios.setItems(getServicio());
        colCodigoServicio.setCellValueFactory(new PropertyValueFactory<Servicio, Integer>("codigoServicio"));
        colFechaServicio.setCellValueFactory(new PropertyValueFactory<Servicio, Date>("fechaServicio"));
        colTipoServicio.setCellValueFactory(new PropertyValueFactory<Servicio, String>("tipoServicio"));
        colHoraServicio.setCellValueFactory(new PropertyValueFactory<Servicio, Time>("horaServicio"));
        colLugarServicio.setCellValueFactory(new PropertyValueFactory<Servicio, String>("lugarServicio"));
        colTelefonoContacto.setCellValueFactory(new PropertyValueFactory<Servicio, String>("telefonoContacto"));
        colCodigoEmpresa.setCellValueFactory(new PropertyValueFactory<Servicio, Integer>("codigoEmpresa"));
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
        if (tblServicios.getSelectionModel().getSelectedItem() != null) {
            txtCodigoServicio.setText(String.valueOf(((Servicio) tblServicios.getSelectionModel().getSelectedItem()).getCodigoServicio()));
            fecha.selectedDateProperty().set(((Servicio) tblServicios.getSelectionModel().getSelectedItem()).getFechaServicio());
            String cadena = String.valueOf(((Servicio) tblServicios.getSelectionModel().getSelectedItem()).getHoraServicio());
            String[] partes = cadena.split(":");
            txtHoras.setText(partes[0]);
            txtMinutos.setText(partes[1]);
            txtSegundos.setText(partes[2]);
            txtTipoServicio.setText(String.valueOf(((Servicio) tblServicios.getSelectionModel().getSelectedItem()).getTipoServicio()));
            txtLugarServicio.setText(String.valueOf(((Servicio) tblServicios.getSelectionModel().getSelectedItem()).getLugarServicio()));
            txtTelefonoContacto.setText(String.valueOf(((Servicio) tblServicios.getSelectionModel().getSelectedItem()).getTelefonoContacto()));
            cmbCodigoEmpresa.getSelectionModel().select(buscarEmpresa(((Servicio) tblServicios.getSelectionModel().getSelectedItem()).getCodigoEmpresa()));
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
//                fecha.setDisable(true);
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
        String tipoServi = txtTipoServicio.getText();
        String lugar = txtLugarServicio.getText();
        String tel = txtTelefonoContacto.getText();
        String hora = txtHoras.getText();
        String minu = txtMinutos.getText();
        String segun = txtSegundos.getText();
        lugar = lugar.replaceAll(" ", "");
        tipoServi = tipoServi.replaceAll(" ", "");
        tel = tel.replaceAll(" ", "");
        hora = hora.replaceAll(" ", "");
        minu = minu.replaceAll(" ", "");
        segun = segun.replaceAll(" ", "");
        if (tipoServi.length() == 0) {
            JOptionPane.showMessageDialog(null, "Debe colocar el tipo de servicio");
        } else {
            if (lugar.length() == 0) {
                JOptionPane.showMessageDialog(null, "Debe colocar el lugar del servicio");
            } else {
                if (tel.length() == 0) {
                    JOptionPane.showMessageDialog(null, "Debe colocar el telefono del contacto");
                } else {
                    if (cmbCodigoEmpresa.getSelectionModel().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Debe colocar el código de la empresa");
                    } else {
                        if (hora.length() == 0) {
                            JOptionPane.showMessageDialog(null, "Debe colocar la hora");
                        } else {
                            if (minu.length() == 0) {
                                JOptionPane.showMessageDialog(null, "Debe colocar la hora");
                            } else {
                                if (segun.length() == 0) {
                                    JOptionPane.showMessageDialog(null, "Debe colocar la hora");
                                } else {
                                    if (Integer.parseInt(txtHoras.getText()) > 24) {
                                        JOptionPane.showMessageDialog(null, "El formato de la hora es 24 horas");
                                    } else {
                                        if (Integer.parseInt(txtMinutos.getText()) > 60) {
                                            JOptionPane.showMessageDialog(null, "El formato de los minutos es 60 minutos");
                                        } else {
                                            if (Integer.parseInt(txtSegundos.getText()) > 60) {
                                                JOptionPane.showMessageDialog(null, "El formato de los segundos es 60 segundos");
                                            } else {
                                                try {
                                                    Servicio registro = new Servicio();
                                                    registro.setFechaServicio(fecha.getSelectedDate());
                                                    registro.setTipoServicio(txtTipoServicio.getText());
                                                    String horaServicio = txtHoras.getText() + ":" + txtMinutos.getText() + ":" + txtSegundos.getText();
                                                    registro.setHoraServicio(horaServicio);
                                                    registro.setLugarServicio(txtLugarServicio.getText());
                                                    registro.setTelefonoContacto(txtTelefonoContacto.getText());
                                                    registro.setCodigoEmpresa(((Empresa) cmbCodigoEmpresa.getSelectionModel().getSelectedItem()).getCodigoEmpresa());
                                                    PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_AgregarServicio(?,?,?,?,?,?)");
                                                    procedimiento.setDate(1, new java.sql.Date(registro.getFechaServicio().getTime()));
                                                    procedimiento.setString(2, registro.getTipoServicio());
                                                    procedimiento.setTime(3, Time.valueOf(registro.getHoraServicio()));
                                                    procedimiento.setString(4, registro.getLugarServicio());
                                                    procedimiento.setString(5, registro.getTelefonoContacto());
                                                    procedimiento.setInt(6, registro.getCodigoEmpresa());
                                                    procedimiento.execute();
                                                    listaServicio.add(registro);
                                                } catch (NullPointerException e) {
                                                    Toolkit.getDefaultToolkit().beep();
                                                    JOptionPane.showMessageDialog(null, "Debe rellenar la fecha", "Aviso", JOptionPane.WARNING_MESSAGE);
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
                if (tblServicios.getSelectionModel().getSelectedItem() != null) {
                    int respuesta = JOptionPane.showConfirmDialog(null, "Está seguro de eliminar el registro?", "Eliminar Servicio", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (respuesta == JOptionPane.YES_OPTION) {
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_EliminarServicio(?)");
                            procedimiento.setInt(1, ((Servicio) tblServicios.getSelectionModel().getSelectedItem()).getCodigoServicio());
                            procedimiento.execute();
                            listaServicio.remove(tblServicios.getSelectionModel().getSelectedIndex());
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
                if (tblServicios.getSelectionModel().getSelectedItem() != null) {
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
                fecha.setDisable(true);
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
        String tipoServi = txtTipoServicio.getText();
        String lugar = txtLugarServicio.getText();
        String tel = txtTelefonoContacto.getText();
        String hora = txtHoras.getText();
        String minu = txtMinutos.getText();
        String segun = txtSegundos.getText();
        lugar = lugar.replaceAll(" ", "");
        tipoServi = tipoServi.replaceAll(" ", "");
        hora = hora.replaceAll(" ", "");
        minu = minu.replaceAll(" ", "");
        segun = segun.replaceAll(" ", "");
        if (tipoServi.length() == 0) {
            JOptionPane.showMessageDialog(null, "Debe colocar el tipo de servicio");
        } else {
            if (lugar.length() == 0) {
                JOptionPane.showMessageDialog(null, "Debe colocar el lugar del servicio");
            } else {
                if (tel.length() == 0) {
                    JOptionPane.showMessageDialog(null, "Debe colocar el telefono del contacto");
                } else {
                    if (hora.length() == 0) {
                        JOptionPane.showMessageDialog(null, "Debe colocar la hora");
                    } else {
                        if (minu.length() == 0) {
                            JOptionPane.showMessageDialog(null, "Debe colocar la hora");
                        } else {
                            if (segun.length() == 0) {
                                JOptionPane.showMessageDialog(null, "Debe colocar la hora");
                            } else {
                                if (Integer.parseInt(txtHoras.getText()) > 24) {
                                    JOptionPane.showMessageDialog(null, "El formato de la hora es 24 horas");
                                } else {
                                    if (Integer.parseInt(txtMinutos.getText()) > 60) {
                                        JOptionPane.showMessageDialog(null, "El formato de los minutos es 60 minutos");
                                    } else {
                                        if (Integer.parseInt(txtSegundos.getText()) > 60) {
                                            JOptionPane.showMessageDialog(null, "El formato de los segundos es 60 segundos");
                                        } else {
                                            try {
                                                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_EditarServicio(?,?,?,?,?,?)");
                                                Servicio registro = (Servicio) tblServicios.getSelectionModel().getSelectedItem();
                                                registro.setFechaServicio(fecha.getSelectedDate());
                                                registro.setTipoServicio(txtTipoServicio.getText());
                                                String horaServicio = txtHoras.getText() + ":" + txtMinutos.getText() + ":" + txtSegundos.getText();
                                                registro.setHoraServicio(horaServicio);
                                                registro.setLugarServicio(txtLugarServicio.getText());
                                                registro.setTelefonoContacto(txtTelefonoContacto.getText());
                                                registro.setCodigoEmpresa(((Empresa) cmbCodigoEmpresa.getSelectionModel().getSelectedItem()).getCodigoEmpresa());
                                                procedimiento.setInt(1, registro.getCodigoServicio());
                                                procedimiento.setDate(2, new java.sql.Date(registro.getFechaServicio().getTime()));
                                                procedimiento.setString(3, registro.getTipoServicio());
                                                procedimiento.setTime(4, Time.valueOf(registro.getHoraServicio()));
                                                procedimiento.setString(5, registro.getLugarServicio());
                                                procedimiento.setString(6, registro.getTelefonoContacto());
                                                procedimiento.execute();
                                            } catch (NullPointerException e) {
                                                    Toolkit.getDefaultToolkit().beep();
                                                    JOptionPane.showMessageDialog(null, "Debe rellenar la fecha", "Aviso", JOptionPane.WARNING_MESSAGE);
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
            }
        }
    }

    public void reporte() {
        switch (tipoDeOperacion) {
            case NINGUNO:
                if (tblServicios.getSelectionModel().getSelectedItem() != null) {
                    imprimirReporte();
                    deseleccionar();
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
                tblServicios.getSelectionModel().clearSelection();
                break;
        }
    }

    public void imprimirReporte() {
        Map parametros = new HashMap();
        int codEmpresa = Integer.valueOf(((Empresa) cmbCodigoEmpresa.getSelectionModel().getSelectedItem()).getCodigoEmpresa());
        parametros.put("imagen", GenerarReporte.class.getResource("/org/denisabad/image/LogoDos.png"));
        parametros.put("codEmpresa", codEmpresa);
        GenerarReporte.mostrarReporte("ReporteFinal.jasper", "Reporte Final", parametros);
    }

    public void deseleccionar() {
        tblServicios.getSelectionModel().clearSelection();
        limpiarControles();
    }

    public void desactivarControles() {
        txtCodigoServicio.setEditable(false);
        txtTipoServicio.setEditable(false);
//        fecha.setVisible(true);
        txtHoras.setEditable(false);
        txtMinutos.setEditable(false);
        txtSegundos.setEditable(false);
        txtLugarServicio.setEditable(false);
        txtTelefonoContacto.setEditable(false);
        cmbCodigoEmpresa.setDisable(true);
    }

    public void activarControles() {
        txtCodigoServicio.setEditable(false);
        fecha.setDisable(false);
        txtTipoServicio.setEditable(true);
        txtHoras.setEditable(true);
        txtMinutos.setEditable(true);
        txtSegundos.setEditable(true);
        txtLugarServicio.setEditable(true);
        txtTelefonoContacto.setEditable(true);
        cmbCodigoEmpresa.setDisable(false);
    }

    public void limpiarControles() {
        txtCodigoServicio.clear();
        fecha.setSelectedDate(null);
        txtTipoServicio.clear();
        txtHoras.clear();
        txtMinutos.clear();
        txtSegundos.clear();
        txtLugarServicio.clear();
        txtTelefonoContacto.clear();
        cmbCodigoEmpresa.setValue(null);
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }

    public void ventanaEmpresa() {
        escenarioPrincipal.ventanaEmpresa();
    }

}
