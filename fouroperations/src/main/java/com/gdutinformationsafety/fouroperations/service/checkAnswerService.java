package com.gdutinformationsafety.fouroperations.service;


import com.gdutinformationsafety.fouroperations.pojo.fraction;
import com.gdutinformationsafety.fouroperations.properties.testFileProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class checkAnswerService {

    private testFileProperties testFileProperties;

    public checkAnswerService(@Autowired testFileProperties testFileProperties){
        this.testFileProperties=testFileProperties;
    }

    public File saveFile(MultipartFile file){
        try {
            File saveFile=new File(testFileProperties.getCompareDir()+UUID.randomUUID().toString().substring(0,1)+file.getOriginalFilename());
            saveFile.createNewFile();
            file.transferTo(saveFile);
            return saveFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    public Resource check(File testFile,File answerFile){
        FileReader fileReader= null;
        FileReader fileReader1=null;
        StringBuilder test=new StringBuilder();
        StringBuilder answer=new StringBuilder();
        try {
            fileReader = new FileReader(testFile);
            fileReader1=new FileReader(answerFile);
            BufferedReader testFileReader=new BufferedReader(fileReader);
            BufferedReader answerFileReader=new BufferedReader(fileReader1);
            String temp;
            while ((temp=testFileReader.readLine())!=null){
                test.append(temp);
            }
            while ((temp=answerFileReader.readLine())!=null){
                answer.append(temp);
            }
            testFileReader.close();
            answerFileReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        String[] subjects = test.toString().replace(" ", "").split("=");
        String[] answers = answer.toString().replace(" ", "").split("，");
        List<String> correctAnswerList=new ArrayList<>();
        long calculateNumber1;
        long calculateNumber2;
        char operator;
        for(int i=0;i<subjects.length;i++){
            String str=subjects[i].replace(i+1+"、","");
            if(str.indexOf("'")!=-1){
                correctAnswerList.add(i+1+"、"+calculateFraction(str));
            }else if(str.indexOf("/")!=-1){
                int j=str.indexOf("/");
                int left=0;
                int right=0;
                fraction fraction1;
                fraction fraction2;
                for(left=j-1;left>=0&&str.charAt(left)>=48&&str.charAt(left)<=57;left--);
                for(right=j+1;right<str.length()&&str.charAt(right)>=48&&str.charAt(right)<=57;right++);
                if(left<0&&right==str.length()){
                    correctAnswerList.add(i+1+"、"+new fraction(str).getBastString());
                    continue;
                }else if(left>0&&right==str.length()){
                    fraction1=new fraction(Long.valueOf(str.substring(0,left)),1);
                    fraction2=new fraction(str.substring(left+1));
                    operator=str.charAt(left);
                }else{
                    operator=str.charAt(right);
                    fraction1=new fraction(str.substring(0,right));
                    if(str.substring(right+1).indexOf("/")!=-1){
                        fraction2=new fraction(str.substring(right+1));
                    }else{
                        fraction2=new fraction(Long.valueOf(str.substring(right+1)),1);
                    }
                }
                switch (operator){
                    case '+':
                        correctAnswerList.add(i+1+"、"+fraction1.add(fraction2).getBastString());
                        break;
                    case '-':
                        correctAnswerList.add(i+1+"、"+fraction1.subtract(fraction2).getBastString());
                        break;
                    case '*':
                        correctAnswerList.add(i+1+"、"+fraction1.multiply(fraction2).getBastString());
                        break;
                    case '/':
                        correctAnswerList.add(i+1+"、"+fraction1.divide(fraction2).getBastString());
                        break;
                    default:
                        correctAnswerList.add(i+1+"、错误");
                        break;
                }
            }else {
                int j=0;
                for(;j<str.length()&&str.charAt(j)>=48&&str.charAt(j)<=57;j++);
                operator=str.charAt(j);
                calculateNumber1=Long.valueOf(str.substring(0,j));
                calculateNumber2=Long.valueOf(str.substring(j+1));
                switch (operator){
                    case '+':
                        correctAnswerList.add(i+1+"、"+(calculateNumber1+calculateNumber2));
                        break;
                    case '-':
                        correctAnswerList.add(i+1+"、"+(calculateNumber1-calculateNumber2));
                        break;
                    case '*':
                        correctAnswerList.add(i+1+"、"+(calculateNumber1*calculateNumber2));
                        break;
                    default:
                        correctAnswerList.add(i+1+"、错误");
                        break;
                }
            }
            
        }
        StringBuilder correctGrade=new StringBuilder("Correct:(");
        StringBuilder wrongGrade=new StringBuilder("Wrong:(");
        long correctNumber=0;
        long wrongNumber=0;
        for(int i=0;i<correctAnswerList.size();i++){
            if(correctAnswerList.get(i).equals(answers[i])){
                correctNumber++;
                correctGrade.append(i+1+"，");
            }else {
                wrongNumber++;
                wrongGrade.append(i+1+"，");
            }
        }
        if(correctGrade.length()>9){
            correctGrade.deleteCharAt(correctGrade.length()-1);
        }
        if(wrongGrade.length()>7){
            wrongGrade.deleteCharAt(wrongGrade.length()-1);
        }
        correctGrade.append(")"+'\n'+'\r');
        wrongGrade.append(")");
        correctGrade.insert(8,correctNumber);
        wrongGrade.insert(6,wrongNumber);
        String gradeFileName=testFileProperties.getGradeDir()+ UUID.randomUUID().toString().substring(0,10)+".txt";
        File gradeFile=new File(gradeFileName);
        try {
            gradeFile.createNewFile();
            FileWriter fileWriter=new FileWriter(gradeFile);
            fileWriter.write(correctGrade.toString());
            fileWriter.write(wrongGrade.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Path path= Paths.get(gradeFileName);
            UrlResource urlResource= new UrlResource(path.toUri());
            return urlResource;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean trueFraction(StringBuilder timesStr,StringBuilder numeratorStr,StringBuilder denominatorStr,StringBuilder operator,StringBuilder str){
        int j=0;
        j=str.indexOf("'");
        int left=0;
        int right=0;
        for(left=j-1;left>=0&&str.charAt(left)>=48&&str.charAt(left)<=57;left--){
            timesStr.insert(0,str.charAt(left));
        }
        for(right=j+1;str.charAt(right)>=48&&str.charAt(right)<=57;right++){
            numeratorStr.append(str.charAt(right));
        }
        for(right++;right<str.length()&&str.charAt(right)>=48&&str.charAt(right)<=57;right++){
            denominatorStr.append(str.charAt(right));
        }
        if(left>0){
            if(operator!=null)
                operator.append(str.charAt(left));
            str.delete(left,str.length()+1);
            return true;
        }
        if(operator!=null)
            operator.append(str.charAt(right));
        str.delete(0,right+1);
        return false;
    }

    private String calculateFraction(String subject) {
        StringBuilder str = new StringBuilder(subject);
        StringBuilder timesStr = new StringBuilder();
        StringBuilder numeratorStr = new StringBuilder();
        StringBuilder denominatorStr = new StringBuilder();
        fraction fraction1;
        fraction fraction2;
        StringBuilder operator = new StringBuilder();
        boolean flag = trueFraction(timesStr, numeratorStr, denominatorStr, operator, str);
        long times = Long.valueOf(timesStr.toString());
        long numerator = Long.valueOf(numeratorStr.toString());
        long denominator = Long.valueOf(denominatorStr.toString());
        if (flag) {
            fraction2 = new fraction(times * denominator + numerator, denominator);
            if (str.indexOf("/") != -1) {
                fraction1 = new fraction(str.toString());
            } else {
                fraction1 = new fraction(Long.valueOf(str.toString()), 1);
            }
        } else {
            fraction1 = new fraction(times * denominator + numerator, denominator);
            if (str.indexOf("'") != -1) {
                timesStr.setLength(0);
                numeratorStr.setLength(0);
                denominatorStr.setLength(0);
                trueFraction(timesStr, numeratorStr, denominatorStr, null, str);
                times = Long.valueOf(timesStr.toString());
                numerator = Long.valueOf(numeratorStr.toString());
                denominator = Long.valueOf(denominatorStr.toString());
                fraction2 = new fraction(times * denominator + numerator, denominator);
            } else {
                if (str.indexOf("/") != -1) {
                    fraction2 = new fraction(str.toString());
                } else {
                    fraction2 = new fraction(Long.valueOf(str.toString()), 1);
                }
            }
        }
        switch (operator.toString()){
            case "+":
                return fraction1.add(fraction2).getBastString();
            case "-":
                return fraction1.subtract(fraction2).getBastString();
            case "*":
                return fraction1.multiply(fraction2).getBastString();
            case "/":
                return fraction1.divide(fraction2).getBastString();
            default:
                return null;
        }
    }




}
