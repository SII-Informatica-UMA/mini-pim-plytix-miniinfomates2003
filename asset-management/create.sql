create table activo (id integer not null, id_producto integer array, nombre varchar(255), tamanio integer, tipo varchar(255), url varchar(255), primary key (id));
create table activo_categoria (categoria_id integer not null, activo_id integer not null, primary key (categoria_id, activo_id));
create table categoria (id integer not null, nombre varchar(255), primary key (id));
create sequence activo_seq start with 1 increment by 50;
create sequence categoria_seq start with 1 increment by 50;
alter table if exists activo_categoria add constraint FKkav4ibf8big7574v7y5dr4vb foreign key (activo_id) references activo;
alter table if exists activo_categoria add constraint FKsbkjhn8qqt7d3187jic7i1iqj foreign key (categoria_id) references categoria;
