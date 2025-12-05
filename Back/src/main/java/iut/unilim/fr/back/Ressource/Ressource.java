package iut.unilim.fr.back.Ressource;

import java.util.List;
public class Ressource {
    private String ressourceName;
    private String refUE;
    private int groupsNb;
    private int studentsNb;


    public static class Duration {
        private int TD;
        private int TP;
        private int CM;
        private int alternance;

        public int getTotal(){
            return TD + TP + CM + alternance;
        }
    }

    public static class KeyWords{
        private List<String> list;

        public List<String> getter() {
            return list;
        }


        public void setter(List<String> list) {
            this.list = list;
        }

    }
    public static class Descritpion{
        private String objective;
        private String lessonPlan;
        private String assessmentEvolution;
        private List<String> skillsListy;

    }

    public static class RessourceMonitoring{
        private String formStudents;
        private String formTeacher;
        private String remarque;


    }

    public void isComplete() {
        //TODO
    }

}
