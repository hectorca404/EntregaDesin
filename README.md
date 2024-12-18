Proyecto preparado para que cumpla los siguientes requerimientos:
* Java SDK 23
* MySQL 8.x y una base de datos creada pero vacía de nombre bdtarea3ad preparada en localhost:3306 con usuario root y sin password

Para lanzar el programa, seleccionar:
Run As > Run Configurations... > en el apartado Goals escribir clean javafx:run y pulsar Run 

Se arrancará String Boot, se conectará a la BD bdtarea3ad y aparecerá la GUI de Login. 
Para poder autenticarse, ir a la tabla user de la BD y añadir una nueva fila con:
INSERT INTO `user` (`id`, `dob`, `email`, `first_name`, `gender`, `last_name`, `password`, `role`) VALUES ('1', '2000-01-01', 'admin', 'Admin', '-', NULL, 'admin', 'Admin') 

Así se crean unas credenciales admin/admin que dan acceso a la aplicación.
Desde ahí, ya se pueden insertar nuevos usuarios.
