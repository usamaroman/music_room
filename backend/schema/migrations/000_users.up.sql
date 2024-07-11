create table if not exists users (
    id serial primary key,
    email varchar(255) not null unique,
    nickname varchar(255) not null unique,
    password text not null,
    created_at timestamp with time zone default now()
)