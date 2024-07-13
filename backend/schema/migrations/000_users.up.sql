create table if not exists users (
    id serial primary key,
    email varchar(255) not null unique,
    nickname varchar(255) not null unique,
    password text not null,
    avatar varchar(255) unique,
    is_active bool default false,
    created_at timestamp with time zone default now()
)

