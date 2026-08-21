package com.maslov.booksmaslov.service;

import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class ScannerHelper {

    Scanner scanner = new Scanner(System.in);

    public int getIdFromUser() {
        return scanner.nextInt();
    }

    public String getFromUser() {
        return scanner.nextLine();
    }

    public String getEmptyString() {
        return scanner.nextLine();
    }
}
