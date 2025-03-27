create sequence categoria_seq start with 1 increment by 50;
create table activo (id integer not null, tamanio integer, nombre varchar(255), tipo varchar(255), url varchar(255), id_producto integer array, primary key (id));
create table activo_categoria (activo_id integer not null, categoria_id integer not null, primary key (activo_id, categoria_id));
create table categoria (id integer not null, nombre varchar(255), primary key (id));
alter table if exists activo_categoria add constraint FKkav4ibf8big7574v7y5dr4vb foreign key (activo_id) references activo;
alter table if exists activo_categoria add constraint FKsbkjhn8qqt7d3187jic7i1iqj foreign key (categoria_id) references categoria;
