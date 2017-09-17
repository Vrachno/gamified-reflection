/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entities.ActivitiesMap;
import entities.AppUser;
import entities.Category;
import entities.SkillsMap;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.HorizontalBarChartModel;
import org.primefaces.model.chart.LineChartModel;

/**
 *
 * @author dpattas
 */
@Stateless
@Named("aux")
public class Auxilliary {

    private final int ANSWER_GOOD = 10;
    private final int ANSWER_NEUTRAL = 5;
    private final int ANSWER_BAD = 1;

    @PersistenceContext
    EntityManager em;

    private boolean activitiesPending;

    public int getANSWER_GOOD() {
        return ANSWER_GOOD;
    }

    public int getANSWER_NEUTRAL() {
        return ANSWER_NEUTRAL;
    }

    public int getANSWER_BAD() {
        return ANSWER_BAD;
    }

    public boolean isActivitiesPending() {
        return activitiesPending;
    }

    public void setActivitiesPending(boolean activitiesPending) {
        this.activitiesPending = activitiesPending;
    }

    public HorizontalBarChartModel createBarModel(AppUser student, HorizontalBarChartModel barModel) {

        barModel = new HorizontalBarChartModel();

        ChartSeries skills = new ChartSeries();
        List<SkillsMap> skillsMapList = student.getSkillsMapList();
        skillsMapList.sort(new Comparator<SkillsMap>() {
            @Override
            public int compare(SkillsMap o1, SkillsMap o2) {
                return (int) (o1.getCategoryId().getId() - o2.getCategoryId().getId());
            }
        });
        for (SkillsMap skillsMap : skillsMapList) {
            skills.set(skillsMap.getCategoryId().getTitle(), skillsMap.getSkillLevel());
        }

        barModel.addSeries(skills);

        barModel.setTitle(student.getFirstName() + " " + student.getLastName() + " skills");
        barModel.setStacked(true);
        barModel.setDatatipFormat("%s");

        Axis xAxis = barModel.getAxis(AxisType.X);
        xAxis.setLabel("Level");
        xAxis.setMin(0);
        xAxis.setMax(100);
        xAxis.setTickCount(10);
        xAxis.setTickFormat("%d");
        xAxis.setTickInterval("10");

        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setLabel("Skill");

        return barModel;
    }

    public LineChartModel createLineModel(AppUser student, LineChartModel lineModel) {
        lineModel = new LineChartModel();

        List<ActivitiesMap> activitiesMapList = em.createNamedQuery("ActivitiesMap.findNotLoggedByStudent").setParameter("studentEmail", student).setParameter("logged", true).getResultList();
        activitiesMapList.sort(new Comparator<ActivitiesMap>() {
            @Override
            public int compare(ActivitiesMap o1, ActivitiesMap o2) {
                return (int) (o1.getDateAnswered().compareTo(o2.getDateAnswered()));
            }
        });

        List<SkillsMap> studentSkillsMapList = em.createNamedQuery("SkillsMap.findByStudentEmail").setParameter("studentEmail", student).getResultList();
        for (SkillsMap skills : studentSkillsMapList) {
            ChartSeries series = new ChartSeries();
            series.setLabel(skills.getCategoryId().getTitle());
            series.set(new SimpleDateFormat("dd/MM").format(student.getDateRegistered()), 0);
            lineModel.addSeries(series);
        }

        for (ChartSeries series : lineModel.getSeries()) {
        List<Integer> seriesValues = new ArrayList(series.getData().values());
            for (ActivitiesMap activitiesMap : activitiesMapList) {
                if (activitiesMap.getActivity().getCategoryId().getTitle().equals(series.getLabel())) {
                    if (series.getData().get(new SimpleDateFormat("dd/MM").format(activitiesMap.getDateAnswered())) == null
                            || activitiesMap.getNewSkillLevel() > (int) series.getData().get(new SimpleDateFormat("dd/MM").format(activitiesMap.getDateAnswered()))) {
                        series.set(new SimpleDateFormat("dd/MM").format(activitiesMap.getDateAnswered()), activitiesMap.getNewSkillLevel());
                        seriesValues.add(activitiesMap.getNewSkillLevel());
                    }
                } else {
                    series.set(new SimpleDateFormat("dd/MM").format(activitiesMap.getDateAnswered()), seriesValues.get(seriesValues.size()-1));
                }
            }
        }


        Axis axisY = lineModel.getAxis(AxisType.Y);

        axisY.setLabel("Level");
        axisY.setMax(100);
        axisY.setMin(0);

        lineModel.setLegendPosition("ne");
        lineModel.setShowPointLabels(true);
        lineModel.getAxes().put(AxisType.X, new CategoryAxis("Time"));
        lineModel.setTitle("Progress");

        return lineModel;
    }

    public String hash256(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data.getBytes());
        return bytesToHex(md.digest());
    }

    public String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte byt : bytes) {
            result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }

    public void logout() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try {
            request.logout();
            FacesContext.getCurrentInstance().getExternalContext().redirect("http://localhost:8080/GamifiedReflection/login.html");
        } catch (ServletException e) {

            context.addMessage(null, new FacesMessage("Logout failed."));
        }
    }
    
        public ArrayList<AppUser> topOfClass(Category category) {
        List<SkillsMap> allSkillsMaps= em.createNamedQuery("SkillsMap.findByCategoryId").setParameter("categoryId", category).getResultList();
        ArrayList<AppUser> topStudents = new ArrayList<>();
        allSkillsMaps.sort(new Comparator<SkillsMap>() {
            @Override
            public int compare(SkillsMap o1, SkillsMap o2) {
               return (int) (o2.getSkillLevel() - o1.getSkillLevel());
            }
        });
        if (allSkillsMaps.get(0).getSkillLevel()!=0)
            topStudents.add(allSkillsMaps.get(0).getStudentEmail());
        for (int i=1; i<allSkillsMaps.size(); i++) {
            if (allSkillsMaps.get(i).getSkillLevel() == allSkillsMaps.get(0).getSkillLevel()){
                topStudents.add(allSkillsMaps.get(i).getStudentEmail());
            }
        }
        
        return topStudents;
    }
}
