create table user_management
(
    id varchar(255) not null primary key,
    account_type varchar(20) not null
        constraint user_management_account_type_check
            check (account_type IN('BASIC', 'PREMIUM', 'STUDENT', 'TEACHER')),
    confirmed boolean not null,
    created_at timestamp(6) with time zone not null,
    email varchar(100) not null
        constraint email_unique unique,
    enabled boolean not null,
    first_name varchar(255),
    last_login timestamp(6) with time zone,
    last_name varchar(255),
    last_password_change timestamp(6) with time zone,
    login_count integer not null,
    password varchar(255) not null,
    updated_at timestamp(6) with time zone not null,
    user_type varchar(20) not null
        constraint user_management_user_type_check
            check (user_type IN ('NORMAL', 'ADMIN')),
    username varchar(50) not null
        constraint username_unique unique
);