-- Programador: Alvaro Miguel González Hic
-- Creacion: 25/05/2019
-- Modificaciones:
	-- 25/05/2019 Creación de Tablas 
    -- 26/05/2019 Creación de Procedimientos
	-- 27/05/2019 Creación de Procediminetos

drop database if exists DBHospitalInfectologia2018036;
create database DBHospitalInfectologia2018036;
use DBHospitalInfectologia2018036;

create table Pacientes (
	codigoPaciente int not null auto_increment,
    DPI varchar(20) not null,
    apellidos varchar(100) not null,
    nombres varchar(100) not null,
    fechaNacimiento date not null,
    edad int,
    direccion varchar(150) not null,
    ocupacion varchar(50) not null,
    sexo varchar(15) not null,
    
    primary key PK_codigoPaciente(codigoPaciente)
);

create table ContactoUrgencia (
	codigoContactoUrgencia int not null auto_increment,
    nombres varchar(100) not null,
    apellidos varchar(100) not null,
    numeroContacto varchar(10) not null,
    codigoPaciente int not null unique,
    
    primary key PK_codigoContactoUrgencia(codigoContactoUrgencia),
    
	constraint FK_ContactoUrgencia_Pacientes foreign key(codigoPaciente)
    references Pacientes(codigoPaciente) on delete cascade
);

create table Areas (
	codigoArea int not null auto_increment,
    nombreArea varchar(45) not null,
    
    primary key PK_codigoArea(codigoArea)
);

create table Cargos (
	codigoCargo int not null auto_increment,
	nombreCargo varchar(45) not null,

	primary key PK_codigoCargo(codigoCargo)
);

create table ResponsableTurno (
	codigoResponsableTurno int not null auto_increment,
    nombreResponsable varchar(75) not null,
    apellidoResponsable varchar(75)not null,
    telefonoPersonal varchar(10)not null,
    codigoArea int not null, 
    codigoCargo int not null,
    
    primary key PK_codigoResponsableTurno(codigoResponsableTurno),
    
    constraint FK_ResponsableTurno_Areas foreign key(codigoArea)
    references Areas(codigoArea) on delete cascade,
    constraint FK_ResponsableTurno_Cargos foreign key(codigoCargo)
    references Cargos(codigoCargo) on delete cascade
);

create table Medicos (
	codigoMedico int not null auto_increment,
    licenciaMedica int not null,
    nombres varchar(100) not null,
    apellidos varchar(100) not null,
    horaEntrada varchar(10) not null,
    horaSalida varchar(10) not null,
    turnoMaximo int default 0,
    sexo varchar(20) not null,
    
    primary key PK_codigoMedico(codigoMedico)
);


create table telefonosMedico (

	codigoTelefonoMedico int not null auto_increment,
    telefonoPersonal varchar(15) not null,
    telefonoTrabajo varchar(15),
    codigoMedico int not null unique,
    
    primary key PK_codigoTelefonoMedico(codigoTelefonoMedico),
    
    constraint FK_telefonosMedico_Medico foreign key(codigoMedico)
    references Medicos(codigoMedico) on delete cascade
);

create table Especialidades (
	codigoEspecialidad int not null auto_increment,
    nombreEspecialidad varchar(45) not null,
    
    primary key PK_codigoEspecialidad(codigoEspecialidad)
);

create table Horarios (
	codigoHorario int not null auto_increment,
    horaEntrada varchar(10) not null,
    horaSalida varchar(10) not null,
    lunes boolean,
    martes boolean,
    miercoles boolean,
    jueves boolean,
    viernes boolean,
    
    primary key PK_codigoHorario(codigoHorario)
);


create table Medico_especialidad (
	codigoMedicoEspecialidad int not null auto_increment,
    codigoMedico int not null,
    codigoEspecialidad int not null,
    codigoHorario int not null,
    
    primary key PK_codigoMedicoEspecialidad(codigoMedicoEspecialidad),
    
    constraint FK_Medico_Especialidad_Medicos foreign key(codigoMedico)
    references Medicos(codigoMedico),
    constraint FK_Medico_Especialidad_Especialidades foreign key(codigoEspecialidad)
    references Especialidades(codigoEspecialidad),
    constraint FK_Medico_Especialidad_Horarios foreign key(codigoHorario)
    references Horarios(codigoHorario)
);

create table Turnos (
	codigoTurno int not null auto_increment,
    fechaTurno datetime not null,
    fechaCita datetime not null,
    valorCita decimal(10, 2) not null,
    codigoResponsableTurno int not null,
    codigoMedicoEspecialidad int not null,
    codigoPaciente int not null,
    
    primary key PK_codigoTurno(codigoTurno),
    
    constraint FK_turno_ResponsableTurno foreign key(codigoResponsableTurno)
    references ResponsableTurno(codigoResponsableTurno),
    constraint FK_turno_Medico_Especialidad foreign key(codigoMedicoEspecialidad)
    references Medico_Especialidad(codigoMedicoEspecialidad),
    constraint FK_turno_Pacientes foreign key(codigoPaciente)
    references Pacientes(codigoPaciente)
);

create table ControlCitas (
	codigoControlCita int auto_increment,
    fecha Date not null,
    horaInicio varchar(45) not null,
    horaFin varchar(45) not null,
    codigoMedico int not null,
    codigoPaciente int not null,
    
    primary key PK_codigoControlCita(codigoControlCita),
    constraint FK_ControlCitas_Medicos foreign key(codigoMedico)
    references Medicos(codigoMedico) on delete cascade,
    constraint FK_ControlCitas_Pacientes foreign key(codigoPaciente)
    references Pacientes(codigoPaciente) on delete cascade
);

create table Recetas (
	codigoReceta int auto_increment,
    descripcionReceta varchar(45) not null,
    codigoControlCita int not null,
    primary key PK_codigoReceta(codigoReceta),
    constraint FK_Recetas_ControlCitas foreign key(codigoControlCita)
    references ControlCitas(codigoControlCita) on delete cascade
);
		

															-- Procedimientos almacenados de Pacientes --
										-- ------------------------------------Agregar----------------------------------- --
Delimiter $$
create trigger tr_Pacientes_Before_Insert
	before insert on dbhospitalinfectologia2018036.Pacientes
		for each row 
		begin
			set new.edad = timestampdiff(year, new.fechaNacimiento, now());
		end$$
Delimiter $$

Delimiter $$
create trigger tr_Pacientes_Before_Update
	before update on dbhospitalinfectologia2018036.Pacientes
		for each row 
		begin
			set new.edad = timestampdiff(year, new.fechaNacimiento, now());
		end$$
Delimiter $$

Delimiter $$
	create procedure sp_AgregarPaciente(in nomb varchar(100), in apell varchar(100), in dir varchar(150), in ocup varchar(50), in fechaN date,in DPI varchar(20), in sexo varchar(15))
		begin
			insert into Pacientes(nombres, apellidos, direccion,  ocupacion, fechaNacimiento,DPI, sexo)
						values(nomb, apell, dir, ocup, fechaN, DPI, sexo);
        end$$
Delimiter ;

										-- ----------------------------------Actualizar--------------------------------- --
Delimiter $$
	create procedure sp_ActualizarPaciente(in cod int, in DPI varchar(20), in apell varchar(100), in nomb varchar(100), in fechN date, in dir varchar(150), in ocup varchar(50), in sexo varchar(15))
		begin
			update Pacientes set DPI = DPI where codigoPaciente = cod;
            update Pacientes set apellidos = apell where codigoPaciente = cod;
            update Pacientes set nombres = nomb where codigoPaciente = cod;
            update Pacientes set fechaNacimiento = fechN where codigoPaciente = cod;
			update Pacientes set direccion = dir where codigoPaciente = cod;
            update Pacientes set ocupacion = ocup where codigoPaciente = cod;
            update Pacientes set sexo = sexo where codigoPaciente = cod;
        end$$
Delimiter ;

										-- -----------------------------------Eliminar--------------------------------- --
Delimiter $$
	create procedure sp_EliminarPaciente(in cod int)
		begin
			delete from Pacientes where codigoPaciente = cod;
        end$$
Delimiter ;
										
										-- -----------------------------------Buscar---------------------------------- --
Delimiter $$
	create procedure sp_BuscarPaciente(in cod int)
		begin
			select codigoPaciente, DPI, apellidos, nombres, fechaNacimiento, edad, direccion, ocupacion, sexo from Pacientes where codigoPaciente = cod;
        end$$
Delimiter ;

										-- -----------------------------------Listar---------------------------------- --
Delimiter $$
	create procedure sp_ListarPacientes()
		begin
			select codigoPaciente, DPI, apellidos, nombres, fechaNacimiento, edad, direccion, ocupacion, sexo from Pacientes;
        end$$
Delimiter ;

													  -- Procedimientos almacenados de ContactoUrgencia --
										-- -----------------------------------Agregar-------------------------------- --
Delimiter $$
	create procedure sp_AgregarContactoUrgencia(in nomb varchar(100), in apell varchar(100), in numeroC varchar(10), in codP int)
		begin
			insert into ContactoUrgencia(nombres, apellidos, numeroContacto, codigoPaciente)
								values(nomb, apell, numeroC, codP);
		end$$
Delimiter ;

										-- ----------------------------------Actualizar----------------------------- --
Delimiter $$
	create procedure sp_ActualizarContactoUrgencia(in cod int, in nomb varchar(100), in apell varchar(100), in numeroC varchar(10))
		begin
			update ContactoUrgencia set nombres = nomb where codigoContactoUrgencia = cod;
 			update ContactoUrgencia set apellidos = apell where codigoContactoUrgencia = cod;
			update ContactoUrgencia set numeroContacto = numeroC where codigoContactoUrgencia = cod;
		end$$
Delimiter ;
                                        
										-- ----------------------------------Eliminar------------------------------ --
Delimiter $$
	create procedure sp_EliminarContactoUrgencia(in cod int)
		begin
			delete from ContactoUrgencia where codigoContactoUrgencia = cod;
		end$$
Delimiter ;

										-- ----------------------------------Buscar-------------------------------- --
Delimiter $$
	create procedure sp_BuscarContactoUrgencia(in cod int)
		begin
			select nombres, apellidos, numeroContacto from ContactoUrgencia where codigoContactoUrgencia = cod;
		end$$
Delimiter ;

										-- ----------------------------------Listar-------------------------------- --
Delimiter $$
	create procedure sp_ListarContactosUrgencia()
		begin
			select codigoContactoUrgencia, nombres, apellidos, numeroContacto, codigoPaciente from ContactoUrgencia;
		end$$
Delimiter ;
                                        
													  -- Procedimientos almacenados de Areas --
										-- -----------------------------------Agregar-------------------------------- --
Delimiter $$
	create procedure sp_AgregarArea(in nombre varchar(45))
		begin
			insert into Areas(nombreArea) values(nombre);
		end$$
Delimiter ;

										-- ----------------------------------Actualizar------------------------------ --
Delimiter $$
	create procedure sp_ActualizarArea(in cod int, in nombre varchar(45))
		begin
			update Areas set nombreArea = nombre where codigoArea = cod;
		end$$
Delimiter ;

										-- ----------------------------------Eliminar--------------------------------- --
Delimiter $$
	create procedure sp_EliminarArea(in cod int)
		begin
			delete from Areas where codigoArea = cod;
		end$$
Delimiter ;

										-- -----------------------------------Buscar---------------------------------- --
Delimiter $$
	create procedure sp_BuscarArea(in cod int)
		begin
			select codigoArea, nombreArea from Areas where codigoArea = cod;
		end$$
Delimiter ;

										-- -----------------------------------Listar---------------------------------- --
Delimiter $$
	create procedure sp_ListarAreas()
		begin
			select codigoArea, nombreArea from Areas;
		end$$
Delimiter ;
													  -- Procedimientos almacenados de Cargos --
										-- -----------------------------------Agregar-------------------------------- --
Delimiter $$
	create procedure sp_AgregarCargo(in nombreC varchar(45))
		begin
			insert into Cargos(nombreCargo) values(nombreC);
		end$$
Delimiter ;

										-- -----------------------------------Actualizar------------------------------- --
Delimiter $$
	create procedure sp_ActualizarCargo(in cod int, in nombreC varchar(45))
		begin
			update Cargos set nombreCargo = nombreC where codigoCargo = cod;
		end$$
Delimiter ;                  
						
										-- ------------------------------------Borrar----------------------------------- --
Delimiter $$
	create procedure sp_EliminarCargo(in cod int)
		begin
			delete from Cargos where codigoCargo = cod;
		end$$
Delimiter ;                                        

										-- ------------------------------------Buscar----------------------------------- --
Delimiter $$
	create procedure sp_BuscarCargos(in cod int)
		begin
			select codigoCargo, nombreCargo from Cargos where codigoCargo = cod;
		end$$
Delimiter ;

										-- -------------------------------------Listar---------------------------------- --
Delimiter $$
	create procedure sp_ListarCargos()
		begin
			select codigoCargo, nombreCargo from Cargos;
		end$$
Delimiter ;                                        
													  -- Procedimientos almacenados de Medicos --
										-- -----------------------------------Agregar-------------------------------- --
Delimiter $$
	create procedure sp_AgregarMedico(in licM int, in nomb varchar(100), in apell varchar(100), in horaE varchar(10), in horaS varchar(10), in sexo varchar(20))
		begin
			insert into Medicos(licenciaMedica, nombres, apellidos, horaEntrada, horaSalida, sexo)
						values(licM, nomb, apell, horaE, horaS, sexo);
		end$$
Delimiter ; 
                                        -- ----------------------------------Actualizar------------------------------ --
Delimiter $$
	create procedure sp_ActualizarMedico(in cod int,in lic int, in nomb varchar(100), in apell varchar(100), in horaE varchar(10), in horaS varchar(10),in sexo varchar(20))
		begin
			update Medicos set licenciaMedica = lic where codigoMedico = cod;
		    update Medicos set nombres = nomb where codigoMedico = cod;		
			update Medicos set apellidos = apell where codigoMedico = cod;
            update Medicos set horaEntrada = horaE where codigoMedico = cod;
            update Medicos set horaSalida = horaS where codigoMedico = cod;
            update Medicos set sexo = sexo where codigoMedico = cod;
		end$$
Delimiter ;                 

										-- -----------------------------------Eliminar------------------------------- --
Delimiter $$
	create procedure sp_EliminarMedico(in cod int)
		begin
			delete from Medicos where codigoMedico = cod;
		end$$
Delimiter ; 					

										-- ------------------------------------Buscar-------------------------------- --
Delimiter $$
	create procedure sp_BuscarMedico(in cod int)
		begin
			select codigoMedico, licenciaMedica, nombres, apellidos, horaEntrada, horaSalida, turnoMaximo, sexo from Medicos where codigoMedico = cod;
		end$$
Delimiter ;  

										-- ------------------------------------Listar-------------------------------- --
Delimiter $$
	create procedure sp_ListarMedicos()
		begin
			select codigoMedico, licenciaMedica, nombres, apellidos, horaEntrada, horaSalida,turnoMaximo, sexo from Medicos;
		end$$
Delimiter ;       

														  -- Procedimientos almacenados de ResponsableTurno --
										-- -----------------------------------Agregar-------------------------------- --		
Delimiter $$
	create procedure sp_AgregarResponsableTurno(in nomb varchar(75), in apell varchar(75), in tel varchar(10), in codA int, in codC int)
		begin
			insert into ResponsableTurno(nombreResponsable, apellidoResponsable, telefonoPersonal, codigoArea, codigoCargo) 
						  values(nomb, apell, tel, codA, codC);
		end$$
Delimiter ;        

										-- ----------------------------------Actualizar------------------------------ --
Delimiter $$
	create procedure sp_ActualizarResponsableTurno(in cod int, in nomb varchar(75), in apell varchar(75), in tel varchar(10))
		begin
			update ResponsableTurno set nombreResponsable = nomb where codigoResponsableTurno = cod;
		    update ResponsableTurno set apellidoResponsable = apell where codigoResponsableTurno = cod;
			update ResponsableTurno set telefonoPersonal = tel where codigoResponsableTurno = cod;
        end$$
Delimiter ;

										-- ----------------------------------Horario-------------------------------- --
Delimiter $$
	create procedure sp_EliminarResponsableTurno(in cod int)
		begin
			delete from ResponsableTurno where codigoResponsableTurno = cod;
        end$$
Delimiter ;                            
                
										-- ----------------------------------Buscar--------------------------------- --
Delimiter $$
	create procedure sp_BuscarResponsableTurno(in cod int)
		begin
			select codigoResponsableTurno, nombreResponsable, apellidoResponsable, telefonoPersonal, codigoArea, codigoCargo from ResponsableTurno where codigoResponsableTurno = cod;
        end$$
Delimiter ;									

										-- ----------------------------------Listar---------------------------------- --
Delimiter $$
	create procedure sp_ListarResponsablesTurno()
		begin
			select codigoResponsableTurno, nombreResponsable, apellidoResponsable, telefonoPersonal, codigoArea, codigoCargo from responsableturno; 
        end$$
Delimiter ;

														  -- Procedimientos almacenados de TelefonosMedico --
										-- -----------------------------------Agregar-------------------------------- --		
Delimiter $$
	create procedure sp_AgregarTelefonoMedico(in telp varchar(15), in telt varchar(15), in codM int)
		begin
			insert into TelefonosMedico(telefonoPersonal, telefonoTrabajo, codigoMedico) 
						  values(telp, telt, codM);
		end$$
Delimiter ;        

										-- ----------------------------------Actualizar------------------------------ --
Delimiter $$
	create procedure sp_ActualizarTelefonoMedico(in cod int, in telp varchar(15), in telt varchar(15))
		begin
			update TelefonosMedico set telefonoPersonal = telp where codigoTelefonoMedico = cod;
		    update TelefonosMedico set telefonoTrabajo = telt where codigoTelefonoMedico = cod;
        end$$
Delimiter ;

										-- ----------------------------------Horario-------------------------------- --
Delimiter $$
	create procedure sp_EliminarTelefonoMedico(in cod int)
		begin
			delete from TelefonosMedico where codigoTelefonoMedico = cod;
        end$$
Delimiter ;                            
                
										-- ----------------------------------Buscar--------------------------------- --
Delimiter $$
	create procedure sp_BuscarTelefonoMedico(in cod int)
		begin
			select codigoTelefonoMedico, telefonoPersonal, telefonoTrabajo, codigoMedico from TelefonosMedico where codigoTelefonoMedico = cod;
        end$$
Delimiter ;									

										-- ----------------------------------Listar---------------------------------- --
Delimiter $$
	create procedure sp_ListarTelefonosMedico()
		begin
			select codigoTelefonoMedico, telefonoPersonal, telefonoTrabajo, codigoMedico from TelefonosMedico; 
        end$$
Delimiter ;

														  -- Procedimientos almacenados de Especialidades --
										-- -----------------------------------Agregar-------------------------------- --		
Delimiter $$
	create procedure sp_AgregarEspecialidad(in nomb varchar(45))
		begin
			insert into Especialidades(nombreEspecialidad) 
						  values(nomb);
		end$$
Delimiter ;    

										-- ----------------------------------Actualizar------------------------------ --
Delimiter $$
	create procedure sp_ActualizarEspecialidad(in cod int, in nomb varchar(45))
		begin
			update Especialidades set nombreEspecialidad = nomb where codigoEspecialidad = cod;

        end$$
Delimiter ;

										-- ----------------------------------Horario-------------------------------- --
Delimiter $$
	create procedure sp_Especialidad(in cod int)
		begin
			delete from Especialidad where codigoEspecialidad = cod;
        end$$
Delimiter ;                            
                
										-- ----------------------------------Buscar--------------------------------- --
Delimiter $$
	create procedure sp_BuscarEspecialidad(in cod int)
		begin
			select codigoEspecialidad, nombreEspecialidad from Especialidades where codigoEspecialidad = cod;
        end$$
Delimiter ;									

										-- ----------------------------------Listar---------------------------------- --
Delimiter $$
	create procedure sp_ListarEspecialidades()
		begin
			select codigoEspecialidad, nombreEspecialidad from Especialidades; 
        end$$
Delimiter ;
														  -- Procedimientos almacenados de Medico_Especialidad --
										-- -----------------------------------Agregar-------------------------------- --		
Delimiter $$
	create procedure sp_AgregarMedicoEspecialidad(in codigoMedico int, in codigoEspecialidad int, in codigoHorario int)
		begin
			insert into Medico_Especialidad(codigoMedico, codigoEspecialidad, codigoHorario) 
						  values(codigoMedico, codigoEspecialidad, codigoHorario);
		end$$
Delimiter ;        

										-- ----------------------------------Actualizar------------------------------ --
Delimiter $$
	create procedure sp_ActualizarMedicoEspecialidad(in cod int, in codigoMed int, in codigoEsp int, in codigoH int)
		begin
			update Medico_Especialidad set codigoMedico = codigoMed where codigoMedicoEspecialidad = cod;
		    update Medico_Especialidad set codigoEspecialidad = codigoEsp where codigoMedicoEspecialidad = cod;
			update Medico_Especialidad set codigoHorario = codigoH where codigoMedicoEspecialidad = cod;
        end$$
Delimiter ;

										-- ----------------------------------Horario-------------------------------- --
Delimiter $$
	create procedure sp_EliminarMedicoEspecialidad(in cod int)
		begin
			delete from Medico_Especialidad where codigoMedicoEspecialidad = cod;
        end$$
Delimiter ;                            
                
										-- ----------------------------------Buscar--------------------------------- --
Delimiter $$
	create procedure sp_BuscarMedicoEspecialidad(in cod int)
		begin
			select codigoMedicoEspecialidad, codigoMedico, codigoEspecialidad, codigoHorario from Medico_Especialidad where codigoMedicoEspecialidad = cod;
        end$$
Delimiter ;									
										-- ----------------------------------Listar---------------------------------- --
Delimiter $$
	create procedure sp_ListarMedicoEspecialidad()
		begin
			select codigoMedicoEspecialidad, codigoMedico, codigoEspecialidad, codigoHorario from Medico_Especialidad; 
        end$$
Delimiter ;

														  -- Procedimientos almacenados de Horarios --
										-- -----------------------------------Agregar-------------------------------- --		
Delimiter $$
	create procedure sp_AgregarHorario(in horaEntrada varchar(10), in horaSalida varchar(10), in lunes boolean, in martes boolean, in miercoles boolean, in jueves boolean, in viernes boolean)
		begin
			insert into Horarios(horaEntrada, horaSalida, lunes, martes, miercoles, jueves, viernes) 
						  values(horaEntrada, horaSalida, lunes, martes, miercoles, jueves, viernes);
		end$$
Delimiter ;        

										-- ----------------------------------Actualizar------------------------------ --
Delimiter $$
	create procedure sp_ActualizarHorario(in cod int, in horaEntrada varchar(10), in horaSalida varchar(10), in lunes boolean, in martes boolean, in miercoles boolean, in jueves boolean, in viernes boolean)
		begin
			update Horarios set horaEntrada = horaEntrada where codigoHorario = cod;
		    update Horarios set horaSalida = horaSalida where codigoHorario = cod;
			update Horarios set lunes = lunes where codigoHorario = cod;
			update Horarios set martes = martes where codigoHorario = cod;
		    update Horarios set miercoles = miercoles where codigoHorario = cod;
			update Horarios set jueves = jueves where codigoHorario = cod;
			update Horarios set viernes = viernes where codigoHorario = cod;
        end$$
Delimiter ;

										-- ----------------------------------Horario-------------------------------- --
Delimiter $$
	create procedure sp_EliminarHorario(in cod int)
		begin
			delete from Horarios where codigoHorario = cod;
        end$$
Delimiter ;                            
                
										-- ----------------------------------Buscar--------------------------------- --
Delimiter $$
	create procedure sp_BuscarHorario(in cod int)
		begin
			select codigoHorario, horaEntrada, horaSalida, lunes, martes, miercoles, jueves, viernes from Horarios where codigoHorario = cod;
        end$$
Delimiter ;									

										-- ----------------------------------Listar---------------------------------- --
Delimiter $$
	create procedure sp_ListarHorarios()
		begin
			select codigoHorario, horaEntrada, horaSalida, lunes, martes, miercoles, jueves, viernes from Horarios; 
        end$$
Delimiter ;

														  -- Procedimientos almacenados de Turno --
										-- -----------------------------------Agregar-------------------------------- --		
Delimiter $$
	create procedure sp_AgregarTurno(in fechaTurno datetime, in fechaCita datetime, in valorCita decimal(6, 2), in codigoResponsableTurno int, in codigoMedicoEspecialidad int, in codigoPaciente int)
		begin
			insert into Turnos(fechaTurno, fechaCita, valorCita, codigoResponsableTurno, codigoMedicoEspecialidad, codigoPaciente) 
						  values(fechaTurno, fechaCita, valorCita, codigoResponsableTurno, codigoMedicoEspecialidad, codigoPaciente);
		end$$
Delimiter ;        
-- ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

										-- ----------------------------------Actualizar------------------------------ --
Delimiter $$
	create procedure sp_ActualizarTurno(in cod int, in fechaTurno datetime, in fechaCita datetime, in valorCita decimal(10, 2))
		begin
			update Turno set fechaTurno = fechaTurno where codigoHorario = cod;
		    update Turno set fechaCita = fechaCita where codigoHorario = cod;
			update Turno set valorCita = valorCita where codigoHorario = cod;
		end$$
Delimiter ;
										-- ----------------------------------Eliminar-------------------------------- --
Delimiter $$
	create procedure sp_EliminarTurno(in cod int)
		begin
			delete from Turnos where codigoTurno = cod;
        end$$
Delimiter ;                            
                
										-- ----------------------------------Buscar--------------------------------- --
Delimiter $$
	create procedure sp_BuscarTurno(in cod int)
		begin	
			select fechaTurno, fechaCita, valorCita, codigoResponsableTurno, codigoMedicoEspecialidad, codigoPaciente from Turnos where codigoTurno = cod;
        end$$
Delimiter ;									

										-- ----------------------------------Listar---------------------------------- --
Delimiter $$
	create procedure sp_ListarTurnos()
		begin
			select codigoTurno, fechaTurno, fechaCita, valorCita, codigoResponsableTurno, codigoMedicoEspecialidad, codigoPaciente from Turnos; 
        end$$
Delimiter ;


										-- ------------------------Inner join's para los reportes--------------------- --
Delimiter $$
	create procedure sp_InformacionMedicos()
		begin
			select M.codigoMedico, M.nombres, M.apellidos, M.horaEntrada, M.horaSalida, M.turnoMaximo, T.telefonoPersonal from Medicos M inner join TelefonosMedico T
            on M.codigoMedico = T.codigoMedico;
        end$$
Delimiter ;

Delimiter $$
	create procedure sp_InformacionPacientes()
		begin
			select P.codigoPaciente, P.nombres, P.apellidos, P.edad, P.direccion, C.numeroContacto from Pacientes P inner join ContactoUrgencia C
            on P.codigoPaciente = C.codigoPaciente;
        end$$ 
Delimiter ;

Delimiter $$
	create procedure sp_InformacionEncargadoTurno()
		begin
			select R.codigoResponsableTurno, R.nombreResponsable, R.apellidoResponsable, R.telefonoPersonal, A.nombreArea, C.nombreCargo from ResponsableTurno R inner join Areas A
            on R.codigoArea = A.codigoArea inner join Cargos c on R.codigoCargo = C.codigoCargo;
        end$$
Delimiter ;

Delimiter $$
	create procedure sp_InformacionMedicoEspecialidad()
		begin
			select P.codigoMedicoEspecialidad, M.codigoMedico, M.nombres, M.apellidos, H.horaEntrada, H.horaSalida, E.nombreEspecialidad from Medico_especialidad P inner join Medicos M
            on P.codigoMedico = M.codigoMedico inner join Horarios H  on H.codigoHorario = P.codigoHorario inner join Especialidades E on E.codigoEspecialidad = P.codigoEspecialidad;
		end$$
Delimiter ;

Delimiter $$
	create procedure sp_InformacionTurno()
		begin
			select T.codigoTurno, T.fechaCita, T.valorCita, M.nombres, M.apellidos, E.nombreEspecialidad, P.nombres, P.apellidos from Pacientes P inner join Turnos T 
            on P.codigoPaciente = T.codigoPaciente inner join Medico_especialidad X on X.codigoMedicoEspecialidad = T.codigoMedicoEspecialidad inner join Medicos M
            on M.codigoMedico = X.codigoMedico inner join Especialidades E on E.codigoEspecialidad = X.codigoEspecialidad;
        end$$
Delimiter ;

-- Procedimientos de ControlCitas --
Delimiter $$
	create procedure sp_AgregarControlCita(in fech date, in horaI varchar(45), in horaF varchar(45), in codMedico int, in codPaciente int)
		begin
			insert into ControlCitas(fecha, horaInicio, horaFin, codigoMedico, codigoPaciente)
						values(fech, horaI, horaF, codMedico, codPaciente);
        end$$
Delimiter ;

Delimiter $$
	create procedure sp_EliminarControlCita(in cod int)
		begin
			delete from ControlCitas where codigoControlCita = cod;
        end$$
Delimiter ;

Delimiter $$
	create procedure sp_ListarControlCitas()
		begin
			select codigoControlCita, fecha, horaInicio, horaFin, codigoMedico, codigoPaciente from ControlCitas;
        end$$
Delimiter ;

Delimiter $$
	create procedure sp_BuscarControlCita(in cod int)
		begin
			select codigoControlCita, fecha, horaInicio, horaFin, codigoMedico, codigoPaciente from ControlCitas where codigoControlCita = cod;
		end$$
Delimiter ;


Delimiter $$
	create procedure sp_ActualizarControlCita(in cod int, in fech date, in horaI varchar(45), in horaS varchar(45))
		begin
			update controlcitas set fecha = fech where codigoControlCita = cod;
            update controlcitas set horaInicio = horaI where codigoControlCita = cod;
            update controlcitas set horaFin = horaS where codigoControlCita = cod;
        end$$
Delimiter ;

							-- Procedimientos Recetas --
Delimiter $$
	create procedure sp_AgregarReceta(in descripcion varchar(45), in codigoC int)
		begin
			insert into Recetas(descripcionReceta, codigoControlCita)
					values(descripcion, codigoC);
        end$$
Delimiter ;

Delimiter $$
	create procedure sp_EliminarReceta(in cod int)
		begin
			delete from Recetas where codigoReceta = cod;
		end$$
Delimiter ;

Delimiter $$
	create procedure sp_ListarRecetas()
		begin
			select codigoReceta, descripcionReceta, codigoControlCita from Recetas;
        end$$
Delimiter ;

Delimiter $$
	create procedure sp_BuscarReceta(in cod int)
		begin
			select codigoReceta, descripcionReceta, codigoControlCita from Recetas where codigoReceta = cod;
        end$$
Delimiter ;

Delimiter $$
	create procedure sp_ActualizarReceta(in cod int, in descripcion varchar(45))
		begin
			update recetas set descripcionReceta = descripcion where codigoReceta = cod;
        end$$
Delimiter ;

								-- Inner join del reporte final --
Delimiter $$
	create procedure sp_InformacionReceta(in codigo int)
		begin 
			select P.nombres as primero, P.apellidos as primero1, P.edad, M.nombres as segundo, M.apellidos as segundo1, R.descripcionReceta, C.fecha
            from Pacientes P inner join ControlCitas C on P.codigoPaciente = C.codigoPaciente inner join Medicos M on M.codigoMedico = C.codigoMedico
            inner join Recetas R on R.codigoControlCita = C.codigoControlCita where P.codigoPaciente = codigo;
		end$$
Delimiter ;