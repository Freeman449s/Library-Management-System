create table book (
    bno varchar(15),
    type nvarchar(40) not null,
    title nvarchar(60) not null,
    press nvarchar(40) not null,
    pub_year int not null,
    author nvarchar(60) not null,
    price decimal(7,2) not null,
    total int not null,
    stock int not null,
    primary key (bno),
    check (pub_year > 0),
    check (price > 0),
    check (total > 0),
    check (stock >= 0),
    check (stock <= total)
);

create table card (
    cno varchar(20),
    name nvarchar(60) not null,
    dept nvarchar(40),
    type char(1),
    primary key (cno),
    check (type in ('t','s','o'))
);

create table admin (
    id varchar(20) not null,
    pwd varchar(20) not null,
    name nvarchar(60) not null,
    phone varchar(20) not null,
    primary key (id)
);

create table borrow (
    cno varchar(20) references card not null,
    bno varchar(15) references book not null,
    borrow_date date not null,
    return_date date,
    handler varchar(20) references admin
);