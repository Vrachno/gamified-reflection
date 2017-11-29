/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 *
 * @author dpattas
 */
public class MainFlowTests {

    static WebDriver driver;
    private static Connection con;

    
    @BeforeClass
    @Parameters(value = "base-url")
    public static void setUpClass(String baseUrl) throws Exception {
        //ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get(baseUrl);
        con = DriverManager.getConnection("jdbc:derby://localhost:1527/GamifiedReflection", "dpattas", "pattdimit");
    }
    
    @Test
    @Parameters(value = {"student-email", "password", "first-name", "last-name", "nickname", "admin-email"})
    public void TestStudentRegister(String email, String password, String firstName, String lastName, String nickname, String adminEmail) throws SQLException, InterruptedException {
        Statement statement = con.createStatement();
        ResultSet users = statement.executeQuery("select * from APP_USER where EMAIL = '" + email + "'");
        if (!users.next()) {
            driver.findElement(By.id("loginForm:registerbutton")).click();
            driver.findElement(By.id("register-form:submit")).click();
            assertNotNull(new WebDriverWait(driver, 10).until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("register-form:emailMsg")))
                    .findElement(By.className("ui-message-error-detail")));
            assertNotNull(new WebDriverWait(driver, 10).until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("register-form:firstNameMsg")))
                    .findElement(By.className("ui-message-error-detail")));
            assertNotNull(new WebDriverWait(driver, 10).
                    until(ExpectedConditions.visibilityOfElementLocated(By.id("register-form:lastNameMsg")))
                    .findElement(By.className("ui-message-error-detail")));
            assertNotNull(new WebDriverWait(driver, 10).until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("register-form:pswdMsg")))
                    .findElement(By.className("ui-message-error-detail")));
            assertNotNull(new WebDriverWait(driver, 10).until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("register-form:validateMsg")))
                    .findElement(By.className("ui-message-error-detail")));
            assertNotNull(new WebDriverWait(driver, 10).until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("register-form:nicknameMsg")))
                    .findElement(By.className("ui-message-error-detail")));
            driver.findElement(By.id("register-form:email")).clear();
            driver.findElement(By.id("register-form:email")).sendKeys(email);
            driver.findElement(By.id("register-form:first-name")).clear();
            driver.findElement(By.id("register-form:first-name")).sendKeys(firstName);
            driver.findElement(By.id("register-form:last-name")).clear();
            driver.findElement(By.id("register-form:last-name")).sendKeys(lastName);
            driver.findElement(By.id("register-form:password")).clear();
            driver.findElement(By.id("register-form:password")).sendKeys(password);
            driver.findElement(By.id("register-form:validatePassword")).clear();
            driver.findElement(By.id("register-form:validatePassword")).sendKeys(password);
            driver.findElement(By.id("register-form:nickname")).clear();
            driver.findElement(By.id("register-form:nickname")).sendKeys(nickname);
            driver.findElement(By.id("register-form:submit")).click();
//            assertNotNull(new WebDriverWait(driver, 10).until(
//                    ExpectedConditions.visibilityOfElementLocated(By.id("register-form:messages")))
//                    .findElement(By.className("ui-messages-error-detail")));
//            driver.findElement(By.id("register-form:email")).clear();
//            driver.findElement(By.id("register-form:email")).sendKeys(email);
//            driver.findElement(By.id("register-form:password")).clear();
//            driver.findElement(By.id("register-form:password")).sendKeys(password);
//            driver.findElement(By.id("register-form:validatePassword")).clear();
//            driver.findElement(By.id("register-form:validatePassword")).sendKeys(password);
            driver.findElement(By.id("register-form:submit")).click();
            new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.id("loginForm")));
            Statement statement2 = con.createStatement();
            ResultSet updatedUsers = statement2.executeQuery("select * from APP_USER where EMAIL = '" + email + "'");
            assertTrue(updatedUsers.next());
        }
    }

    @Test(dependsOnMethods = "TestStudentRegister")
    @Parameters(value = {"admin-email", "student-email", "password"})
    public void TestStudentActivation(String adminEmail, String studentEmail, String password) throws SQLException, InterruptedException {
        Statement statement = con.createStatement();
        ResultSet users = statement.executeQuery("select * from APP_USER where EMAIL = '" + studentEmail + "'");
        if (users.next()) {
            if (!users.getBoolean("ACTIVE")) {
                driver.findElement(By.id("loginForm:username")).clear();
                driver.findElement(By.id("loginForm:username")).sendKeys(adminEmail);
                driver.findElement(By.id("loginForm:password")).clear();
                driver.findElement(By.id("loginForm:password")).sendKeys(password);
                driver.findElement(By.id("loginForm:loginbutton")).click();
                WebDriverWait wait = new WebDriverWait(driver, 10);
                WebElement headerForm = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("headerForm")));
                Assert.assertNotNull(headerForm);
                driver.findElement(By.id("headerForm:studentsTab")).click();
                WebElement studentsForm = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("studentsForm")));
                Assert.assertNotNull(studentsForm);
                driver.findElement(By.id("headerForm:activitiesTab")).click();
                WebElement categoryForm = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("categoryForm")));
                Assert.assertNotNull(categoryForm);
                driver.findElement(By.id("headerForm:usersTab")).click();
                WebElement usersTableData = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("usersForm:usersTable_data")));
                Assert.assertNotNull(usersTableData);
                List<WebElement> rows = usersTableData.findElements(By.tagName("tr"));
                for (WebElement row : rows) {
                    List<WebElement> columns = row.findElements(By.tagName("td"));
                    if (columns.get(0).getText().equalsIgnoreCase(studentEmail)) {
                        columns.get(4).findElement(By.partialLinkText("Enable")).click();
                        break;
                    }
                }
                Thread.sleep(500);
                driver.findElement(By.id("headerForm:logout")).click();
                WebElement loginForm = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loginForm")));
                Assert.assertNotNull(loginForm);
                Statement statement2 = con.createStatement();
                ResultSet updatedUsers = statement2.executeQuery("select * from APP_USER where EMAIL = '" + studentEmail + "'");
                while (updatedUsers.next()) {
                    assertTrue(updatedUsers.getBoolean("ACTIVE"));
                }
            }
        }
    }
    
    @Test(dependsOnMethods = "TestStudentActivation")
    @Parameters(value = {"student-email", "password", "new-nickname"})
    public void TestStudentLoginAndNavigation(String email, String password, String newNick) throws SQLException, InterruptedException {
        driver.findElement(By.id("loginForm:username")).clear();
        driver.findElement(By.id("loginForm:username")).sendKeys(email);
        driver.findElement(By.id("loginForm:password")).clear();
        driver.findElement(By.id("loginForm:password")).sendKeys(password);
        driver.findElement(By.id("loginForm:loginbutton")).click();
        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement info = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("info")));
        Assert.assertNotNull(info);
        WebElement studentDetails = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("studentDetails")));
        Assert.assertNotNull(studentDetails);
        WebElement messages = driver.findElement(By.id("welcome:nicknameMsg"));
        assertTrue(messages.findElements(By.tagName("li")).isEmpty());
        driver.findElement(By.id("welcome:changeNicknameButton")).click();
        driver.findElement(By.id("welcome:nickname")).clear();
        driver.findElement(By.id("welcome:confirmNicknameButton")).click();
        messages = driver.findElement(By.id("welcome:nicknameMsg"));
        assertFalse(messages.findElements(By.tagName("li")).isEmpty());
        driver.findElement(By.id("welcome:nickname")).clear();
        driver.findElement(By.id("welcome:nickname")).sendKeys(newNick);
        driver.findElement(By.id("welcome:confirmNicknameButton")).click();
        driver.findElement(By.id("welcome:logout")).click();
        WebElement loginForm = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loginForm")));
        Assert.assertNotNull(loginForm);
        Statement statement = con.createStatement();
        ResultSet users = statement.executeQuery("select * from APP_USER where EMAIL = '" + email + "'");
        if (users.next()) {
            assertEquals(users.getString("NICKNAME"), newNick);
        }
    }

    @Test(dependsOnMethods = "TestStudentLoginAndNavigation")
    @Parameters(value = {"admin-email", "student-email", "password"})
    public void TestStudentDeletion(String adminEmail, String studentEmail, String password) throws SQLException, InterruptedException {
        Statement statement = con.createStatement();
        ResultSet users = statement.executeQuery("select * from APP_USER where EMAIL = '" + studentEmail + "'");
        if (users.next()) {
            if (users.getBoolean("ACTIVE")) {
                driver.findElement(By.id("loginForm:username")).clear();
                driver.findElement(By.id("loginForm:username")).sendKeys(adminEmail);
                driver.findElement(By.id("loginForm:password")).clear();
                driver.findElement(By.id("loginForm:password")).sendKeys(password);
                driver.findElement(By.id("loginForm:loginbutton")).click();
                WebDriverWait wait = new WebDriverWait(driver, 10);
                driver.findElement(By.id("headerForm:usersTab")).click();
                WebElement usersTableData = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("usersForm:usersTable_data")));
                List<WebElement> rows = usersTableData.findElements(By.tagName("tr"));
                for (WebElement row : rows) {
                    List<WebElement> columns = row.findElements(By.tagName("td"));
                    if (columns.get(0).getText().equalsIgnoreCase(studentEmail)) {
                        columns.get(4).findElement(By.partialLinkText("Disable")).click();
                        break;
                    }
                }
                Thread.sleep(500);
                Statement statement2 = con.createStatement();
                ResultSet usersAfterDisable = statement2.executeQuery("select * from APP_USER where EMAIL = '" + studentEmail + "'");
                usersAfterDisable.next();
                assertFalse(usersAfterDisable.getBoolean("ACTIVE"));
                usersTableData = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("usersForm:usersTable_data")));
                rows = usersTableData.findElements(By.tagName("tr"));
                for (WebElement row : rows) {
                    List<WebElement> columns = row.findElements(By.tagName("td"));
                    if (columns.get(0).getText().equalsIgnoreCase(studentEmail)) {
                        columns.get(4).findElement(By.partialLinkText("Delete")).click();
                        Thread.sleep(500);
                        driver.switchTo().activeElement().sendKeys(Keys.ENTER);
                        break;
                    }
                }
                Thread.sleep(500);
                driver.findElement(By.id("headerForm:logout")).click();
                WebElement loginForm = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loginForm")));
                Assert.assertNotNull(loginForm);
                Statement statement3 = con.createStatement();
                ResultSet updatedUsers = statement3.executeQuery("select * from APP_USER where EMAIL = '" + studentEmail + "'");
                assertFalse(updatedUsers.next());
            }
        }
    }

    @Test
    @Parameters(value = {"admin-email", "password"})
    public void TestSkillAndActivityManagement(String email, String password) throws SQLException, InterruptedException {
        Statement statement = con.createStatement();
        ResultSet skills = statement.executeQuery("select COUNT(*) AS rowcount from CATEGORY");
        skills.next();
        int skillsCount = skills.getInt("rowcount");
        driver.findElement(By.id("loginForm:username")).clear();
        driver.findElement(By.id("loginForm:username")).sendKeys(email);
        driver.findElement(By.id("loginForm:password")).clear();
        driver.findElement(By.id("loginForm:password")).sendKeys(password);
        driver.findElement(By.id("loginForm:loginbutton")).click();
        WebDriverWait wait = new WebDriverWait(driver, 10);
        driver.findElement(By.id("headerForm:activitiesTab")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("categoryForm:newSkillBtn"))).click();
        Thread.sleep(500);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("newSkillForm:skillName"))).sendKeys("New Skill");
        driver.findElement(By.id("newSkillForm:confirmNewSkillBtn")).click();
        Thread.sleep(1000);
        Statement statement2 = con.createStatement();
        ResultSet updatedSkills = statement2.executeQuery("select COUNT(*) AS rowcount from CATEGORY");
        updatedSkills.next();
        int updatedSkillsCount = updatedSkills.getInt("rowcount");
        assertEquals(updatedSkillsCount, skillsCount + 1);
        Statement statement3 = con.createStatement();
        ResultSet skill = statement3.executeQuery("SELECT * FROM CATEGORY ORDER BY id DESC FETCH FIRST ROW ONLY");
        skill.next();
        Short categoryId = skill.getShort("ID");
        Statement statement4 = con.createStatement();
        ResultSet activities = statement4.executeQuery("select COUNT(*) AS rowcount from ACTIVITY where CATEGORY_ID = " + categoryId);
        activities.next();
        int activitiesCount = activities.getInt("rowcount");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("activitiesForm:newActivityBtn"))).click();
        Thread.sleep(500);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("newActivityForm:activityName"))).sendKeys("New Activity");
        driver.findElement(By.id("newActivityForm:confirmNewActivityBtn")).click();
        Thread.sleep(500);
        Statement statement5 = con.createStatement();
        ResultSet updatedActivities = statement5.executeQuery("select COUNT(*) AS rowcount from ACTIVITY where CATEGORY_ID = " + categoryId);
        updatedActivities.next();
        int updatedActivitiesCount = updatedActivities.getInt("rowcount");
        assertEquals(updatedActivitiesCount, activitiesCount + 1);
        WebElement skillsTableData = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("categoryForm:categoriesList_data")));
        Assert.assertNotNull(skillsTableData);
        List<WebElement> rows = skillsTableData.findElements(By.tagName("tr"));
        rows.get(rows.size() - 1).findElement(By.partialLinkText("X")).click();
        Thread.sleep(500);
        driver.switchTo().activeElement().sendKeys(Keys.ENTER);
        Thread.sleep(500);
        Statement statement6 = con.createStatement();
        ResultSet skillsAfterDeletion = statement6.executeQuery("select COUNT(*) AS rowcount from CATEGORY");
        skillsAfterDeletion.next();
        int skillsAfterDeletionCount = skillsAfterDeletion.getInt("rowcount");
        assertEquals(skillsAfterDeletionCount, updatedSkillsCount - 1);
        driver.findElement(By.id("headerForm:logout")).click();
        WebElement loginForm = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loginForm")));
        Assert.assertNotNull(loginForm);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }
}
