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
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.imageio.ImageIO;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

    @PersistenceContext
    EntityManager em;

    private boolean activitiesPending;
    private LinkedHashMap<AppUser, Integer> studentsScores;
    private String level;
    private int progress;

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

        List<ActivitiesMap> activitiesMapList = em.createNamedQuery("ActivitiesMap.findNotLoggedByStudent").setParameter("studentEmail", student).setParameter("logged", true).getResultList();
        activitiesMapList.sort(new Comparator<ActivitiesMap>() {
            @Override
            public int compare(ActivitiesMap o1, ActivitiesMap o2) {
                return (int) (o1.getDateAnswered().compareTo(o2.getDateAnswered()));
            }
        });

        List<SkillsMap> studentSkillsMapList = em.createNamedQuery("SkillsMap.findByStudentEmail").setParameter("studentEmail", student).getResultList();
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
                    series.set(new SimpleDateFormat("dd/MM").format(activitiesMap.getDateAnswered()), seriesValues.get(seriesValues.size()-1));
                }
            }
        }


        Axis axisY = lineModel.getAxis(AxisType.Y);

        axisY.setLabel("Level");
        axisY.setMax(highestScore + 20);
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
        List<SkillsMap> allSkillsMaps = em.createNamedQuery("SkillsMap.findByCategoryId").setParameter("categoryId", category).getResultList();
        allSkillsMaps.sort(new Comparator<SkillsMap>() {
            @Override
            public int compare(SkillsMap o1, SkillsMap o2) {
                return (int) (o2.getSkillLevel() - o1.getSkillLevel());
            }
        });
        ArrayList<AppUser> topStudents = new ArrayList<>();
        if (allSkillsMaps.size()>0) {
        if (allSkillsMaps.get(0).getSkillLevel() != 0) {
            topStudents.add(allSkillsMaps.get(0).getStudentEmail());
        }
        for (int i = 1; i < allSkillsMaps.size(); i++) {
            if ((allSkillsMaps.get(i).getSkillLevel() == allSkillsMaps.get(0).getSkillLevel()) && allSkillsMaps.get(0).getSkillLevel() != 0) {
                topStudents.add(allSkillsMaps.get(i).getStudentEmail());
            }
        }
        }
        return topStudents;
    }

    public StreamedContent getGraphicImage() {
        FacesContext context = FacesContext.getCurrentInstance();
        setStudentScores();
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return new DefaultStreamedContent();
        } else {
            try {
                File img = new File("../docroot/GamifiedReflection/topthree.png");
                BufferedImage bufferedImg = ImageIO.read(img);
                Graphics2D g2 = bufferedImg.createGraphics();
                g2.setColor(Color.black);
                g2.setFont(new Font(null, 0, 30));
                List<AppUser> students = new ArrayList<>(studentsScores.keySet());
                List<Integer> scores = new ArrayList<>(studentsScores.values());
                g2.drawString(students.get(0).getNickname(), (465 - (int) g2.getFontMetrics().stringWidth(students.get(0).getNickname()) / 2), 115);
                g2.drawString(scores.get(0).toString(), 390, 450);
                if (students.size() > 1) {
                    g2.drawString(students.get(1).getNickname(), (150 - (int) g2.getFontMetrics().stringWidth(students.get(1).getNickname()) / 2), 210);
                    g2.drawString(scores.get(1).toString(), 100, 450);
                }
                if (students.size() > 2) {
                    g2.drawString(students.get(2).getNickname(), (750 - (int) g2.getFontMetrics().stringWidth(students.get(2).getNickname()) / 2), 260);
                    g2.drawString(scores.get(2).toString(), 670, 450);
                }
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(bufferedImg, "png", os);
                byte[] byteArray = os.toByteArray();
                ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
                return new DefaultStreamedContent(inputStream, "image/png");
            } catch (IOException ex) {
                Logger.getLogger(ProfileController.class.getName()).log(Level.SEVERE, null, ex);
                return new DefaultStreamedContent();
            }
        }
    }
    
    public void setStudentScores() {
        List<AppUser> studentsList = em.createNamedQuery("AppUser.findByUserRole").setParameter("userRole", 1).getResultList();
        HashMap<AppUser, Integer> unorderedStudentsScores = new HashMap();
        studentsList.forEach((student) -> {
            int score = 0;
            List<SkillsMap> studentSkillMaps = em.createNamedQuery("SkillsMap.findByStudentEmail").setParameter("studentEmail", student).getResultList();
            score = studentSkillMaps.stream().map((skillsMap) -> skillsMap.getSkillLevel()).reduce(score, Integer::sum);
            unorderedStudentsScores.put(student, score);
        });
        studentsScores = sortByValue(unorderedStudentsScores);
    }

    public LinkedHashMap<AppUser, Integer> getStudentsScores() {
        return studentsScores;
    }

    public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValue(Map<K, V> map) {
    return map.entrySet()
              .stream()
              .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
              .collect(Collectors.toMap(
                Map.Entry::getKey, 
                Map.Entry::getValue, 
                (e1, e2) -> e1, 
                LinkedHashMap::new
              ));
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
        setStudentScores();
        int score = getStudentsScores().get(student);
        if (score < 20) {
            level = "n00b";
            progress = calculateProgress(score, 0, 20);
        } else if (score <= 50) {
            level = "Rookie";
            progress = calculateProgress(score, 20, 50);
        } else if (score <= 90) {
            level = "Beginner";
            progress = calculateProgress(score, 50, 90);
        } else if (score <= 140) {
            level = "Elementary";
            progress = calculateProgress(score, 90, 140);
        } else if (score <= 200) {
            level = "Intermediate";
            progress = calculateProgress(score, 140, 200);
        } else if (score <= 270) {
            level = "Experienced";
            progress = calculateProgress(score, 200, 270);
        } else if (score <= 350) {
            level = "Achiever";
            progress = calculateProgress(score, 270, 350);
        } else if (score <= 440) {
            level = "Seasoned";
            progress = calculateProgress(score, 350, 440);
        } else if (score <= 540) {
            level = "Exemplary";
            progress = calculateProgress(score, 440, 540);
        } else if (score <= 650) {
            level = "Master";
            progress = calculateProgress(score, 540, 650);
        } else if (score <= 770) {
            level = "Extraordinary";
            progress = calculateProgress(score, 650, 770);
        } else {
            level = "Supreme Leader";
            progress = 100;
        }
    }
}
