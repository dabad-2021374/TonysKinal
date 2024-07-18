package org.denisabad.controller;

import eu.schudt.javafx.controls.calendar.DatePicker;
import java.awt.Toolkit;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
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
import org.denisabad.bean.Empleado;
import org.denisabad.bean.Servicio;
import org.denisabad.bean.ServicioHasEmpleado;
import org.denisabad.db.Conexion;
import org.denisabad.main.Principal;

public class ServiciohasEmpleadoController implements Initializable {

    private Principal escenarioPrincipal;

    private enum operaciones {
        NUEVO, GUARDAR, ELIMINAR, ACTUALIZAR, CANCELAR, NINGUNO
    };
    private operaciones tipoOperacion = operaciones.NINGUNO;

    private ObservableList<ServicioHasEmpleado> listaServiciohasEmpleado;
    private ObservableList<Servicio> listaServicio;
    private ObservableList<Empleado> listaEmpleado;
    private DatePicker fecha;

    @FXML
    private TextField txtServicioCodigoServicio;
    @FXML
    private TextField txtHoras;
    @FXML
    private TextField txtMinutos;
    @FXML
    private TextField txtSegundos;
    @FXML
    private TextField txtLugarEvento;
    @FXML
    private ComboBox cmbCodigoServicio;
    @FXML
    private ComboBox cmbCodigoEmpleado;
    @FXML
    private GridPane grpFecha;
    @FXML
    private TableView tblServicioshasEmpleados;
    @FXML
    private TableColumn colServiciosCodigoServicio;
    @FXML
    private TableColumn colCodigoServicio;
    @FXML
    private TableColumn colCodigoEmpleado;
    @FXML
    private TableColumn colFechaEvento;
    @FXML
    private TableColumn colHoraEvento;
    @FXML
    private TableColumn colLugarEvento;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnReporte;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnEditar;
    @FXML
    private ImageView imgNuevo;
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
        grpFecha.add(fecha, 1, 3);
        cmbCodigoServicio.setItems(getServicio());
        cmbCodigoEmpleado.setItems(getEmpleado());
        fecha.setDisable(true);
        btnEliminar.setDisable(true);
        btnEditar.setDisable(true);
    }

    public void cargarDatos() {
        try {
            tblServicioshasEmpleados.setItems(getServicioHasPlatos());
            colServiciosCodigoServicio.setCellValueFactory(new PropertyValueFactory<ServicioHasEmpleado, Integer>("Servicios_codigoServicio"));
            colCodigoServicio.setCellValueFactory(new PropertyValueFactory<ServicioHasEmpleado, Integer>("codigoServicio"));
            colCodigoEmpleado.setCellValueFactory(new PropertyValueFactory<ServicioHasEmpleado, Integer>("codigoEmpleado"));
            colFechaEvento.setCellValueFactory(new PropertyValueFactory<ServicioHasEmpleado, Date>("fechaEvento"));
            colHoraEvento.setCellValueFactory(new PropertyValueFactory<ServicioHasEmpleado, Time>("horaEvento"));
            colLugarEvento.setCellValueFactory(new PropertyValueFactory<ServicioHasEmpleado, String>("lugarEvento"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ObservableList<ServicioHasEmpleado> getServicioHasPlatos() {
        ArrayList<ServicioHasEmpleado> lista = new ArrayList<ServicioHasEmpleado>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_ListarServicios_has_Empleados()");
            ResultSet resultado = procedimiento.executeQuery();
            while (resultado.next()) {
                lista.add(new ServicioHasEmpleado(resultado.getInt("Servicios_codigoServicio"),
                        resultado.getInt("codigoServicio"),
                        resultado.getInt("codigoEmpleado"),
                        resultado.getDate("fechaEvento"),
                        resultado.getString("horaEvento"),
                        resultado.getString("lugarEvento")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaServiciohasEmpleado = FXCollections.observableArrayList(lista);
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

    public void seleccionarElemento() {
        if (tblServicioshasEmpleados.getSelectionModel().getSelectedItem() != null) {
            txtServicioCodigoServicio.setText(String.valueOf(((ServicioHasEmpleado) tblServicioshasEmpleados.getSelectionModel().getSelectedItem()).getServicios_codigoServicio()));
            cmbCodigoServicio.getSelectionModel().select(buscarServicio(((ServicioHasEmpleado) tblServicioshasEmpleados.getSelectionModel().getSelectedItem()).getCodigoServicio()));
            cmbCodigoEmpleado.getSelectionModel().select(buscarEmpleado(((ServicioHasEmpleado) tblServicioshasEmpleados.getSelectionModel().getSelectedItem()).getCodigoEmpleado()));
            fecha.selectedDateProperty().set(((ServicioHasEmpleado) tblServicioshasEmpleados.getSelectionModel().getSelectedItem()).getFechaEvento());
            String cadena = String.valueOf(((ServicioHasEmpleado) tblServicioshasEmpleados.getSelectionModel().getSelectedItem()).getHoraEvento());
            String[] partes = cadena.split(":");
            txtHoras.setText(partes[0]);
            txtMinutos.setText(partes[1]);
            txtSegundos.setText(partes[2]);
            txtLugarEvento.setText(String.valueOf(((ServicioHasEmpleado) tblServicioshasEmpleados.getSelectionModel().getSelectedItem()).getLugarEvento()));
        } else {
            JOptionPane.showMessageDialog(null, "Selecciona un campo que tenga datos.");
        }
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

    public Empleado buscarEmpleado(int codigoEmpleado) {
        Empleado resultado = null;
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_BuscarEmpleado(?)");
            procedimiento.setInt(1, codigoEmpleado);
            ResultSet registro = procedimiento.executeQuery();
            while (registro.next()) {
                resultado = new Empleado(registro.getInt("codigoEmpleado"),
                        registro.getInt("numeroEmpleado"),
                        registro.getString("apellidosEmpleado"),
                        registro.getString("nombresEmpleado"),
                        registro.getString("direccionEmpleado"),
                        registro.getString("telefonoContacto"),
                        registro.getString("gradoCocinero"),
                        registro.getInt("codigoTipoEmpleado"));
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
        String codSerHasEmple = txtServicioCodigoServicio.getText();
        codSerHasEmple = codSerHasEmple.replaceAll(" ", "");
        String lugarServi = txtLugarEvento.getText();
        lugarServi = lugarServi.replaceAll(" ", "");
        String hora = txtHoras.getText();
        String minu = txtMinutos.getText();
        String segun = txtSegundos.getText();
        hora = hora.replaceAll(" ", "");
        minu = minu.replaceAll(" ", "");
        segun = segun.replaceAll(" ", "");
        if (codSerHasEmple.length() == 0) {
            JOptionPane.showMessageDialog(null, "Debe colocar el código ServiciosCodigoServicio");
        } else {
            if (cmbCodigoEmpleado.getSelectionModel().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Debe seleccionar el código del empleado");
            } else {
                if (cmbCodigoServicio.getSelectionModel().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Debe seleccionar el código del servicio");
                } else {
                    if (lugarServi.length() == 0) {
                        JOptionPane.showMessageDialog(null, "Debe colocar el lugar del servicio");
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
                                                    ServicioHasEmpleado registro = new ServicioHasEmpleado();
                                                    registro.setServicios_codigoServicio(Integer.parseInt(txtServicioCodigoServicio.getText()));
                                                    registro.setCodigoServicio(((Servicio) cmbCodigoServicio.getSelectionModel().getSelectedItem()).getCodigoServicio());
                                                    registro.setCodigoEmpleado(((Empleado) cmbCodigoEmpleado.getSelectionModel().getSelectedItem()).getCodigoEmpleado());
                                                    registro.setFechaEvento(fecha.getSelectedDate());
                                                    String horaServicio = txtHoras.getText() + ":" + txtMinutos.getText() + ":" + txtSegundos.getText();
                                                    registro.setHoraEvento(horaServicio);
                                                    registro.setLugarEvento(txtLugarEvento.getText());
                                                    PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_AgregarServicio_has_Empleado(?,?,?,?,?,?)");
                                                    procedimiento.setInt(1, registro.getServicios_codigoServicio());
                                                    procedimiento.setInt(2, registro.getCodigoServicio());
                                                    procedimiento.setInt(3, registro.getCodigoEmpleado());
                                                    procedimiento.setDate(4, new java.sql.Date(registro.getFechaEvento().getTime()));
                                                    procedimiento.setTime(5, Time.valueOf(registro.getHoraEvento()));
                                                    procedimiento.setString(6, registro.getLugarEvento());
                                                    procedimiento.execute();
                                                    listaServiciohasEmpleado.add(registro);
                                                } catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e) {
                                                    Toolkit.getDefaultToolkit().beep();
                                                    JOptionPane.showMessageDialog(null, "El código ServiciosCodigoServicio ya existe", "Aviso", JOptionPane.WARNING_MESSAGE);                                              
                                                } catch (java.lang.NumberFormatException e) {
                                                    Toolkit.getDefaultToolkit().beep();
                                                    JOptionPane.showMessageDialog(null, "El valor del código ServiciosCodigoServicio solo puede ser de tipo númerico", "Aviso", JOptionPane.WARNING_MESSAGE);
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
                tblServicioshasEmpleados.getSelectionModel().clearSelection();
                break;
        }
    }

    public void deseleccionar() {
        tblServicioshasEmpleados.getSelectionModel().clearSelection();
        limpiarControles();
    }

    public void desactivarControles() {
        txtServicioCodigoServicio.setEditable(false);
        cmbCodigoServicio.setDisable(true);
        cmbCodigoEmpleado.setDisable(true);
//        fecha.setDisable(true);
        txtHoras.setEditable(false);
        txtMinutos.setEditable(false);
        txtSegundos.setEditable(false);
        txtLugarEvento.setEditable(false);
    }

    public void activarControles() {
        txtServicioCodigoServicio.setEditable(true);
        cmbCodigoServicio.setDisable(false);
        cmbCodigoEmpleado.setDisable(false);
        fecha.setDisable(false);
        txtHoras.setEditable(true);
        txtMinutos.setEditable(true);
        txtSegundos.setEditable(true);
        txtLugarEvento.setEditable(true);
    }

    public void limpiarControles() {
        txtServicioCodigoServicio.clear();
        cmbCodigoServicio.setValue(null);
        cmbCodigoEmpleado.setValue(null);
        fecha.setSelectedDate(null);
        txtHoras.clear();
        txtMinutos.clear();
        txtSegundos.clear();
        txtLugarEvento.clear();
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }

    public void menuPrincipal() {
        this.escenarioPrincipal.menuPrincipal();
    }

}
