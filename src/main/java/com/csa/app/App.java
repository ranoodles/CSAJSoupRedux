package com.csa.app;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class App {
    public static ArrayList<String> good = new ArrayList<String>();
    public static ArrayList<String> amazing = new ArrayList<String>();
    public static ArrayList<String> okay = new ArrayList<String>();
    public static ArrayList<String> bad = new ArrayList<String>();
    public static ArrayList<String> terrible = new ArrayList<String>();

    public static void scrape() {
        System.setProperty("chromedriver", "/Users/ramanarora/Downloads/chromedriver-mac-arm64/");
        WebDriver driver = new ChromeDriver();
        String part1 = "https://www.amazon.com/SAMSUNG-Unlocked-Smartphone-Advanced-Expandable/product-reviews/B0CN1Q2X3B/ref=cm_cr_getr_d_paging_btm_prev_";
        String part2 = "?ie=UTF8&reviewerType=all_reviews&pageNumber=";
        ArrayList<String> profiles = new ArrayList<String>();
        ArrayList<String> reviews = new ArrayList<String>();
        try {
            for (int num=1; num<7; num++){
                driver.get(part1 + num + part2 + num);
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
                List<WebElement> profileElements = driver.findElements(By.className("a-profile-name"));
                List<WebElement> reviewElements = driver.findElements(By.className("review-text-content"));
                for (WebElement profile : profileElements) {
                    if (num != 1 && profile.getText() != "" && !profile.getText().contains("Emily Melvin") && !profile.getText().contains("Duane")) {
                        profiles.add(profile.getText());
                    }
                }
                for (WebElement review : reviewElements) {
                    if (num != 1 && review.getText() != "") {
                        reviews.add(review.getText());
                    }
                }
            }
            writeOnFile(profiles, reviews);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
    public static void writeOnFile(ArrayList<String> profiles, ArrayList<String> reviews) throws IOException {
        FileWriter myWriter = new FileWriter("socialmediaposts.txt");
        for (int i=0; i < profiles.size(); i++) {
            String text = reviews.get(i).replaceAll("\n", "");
            myWriter.write(profiles.get(i) + ", " + text + "\n");
        }
        myWriter.close();
    }
    public static void targetRatings() throws IOException {
        File myObj = new File("socialmediaposts.txt");
        Scanner myReader = new Scanner(myObj);  
        while (myReader.hasNextLine()) {
            // System.out.println(myReader.nextLine());
            FileWriter myWriter = new FileWriter("temp.txt");
            String data = myReader.nextLine();

            String revStr = data.substring(data.indexOf(',')+2);
            String profStr = data.substring(0, data.indexOf(','));

            myWriter.write(revStr);
            myWriter.close();
            int rating = Review.starRating("temp.txt");
            System.out.println(rating);
            switch (rating) {
                case 0:
                    terrible.add(profStr);
                    break;
                case 1:
                    bad.add(profStr);
                    break;
                case 2:
                    okay.add(profStr);
                    break;
                case 3:
                    good.add(profStr);
                    break;
                case 4:
                    amazing.add(profStr);
                    break;
                default:
                    break;
            }
        }
        myReader.close();

        FileWriter goodWriter = new FileWriter("targetMarketGood.txt");
        FileWriter badWriter = new FileWriter("targetMarketBad.txt");
        for (String rev : amazing) {goodWriter.write(rev+"\n");}
        for (String rev : good) {goodWriter.write(rev+"\n");}
        for (String rev : bad) {badWriter.write(rev+"\n");}
        for (String rev : terrible) {badWriter.write(rev+"\n");}
        goodWriter.close();
        badWriter.close();
    }
    public static void makeAndDisplayAds() throws IOException {
        File badUsers = new File("targetMarketBad.txt");
        File goodUsers = new File("targetMarketGood.txt");
        Scanner badReader = new Scanner(badUsers);
        Scanner goodReader = new Scanner(goodUsers);

        String badBody = "";
        File badFile = new File("badAd.txt");
        Scanner badReader2 = new Scanner(badFile);
        while (badReader2.hasNextLine()) {badBody += badReader2.nextLine() + "\n";}

        String goodBody = "";
        File goodFile = new File("goodAd.txt");
        Scanner goodReader2 = new Scanner(goodFile);
        while (goodReader2.hasNextLine()) {goodBody += goodReader2.nextLine() + "\n";}

        while (badReader.hasNextLine()) {
            String user = badReader.nextLine();
            FileWriter badWriter = new FileWriter("badAdsSend/"+user+"Ad.txt");
            String heading = "Dear " + user + "," + "\n";
            badWriter.write(heading + badBody);
            badWriter.close();
        }
        
        while (goodReader.hasNextLine()) {
            String user = goodReader.nextLine();
            FileWriter goodWriter = new FileWriter("goodAdsSend/"+user+"Ad.txt");
            String heading = "Dear " + user + "," + "\n";
            goodWriter.write(heading + goodBody);
            goodWriter.close();
        }
    }
    public static void main(String[] args) throws IOException {
        // scrape();
        // targetRatings();
        makeAndDisplayAds();
    }
}
