-- auto-generated definition
create table RATE_PRICE
(
    ID    NUMBER default ESB_USER.RATE_PRICE_SEQ.NEXTVAL not null
        primary key,
    RATE  VARCHAR2(100 char)                          not null,
    PRICE VARCHAR2(100 char)                          not null
)
/


INSERT INTO ESB_USER.RATE_PRICE (ID, RATE, PRICE) VALUES (4, '40', 'Unlucky');
INSERT INTO ESB_USER.RATE_PRICE (ID, RATE, PRICE) VALUES (2, '40', '10000');
INSERT INTO ESB_USER.RATE_PRICE (ID, RATE, PRICE) VALUES (3, '20', '20000');
INSERT INTO ESB_USER.RATE_PRICE (ID, RATE, PRICE) VALUES (5, '0', '50000');
