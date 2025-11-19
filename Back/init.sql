CREATE TABLE INSTITUTION(
    id_institution SERIAL PRIMARY KEY,
    name TEXT NOT NULL
)

CREATE TABLE USER(
    id_User SERIAL PRIMARY KEY,
    accessRight IINT NOT NULL,
    firstname TEXT NOT NULL,
    lastname TEXT NOT NULL,
    username TEXT NOT NULL,
    password TEXT NOT NULL,
    id_institution INT REFERENCES INSTITUTION(id_institution)
)

CREATE TABLE TERMS(
    id_Terms SERIAL PRIMARY KEY,
    code TEXT NOT NULL
)

CREATE TABLE HOURS_PER_STUDENT(
    id_HoursPerStudent SERIAL PRIMARY KEY,
    cm INT ,
    td INT,
    tp INT 
)

CREATE TABLE PATH(
    id_Path SERIAL PRIMARY KEY,
    number INT NOT NULL,
    label TEXT NOT NULL
)

CREATE TABLE UE(
    UE_Number SERIAL PRIMARY KEY,
    EuApogeeCode TEXT NOT NULL,
    label TEXT NOT NULL,
    competenceLevel INT NOT NULL,
    id_Path INT REFERENCES PATH(id_Path)
)

CREATE TABLE UE_COEFFICIENT(
    id_Coefficient SERIAL PRIMARY KEY,
    coefficient INT NOT NULL,
    UE_Number INT REFERENCES UE(UE_Number)
)

CREATE TABLE RESSOURCE(
    id_Ressource SERIAL PRIMARY KEY,
    apogeeCode TEXT NOT NULL,
    label TEXT NOT NULL,
    diffMultiCompetences BOOLEAN NOT NULL,
    id_Terms INT REFERENCES TERMS(id_Terms),
    id_HoursPerStudent INT REFERENCES HOURS_PER_STUDENT(id_HoursPerStudent),
    id_Coefficient INT REFERENCES UE_COEFFICIENT(id_Coefficient)
)

CREATE TABLE PEDAGOGICAL_CONTENT(
    id_PedagogicalContent SERIAL PRIMARY KEY,
    cm TEXT,
    td TEXT,
    tp TEXT
)

CREATE TABLE RESSOURCE_TRACKING(
    id_RessourceTracking SERIAL PRIMARY KEY,
    pedagogicalFeedback TEXT,
    studentFeedback TEXT,
    improvementSuggestions TEXT
)

CREATE TABLE RESSOURCE_SHEET(
    id_RessourceSheet SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    competence TEXT NOT NULL,
    SAE TEXT,
    year DATE,
    id_Ressource INT REFERENCES RESSOURCE(id_Ressource),
    id_PedagogicalContent INT REFERENCES PEDAGOGICAL_CONTENT(id_PedagogicalContent),
    id_RessourceTracking INT REFERENCES RESSOURCE_TRACKING(id_RessourceTracking),
    id_User INT REFERENCES USER(id_User)
)

