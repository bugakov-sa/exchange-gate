create schema exchange_gate;

create table exchange_gate.robot(
    id uuid primary key,
    name text not null,
    strategy text not null,
    active boolean not null default false
);

create table exchange_gate.param(
    id uuid primary key,
    name text not null,
    type text not null,
    value text not null,
    robot_id uuid not null,
    constraint fk_job foreign key(robot_id) references exchange_gate.robot(id) on delete cascade
);
