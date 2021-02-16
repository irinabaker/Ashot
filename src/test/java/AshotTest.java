import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class AshotTest {
    WebDriver driver;
    public String resourcesImagesDir;
    public String expectedDir;
    public String actualDir;
    public String diffDir;
    public String resultGifsDir;
    public String markedImages;
    public String name;

    @BeforeMethod
    public void openDriver() {
        driver = new ChromeDriver();
        driver.manage().window().setSize(new Dimension(1920, 1000));
    }


    public void setRootScreenshotsDir(){
        resourcesImagesDir= "./testScreenshots";
        expectedDir = resourcesImagesDir+"/expected/";
        actualDir = resourcesImagesDir+"/actual/";
       // diffDir = resourcesImagesDir+"/diff/";
        markedImages = resourcesImagesDir + "/markedImages/";
        resultGifsDir = resourcesImagesDir+"/gifs/";
    }

    public void getActualScreenshot() throws IOException {
        // get a screenshot of the page
        Screenshot screenshot = new AShot().takeScreenshot(driver);
        name = "scr" + driver.getTitle() + driver.manage().window().getSize();
        File actualFile = new File(actualDir + name + ".png");
        ImageIO.write(screenshot.getImage(), "png", actualFile);
    }

    public boolean compareImages() throws IOException, AWTException {
        // read the image to compare
        File actualFile = new File(expectedDir + name + ".png");
        BufferedImage expectedImage = ImageIO.read(actualFile);

        File expectedFile = new File(actualDir + name + ".png");
        BufferedImage actualImage = ImageIO.read(expectedFile);

        // Create ImageDiffer object and call method makeDiff()

        ImageDiff diff = new ImageDiffer().makeDiff(expectedImage, actualImage);

        if (diff.getDiffSize() == 0) {
            File diffFile = new File(markedImages + name + ".png");
            ImageIO.write(diff.getMarkedImage(), "png", diffFile);
            System.out.println("Images are same");
            return true;

        } else {
            System.out.println("Images are different");
            File diffFile = new File(markedImages + name + ".png");
            ImageIO.write(diff.getMarkedImage(), "png", diffFile);
            return false;
        }
    }

    @Test
    public void browserTest() throws IOException, InterruptedException, AWTException {
        Robot bot = new Robot();
        bot.mouseMove(0, 0);
        setRootScreenshotsDir();

        Thread.sleep(5000);
        driver.get("https://vp.ru");
        getActualScreenshot();

        Assert.assertTrue(compareImages());
    }

    @AfterMethod
    public void closeDriver(){
        driver.quit();
    }
}
