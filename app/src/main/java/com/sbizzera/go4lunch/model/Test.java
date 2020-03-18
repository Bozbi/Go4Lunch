package com.sbizzera.go4lunch.model;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Test {
    private HashMap<String,List<String>> frequentation = new HashMap<>();

    public Test(){}

    public Test(HashMap<String, List<String>> frequentation) {
        this.frequentation = frequentation;
    }

    public HashMap<String, List<String>> getFrequentation() {
        return frequentation;
    }

    public void setFrequentation(HashMap<String, List<String>> frequentation) {
        this.frequentation = frequentation;
    }
}
