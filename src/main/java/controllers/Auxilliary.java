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
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.imageio.ImageIO;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
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

    private final int ANSWER_GOOD = 2;
    private final int ANSWER_NEUTRAL = 1;
    private final int ANSWER_BAD = 0;

    private final String levels[] = {
        "Level 1: n00b",
        "Level 2: Rookie",
        "Level 3: Beginner",
        "Level 4: Elementary",
        "Level 5: Intermediate",
        "Level 6: Experienced",
        "Level 7: Achiever",
        "Level 8: Seasoned",
        "Level 9: Exemplary",
        "Level 10: Master",
        "Level 11: Extraordinary",
        "Level 12 (Max): Supreme Leader"
    };

    @PersistenceContext
    EntityManager em;

    private boolean activitiesPending;
    private LinkedHashMap<AppUser, Integer> studentsScores;
    private LinkedHashMap<AppUser, Integer> studentsOverallScores;
    private String level;
    private int progress;
    private StreamedContent graphicImage;
    private List<AppUser> studentsList;
    private AppUser student;
    private Category previousCategory;
    List<ActivitiesMap> currentActivities;

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
        skillsMapList.sort((SkillsMap o1, SkillsMap o2) -> (int) (o1.getCategoryId().getId() - o2.getCategoryId().getId()));
        int highestScore = 0;
        for (SkillsMap skillsMap : skillsMapList) {
            skills.set(skillsMap.getCategoryId().getTitle(), skillsMap.getSkillLevel());
            if (skillsMap.getSkillLevel() > highestScore) {
                highestScore = skillsMap.getSkillLevel();
            }
        }
        barModel.addSeries(skills);
        barModel.setTitle(student.getFirstName() + " " + student.getLastName() + " skills");
        barModel.setStacked(true);
        barModel.setDatatipFormat("%s");
        Axis xAxis = barModel.getAxis(AxisType.X);
        xAxis.setLabel("Level");
        xAxis.setMin(0);
        xAxis.setMax(highestScore + 10);
        xAxis.setTickFormat("%d");
        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setLabel("Skill");
        return barModel;
    }
    
    public LineChartModel createLineModel(AppUser student, LineChartModel lineModel) {
        lineModel = new LineChartModel();
        List<ActivitiesMap> activitiesMapList = new ArrayList<>();
        for (ActivitiesMap activitiesMap : student.getActivitiesMapList()) {
            if (activitiesMap.getLogged()){
                activitiesMapList.add(activitiesMap);
            }
        }
        activitiesMapList.sort((ActivitiesMap o1, ActivitiesMap o2) -> (int) (o1.getDateAnswered().compareTo(o2.getDateAnswered())));
        List<SkillsMap> studentSkillsMapList = student.getSkillsMapList();
        int highestScore = 0;
        for (SkillsMap skills : studentSkillsMapList) {
            ChartSeries series = new ChartSeries();
            series.setLabel(skills.getCategoryId().getTitle());
            series.set(new SimpleDateFormat("dd/MM").format(student.getDateRegistered()), 0);
            lineModel.addSeries(series);
            if (skills.getSkillLevel() > highestScore) {
                highestScore = skills.getSkillLevel();
            }
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
                    series.set(new SimpleDateFormat("dd/MM").format(activitiesMap.getDateAnswered()), seriesValues.get(seriesValues.size() - 1));
                }
            }
        }
        Axis axisY = lineModel.getAxis(AxisType.Y);
        axisY.setLabel("Level");
        axisY.setMax(highestScore + 20);
        axisY.setMin(0);
        axisY.setTickFormat("%s");
        lineModel.setLegendPosition("nw");
        lineModel.setShowPointLabels(true);
        lineModel.getAxes().put(AxisType.X, new CategoryAxis("Time"));
        lineModel.setTitle("Progress");
        String[] dates;
        for (ChartSeries series : lineModel.getSeries()) {
            Category skill = (Category) em.createNamedQuery("Category.findByTitle").setParameter("title", series.getLabel()).getSingleResult();
            SkillsMap skillsMap = (SkillsMap) em.createNamedQuery("SkillsMap.findByCategoryIdAndStudentEmail").setParameter("categoryId", skill).setParameter("studentEmail", student).getSingleResult();
            int level = skillsMap.getSkillLevel();
            dates = series.getData().keySet().toArray(new String[series.getData().keySet().size()]);
            if (!series.getData().get(dates[dates.length - 1]).equals(level)) {
                series.set(dates[dates.length - 1], level);
            }
        }
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

    public void setGraphicImage(Category selectedCategory) {
        try {
            BufferedImage bufferedImg = ImageIO.read(getClass().getResourceAsStream("/img/topthree.png"));
            Graphics2D g2 = bufferedImg.createGraphics();
            g2.setColor(Color.black);
            g2.setFont(new Font(null, 0, 30));
            List<AppUser> students = getSortedStudents(selectedCategory);
            g2.drawString(students.get(0).getNickname(), (465 - (int) g2.getFontMetrics().stringWidth(students.get(0).getNickname()) / 2), 115);
            g2.drawString(String.valueOf(students.get(0).getScore()), 390, 450);
            if (students.size() > 1) {
                g2.drawString(students.get(1).getNickname(), (150 - (int) g2.getFontMetrics().stringWidth(students.get(1).getNickname()) / 2), 210);
                g2.drawString(String.valueOf(students.get(1).getScore()), 100, 450);
            }
            if (students.size() > 2) {
                g2.drawString(students.get(2).getNickname(), (750 - (int) g2.getFontMetrics().stringWidth(students.get(2).getNickname()) / 2), 260);
                g2.drawString(String.valueOf(students.get(2).getScore()), 670, 450);
            }
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(bufferedImg, "png", os);
            byte[] byteArray = os.toByteArray();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
            graphicImage = new DefaultStreamedContent(inputStream, "image/png");
        } catch (IOException ex) {
            Logger.getLogger(ProfileController.class.getName()).log(Level.SEVERE, null, ex);
            graphicImage = new DefaultStreamedContent();
        }
    }

    public StreamedContent getGraphicImage() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return new DefaultStreamedContent();
        } else {
            return graphicImage;
        }
    }


    public LinkedHashMap<AppUser, Integer> getStudentsScores() {
        return studentsScores;
    }

    public List<AppUser> getSortedStudents(Category category) {
        List<AppUser> students = em.createNamedQuery("AppUser.findByUserRole").setParameter("userRole", 1).getResultList();
        if (category == null) {
            for (AppUser student : students) {
                student.setScore(
                        ((Long) em.createNamedQuery("SkillsMap.getOverallScore")
                        .setParameter("studentEmail", student)
                        .getSingleResult()).intValue());
                setStudentLevel(student);
            }
        } else {
            for (AppUser student : students) {
                student.setScore(
                        ((int) ((SkillsMap) em.createNamedQuery("SkillsMap.findByCategoryIdAndStudentEmail")
                                .setParameter("studentEmail", student)
                                .setParameter("categoryId", category)
                                .getSingleResult())
                                .getSkillLevel())
                );
                setStudentLevel(student);
            }
        }
        students.sort((AppUser o1, AppUser o2) -> (int) (o2.getScore() - o1.getScore()));
        return students;
    }

    public int calculateProgress(double score, double currentMinimum, double nextMinimum) {
        return (int) ((int) 100 * (score - currentMinimum) / (nextMinimum - currentMinimum));
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setStudentLevel(AppUser student) {
        int score = (((Long) em.createNamedQuery("SkillsMap.getOverallScore").setParameter("studentEmail", student).getSingleResult()).intValue());
        if (score < 20) {
            student.setLevel(levels[0]);
            if (this.student != null && student.getEmail().equals(this.student.getEmail())) {
                progress = calculateProgress(score, 0, 20);
                if (progress == 100) {
                    student.setLevel(levels[1]);
                    progress = 0;
                }
            }
        } else if (score <= 50) {
            student.setLevel(levels[1]);
            if (this.student != null && student.getEmail().equals(this.student.getEmail())) {
                progress = calculateProgress(score, 20, 50);
                if (progress == 100) {
                    student.setLevel(levels[2]);
                    progress = 0;
                }
            }
        } else if (score <= 90) {
            student.setLevel(levels[2]);
            if (this.student != null && student.getEmail().equals(this.student.getEmail())) {
                progress = calculateProgress(score, 50, 90);
                if (progress == 100) {
                    student.setLevel(levels[3]);
                    progress = 0;
                }
            }
        } else if (score <= 140) {
            student.setLevel(levels[3]);
            if (this.student != null && student.getEmail().equals(this.student.getEmail())) {
                progress = calculateProgress(score, 90, 140);
                if (progress == 100) {
                    student.setLevel(levels[4]);
                    progress = 0;
                }
            }
        } else if (score <= 200) {
            student.setLevel(levels[4]);
            if (this.student != null && student.getEmail().equals(this.student.getEmail())) {
                progress = calculateProgress(score, 140, 200);
                if (progress == 100) {
                    student.setLevel(levels[5]);
                    progress = 0;
                }
            }
        } else if (score <= 270) {
            student.setLevel(levels[5]);
            if (this.student != null && student.getEmail().equals(this.student.getEmail())) {
                progress = calculateProgress(score, 200, 270);
                if (progress == 100) {
                    student.setLevel(levels[6]);
                    progress = 0;
                }
            }
        } else if (score <= 350) {
            student.setLevel(levels[6]);
            if (this.student != null && student.getEmail().equals(this.student.getEmail())) {
                progress = calculateProgress(score, 270, 350);
                if (progress == 100) {
                    student.setLevel(levels[7]);
                    progress = 0;
                }
            }
        } else if (score <= 440) {
            student.setLevel(levels[7]);
            if (this.student != null && student.getEmail().equals(this.student.getEmail())) {
                progress = calculateProgress(score, 350, 440);
                if (progress == 100) {
                    student.setLevel(levels[8]);
                    progress = 0;
                }
            }
        } else if (score <= 540) {
            student.setLevel(levels[8]);
            if (this.student != null && student.getEmail().equals(this.student.getEmail())) {
                progress = calculateProgress(score, 440, 540);
                if (progress == 100) {
                    student.setLevel(levels[9]);
                    progress = 0;
                }
            }
        } else if (score <= 650) {
            student.setLevel(levels[9]);
            if (this.student != null && student.getEmail().equals(this.student.getEmail())) {
                progress = calculateProgress(score, 540, 650);
                if (progress == 100) {
                    student.setLevel(levels[10]);
                    progress = 0;
                }
            }
        } else if (score <= 770) {
            student.setLevel(levels[10]);
            if (this.student != null && student.getEmail().equals(this.student.getEmail())) {
                progress = calculateProgress(score, 650, 770);
                if (progress == 100) {
                    student.setLevel(levels[11]);
                    progress = 0;
                }
            }
        } else {
            student.setLevel(levels[11]);
            if (this.student != null && student.getEmail().equals(this.student.getEmail())) {
                progress = calculateProgress(score, 770, 900);
                if (progress > 100) {
                    progress = 100;
                }
            }
        }
    }

    public List<ActivitiesMap> getCurrentActivities() {
        return currentActivities;
    }

    public void setCurrentActivities(List<ActivitiesMap> allActivities) {
        currentActivities = new ArrayList<>();
        if (!allActivities.isEmpty()) {
            Calendar cCurrent = Calendar.getInstance();
            for (ActivitiesMap activity : allActivities) {
                Calendar cEnabled = Calendar.getInstance();
                cEnabled.setTime(activity.getDateEnabled());
                Calendar cDisabled = Calendar.getInstance();
                cDisabled.setTime(activity.getDateDisabled());
                cDisabled.add(Calendar.DATE, 1);
                if (activity.getEnabled() && !cCurrent.getTime().before(cEnabled.getTime()) && !cCurrent.getTime().after(cDisabled.getTime())) {
                    currentActivities.add(activity);
                }
            }
        }
    }

    public List<AppUser> getStudentsList() {
        return studentsList;
    }

    public void setStudentsList(List<AppUser> studentsList) {
        this.studentsList = studentsList;
    }

    public LinkedHashMap<AppUser, Integer> getStudentsOverallScores() {
        return studentsOverallScores;
    }

    public void setStudentsOverallScores(LinkedHashMap<AppUser, Integer> studentsOverallScores) {
        this.studentsOverallScores = studentsOverallScores;
    }

    public AppUser getStudent() {
        return student;
    }

    public Category getPreviousCategory() {
        return previousCategory;
    }

    public void setPreviousCategory(Category previousCategory) {
        this.previousCategory = previousCategory;
    }

    public void setStudent() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        student = em.find(AppUser.class, request.getUserPrincipal().getName());
        student.setScore(((Long) em.createNamedQuery("SkillsMap.getOverallScore").setParameter("studentEmail", student).getSingleResult()).intValue());
        setStudentLevel(student);
    }

}