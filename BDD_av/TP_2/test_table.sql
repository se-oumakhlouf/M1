drop table if exists test;

create table test(
    val int
);

INSERT INTO test SELECT mod(i,1000)+1 FROM generate_series(1,1000000) AS s(i);
