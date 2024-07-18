/** 
	Denis Sebastian Abad Santos
	Carnet: 2021374
	Código técnico: IN5AM
	fecha de creación: 
		28/03/2023
	fechas de modificaciones: 
		31-01-2023
**/

Drop database if exists DBTonysKinal2023; 
Create database DBTonysKinal2023;
 
Use DBTonysKinal2023;

ALTER DATABASE DBTonysKinal2023 CHARACTER SET utf8 COLLATE utf8_general_ci;

Create table Empresas(
	codigoEmpresa int auto_increment not null,
    nombreEmpresa varchar(150) not null, 
    direccion varchar(150) not null, 
    telefono varchar(8),
	primary key PK_codigoEmpresa (codigoEmpresa)
); 

Create table TipoEmpleado( 
	codigoTipoEmpleado int not null auto_increment,
    descripcion varchar(50) not null, 
    primary key PK_codigoTipoEmpleado (codigoTipoEmpleado)
);

Create table TipoPlato(
	codigoTipoPlato int not null auto_increment, 
    descripcionTipo varchar(100) not null, 
    primary key PK_codigoTipoPlato (codigoTipoPlato) 
);

Create table Productos(
	codigoProducto int not null auto_increment,
    nombreProducto varchar(150) not null, 
    cantidad int not null, 
    primary key PK_codigoProducto (codigoProducto)
); 

Create table Empleados(
	codigoEmpleado int not null auto_increment, 
    numeroEmpleado int not null, 
    apellidosEmpleado varchar(150) not null, 
    nombresEmpleado varchar(150) not null,
    direccionEmpleado varchar(150) not null,
    telefonoContacto varchar(8) not null,
    gradoCocinero varchar(50), 
    codigoTipoEmpleado int not null, 
    primary key PK_codigoEmpleado (codigoEmpleado),
	constraint FK_Empleados_TipoEmpleado foreign key
		(codigoTipoEmpleado) references TipoEmpleado(codigoTipoEmpleado)
);

Create table Servicios(
	codigoServicio int not null auto_increment, 
    fechaServicio date not null, 
    tipoServicio varchar (150) not null, 
    horaServicio time not null, 
    lugarServicio varchar (150) not null, 
    telefonoContacto varchar (150) not null, 
    codigoEmpresa int not null, 
    primary key PK_codigoServicio (codigoServicio), 
    constraint FK_Servicios_Empresas foreign key (codigoEmpresa) 
		references Empresas(codigoEmpresa)
); 


Create table Presupuestos(
	codigoPresupuesto int not null auto_increment, 
    fechaSolicitud date not null, 
    cantidadPresupuesto decimal(10, 2) not null, 
    codigoEmpresa int not null, 
    primary key PK_codigoPresupuesto (codigoPresupuesto), 
    constraint FK_Presupuesto_Empresas foreign key (codigoEmpresa)
		references Empresas(codigoEmpresa)
); 

Create table Platos(
	codigoPlato int not null auto_increment, 
    cantidad int not null, 
    nombrePlato varchar(150) not null, 
    descripcion varchar(150) not null, 
    precioPlato decimal (10, 2) not null, 
    codigoTipoPlato int not null,
    primary key PK_codigoPlato (codigoPlato), 
    constraint FK_Platos_Tipo foreign key (codigoTipoPlato)
		references TipoPlato(codigoTipoPlato)
); 

Create table Productos_has_Platos(
	Productos_codigoProducto int not null, 
    codigoPlato int not null, 
    codigoProducto int not null, 
    primary key PK_Productos_codigoProducto (Productos_codigoProducto),
	constraint FK_Productos_has_Platos_Productos foreign key (codigoProducto) 
		references Productos(codigoProducto), 
	constraint FK_Productos_has_Platos_Platos foreign key (codigoPlato)
		references Platos(codigoPlato)
); 

Create table Servicios_has_Platos(
	Servicios_codigoServicio int not null, 
    codigoPlato int not null, 
    codigoServicio int not null, 
    primary key PK_Servicios_codigoServicio (Servicios_codigoServicio), 
    constraint FK_Servicios_has_Platos_Servicios foreign key (codigoServicio)
		references Servicios(codigoServicio), 
	constraint FK_Servicios_has_Platos_Platos foreign key (codigoPlato) 
		references Platos(codigoPlato) 
);

Create table Servicios_has_Empleados( 
	Servicios_codigoServicio int not null, 
    codigoServicio int not null, 
    codigoEmpleado int not null, 
    fechaEvento date not null, 
    horaEvento time not null, 
    lugarEvento varchar(150) not null, 
    primary key PK_Servicios_codigoServicio (Servicios_codigoServicio), 
    constraint FK_Servicios_has_Empleados_Servicios foreign key (codigoServicio)
		references Servicios(codigoServicio), 
	constraint FK_Servicios_has_Empleados_Empleados foreign key (codigoEmpleado) 
		references Empleados(codigoEmpleado) 
); 

Create table Usuario(
	codigoUsuario int not null auto_increment, 
    nombreUsuario varchar (100) not null,
    apellidoUsuario varchar (100) not null, 
    usuarioLogin varchar (50) not null, 
    contrasena varchar(50) not null,
    primary key PK_codigoUsuario(codigoUsuario)
);

Delimiter //
	Create procedure sp_AgregarUsuario(in nombreUsuario varchar (100), apellidoUsuario varchar (100), usuarioLogin varchar (50),
		in contrasena varchar (50))
	Begin 
		Insert into Usuario(nombreUsuario, apellidoUsuario, UsuarioLogin, contrasena)
			values (nombreUsuario, apellidoUsuario, UsuarioLogin, contrasena);
    End// 
Delimiter ; 

Delimiter //
	Create procedure sp_ListarUsuarios()
		Begin 
			Select 
            U.codigoUsuario, 
            U.nombreUsuario, 
            U.apellidoUsuario,
            U.usuarioLogin,
            U.contrasena
            from Usuario U;
		End//
Delimiter ;

call sp_AgregarUsuario('Denis','Abad','dabad','1234');
call sp_AgregarUsuario('Pedro','Armas','parmas','parmas');
call sp_ListarUsuarios();

Create table Login(
	usuarioMaster varchar(50) not null, 
    passwordLogin varchar(50) not null,
    primary key PK_usuarioMaster (usuarioMaster)
);

-- ------------------------------------PROCEDIMIENTOS ALMACENADOS ----------------------------------
-- ------------------------------------------Agregar Empresa----------------------------------------------
Delimiter //
	Create procedure sp_AgregarEmpresa(in nombreEmpresa varchar (150), in direccion varchar (150), in telefono varchar(8))
		Begin 
			Insert into Empresas(nombreEmpresa, direccion, telefono)
				values(nombreEmpresa, direccion, telefono); 
        End//
Delimiter ;
-- -------------------------------------------Listar Empresa-------------------------------------------------------- 
Delimiter //
	Create procedure sp_ListarEmpresas()
	Begin 
		Select 
			E.codigoEmpresa, 
			E.nombreEmpresa, 
            E.direccion, 
            E.telefono
            From Empresas E; 
    End//
Delimiter ;
-- ------------------------------------------Buscar Empresa ------------------------------------------------
Delimiter //
	Create procedure sp_BuscarEmpresa(in codEmpresa int)
		Begin 
			Select 
				E.codigoEmpresa, 
				E.nombreEmpresa, 
				E.direccion,
				E.telefono
				From Empresas E
					where codigoEmpresa = codEmpresa; 
        End//
Delimiter ;
-- ------------------------------------------Eliminar Empresa --------------------------------------------------- 
Delimiter //
	Create procedure sp_EliminarEmpresa(in codEmpresa int) 
		Begin 
			Delete from Empresas 	
				where codigoEmpresa = codEmpresa; 
        End//
Delimiter ;
-- -------------------------------------------Editar Empresa -----------------------------------------------------
Delimiter //
	Create procedure sp_EditarEmpresa(in codEmpresa int, in nomEmpresa varchar(150), in direc varchar(105), in tel varchar (8))
		Begin 
			Update Empresas E
				set 
				E.nombreEmpresa = nomEmpresa, 
                E.direccion = direc,
                E.telefono = tel 
				where codigoEmpresa =  codEmpresa;  
        End//
Delimiter ;
-- ---------------DATOS
call sp_AgregarEmpresa('CASSESA','Carr. Muxbal, Cdad. de Guatemala', '24622900');
call sp_AgregarEmpresa('McDonalds','7A Avenida 1-34, Cdad. de Guatemala Zona 4', '87569820');
call sp_AgregarEmpresa('Licores de Guatemala','Km 16.5 Carretera Roosevelt 4-81 zona 01 de Mixco Guatemala', '87569820');
call sp_AgregarEmpresa('Maxi Despensa','Calzada Roosevelt, Cdad. de Guatemala', '24859825');
call sp_AgregarEmpresa('Hospital Roosevelt','Calzada Roosevelt, Cdad. de Guatemala 01011', '23217400');
call sp_ListarEmpresas();
call sp_BuscarEmpresa(1);
-- call sp_EliminarEmpresa(2);
-- call sp_EditarEmpresa(3,'Tonys Kinal 3', 'Zona 1 Sexta Avenida 18 calle', '45122014'); 

-- ------------------------------------PROCEDIMIENTOS ALMACENADOS DE PRESUPUESTO ----------------------------------
-- ---------------------------------------------Agregar Presupuesto----------------------------------------------
Delimiter //
	Create procedure sp_AgregarPresupuesto(in fechaSolicitud date, in cantidadPresupuesto decimal(10,2), in codigoEmpresa int)
		Begin 
			Insert into Presupuestos(fechaSolicitud, cantidadPresupuesto, codigoEmpresa)
				values(fechaSolicitud, cantidadPresupuesto, codigoEmpresa); 
        End//
Delimiter ;
-- -------------------------------------------Listar Presupuesto-------------------------------------------------------- 
Delimiter //
	Create procedure sp_ListarPresupuestos()
	Begin 
		Select 
			P.codigoPresupuesto, 
            P.fechaSolicitud, 
            P.cantidadPresupuesto, 
            P.codigoEmpresa
            From Presupuestos P; 
    End//
Delimiter ;

call sp_ListarPresupuestos();
-- ------------------------------------------Buscar Presupuesto ------------------------------------------------
Delimiter //
	Create procedure sp_BuscarPresupuesto(in codPresupuesto int)
		Begin 
			Select 
				P.codigoPresupuesto, 
				P.fechaSolicitud, 
				P.cantidadPresupuesto, 
				P.codigoEmpresa
				From Presupuestos P
					where codigoPresupuesto = codPresupuesto; 
        End//
Delimiter ;

-- call sp_BuscarPresupuesto();
-- ------------------------------------------Eliminar Presupuesto --------------------------------------------------- 
Delimiter //
	Create procedure sp_EliminarPresupuesto(in codPresupuesto int) 
		Begin 
			Delete from Presupuestos 	
				where codigoPresupuesto = codPresupuesto; 
        End//
Delimiter ;
-- -------------------------------------------Editar Presupuesto -----------------------------------------------------
Delimiter //
	Create procedure sp_EditarPresupuesto(in codPresupuesto int, in fechaSoli date, in cantPresupuesto decimal(10,2),
		in codEmpresa int)
		Begin 
			Update Presupuestos P
				set 
				P.codigoPresupuesto = codPresupuesto, 
				P.fechaSolicitud = fechaSoli, 
				P.cantidadPresupuesto = cantPresupuesto
				where codigoPresupuesto = codPresupuesto;   
        End//
Delimiter ;
-- ---------------DATOS
call sp_AgregarPresupuesto('2022-01-05','20000.00', 1);
call sp_AgregarPresupuesto('2022-01-12','10000.00', 1);
call sp_AgregarPresupuesto('2022-01-15','12000.00', 3);
call sp_AgregarPresupuesto('2022-01-16','15000.00', 4);
call sp_AgregarPresupuesto('2022-01-20','17000.00', 5);
call sp_ListarPresupuestos();
call sp_BuscarPresupuesto(1);
-- call sp_EliminarPresupuesto(2);
-- call sp_EditarPresupuesto(3,'2021-02-12', '4000.00');

-- ------------------------------------PROCEDIMIENTOS ALMACENADOS DE TIPOEMPLEADO ----------------------------------
-- ---------------------------------------------Agregar TipoEmpleado----------------------------------------------
Delimiter //
	Create procedure sp_AgregarTipoEmpleado(in descripcion varchar(50))
		Begin 
			Insert into TipoEmpleado(descripcion)
				values(descripcion); 
        End//
Delimiter ;
-- -------------------------------------------Listar TipoEmpleados-------------------------------------------------------- 
Delimiter //
	Create procedure sp_ListarTipoEmpleados()
	Begin 
		Select 
			T.codigoTipoEmpleado, 
            T.descripcion
            From TipoEmpleado T; 
    End//
Delimiter ;
-- ------------------------------------------Buscar TipoEmpleados ------------------------------------------------
Delimiter //
	Create procedure sp_BuscarTipoEmpleado(in codTipoEmpleado int)
	Begin 
		Select 
			T.codigoTipoEmpleado,
            T.descripcion
            From TipoEmpleado T
			where codigoTipoEmpleado = codTipoEmpleado; 
    End//
Delimiter ;
-- ------------------------------------------Eliminar TipoEmpleado --------------------------------------------------- 
Delimiter //
	Create procedure sp_EliminarTipoEmpleado(in codTipoEmpleado int) 
		Begin 
			Delete from TipoEmpleado	
				where codigoTipoEmpleado = codTipoEmpleado; 
        End//
Delimiter ;
-- -------------------------------------------Editar TipoEmpleado -----------------------------------------------------
Delimiter //
	Create procedure sp_EditarTipoEmpleado(in codTipoEmpleado int, in descri varchar(50))
		Begin 
			Update TipoEmpleado T
				set 
				T.descripcion = descri 
				where codigoTipoEmpleado =  codTipoEmpleado;  
        End//
Delimiter ; 
-- ---------------DATOS
call sp_AgregarTipoEmpleado('Camarero');
call sp_AgregarTipoEmpleado('Chef');
call sp_AgregarTipoEmpleado('Ayudante de camarero');
call sp_AgregarTipoEmpleado('Ayudante de cocina');
call sp_AgregarTipoEmpleado('Bartender');

-- ------------------------------------PROCEDIMIENTOS ALMACENADOS DE TIPOPLATO ----------------------------------
-- ---------------------------------------------Agregar TipoPlato----------------------------------------------
Delimiter //
	Create procedure sp_AgregarTipoPlato(in descripcionTipo varchar(50))
		Begin 
			Insert into TipoPlato(descripcionTipo)
				values(descripcionTipo); 
        End//
Delimiter ;
-- -------------------------------------------Listar TipoPlatos-------------------------------------------------------- 
Delimiter //
	Create procedure sp_ListarTipoPlatos()
	Begin 
		Select 
			T.codigoTipoPlato, 
            T.descripcionTipo
            From TipoPlato T; 
    End//
Delimiter ;
-- ------------------------------------------Buscar TipoPlato ------------------------------------------------
Delimiter //
	Create procedure sp_BuscarTipoPlato(in codTipoPlato int)
	Begin 
		Select 
			T.codigoTipoPlato,
            T.descripcionTipo
            From TipoPlato T
			where codigoTipoPlato = codTipoPlato; 
    End//
Delimiter ;
-- ------------------------------------------Eliminar TipoPlato --------------------------------------------------- 
Delimiter //
	Create procedure sp_EliminarTipoPlato(in codTipoPlato int) 
		Begin 
			Delete from TipoPlato 	
				where codigoTipoPlato = codTipoPlato; 
        End//
Delimiter ;
-- -------------------------------------------Editar TipoPlato -----------------------------------------------------
Delimiter //
	Create procedure sp_EditarTipoPlato(in codTipoPlato int, in descriTipo varchar(50))
		Begin 
			Update TipoPlato T
				set 
				T.descripcionTipo = descriTipo
				where codigoTipoPlato =  codTipoPlato;  
        End//
Delimiter ;
-- ---------------DATOS
call sp_AgregarTipoPlato('Plato principal');
call sp_AgregarTipoPlato('Postres');
call sp_AgregarTipoPlato('Aperitivos');
call sp_AgregarTipoPlato('Guarniciones');
call sp_AgregarTipoPlato('Bebidas');
call sp_ListarTipoPlatos();
call sp_BuscarTipoPlato(1);
-- call sp_EliminarTipoPlato(2);
-- call sp_EditarTipoPlato(3,'Plato fuerte');

-- ------------------------------------PROCEDIMIENTOS ALMACENADOS DE SERVICIOS ----------------------------------
-- ---------------------------------------------Agregar Servicio----------------------------------------------
Delimiter //
	Create procedure sp_AgregarServicio(in fechaServicio date, in tipoServicio varchar(50), in horaServicio time, 
		in lugarServicio varchar(150),in telefonoContacto varchar(150), in codigoEmpresa int)
		Begin 
			Insert into Servicios(fechaServicio, tipoServicio, horaServicio, lugarServicio, telefonoContacto, codigoEmpresa)
				values(fechaServicio, tipoServicio, horaServicio, lugarServicio, telefonoContacto, codigoEmpresa); 
        End//
Delimiter ;
-- -------------------------------------------Listar Servicios-------------------------------------------------------- 
Delimiter //
	Create procedure sp_ListarServicios()
	Begin 
		Select 
			S.codigoServicio,
			S.fechaServicio, 
            S.tipoServicio, 
            S.horaServicio, 
            S.lugarServicio, 
            S.telefonoContacto, 
            S.codigoEmpresa
            From Servicios S; 
    End//
Delimiter ;
-- ------------------------------------------Buscar Servicio ------------------------------------------------
Delimiter //
	Create procedure sp_BuscarServicio(in codServicio int)
	Begin 
		Select 
			S.codigoServicio,
			S.fechaServicio, 
            S.tipoServicio, 
            S.horaServicio, 
            S.lugarServicio, 
            S.telefonoContacto, 
            S.codigoEmpresa
            From Servicios S
			where codigoServicio = codServicio; 
    End//
Delimiter ;
-- ------------------------------------------Eliminar Servicio --------------------------------------------------- 
Delimiter //
	Create procedure sp_EliminarServicio(in codServicio int) 
		Begin 
			Delete from Servicios 	
				where codigoServicio = codServicio; 
        End//
Delimiter ;
-- -------------------------------------------Editar Servicio -----------------------------------------------------
Delimiter //
	Create procedure sp_EditarServicio(in codServicio int, in fechaServi date, in tipoServi varchar(150), 
		in horaServi time, in lugarServi varchar(150), in telContacto varchar(150))
		Begin 
			Update Servicios S
				set 
				S.fechaServicio = fechaServi, 
				S.tipoServicio = tipoServi, 
				S.horaServicio = horaServi, 
				S.lugarServicio = lugarServi, 
				S.telefonoContacto = telContacto 
				where codigoServicio =  codServicio;  
        End//
Delimiter ;
-- ---------------DATOS
call sp_AgregarServicio('2023-06-18', 'Cumpleaños', '13:00:00', 'Unnamed Road, San Lucas Sacatepéquez', '78419852', 1);
call sp_AgregarServicio('2023-10-10', 'Fiesta', '22:00:00', '15 Avenida 24-65r Mixco', '34000047', 2);
call sp_AgregarServicio('2023-07-21', 'Boda', '19:30:00', '19.5, Carr. a Fraijanes, Cdad. de Guatemala 01062', '04893592', 3);
call sp_AgregarServicio('2023-07-05', 'Aniversario', '16:00:00', 'Carr. Colmenas-Villa Canales, Villa Canales', '24584599', 4);
call sp_AgregarServicio('2023-12-24', 'Cena', '18:30:00', 'Km. 19.5 Carretera a Fraijanes, RN-2', '44717505', 5);
call sp_ListarServicios();
call sp_BuscarServicio(1);
-- call sp_EliminarServicio(5);
-- call sp_EditarServicio(3, '2023-04-20', 'Boda', '14:00:00', 'Zona 10, diagonal 6', '8623 1931');

-- ------------------------------------PROCEDIMIENTOS ALMACENADOS DE PLATOS ----------------------------------
-- ---------------------------------------------Agregar Platos----------------------------------------------
Delimiter //
	Create procedure sp_AgregarPlato(in cantidad int, in nombrePlato varchar(150), in descripcion varchar(150), 
		in precioPlato decimal(10,2), in codigoTipoPlato int)
		Begin 
			Insert into Platos(cantidad, nombrePlato, descripcion, precioPlato, codigoTipoPlato)
				values(cantidad, nombrePlato, descripcion, precioPlato, codigoTipoPlato); 
        End//
Delimiter ;
-- -------------------------------------------Listar Platos-------------------------------------------------------- 
Delimiter //
	Create procedure sp_ListarPlatos()
	Begin 
		Select 
			P.codigoPlato, 
            P.cantidad, 
            P.nombrePlato,
            P.descripcion, 
            P.precioPlato, 
            P.codigoTipoPlato
            From Platos P; 
    End//
Delimiter ;
-- ------------------------------------------Buscar Plato ------------------------------------------------
Delimiter //
	Create procedure sp_BuscarPlato(in codPlato int)
	Begin 
		Select 
			P.codigoPlato, 
            P.cantidad, 
            P.nombrePlato,
            P.descripcion, 
            P.precioPlato, 
            P.codigoTipoPlato
            From Platos P
			where codigoPlato = codPlato; 
    End//
Delimiter ;
-- ------------------------------------------Eliminar Plato --------------------------------------------------- 
Delimiter //
	Create procedure sp_EliminarPlato(in codPlato int) 
		Begin 
			Delete from Platos 	
				where codigoPlato = codPlato; 
        End//
Delimiter ;
-- -------------------------------------------Editar Plato -----------------------------------------------------
Delimiter //
	Create procedure sp_EditarPlato(in codPlato int, in cant int, in nomPlato varchar(150), in descri varchar(150), 
		in precPlato decimal(10,2))
		Begin 
			Update Platos P
				set  
				P.cantidad = cant, 
				P.nombrePlato = nomPlato,
				P.descripcion = descri, 
				P.precioPlato = precPlato
				where codigoPlato =  codPlato;  
        End//
Delimiter ;
-- ---------------DATOS
call sp_AgregarPlato(100, 'Pierna al horno', 'Pierna al horno con arroz y ensalada', '80.00', 1);
call sp_AgregarPlato(100, 'Rellenitos de plátano', 'Platano en aceite rellenos de frijol dulce', '8.00', 2);
call sp_AgregarPlato(100, 'Churrasco', 'Carne a elección, guacamole, frijoles, arroz, pico de gallo', '25.00', 1);
call sp_AgregarPlato(100, 'Pollo encebollado', 'Es un plato de pollo cocinado con cebolla, tomate, especias, se sirve con arroz, frijoles y tortillas.', '40.00', 1);
call sp_AgregarPlato(100, 'Horchata', 'Bebida a partir de arroz con canela', '5.00', 5);
call sp_ListarPlatos();
call sp_BuscarPlato(1);
-- call sp_EliminarPlato(2);
-- call sp_EditarPlato(3, 200, 'Carne a las brazas', 'Carne a las brazas con arroz y ensalada', '40.00');

-- ------------------------------------PROCEDIMIENTOS ALMACENADOS DE PRODUCTOS ----------------------------------
-- ---------------------------------------------Agregar Producto------------------------------------------------
Delimiter //
	Create procedure sp_AgregarProducto(in nombreProducto varchar(150), in cantidad int)
		Begin
			Insert into Productos (nombreProducto, cantidad)
				Values(nombreProducto, cantidad);
        End//
Delimiter ;
-- ------------------------------------------------Listar Productos--------------------------------------------------- 
Delimiter //
	Create procedure sp_ListarProductos()
		Begin
			Select 
				P.codigoProducto,
				P.nombreProducto,
                P.cantidad
                From Productos P;
        End//
Delimiter ;
-- -------------------------------------------------Buscar Producto ------------------------------------------------
Delimiter //
	Create procedure sp_BuscarProducto(in codProducto int)
		Begin
			Select
				P.codigoProducto,
				P.nombreProducto, 
                P.cantidad
                From Productos P
                Where codigoProducto = codProducto;
        End//
Delimiter ;
-- ------------------------------------------------Eliminar Producto --------------------------------------------------- 
Delimiter //
	Create  procedure sp_EliminarProducto(in codProducto int)
		Begin
			delete from Productos
            where codigoProducto = codProducto;
        End//
Delimiter ;
-- ------------------------------------------------Editar Producto -----------------------------------------------------
Delimiter //
	Create procedure sp_EditarProducto(in codProducto int, in nomProducto varchar(150), in cant int)	
		Begin
			Update Productos P
				set
                    P.nombreProducto = nomProducto, 
                    P.cantidad = cant
                    Where codigoProducto = codProducto;
		End //
Delimiter ;
-- DATOS ---------------
call sp_AgregarProducto('Bolsas de frijoles', 50);
call sp_AgregarProducto('Especias variadas', 30);
call sp_AgregarProducto('Platanos', 200);
call sp_AgregarProducto('Carne de cerdo', 100);
call sp_AgregarProducto('Carne de cerdo', 200);
call sp_ListarProductos();
call sp_BuscarProducto(1);
-- call sp_EliminarProducto(2);
-- call sp_EditarProducto(3, 'Pan con salchicha y papa', 100);
-- ------------------------------------PROCEDIMIENTOS ALMACENADOS DE PRODUCTOS ----------------------------------
-- ---------------------------------------------Agregar Empleado------------------------------------------------
Delimiter //
	Create procedure sp_AgregarEmpleado(in numeroEmpleado int,in apellidosEmpleado varchar(150),in nombresEmpleado varchar(150), 
		in direccionEmpleado varchar(150), in telefonoContacto varchar(8), in gradoCocinero varchar(50), in codigoTipoEmpleado int)
		Begin
			Insert into Empleados (numeroEmpleado, apellidosEmpleado, nombresEmpleado, direccionEmpleado, telefonoContacto, gradoCocinero,
				codigoTipoEmpleado)
				Values(numeroEmpleado, apellidosEmpleado, nombresEmpleado, direccionEmpleado, telefonoContacto, gradoCocinero, 
					codigoTipoEmpleado);
        End//
Delimiter ;
-- ------------------------------------------------Listar Empleados--------------------------------------------------- 
Delimiter //
	Create procedure sp_ListarEmpleados()
		Begin
			Select 
				E.codigoEmpleado,
				E.numeroEmpleado, 
                E.apellidosEmpleado, 
                E.nombresEmpleado, 
                E.direccionEmpleado, 
                E.telefonoContacto, 
                E.gradoCocinero, 
                E.codigoTipoEmpleado
                from Empleados E;
        End//
Delimiter ;
-- -------------------------------------------------Buscar Empleado ------------------------------------------------
Delimiter //
	Create procedure sp_BuscarEmpleado(in codEmpleado int)
		Begin
			Select 
				E.codigoEmpleado, 
				E.numeroEmpleado, 
                E.apellidosEmpleado, 
                E.nombresEmpleado, 
                E.direccionEmpleado, 
                E.telefonoContacto, 
                E.gradoCocinero, 
                E.codigoTipoEmpleado
                from Empleados E
                where codigoEmpleado = codEmpleado;
        End//
Delimiter ;
-- ------------------------------------------------Eliminar Empleado --------------------------------------------------- 
Delimiter //
	Create  procedure sp_EliminarEmpleado(in codEmpleado int)
		Begin
			Delete From Empleados
            Where codigoEmpleado = codEmpleado;
        End//
Delimiter ;
-- ------------------------------------------------Editar Empleado --------------------------------------------------- 
Delimiter //
	Create procedure sp_EditarEmpleado(in codEmpleado int, in numEmpleado int, in apelliEmpleado varchar(150),
		in nomEmpleado varchar(150), in direcEmpleado varchar(150), in telContacto varchar(8), in graCoci varchar(50))
        Begin
			Update Empleados E
				set
                E.numeroEmpleado = numEmpleado,
                E.apellidosEmpleado = apelliEmpleado, 
                E.nombresEmpleado = nomEmpleado, 
                E.direccionEmpleado = direcEmpleado,
                E.telefonoContacto = telContacto,
                E.gradoCocinero = graCoci
                where E.codigoEmpleado = codEmpleado; 		
        End//
 Delimiter ; 

call sp_ListarTipoEmpleados();
call sp_AgregarEmpleado(5, 'Alvarez Cortez', 'Benito Raul', 'Zona 13, 13 calle, lote 102', '87565321', 'Chef de cocina caliente', 2);
call sp_AgregarEmpleado(12, 'Marroquin Santos', 'Fernanda Stacy', 'Av. Petapa, Cdad. de Guatemala, lote 2', '54213894', 'Chef de cocina fría', 2);
call sp_AgregarEmpleado(7, 'Cordoba Espinoza', 'Roberto Enrique', 'Calz Raul Aguilar Batres 38-51, Villa Nueva', '87565321', '', 1);
call sp_AgregarEmpleado(9, 'Estrada Esquite', 'Marta Jazmín', '47 Avenida 113-137, Cdad. de Guatemala', '87565321', '', 3);
call sp_AgregarEmpleado(50, 'Guzmán Rivas', 'Carlos Fernando', 'Avenida Hincapie 601-429, Cdad. de Guatemala', '87565321', '', 4);
call sp_ListarEmpleados();
call sp_BuscarEmpleado(1);
-- call sp_EliminarEmpleado(2);
-- call sp_EditarEmpleado(3, 12, 'Marroquin Santos', 'Fernanda Stacy', 'Zona 8, 8va calle', '51698732', 'Chef de cocina fría');

-- ------------------------------------PROCEDIMIENTOS ALMACENADOS DE PRODUCTOS_HAS_PLATOS ----------------------------------
-- ---------------------------------------------Agregar PRODUCTO_HAS_PLATO----------------------------------------------
Delimiter //
	Create procedure sp_AgregarProducto_has_Plato(in Productos_codigoProducto int, in codigoPlato int, in codigoProducto int)
        Begin
			Insert Into Productos_has_Platos(Productos_codigoProducto, codigoPlato, codigoProducto)
				Values (Productos_codigoProducto, codigoPlato, codigoProducto);
        End//
Delimiter ; 
-- ---------------------------------------------Listar PRODUCTOS_HAS_PLATOS----------------------------------------------
Delimiter //
	Create procedure sp_ListarProductos_has_Platos()
		Begin
			Select 
				PHP.Productos_codigoProducto, 
				PHP.codigoPlato, 
				PHP.codigoProducto
				From Productos_has_Platos PHP; 
        End//
Delimiter ; 
-- ---------------------------------------------Buscar PRODUCTO_HAS_PLATO----------------------------------------------
Delimiter //
	Create procedure sp_BuscarProducto_has_Plato(in Producto_codProducto int)
		Begin
			Select 
				PHP.codigoPlato, 
				PHP.codigoProducto
				From Productos_has_Platos PHP
					Where PHP.Productos_codigoProducto = Producto_codProducto; 
        End//
Delimiter ; 
-- ---------------------------------------------Eliminar PRODUCTO_HAS_PLATO----------------------------------------------
Delimiter //
	Create procedure sp_EliminarProducto_has_Plato(in Productos_codProducto int)
		Begin
			Delete from Productos_has_Platos
				Where PHP.Productos_codigoProducto = Productos_codProducto; 
        End//
Delimiter ; 
-- ---------------------------------------------Editar PRODUCTO_HAS_PLATO----------------------------------------------
Delimiter //
	Create procedure sp_EditarProducto_has_Plato(in Productos_codProducto int, 
		in codPlato int, in codProducto int)
		Begin
			update Productos_has_Platos PHP
				set 
					PHP.codigoPlato = codPlato, 
                    PHP.codigoProducto = codProducto
                    Where PHP.Productos_codigoProducto = Productos_codProducto;
        End//
Delimiter ; 
-- DATOS ---------------
call sp_AgregarProducto_has_Plato(1, 1, 5);
call sp_AgregarProducto_has_Plato(2, 1, 4);
call sp_AgregarProducto_has_Plato(3, 2, 3);
call sp_AgregarProducto_has_Plato(4, 3, 4);
call sp_AgregarProducto_has_Plato(5, 3, 1);
call sp_ListarProductos_has_Platos();
call sp_BuscarProducto_has_Plato(1);

-- ------------------------------------PROCEDIMIENTOS ALMACENADOS DE SERVICIOS_HAS_EMPLEADOS ----------------------------------
-- ---------------------------------------------Agregar SERVICIO_HAS_EMPLEADO----------------------------------------------
Delimiter //
	create procedure sp_AgregarServicio_has_Empleado(in Servicios_codigoServicio int, in codigoServicio int,
		in codigoEmpleado int , in fechaEvento date ,in  horaEvento time, in lugarEvento varchar(150))
        Begin
			Insert into Servicios_has_Empleados(Servicios_codigoServicio, codigoServicio, 
				codigoEmpleado, fechaEvento, horaEvento, lugarEvento) values 
                (Servicios_codigoServicio, codigoServicio, codigoEmpleado, fechaEvento, horaEvento, lugarEvento);
        End//
Delimiter ; 
-- ---------------------------------------------Listar SERVICIOS_HAS_EMPLEADOS----------------------------------------------
Delimiter //
	Create procedure sp_ListarServicios_has_Empleados()
		Begin
			Select
            SHE.Servicios_codigoServicio,
            SHE.codigoServicio, 
            SHE.codigoEmpleado,
            SHE.fechaEvento,
            SHE.horaEvento,
            SHE.lugarEvento
            From Servicios_has_Empleados SHE; 
        End//
Delimiter ; 
-- ---------------------------------------------Buscar SERVICIO_HAS_EMPLEADO----------------------------------------------
Delimiter //
	Create procedure sp_BuscarServicio_has_Empleado(in Servicio_codServicio int)
		Begin
			Select
            SHE.codigoServicio, 
            SHE.codigoEmpleado,
            SHE.fechaEvento,
            SHE.horaEvento,
            SHE.lugarEvento
            From Servicios_has_Empleados SHE
				Where SHE.Servicios_codigoServicio = Servicio_codServicio; 
				
        End//
Delimiter ; 
-- ---------------------------------------------Eliminar SERVICIO_HAS_EMPLEADO----------------------------------------------
Delimiter //
	Create procedure sp_EliminarServicio_has_Empleado(in Servicios_codServicio int )
		Begin
			Delete from Servicios_has_Empleados
				where SHE.Servicios_codigoServicio = Servicios_codServicio; 
        End//
Delimiter ; 
-- ---------------------------------------------Editar SERVICIO_HAS_EMPLEADO----------------------------------------------
Delimiter //
	Create procedure sp_EditarServicio_has_Empleado(in Servicios_codServicio int, in fechaEvent date,
		in  horaEvent time, in lugarEvent varchar(150))
        Begin
			Update Servicios_has_Empleados SHE 
				set
					SHE.fechaEvento = fechaEvent, 
                    SHE.horaEvento = horaEvent, 
                    SHE.lugarEvento = lugarEvent
                    Where SHE.Servicios_codigoServicio = Servicios_codServicio; 
        End//
Delimiter ; 
-- -----DATOSSSS
call sp_AgregarServicio_has_Empleado(1, 1, 1, '2023-06-18', '13:00:00', 'Jardín Casa Ariana');
call sp_AgregarServicio_has_Empleado(2, 1, 4, '2023-06-18', '13:00:00', 'Jardín Casa Ariana');
call sp_AgregarServicio_has_Empleado(3, 1, 5, '2023-06-18', '13:00:00', 'Jardín Casa Ariana');
call sp_AgregarServicio_has_Empleado(4, 2, 2, '2023-10-10', '22:00:00', 'Jardín las Orquídeas');
call sp_AgregarServicio_has_Empleado(5, 3, 2, '2023-07-21', '19:30:00', 'Eventos San Bernardo');
call sp_ListarServicios_has_Empleados();
-- ------------------------------------PROCEDIMIENTOS ALMACENADOS DE SERVICIOS_HAS_PLATOS ----------------------------------
-- ---------------------------------------------Agregar SERVICIO_HAS_PLATO----------------------------------------------
Delimiter //
	Create procedure sp_AgregarServicio_has_Plato(in Servicios_codigoServicio int , in codigoPlato int,
		in codigoServicio int)
        Begin
			Insert into Servicios_has_Platos(Servicios_codigoServicio, codigoPlato, codigoServicio)
				Values (Servicios_codigoServicio, codigoPlato, codigoServicio);
        End//
Delimiter ; 
-- ---------------------------------------------Listar SERVICIOS_HAS_PLATOS----------------------------------------------
Delimiter //
	Create procedure sp_ListarServicios_has_Platos()
		Begin
			Select 
				Servicios_codigoServicio,
				codigoPlato, 
                codigoServicio
                From Servicios_has_Platos; 
        End//
Delimiter ;
-- ---------------------------------------------Buscar SERVICIO_HAS_PLATO----------------------------------------------
Delimiter //
	Create procedure sp_BuscarServicio_has_Plato (in Servicios_codServicio int)
		Begin
			Select 
                SHP.codigoPlato, 
                SHP.codigoServicio
                From Servicios_has_Platos SHP
					Where SHP.Servicios_codigoServicio = Servicios_codServicio; 
        End//
Delimiter ;
-- ---------------------------------------------Eliminar SERVICIO_HAS_PLATO----------------------------------------------
Delimiter //
	Create procedure sp_EliminarServicio_has_Plato(in Servicios_codServicio int)
		Begin
			Delete from Servicios_has_Platos
				where SHP.Servicios_codigoServicio = Servicios_codServicio; 
        End//
Delimiter ;
-- ---------------------------------------------Editar SERVICIO_HAS_PLATO----------------------------------------------
Delimiter //
	Create Procedure sp_EditarServicio_has_Plato(in Servicios_codServicio int , in codPlato int,
		in codServicio int)
        Begin
			Update Servicios_has_Platos SHP
				set 
					SHP.codigoPlato = codPlato, 
					SHP.codigoServicio = codServicio
					Where SHP.Servicios_codigoServicio = Servicios_codServicio; 					
        End//
Delimiter ; 

call sp_AgregarServicio_has_Plato(1, 1, 1);
call sp_AgregarServicio_has_Plato(2, 1, 2);
call sp_AgregarServicio_has_Plato(3, 1, 5);
call sp_AgregarServicio_has_Plato(4, 2, 4);
call sp_AgregarServicio_has_Plato(5, 2, 5);
call sp_ListarServicios_has_Platos();

Delimiter // 
	Create Procedure sp_ReporteFinal(in codEmpresa int)
		begin 
			Select
				E.nombreEmpresa,
                E.direccion,
                E.telefono,
                P.cantidadPresupuesto,
                S.fechaServicio,
                S.tipoServicio,
                EM.nombresEmpleado,
                EM.apellidosEmpleado,
                PLA.nombrePlato,
                PLA.cantidad,
                PLA.precioPlato,
                PRO.nombreProducto,
                SHE.horaEvento,
                SHE.lugarEvento
                From Empresas E Inner Join Presupuestos P on E.codigoEmpresa = P.codigoEmpresa
					Inner Join Servicios S on E.codigoEmpresa = S.codigoEmpresa
						Inner Join Servicios_has_Empleados SHE on S.codigoServicio = SHE.codigoServicio
							Inner Join Empleados EM on SHE.codigoEmpleado = EM.codigoEmpleado
								Inner Join TipoEmpleado TE on EM.codigoTipoEmpleado = TE.codigoTipoEmpleado
									Inner Join Servicios_has_platos SHP on S.codigoServicio = SHP.codigoServicio
										Inner Join Platos PLA on SHP.codigoPlato = PLA.codigoPlato
											Inner Join Productos_has_Platos PHP on PLA.codigoPlato = PHP.codigoPlato
												Inner Join Productos PRO on PHP.codigoProducto = PRO.codigoProducto
				where E.codigoEmpresa = codEmpresa	group by (S.fechaServicio);
		end //
Delimiter ;
call sp_ReporteFinal(1);