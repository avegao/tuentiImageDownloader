package com.avegao.tuenti.imagedownloader;

import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class Main extends JFrame {

    private JLabel emailLabel;
    private JTextField emailInput;

    private JLabel passwordLabel;
    private JPasswordField passwordInput;

    private JLabel downloadDirLabel;
    private JTextField downloadDirInput;
    private JButton downloadDirButton;
    private JFileChooser downloadDirChooser;

    private  JButton startButton;

    public Main() {

        this.setLayout(null);

        emailLabel = new JLabel("Email");
        emailLabel.setBounds(50, 12, 150, 25);
        this.add(emailLabel);

        emailInput = new JTextField();
        emailInput.setBounds(120, 12, 250, 25);
        this.add(emailInput);

        passwordLabel = new JLabel("Contraseña");
        passwordLabel.setBounds(50, 50, 150, 25);
        this.add(passwordLabel);

        passwordInput = new JPasswordField();
        passwordInput.setBounds(120, 50, 250, 25);
        this.add(passwordInput);

        downloadDirLabel = new JLabel("Carpeta de descarga");
        downloadDirLabel.setBounds(50, 88, 150, 25);
        this.add(downloadDirLabel);

        downloadDirInput = new JTextField();
        downloadDirInput.setBounds(120, 88, 250, 25);
        this.add(downloadDirInput);

        downloadDirButton = new JButton("Descargar");
        downloadDirButton.setBounds(400, 88, 250, 25);
        this.add(downloadDirButton);

        downloadDirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String email = emailInput.getText();
                String password = String.copyValueOf(passwordInput.getPassword());

                //TODO: Empezar Selenium
                FirefoxDriver driver = new FirefoxDriver();
                driver.get("http://www.tuenti.com");

                try {
                    Thread.sleep(5000);
                } catch (Exception e1) {}

                driver.findElement(By.id("email")).sendKeys(email);
                driver.findElement(By.id("input_password")).sendKeys(password);
                driver.findElement(By.id("submit_button")).click();

                String userId = driver.findElement(By.xpath(
                        "//div[contains(concat(' ', @class, ' '), ' js-avatar h-pr')]")).getAttribute("data-entity-id");

                driver.navigate().to("http://www.tuenti.com/#m=Albums&func=index&collection_key=1-" + userId);
                driver.navigate().refresh();

                int numImages = Integer.parseInt(driver.findElement(By.xpath(
                        "//a[@id=\"albumSelectorTrigger\"]/span")).getText().replace("(", "").replace(")", "").replace(".", ""));

                driver.findElement(By.xpath("//ul[@id='albumBody']/li/a")).click();

                try {
                    Thread.sleep(2000);
                } catch (Exception e1) {}

                ArrayList<String> urlImages = new ArrayList<String>();
                urlImages.add(driver.findElement(By.id("photo_image")).getAttribute("src"));

                System.out.println(numImages);

                for (int i = 0; i < numImages; i++) {
                    try {
                        Thread.sleep(2000);
                    } catch (Exception e1) {}

                    urlImages.add(driver.findElement(By.id("photo_image")).getAttribute("src"));

                    System.out.println(i);

                    try {
                        downloadImage(driver.findElement(By.id("photo_image")).getAttribute("src"), i);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    driver.findElement(By.id("photo_nav_next")).click();
                }

                System.out.println(urlImages.size());

            }
        });

    }

    public static void main(String[] args) {

        JFrame window = new Main();
        window.setBounds(80, 80, 500, 250);
        window.setVisible(true);
        window.addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                    }
                }
        );

    }

    private void downloadImage(String url, int number) throws IOException {

        BufferedImage image = null;
        String downloadDir = null;

        if (null != downloadDirInput.getText()) {
            downloadDir = downloadDirInput.getText();
        } else {
            downloadDir = "./";
        }

        try {
            URL urlImage = new URL(url);
            image = ImageIO.read(urlImage);

            if (image != null) {
                File file = new File(downloadDir + "/" + number + ".jpg");
                ImageIO.write(image, "JPG", file);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
}
