package com.gdutinformationsafety.fouroperations.service;


import com.gdutinformationsafety.fouroperations.pojo.fraction;
import com.gdutinformationsafety.fouroperations.properties.testFileProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service
public class newTestService {

    private static Random random=new Random(System.currentTimeMillis());

    private testFileProperties testFileProperties;

    public newTestService(@Autowired testFileProperties testFileProperties){
        this.testFileProperties=testFileProperties;
    }

    public Resource loadTestToFile(String fileName) {
        try {
            Path path= Paths.get(fileName);
            UrlResource urlResource= new UrlResource(path.toUri());
            return urlResource;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String newTest(int number,int resultLimit) {
        List<String> subjects=new ArrayList<>();
        List<String> answers=new ArrayList<>();
        for (int i = 0; i < number; i++) {
            switch (random.nextInt(20)%5){
                case 0:
                    newAdd(subjects,answers,resultLimit,i+1);
                    break;
                case 1:
                    newSubtraction(subjects,answers,resultLimit,i+1);
                    break;
                case 2:
                    newMultiplication(subjects,answers,resultLimit,i+1);
                    break;
                case 3:
                    newDivision(subjects,answers,resultLimit,i+1);
                    break;
                case 4:
                    newFractional(subjects,answers,resultLimit,i+1);
                    break;
                default:
                    break;
            }
        }
        String testFileName=testFileProperties.getTestDir()+UUID.randomUUID().toString().substring(0,10)+".txt";
        String answerFileName=testFileProperties.getTestDir()+UUID.randomUUID().toString().substring(0,10)+".txt";
        File testFile=new File(testFileName);
        File answerFile=new File(answerFileName);
        try{
            if(!testFile.exists()&&!answerFile.exists()){
                testFile.createNewFile();
                answerFile.createNewFile();
            }
            FileWriter testFileWriter=new FileWriter(testFile);
            FileWriter answerFileWriter=new FileWriter(answerFile);
            for (String subject : subjects) {
                testFileWriter.append(subject);
                testFileWriter.flush();
            }
            for (String answer : answers) {
                answerFileWriter.append(answer);
                answerFileWriter.flush();
            }
            testFileWriter.close();
            answerFileWriter.close();
            String zipName = compress(testFile, answerFile);
            return zipName;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private void newAdd(List<String> subjects,List<String> answers,int resultLimit,int i){
        StringBuilder subject=new StringBuilder();
        StringBuilder answer=new StringBuilder();
        do{
            subject.setLength(0);
            answer.setLength(0);
            long addend1=random.nextInt(resultLimit);
            long addend2=random.nextInt(resultLimit);
            if(addend1<addend2){
                answer.append(-1);
            }else {
                subject.append(addend1+" "+"+"+" "+addend2+" "+"=");
                answer.append(addend1+addend2);
            }
        }while (answer.toString().equals("-1"));
        subjects.add(i+"、"+subject.toString()+'\n'+'\r');
        answers.add(i+"、"+answer.toString()+"，"+'\n'+'\r');
    }

    private void newSubtraction(List<String> subjects,List<String> answers,int resultLimit,int i){
        StringBuilder subject=new StringBuilder();
        StringBuilder answer=new StringBuilder();
        do{
            subject.setLength(0);
            answer.setLength(0);
            long Subtraction=random.nextInt(resultLimit);
            long minuend=random.nextInt(resultLimit);
            if(Subtraction<minuend){
                answer.append(-1);
            }else {
                subject.append(Subtraction+" "+"-"+" "+minuend+" "+"=");
                answer.append(Subtraction-minuend);
            }
        }while (answer.toString().equals("-1"));
        subjects.add(i+"、"+subject.toString()+'\n'+'\r');
        answers.add(i+"、"+answer.toString()+"，"+'\n'+'\r');
    }


    private void newMultiplication(List<String> subjects,List<String> answers,int resultLimit,int i){
        StringBuilder subject=new StringBuilder();
        StringBuilder answer=new StringBuilder();
        do{
            subject.setLength(0);
            answer.setLength(0);
            long multiplier1=random.nextInt(resultLimit);
            long multiplier2=random.nextInt(resultLimit);
            if(multiplier1<multiplier2){
                answer.append(-1);
            }else {
                subject.append(multiplier1+" "+"*"+" "+multiplier2+" "+"=");
                answer.append(multiplier1*multiplier2);
            }
        }while (answer.toString().equals("-1"));
        subjects.add(i+"、"+subject.toString()+'\n'+'\r');
        answers.add(i+"、"+answer.toString()+"，"+'\n'+'\r');
    }

    private void newDivision(List<String> subjects,List<String> answers,int resultLimit,int i){
        StringBuilder subject=new StringBuilder();
        StringBuilder answer=new StringBuilder();
        long dividend=random.nextInt(resultLimit);
        long divisor=random.nextInt(resultLimit-1)+1;
        subject.append(dividend+" "+"÷"+" "+divisor+" "+"=");
        fraction fraction=new fraction(dividend,divisor);
        answer.append(fraction.getBastString());
        subjects.add(i+"、"+subject.toString()+'\n'+'\r');
        answers.add(i+"、"+answer.toString()+"，"+'\n'+'\r');
    }

    private void newFractional(List<String> subjects,List<String> answers,int resultLimit,int i){
        StringBuilder subject=new StringBuilder();
        StringBuilder answer=new StringBuilder();
        do{
            subject.setLength(0);
            answer.setLength(0);
            fraction fraction1=new fraction(random.nextInt(resultLimit),random.nextInt(resultLimit-1)+1);
            fraction fraction2=new fraction(random.nextInt(resultLimit-1)+1,random.nextInt(resultLimit-1)+1);
            if(fraction1.compare(fraction2)==-1){
                answer.append(-1);
            }else{
                switch (random.nextInt(10)%4){
                    case 0:
                        subject.append(fraction1.getBastString()+" "+"+"+" "+fraction2.getBastString()+" "+"=");
                        fraction add = fraction1.add(fraction2);
                        answer.append(add.getBastString());
                        break;
                    case 1:
                        subject.append(fraction1.getBastString()+" "+"-"+" "+fraction2.getBastString()+" "+"=");
                        fraction subtract = fraction1.subtract(fraction2);
                        answer.append(subtract.getBastString());
                        break;
                    case 2:
                        subject.append(fraction1.getBastString()+" "+"*"+" "+fraction2.getBastString()+" "+"=");
                        fraction multiply = fraction1.multiply(fraction2);
                        answer.append(multiply.getBastString());
                        break;
                    case 3:
                        subject.append(fraction1.getBastString()+" "+"÷"+" "+fraction2.getBastString()+" "+"=");
                        fraction divide = fraction1.divide(fraction2);
                        answer.append(divide.getBastString());
                        break;
                    default:
                        break;
                }
            }
        }while (answer.toString().equals("-1"));
        subjects.add(i+"、"+subject.toString()+'\n'+'\r');
        answers.add(i+"、"+answer.toString()+"，"+'\n'+'\r');
    }

    private String compress(File testFile,File answerFile) {
        byte[] buf=new byte[1024];
        File[] files={testFile,answerFile};
        String zipName=testFileProperties.getZipDir()+UUID.randomUUID().toString().substring(0,10)+".zip";
        File zipFile=new File(zipName);
        try{
            if(!zipFile.exists()){
                zipFile.createNewFile();
            }
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
            for (File file : files) {
                FileInputStream fis = new FileInputStream(file);
                zos.putNextEntry(new ZipEntry(file.getName()));
                int len;
                while((len = fis.read(buf)) > 0){
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
            return zipName;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }



}
