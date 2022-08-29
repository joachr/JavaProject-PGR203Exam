create table survey
(
    id                  serial primary key,
    name                varchar(100) not null,
    number_of_questions int
);

create table question
(
    id                serial primary key,
    question_name     varchar(100) not null,
    number_of_answers int          not null,
    survey_id         int,
    foreign key (survey_id) references survey
);

create table answer
(
    id          serial primary key,
    name        varchar(100) not null,
    question_id int          not null,
    foreign key (question_id) references question
);

create table users
(
    id               serial primary key,
    first_name       varchar(100) not null,
    last_name        varchar(100) not null,
    email            varchar(320) not null unique,
    last_survey_done int,
    foreign key (last_survey_done) references survey
);
