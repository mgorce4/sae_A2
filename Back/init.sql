
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
    id_institution INT REFERENCES INSTITUTION(id_institution)
);

CREATE TABLE ACCESSRIGHT(
    accessRight INT NOT NULL,
    id_User INT REFERENCES USERSYNCADIA(id_User)ON DELETE CASCADE,
    PRIMARY KEY (accessRight, id_User)
);


CREATE TABLE PATH(
    id_Path SERIAL PRIMARY KEY,
    number INT NOT NULL,
    name TEXT NOT NULL,
    id_institution INT REFERENCES INSTITUTION(id_institution)
);

CREATE TABLE UE(
    UE_Number SERIAL PRIMARY KEY,
    EuApogeeCode TEXT NOT NULL,
    label TEXT NOT NULL,
    name TEXT NOT NULL,
    competenceLevel INT NOT NULL,
    semester INT NOT NULL,
    id_Path INT REFERENCES PATH(id_Path),
    id_terms INT REFERENCES TERMS(id_Terms)
);

CREATE TABLE TERMS(
    id_Terms SERIAL PRIMARY KEY,
    code TEXT NOT NULL
);

CREATE TABLE SAE(
    id_SAE SERIAL PRIMARY KEY,
    label TEXT NOT NULL,
    apogeeCode TEXT,
    semester INT NOT NULL,
    id_terms INT REFERENCES TERMS(id_Terms)
);

CREATE TABLE RESOURCE(
    id_Resource SERIAL PRIMARY KEY,
    apogeeCode TEXT NOT NULL,
    label TEXT NOT NULL,
    name TEXT NOT NULL,
    diffMultiCompetences BOOLEAN NOT NULL,
    semester INT NOT NULL,
    id_terms INT REFERENCES TERMS(id_Terms)
);

CREATE TABLE SAE_LINK_RESOURCE(
    id_SAE INT REFERENCES SAE(id_SAE),
    id_resource INT REFERENCES RESOURCE(id_Resource)
);

CREATE TABLE HOURS_PER_STUDENT(
    id_HoursPerStudent SERIAL PRIMARY KEY,
    has_alternance BOOLEAN NOT NULL,
    cm FLOAT ,
    td FLOAT,
    tp FLOAT,
    id_resource INT REFERENCES RESOURCE(id_Resource)
);

CREATE TABLE SAE_HOURS(
    id_SAE_Hours SERIAL PRIMARY KEY,
    hours FLOAT NOT NULL,
    id_SAE INT REFERENCES SAE(id_SAE),
    has_alternance BOOLEAN NOT NULL
);

CREATE TABLE UE_COEFFICIENT_RESOURCE(
    id_Coefficient SERIAL PRIMARY KEY,
    coefficient FLOAT NOT NULL,
    UE_Number INT REFERENCES UE(UE_Number),
    id_resource INT REFERENCES RESOURCE(id_Resource)
);

CREATE TABLE UE_COEFFICIENT_SAE(
    id_Coefficient_SAE SERIAL PRIMARY KEY,
    coefficient FLOAT NOT NULL,
    UE_Number INT REFERENCES UE(UE_Number),
    id_SAE INT REFERENCES SAE(id_SAE)
);

CREATE TABLE RESOURCE_SHEET(
    id_ResourceSheet SERIAL PRIMARY KEY,
    year DATE,
    id_Resource INT REFERENCES RESOURCE(id_Resource)
);

CREATE TABLE MAIN_TEACHER_FOR_RESOURCE(
    id_User INT REFERENCES USERSYNCADIA(id_User),
    id_Resource INT REFERENCES RESOURCE(id_Resource)
);

CREATE TABLE TEACHERS_FOR_RESOURCE(
    id_User INT REFERENCES USERSYNCADIA(id_User),
    id_Resource INT REFERENCES RESOURCE(id_Resource)
);

CREATE TABLE NATIONAL_PROGRAM_OBJECTIVE(
    id_NationalProgramObjective SERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    id_ResourceSheet INT REFERENCES RESOURCE_SHEET(id_ResourceSheet)
);

CREATE TABLE NATIONAL_PROGRAM_SKILL(
    id_Skill SERIAL PRIMARY KEY,
    label TEXT NOT NULL,
    description TEXT NOT NULL,
    id_ResourceSheet INT REFERENCES RESOURCE_SHEET(id_ResourceSheet)
);

CREATE TABLE PEDAGOGICAL_CONTENT(
    id_PedagogicalContent SERIAL PRIMARY KEY,
    cm TEXT,
    td TEXT,
    tp TEXT,
    ds TEXT,
    id_ResourceSheet INT REFERENCES RESOURCE_SHEET(id_ResourceSheet)
);

CREATE TABLE RESOURCE_TRACKING(
    id_ResourceTracking SERIAL PRIMARY KEY,
    pedagogicalFeedback TEXT,
    studentFeedback TEXT,
    improvementSuggestions TEXT,
    id_ResourceSheet INT REFERENCES RESOURCE_SHEET(id_ResourceSheet)
);

CREATE TABLE MODALITIES_OF_IMPLEMENTATION(
    modality TEXT NOT NULL,
    id_ResourceSheet INT REFERENCES RESOURCE_SHEET(id_ResourceSheet)
);

CREATE TABLE KEYWORD(
    keyword TEXT NOT NULL,
    id_ResourceSheet INT REFERENCES RESOURCE_SHEET(id_ResourceSheet)
);

CREATE TABLE TEACHER_HOURS(
    id_TeacherHours SERIAL PRIMARY KEY,
    cm TEXT,
    td TEXT,
    tp TEXT,
    isalternance BOOLEAN,
    id_ResourceSheet INT REFERENCES RESOURCE_SHEET(id_ResourceSheet)
);

ALTER TABLE UE
DROP CONSTRAINT ue_id_path_fkey;

ALTER TABLE UE
ADD CONSTRAINT ue_id_path_fkey
FOREIGN KEY (id_Path)
REFERENCES PATH(id_Path)
ON DELETE CASCADE;

ALTER TABLE UE_COEFFICIENT_RESOURCE
DROP CONSTRAINT ue_coefficient_resource_ue_number_fkey;

ALTER TABLE UE_COEFFICIENT_RESOURCE
ADD CONSTRAINT ue_coefficient_resource_ue_number_fkey
FOREIGN KEY (UE_Number)
REFERENCES UE(UE_Number)
ON DELETE CASCADE;

ALTER TABLE UE_COEFFICIENT_SAE
DROP CONSTRAINT ue_coefficient_sae_ue_number_fkey;

ALTER TABLE UE_COEFFICIENT_SAE
ADD CONSTRAINT ue_coefficient_sae_ue_number_fkey
FOREIGN KEY (UE_Number)
REFERENCES UE(UE_Number)
ON DELETE CASCADE;

ALTER TABLE UE_COEFFICIENT_SAE
DROP CONSTRAINT ue_coefficient_sae_id_sae_fkey;

ALTER TABLE UE_COEFFICIENT_SAE
ADD CONSTRAINT ue_coefficient_sae_id_sae_fkey
FOREIGN KEY (id_SAE)
REFERENCES SAE(id_SAE)
ON DELETE CASCADE;

ALTER TABLE UE_COEFFICIENT_RESOURCE
DROP CONSTRAINT ue_coefficient_resource_id_resource_fkey;

ALTER TABLE UE_COEFFICIENT_RESOURCE
ADD CONSTRAINT ue_coefficient_resource_id_resource_fkey
FOREIGN KEY (id_resource)
REFERENCES RESOURCE(id_Resource)
ON DELETE CASCADE;

CREATE OR REPLACE FUNCTION cleanup_orphan_sae()
RETURNS TRIGGER AS $$
BEGIN
    DELETE FROM SAE s
    WHERE s.id_sae = OLD.id_sae
      AND NOT EXISTS (
          SELECT 1
          FROM UE_COEFFICIENT_SAE ucs
          WHERE ucs.id_sae = s.id_sae
      );
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_cleanup_orphan_sae
AFTER DELETE ON UE_COEFFICIENT_SAE
FOR EACH ROW
EXECUTE FUNCTION cleanup_orphan_sae();


CREATE OR REPLACE FUNCTION cleanup_orphan_resource()
RETURNS TRIGGER AS $$
BEGIN
    DELETE FROM RESOURCE r
    WHERE r.id_resource = OLD.id_resource
      AND NOT EXISTS (
          SELECT 1
          FROM UE_COEFFICIENT_RESOURCE ucr
          WHERE ucr.id_resource = r.id_resource
      );
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_cleanup_orphan_resource
AFTER DELETE ON UE_COEFFICIENT_RESOURCE
FOR EACH ROW
EXECUTE FUNCTION cleanup_orphan_resource();


ALTER TABLE RESOURCE_SHEET
DROP CONSTRAINT resource_sheet_id_resource_fkey;

ALTER TABLE RESOURCE_SHEET
ADD CONSTRAINT resource_sheet_id_resource_fkey
FOREIGN KEY (id_resource)
REFERENCES RESOURCE(id_Resource)
ON DELETE CASCADE;


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
('administration','test','atest','test123',4),
('Nath', 'nathyouistiti', 'nn', '123', 1),
('julie', 'ju', 'jCty', '456', 1),
('edith', 'didith', 'BigED', '789', 1),
('martin', 'matin', 'mm', '101112', 1),
('noé', 'NO WAY', 'njacquet', '131415', 1);

INSERT INTO TASK (name, description, delivery , id_User) VALUES
('remplir fiche ressource', 'vous devez remplir la fiche ressource pour chaque ressource pédagogique que vous créez', '2026-01-30',  1),
('mettre à jour fiche ressource', 'vous devez mettre à jour la fiche ressource pour chaque ressource pédagogique que vous modifiez', '2026-01-30', 3);



INSERT INTO ACCESSRIGHT ( id_User,accessRight) VALUES
(1, 1),
(1, 2),
(1, 3),
(2, 2),
(3, 1),
(4, 3),
(5, 1),
(6, 2),
(7, 2),
(8, 1),
(9, 1),
(10, 1),
(11, 2);


INSERT INTO PATH(number, name, id_institution) VALUES
(1, 'BUT INFORMATIQUE parcours réalisation d''applications : conception, développement, validation', 1),
(2, 'parcours de test', 4);

INSERT INTO UE(EuApogeeCode, label, name, competenceLevel, semester, id_Path) VALUES
('TIN0101U', 'UE1.1','Réaliser',1, 1, 1),
('TIN0102U', 'UE1.2','Optimiser',1, 1, 1),
('TIN0103U', 'UE1.3','Administrer',1, 1, 1);

INSERT INTO TERMS(code) VALUES
('NGCC'),
('NGCC'),
('NGCC'),
('NGCC'),
('NGCC'),
('NGCC'),
('NGCC'),
('NGCC'),
('NGCC'),
('NGCC'),
('NGCC');

INSERT INTO SAE(label, apogeeCode, semester, id_terms) VALUES
('SAE 1', 'SAE101', 1, 5),
('SAE 2', 'SAE102', 1, 6),
('SAE 3', 'SAE103', 1, 7),
('SAE 4', 'SAE201', 2, 8),
('SAE 5', 'SAE202', 2, 9),
('SAE 6', 'SAE203', 2, 10);


INSERT INTO RESOURCE(apogeeCode, label,name, diffMultiCompetences, semester, id_terms) VALUES
('TIN01A1M', 'R1.01',' Initiation au développement', FALSE, 1, 1),
('TIN01A2M', 'R1.02','Développement d''interfaces web', FALSE, 1, 2),
('TIN01A3M', 'R1.03','Introduction à l''architecture des ordinateurs', FALSE, 1, 3),
('TIN01A4M', 'R2.04','Test de la nouvelle ressource', FALSE, 2, 4);


INSERT INTO SAE_LINK_RESOURCE(id_SAE, id_resource) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4);

INSERT INTO HOURS_PER_STUDENT (has_alternance,cm, td, tp,id_resource) VALUES
(TRUE, 8, 12, 20, 1),
(FALSE,6, 10, 20, 1),
(FALSE, 4, 10, 16, 2),
(FALSE, 10, 8, 10, 3),
(FALSE, 5, 5, 5, 4);

INSERT INTO TEACHER_HOURS (isalternance,cm, td, tp,id_resourcesheet) VALUES
(TRUE, 8, 12, 20, 1);

INSERT INTO SAE_HOURS(hours, id_SAE) VALUES
(36, 1),
(30, 2),
(28, 3),
(20, 4);

INSERT INTO UE_COEFFICIENT_RESOURCE(coefficient, UE_Number,id_resource) VALUES
(3, 1, 1),
(4, 2, 2),
(2, 3, 3),
(5, 1, 4);
INSERT INTO UE_COEFFICIENT_SAE(coefficient, UE_Number,id_SAE) VALUES
(3, 2, 1),
(4, 2, 2),
(2, 3, 3),
(5, 1, 4);
INSERT INTO RESOURCE_SHEET( year, id_Resource) VALUES
('2025-09-01', 1),
('2025-09-01', 2),
('2025-09-01', 3),
( '2026-01-01', 4);

INSERT INTO MAIN_TEACHER_FOR_RESOURCE(id_User, id_Resource) VALUES
(1, 1),
(3, 2),
(3, 3),
(5,4);

INSERT INTO TEACHERS_FOR_RESOURCE(id_User, id_Resource) VALUES
(1, 1),
(2, 1),
(3, 2),
(4, 2),
(3, 3),
(6,4);

INSERT INTO NATIONAL_PROGRAM_OBJECTIVE(content, id_ResourceSheet) VALUES
('Comprendre les bases de la programmation', 1),
('Savoir créer des interfaces web', 2),
('Connaître l''architecture des ordinateurs', 3),
('Tester des ressources pédagogiques', 4);

INSERT INTO NATIONAL_PROGRAM_SKILL(label, description, id_ResourceSheet) VALUES
('dev','Être capable de coder en Python', 1),
('web','Maîtriser HTML et CSS', 2),
('arch','Comprendre le fonctionnement des processeurs', 3),
('test','Savoir évaluer une ressource pédagogique', 4);

INSERT INTO PEDAGOGICAL_CONTENT(cm, td, tp,id_resourceSheet) VALUES
('1 Ennui ultime du python,2 pourquoi le prof il fait des dessins frr?,3 j''abandonne c''est trop là', '1 waw en vrai c''est ok de fou,2 euh ok c''est chelou mais ok ig,3 c''est quoi cette merde là laissez moi mourir', '1 hello world,2 bibliothèque des pleurs,3 putain c''est quoi pickle ;-;',1),
(NULL, '1 oww un petit chat,2 mmm redirections? c''est quoi ça?', '1 euh pourquoi le niveau il a triplé, 2 MAIS C''EST QUOI LE CSS DE MERDE,3 NON',2),
('1 waw les bits,2 mm pas cool les bits','1 pourquoi j''apprend ça même,2 aled','1 non laisse mo rentrer chez moi,2 non juste par pitié,3 pourquoi je m''inflige ça même',3),
(NULL, 'TD de test 1, TD de test 2', 'TP de test 1, TP de test 2',4);

INSERT INTO RESOURCE_TRACKING(pedagogicalFeedback, studentFeedback, improvementSuggestions,id_resourceSheet) VALUES
('Besoin de plus d''exemples','Trop facile','Ajouter des exercices',1),
('Parfait tel quel','Trop dur','Rien à ajouter',2),
('Bien structuré','Correct','Ajouter des vidéos explicatives',3),
('Feedback pédagogique de test','Feedback étudiant de test','Suggestions d''amélioration de test',4);

INSERT INTO KEYWORD(keyword, id_ResourceSheet) VALUES
('programmation', 1),
('python', 1),
('web', 2),
('html', 2),
('architecture', 3),
('ordinateur', 3),
('test', 4),
('ressource pédagogique', 4);

INSERT INTO MODALITIES_OF_IMPLEMENTATION(modality, id_ResourceSheet) VALUES
('IUZHDEIZUHDEOID', 1),
('ZEUDHZEUDHZEUDH', 2),
('EZUDHZEUDHZEUDH', 3),
('ZEUDHZEUDHZEUDH', 4);