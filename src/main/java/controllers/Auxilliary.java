/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entities.AppUser;
import entities.SkillsMap;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.ejb.Stateless;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.HorizontalBarChartModel;

/**
 *
 * @author dpattas
 */
@Stateless
public class Auxilliary {

        public HorizontalBarChartModel createBarModel(AppUser student, HorizontalBarChartModel barModel) {

        barModel = new HorizontalBarChartModel();

        ChartSeries skills = new ChartSeries();
            for (SkillsMap skillsMap : student.getSkillsMapList()) {
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
}
