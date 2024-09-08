-- 회원 테이블
create table member (
    id serial primary key,
    email varchar(100) not null,
    password varchar(255) not null,
    name varchar(100) not null,
    created timestamp default current_timestamp,
    updated timestamp default current_timestamp,
    role varchar(20) not null
);

-- 태그 테이블: 회원 테이블 id 참조
create table tag (
     id serial primary key,
     name varchar(100),
     member_id int references member(id) on delete cascade
);