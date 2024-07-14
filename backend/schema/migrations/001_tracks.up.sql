create table if not exists tracks (
    id         serial primary key,
    title      text not null,
    artist     text not null,
    cover      text not null,
    duration   int  not null,
    created_at timestamp with time zone default NOW()
)

