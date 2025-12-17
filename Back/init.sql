CREATE TABLE INSTITUTION(
    id_institution SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    location TEXT NOT NULL
);
CREATE TABLE USERSYNCADIA(
    id_User SERIAL PRIMARY KEY,
    firstname TEXT NOT NULL,
    lastname TEXT NOT NULL,
    username TEXT NOT NULL,
    password TEXT NOT NULL,
    id_institution INT REFERENCES INSTITUTION(id_institution)
);
CREATE TABLE TASK(
    id_Task SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    firstDelivery DATE,
    secondDelivery DATE,
    id_User INT REFERENCES USERSYNCADIA(id_User)
);


CREATE TABLE ACCESSRIGHT(
    accessRight INT NOT NULL,
    id_User INT REFERENCES USERSYNCADIA(id_User)ON DELETE CASCADE,
    PRIMARY KEY (accessRight, id_User)
);

CREATE TABLE TERMS(
    id_Terms SERIAL PRIMARY KEY,
    code TEXT NOT NULL
);

CREATE TABLE HOURS_PER_STUDENT(
    id_HoursPerStudent SERIAL PRIMARY KEY,
    cm INT ,
    td INT,
    tp INT 
);

CREATE TABLE PATH(
    id_Path SERIAL PRIMARY KEY,
    number INT NOT NULL,
    label TEXT NOT NULL
);

CREATE TABLE UE(
    UE_Number SERIAL PRIMARY KEY,
    EuApogeeCode TEXT NOT NULL,
    label TEXT NOT NULL,
    competenceLevel INT NOT NULL,
    id_Path INT REFERENCES PATH(id_Path)
);

CREATE TABLE UE_COEFFICIENT(
    id_Coefficient SERIAL PRIMARY KEY,
    coefficient INT NOT NULL,
    UE_Number INT REFERENCES UE(UE_Number)
);

CREATE TABLE RESSOURCE(
    id_Ressource SERIAL PRIMARY KEY,
    apogeeCode TEXT NOT NULL,
    label TEXT NOT NULL,
    diffMultiCompetences BOOLEAN NOT NULL,
    id_Terms INT REFERENCES TERMS(id_Terms),
    id_HoursPerStudent INT REFERENCES HOURS_PER_STUDENT(id_HoursPerStudent),
    id_Coefficient INT REFERENCES UE_COEFFICIENT(id_Coefficient)
);

CREATE TABLE PEDAGOGICAL_CONTENT(
    id_PedagogicalContent SERIAL PRIMARY KEY,
    cm TEXT,
    td TEXT,
    tp TEXT
);

CREATE TABLE RESSOURCE_TRACKING(
    id_RessourceTracking SERIAL PRIMARY KEY,
    pedagogicalFeedback TEXT,
    studentFeedback TEXT,
    improvementSuggestions TEXT
);

CREATE TABLE RESSOURCE_SHEET(
    id_RessourceSheet SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    competence TEXT NOT NULL,
    SAE TEXT,
    year DATE,
    id_Ressource INT REFERENCES RESSOURCE(id_Ressource),
    id_PedagogicalContent INT REFERENCES PEDAGOGICAL_CONTENT(id_PedagogicalContent),
    id_RessourceTracking INT REFERENCES RESSOURCE_TRACKING(id_RessourceTracking),
    id_User INT REFERENCES USERSYNCADIA(id_User)
);

INSERT INTO INSTITUTION (name, location) VALUES
('INFORMATIQUE', 'IUT du Limousin'),
('GEA', 'IUT de Guéret'),
('machin', 'IUT de Brive');

INSERT INTO USERSYNCADIA ( firstname, lastname, username, password, id_institution) VALUES
( 'Anais', 'Poursat', 'apoursat', 'Password123', 1),
( 'Tux', 'Linus', 'tlinus', 'TuxLePlusCool_1991_B)', 2),
('Thomas', 'Hugel', 'thugel', 'FanDeTux:)', 2),
('Max', 'Gorce', 'mgorce', 'SlayKing', 3);

INSERT INTO TASK (name, description, firstDelivery, secondDelivery, id_User) VALUES
('remplir fiche ressource', 'vous devez remplir la fiche ressource pour chaque ressource pédagogique que vous créez', '2026-01-30', '2026-07-15', 1),
('mettre à jour fiche ressource', 'vous devez mettre à jour la fiche ressource pour chaque ressource pédagogique que vous modifiez', '2026-01-30', '2026-08-15', 3);

INSERT INTO ACCESSRIGHT ( id_User,accessRight) VALUES
(1, 1),
(1, 2),
(1, 3),
(2, 2),
(3, 1),
(4, 3);

INSERT INTO TERMS(code) VALUES
('NGCC'),
('NGCC'),
('NGCC');

INSERT INTO HOURS_PER_STUDENT (cm, td, tp) VALUES
(6, 10, 20),
(4, 10, 16),
(10, 8, 10);

INSERT INTO PATH(number, label) VALUES
(1, 'BUT INFORMATIQUE parcours réalisation d''applications : conception, développement, validation');

INSERT INTO UE(EuApogeeCode, label, competenceLevel, id_Path) VALUES
('TIN0101U', 'UE1.1 - Réaliser',1, 1),
('TIN0102U', 'UE1.2 - Optimiser',1, 1),
('TIN0103U', 'UE1.3 - Administrer',1, 1);

INSERT INTO UE_COEFFICIENT(coefficient, UE_Number) VALUES
(3, 1),
(4, 2),
(2, 3);

INSERT INTO RESSOURCE(apogeeCode, label, diffMultiCompetences, id_Terms, id_HoursPerStudent, id_Coefficient) VALUES
('TIN01A1M', 'R1.01 Initiation au développement', FALSE, 1, 1, 1),
('TIN01A2M', 'R1.02 Développement d''interfaces web', FALSE, 2, 2, 2),
('TIN01A3M', 'R1.03 Introduction à l''architecture des ordinateurs', FALSE, 3, 3, 3);

INSERT INTO PEDAGOGICAL_CONTENT(cm, td, tp) VALUES
('1 Ennui ultime du python,2 pourquoi le prof il fait des dessins frr?,3 j''abandonne c''est trop là', '1 waw en vrai c''est ok de fou,2 euh ok c''est chelou mais ok ig,3 c''est quoi cette merde là laissez moi mourir', '1 hello world,2 bibliothèque des pleurs,3 putain c''est quoi pickle ;-;'),
(NULL, '1 oww un petit chat,2 mmm redirections? c''est quoi ça?', '1 euh pourquoi le niveau il a triplé, 2 MAIS C''EST QUOI LE CSS DE MERDE,3 NON'),
('1 waw les bits,2 mm pas cool les bits','1 pourquoi j''apprend ça même,2 aled','1 non laisse mo rentrer chez moi,2 non juste par pitié,3 pourquoi je m''inflige ça même');

INSERT INTO RESSOURCE_TRACKING(pedagogicalFeedback, studentFeedback, improvementSuggestions) VALUES
('Besoin de plus d''exemples','Trop facile','Ajouter des exercices'),
('Parfait tel quel','Trop dur','Rien à ajouter'),
('Bien structuré','Correct','Ajouter des vidéos explicatives');

INSERT INTO RESSOURCE_SHEET(name, competence, SAE, year, id_Ressource, id_PedagogicalContent, id_RessourceTracking, id_User) VALUES
('R1.01', 'Développement de base', 'SAE1', '2025-09-01', 1, 1, 1, 1),
('R1.02', 'Développement web', 'SAE2', '2025-09-01', 2, 2, 2, 3),
('R1.03', 'Architecture des ordinateurs', 'SAE3', '2025-09-01', 3, 3, 3, 3);

--- New version of the database schema
CREATE TABLE INSTITUTION(
    id_institution SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    location TEXT NOT NULL
);
CREATE TABLE USERSYNCADIA(
    id_User SERIAL PRIMARY KEY,
    firstname TEXT NOT NULL,
    lastname TEXT NOT NULL,
    username TEXT NOT NULL,
    password TEXT NOT NULL,
    id_institution INT REFERENCES INSTITUTION(id_institution)
);
CREATE TABLE TASK(
    id_Task SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    delivery DATE,
    id_User INT REFERENCES USERSYNCADIA(id_User)
);

CREATE TABLE FINAL_DELIVERY_DATES(
    id_FinalDelivery SERIAL PRIMARY KEY,
    firstDelivery DATE,
    secondDelivery DATE,
);

CREATE TABLE ACCESSRIGHT(
    accessRight INT NOT NULL,
    id_User INT REFERENCES USERSYNCADIA(id_User)ON DELETE CASCADE,
    PRIMARY KEY (accessRight, id_User)
);


CREATE TABLE PATH(
    id_Path SERIAL PRIMARY KEY,
    number INT NOT NULL,
    name TEXT NOT NULL
);

CREATE TABLE UE(
    UE_Number SERIAL PRIMARY KEY,
    EuApogeeCode TEXT NOT NULL,
    label TEXT NOT NULL,
    name TEXT NOT NULL,
    competenceLevel INT NOT NULL,
    id_Path INT REFERENCES PATH(id_Path)
);

CREATE TABLE TERMS(
    id_Terms SERIAL PRIMARY KEY,
    code TEXT NOT NULL,
);

CREATE TABLE SAE(
    id_SAE SERIAL PRIMARY KEY,
    label TEXT NOT NULL,
    id_terms INT REFERENCES TERMS(id_Terms)
)

CREATE TABLE RESSOURCE(
    id_Ressource SERIAL PRIMARY KEY,
    apogeeCode TEXT NOT NULL,
    label TEXT NOT NULL,
    name TEXT NOT NULL,
    diffMultiCompetences BOOLEAN NOT NULL,
    semester INT NOT NULL,
    id_terms INT REFERENCES TERMS(id_Terms),
);

CREATE TABLE SAE_LINK_RESSOURCE(
    id_SAE INT REFERENCES SAE(id_SAE),
    id_ressource INT REFERENCES RESSOURCE(id_Ressource)
);

CREATE TABLE HOURS_PER_STUDENT(
    id_HoursPerStudent SERIAL PRIMARY KEY,
    has_alternance BOOLEAN NOT NULL,
    cm INT ,
    td INT,
    tp INT,
    id_ressource INT REFERENCES RESSOURCE(id_Ressource)
);

CREATE TABLE SAE_HOURS_SAE(
    id_SAE_Hours SERIAL PRIMARY KEY,
    hours INT NOT NULL,
    id_SAE INT REFERENCES SAE(id_SAE)
);

CREATE TABLE UE_COEFFICIENT_RESSOURCE(
    id_Coefficient SERIAL PRIMARY KEY,
    coefficient INT NOT NULL,
    UE_Number INT REFERENCES UE(UE_Number),
    id_ressource INT REFERENCES RESSOURCE(id_Ressource)
);

CREATE TABLE UE_COEFFICIENT_SAE(
    id_Coefficient_SAE SERIAL PRIMARY KEY,
    coefficient INT NOT NULL,
    UE_Number INT REFERENCES UE(UE_Number),
    id_SAE INT REFERENCES SAE(id_SAE)
);

CREATE TABLE RESSOURCE_SHEET(
    id_RessourceSheet SERIAL PRIMARY KEY,
    year DATE,
    id_Ressource INT REFERENCES RESSOURCE(id_Ressource),
    id_User INT REFERENCES USERSYNCADIA(id_User)
);

CREATE TABLE NATIONAL_PROGRAM_OBJECTIVE(
    id_NationalProgramObjective SERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    id_RessourceSheet INT REFERENCES RESSOURCE_SHEET(id_RessourceSheet)
);

CREATE TABLE NATIONAL_PROGRAM_SKILL(
    id_Skill SERIAL PRIMARY KEY,
    description TEXT NOT NULL,
    id_RessourceSheet INT REFERENCES RESSOURCE_SHEET(id_RessourceSheet)
);

CREATE TABLE PEDAGOGICAL_CONTENT(
    id_PedagogicalContent SERIAL PRIMARY KEY,
    cm TEXT,
    td TEXT,
    tp TEXT,
    id_RessourceSheet INT REFERENCES RESSOURCE_SHEET(id_RessourceSheet)
);

CREATE TABLE RESSOURCE_TRACKING(
    id_RessourceTracking SERIAL PRIMARY KEY,
    pedagogicalFeedback TEXT,
    studentFeedback TEXT,
    improvementSuggestions TEXT,
    id_RessourceSheet INT REFERENCES RESSOURCE_SHEET(id_RessourceSheet)
);

CREATE TABLE KEYWORD(
    keyword TEXT NOT NULL,
    id_RessourceSheet INT REFERENCES RESSOURCE_SHEET(id_RessourceSheet)
);

INSERT INTO INSTITUTION (name, location) VALUES
('INFORMATIQUE', 'IUT du Limousin'),
('GEA', 'IUT de Guéret'),
('MMI', 'IUT de Brive'),
('TEST', 'IUT de Test');

INSERT INTO USERSYNCADIA ( firstname, lastname, username, password, id_institution) VALUES
( 'Anais', 'Poursat', 'apoursat', 'Password123', 1),
( 'Tux', 'Linus', 'tlinus', 'TuxLePlusCool_1991_B)', 2),
('Thomas', 'Hugel', 'thugel', 'FanDeTux:)', 2),
('Max', 'Gorce', 'mgorce', 'SlayKing', 3),
('prof','test','ptest','test123',4),
('administration','test','atest','test123',4);

INSERT INTO TASK (name, description, firstDelivery, secondDelivery, id_User) VALUES
('remplir fiche ressource', 'vous devez remplir la fiche ressource pour chaque ressource pédagogique que vous créez', '2026-01-30', '2026-07-15', 1),
('mettre à jour fiche ressource', 'vous devez mettre à jour la fiche ressource pour chaque ressource pédagogique que vous modifiez', '2026-01-30', '2026-08-15', 3);

INSERT INTO FINAL_DELIVERY_DATES ( firstDelivery, secondDelivery) VALUES
('2026-01-30', '2026-07-15');

INSERT INTO ACCESSRIGHT ( id_User,accessRight) VALUES
(1, 1),
(1, 2),
(1, 3),
(2, 2),
(3, 1),
(4, 3),
(5, 1),
(6, 2);


INSERT INTO PATH(number, name) VALUES
(1, 'BUT INFORMATIQUE parcours réalisation d''applications : conception, développement, validation'),
(2, 'parcours de test');

INSERT INTO UE(EuApogeeCode, label, name, competenceLevel, id_Path) VALUES
('TIN0101U', 'UE1.1','Réaliser',1, 1),
('TIN0102U', 'UE1.2','Optimiser',1, 1),
('TIN0103U', 'UE1.3','Administrer',1, 1);

INSERT INTO TERMS(code,id_ressource) VALUES
('NGCC', 1),
('NGCC', 2),
('NGCC', 3),
('NGCC', 4),
('NGCC', 5),
('NGCC', 6),
('NGCC', 7),
('NGCC', 8),
('NGCC', 9),
('NGCC', 10);
INSERT INTO SAE(label, id_terms) VALUES
('SAE 1', 5),
('SAE 2', 6),
('SAE 3', 7),
('SAE 4', 8),
('SAE 5', 9),
('SAE 6', 10);


INSERT INTO RESSOURCE(apogeeCode, label,name, diffMultiCompetences, semester) VALUES
('TIN01A1M', 'R1.01',' Initiation au développement', FALSE, 1),
('TIN01A2M', 'R1.02','Développement d''interfaces web', FALSE, 1),
('TIN01A3M', 'R1.03','Introduction à l''architecture des ordinateurs', FALSE, 1),
('TIN01A4M', 'R2.04','Test de la nouvelle ressource', FALSE, 2);

INSERT INTO SAE_LINK_RESSOURCE(id_SAE, id_ressource) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4);

INSERT INTO HOURS_PER_STUDENT (cm, td, tp,id_ressource) VALUES
(6, 10, 20, 1),
(4, 10, 16, 2),
(10, 8, 10, 3),
(5, 5, 5, 4);

INSERT INTO SAE_HOURS_SAE(hours, id_SAE) VALUES
(36, 1),
(30, 2),
(28, 3),
(20, 4);

INSERT INTO UE_COEFFICIENT_RESSOURCE(coefficient, UE_Number,id_ressource) VALUES
(3, 1, 1),
(4, 2, 2),
(2, 3, 3),
(5, 1, 4);
INSERT INTO UE_COEFFICIENT_SAE(coefficient, UE_Number,id_SAE) VALUES
(3, 1, 1),
(4, 2, 2),
(2, 3, 3),
(5, 1, 4);
INSERT INTO RESSOURCE_SHEET( year, id_Ressource, id_User) VALUES
('2025-09-01', 1,  1),
('2025-09-01', 2, 3),
('2025-09-01', 3, 3),
( '2026-01-01', 4, 4);

INSERT INTO NATIONAL_PROGRAM_OBJECTIVE(content, id_RessourceSheet) VALUES
('Comprendre les bases de la programmation', 1),
('Savoir créer des interfaces web', 2),
('Connaître l''architecture des ordinateurs', 3),
('Tester des ressources pédagogiques', 4);

INSERT INTO NATIONAL_PROGRAM_SKILL(description, id_RessourceSheet) VALUES
('Être capable de coder en Python', 1),
('Maîtriser HTML et CSS', 2),
('Comprendre le fonctionnement des processeurs', 3),
('Savoir évaluer une ressource pédagogique', 4);

INSERT INTO PEDAGOGICAL_CONTENT(cm, td, tp,id_ressourceSheet) VALUES
('1 Ennui ultime du python,2 pourquoi le prof il fait des dessins frr?,3 j''abandonne c''est trop là', '1 waw en vrai c''est ok de fou,2 euh ok c''est chelou mais ok ig,3 c''est quoi cette merde là laissez moi mourir', '1 hello world,2 bibliothèque des pleurs,3 putain c''est quoi pickle ;-;',1),
(NULL, '1 oww un petit chat,2 mmm redirections? c''est quoi ça?', '1 euh pourquoi le niveau il a triplé, 2 MAIS C''EST QUOI LE CSS DE MERDE,3 NON',2),
('1 waw les bits,2 mm pas cool les bits','1 pourquoi j''apprend ça même,2 aled','1 non laisse mo rentrer chez moi,2 non juste par pitié,3 pourquoi je m''inflige ça même',3),
(NULL, 'TD de test 1, TD de test 2', 'TP de test 1, TP de test 2',4);

INSERT INTO RESSOURCE_TRACKING(pedagogicalFeedback, studentFeedback, improvementSuggestions,id_ressourceSheet) VALUES
('Besoin de plus d''exemples','Trop facile','Ajouter des exercices',1),
('Parfait tel quel','Trop dur','Rien à ajouter',2),
('Bien structuré','Correct','Ajouter des vidéos explicatives',3),
('Feedback pédagogique de test','Feedback étudiant de test','Suggestions d''amélioration de test',4);

INSERT INTO KEYWORDS(keyword, id_RessourceSheet) VALUES
('programmation', 1),
('python', 1),
('web', 2),
('html', 2),
('architecture', 3),
('ordinateur', 3),
('test', 4),
('ressource pédagogique', 4);